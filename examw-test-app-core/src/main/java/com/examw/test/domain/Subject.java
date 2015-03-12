package com.examw.test.domain;

import java.io.Serializable;

/**
 * 科目
 * @author fengwei.
 * @since 2014年12月4日 上午11:48:20.
 */
public class Subject implements Serializable {
	private static final long serialVersionUID = 1L;
	private String subjectId, name,abbr;
	private Integer orderNo;
	private Integer total;
	private Integer status;

	/**
	 * 获取
	 * 
	 * @return subjectId
	 * 
	 */
	public String getSubjectId() {
		return subjectId;
	}

	/**
	 * 设置
	 * 
	 * @param subjectId
	 * 
	 */
	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	/**
	 * 获取
	 * 
	 * @return name
	 * 
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置
	 * 
	 * @param name
	 * 
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取
	 * 
	 * @return orderNo
	 * 
	 */
	public Integer getOrderNo() {
		return orderNo;
	}

	/**
	 * 设置
	 * 
	 * @param orderNo
	 * 
	 */
	public void setOrderNo(Integer orderNo) {
		this.orderNo = orderNo;
	}

	/**
	 * 获取
	 * 
	 * @return total
	 * 
	 */
	public Integer getTotal() {
		return total;
	}

	/**
	 * 设置
	 * 
	 * @param total
	 * 
	 */
	public void setTotal(Integer total) {
		this.total = total;
	}

	public Subject() {
	}

	public Subject(String subjectId, String name, Integer orderNo) {
		this.subjectId = subjectId;
		this.name = name;
		this.orderNo = orderNo;
	}
	
	@Override
	public String toString() {
		return this.name;
	}

	/**
	 * 获取 
	 * @return abbr
	 * 
	 */
	public String getAbbr() {
		return abbr;
	}

	/**
	 * 设置 
	 * @param abbr
	 * 
	 */
	public void setAbbr(String abbr) {
		this.abbr = abbr;
	}

	/**
	 * 获取 
	 * @return status
	 * 
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * 设置 
	 * @param status
	 * 
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}
}
