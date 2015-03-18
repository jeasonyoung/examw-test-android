package com.examw.test.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.examw.test.app.AppContext;

/**
 * 题库数据库
 * 
 * @author fengwei.
 * @since 2014年12月2日 下午4:50:04.
 */
public class NewLibraryDBHelper extends SQLiteOpenHelper {
	// 数据库名称
	public static final String DATABASENAME = "examw_library.db";
	// 版本
	public static final int VERSION = 1;

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE tbl_exams(code text primary key,name text,abbr text,status integer)");
		db.execSQL("CREATE TABLE tbl_subjects(code text primary key,name text,status integer,exam_code text)");
		db.execSQL("CREATE TABLE tbl_papers(id text primary key,title text,type integer,total integer,content text,createTime datetime,subjectCode text)");
		db.execSQL("CREATE TABLE tbl_favorites (id text primary key,subjectCode text,itemId text,itemType integer,content text,remarks text,status integer,createTime datetime,sync integer)");
		db.execSQL("CREATE TABLE tbl_itemRecords (id text primary key,paperRecordId text,structureId text,itemId text,content text,answer text,status integer,score double,useTimes integer,createTime datetime,lastTime datetime,sync integer)");
		db.execSQL("CREATE TABLE tbl_paperRecords (id text primary key,paperId text,status integer,score double,rights integer,useTimes integer,createTime datetime,lastTime datetime, sync integer)");
		db.setVersion(VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

	public NewLibraryDBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, DATABASENAME, factory, VERSION);
	}

	public NewLibraryDBHelper() {
		this(AppContext.getContext(), DATABASENAME, null, VERSION);
	}
}
