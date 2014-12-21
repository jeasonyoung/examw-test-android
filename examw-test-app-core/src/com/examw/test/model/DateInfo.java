package com.examw.test.model;
/**
 * 
 * @author fengwei.
 * @since 2014年12月21日 下午2:22:52.
 */
public class DateInfo {
	private int orderNo;
	private String eee,mmdd;
	/**
	 * 获取 
	 * @return orderNo
	 * 
	 */
	public int getOrderNo() {
		return orderNo;
	}
	/**
	 * 设置 
	 * @param orderNo
	 * 
	 */
	public void setOrderNo(int orderNo) {
		this.orderNo = orderNo;
	}
	/**
	 * 获取 
	 * @return eee
	 * 
	 */
	public String getEee() {
		return eee;
	}
	/**
	 * 设置 
	 * @param eee
	 * 
	 */
	public void setEee(String eee) {
		this.eee = eee;
	}
	/**
	 * 获取 
	 * @return mmdd
	 * 
	 */
	public String getMmdd() {
		return mmdd;
	}
	/**
	 * 设置 
	 * @param mmdd
	 * 
	 */
	public void setMmdd(String mmdd) {
		this.mmdd = mmdd;
	}
	public DateInfo(int orderNo, String eee, String mmdd) {
		super();
		this.orderNo = orderNo;
		this.eee = eee;
		this.mmdd = mmdd;
	}
	
}
