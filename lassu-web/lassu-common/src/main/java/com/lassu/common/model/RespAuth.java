/**
 * 
 */
package com.lassu.common.model;

import java.io.Serializable;

/**
 * @author abhinab
 *
 */
public class RespAuth implements Serializable {

	private static final long serialVersionUID = 5352595312122562208L;

	private String source;
	private boolean isSecure;

	public RespAuth() {}

	public RespAuth(String source, boolean isSecure) {
		this.source = source;
		this.isSecure = isSecure;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public boolean isSecure() {
		return isSecure;
	}

	public void setSecure(boolean isSecure) {
		this.isSecure = isSecure;
	}

}
