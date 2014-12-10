package com.examw.test.domain;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 考试记录
 * @author fengwei.
 * @since 2014年11月27日 下午4:38:01.
 */
public class PaperRecord implements Serializable{
	private static final long serialVersionUID = 1L;
	private String recordId,paperId,paperName,userId,userName,productId,terminalId;
	private Double score;
	private Integer paperType,usedTime,rightNum,status;
	private String createTime,lastTime;
	private ArrayList<ItemRecord> items;
	private String torf;
	/**
	 * 获取 记录ID
	 * @return recordId
	 * 
	 */
	public String getRecordId() {
		return recordId;
	}

	/**
	 * 设置 记录ID
	 * @param recordId
	 * 
	 */
	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	/**
	 * 获取 试卷ID
	 * @return paperId
	 * 
	 */
	public String getPaperId() {
		return paperId;
	}

	/**
	 * 设置 试卷ID
	 * @param paperId
	 * 
	 */
	public void setPaperId(String paperId) {
		this.paperId = paperId;
	}

	/**
	 * 获取 试卷名称
	 * @return paperName
	 * 
	 */
	public String getPaperName() {
		return paperName;
	}

	/**
	 * 设置 试卷名称
	 * @param paperName
	 * 
	 */
	public void setPaperName(String paperName) {
		this.paperName = paperName;
	}

	/**
	 * 获取 题目记录
	 * @return items
	 * 
	 */
	public ArrayList<ItemRecord> getItems() {
		return items;
	}

	/**
	 * 设置 题目记录
	 * @param items
	 * 
	 */
	public void setItems(ArrayList<ItemRecord> items) {
		this.items = items;
	}

	/**
	 * 获取 用户ID
	 * @return userId
	 * 
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * 设置 用户ID
	 * @param userId
	 * 
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * 获取 产品ID
	 * @return productId
	 * 
	 */
	public String getProductId() {
		return productId;
	}

	/**
	 * 设置 产品ID
	 * @param productId
	 * 
	 */
	public void setProductId(String productId) {
		this.productId = productId;
	}

	/**
	 * 获取 终端ID
	 * @return terminalId
	 * 
	 */
	public String getTerminalId() {
		return terminalId;
	}

	/**
	 * 设置  终端ID
	 * @param terminalId
	 * 
	 */
	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	/**
	 * 获取 状态
	 * @return status
	 * 
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * 设置 状态
	 * @param status
	 * 
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 * 获取 得分
	 * @return score
	 * 
	 */
	public Double getScore() {
		return score;
	}

	/**
	 * 设置 得分
	 * @param score
	 * 
	 */
	public void setScore(Double score) {
		this.score = score;
	}

	/**
	 * 获取 用时
	 * @return usedTime
	 * 
	 */
	public Integer getUsedTime() {
		return usedTime;
	}

	/**
	 * 设置 用时
	 * @param usedTime
	 * 
	 */
	public void setUsedTime(Integer usedTime) {
		this.usedTime = usedTime;
	}

	/**
	 * 获取 正确数
	 * @return rightNum
	 * 
	 */
	public Integer getRightNum() {
		return rightNum;
	}

	/**
	 * 设置 正确数
	 * @param rightNum
	 * 
	 */
	public void setRightNum(Integer rightNum) {
		this.rightNum = rightNum;
	}

	/**
	 * 获取 创建时间
	 * @return createTime
	 * 
	 */
	public String getCreateTime() {
		return createTime;
	}

	/**
	 * 设置 创建时间
	 * @param createTime
	 * 
	 */
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	/**
	 * 获取 修改时间
	 * @return lastTime
	 * 
	 */
	public String getLastTime() {
		return lastTime;
	}

	/**
	 * 设置 修改时间
	 * @param lastTime
	 * 
	 */
	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
	}

	/**
	 * 获取 用户名
	 * @return userName
	 * 
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * 设置 用户名
	 * @param userName
	 * 
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	/**
	 * 获取 试卷类型
	 * @return paperType
	 * 
	 */
	public Integer getPaperType() {
		return paperType;
	}

	/**
	 * 设置 试卷类型
	 * @param paperType
	 * 
	 */
	public void setPaperType(Integer paperType) {
		this.paperType = paperType;
	}
	
	/**
	 * 获取 
	 * @return torf
	 * 
	 */
	public String getTorf() {
		return torf;
	}

	/**
	 * 设置 
	 * @param torf
	 * 
	 */
	public void setTorf(String torf) {
		this.torf = torf;
	}
	
	public PaperRecord() {
	}
	

	public PaperRecord(String recordId, String paperId, String paperName,Integer paperType,
			String userId, String userName, String productId,
			String terminalId, Integer status, Double score,
			Integer usedTime, Integer rightNum, String createTime, String lastTime,String torf) {
		super();
		this.recordId = recordId;
		this.paperId = paperId;
		this.paperName = paperName;
		this.paperType = paperType;
		this.userId = userId;
		this.userName = userName;
		this.productId = productId;
		this.terminalId = terminalId;
		this.status = status;
		this.score = score;
		this.usedTime = usedTime;
		this.rightNum = rightNum;
		this.createTime = createTime;
		this.lastTime = lastTime;
		this.torf = torf;
	}

}
