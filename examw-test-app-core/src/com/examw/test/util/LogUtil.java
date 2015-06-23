package com.examw.test.util;

import android.util.Log;

/**
 * 自定义日志工具类
 * @author fengwei.
 * @since 2015年1月10日 下午3:30:22.
 */
public class LogUtil {
	public static boolean showLog = false;
	public static final String TAG = "LogUtil";
	
	/**
	 * 打印debug信息
	 * @param msg
	 */
	public static void d(Object msg)
	{
		if(showLog){
			Log.d(TAG,String.valueOf(msg));
		}
	}
	/**
	 * 打印info信息
	 * @param msg
	 */
	public static void i(Object msg)
	{
		if(showLog){
			Log.d(TAG,String.valueOf(msg));
		}
	}
	/**
	 * 打印error信息
	 * @param msg
	 */
	public static void e(Object msg)
	{
		if(showLog){
			Log.e(TAG,String.valueOf(msg));
		}
	}
}
