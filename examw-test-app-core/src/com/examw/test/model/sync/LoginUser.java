package com.examw.test.model.sync;

import com.google.gson.Gson;

import android.content.Context;
import android.util.Log;


/**
 * 登录用户信息。
 * 
 * @author yangyong
 * @since 2015年2月4日
 */
public class LoginUser extends AppClient {
	private static final long serialVersionUID = 1L;
	private static final String TAG = "LoginUser";
	private String account,password;
	/**
	 * 构造函数。
	 * @param context
	 * 上下文。
	 */
	public LoginUser(Context context) {
		super(context);
		Log.d(TAG, "初始化...");
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
	/*
	 * 生成JSON字符串
	 * @see com.examw.test.model.sync.AppClient#toString()
	 */
	@Override
	public String toString() {
		Log.d(TAG, "生成JSON字符串...");
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}