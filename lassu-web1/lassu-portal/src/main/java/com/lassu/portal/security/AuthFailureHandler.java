/**
 * 
 */
package com.lassu.portal.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.lassu.common.util.CommonConstant;

/**
 * @author abhinab
 *
 */
@Component
public class AuthFailureHandler implements AuthenticationFailureHandler {

	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authenticationException) throws IOException {
		handle(request, response, authenticationException);
	}

	protected void handle(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authenticationException) throws IOException {
		redirectStrategy.sendRedirect(request, response, CommonConstant.URL_LOGIN_ERROR_INVALID);
	}

}
