package com.examw.test.model;

import java.io.Serializable;

/**
 * 
 * @author fengwei.
 * @since 2015年1月9日 下午2:21:39.
 */
public class RemoteUserInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	private String UserName,uid,GroupID,GroupExpiry,RegTime,LoginTime;
	private Double ExamwB,UserMoney;
	/**
	 * 获取 
	 * @return userName
	 * 
	 */
	public String getUserName() {
		return UserName;
	}
	/**
	 * 设置 
	 * @param userName
	 * 
	 */
	public void setUserName(String userName) {
		UserName = userName;
	}
	/**
	 * 获取 
	 * @return uid
	 * 
	 */
	public String getUid() {
		return uid;
	}
	/**
	 * 设置 
	 * @param uid
	 * 
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}
	/**
	 * 获取 
	 * @return groupID
	 * 
	 */
	public String getGroupID() {
		return GroupID;
	}
	/**
	 * 设置 
	 * @param groupID
	 * 
	 */
	public void setGroupID(String groupID) {
		GroupID = groupID;
	}
	/**
	 * 获取 
	 * @return groupExpiry
	 * 
	 */
	public String getGroupExpiry() {
		return GroupExpiry;
	}
	/**
	 * 设置 
	 * @param groupExpiry
	 * 
	 */
	public void setGroupExpiry(String groupExpiry) {
		GroupExpiry = groupExpiry;
	}
	/**
	 * 获取 
	 * @return regTime
	 * 
	 */
	public String getRegTime() {
		return RegTime;
	}
	/**
	 * 设置 
	 * @param regTime
	 * 
	 */
	public void setRegTime(String regTime) {
		RegTime = regTime;
	}
	/**
	 * 获取 
	 * @return loginTime
	 * 
	 */
	public String getLoginTime() {
		return LoginTime;
	}
	/**
	 * 设置 
	 * @param loginTime
	 * 
	 */
	public void setLoginTime(String loginTime) {
		LoginTime = loginTime;
	}
	/**
	 * 获取 
	 * @return examwB
	 * 
	 */
	public Double getExamwB() {
		return ExamwB;
	}
	/**
	 * 设置 
	 * @param examwB
	 * 
	 */
	public void setExamwB(Double examwB) {
		ExamwB = examwB;
	}
	/**
	 * 获取 
	 * @return userMoney
	 * 
	 */
	public Double getUserMoney() {
		return UserMoney;
	}
	/**
	 * 设置 
	 * @param userMoney
	 * 
	 */
	public void setUserMoney(Double userMoney) {
		UserMoney = userMoney;
	}
	
}
