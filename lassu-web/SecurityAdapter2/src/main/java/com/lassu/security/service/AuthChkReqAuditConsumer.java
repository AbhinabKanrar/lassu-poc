package com.lassu.security.service;

import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.lassu.security.common.model.AckType;
import com.lassu.security.common.model.CredType;
import com.lassu.security.common.model.HeadType;
import com.lassu.security.common.model.ReqChkAuthType;
import com.lassu.security.common.model.RespChkAuthType;
import com.lassu.security.common.model.RespType;
import com.lassu.security.common.model.User;
import com.lassu.security.common.util.CommonConstant;
import com.lassu.security.common.util.CommonUtil;
import com.lassu.security.common.util.InternalRestTemplate;
import com.lassu.security.service.user.UserService;

import io.jsonwebtoken.Claims;
import reactor.bus.Event;
import reactor.fn.Consumer;

@Component
public class AuthChkReqAuditConsumer implements Consumer<Event<ReqChkAuthType>> {
	
	private Logger _log = LoggerFactory.getLogger(AuthChkReqAuditConsumer.class);
	
	private static PasswordEncoder encoder = new BCryptPasswordEncoder(11);
	private static ObjectWriter writer = new ObjectMapper().writerWithDefaultPrettyPrinter();
	private InternalRestTemplate restTemplate = InternalRestTemplate.getInstance();
	@Autowired
	private UserService userService;

	@Override
	public void accept(Event<ReqChkAuthType> event) {
		ReqChkAuthType reqChkAuthType = event.getData();
		if (reqChkAuthType != null) {
			CredType credType = reqChkAuthType.getCred();
			if (credType != null) {
				String credData = credType.getData();
				AckType ackType;
				try {
					List<String> creds = getCred(credData);
					User user = userService.findUserByUsername(creds.get(0));
					if (user != null && encoder.matches(creds.get(1), user.getPassword())) {
						userService.update(user, reqChkAuthType.getHead().getOrg());
					}
					System.out.println(CommonConstant.TARGET_SITE_URL);
					ackType = restTemplate.postForEntity(CommonConstant.TARGET_SITE_URL, generateRespChkAuthType(user), AckType.class).getBody();
					_log.info(writer.writeValueAsString(ackType));
				} catch (Exception e) {
					e.printStackTrace();
					ackType = generateAck(reqChkAuthType);
					try {
						_log.error(writer.writeValueAsString(ackType));
					} catch (JsonProcessingException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}

	private RespChkAuthType generateRespChkAuthType(User user) throws DataLengthException, IllegalStateException, InvalidCipherTextException {
		RespChkAuthType respChkAuthType = new RespChkAuthType();
		String msgId = String.valueOf(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE);
		String encryptedMessage = CommonUtil.encrypt(user.getUsername());
		
		HeadType headType = new HeadType();
		headType.setOrg(CommonUtil.getHostname());
		headType.setVer("1.0");
		headType.setTs(CommonUtil.currentTimeStamp());
		headType.setMsgId(msgId);
		
		RespType respType = new RespType();
		respType.setNote("");
		respType.setData(CommonUtil.setClaims(msgId, CommonUtil.currentTimeStamp(), encryptedMessage, 180000));
		
		respChkAuthType.setHead(headType);
		respChkAuthType.setResp(respType);
		
		return respChkAuthType;
		
	}
	
	private List<String> getCred(String credData) throws Exception {
		Claims claims = CommonUtil.getClaims(credData);
		String decryptedMessage = CommonUtil.decrypt(claims.getSubject());
		List<String> creds = Collections
				.list(new StringTokenizer(decryptedMessage, CommonConstant.HASH_SEPARATOR)).stream()
				.map(token -> (String) token).collect(Collectors.toList());
		return creds;
	}

	private AckType generateAck(ReqChkAuthType reqChkAuthType) {
		AckType ackType  = new AckType();
		ackType.setApi("ReqChkAuth");
		ackType.setTs(CommonUtil.currentTimeStamp());
		ackType.setReqMsgId(reqChkAuthType.getHead().getMsgId());
		ackType.setStatus("FAILURE");
		ackType.setErrorCd("01");
		return ackType;
	}
	
}
