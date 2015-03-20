package com.examw.test.support;

import java.io.Serializable;

import com.examw.test.app.AppContext;

/**
 * 访问数据接口地址
 * @author fengwei.
 * @since 2014年12月1日 上午9:38:27.
 */
public class URLs implements Serializable{

	private static final long serialVersionUID = 1L;

	public static final String HOST = "http://tiku.examw.com";
//	public static final String HOST = AppContext.getMetaInfo("host");//"http://192.168.1.246:8080";
	
	public static final String BASE_URL = HOST + "/examw-test/api/m";
	
	//登录地址
	public static final String LOGIN =  "http://test.examw.com/user/Login/CheckUser.asp";
	public static final String REGIST = LOGIN;
	//注册登录代理地址
	public static final String LOGIN_PROXY = HOST + "/examw-test/api/m/user/login";
	public static final String REGIST_PROXY = HOST + "/examw-test/api/m/user/register";
	//产品信息 
	public static final String PRODUCT_INFO = BASE_URL + "/product/"+AppContext.getMetaInfo("productId");
	//科目列表
	public static final String SUBJECT_LIST = BASE_URL + "/products/"+AppContext.getMetaInfo("productId")+"/subjects";
	public static final String EXAM_SUBJECT_SYNC = BASE_URL + "/sync/exams";
	//试卷列表
	public static final String PAPER_LIST = BASE_URL + "/products/" + AppContext.getMetaInfo("productId") + "/papers";
	public static final String PAPER_SYNC = BASE_URL + "/sync/papers";
	//试卷的更新列表
	public static final String PAPER_UPDATE_LIST = BASE_URL + "/products/" + AppContext.getMetaInfo("productId") + "/papersupdate?lastTime=";
	//每日一练试卷列表
	public static final String DAILY_PAPER_LIST = BASE_URL + "/daily/papers/" + AppContext.getMetaInfo("productId") ;
	//单个试卷内容
	public static final String SINGLE_PAPER = BASE_URL + "/papers/%1$s";
	//大纲内容
	public static final String SYLLABUS = BASE_URL + "/subjects/%s/syllabuses";
	//知识点内容
	public static final String KNOWLEDGE = BASE_URL +"/syllabus/%1$s/knowledges";
	//知识点试题
	public static final String SYLLABUS_ITEMS = BASE_URL +"/syllabus/%1$s/items";
	//验证用户
	public static final String VERIFYUSER =  BASE_URL+"/user";
	//意见反馈
	public static final String FEEDBACK =  BASE_URL+"/feedback/add";
	//上传用户考试记录
	public static final String UPLOAD_RECORDS = BASE_URL + "/user/records/add";
	public static final String PAPER_RECORD_SYNC = BASE_URL + "/sync/records/papers";
	public static final String ITEM_RECORD_SYNC = BASE_URL + "/sync/records/items";
	//上传用户的收藏
	public static final String UPLOAD_FAVORS = BASE_URL + "/user/favorites/add";
	public static final String FAVORITE_SYNC = BASE_URL + "/sync/favorites";
	
	public static final String CODE_REGISTER = BASE_URL + "/app/register";
	
	public static final String BUY = "";


	
}
