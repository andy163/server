package com.pangbohao.server.rest.model;

/**
 * 注册结果bean类
 * 
 * @author Administrator
 *
 */
public class RegistResult extends PostResult {

	private static final long serialVersionUID = 1L;

	private String id;
	private String username;
	private String userkey;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserkey() {
		return userkey;
	}

	public void setUserkey(String userkey) {
		this.userkey = userkey;
	}
}
