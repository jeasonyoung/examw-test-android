package com.examw.test.model;

import java.io.Serializable;

/**
 * 前段用户信息。
 * 
 * @author yangyong
 * @since 2014年10月17日
 */
public class FrontUserInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String code,name;
	/**
	 * 获取用户代码。
	 * @return 用户代码。
	 */
	public String getCode() {
		return code;
	}
	/**
	 * 设置用户代码。
	 * @param code 
	 *	  用户代码。
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * 获取用户名。
	 * @return 用户名。
	 */
	public String getName() {
		return name;
	}
	/**
	 * 设置用户名。
	 * @param name 
	 *	  用户名。
	 */
	public void setName(String name) {
		this.name = name;
	}
}