package com.examw.test.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.examw.test.app.AppContext;

/**
 * 用户相关数据库
 * @author fengwei.
 * @since 2014年12月1日 下午2:55:53.
 */
public class UserDBHelper extends SQLiteOpenHelper{
	//数据库名称
	public static final String DATABASENAME = "examw_user.db";
	//版本
	public static final int VERSION = 1;
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE UserTab(_ID INTEGER PRIMARY KEY AUTOINCREMENT,UID TEXT,USERNAME TEXT,PASSWORD TEXT,INFO TEXT)");
		db.execSQL("CREATE TABLE ItemRecordTab(_ID INTEGER PRIMARY KEY AUTOINCREMENT,CLASSID TEXT,CLASSNAME TEXT,CLASSPID TEXT)");
		db.execSQL("CREATE TABLE FavoriteTab(_ID INTEGER,DAYS_ID INTEGER,CLASS_ID TEXT,SUMMARY_CONTENT TEXT,CONTAINS_KID TEXT,CONTAINS_PAPERID TEXT,AREA_ID TEXT)");
		db.execSQL("CREATE TABLE PaperRecordTab(_ID INTEGER PRIMARY KEY AUTOINCREMENT,KNOWLEDGEID TEXT,KNOWLEDGETITLE TEXT,KNOWLEDGECONTENT TEXT,CHAPTERID TEXT,CLASSID TEXT,ORDERID INTEGER)");
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	public UserDBHelper(Context context, String name, CursorFactory factory,  
            int version) {  
        super(context, DATABASENAME, factory, VERSION);  
    }
	
	public UserDBHelper()
	{
		this(AppContext.getContext(), DATABASENAME, null, VERSION);
	}
}
