package com.examw.test.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.examw.test.db.LibraryDBUtil;
import com.examw.test.domain.Syllabus;
import com.examw.test.model.SyllabusInfo;

/**
 * 大纲DAO
 * @author fengwei.
 * @since 2014年12月15日 下午3:25:58.
 */
public class SyllabusDao {
	private static final String TAG = "SyllabusDao";
	
	public static ArrayList<Syllabus> insertSyllabus(List<SyllabusInfo> list)
	{
		if(list == null || list.isEmpty()) return null;
		SQLiteDatabase db = LibraryDBUtil.getDatabase();
		db.beginTransaction();
		ArrayList<Syllabus> result = new ArrayList<Syllabus>();
		for(SyllabusInfo info : list)
		{
			if(info == null) continue;
			insert(db,info);
			result.add(convert(info));
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
		return result;
	}
	//插入[避免重复插入]
	private static void insert(SQLiteDatabase db,SyllabusInfo info)
	{
		Cursor cursor = db.rawQuery("select * from SyllabusTab where syllabusId = ?",
				new String[] { info.getId() });
		if (cursor.getCount() > 0) {
			cursor.close();
			return;
		}
		cursor.close();
		//syllabusId ,title,content ,subjectId ,year ,orderNo 
		db.execSQL("insert into SyllabusTab(syllabusId,title,content,subjectId,year,orderNo)values(?,?,?,?,?,?)",
				new Object[] {info.getId(),info.getTitle(),info.getFullTitle(),info.getSubjectId(),info.getYear(),info.getOrderNo()});
	}
	//类型转换
	private static Syllabus convert(SyllabusInfo info)
	{
		Syllabus data = new Syllabus();
		data.setSyllabusId(info.getId());
		data.setSubjectId(info.getSubjectId());
		data.setTitle(info.getTitle());
		data.setYear(info.getYear());
		data.setOrderNo(info.getOrderNo());
		return data;
	}
	/**
	 * 按科目ID查询大纲
	 * @param subjectId
	 * @return
	 */
	public static ArrayList<Syllabus> loadSyllabus(String subjectId)
	{
		if(subjectId == null) return null;
		SQLiteDatabase db = LibraryDBUtil.getDatabase();
		Cursor cursor = db.rawQuery("select syllabusId,subjectId,title,content,year,orderNo from SyllabusTab where subjectId = ?", new String[]{subjectId});
		if(cursor.getCount()>0)
		{
			ArrayList<Syllabus> list = new ArrayList<Syllabus>();
			while(cursor.moveToNext()){
				Syllabus data = new Syllabus(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getInt(4),cursor.getInt(5));
				list.add(data);
			}
			cursor.close();
			db.close();
			return list;
		}
		cursor.close();
		db.close();
		return null;
	}
	/**
	 * 更新大纲内容
	 * @param syllabusId
	 * @param content
	 */
	public static void updateContent(String syllabusId,String content)
	{
		SQLiteDatabase db = LibraryDBUtil.getDatabase();
		db.execSQL("update SyllabusTab set content = ? where syllabusId = ?", 
				new Object[]{content,syllabusId});
		//TODO 分开插入到章节表中
		db.close();
	}
	
	public static String loadContent(String syllabusId)
	{
		SQLiteDatabase db = LibraryDBUtil.getDatabase();
		Cursor cursor = db.rawQuery("select content from SyllabusTab where syllabusId = ?",new String[]{syllabusId});
		if(cursor.getCount()>0)
		{
			cursor.moveToNext();
			String content = cursor.getString(0);
			cursor.close();
			db.close();
			return content;
		}
		cursor.close();
		db.close();
		return null;
	}
}
