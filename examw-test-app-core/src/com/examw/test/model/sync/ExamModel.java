package com.examw.test.model.sync;

import java.util.List;

/**
 * 考试数据模型。
 * 
 * @author jeasonyoung
 * @since 2015年6月26日
 */
public class ExamModel extends ExamBaseModel {
	private static final long serialVersionUID = 1L;
	private String abbr;
	private List<ProductModel> products;
	/**
	 * 获取考试EN简称。
	 * @return 考试EN简称。
	 */
	public String getAbbr() {
		return abbr;
	}
	/**
	 * 设置考试EN简称。
	 * @param abbr 
	 *	  考试EN简称。
	 */
	public void setAbbr(String abbr) {
		this.abbr = abbr;
	}
	/**
	 * 获取产品数据集合。
	 * @return 产品数据集合。
	 */
	public List<ProductModel> getProducts() {
		return products;
	}
	/**
	 * 设置产品数据集合。
	 * @param products 
	 *	  产品数据集合。
	 */
	public void setProducts(List<ProductModel> products) {
		this.products = products;
	}
}