package com.examw.test.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author fengwei.
 * @since 2014年12月21日 下午2:22:52.
 */
public class DateInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	private int orderNo;
	private String eee,mmdd;
	private Date date;
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
	 * 获取 
	 * @return date
	 * 
	 */
	public Date getDate() {
		return date;
	}
	/**
	 * 设置 
	 * @param date
	 * 
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	/**
	 * 设置 
	 * @param mmdd
	 * 
	 */
	public void setMmdd(String mmdd) {
		this.mmdd = mmdd;
	}
	public DateInfo(int orderNo, String eee, String mmdd,Date date) {
		super();
		this.orderNo = orderNo;
		this.eee = eee;
		this.mmdd = mmdd;
		this.date = date;
	}
	
}
