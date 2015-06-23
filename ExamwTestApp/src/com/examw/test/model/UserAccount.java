package com.examw.test.model;

import java.io.Serializable;

import android.util.Log;
/**
 * 用户账户信息。
 * @author jeasonyoung
 * @since 2015年6月21日
 */
public final class UserAccount implements Serializable {
	private static final String TAG = "UserAccount";
	private static final long serialVersionUID = 1L;
	
	
	private String userId,username,password, regCode;
	private boolean isDirty;
	/**
	 * 构造函数。
	 * @param userId
	 * 用户ID。
	 * @param username
	 * 用户名。
	 */
	public UserAccount(String userId, String username){
		Log.d(TAG, String.format("初始化用户账户[%1$s,%2$s]信息...", userId, username));
		this.userId = userId;
		this.username = username;
		this.password = this.regCode = "";
		this.isDirty = true;
	}
	/**
	 * 获取用户ID。
	 * @return 用户ID。
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * 获取用户账号。
	 * @return 用户账号。
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * 获取注册码。
	 * @return 注册码。
	 */
	public String getRegCode() {
		return regCode;
	}
	
	/**
	 * 根据用户账号加载数据。
	 * @param username
	 * @return
	 */
	public static UserAccount loadAccount(String username){
		return null;
	}
	/**
	 * 加载当前用户。
	 * @return
	 */
	public static UserAccount loadCurrent(){
		return null;
	}
	/**
	 * 验证密码。
	 * @param password
	 * 密码。
	 * @return 
	 */
	public boolean validateWithPassword(String password){
		return true;
	}
	/**
	 * 更新密码。
	 * @param password
	 * 新密码。
	 */
	public void updatePassword(String password){
		if(password == null ||password.length() == 0)return;
		
	}
	/**
	 * 更新注册码。
	 * @param regCode
	 * 注册码。
	 */
	public void updateRegCode(String regCode){
		
	}
	/**
	 * 保存数据。
	 * @return 保存成功为True，否则为False。
	 */
	public boolean save(){
		
		return false;
	}
	/**
	 * 保存数据为当前用户。
	 * @return
	 */
	public boolean saveForCurrent(){
		return false;
	}
	/**
	 * 清除账号信息。
	 */
	public void clean(){
		
	}
	/**
	 * 清除当前用户信息。
	 */
	public void cleanForCurrent(){
		
	}
}