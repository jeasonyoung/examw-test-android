package com.examw.test.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
/**
 * 用户试卷记录信息。
 * 
 * @author yangyong
 * @since 2014年10月10日
 */
public class UserPaperRecordInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id,userId,userName,paperId,productId,paperName,paperTypeName,subjectId;
	private Integer status,terminalCode,paperType,rightNum;
	private Long usedTime;
	private BigDecimal score;
	private Date createTime,lastTime;
	private Set<UserItemRecordInfo> items;
	/**
	 * 获取记录ID。
	 * @return 记录ID。
	 */
	public String getId() {
		return id;
	}
	/**
	 * 设置记录ID。
	 * @param id 
	 *	 记录ID。
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
	 * 获取试卷ID。
	 * @return 试卷ID。
	 */
	public String getPaperId() {
		return paperId;
	}
	/**
	 * 设置试卷ID。
	 * @param paperId 
	 *	  试卷ID。
	 */
	public void setPaperId(String paperId) {
		this.paperId = paperId;
	}
	/**
	 * 获取所属产品ID。
	 * @return 所属产品ID。
	 */
	public String getProductId() {
		return productId;
	}
	/**
	 * 设置所属产品ID。
	 * @param productId 
	 *	  所属产品ID。
	 */
	public void setProductId(String productId) {
		this.productId = productId;
	}
	/**
	 * 获取用户终端类型代码。
	 * @return 用户终端类型代码。
	 */
	public Integer getTerminalCode() {
		return terminalCode;
	}
	/**
	 * 设置用户终端类型代码。
	 * @param terminalCode 
	 *	  用户终端类型代码。
	 */
	public void setTerminalCode(Integer terminalCode) {
		this.terminalCode = terminalCode;
	}
	/**
	 * 获取用户试卷状态。
	 * @return 用户试卷状态。
	 */
	public Integer getStatus() {
		return status;
	}
	/**
	 * 设置用户试卷状态。
	 * @param status 
	 *	  用户试卷状态。
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}
	/**
	 * 获取用户试卷用时（秒）。
	 * @return 用户试卷用时（秒）。
	 */
	public Long getUsedTime() {
		return usedTime;
	}
	/**
	 * 设置用户试卷用时（秒）。
	 * @param usedTime 
	 *	  用户试卷用时（秒）。
	 */
	public void setUsedTime(Long usedTime) {
		this.usedTime = usedTime;
	}
	/**
	 * 获取用户得分。
	 * @return 用户得分。
	 */
	public BigDecimal getScore() {
		return score;
	}
	/**
	 * 设置用户得分。
	 * @param score 
	 *	  用户得分。
	 */
	public void setScore(BigDecimal score) {
		this.score = score;
	}
	/**
	 * 获取创建时间。
	 * @return 创建时间。
	 */
	public Date getCreateTime() {
		return createTime;
	}
	/**
	 * 设置创建时间。
	 * @param createTime 
	 *	  创建时间。
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	/**
	 * 获取最后修改时间。
	 * @return 最后修改时间。
	 */
	public Date getLastTime() {
		return lastTime;
	}
	/**
	 * 设置最后修改时间。
	 * @param lastTime 
	 *	  最后修改时间。
	 */
	public void setLastTime(Date lastTime) {
		this.lastTime = lastTime;
	}
	/**
	 * 获取试题记录集合。
	 * @return 试题记录集合。
	 */
	public Set<UserItemRecordInfo> getItems() {
		return items;
	}
	/**
	 * 设置试题记录集合。
	 * @param items 
	 *	  试题记录集合。
	 */
	public void setItems(Set<UserItemRecordInfo> items) {
		this.items = items;
	}
	/**
	 * 获取 试卷名称
	 * @return paperName
	 * 试卷名称
	 */
	public String getPaperName() {
		return paperName;
	}
	/**
	 * 设置 试卷名称
	 * @param paperName
	 * 试卷名称
	 */
	public void setPaperName(String paperName) {
		this.paperName = paperName;
	}
	/**
	 * 获取 试卷类型名称
	 * @return paperTypeName
	 * 试卷类型名称
	 */
	public String getPaperTypeName() {
		return paperTypeName;
	}
	/**
	 * 设置 试卷类型名称
	 * @param paperTypeName
	 * 试卷类型名称
	 */
	public void setPaperTypeName(String paperTypeName) {
		this.paperTypeName = paperTypeName;
	}
	/**
	 * 获取  试卷类型
	 * @return paperType
	 *  试卷类型
	 */
	public Integer getPaperType() {
		return paperType;
	}
	/**
	 * 设置 试卷类型
	 * @param paperType
	 *  试卷类型
	 */
	public void setPaperType(Integer paperType) {
		this.paperType = paperType;
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
	 * 获取 科目ID
	 * @return subjectId
	 * 科目ID
	 */
	public String getSubjectId() {
		return subjectId;
	}
	/**
	 * 获取 做对的题目个数
	 * @return rightNum
	 * 做对的题目个数
	 */
	public Integer getRightNum() {
		return rightNum;
	}
	/**
	 * 设置 做对的题目个数
	 * @param rightNum
	 * 做对的题目个数
	 */
	public void setRightNum(Integer rightNum) {
		this.rightNum = rightNum;
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
	
}