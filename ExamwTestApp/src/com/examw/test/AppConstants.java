package com.examw.test;

/**
 * 全局常量配置
 * 
 * @author jeasonyoung
 * @since 2015年6月20日
 */
public final class AppConstants {
	/**
	 * 当前应用版本。
	 */
	public static final float VER = 1.0f;
	/**
	 * 客户端标示(用于考试网用户登录验证)。
	 */
	public static final String ID = "357070005327186";
	/**
	 * 客户端名称。
	 */
	public static final String NAME = "中华考试网题库客户端 iOS v1.0";
	/**
	 * 客户端类型代码。
	 */
	public static final Integer TYPE_CODE = 7;
	/**
	 * 服务器认证用户名。
	 */
	public static final String API_USERNAME = "admin";
	/**
	 * 服务器认证密码。
	 */
	public static final String API_PASSWORD = "123456";
	/**
	 * 频道(用于用户登录/注册)。
	 */
	public static final String API_CHANNEL = "jzs1";
	/**
	 * 服务器数据API。
	 */
	public static final String API_HOST = "http://127.0.0.1";
	/**
	 * 注册API。
	 */
	public static final String API_REGISTER_URL = API_HOST + "/examw-test/api/m/user/register";
	/**
	 * 登录API。
	 */
	public static final String API_LOGIN_URL = API_HOST + "/examw-test/api/m/user/login";
	/**
	 * 注册码校验API。
	 */
	public static final String API_REGCODECHECK_URL = API_HOST + "/examw-test/api/m/app/register";
	/**
	 * 考试类别API。
	 */
	public static final String API_CATEGORY_URL = API_HOST + "/examw-test/api/m/download/categories";
	/**
	 * 科目数据API。
	 */
	public static final String API_SUBJECTS_URL = API_HOST + "/examw-test/api/m/sync/exams";
	/**
	 * 试卷数据API。
	 */
	public static final String API_PAPERS_URL = API_HOST + "/examw-test/api/m/sync/papers";
}