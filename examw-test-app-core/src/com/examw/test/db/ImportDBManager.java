package com.examw.test.db;
/**
 * 
 * @author fengwei.
 * @since 2014年12月28日 下午2:34:36.
 */
import java.io.File;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

public class ImportDBManager {
	private SQLiteDatabase database; 
    private String dirPath = Environment.getExternalStorageDirectory().getPath()+File.separator+"examw"+File.separator+
    		"database"+File.separator;
    private String dbName;
    private final int version = 1;
  
    public ImportDBManager() { 
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
    		db.execSQL("create table ProductTab(_id integer primary key autoincrement,productId text,name text,info text)");
    		db.execSQL("create table SubjectTab(_id integer primary key autoincrement,subjectId text,name text,orderno integer)");
    		db.execSQL("create table PaperTab(_id integer primary key autoincrement,paperId text,name text,description text,content text,structures text,examId text,subjectId text,sourceName text,areaName text,type integer,price integer,time integer,year integer,total integer,userTotal integer,score double,publishTime datetime default (datetime('now','localtime')))");
    		db.execSQL("create table SyllabusTab(_id integer primary key autoincrement,syllabusId text,title text,content text,subjectId text,year integer,orderNo integer)");
    		db.execSQL("create table ChapterTab(_id integer primary key autoincrement,syllabusId text,chapterId text,chapterPid text,title text,content text,orderNo integer,subjectId text)");
    		db.execSQL("create table KnowledgeTab(_id integer primary key autoincrement,knowledgeId text,title text,content text,chapterId text,subjectId text,orderid integer)");
    		db.execSQL("create table ItemTab(_id integer primary key autoincrement,itemId text,subjectId text,content text,material text,type integer)");
    		db.execSQL("create table ItemsyllabusTab(_id integer primary key autoincrement,itemId text,chapterId text)");
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
