package com.examw.test.domain;

import java.io.Serializable;

/**
 * 试卷[已经发布过的试卷]
 * @author fengwei.
 * @since 2014年11月27日 下午3:56:12.
 */
public class Paper implements Serializable {
	private static final long serialVersionUID = 1L;
	private String paperId,name,description,content,examId,subjectId,sourceName,areaName;
	private Integer type,price,time,year,total,userTotal;
	private Double score;
	private String publishTime;
	private String structures;
	/**
	 * 获取 试卷ID
	 * @return paperId
	 * 试卷ID
	 */
	public String getPaperId() {
		return paperId;
	}
	/**
	 * 设置 试卷ID
	 * @param paperId
	 * 试卷ID
	 */
	public void setPaperId(String paperId) {
		this.paperId = paperId;
	}
	/**
	 * 获取 试卷名称
	 * @return title
	 * 试卷名称
	 */
	public String getName() {
		return name;
	}
	/**
	 * 设置 试卷名称
	 * @param title
	 * 试卷名称
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 获取 试卷内容
	 * @return content
	 * 试卷内容
	 */
	public String getContent() {
		return content;
	}
	/**
	 * 设置 试卷内容
	 * @param content
	 * 试卷内容
	 */
	public void setContent(String content) {
		this.content = content;
	}
	/**
	 * 获取 试题总数
	 * @return total
	 * 试题总数
	 */
	public Integer getTotal() {
		return total;
	}
	/**
	 * 设置 试题总数
	 * @param total
	 * 试题总数
	 */
	public void setTotal(Integer total) {
		this.total = total;
	}
	/**
	 * 获取 试卷描述
	 * @return description
	 * 试卷描述
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * 设置 试卷描述
	 * @param description
	 * 试卷描述
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * 获取 考试ID
	 * @return examId
	 * 考试ID
	 */
	public String getExamId() {
		return examId;
	}
	/**
	 * 设置 考试ID
	 * @param examId
	 * 考试ID
	 */
	public void setExamId(String examId) {
		this.examId = examId;
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
	 * 获取 来源名称
	 * @return sourceName
	 * 来源名称
	 */
	public String getSourceName() {
		return sourceName;
	}
	/**
	 * 设置 来源名称
	 * @param sourceName
	 * 来源名称
	 */
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}
	/**
	 * 获取 地区名称
	 * @return areaName
	 * 地区名称
	 */
	public String getAreaName() {
		return areaName;
	}
	/**
	 * 设置 地区名称
	 * @param areaName
	 * 地区名称
	 */
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	/**
	 * 获取 试卷类型
	 * @return type
	 * 试卷类型
	 */
	public Integer getType() {
		return type;
	}
	/**
	 * 设置 试卷类型
	 * @param type
	 * 试卷类型
	 */
	public void setType(Integer type) {
		this.type = type;
	}
	/**
	 * 获取 试卷价格
	 * @return price
	 * 试卷价格
	 */
	public Integer getPrice() {
		return price;
	}
	/**
	 * 设置 试卷价格
	 * @param price
	 * 试卷价格
	 */
	public void setPrice(Integer price) {
		this.price = price;
	}
	/**
	 * 获取 考试时间
	 * @return time
	 * 考试时间
	 */
	public Integer getTime() {
		return time;
	}
	/**
	 * 设置 考试时间
	 * @param time
	 * 考试时间
	 */
	public void setTime(Integer time) {
		this.time = time;
	}
	/**
	 * 获取 年份
	 * @return year
	 * 年份
	 */
	public Integer getYear() {
		return year;
	}
	/**
	 * 设置 年份
	 * @param year
	 * 年份
	 */
	public void setYear(Integer year) {
		this.year = year;
	}
	/**
	 * 获取 试卷分数
	 * @return score
	 * 试卷分数
	 */
	public Double getScore() {
		return score;
	}
	/**
	 * 设置 试卷分数
	 * @param score
	 * 试卷分数
	 */
	public void setScore(Double score) {
		this.score = score;
	}
	/**
	 * 获取 发布时间
	 * @return publishTime
	 * 发布时间
	 */
	public String getPublishTime() {
		return publishTime;
	}
	/**
	 * 设置 发布时间
	 * @param publishTime
	 * 发布时间
	 */
	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}
	/**
	 * 获取 参考人数
	 * @return userTotal
	 * 参考人数
	 */
	public Integer getUserTotal() {
		return userTotal;
	}
	
	/**
	 * 获取 结构json
	 * @return structures
	 * 结构json
	 */
	public String getStructures() {
		return structures;
	}
	/**
	 * 设置 结构json
	 * @param structures
	 * 结构json
	 */
	public void setStructures(String structures) {
		this.structures = structures;
	}
	/**
	 * 设置 参考人数
	 * @param userTotal
	 * 参考人数
	 */
	public void setUserTotal(Integer userTotal) {
		this.userTotal = userTotal;
	}
	
	public Paper() {}
	
	public Paper(String paperId,String name,Double score,Integer type,Integer time,Integer year,Integer price,Integer total,Integer userTotal,String publishTime){
		//paperid,name,score,time,year,price,total
		this.paperId = paperId;
		this.name = name;
		this.score = score;
		this.type = type;
		this.time = time;
		this.year = year;
		this.price = price;
		this.total = total;
		this.userTotal = userTotal;
		this.publishTime = publishTime;
	}
	
}
