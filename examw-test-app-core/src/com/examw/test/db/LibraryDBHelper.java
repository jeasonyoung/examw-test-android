package com.examw.test.db;

import com.examw.test.app.AppContext;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

/**
 * 题库数据库
 * 
 * @author fengwei.
 * @since 2014年12月2日 下午4:50:04.
 */
public class LibraryDBHelper extends SQLiteOpenHelper {
	// 数据库名称
	public static final String DATABASENAME = "examw_library.db";
	// 版本
	public static final int VERSION = 1;

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table ProductTab(_id integer primary key autoincrement,productId text,name text,info text)");
		db.execSQL("create table SubjectTab(_id integer primary key autoincrement,subjectId text,name text,orderno integer)");
		db.execSQL("create table PaperTab(_id integer primary key autoincrement,paperId text,name text,description text,content text,examId text,subjectId text,sourceName text,areaName text,type integer,price integer,time integer,year integer,total integer,score double,publishTime datetime default (datetime('now','localtime')))");
		db.execSQL("create table KnowledgeTab(_id integer primary key autoincrement,knowledgeid text,knowledgetitle text,knowledgecontent text,chapterid text,classid text,orderid integer)");
		db.execSQL("create table SyllabusTab(_id integer primary key autoincrement,chapterId text,chapterTitle text,orderNo integer)");
		db.execSQL("create table ItemTab(_id integer primary key autoincrement,itemId text,subjectId text,content text,material text,type integer)");
		db.execSQL("create table ItemsyllabusTab(_id integer primary key autoincrement,itemId text,chapterId text)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

	public LibraryDBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, DATABASENAME, factory, VERSION);
	}

	public LibraryDBHelper() {
		this(AppContext.getContext(), DATABASENAME, null, VERSION);
	}
}
