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
	//产品信息 
	public static final String PRODUCT_INFO = BASE_URL + "/product/"+AppConfig.PRODUCTID;
	//科目列表
	public static final String SUBJECT_LIST = BASE_URL + "/products/"+AppConfig.PRODUCTID+"/subjects";
	//试卷列表
	public static final String PAPER_LIST = BASE_URL + "/products/" + AppConfig.PRODUCTID + "/papers";
	//每日一练试卷列表
	public static final String DAILY_PAPER_LIST = BASE_URL + "/daily/papers/" + AppConfig.PRODUCTID ;
	//单个试卷内容
	public static final String SINGLE_PAPER = BASE_URL + "/papers/%1$s";
	//大纲内容
	public static final String SYLLABUS = BASE_URL + "/subjects/%s/syllabuses";
	//知识点内容
	public static final String KNOWLEDGE = BASE_URL +"/syllabus/%1$s/knowledges";
	
	public static final String BUY = "";

	
}
