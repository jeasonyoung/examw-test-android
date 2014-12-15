package com.examw.test.domain;

import java.io.Serializable;

/**
 * 大纲
 * @author fengwei.
 * @since 2014年12月15日 下午4:06:42.
 */
public class Syllabus implements Serializable{
	private static final long serialVersionUID = 1L;
	private String syllabusId,subjectId,title,content;
	private Integer year,orderNo;
	/**
	 * 获取 
	 * @return syllabusId
	 * 
	 */
	public String getSyllabusId() {
		return syllabusId;
	}
	/**
	 * 设置 
	 * @param syllabusId
	 * 
	 */
	public void setSyllabusId(String syllabusId) {
		this.syllabusId = syllabusId;
	}
	/**
	 * 获取 
	 * @return subjectId
	 * 
	 */
	public String getSubjectId() {
		return subjectId;
	}
	/**
	 * 设置 
	 * @param subjectId
	 * 
	 */
	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}
	/**
	 * 获取 
	 * @return title
	 * 
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * 设置 
	 * @param title
	 * 
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * 获取 
	 * @return content
	 * 
	 */
	public String getContent() {
		return content;
	}
	/**
	 * 设置 
	 * @param content
	 * 
	 */
	public void setContent(String content) {
		this.content = content;
	}
	/**
	 * 获取 
	 * @return year
	 * 
	 */
	public Integer getYear() {
		return year;
	}
	/**
	 * 设置 
	 * @param year
	 * 
	 */
	public void setYear(Integer year) {
		this.year = year;
	}
	/**
	 * 获取 
	 * @return orderNo
	 * 
	 */
	public Integer getOrderNo() {
		return orderNo;
	}
	/**
	 * 设置 
	 * @param orderNo
	 * 
	 */
	public void setOrderNo(Integer orderNo) {
		this.orderNo = orderNo;
	}
	
	public Syllabus() {
	}
	public Syllabus(String syllabusId, String subjectId, String title,
			String content, Integer year, Integer orderNo) {
		super();
		this.syllabusId = syllabusId;
		this.subjectId = subjectId;
		this.title = title;
		this.content = content;
		this.year = year;
		this.orderNo = orderNo;
	}
	
}
