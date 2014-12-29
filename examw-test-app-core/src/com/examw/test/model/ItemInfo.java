package com.examw.test.model;

import java.util.Set;

/**
 * 题目信息。
 * @author yangyong
 * @since 2014年8月7日
 */
public class ItemInfo extends BaseItemInfo<ItemInfo> {
	private static final long serialVersionUID = 1L;
	private String userId,userName;
	private Set<ItemInfo> children;
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
	 *	  所属用户ID。
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
	/*
	 * 获取子题目集合。
	 * @see com.examw.test.model.library.BaseItemInfo#getChildren()
	 */
	@Override
	public Set<ItemInfo> getChildren() {
		return children;
	}
	/*
	 * 设置子题目集合。
	 * @see com.examw.test.model.library.BaseItemInfo#setChildren(java.util.Set)
	 */
	@Override
	public void setChildren(Set<ItemInfo> children) {
		this.children = children;
	}
	
	// 大纲要点 Add by FW [2014.11.08]
	private Set<SyllabusInfo> syllabuses;	//大纲要点
	/**
	 * 获取 大纲要点集合
	 * @return syllabuses
	 * 大纲要点集合
	 */
	public Set<SyllabusInfo> getSyllabuses() {
		return syllabuses;
	}
	/**
	 * 设置 大纲要点集合
	 * @param syllabuses
	 * 大纲要点集合
	 */
	public void setSyllabuses(Set<SyllabusInfo> syllabuses) {
		this.syllabuses = syllabuses;
	}
	
	//是否关联大纲 Add by FW 2014.11.19
	private boolean hasSyllabus;
	/**
	 * 获取 是否关联大纲
	 * @return hasSyllabus
	 * 是否关联大纲
	 */
	public boolean isHasSyllabus() {
		return hasSyllabus;
	}
	/**
	 * 设置 是否关联大纲
	 * @param hasSyllabus
	 * 是否关联大纲
	 */
	public void setHasSyllabus(boolean hasSyllabus) {
		this.hasSyllabus = hasSyllabus;
	}
	
}