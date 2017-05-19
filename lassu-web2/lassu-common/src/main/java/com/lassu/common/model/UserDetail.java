/**
 * 
 */
package com.lassu.common.model;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

/**
 * @author abhinab
 *
 */
public class UserDetail extends User {

	private static final long serialVersionUID = -4259668335909442858L;
	
	private String username;
	private String password;
	private String role;

	public UserDetail(String username, String password, String role) {
		super(username, password, true, true, true, true, AuthorityUtils.createAuthorityList(role));
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	
}
