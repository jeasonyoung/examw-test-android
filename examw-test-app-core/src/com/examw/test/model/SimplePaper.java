package com.examw.test.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 简单试卷
 * @author fengwei.
 * @since 2014年12月13日 上午9:56:55.
 */
public class SimplePaper implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer total;
	private ArrayList<StructureInfo> ruleList;
	private ArrayList<StructureItemInfo> items;
	/**
	 * 获取 总题数
	 * @return total
	 * 总题数
	 */
	public Integer getTotal() {
		return total;
	}
	/**
	 * 设置 总题数
	 * @param total
	 * 总题数
	 */
	public void setTotal(Integer total) {
		this.total = total;
	}
	/**
	 * 获取 大题集合[按题型分]
	 * @return ruleList
	 * 大题集合[按题型分]
	 */
	public ArrayList<StructureInfo> getRuleList() {
		return ruleList;
	}
	/**
	 * 设置 大题集合[按题型分]
	 * @param ruleList
	 * 大题集合[按题型分]
	 */
	public void setRuleList(ArrayList<StructureInfo> ruleList) {
		this.ruleList = ruleList;
	}
	/**
	 * 获取 题目集合
	 * @return items
	 * 题目集合
	 */
	public ArrayList<StructureItemInfo> getItems() {
		return items;
	}
	/**
	 * 设置 题目集合
	 * @param items
	 * 题目集合
	 */
	public void setItems(ArrayList<StructureItemInfo> items) {
		this.items = items;
	}
	
}
