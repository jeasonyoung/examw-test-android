package com.examw.test.app;
/**
 * 应用常量。
 * @author fengwei.
 * @since 2014年11月26日 下午2:17:39.
 */
public final class AppConstant {
	/**
	 * 客户端标示(用于考试网用户登陆验证)。
	 */
	public static final String APP_ID = "357070005327186";
	/**
	 * 客户端类型代码。
	 */
	public static final int APP_TYPECODE = 4;
	/**
	 * 服务器认证用户名。
	 */
	public static final String APP_API_USERNAME = "__iap-apk_accessUser";
	/**
	 * 服务器认证密码。
	 */
	public static final String APP_API_PASSWORD = "__iap-apk_accessUser";
	/**
	 * 频道(用户登录/注册)。
	 */
	public static final String APP_API_CHANNEL = "jzs1";
	
	/**
	 * 分页行数据。
	 */
	public static final int PAGEOFROWS = 10;
	
	/**
	 * 服务器数据API。
	 */
	public static final String APP_API_HOST = "http://tiku.examw.com";
	/**
	 * 注册API。
	 */
	public static final String APP_API_REGISTER_URL = APP_API_HOST + "/examw-test/api/m/user/register";
	/**
	 * 登陆API。
	 */
	public static final String APP_API_LOGIN_URL = APP_API_HOST + "/examw-test/api/m/user/login";
	/**
	 * 注册码校验API。
	 */
	public static final String APP_API_REGCODECHECK_URL = APP_API_HOST + "/examw-test/api/m/app/register";
	/**
	 * 考试类别API。
	 */
	public static final String APP_API_CATEGORY_URL = APP_API_HOST + "/examw-test/api/m/download/categories";
	/**
	 * 科目数据API。
	 */
	public static final String APP_API_SUBJECTS_URL = APP_API_HOST + "/examw-test/api/m/sync/exams";
	/**
	 * 试卷数据API。
	 */
	//public static final String APP_API_PAPERS_URL = APP_API_HOST + "/examw-test/api/m/sync/papers";
	public static final String APP_API_PAPERS_URL = APP_API_HOST + "/examw-test/api/m/download/papers";
	/**
	 * 上传试题收藏API。
	 */
	public static final String APP_API_FAVORITES_URL = APP_API_HOST + "/examw-test/api/m/sync/favorites";
	/**
	 * 上传试卷记录API。
	 */
	public static final String APP_API_PAPERRECORDS_URL = APP_API_HOST + "/examw-test/api/m/sync/records/papers";
	/**
	 * 上传试题记录API。
	 */
	public static final String APP_API_PAPERITEMRECORDS_URL = APP_API_HOST + "/examw-test/api/m/sync/records/items";
}