/**
 * 
 */
package com.lassu.portal.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.lassu.common.util.CommonConstant;


/**
 * @author abhinab
 *
 */
@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private AuthSuccessHandler authSuccessHandler;

	@Autowired
	private AuthFailureHandler authFailureHandler;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http
			.authorizeRequests()
			.antMatchers(CommonConstant.URL_CSS, CommonConstant.URL_JS, CommonConstant.URL_IMG, CommonConstant.URL_LOGIN).permitAll()
			.anyRequest().fullyAuthenticated()
		.and()
			.formLogin()
			.defaultSuccessUrl(CommonConstant.URL_DEFAULT_SUCCESS, true)
			.successHandler(authSuccessHandler)
			.failureHandler(authFailureHandler)
			.loginPage(CommonConstant.URL_LOGIN).permitAll()
		.and()
			.logout()
			.addLogoutHandler(new LogoutHandler() {
				@Override
				public void logout(HttpServletRequest request, HttpServletResponse response,Authentication authentication) {
					request.getSession().invalidate();
				}
			})
			.permitAll()
			.logoutUrl(CommonConstant.URL_LOGOUT)
			.logoutSuccessUrl(CommonConstant.URL_LOGIN)
			.invalidateHttpSession(true)
			.logoutRequestMatcher(new AntPathRequestMatcher(CommonConstant.URL_LOGOUT))
			.deleteCookies(CommonConstant.COOKIE_JSESSIONID)
		.and()
			.sessionManagement().maximumSessions(CommonConstant.MAX_SESSION);

	}

}
