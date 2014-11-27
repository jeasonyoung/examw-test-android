package com.examw.test.model;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 * 试卷信息基类
 * 
 * @author yangyong
 * @since 2014年9月22日
 */
public abstract class BasePaperInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id,name,description,typeName,statusName,examId,examName,subjectId,subjectName,
	                        sourceId,sourceName,areaId,areaName,userId,userName;
	private Integer type,status,price,time,year;
	private BigDecimal score;
	private String createTime,lastTime,publishTime;
	/**
	 * 获取试卷ID。
	 * @return 试卷ID。
	 */
	public String getId() {
		return id;
	}
	/**
	 * 设置试卷ID。
	 * @param id 
	 *	  试卷ID。
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * 获取试卷名称。
	 * @return 试卷名称。
	 */
	public String getName() {
		return name;
	}
	/**
	 * 设置试卷名称。
	 * @param name 
	 *	  试卷名称。
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 获取试卷描述。
	 * @return 试卷描述。
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * 设置试卷描述。
	 * @param description 
	 *	  试卷描述。
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * 获取所属考试ID。
	 * @return 所属考试ID。
	 */
	public String getExamId() {
		return examId;
	}
	/**
	 * 设置所属考试ID。
	 * @param examId 
	 *	  所属考试ID。
	 */
	public void setExamId(String examId) {
		this.examId = examId;
	}
	/**
	 * 获取所属考试名称。
	 * @return 所属考试名称。
	 */
	public String getExamName() {
		return examName;
	}
	/**
	 * 设置所属考试名称。
	 * @param examName 
	 *	  所属考试名称。
	 */
	public void setExamName(String examName) {
		this.examName = examName;
	}
	/**
	 * 获取所属考试科目ID。
	 * @return 所属考试科目ID。
	 */
	public String getSubjectId() {
		return subjectId;
	}
	/**
	 * 设置所属考试科目ID。
	 * @param subjectId 
	 *	  所属考试科目ID。
	 */
	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}
	/**
	 * 获取所属考试科目名称。
	 * @return 所属考试科目名称。
	 */
	public String getSubjectName() {
		return subjectName;
	}
	/**
	 * 设置所属考试科目名称。
	 * @param subjectName 
	 *	  所属考试科目名称。
	 */
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	/**
	 * 获取所属来源ID。
	 * @return 所属来源ID。
	 */
	public String getSourceId() {
		return sourceId;
	}
	/**
	 * 设置所属来源ID。
	 * @param sourceId 
	 *	  所属来源ID。
	 */
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
	/**
	 * 获取所属来源名称。
	 * @return 所属来源名称。
	 */
	public String getSourceName() {
		return sourceName;
	}
	/**
	 * 设置所属来源名称。
	 * @param sourceName 
	 *	  所属来源名称。
	 */
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}
	/**
	 * 获取所属地区ID。
	 * @return 所属地区ID。
	 */
	public String getAreaId() {
		return areaId;
	}
	/**
	 * 设置所属地区ID。
	 * @param areaId 
	 *	  所属地区ID。
	 */
	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}
	/**
	 * 获取所属地区名称。
	 * @return 所属地区名称。
	 */
	public String getAreaName() {
		return areaName;
	}
	/**
	 * 设置所属地区名称。
	 * @param areaName 
	 *	  所属地区名称。
	 */
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	/**
	 * 获取试卷类型。
	 * @return 试卷类型。
	 */
	public Integer getType() {
		return type;
	}
	/**
	 * 设置试卷类型。
	 * @param type 
	 *	  试卷类型。
	 */
	public void setType(Integer type) {
		this.type = type;
	}
	/**
	 * 获取试卷类型名称。
	 * @return 试卷类型名称。
	 */
	public String getTypeName() {
		return typeName;
	}
	/**
	 * 设置试卷类型名称。
	 * @param typeName 
	 *	  试卷类型名称。
	 */
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	/**
	 * 获取试卷状态。
	 * @return 试卷状态。
	 */
	public Integer getStatus() {
		return status;
	}
	/**
	 * 设置试卷状态。
	 * @param status 
	 *	  试卷状态。
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}
	/**
	 * 获取试卷状态名称。
	 * @return 试卷状态名称。
	 */
	public String getStatusName() {
		return statusName;
	}
	/**
	 * 设置试卷状态名称。
	 * @param statusName 
	 *	  试卷状态名称。
	 */
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	/**
	 * 获取试卷价格。
	 * @return 试卷价格。
	 */
	public Integer getPrice() {
		return price;
	}
	/**
	 * 设置试卷价格。
	 * @param price 
	 *	  试卷价格。
	 */
	public void setPrice(Integer price) {
		this.price = price;
	}
	/**
	 * 获取考试时长(分钟)。
	 * @return 考试时长(分钟)。
	 */
	public Integer getTime() {
		return time;
	}
	/**
	 * 设置考试时长(分钟)。
	 * @param time 
	 *	  考试时长(分钟)。
	 */
	public void setTime(Integer time) {
		this.time = time;
	}
	/**
	 * 获取使用年份。
	 * @return 使用年份。
	 */
	public Integer getYear() {
		return year;
	}
	/**
	 * 设置使用年份。
	 * @param year 
	 *	  使用年份。
	 */
	public void setYear(Integer year) {
		this.year = year;
	}
	/**
	 * 获取试卷总分。
	 * @return 试卷总分。
	 */
	public BigDecimal getScore() {
		return score;
	}
	/**
	 * 设置试卷总分。
	 * @param score 
	 *	  试卷总分。
	 */
	public void setScore(BigDecimal score) {
		this.score = score;
	}
	/**
	 * 获取创建时间。
	 * @return 创建时间。
	 */
	public String getCreateTime() {
		return createTime;
	}
	/**
	 * 设置创建时间。
	 * @param createTime 
	 *	  创建时间。
	 */
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	/**
	 * 获取最后修改时间。
	 * @return 最后修改时间。
	 */
	public String getLastTime() {
		return lastTime;
	}
	/**
	 * 设置最后修改时间。
	 * @param lastTime 
	 *	  最后修改时间。
	 */
	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
	}
	/**
	 * 获取发布时间。
	 * @return 发布时间。
	 */
	public String getPublishTime() {
		return publishTime;
	}
	/**
	 * 设置发布时间。
	 * @param publishTime 
	 *	  发布时间。
	 */
	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}
	/**
	 * 获取所属用户ID。
	 * @return 所属用户ID。
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * 设置所属用户ID。
	 * @param userId 
	 *	所属用户ID。
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	/**
	 * 获取所属用户名称。
	 * @return 所属用户名称。
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * 设置所属用户名称。
	 * @param userName 
	 *	  所属用户名称。
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
}