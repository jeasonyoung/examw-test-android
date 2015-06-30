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
	public static final String APP_API_USERNAME = "admin";
	/**
	 * 服务器认证密码。
	 */
	public static final String APP_API_PASSWORD = "123456";
	/**
	 * 频道(用户登录/注册)。
	 */
	public static final String APP_API_CHANNEL = "jzs1";
	
	
	/**
	 * 服务器数据API。
	 */
	public static final String APP_API_HOST = "http://127.0.0.1";
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
	public static final String APP_API_PAPERS_URL = APP_API_HOST + "/examw-test/api/m/sync/papers";
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
	
//	public static final int SUCCESS = 1;
//	public static final int FAILURE	= 0;
//	
//	public static final int ITEM_TYPE_SINGLE = 1; // 单选
//	public static final int ITEM_TYPE_MULTI = 2; // 多选
//	public static final int ITEM_TYPE_UNCERTAIN = 3; // 不定项
//	public static final int ITEM_TYPE_JUDGE = 4; // 判断
//	public static final int ITEM_TYPE_QANDA = 5; // 问答
//	public static final int ITEM_TYPE_SHARE_TITLE = 6; // 共享题干
//	public static final int ITEM_TYPE_SHARE_ANSWER = 7;// 共享答案
//
//	public static final int PAPER_TYPE_REAL = 1;		//真题
//	public static final int PAPER_TYPE_SIMU = 2;		//模拟题
//	public static final int PAPER_TYPE_FORECAST = 3;	//预测题
//	public static final int PAPER_TYPE_PRACTICE = 4;	//练习题
//	public static final int PAPER_TYPE_CHAPTER = 5;		//章节练习
//	public static final int PAPER_TYPE_DAILY = 6;		//每日一练
//	
//	public static final Integer STATUS_DONE = 1;
//	public static final Integer STATUS_NONE = 0;
//	
//	public static final int ANSWER_RIGHT = 1;	//答对
//	public static final int ANSWER_NONE = 0;	//没答
//	public static final int ANSWER_WRONG = -1;	//答错
//	public static final int ANSWER_LESS = 2;	//少选
//	
//	//ACTION
//	public static final int ACTION_NONE = 0;
//	public static final int ACTION_DO_EXAM = 1;		//考试
//	public static final int ACTION_SHOW_ANSWER = 2;	//显示答案
//	public static final int ACTION_DO_PRACTICE = 3;	//做练习
//	public static final int ACTION_ERROR = 4;		//错题
//	public static final int ACTION_FAVORITE = 5;	//收藏
//	
//	public static final int ACTION_CHOOSE_ITEM = 6;	//答题卡选题
//	public static final int ACTION_SUBMIT = 7;		//提交答案
//	public static final int ACTION_CHOOSE_ITEM_WITH_ANSWER = 8;	//带答案
//	public static final int ACTION_CHAPTER = 9;	//章节练习
//	
//	public static final int SYNC_NONE = 0;
//	public static final int SYNC_DONE = 1;
//	
//	public static String getPaperTypeName(Integer type)
//	{
//		switch(type)
//		{
//		case PAPER_TYPE_REAL:
//			return "历年真题";
//		case PAPER_TYPE_SIMU:
//			return "模拟试卷";
//		case PAPER_TYPE_FORECAST:
//			return "预测试卷";
//		case PAPER_TYPE_PRACTICE:
//			return "练习试卷";
//		case PAPER_TYPE_CHAPTER:
//			return "章节练习";
//		case PAPER_TYPE_DAILY:
//			return "每日一练";
//		default:
//			return "";
//		}
//	}
//	public static String getItemTypeName(Integer type)
//	{
//		switch(type)
//		{
//		case ITEM_TYPE_SINGLE:
//			return "单选题";
//		case ITEM_TYPE_MULTI:
//			return "多选题";
//		case ITEM_TYPE_UNCERTAIN:
//			return "不定项";
//		case ITEM_TYPE_JUDGE:
//			return "判断题";
//		case ITEM_TYPE_QANDA:
//			return "问答题";
//		case ITEM_TYPE_SHARE_TITLE:
//			return "材料题";
//		case ITEM_TYPE_SHARE_ANSWER:
//			return "配伍题";
//		default:
//			return "";
//		}
//	}
}