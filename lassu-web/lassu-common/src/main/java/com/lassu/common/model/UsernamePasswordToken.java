/**
 * 
 */
package com.lassu.common.model;

import java.io.Serializable;

/**
 * @author abhinab
 *
 */
public class UsernamePasswordToken implements Serializable {

	private static final long serialVersionUID = 7751678550823500289L;

	private String username;
	private String password;
	private String initiatorSite;

	public UsernamePasswordToken() {
	}

	public UsernamePasswordToken(String username, String password, String initiatorSite) {
		this.username = username;
		this.password = password;
		this.initiatorSite = initiatorSite;
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

	public String getInitiatorSite() {
		return initiatorSite;
	}

	public void setInitiatorSite(String initiatorSite) {
		this.initiatorSite = initiatorSite;
	}

}
