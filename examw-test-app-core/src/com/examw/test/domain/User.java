package com.examw.test.domain;

import java.io.Serializable;

/**
 * 用户
 * @author fengwei.
 * @since 2014年12月1日 下午2:52:08.
 */
public class User implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String uid,username,password;
	private String registerCode;
	private String token;
	private String info;
	private String productUserId;
	/**
	 * 获取 ID
	 * @return id
	 * ID
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * 设置 ID
	 * @param id
	 * ID
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * 获取 uid
	 * @return uid
	 * uid
	 */
	public String getUid() {
		return uid;
	}
	/**
	 * 设置 uid
	 * @param uid
	 * uid
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}
	/**
	 * 获取 用户名
	 * @return username
	 * 用户名
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * 设置 用户名
	 * @param username
	 * 用户名
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * 获取 密码
	 * @return password
	 * 密码
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * 设置 密码
	 * @param password
	 * 密码
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * 获取 信息
	 * @return info
	 * 信息
	 */
	public String getInfo() {
		return info;
	}
	/**
	 * 设置 信息
	 * @param info
	 * 信息
	 */
	public void setInfo(String info) {
		this.info = info;
	}
	/**
	 * 获取 
	 * @return registerCode
	 * 
	 */
	public String getRegisterCode() {
		return registerCode;
	}
	/**
	 * 设置 
	 * @param registerCode
	 * 
	 */
	public void setRegisterCode(String registerCode) {
		this.registerCode = registerCode;
	}
	/**
	 * 获取 
	 * @return token
	 * 
	 */
	public String getToken() {
		return token;
	}
	/**
	 * 设置 
	 * @param token
	 * 
	 */
	public void setToken(String token) {
		this.token = token;
	}
	/**
	 * 获取 产品用户ID
	 * @return productUserId
	 * 
	 */
	public String getProductUserId() {
		return productUserId;
	}
	/**
	 * 设置 产品用户ID
	 * @param productUserId
	 * 
	 */
	public void setProductUserId(String productUserId) {
		this.productUserId = productUserId;
	}
}
