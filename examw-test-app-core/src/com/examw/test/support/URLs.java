package com.examw.test.support;

import java.io.Serializable;

import com.examw.test.app.AppConfig;

/**
 * 访问数据接口地址
 * @author fengwei.
 * @since 2014年12月1日 上午9:38:27.
 */
public class URLs implements Serializable{

	private static final long serialVersionUID = 1L;

	public static final String HOST = "http://tiku.examw.com";
	
	public static final String BASE_URL = "http://tiku.examw.com/examw-test/api/data";
	
	//登录地址
	public static final String LOGIN = "";
	public static final String PRODUCT_INFO = BASE_URL + "/product/"+AppConfig.PRODUCTID;
	public static final String SUBJECT_LIST = BASE_URL + "/products/"+AppConfig.PRODUCTID+"/subjects";
	public static final String PAPER_LIST = BASE_URL + "/products/" + AppConfig.PRODUCTID + "/papers";
	public static final String SINGLE_PAPER = BASE_URL + "/papers/%1$s"; 
	public static final String BUY = "";
}
