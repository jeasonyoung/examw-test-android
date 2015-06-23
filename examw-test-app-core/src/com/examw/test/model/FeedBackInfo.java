package com.examw.test.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 意见反馈信息
 * @author fengwei.
 * @since 2015年1月12日 下午4:44:24.
 */
public class FeedBackInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	private String id,content,username,terminalName;
	private Date createTime;
	private Integer terminalCode;
	/**
	 * 获取 ID
	 * @return id
	 * 
	 */
	public String getId() {
		return id;
	}
	/**
	 * 设置 ID
	 * @param id
	 * 
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * 获取 反馈内容
	 * @return content
	 * 
	 */
	public String getContent() {
		return content;
	}
	/**
	 * 设置 反馈内容
	 * @param content
	 * 
	 */
	public void setContent(String content) {
		this.content = content;
	}
	/**
	 * 获取 用户名
	 * @return username
	 * 
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * 设置 用户名
	 * @param username
	 * 
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * 获取 创建时间
	 * @return createTime
	 * 
	 */
	public Date getCreateTime() {
		return createTime;
	}
	/**
	 * 设置 创建时间
	 * @param createTime
	 * 
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	/**
	 * 获取 类型
	 * @return type
	 * 
	 */
	public Integer getTerminalCode() {
		return terminalCode;
	}
	/**
	 * 设置 类型
	 * @param type
	 * 
	 */
	public void setTerminalCode(Integer terminalCode) {
		this.terminalCode = terminalCode;
	}
	/**
	 * 获取 类型名称
	 * @return terminalName
	 * 
	 */
	public String getTerminalName() {
		return terminalName;
	}
	/**
	 * 设置 类型名称
	 * @param terminalName
	 * 
	 */
	public void setTerminalName(String terminalName) {
		this.terminalName = terminalName;
	}
	
}
