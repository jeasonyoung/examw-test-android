package com.examw.test.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 做题记录
 * @author fengwei.
 * @since 2014年11月27日 下午4:36:51.
 */
public class ItemRecordModel implements Serializable{
	private static final long serialVersionUID = 1L;
	private String recordId,userId,structureId,subjectId,userName,answer,itemId,itemContent,terminalId;
	private Integer itemType,status,sync;
	private BigDecimal score;
	private String createTime;
	private String lastTime;
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
	 * 获取 结构ID
	 * @return structureId
	 * 结构ID
	 */
	public String getStructureId() {
		return structureId;
	}
	/**
	 * 设置 结构ID
	 * @param structureId
	 * 结构ID
	 */
	public void setStructureId(String structureId) {
		this.structureId = structureId;
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
	 * 获取 用户答案
	 * @return answer
	 * 用户答案
	 */
	public String getAnswer() {
		return answer;
	}
	/**
	 * 设置 用户答案
	 * @param answer
	 * 用户答案
	 */
	public void setAnswer(String answer) {
		this.answer = answer;
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
	 * 获取 得分
	 * @return score
	 * 得分
	 */
	public BigDecimal getScore() {
		return score;
	}
	/**
	 * 设置 得分
	 * @param score
	 * 得分
	 */
	public void setScore(BigDecimal score) {
		this.score = score;
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
	 * 获取 更新时间
	 * @return lastTime
	 * 更新时间
	 */
	public String getLastTime() {
		return lastTime;
	}
	/**
	 * 设置 更新时间
	 * @param lastTime
	 * 更新时间
	 */
	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
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
	 * 设置科目ID 
	 * @param subjectId
	 * 科目ID
	 */
	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}
	public ItemRecordModel() {}
	
//	/recordId,structureId,itemId,answer,status,score
	public ItemRecordModel(String recordId, String structureId, String itemId,
			String answer, int status, BigDecimal score) {
		this.recordId = recordId;
		this.structureId = structureId;
		this.itemId = itemId;
		this.answer = answer;
		this.status = status;
		this.score = score;
	}
	
	public String getItemTypeName()
	{
		if(itemType == null) return "";
		return null;//AppConstant.getPaperTypeName(itemType);
	}
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
	
	
}
