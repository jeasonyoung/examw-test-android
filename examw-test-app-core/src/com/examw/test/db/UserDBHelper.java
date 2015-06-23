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
	public static final String DATABASENAME = "user.db";
	//版本
	public static final int VERSION = 1;
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE UserTab(_id integer primary key autoincrement,uid text,userId text,username text,password text,info text,productUserId text,lastSyncPaperTime datetime,lastSyncFavorTime datetime,lastUpdateTime datetime)");
//		db.execSQL("CREATE TABLE PaperRecordTab(_id integer primary key autoincrement,recordId text,paperId text,paperName text,paperType integer,userId text,username text,score double,torf text,lastTime datetime default(datetime('now','localtime')),createTime datetime default(datetime('now','localtime')),useTime integer,status integer,terminalId text,productId text,rightNum integer)");
//		db.execSQL("CREATE TABLE ItemRecordTab(_id integer primary key autoincrement,recordId text,structureId text,subjectId text,username text,itemId text,itemType integer,itemContent text,answer text,termialId text,status integer,score double,useTime integer,createTime date default(datetime('now','localtime')),lastTime date default(datetime('now','localtime')))");
//		db.execSQL("CREATE TABLE FavoriteTab(_id integer primary key autoincrement,userId text,username text,userAnswer text,itemId text,itemType integer,status integer,itemContent text,subjectId text,terminalId text,remarks text,createTime date default(datetime('now','localtime')))");
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
