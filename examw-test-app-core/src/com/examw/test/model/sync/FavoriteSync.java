package com.examw.test.model.sync;

import java.io.Serializable;
/**
 * 收藏同步。
 * 
 * @author yangyong
 * @since 2015年3月9日
 */
public class FavoriteSync implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id,subjectId,itemId,content,remarks;
	private Integer itemType,status;
	private String createTime;
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
	 * 获取试题内容JSON。
	 * @return 试题内容JSON。
	 */
	public String getContent() {
		return content;
	}
	/**
	 * 设置试题内容JSON。
	 * @param content 
	 *	  试题内容JSON。
	 */
	public void setContent(String content) {
		this.content = content;
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
	 * 获取状态(0-删除，1-收藏)。
	 * @return 状态(0-删除，1-收藏)。
	 */
	public Integer getStatus() {
		return status;
	}
	/**
	 * 设置状态(0-删除，1-收藏)。
	 * @param status 
	 *	  状态(0-删除，1-收藏)。
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}
	/**
	 * 获取收藏时间。
	 * @return 收藏时间。
	 */
	public String getCreateTime() {
		return createTime;
	}
	/**
	 * 设置收藏时间。
	 * @param createTime 
	 *	  收藏时间。
	 */
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("id").append(":").append(this.getId()).append(",")
		           .append("subjectId").append(":").append(this.getSubjectId()).append(",")
		           .append("itemId").append(":").append(this.getItemId()).append(",")
		           .append("content").append(":").append(this.getContent()).append(",")
		           .append("remarks").append(":").append(this.getRemarks()).append(",")
		           .append("itemType").append(":").append(this.getItemType()).append(",")
		           .append("status").append(":").append(this.getStatus()).append(",")
				   .append("createTime").append(":").append(this.getCreateTime());
		return builder.toString();
	}
}