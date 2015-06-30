package com.examw.test.model.sync;

import java.io.Serializable;
/**
 * 产品数据模型。
 * 
 * @author jeasonyoung
 * @since 2015年6月26日
 */
public class ProductModel implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id,name,area,content;
	private float price,discount;
	private int papers,items,order;
	/**
	 * 获取产品ID。
	 * @return 产品ID。
	 */
	public String getId() {
		return id;
	}
	/**
	 * 设置产品ID。
	 * @param id 
	 *	  产品ID。
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * 获取产品名称。
	 * @return 产品名称。
	 */
	public String getName() {
		return name;
	}
	/**
	 * 设置产品名称。
	 * @param name 
	 *	  产品名称。
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 获取所属地区。
	 * @return 所属地区。
	 */
	public String getArea() {
		return area;
	}
	/**
	 * 设置所属地区。
	 * @param area 
	 *	  所属地区。
	 */
	public void setArea(String area) {
		this.area = area;
	}
	/**
	 * 获取产品介绍。
	 * @return 产品介绍。
	 */
	public String getContent() {
		return content;
	}
	/**
	 * 设置产品介绍。
	 * @param content 
	 *	  产品介绍。
	 */
	public void setContent(String content) {
		this.content = content;
	}
	/**
	 * 获取产品原价。
	 * @return 产品原价。
	 */
	public float getPrice() {
		return price;
	}
	/**
	 * 设置产品原价。
	 * @param price 
	 *	  产品原价。
	 */
	public void setPrice(float price) {
		this.price = price;
	}
	/**
	 * 获取产品优惠价。
	 * @return 产品优惠价。
	 */
	public float getDiscount() {
		return discount;
	}
	/**
	 * 设置产品优惠价。
	 * @param discount 
	 *	  产品优惠价。
	 */
	public void setDiscount(float discount) {
		this.discount = discount;
	}
	/**
	 * 获取试卷总数。
	 * @return 试卷总数。
	 */
	public int getPapers() {
		return papers;
	}
	/**
	 * 设置试卷总数。
	 * @param papers 
	 *	  试卷总数。
	 */
	public void setPapers(int papers) {
		this.papers = papers;
	}
	/**
	 * 获取试题总数。
	 * @return 试题总数。
	 */
	public int getItems() {
		return items;
	}
	/**
	 * 设置试题总数。
	 * @param items 
	 *	  试题总数。
	 */
	public void setItems(int items) {
		this.items = items;
	}
	/**
	 * 获取排序号。
	 * @return 排序号。
	 */
	public int getOrder() {
		return order;
	}
	/**
	 * 设置排序号。
	 * @param order 
	 *	  order
	 */
	public void setOrder(int order) {
		this.order = order;
	}
}