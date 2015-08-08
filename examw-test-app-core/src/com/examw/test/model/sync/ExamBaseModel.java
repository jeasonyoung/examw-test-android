package com.examw.test.model.sync;

import java.io.Serializable;
/**
 * 考试数据模型基类。
 * 
 * @author jeasonyoung
 * @since 2015年6月26日
 */
public abstract class ExamBaseModel implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id,code,name;
	/**
	 * 获取考试ID。
	 * @return 考试ID。
	 */
	public String getId() {
		return id;
	}
	/**
	 * 设置考试ID。
	 * @param id 
	 *	  考试ID。
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * 获取考试代码。
	 * @return 考试代码。
	 */
	public String getCode() {
		return code;
	}
	/**
	 * 设置考试代码。
	 * @param code 
	 *	  考试代码。
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * 获取考试名称。
	 * @return 考试名称。
	 */
	public String getName() {
		return name;
	}
	/**
	 * 设置考试名称。
	 * @param name 
	 *	  考试名称。
	 */
	public void setName(String name) {
		this.name = name;
	}
}