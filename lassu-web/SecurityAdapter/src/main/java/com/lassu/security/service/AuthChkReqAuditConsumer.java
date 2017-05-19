package com.lassu.security.service;

import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.lassu.security.common.model.CredType;
import com.lassu.security.common.model.ReqChkAuthType;
import com.lassu.security.common.model.User;
import com.lassu.security.common.util.CommonConstant;
import com.lassu.security.common.util.CommonUtil;
import com.lassu.security.service.user.UserService;

import io.jsonwebtoken.Claims;
import reactor.bus.Event;
import reactor.fn.Consumer;

@Component
public class AuthChkReqAuditConsumer implements Consumer<Event<ReqChkAuthType>> {
	
	private static PasswordEncoder encoder = new BCryptPasswordEncoder(11);
	
	@Autowired
	private UserService userService;

	@Override
	public void accept(Event<ReqChkAuthType> event) {
		ReqChkAuthType reqChkAuthType = event.getData();
		if (reqChkAuthType != null) {
			CredType credType = reqChkAuthType.getCred();
			if (credType != null) {
				String credData = credType.getData();
				try {
					List<String> creds = getCred(credData);
					User user = userService.findUserByUsername(creds.get(0));
					if (user != null && encoder.matches(creds.get(1), user.getPassword())) {
						userService.update(user, reqChkAuthType.getHead().getOrg());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private List<String> getCred(String credData) throws Exception {
		Claims claims = CommonUtil.getClaims(credData);
		String decryptedMessage = CommonUtil.decrypt(claims.getSubject());
		List<String> creds = Collections
				.list(new StringTokenizer(decryptedMessage, CommonConstant.HASH_SEPARATOR)).stream()
				.map(token -> (String) token).collect(Collectors.toList());
		return creds;
	}

}
