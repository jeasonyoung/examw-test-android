package com.examw.test.db;

import java.io.File;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.examw.test.app.AppConfig;
import com.examw.test.app.AppContext;

/**
 * 每个用户一个数据库
 * @author fengwei.
 * @since 2014年12月1日 下午2:55:45.
 */
public class UserDBManager {
	private static SQLiteDatabase database; 
    private static String dirPath = AppConfig.DEFAULT_DATA_PATH + AppContext.getContext().getPackageName() + File.separator +"databases" + File.separator;
    private static String dbName;
    private static final int mNewVersion = 1;
  
    public UserDBManager() { 
        if(!new File(dirPath).exists())
        {
        	new File(dirPath).mkdirs();
        } 
    } 
  
    public synchronized static SQLiteDatabase openDatabase(String username) {
    	dbName = "examw_"+username+".db";
        database = mOpenDatabase(dirPath+dbName); 
        return database;
    } 
    private static SQLiteDatabase mOpenDatabase(String dbPath) { 
    	if(!new File(dbPath).exists())
    	{
    		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbPath, 
                    null); 
    		//插表
    		db.setVersion(mNewVersion);
    		db.execSQL("CREATE TABLE UserTab(_id integer primary key autoincrement,uid text,userId text,username text,password text,info text,lastSyncTime datetime)");
    		db.execSQL("CREATE TABLE PaperRecordTab(_id integer primary key autoincrement,recordId text,paperId text,paperName text,paperType integer,userId text,username text,score double,torf text,lastTime datetime default(datetime('now','localtime')),createTime datetime default(datetime('now','localtime')),useTime integer,status integer,terminalId text,productId text,rightNum integer)");
    		db.execSQL("CREATE TABLE ItemRecordTab(_id integer primary key autoincrement,recordId text,structureId text,subjectId text,username text,itemId text,itemType integer,itemContent text,answer text,termialId text,status integer,score double,useTime integer,createTime date default(datetime('now','localtime')),lastTime date default(datetime('now','localtime')))");
    		db.execSQL("CREATE TABLE FavoriteTab(_id integer primary key autoincrement,userId text,username text,userAnswer text,itemId text,itemType integer,status integer,itemContent text,subjectId text,terminalId text,remarks text,createTime date default(datetime('now','localtime')))");
    		db.setVersion(mNewVersion);
            return db; 
    	}
    	SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbPath,null); 
    	final int version = db.getVersion();
		if (version != mNewVersion) {
            if (db.isReadOnly()) {
                throw new SQLiteException("Can't upgrade read-only database from version " +
                        db.getVersion() + " to " + mNewVersion + ": " + dbName);
            }

            db.beginTransaction();
            try {
                if (version == 0) {
                } else {
                    if (version > mNewVersion) {
                    	 throw new SQLiteException("Can't downgrade database from version " +
                    			 version + " to " + mNewVersion);
                    } else {
                        onUpgrade(db, version, mNewVersion);
                    }
                }
                db.setVersion(mNewVersion);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
        return db; 
    }
    /**
     * 数据库升级
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    private static void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion)
    {
    	switch (oldVersion) {  
        case 1:  
           break;
        default:
    	}
    }
    /**
     * 关闭数据库
     */
    public synchronized void closeDatabase() {
    	if(database != null)
    		database.close(); 
    } 
}
