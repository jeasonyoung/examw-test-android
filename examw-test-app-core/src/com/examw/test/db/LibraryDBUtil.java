package com.examw.test.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * 题库数据库工具类
 * @author fengwei.
 * @since 2014年12月2日 下午4:50:40.
 */
public class LibraryDBUtil {
private static LibraryDBHelper mLibraryDBHelper;
	
	/**
	 * 获取可读写的数据库
	 * @return
	 */
	public synchronized static SQLiteDatabase getWritableDatabase() {
		LibraryDBHelper dbHelper = buildConnection();
		return dbHelper.getWritableDatabase();
	}
	/**
	 * 获取可读数据库
	 * @return
	 */
	public synchronized static SQLiteDatabase getReadableDatabase() {
		LibraryDBHelper dbHelper = buildConnection();
		return dbHelper.getReadableDatabase();
	}

	public static SQLiteDatabase getDatabase() {
		return getWritableDatabase();
	}

	private static LibraryDBHelper buildConnection() {
		if (mLibraryDBHelper == null) {
			mLibraryDBHelper = new LibraryDBHelper();
		}
		return mLibraryDBHelper;
	}
	
	/**
	 * 关闭数据库
	 */
	public static void close()
	{
		if(mLibraryDBHelper != null)
			mLibraryDBHelper.close();
	}
}
