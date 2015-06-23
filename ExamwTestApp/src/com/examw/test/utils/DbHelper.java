package com.examw.test.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite操作工具类。
 * 
 * @author jeasonyoung
 * @since 2015年6月20日
 */
public final class DbHelper extends SQLiteOpenHelper{
	private static final String DATABASENAME = "";
	private static final int VERSION = 1;
	/**
	 * 构造函数。
	 * @param context
	 * 上下文。
	 */
	public DbHelper(Context context){
		super(context, DATABASENAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
