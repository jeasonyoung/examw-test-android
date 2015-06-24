package com.examw.test.model;

import java.io.Serializable;


/**
 * 登录用户信息。
 * 
 * @author yangyong
 * @since 2015年2月4日
 */
public class LoginUser implements Serializable{
	private static final long serialVersionUID = 1L;
	private String account,password;
	private String clientId;
	/**
	 * 获取客户端ID。
	 * @return 客户端ID。
	 */
	public String getClientId() {
		return clientId;
	}
	/**
	 * 设置客户端ID。
	 * @param clientId 
	 *	  客户端ID。
	 */
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	/**
	 * 获取用户名。
	 * @return 用户名。
	 */
	public String getAccount() {
		return account;
	}
	/**
	 * 设置用户名。
	 * @param account 
	 *	  用户名。
	 */
	public void setAccount(String account) {
		this.account = account;
	}
	/**
	 * 获取用户密码。
	 * @return 用户密码。
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * 设置用户密码。
	 * @param password 
	 *	  用户密码。
	 */
	public void setPassword(String password) {
		this.password = password;
	}
}