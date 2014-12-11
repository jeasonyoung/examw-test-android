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
}	
