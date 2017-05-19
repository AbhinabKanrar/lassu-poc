/**
 * 
 */
package com.lassu.portal.security;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import com.lassu.common.model.CredType;
import com.lassu.common.model.HeadType;
import com.lassu.common.model.ReqChkAuthType;
import com.lassu.common.model.User;
import com.lassu.common.model.UserStatus;
import com.lassu.common.util.CommonConstant;
import com.lassu.common.util.CommonUtil;

import reactor.bus.Event;
import reactor.bus.EventBus;

/**
 * @author abhinab
 *
 */
@Component
public class AuthSuccessHandler implements AuthenticationSuccessHandler {

	@Autowired
	private EventBus eventBus;

	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException {
		handle(request, response, authentication);
	}

	protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException {
		User user =  (User) WebUtils.getSessionAttribute(request, "user");
		user.setPassword(request.getParameter("password"));
		if (user.getUserStatus() != null) {
			if (user.getUserStatus() == UserStatus.ACTIVE) {
				try {
					ReqChkAuthType reqChkAuthType = generateReqChkAuthType(user);
					eventBus.notify(CommonConstant.COMPONENT_AUTHDISPATCHER, Event.wrap(reqChkAuthType));
				} catch (DataLengthException | IllegalStateException | InvalidCipherTextException e) {
					e.printStackTrace();
				}
				user.setPassword(null);
				WebUtils.setSessionAttribute(request, "user", user);
				redirectStrategy.sendRedirect(request, response, CommonConstant.URL_DEFAULT_SUCCESS);
			} else if(user.getUserStatus() == UserStatus.INSECURE) {
				redirectStrategy.sendRedirect(request, response, CommonConstant.URL_DEFAULT_SUCCESS);
			} else {
				request.getSession().invalidate();
				if (user.getUserStatus() == UserStatus.DISABLED) {
					redirectStrategy.sendRedirect(request, response, CommonConstant.URL_LOGIN_ERROR_DISABLED);
				} else if (user.getUserStatus() == UserStatus.DEACTIVE) {
					redirectStrategy.sendRedirect(request, response, CommonConstant.URL_LOGIN_ERROR_DEACTIVE);
				}
			}
		} else {
			request.getSession().invalidate();
			redirectStrategy.sendRedirect(request, response, CommonConstant.URL_LOGIN_ERROR_INVALID);
		}
	}

	private ReqChkAuthType generateReqChkAuthType(User user) throws DataLengthException, IllegalStateException, InvalidCipherTextException {
		
		String msgId = String.valueOf(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE);
		String encryptedMessage = CommonUtil.encrypt(user.getUsername() + CommonConstant.HASH_SEPARATOR + user.getPassword());
		HeadType headType = new HeadType();
		headType.setOrg(CommonUtil.getHostname());
		headType.setVer("1.0");
		headType.setTs(CommonUtil.currentTimeStamp());
		headType.setMsgId(msgId);
		
		CredType credType = new CredType();
		credType.setNote("");
		credType.setData(CommonUtil.setClaims(msgId, CommonUtil.currentTimeStamp(), encryptedMessage, 180000));
		
		ReqChkAuthType reqChkAuthType = new ReqChkAuthType();
		reqChkAuthType.setHead(headType);
		reqChkAuthType.setCred(credType);
		
		return reqChkAuthType;
	}
	
}
