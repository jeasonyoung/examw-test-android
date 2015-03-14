package com.examw.test.db;
/**
 * 
 * @author fengwei.
 * @since 2014年12月28日 下午2:34:36.
 */
import java.io.File;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

public class NewImportDBManager {
	private SQLiteDatabase database; 
    private String dirPath = Environment.getExternalStorageDirectory().getPath()+File.separator+"examw"+File.separator+
    		"database"+File.separator;
    private String dbName;
    private final int version = 1;
  
    public NewImportDBManager() { 
        if(!new File(dirPath).exists())
        {
        	new File(dirPath).mkdirs();
        } 
        dbName = "examw_library.db";
    } 
  
    public synchronized SQLiteDatabase openDatabase() { 
        this.database = this.openDatabase(dirPath+dbName); 
        return this.database;
    } 
    private SQLiteDatabase openDatabase(String dbPath) { 
    	if(!new File(dbPath).exists())
    	{
    		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbPath, 
                    null); 
    		//插表
    		db.execSQL("CREATE TABLE tbl_exams(code text primary key,name text,abbr text,status integer)");
    		db.execSQL("CREATE TABLE tbl_subjects(code text primary key,name text,status integer,exam_code text)");
    		db.execSQL("CREATE TABLE tbl_papers(id text primary key,title text,type integer,total integer,content text,createTime datetime,subjectCode text)");
    		db.execSQL("CREATE TABLE tbl_favorites (id text primary key,subjectId text,itemId text,itemType integer,content text,remarks text,status integer,createTime datetime,sync integer)");
    		db.execSQL("CREATE TABLE tbl_itemRecords (id text primary key,paperRecordId text,structureId text,itemId text,content text,answer text,status integer,score double,useTimes integer,createTime datetime,lastTime datetime,sync integer)");
    		db.execSQL("CREATE TABLE tbl_paperRecords (id text primary key,paperId text,status integer,score double,rights integer,useTimes integer,createTime datetime,lastTime datetime, sync integer)");
    		db.setVersion(version);
           return db; 
    	}
    	SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbPath, 
                null); 
        return db; 
    }
    public synchronized void closeDatabase() { 
        this.database.close(); 
    } 
}
