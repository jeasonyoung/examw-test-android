package com.examw.test.domain;

import java.io.Serializable;

/**
 * 题目收藏
 * @author fengwei.
 * @since 2014年11月27日 下午4:37:11.
 */
public class FavoriteItem implements Serializable{//extends FavoriteSync {
	private static final long serialVersionUID = 1L;
	//,userId ,username ,itemId ,itemType ,itemContent ,subjectId ,terminalId ,remarks ,createTime
	private String itemId,username,userId,itemContent,subjectId,terminalId,userAnswer,remarks,createTime;
	private Integer itemType,status,sync;
	/**
	 * 获取 
	 * @return sync
	 * 
	 */
	public Integer getSync() {
		return sync;
	}
	/**
	 * 设置 
	 * @param sync
	 * 
	 */
	public void setSync(Integer sync) {
		this.sync = sync;
	}
	private Boolean needDelete;
	/**
	* 获取 题目ID
	* @return itemId
	*
	*/
	public String getItemId() {
	return itemId;
	}
	/**
	* 设置 题目ID
	* @param itemId
	*
	*/
	public void setItemId(String itemId) {
	this.itemId = itemId;
	}
	/**
	* 获取 用户名
	* @return username
	*
	*/
	public String getUsername() {
	return username;
	}
	/**
	* 设置 用户名
	* @param username
	*
	*/
	public void setUsername(String username) {
	this.username = username;
	}
	/**
	* 获取 是否删除
	* @return needDelete
	* 是否删除
	*/
	public Boolean isNeedDelete() {
	return needDelete;
	}
	/**
	* 设置 是否删除
	* @param needDelete
	* 是否删除
	*/
	public void setNeedDelete(Boolean needDelete) {
	this.needDelete = needDelete;
	}
	/**
	* 获取 用户ID
	* @return userId
	* 用户ID
	*/
	public String getUserId() {
	return userId;
	}
	/**
	* 设置 用户ID
	* @param userId
	* 用户ID
	*/
	public void setUserId(String userId) {
	this.userId = userId;
	}
	/**
	* 获取 题目内容
	* @return itemContent
	* 题目内容
	*/
	public String getItemContent() {
	return itemContent;
	}
	/**
	* 设置 题目内容
	* @param itemContent
	* 题目内容
	*/
	public void setItemContent(String itemContent) {
	this.itemContent = itemContent;
	}
	/**
	* 获取 科目ID
	* @return subjectId
	* 科目ID
	*/
	public String getSubjectId() {
	return subjectId;
	}
	/**
	* 设置 科目ID
	* @param subjectId
	* 科目ID
	*/
	public void setSubjectId(String subjectId) {
	this.subjectId = subjectId;
	}
	/**
	* 获取 终端号
	* @return terminalId
	* 终端号
	*/
	public String getTerminalId() {
	return terminalId;
	}
	/**
	* 设置 终端号
	* @param terminalId
	* 终端号
	*/
	public void setTerminalId(String terminalId) {
	this.terminalId = terminalId;
	}
	/**
	* 获取 备注
	* @return remarks
	* 备注
	*/
	public String getRemarks() {
	return remarks;
	}
	/**
	* 设置 备注
	* @param remarks
	* 备注
	*/
	public void setRemarks(String remarks) {
	this.remarks = remarks;
	}
	/**
	* 获取 创建时间
	* @return createTime
	* 创建时间
	*/
	public String getCreateTime() {
	return createTime;
	}
	/**
	* 设置 创建时间
	* @param createTime
	* 创建时间
	*/
	public void setCreateTime(String createTime) {
	this.createTime = createTime;
	}
	/**
	* 获取 题型
	* @return itemType
	* 题型
	*/
	public Integer getItemType() {
	return itemType;
	}
	/**
	* 设置 题型
	* @param itemType
	* 题型
	*/
	public void setItemType(Integer itemType) {
	this.itemType = itemType;
	}
	/**
	* 获取 状态
	* @return status
	* 状态
	*/
	public Integer getStatus() {
	return status;
	}
	/**
	* 设置 状态
	* @param status
	* 状态
	*/
	public void setStatus(Integer status) {
	this.status = status;
	}
	/**
	* 获取 用户答案
	* @return userAnswer
	* 用户答案
	*/
	public String getUserAnswer() {
	return userAnswer;
	}
	/**
	* 设置 用户答案
	* @param userAnswer
	* 用户答案
	*/
	public void setUserAnswer(String userAnswer) {
	this.userAnswer = userAnswer;
	}
	public FavoriteItem() {
	}
	public FavoriteItem(String itemId, String username, String itemContent,
	String subjectId, String userAnswer, String remarks,
	String createTime, Integer itemType) {
	this.itemId = itemId;
	this.username = username;
	this.itemContent = itemContent;
	this.subjectId = subjectId;
	this.userAnswer = userAnswer;
	this.remarks = remarks;
	this.createTime = createTime;
	this.itemType = itemType;
	}
	
}
