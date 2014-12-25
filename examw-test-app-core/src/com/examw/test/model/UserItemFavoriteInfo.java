package com.examw.test.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户试题收藏信息。
 * 
 * @author yangyong
 * @since 2014年10月11日
 */
public class UserItemFavoriteInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	private String id,userId,subjectId,itemId,itemContent,remarks;
	private Integer itemType,terminalCode;
	private Date createTime;
	/**
	 * 获取收藏ID。
	 * @return 收藏ID。
	 */
	public String getId() {
		return id;
	}
	/**
	 * 设置收藏ID。
	 * @param id 
	 *	  收藏ID。
	 */
	public void setId(String id) {
		this.id = id;
	}
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
	 * 获取所属科目ID。
	 * @return 所属科目ID。
	 */
	public String getSubjectId() {
		return subjectId;
	}
	/**
	 * 设置所属科目ID。
	 * @param subjectId 
	 *	  所属科目ID。
	 */
	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}
	/**
	 * 获取所属试题ID。
	 * @return 所属试题ID。
	 */
	public String getItemId() {
		return itemId;
	}
	/**
	 * 设置所属试题ID。
	 * @param itemId 
	 *	  所属试题ID。
	 */
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	/**
	 * 获取所属试题内容JSON。
	 * @return 所属试题内容JSON。
	 */
	public String getItemContent() {
		return itemContent;
	}
	/**
	 * 设置所属试题内容JSON。
	 * @param itemContent 
	 *	  所属试题内容JSON。
	 */
	public void setItemContent(String itemContent) {
		this.itemContent = itemContent;
	}
	/**
	 * 获取备注。
	 * @return 备注。
	 */
	public String getRemarks() {
		return remarks;
	}
	/**
	 * 设置备注。
	 * @param remarks 
	 *	  备注。
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	/**
	 * 获取用户终端代码。
	 * @return 用户终端代码。
	 */
	public Integer getTerminalCode() {
		return terminalCode;
	}
	/**
	 * 设置用户终端代码。
	 * @param terminalCode 
	 *	  用户终端代码。
	 */
	public void setTerminalCode(Integer terminalCode) {
		this.terminalCode = terminalCode;
	}
	/**
	 * 获取所属题型。
	 * @return 所属题型。
	 */
	public Integer getItemType() {
		return itemType;
	}
	/**
	 * 设置所属题型。
	 * @param itemType 
	 *	  所属题型。
	 */
	public void setItemType(Integer itemType) {
		this.itemType = itemType;
	}
	/**
	 * 获取收藏时间。
	 * @return 收藏时间。
	 */
	public Date getCreateTime() {
		return createTime;
	}
	/**
	 * 设置收藏时间。
	 * @param createTime 
	 *	  收藏时间。
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}