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
		db.execSQL("CREATE TABLE ProductTab(_ID INTEGER PRIMARY KEY AUTOINCREMENT,PRODUCTID TEXT,NAME TEXT,INFO TEXT)");
		db.execSQL("CREATE TABLE SubjectTab(_ID INTEGER PRIMARY KEY AUTOINCREMENT,SUBJECTID TEXT,NAME TEXT,ORDERNO INTEGER)");
		db.execSQL("CREATE TABLE PaperTab(_ID INTEGER PRIMARY KEY AUTOINCREMENT,PAPERID TEXT,NAME TEXT,DESCRIPTION TEXT,CONTENT TEXT,EXAMID TEXT,SUBJECTID TEXT,SOURCENAME TEXT,AREANAME TEXT,TYPE INTEGER,PRICE INTEGER,TIME INTEGER,YEAR INTEGER,TOTAL INTEGER,SCORE DOUBLE,PUBLISHTIME DATETIME DEFAULT (datetime('now','localtime')))");
		db.execSQL("CREATE TABLE KnowledgeTab(_ID INTEGER PRIMARY KEY AUTOINCREMENT,KNOWLEDGEID TEXT,KNOWLEDGETITLE TEXT,KNOWLEDGECONTENT TEXT,CHAPTERID TEXT,CLASSID TEXT,ORDERID INTEGER)");
		db.execSQL("CREATE TABLE SyllabusTab(_ID INTEGER PRIMARY KEY AUTOINCREMENT,CHAPTERID TEXT,CHAPTERTITLE TEXT,ORDERNO INTEGER)");
		db.execSQL("CREATE TABLE ItemTab(_ID INTEGER PRIMARY KEY AUTOINCREMENT,ITEMID TEXT,SUBJECTID TEXT,CONTENT TEXT,MATERIAL TEXT,TYPE INTEGER)");
		db.execSQL("CREATE TABLE ItemSyllabusTab(_ID INTEGER PRIMARY KEY AUTOINCREMENT,ITEMID TEXT,CHAPTERID TEXT)");
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
