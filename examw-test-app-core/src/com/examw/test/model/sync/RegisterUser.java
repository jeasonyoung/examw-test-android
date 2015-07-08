package com.examw.test.model.sync;

import com.google.gson.Gson;

import android.content.Context;


/**
 * 注册用户信息。
 * 
 * @author yangyong
 * @since 2015年2月4日
 */
public class RegisterUser extends AppClient {
	private static final long serialVersionUID = 1L;
	private String account,password,username,email,phone,channel;
	/**
	 * 构造函数。
	 * @param context
	 */
	public RegisterUser(Context context) {
		super(context);
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
	/*
	 * JSON序列化。
	 * @see com.examw.test.model.sync.AppClient#toString()
	 */
	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}