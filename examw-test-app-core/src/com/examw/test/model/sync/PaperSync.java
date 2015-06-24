package com.examw.test.model.sync;

import java.io.Serializable;

/**
 * 试卷同步数据。
 * 
 * @author yangyong
 * @since 2015年2月28日
 */
public class PaperSync implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id,title,content,subjectCode;
	private Integer type,total;
	private String createTime;
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
	 * 获取试卷标题。
	 * @return 试卷标题。
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * 设置试卷标题。
	 * @param title 
	 *	  试卷标题。
	 */
	public void setTitle(String title) {
		this.title = title;
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
	 * 获取试卷内容。
	 * @return 试卷内容。
	 */
	public String getContent() {
		return content;
	}
	/**
	 * 设置试卷内容。
	 * @param content 
	 *	  试卷内容。
	 */
	public void setContent(String content) {
		this.content = content;
	}
	/**
	 * 获取所属科目代码。
	 * @return 所属科目代码。
	 */
	public String getSubjectCode() {
		return subjectCode;
	}
	/**
	 * 设置所属科目代码。
	 * @param subjectCode 
	 *	  所属科目代码。
	 */
	public void setSubjectCode(String subjectCode) {
		this.subjectCode = subjectCode;
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
	 * 获取试题总数。
	 * @return 试题总数。
	 */
	public Integer getTotal() {
		return total;
	}
	/**
	 * 设置试题总数。
	 * @param total 
	 *	  试题总数。
	 */
	public void setTotal(Integer total) {
		this.total = total;
	}
	
	public PaperSync() {
		
	}
	
	public PaperSync(String id, String title, Integer type, Integer total, String content, String subjectCode, String createTime) {
		super();
		this.id = id;
		this.title = title;
		this.type = type;
		this.total = total;
		this.content = content;
		this.subjectCode = subjectCode;
		this.createTime = createTime;
	}
	
}