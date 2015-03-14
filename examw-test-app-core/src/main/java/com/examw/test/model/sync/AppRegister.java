package com.examw.test.model.sync;


/**
 * 应用注册。
 * 
 * @author yangyong
 * @since 2015年2月14日
 */
public class AppRegister extends AppClient {
	private static final long serialVersionUID = 1L;
	private String userId,code;
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
	 * 获取注册码。
	 * @return 注册码。
	 */
	public String getCode() {
		return code;
	}
	/**
	 * 设置注册码。
	 * @param code 
	 *	  注册码。
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/*
	 * 重载。
	 * @see com.examw.test.model.api.AppClient#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(super.toString()).append(",")
		.append("userId").append("=").append(this.userId).append(",")
		.append("code").append("=").append(this.code);
		return builder.toString();
	}
}