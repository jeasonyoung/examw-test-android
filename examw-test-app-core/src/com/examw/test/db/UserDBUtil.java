package com.examw.test.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * 用户数据库工具类
 * @author fengwei.
 * @since 2014年12月1日 下午3:37:21.
 */
public class UserDBUtil {
	private static UserDBHelper mUserDBHelper;
	public static int openedNum = 0;
	
	/**
	 * 获取可读写的数据库
	 * @return
	 */
	public synchronized static SQLiteDatabase getWritableDatabase() {
		UserDBHelper dbHelper = buildConnection();
		return dbHelper.getWritableDatabase();
	}
	/**
	 * 获取可读数据库
	 * @return
	 */
	public synchronized static SQLiteDatabase getReadableDatabase() {
		UserDBHelper dbHelper = buildConnection();
		return dbHelper.getReadableDatabase();
	}

	public static SQLiteDatabase getDatabase() {
		return getWritableDatabase();
	}

	private static UserDBHelper buildConnection() {
		if (mUserDBHelper == null) {
			mUserDBHelper = new UserDBHelper();
		}
		return mUserDBHelper;
	}
	
	/**
	 * 关闭数据库
	 */
	public static void close()
	{
		if(mUserDBHelper != null)
			mUserDBHelper.close();
	}
}
