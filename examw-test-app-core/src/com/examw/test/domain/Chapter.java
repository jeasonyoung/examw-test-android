package com.examw.test.domain;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * @author fengwei.
 * @since 2014年12月16日 上午9:12:19.
 */
public class Chapter implements Serializable,Comparable<Chapter>{
	private static final long serialVersionUID = 1L;
	private String chapterId,pid,title;
	private Integer orderNo;
	private int icon,level;
	private Chapter parent;
	private ArrayList<Chapter> children = new ArrayList<Chapter>();
	/**
	 * 是否展开
	 */
	private boolean isExpand = false;
	
	public int getIcon()
	{
		return icon;
	}

	public void setIcon(int icon)
	{
		this.icon = icon;
	}

	
	/**
	 * 获取 
	 * @return chapterId
	 * 
	 */
	public String getChapterId() {
		return chapterId;
	}

	/**
	 * 设置 
	 * @param chapterId
	 * 
	 */
	public void setChapterId(String chapterId) {
		this.chapterId = chapterId;
	}

	/**
	 * 获取 
	 * @return pid
	 * 
	 */
	public String getPid() {
		return pid;
	}

	/**
	 * 设置 
	 * @param pid
	 * 
	 */
	public void setPid(String pid) {
		this.pid = pid;
	}

	/**
	 * 获取 
	 * @return title
	 * 
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * 设置 
	 * @param title
	 * 
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	public void setLevel(int level)
	{
		this.level = level;
	}

	public boolean isExpand()
	{
		return isExpand;
	}

	public ArrayList<Chapter> getChildren()
	{
		return children;
	}

	public void setChildren(ArrayList<Chapter> children)
	{
		this.children = children;
	}

	public Chapter getParent()
	{
		return parent;
	}

	public void setParent(Chapter parent)
	{
		this.parent = parent;
	}

	/**
	 * 是否为跟节点
	 * 
	 * @return
	 */
	public boolean isRoot()
	{
		return parent == null;
	}

	/**
	 * 判断父节点是否展开
	 * 
	 * @return
	 */
	public boolean isParentExpand()
	{
		if (parent == null)
			return false;
		return parent.isExpand();
	}

	/**
	 * 是否是叶子界点
	 * 
	 * @return
	 */
	public boolean isLeaf()
	{
		return children.size() == 0;
	}

	/**
	 * 获取level
	 */
	public int getLevel()
	{
		return parent == null ? level : parent.getLevel() + 1;
	}

	/**
	 * 设置展开
	 * 
	 * @param isExpand
	 */
	public void setExpand(boolean isExpand)
	{
		this.isExpand = isExpand;
		if (!isExpand)
		{

			for (Chapter node : children)
			{
				node.setExpand(isExpand);
			}
		}
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
	public Chapter() {}

	public Chapter(String chapterId, String pid, String title, Integer orderNo) {
		super();
		this.chapterId = chapterId;
		this.pid = pid;
		this.title = title;
		this.orderNo = orderNo;
	}

	@Override
	public int compareTo(Chapter o) {
		if(this == o) return 0;
		int index = this.getOrderNo() - o.getOrderNo();
		if(index == 0){
			index = this.getTitle().compareToIgnoreCase(o.getTitle());
			if(index == 0){
				index = this.getChapterId().compareToIgnoreCase(o.getChapterId());
			}
		}
		return index;
	}
	
	@Override
	public String toString() {
		return title;
	}
}
