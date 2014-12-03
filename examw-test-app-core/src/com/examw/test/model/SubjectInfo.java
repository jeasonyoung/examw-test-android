package com.examw.test.model;

import java.io.Serializable;

/**
 * 科目数据
 * @author fengwei.
 * @since 2014年12月3日 下午5:35:30.
 */
public class SubjectInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String subjectId,name;
	private Integer orderNo;
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
	 * @return name
	 * 
	 */
	public String getName() {
		return name;
	}
	/**
	 * 设置 
	 * @param name
	 * 
	 */
	public void setName(String name) {
		this.name = name;
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
	
}
