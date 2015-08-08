package com.examw.test.app;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.examw.codec.digest.DigestUtils;
import com.examw.test.utils.AESUtils;
import com.google.gson.Gson;

/**
 * 用户账户信息。
 * @author jeasonyoung
 * @since 2015年6月21日
 */
public final class UserAccount implements Serializable {
	private static final String TAG = "UserAccount";
	private static final long serialVersionUID = 1L;
	private static final String CURRENT_USER_KEY = "current_user", USER_KEY = "user_%s";
	private UserAccountModel model;
	private SharedPreferences preferences;
	private boolean isDirty;
	/**
	 * 构造函数。
	 * @param model
	 * 用户账号数据模型
	 */
	private UserAccount(UserAccountModel model){
		this.model = model;
		this.isDirty = false;
	}
	/**
	 * 构造函数。
	 * @param userId
	 * 用户ID。
	 * @param username
	 * 用户名。
	 */
	public UserAccount(String userId, String username){
		Log.d(TAG, String.format("初始化用户账户[%1$s,%2$s]信息...", userId, username));
		this.model = new UserAccountModel();
		this.model.setUserId(userId); 
		this.model.setUsername(username);
		this.isDirty = true;
	}
	/**
	 * 获取用户ID。
	 * @return 用户ID。
	 */
	public String getUserId() {
		return (this.model == null ? null : this.model.getUserId());
	}
	/**
	 * 获取用户账号。
	 * @return 用户账号。
	 */
	public String getUsername() {
		return (this.model == null ? null : this.model.getUsername());
	}
	/**
	 * 获取注册码。
	 * @return 注册码。
	 */
	public String getRegCode() {
		return (this.model == null ? null :  this.model.getRegCode());
	}
	/**
	 * 根据用户账号加载数据。
	 * @param username
	 * @return
	 */
	public static UserAccount loadAccount(String username){
		Log.d(TAG, "加载用户["+ username +"]信息...");
		if(StringUtils.isNotBlank(username)){
			String hex  =  DigestUtils.md5Hex(username);
			return loadAccountUserKey(String.format(USER_KEY, hex));
		}
		return null;
	}
	/**
	 * 加载当前用户。
	 * @return
	 */
	public static UserAccount loadCurrent(){
		Log.d(TAG, "加载当前用户["+CURRENT_USER_KEY+"]信息...");
		return loadAccountUserKey(CURRENT_USER_KEY);
	}
	//加载默认首选项。
	private static SharedPreferences loadDefaultSharedPreferences(){
		try {
			Log.d(TAG, "加载默认首选项...");
			//获取当前上下文
			Context context = AppContext.getContext();
			if(context == null) return null;
			//获取默认首选项
			return PreferenceManager.getDefaultSharedPreferences(context);
		} catch (Exception e) {
			Log.e(TAG, "加载默认首选项发生异常:" + e.getMessage(), e);
		}
		return null;
	}
	//通过用户Key加载用户信息。
	private static UserAccount loadAccountUserKey(String userKey){
		if(StringUtils.isNotBlank(userKey)){
			try {
				Log.d(TAG, "加载用户key=" + userKey);
				//获取首选项
				SharedPreferences  preferences = loadDefaultSharedPreferences();
				if(preferences == null){
					Log.d(TAG, "加载首选项失败!");
					return null;
				}
				if(preferences.contains(userKey)){
					String json = preferences.getString(userKey, null);
					//存储数据为空
					if(StringUtils.isBlank(json)){
						Log.d(TAG, "未能加载到数据:" + userKey);
						return null;
					}
					//进行Json转换
					Gson gson = new Gson();
					UserAccountModel userModel = gson.fromJson(json, UserAccountModel.class);
					if(userModel != null){//json反序列化成功
						//校验码比对
						String newCheckCode = userModel.createCheckCode();
						if(!StringUtils.equals(newCheckCode, userModel.getCheckCode())){
							Log.d(TAG, String.format("校验码不一致[%1$s/%2$s]", newCheckCode, userModel.getCheckCode()));
							return null;
						}
						//解密密码
						userModel.decyptPassword();
						//加载成功
						Log.d(TAG, "加载用户成功:" + gson.toJson(userModel));
						return new UserAccount(userModel);
					}
				}
			} catch (Exception e) {
				Log.e(TAG, "加载用户信息发生异常:"+ e.getMessage(), e);
			}
		}
		return null;
	}
	/**
	 * 验证密码。
	 * @param password
	 * 密码。
	 * @return 
	 */
	public boolean validatePassword(String password){
		Log.d(TAG, "验证密码:" + password);
		if(StringUtils.isNotBlank(password) && this.model != null){
			return StringUtils.equals(this.model.getPassword(), password);
		}
		return false;
	}
	/**
	 * 更新密码。
	 * @param password
	 * 新密码。
	 */
	public void updatePassword(String password){
		Log.d(TAG, "更新密码:" + password);
		if(StringUtils.isNotBlank(password) && this.model != null && !StringUtils.equals(password, this.model.getPassword())){
			this.model.setPassword(password);
			this.isDirty = true;
		}
	}
	/**
	 * 更新注册码。
	 * @param regCode
	 * 注册码。
	 */
	public void updateRegCode(String regCode){
		Log.d(TAG, "更新产品注册码:" + regCode);
		if(StringUtils.isNotBlank(regCode) && this.model != null &&  !StringUtils.equalsIgnoreCase(regCode, this.model.getRegCode())){
			this.model.setRegCode(regCode);
			this.isDirty = true;
		}
	}
	/**
	 * 保存数据。
	 * @return 保存成功为True，否则为False。
	 */
	public boolean save(){
		Log.d(TAG, "保存用户数据:" + this.getUsername());
		if(StringUtils.isBlank(this.getUsername())) return false;
		if(this.isDirty){
			String hex  =  DigestUtils.md5Hex(this.getUsername());
			boolean result = this.saveForUserKey(String.format(USER_KEY, hex));
			if(result){
				this.isDirty = false;
			}
			return result;
		}
		return true;
	}
	/**
	 * 保存数据为当前用户。
	 * @return 保存成功为True。
	 */
	public boolean saveForCurrent(){
		Log.d(TAG, "保存用户["+this.getUsername()+"]为当前用户...");
		//1.先保存到本地
		if(this.save()){
			//2.保存为当前用户
			return this.saveForUserKey(CURRENT_USER_KEY);
		}
		return false;
	}
	//按照用户Key保存数据
	private boolean saveForUserKey(String userKey){
		if(StringUtils.isNotBlank(userKey) && this.model != null){
			try {
				Log.d(TAG, "保存用户数据:" + userKey);
				//数据模型处理
				UserAccountModel userAccountModel = this.model.clone();
				//加密密码
				userAccountModel.encyptPassword();
				//生成校验码
				userAccountModel.setCheckCode(userAccountModel.createCheckCode());
				//
				Gson gson = new Gson();
				String json = gson.toJson(userAccountModel);
				if(StringUtils.isBlank(json)){
					Log.d(TAG, "序列化JSON无数据!");
					return false;
				}
				//获取首选项
				if(this.preferences == null){
					this.preferences = loadDefaultSharedPreferences();
				}
				//判断首选项
				if(this.preferences == null){
					Log.d(TAG, "加载首选项失败!");
					return false;
				}
				//
				Editor editor = this.preferences.edit();
				editor.putString(userKey, json);
				return editor.commit();
			} catch (Exception e) {
				Log.e(TAG, "保存用户数据发生异常:" + e.getMessage(), e);
			}
		}
		return false;
	}
	
