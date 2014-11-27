package com.examw.test.model;

import java.io.Serializable;
import java.util.Date;

import org.litepal.crud.DataSupport;

/**
 * 试卷[已经发布过的试卷]
 * @author fengwei.
 * @since 2014年11月27日 下午3:56:12.
 */
public class Paper extends DataSupport implements Serializable{
	private static final long serialVersionUID = 1L;
	private String paperId,title,content;
	private Integer total;
	private Date createTime;
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
	public String getTitle() {
		return title;
	}
	/**
	 * 设置 试卷名称
	 * @param title
	 * 试卷名称
	 */
	public void setTitle(String title) {
		this.title = title;
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
	 * 获取 创建时间
	 * @return createTime
	 * 创建时间
	 */
	public Date getCreateTime() {
		return createTime;
	}
	/**
	 * 设置 创建时间
	 * @param createTime
	 * 创建时间
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
