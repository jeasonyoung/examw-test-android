package com.examw.test.model;

import java.util.Set;

/**
 * 试卷结构试题。
 * 
 * @author yangyong
 * @since 2014年9月27日
 */
public class StructureItemInfo extends BaseItemInfo<StructureItemInfo>{
	private static final long serialVersionUID = 1L;
	private String paperId, structureId;
	private Set<StructureItemInfo> children;
	/**
	 * 获取试卷ID。
	 * @return 试卷ID。
	 */
	public String getPaperId() {
		return paperId;
	}
	/**
	 * 设置试卷ID。
	 * @param paperId 
	 *	  试卷ID。
	 */
	public void setPaperId(String paperId) {
		this.paperId = paperId;
	}
	/**
	 * 获取所属试卷结构ID。
	 * @return 所属试卷结构ID。
	 */
	public String getStructureId() {
		return structureId;
	}
	/**
	 * 设置所属试卷结构ID。
	 * @param structureId 
	 *	  所属试卷结构ID。
	 */
	public void setStructureId(String structureId) {
		this.structureId = structureId;
	}
	/*
	 * 获取子试题集合。
	 * @see com.examw.test.model.library.BaseItemInfo#getChildren()
	 */
	@Override
	public Set<StructureItemInfo> getChildren() {
		return this.children;
	}
	/*
	 * 设置子试题集合。
	 * @see com.examw.test.model.library.BaseItemInfo#setChildren(java.util.Set)
	 */
	@Override
	public void setChildren(Set<StructureItemInfo> children) {
		this.children = children;
	}
}