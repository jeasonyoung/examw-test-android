package com.examw.test.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.examw.test.app.AppConstant;
import com.examw.test.db.LibraryDBUtil;
import com.examw.test.domain.Paper;
import com.examw.test.model.FrontPaperInfo;
import com.examw.test.model.PaperPreview;
import com.examw.test.model.StructureInfo;
import com.examw.test.util.GsonUtil;
import com.examw.test.util.StringUtils;

/**
 * 试卷数据
 * @author fengwei.
 * @since 2014年12月3日 下午2:15:05.
 */
public class PaperDao {
	
	private static final String TAG = "PaperDao";
	
	/**
	 * 判断是否含有试卷
	 * @return
	 */
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
		String sql = "insert into PaperTab(paperid,name,description,content,examid,subjectid,sourcename,areaname,type,price,time,year,total,userTotal,score,publishtime)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Object[] params = new Object[] { paper.getPaperId(), paper.getName(),
				paper.getDescription(), paper.getContent(), paper.getExamId(),
				paper.getSubjectId(), paper.getSourceName(),
				paper.getAreaName(), paper.getType(), paper.getPrice(),
				paper.getTime(), paper.getYear(), paper.getTotal(),paper.getUserTotal(),
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
			String sql2 = "insert into PaperTab(paperid,name,description,content,examid,subjectid,sourcename,areaname,type,price,time,year,total,userTotal,score,publishtime)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
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
							paper.getTime(), paper.getYear(), paper.getTotal(),paper.getUserTotal(),
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

	public static ArrayList<Paper> findPapers(String subjectId,String types) {
		SQLiteDatabase db = LibraryDBUtil.getDatabase();
		// String paperId, String paperName, int paperSorce, int
		// paperTime,String courseId,String examId
		StringBuilder sql = new StringBuilder("select paperid,name,score,time,year,price,total,userTotal,publishTime from PaperTab where 1 = 1 ");
		ArrayList<String> params = new ArrayList<String>();
		if(!StringUtils.isEmpty(subjectId))
		{
			sql.append(" and subjectId = ? ");
			params.add(subjectId);
		}
		if(!StringUtils.isEmpty(types))
		{
			sql.append(" and type in (").append(types).append(")");
		}
		sql.append(" order by publishtime desc");
		Cursor cursor = db.rawQuery(sql.toString(), params.toArray(new String[0]));
		if (cursor.getCount() == 0) {
			cursor.close();
			LibraryDBUtil.close();
			return null;
		}
		ArrayList<Paper> list = new ArrayList<Paper>();
		while (cursor.moveToNext()) {
			Paper p = new Paper(cursor.getString(0), cursor.getString(1),
					cursor.getDouble(2), cursor.getInt(3), cursor.getInt(4),
					cursor.getInt(5), cursor.getInt(6),cursor.getInt(7),cursor.getString(8));
			list.add(p);
		}
		sql = null;params.clear();params = null;
		cursor.close();
		LibraryDBUtil.close();
		return list;
	}
	/**
	 * 查询试卷的内容
	 * @param paperId
	 * @return
	 */
	public static String findPaperContent(String paperId)
	{
		Log.d(TAG, String.format("查询试卷[PaperId= %s]的内容",paperId));
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
	/**
	 * 查询大题
	 * @param paperId
	 * @return
	 */
	public static String findPaperStructureContent(String paperId)
	{
		Log.d(TAG, String.format("查询试卷[PaperId= %s]的大题内容",paperId));
		if(StringUtils.isEmpty(paperId)) return null;
		SQLiteDatabase db = LibraryDBUtil.getDatabase();
		Cursor cursor = db.rawQuery("select structures from PaperTab where paperid = ?", new String[]{paperId});
		if (cursor.getCount() == 0) {
			cursor.close();
			db.close();
			return null;
		}
		cursor.moveToNext();
		String content = cursor.getString(0);
		cursor.close();
		db.close();
		return content;
	}
	/**
	 * 插入试卷的内容
	 * @return
	 */
	public static void updatePaperContent(String paperId,String content)
	{
		Log.d(TAG, String.format("插入试卷[PaperId= %s]的内容",paperId));
		if (StringUtils.isEmpty(content) || StringUtils.isEmpty(paperId)) return;
		SQLiteDatabase db = LibraryDBUtil.getDatabase();
		PaperPreview paper = GsonUtil.jsonToBean(content, PaperPreview.class);
		String ruleContent = getRuleList(paper);
		db.execSQL("update PaperTab set content = ?,structures = ? where paperid = ?", new Object[]{content,ruleContent,paperId});
		db.close();
	}
	//获取大题结构
	private static String getRuleList(PaperPreview paper)
	{
		List<StructureInfo> rules = paper.getStructures();
		if(rules == null) return "";
		clearItems(rules);
		return GsonUtil.objectToJson(paper);
	}
	private static void clearItems(List<StructureInfo> rules)
	{
		for(StructureInfo info:rules)
		{
			if(info == null) continue;
			if(info.getChildren()!=null && info.getChildren().size()>0)
			{
				clearItems(info.getChildren());
			}
			info.setItems(null);
		}
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
	//查找每日一练
	public static ArrayList<Paper> findDailyPapers(long today,int dayOffset) {
		Log.d(TAG,"查询每日一练的数据");
		SQLiteDatabase db = LibraryDBUtil.getDatabase();
		StringBuilder sql = new StringBuilder("select paperid,name,score,time,year,price,total,userTotal,publishTime from PaperTab where 1 = 1 and type = ? and publishTime > ? and publishTime < ? ");
		ArrayList<String> params = new ArrayList<String>();
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(today));
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)-dayOffset, 0, 0, 0);
		params.add(String.valueOf(AppConstant.PAPER_TYPE_DAILY));
		params.add(StringUtils.toStandardDateShort(cal.getTime()));
		Log.d(TAG,"当天的时间1:"+params.get(1));
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)+1, 0, 0, 0);
		params.add(StringUtils.toStandardDateShort(cal.getTime()));
		Log.d(TAG,"当天的时间2:"+params.get(2));
		sql.append(" order by publishtime desc");
		Cursor cursor = db.rawQuery(sql.toString(), params.toArray(new String[0]));
		if (cursor.getCount() == 0) {
			cursor.close();
			LibraryDBUtil.close();
			return null;
		}
		ArrayList<Paper> list = new ArrayList<Paper>();
		while (cursor.moveToNext()) {
			Paper p = new Paper(cursor.getString(0), cursor.getString(1),
					cursor.getDouble(2), cursor.getInt(3), cursor.getInt(4),
					cursor.getInt(5), cursor.getInt(6),cursor.getInt(7),cursor.getString(8));
			list.add(p);
		}
		sql = null;params.clear();params = null;
		cursor.close();
		LibraryDBUtil.close();
		return list;
	}
}
