package com.examw.test.support;

import java.io.Serializable;

/**
 * 访问数据接口地址
 * @author fengwei.
 * @since 2014年12月1日 上午9:38:27.
 */
public class URLs implements Serializable{

	private static final long serialVersionUID = 1L;

	public static final String HOST = "http://tiku.examw.com";
	
	public static final String BASE_URL = "http://tiku.examw.com/examw-test/api/data/";
	
	//登录地址
	public static final String LOGIN = "";
	public static final String SUBJECTS = BASE_URL + "";
	public static final String BUY = "";
}
