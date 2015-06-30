package com.examw.test.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 试卷结构数据模型。
 * 
 * @author jeasonyoung
 * @since 2015年6月27日
 */
public class PaperStructureModel implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id,title,description;
	private Integer type,total,orderNo;
	private Float score,min,ratio;
	private List<PaperItemModel> items;
	private List<PaperStructureModel> children;
	/**
	 * 获取试卷结构ID。
	 * @return 试卷结构ID。
	 */
	public String getId() {
		return id;
	}
	/**
	 * 设置试卷结构ID。
	 * @param id 
	 *	  试卷结构ID。
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * 获取试卷结构标题。
	 * @return 试卷结构标题。
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * 设置试卷结构标题。
	 * @param title 
	 *	  试卷结构标题。
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * 获取试卷结构描述。
	 * @return 试卷结构描述。
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * 设置试卷结构描述。
	 * @param description 
	 *	  试卷结构描述。
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * 获取题型。
	 * @return 题型。
	 */
	public Integer getType() {
		return type;
	}
	/**
	 * 设置题型。
	 * @param type 
	 *	  题型。
	 */
	public void setType(Integer type) {
		this.type = type;
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
	/**
	 * 获取排序号。
	 * @return 排序号。
	 */
	public Integer getOrderNo() {
		return orderNo;
	}
	/**
	 * 设置排序号。
	 * @param orderNo 
	 *	  排序号。
	 */
	public void setOrderNo(Integer orderNo) {
		this.orderNo = orderNo;
	}
	/**
	 * 获取每题得分。
	 * @return 每题得分。
	 */
	public Float getScore() {
		return score;
	}
	/**
	 * 设置每题得分。
	 * @param score 
	 *	  每题得分。
	 */
	public void setScore(Float score) {
		this.score = score;
	}
	/**
	 * 获取每题最少得分。
	 * @return 每题最少得分。
	 */
	public Float getMin() {
		return min;
	}
	/**
	 * 设置每题最少得分。
	 * @param min 
	 *	  每题最少得分。
	 */
	public void setMin(Float min) {
		this.min = min;
	}
	/**
	 * 获取分数比例。
	 * @return 分数比例。
	 */
	public Float getRatio() {
		return ratio;
	}
	/**
	 * 设置分数比例。
	 * @param ratio 
	 *	  分数比例。
	 */
	public void setRatio(Float ratio) {
		this.ratio = ratio;
	}
	/**
	 * 获取试题集合。
	 * @return 试题集合。
	 */
	public List<PaperItemModel> getItems() {
		return items;
	}
	/**
	 * 设置试题集合。
	 * @param items 
	 *	  试题集合。
	 */
	public void setItems(List<PaperItemModel> items) {
		this.items = items;
	}
	/**
	 * 获取子结构数组集合。
	 * @return 子结构数组集合。
	 */
	public List<PaperStructureModel> getChildren() {
		return children;
	}
	/**
	 * 设置子结构数组集合。
	 * @param children 
	 *	  子结构数组集合。
	 */
	public void setChildren(List<PaperStructureModel> children) {
		this.children = children;
	}
}