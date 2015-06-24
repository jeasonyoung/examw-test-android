package com.examw.test.db;

import java.io.File;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.examw.test.app.AppConfig;
import com.examw.test.app.AppContext;
import com.examw.test.util.FileUtils;

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
    	if(username == null || "".equals(username) || "null".equalsIgnoreCase(username))
    	{
    		throw new RuntimeException("用户名为空");
    	}
    	dbName = "examw_"+username+".db";
        database = mOpenDatabase(dirPath+dbName); 
        return database;
    } 
    private static SQLiteDatabase mOpenDatabase(String dbPath) { 
    	if(!new File(dbPath).exists())
    	{
    		//复制数据库文件
    		String library = AppConfig.DEFAULT_DATA_PATH + AppContext.getContext().getPackageName() + File.separator +"databases" + File.separator + AppConfig.DATABASE_NAME;
    		FileUtils.copyFile(library, dbPath);
    		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbPath, null); 
    		//插表
    		db.execSQL("CREATE TABLE PaperRecordTab(id integer primary key autoincrement,recordId text,paperId text,paperName text,paperType integer,score double,torf text,lastTime datetime default(datetime('now','localtime')),createTime datetime default(datetime('now','localtime')),useTime integer,status integer,rightNum integer,sync integer)");
    		db.execSQL("CREATE TABLE ItemRecordTab(id text primary key,recordId text,structureId text,subjectId text,itemId text,itemType integer,itemContent text,answer text,status integer,score double,useTime integer,createTime date default(datetime('now','localtime')),lastTime date default(datetime('now','localtime')),sync integer)");
    		db.execSQL("CREATE TABLE FavoriteTab(id text primary key,userAnswer text,itemId text,itemType integer,status integer,itemContent text,subjectId text,remarks text,createTime date default(datetime('now','localtime')),sync integer)");
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