	/**
	 * 清除用户数据。
	 */
	public void clean(){
		Log.d(TAG, "清除用户数据:" + this.getUsername());
		if(StringUtils.isBlank(this.getUsername())) return;	
		String hex  =  DigestUtils.md5Hex(this.getUsername());
		this.cleanForUserKey(String.format(USER_KEY, hex));
	}
	/**
	 * 清除当前用户数据。
	 */
	public void cleanForCurrent(){
		Log.d(TAG, "清除当前用户数据:" + this.getUsername());
		this.cleanForUserKey(CURRENT_USER_KEY);
	}
	/*
	 * 重载。
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return StringUtils.join(new String[]{
				StringUtils.trimToEmpty(this.getUserId()),
				StringUtils.trimToEmpty(this.getUsername()),
				StringUtils.trimToEmpty(this.getRegCode())
		}, ",");
	}
	//清除用户数据
	private boolean cleanForUserKey(String userKey){
		try {
			Log.d(TAG, "清除用户数据:" + userKey);
			if(StringUtils.isBlank(userKey))return false;
			//获取首选项
			if(this.preferences == null){
				this.preferences = loadDefaultSharedPreferences();
			}
			//判断首选项
			if(this.preferences == null){
				Log.d(TAG, "加载首选项失败!");
				return false;
			}
			if(this.preferences.contains(userKey)){
				Log.d(TAG, "用户key存在:" + userKey);
				Editor editor = this.preferences.edit();
				editor.remove(userKey);
				boolean result = editor.commit();
				Log.d(TAG, "清除用户key=[" + userKey+ "]:" + result);
				return result;
			}
		} catch (Exception e) {
			Log.e(TAG, "清除用户数据异常:" + e.getMessage(), e);
		}
		return false;
	}
	
	/**
	 * 用户数据模型。
	 * 
	 * @author jeasonyoung
	 * @since 2015年6月24日
	 */
	private class UserAccountModel implements Serializable, Cloneable{
		private static final long serialVersionUID = 1L;
		private String userId,username,password, regCode,checkCode;
		/**
		 * 获取用户ID。
		 * @return 用户ID。
		 */
		public String getUserId() {
			return userId;
		}
		/**
		 * 设置用户ID。
		 * @param userId 
		 *	  用户ID。
		 */
		public void setUserId(String userId) {
			this.userId = userId;
		}
		/**
		 * 获取用户账号。
		 * @return 用户账号。
		 */
		public String getUsername() {
			return username;
		}
		/**
		 * 设置用户账号。
		 * @param username 
		 *	  用户账号。
		 */
		public void setUsername(String username) {
			this.username = username;
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
		 * 获取产品注册码。
		 * @return 产品注册码。
		 */
		public String getRegCode() {
			return regCode;
		}
		/**
		 * 设置产品注册码。
		 * @param regCode 
		 *	  产品注册码。
		 */
		public void setRegCode(String regCode) {
			this.regCode = regCode;
		}
		/**
		 * 获取校验码。
		 * @return 校验码。
		 */
		public String getCheckCode() {
			return checkCode;
		}
		/**
		 * 设置校验码。
		 * @param checkCode 
		 *	  校验码。
		 */
		public void setCheckCode(String checkCode) {
			this.checkCode = checkCode;
		}
		/*
		 * 复制对象。
		 * @see java.lang.Object#clone()
		 */
		@Override
		public UserAccountModel clone(){
			try {
				Log.d(TAG, "数据模型对象复制...");
				UserAccountModel model = (UserAccountModel)super.clone();
				model.setUserId(this.getUserId());
				model.setUsername(this.getUsername());
				model.setPassword(this.getPassword());
				model.setRegCode(this.getRegCode());
				model.setCheckCode(this.getCheckCode());
				return model;
			} catch (CloneNotSupportedException e) {
				Log.e(TAG, "对象复制异常:" + e.getMessage(), e);
			}
			return null;
		}
		
		/**
		 * 加密密码。
		 */
		public void encyptPassword(){
			if(StringUtils.isNotBlank(this.getUsername()) && StringUtils.isNotBlank(this.getPassword())){
				Log.d(TAG, "加密密码:"+ this.getPassword());
				String hex = AESUtils.encryptToHex(this.getPassword(), this.getUsername());
				Log.d(TAG, "加密后的密码Hex:" + hex);
				this.setPassword(hex);
			}
		}
		/**
		 * 解密密码。
		 */
		public void decyptPassword(){
			if(StringUtils.isNotBlank(this.getUsername()) && StringUtils.isNotBlank(this.getPassword())){
				Log.d(TAG, "解密密码:" + this.getPassword());
				String pwd = AESUtils.decryptFromHex(this.getPassword(), this.getUsername());
				Log.d(TAG, "解密后的密码明文:" + pwd);
				this.setPassword(pwd);
			}
		}
		/**
		 * 创建校验码。
		 * @return
		 */
		public String createCheckCode(){
			Log.d(TAG, "生成校验码...");
			String s = StringUtils.join(new String[]{  
					StringUtils.trimToEmpty(this.getUserId()),
					StringUtils.trimToEmpty(this.getUsername()),
					StringUtils.trimToEmpty(this.getPassword()),
					StringUtils.trimToEmpty(this.getRegCode())
			}, "-");
			Log.d(TAG, "源字符串为:" + s);
			s = StringUtils.reverse(s);
			Log.d(TAG, "反转后的字符串:" + s);
			return DigestUtils.md5Hex(s);
		}
	}
}