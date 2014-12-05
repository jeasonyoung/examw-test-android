package com.examw.test.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author fengwei.
 * @since 2014年11月27日 下午4:36:51.
 */
public class ItemRecord implements Serializable{
	private static final long serialVersionUID = 1L;
	private String recordId,userId,userName,itemId,itemContent,subjectId,terminalId,remarks;
	private Date createTime;
	/**
	 * 获取 考试记录ID 
	 * @return recordId
	 * 考试记录ID 
	 */
	public String getRecordId() {
		return recordId;
	}
	/**
	 * 设置 考试记录ID 
	 * @param recordId
	 * 考试记录ID 
	 */
	public void setRecordId(String recordId) {
		this.recordId = recordId;
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
	 * 获取 用户名
	 * @return userName
	 * 用户名
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * 设置 用户名
	 * @param userName
	 * 用户名
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * 获取 试题ID
	 * @return itemId
	 *  试题ID
	 */
	public String getItemId() {
		return itemId;
	}
	/**
	 * 设置  试题ID
	 * @param itemId
	 *  试题ID
	 */
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	/**
	 * 获取 试题内容
	 * @return itemContent
	 * 试题内容
	 */
	public String getItemContent() {
		return itemContent;
	}
	/**
	 * 设置 试题内容
	 * @param itemContent
	 * 试题内容
	 */
	public void setItemContent(String itemContent) {
		this.itemContent = itemContent;
	}
	/**
	 * 获取 科目ID
	 * @return subjectId
	 *  科目ID
	 */
	public String getSubjectId() {
		return subjectId;
	}
	/**
	 * 设置  科目ID
	 * @param subjectId
	 *  科目ID
	 */
	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}
	/**
	 * 获取 设备终端ID
	 * @return terminalId
	 * 设备终端ID
	 */
	public String getTerminalId() {
		return terminalId;
	}
	/**
	 * 设置 设备终端ID
	 * @param terminalId
	 * 设备终端ID
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
	public Date getCreateTime() {
		return createTime;
	}
	/**
	 * 设置 创建时间
	 * @param createTime
	 * 创建时间
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
}
