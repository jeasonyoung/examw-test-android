package com.examw.test.app;
/**
 * 返回标识
 * @author fengwei.
 * @since 2014年11月26日 下午2:17:39.
 */
public class AppConstant {
	public static final int SUCCESS = 1;
	public static final int FAILURE	= 0;
	
	public static final int ITEM_TYPE_SINGLE = 1; // 单选
	public static final int ITEM_TYPE_MULTI = 2; // 多选
	public static final int ITEM_TYPE_UNCERTAIN = 3; // 不定项
	public static final int ITEM_TYPE_JUDGE = 4; // 判断
	public static final int ITEM_TYPE_QANDA = 5; // 问答
	public static final int ITEM_TYPE_SHARE_TITLE = 6; // 共享题干
	public static final int ITEM_TYPE_SHARE_ANSWER = 7;// 共享答案

	public static final int PAPER_TYPE_REAL = 1;		//真题
	public static final int PAPER_TYPE_SIMU = 2;		//模拟题
	public static final int PAPER_TYPE_FORECAST = 3;	//预测题
	public static final int PAPER_TYPE_PRACTICE = 4;	//练习题
	public static final int PAPER_TYPE_CHAPTER = 5;		//章节练习
	public static final int PAPER_TYPE_DAILY = 6;		//每日一练
	
	public static final Integer STATUS_DONE = 1;
	public static final Integer STATUS_NONE = 0;
	
	public static final int ANSWER_RIGHT = 1;	//答对
	public static final int ANSWER_NONE = 0;	//没答
	public static final int ANSWER_WRONG = -1;	//答错
	public static final int ANSWER_LESS = 2;	//少选
	
	//ACTION
	public static final int ACTION_NONE = 0;
	public static final int ACTION_DO_EXAM = 1;		//考试
	public static final int ACTION_SHOW_ANSWER = 2;	//显示答案
	public static final int ACTION_DO_PRACTICE = 3;	//做练习
	public static final int ACTION_ERROR = 4;		//错题
	public static final int ACTION_FAVORITE = 5;	//收藏
	
	public static final int ACTION_CHOOSE_ITEM = 6;	//答题卡选题
	public static final int ACTION_SUBMIT = 7;		//提交答案
	public static final int ACTION_CHOOSE_ITEM_WITH_ANSWER = 8;	//带答案
	
	
	
	public static String getPaperTypeName(Integer type)
	{
		switch(type)
		{
		case PAPER_TYPE_REAL:
			return "历年真题";
		case PAPER_TYPE_SIMU:
			return "模拟试卷";
		case PAPER_TYPE_FORECAST:
			return "预测试卷";
		case PAPER_TYPE_PRACTICE:
			return "练习试卷";
		case PAPER_TYPE_CHAPTER:
			return "章节练习";
		case PAPER_TYPE_DAILY:
			return "每日一练";
		default:
			return "";
		}
	}
	public static String getItemTypeName(Integer type)
	{
		switch(type)
		{
		case ITEM_TYPE_SINGLE:
			return "单选题";
		case ITEM_TYPE_MULTI:
			return "多选题";
		case ITEM_TYPE_UNCERTAIN:
			return "不定项";
		case ITEM_TYPE_JUDGE:
			return "判断题";
		case ITEM_TYPE_QANDA:
			return "问答题";
		case ITEM_TYPE_SHARE_TITLE:
			return "材料题";
		case ITEM_TYPE_SHARE_ANSWER:
			return "配伍题";
		default:
			return "";
		}
	}
}	
