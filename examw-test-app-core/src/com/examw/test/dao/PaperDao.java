package com.examw.test.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.examw.test.db.LibraryDBUtil;
import com.examw.test.domain.Paper;
import com.examw.test.model.FrontPaperInfo;
import com.examw.test.util.StringUtils;

/**
 * 试卷数据
 * @author fengwei.
 * @since 2014年12月3日 下午2:15:05.
 */
public class PaperDao {
	/**
	 * 模拟题。
	 * SIMU(0x02),
	FORECAST(0x03),
	PRACTICE(0x04),
	CHAPTER(0x05),
	DAILY(0x06);
	 */
	
	private static final String TAG = "PaperDao";
	public static final int TYPE_REAL = 1;
	public static final int TYPE_SIMU = 2;
	public static final int TYPE_FORECAST = 3;
	public static final int TYPE_PRACTICE = 4;
	public static final int TYPE_CHAPTER = 5;
	public static final int TYPE_DAILY = 6;
	
	
	public static boolean hasPaper()
	{
		SQLiteDatabase db = LibraryDBUtil.getDatabase();
		Cursor cursor = db.rawQuery("select * from PaperTab",new String[] {});
		boolean flag = cursor.getCount()>0;
		cursor.close();
		db.close();
		return flag;
	}
	/**
	 * 插入试卷和大题
	 * @param paper 试卷
	 * 
	 */
	public static void insertPaper(Paper paper) {
		/*
		 * 先看存不存在,不存在就加入
		 */
		if (paper == null) return;
		SQLiteDatabase db = LibraryDBUtil.getDatabase();
		Cursor cursor = db.rawQuery("select * from PaperTab where paperid = ?",
				new String[] { paper.getPaperId() });
		if (cursor.getCount() > 0) {
			Log.d(TAG, "该试卷已经加过了");
			cursor.close();
			LibraryDBUtil.close();
			return;
		}
		cursor.close();
		// paperid ,name ,description ,content ,examid ,subjectid ,sourcename ,
		// areaname ,type ,price ,time ,year ,total ,score ,publishtime
		String sql = "insert into PaperTab(paperid,name,description,content,examid,subjectid,sourcename,areaname,type,price,time,year,total,score,publishtime)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Object[] params = new Object[] { paper.getPaperId(), paper.getName(),
				paper.getDescription(), paper.getContent(), paper.getExamId(),
				paper.getSubjectId(), paper.getSourceName(),
				paper.getAreaName(), paper.getType(), paper.getPrice(),
				paper.getTime(), paper.getYear(), paper.getTotal(),
				paper.getScore(), paper.getPublishTime() };
		db.execSQL(sql, params);
		LibraryDBUtil.close();
	}

	
	/**
	 * 插入试卷的集合
	 * @param list
	 * @return 返回更新的数量
	 */
	public static int insertPaperList(ArrayList<FrontPaperInfo> list) {
		int count = 0;
		if (list != null && list.size() > 0) {
			SQLiteDatabase db = LibraryDBUtil.getDatabase();
			String sql1 = "select * from PaperTab where paperid = ?";
			String sql2 = "insert into PaperTab(paperid,name,description,content,examid,subjectid,sourcename,areaname,type,price,time,year,total,score,publishtime)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			db.beginTransaction();
			try {
				for (FrontPaperInfo paper : list) {
					Cursor cursor = db.rawQuery(sql1,new String[] { paper.getId() });
					if (cursor.getCount() > 0) {
						cursor.close();
						continue;
					}
					cursor.close();
					Object[] params = new Object[] { paper.getId(), paper.getName(),
							paper.getDescription(), null, paper.getExamId(),
							paper.getSubjectId(), paper.getSourceName(),
							paper.getAreaName(), paper.getType(), paper.getPrice(),
							paper.getTime(), paper.getYear(), paper.getTotal(),
							paper.getScore(), paper.getPublishTime() };
					db.execSQL(sql2, params);
					count++;
				}
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
				LibraryDBUtil.close();
			}
		}
		return count;
	}

	public static List<Paper> findPapers(Integer type) {
		SQLiteDatabase db = LibraryDBUtil.getDatabase();
		// String paperId, String paperName, int paperSorce, int
		// paperTime,String courseId,String examId
		String sql = "select paperid,name,score,time,year,price,total from PaperTab order by publishtime desc";
		if(type != null)
		{
			sql = "select paperid,name,score,time,year,price,total from PaperTab where type = "+type+" order by publishtime desc";
		}
		String[] params = new String[] {};
		Cursor cursor = db.rawQuery(sql, params);
		if (cursor.getCount() == 0) {
			cursor.close();
			LibraryDBUtil.close();
			return null;
		}
		List<Paper> list = new ArrayList<Paper>();
		while (cursor.moveToNext()) {
			Paper p = new Paper(cursor.getString(0), cursor.getString(1),
					cursor.getDouble(2), cursor.getInt(3), cursor.getInt(4),
					cursor.getInt(5), cursor.getInt(6));
			list.add(p);
		}
		cursor.close();
		LibraryDBUtil.close();
		return list;
	}
	
	public static String findPaperContent(String paperId)
	{
		if(StringUtils.isEmpty(paperId)) return null;
		SQLiteDatabase db = LibraryDBUtil.getDatabase();
		Cursor cursor = db.rawQuery("select content from PaperTab where paperid = ?", new String[]{paperId});
		if (cursor.getCount() == 0) {
			cursor.close();
			LibraryDBUtil.close();
			return null;
		}
		cursor.moveToNext();
		String content = cursor.getString(0);
		cursor.close();
		LibraryDBUtil.close();
		return content;
	}
	
	
	

//	public PaperList findAllPapers(String classid, int page) {
//		PaperList list = new PaperList();
//		SQLiteDatabase db = LibraryDBUtil.getDatabase();
//		// String paperId, String paperName, int paperSorce, int
//		// paperTime,String courseId,String examId
//		String sql1 = "select count(*) from PaperTab where papertime > -1";
//		Cursor cursor = db.rawQuery(sql1, new String[] {});
//		cursor.moveToNext();
//		int total = cursor.getInt(0);
//		cursor.close();
//		if (total == 0) {
//			LibraryDBUtil.close();
//			return list;
//		}
//		list.setPaperCount(total);
//		String sql = "select paperid,papername,addtime,paperscore,papertime,year,clicknum,price,classid,totalNum from PaperTab where classid =? and papertime > 0 order by addtime desc limit 10 offset ? ";
//		String[] params = new String[] { classid, page * 10 + "" };
//		Cursor cursor1 = db.rawQuery(sql, params);
//		ArrayList<Paper> pList = list.getPaperlist();
//		while (cursor1.moveToNext()) {
//			Paper p = new Paper(cursor1.getString(0), cursor1.getString(1),
//					cursor1.getString(2), cursor1.getInt(3), cursor1.getInt(4),
//					cursor1.getInt(5), cursor1.getInt(6), cursor1.getInt(7),
//					cursor1.getString(8), cursor1.getInt(9));
//			pList.add(p);
//		}
//		cursor1.close();
//		LibraryDBUtil.close();
//		return list;
//	}

	public String findAddTime() {
		SQLiteDatabase db = LibraryDBUtil.getDatabase();
		String addtime = null;
		Cursor cursor = db.rawQuery(
				"select addtime from DataAddTimeTab order by _id desc",
				new String[] {});
		if (cursor.moveToNext()) {
			addtime = cursor.getString(0);
		}
		cursor.close();
		LibraryDBUtil.close();
		return addtime;
	}
}
