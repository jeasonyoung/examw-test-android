package com.examw.test.model;

import java.io.Serializable;

/**
 * 注册用户信息。
 * 
 * @author yangyong
 * @since 2015年2月4日
 */
public class RegisterUser implements Serializable{
	private static final long serialVersionUID = 1L;
	private String account,password,username,email,phone,channel;
	private String clientId,clientName,clientVersion;
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
	 * 获取客户端名称。
	 * @return 客户端名称。
	 */
	public String getClientName() {
		return clientName;
	}
	/**
	 * 设置客户端名称。
	 * @param clientName 
	 *	  客户端名称。
	 */
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	/**
	 * 获取客户端版本。
	 * @return 客户端版本。
	 */
	public String getClientVersion() {
		return clientVersion;
	}
	/**
	 * 设置客户端版本。
	 * @param clientVersion 
	 *	  客户端版本。
	 */
	public void setClientVersion(String clientVersion) {
		this.clientVersion = clientVersion;
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
	/**
	 * 获取真实姓名
	 * @return 真实姓名
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * 设置真实姓名
	 * @param username 
	 *	  真实姓名
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * 获取用户邮箱。
	 * @return 用户邮箱。
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * 设置用户邮箱。
	 * @param email 
	 *	  用户邮箱。
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * 获取用户手机号码。
	 * @return 用户手机号码。
	 */
	public String getPhone() {
		return phone;
	}
	/**
	 * 设置用户手机号码。
	 * @param phone 
	 *	  用户手机号码。
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}
	/**
	 * 获取注册频道。
	 * @return 注册频道。
	 */
	public String getChannel() {
		return channel;
	}
	/**
	 * 设置注册频道。
	 * @param channel 
	 *	  注册频道。
	 */
	public void setChannel(String channel) {
		this.channel = channel;
	}
}