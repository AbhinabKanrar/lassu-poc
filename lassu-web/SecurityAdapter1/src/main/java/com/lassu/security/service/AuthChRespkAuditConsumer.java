package com.lassu.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lassu.security.common.model.RespChkAuthType;
import com.lassu.security.common.model.User;
import com.lassu.security.common.util.CommonUtil;
import com.lassu.security.service.user.UserService;

import io.jsonwebtoken.Claims;
import reactor.bus.Event;
import reactor.fn.Consumer;

@Component
public class AuthChRespkAuditConsumer implements Consumer<Event<RespChkAuthType>> {

	@Autowired
	private UserService userService;
	
	@Override
	public void accept(Event<RespChkAuthType> event) {
		RespChkAuthType respChkAuthType = event.getData();
		if (respChkAuthType != null && respChkAuthType.getResp() != null && respChkAuthType.getResp().getData() != null) {
			try {
				String username = getUsernameFromCred(respChkAuthType.getResp().getData());
				User user = userService.findUserByUsername(username);
				if (user != null) {
					userService.update(user, respChkAuthType.getHead().getOrg());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private String getUsernameFromCred(String credData) throws Exception {
		Claims claims = CommonUtil.getClaims(credData);
		String decryptedMessage = CommonUtil.decrypt(claims.getSubject());
		return decryptedMessage;
	}
	
}
