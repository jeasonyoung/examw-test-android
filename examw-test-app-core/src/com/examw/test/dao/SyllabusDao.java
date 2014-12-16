package com.examw.test.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.examw.test.db.LibraryDBUtil;
import com.examw.test.domain.Chapter;
import com.examw.test.domain.Subject;
import com.examw.test.domain.Syllabus;
import com.examw.test.model.SyllabusInfo;
import com.examw.test.util.GsonUtil;
import com.examw.test.util.StringUtils;
import com.google.gson.reflect.TypeToken;

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
		ArrayList<SyllabusInfo> list = GsonUtil.getGson().fromJson(content, new TypeToken<ArrayList<SyllabusInfo>>(){}.getType());
		db.beginTransaction();
		for(SyllabusInfo info:list)
		{
			insertChapter(db,info,syllabusId);
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}
	/**
	 * 插入章节
	 * @param db
	 * @param info
	 * @param syllabusId
	 */
	private static void insertChapter(SQLiteDatabase db,SyllabusInfo info,String syllabusId)
	{
		if(info.getChildren()!=null && info.getChildren().size()>0)
		{
			List<SyllabusInfo> children = info.getChildren();
			for(SyllabusInfo child:children)
			{
				insertChapter(db, child,syllabusId);
			}
		}
		db.execSQL("insert into ChapterTab(chapterId,syllabusId,chapterPid,title,orderNo)values(?,?,?,?,?)", 
				new Object[]{info.getId(),syllabusId,info.getPid(),info.getTitle(),info.getOrderNo()});
	}
	
	public static ArrayList<Chapter> loadAllChapters(String subjectId)
	{
		if(subjectId == null) return null;
		SQLiteDatabase db = LibraryDBUtil.getDatabase();
		try{
			return loadAllChapters(db, subjectId);
		}finally{
			db.close();
		}
		
	}
	
	private static ArrayList<Chapter> loadAllChapters(SQLiteDatabase db,String subjectId)
	{
		Cursor cursor = db.rawQuery("select c.chapterId,c.chapterPid,c.title,c.orderNo from ChapterTab c left join SyllabusTab s on c.syllabusId = s.syllabusId where s.subjectId = ? and chapterPid is null order by c.orderNo asc",
				new String[]{subjectId});
		ArrayList<Chapter> result = new ArrayList<Chapter>();
		while(cursor.moveToNext())
		{
			Chapter chapter = new Chapter(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getInt(3));
			result.add(chapter);
		}
		cursor.close();
		if(!result.isEmpty())
		{
			for(Chapter parent:result)
			{
				loadChapterChildren(db,parent,0);
			}
		}
		return result;
	}
	private static void loadChapterChildren(SQLiteDatabase db,Chapter parent,int level)
	{
		Cursor cursor = db.rawQuery("select chapterId,chapterPid,title,orderNo from ChapterTab where chapterPid = ? order by orderNo asc",
				new String[]{parent.getChapterId()});
		ArrayList<Chapter> children = parent.getChildren();
		while(cursor.moveToNext())
		{
			Chapter chapter = new Chapter(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getInt(3));
			chapter.setParent(parent);
			chapter.setLevel(level);
			children.add(chapter);
		}
		cursor.close();
		if(!children.isEmpty())
		{
			for(Chapter child:children)
			{
				loadChapterChildren(db,child,++level);
			}
		}
	}
	
	
	public static ArrayList<Chapter> insertSyllabusAndLoadChapters(Subject subject,String content)
	{
		if(StringUtils.isEmpty(content)||content.equals("[]")) return null;
		Log.d(TAG,"插入考试大纲,并且获取章节信息");	
		SQLiteDatabase db = LibraryDBUtil.getDatabase();
		SyllabusInfo syllabus = new SyllabusInfo();
		syllabus.setId(UUID.randomUUID().toString());
		syllabus.setTitle(String.format("《%s》考试大纲", subject.getName()));
		syllabus.setFullTitle(content);
		syllabus.setSubjectId(subject.getSubjectId());
		syllabus.setYear(Calendar.getInstance().get(Calendar.YEAR));
		syllabus.setOrderNo(1);
		ArrayList<SyllabusInfo> list = GsonUtil.getGson().fromJson(content, new TypeToken<ArrayList<SyllabusInfo>>(){}.getType());
		try{
			db.beginTransaction();
			insert(db,syllabus);
			Log.d(TAG,"插入考试大纲章节信息");	
			for(SyllabusInfo info:list)
			{
				insertChapter(db,info,syllabus.getId());
			}
			ArrayList<Chapter> result = loadAllChapters(db,subject.getSubjectId());
			db.setTransactionSuccessful();
			db.endTransaction();
			return result;
		}finally{
			db.close();
		}
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
