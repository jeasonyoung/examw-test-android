package com.examw.test.daonew;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.examw.test.app.AppConstant;
import com.examw.test.db.LibraryDBUtil;
import com.examw.test.db.UserDBManager;
import com.examw.test.domain.Paper;
import com.examw.test.model.PaperPreview;
import com.examw.test.model.StructureInfo;
import com.examw.test.model.sync.PaperSync;
import com.examw.test.util.CryptoUtils;
import com.examw.test.util.GsonUtil;
import com.examw.test.util.LogUtil;
import com.examw.test.util.StringUtils;

/**
 * 试卷数据
 * @author fengwei.
 * @since 2014年12月3日 下午2:15:05.
 */
public class PaperDao {
	/**
	 * 判断是否含有试卷
	 * @return
	 */
	public static boolean hasPaper(String types,String username)
	{
		SQLiteDatabase db = getDatabase(username);
		StringBuilder sql = new StringBuilder("select * from tbl_papers where 1 = 1 ");
		if(!StringUtils.isEmpty(types))
		{
			sql.append(" and type in (").append(types).append(")");
		}
		Cursor cursor = db.rawQuery(sql.toString(),new String[] {});
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
	public static void insertPaper(Paper paper,String username) {
		/*
		 * 先看存不存在,不存在就加入
		 */
		if (paper == null) return;
		SQLiteDatabase db = getDatabase(username);
		Cursor cursor = db.rawQuery("select * from tbl_papers where id = ?",
				new String[] { paper.getId() });
		if (cursor.getCount() > 0) {
			LogUtil.d( "该试卷已经加过了");
			cursor.close();
			db.close();
			return;
		}
		cursor.close();
		// id,title,type,total,content,createTime,subjectCode
		String sql = "insert into tbl_papers(id,title,type,total,content,subjectCode,createTime)values(?,?,?,?,?,?,?)";
		Object[] params = new Object[] { paper.getId(), paper.getTitle(),
				paper.getType(), paper.getTotal(), paper.getContent(),
				paper.getSubjectCode(),paper.getCreateTime()
				};
		db.execSQL(sql, params);
		db.close();
	}

	
	/**
	 * 插入试卷的集合
	 * @param list
	 * @return 返回更新的数量
	 */
	public static int insertPaperList(ArrayList<Paper> list,String username) {
		int count = 0;
		if (list != null && list.size() > 0) {
			SQLiteDatabase db = getDatabase(username);
			String sql1 = "select * from tbl_papers where id = ?";
			String sql2 = "insert into tbl_papers(id,title,type,total,content,subjectCode,createTime)values(?,?,?,?,?,?,?)";
			db.beginTransaction();
			try {
				for (Paper paper : list) {
					Cursor cursor = db.rawQuery(sql1,new String[] { paper.getId() });
					if (cursor.getCount() > 0) {
						cursor.close();
						continue;
					}
					cursor.close();
					Object[] params = new Object[] { paper.getId(), paper.getTitle(),
							paper.getType(), paper.getTotal(), paper.getContent(),
							paper.getSubjectCode(),paper.getCreateTime()};
					db.execSQL(sql2, params);
					count++;
				}
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
				db.close();
			}
		}
		return count;
	}
	/**
	 * 插入试卷的集合
	 * @param list
	 * @return 返回更新的数量
	 */
	public static int insertPapers(ArrayList<PaperSync> list,String username) {
		int count = 0;
		if (list != null && list.size() > 0) {
			SQLiteDatabase db = getDatabase(username);
			String sql1 = "select * from tbl_papers where id = ?";
			String sql2 = "insert into tbl_papers(id,title,type,total,content,subjectCode,createTime)values(?,?,?,?,?,?,?)";
			db.beginTransaction();
			try {
				for (PaperSync paper : list) {
					Cursor cursor = db.rawQuery(sql1,new String[] { paper.getId() });
					if (cursor.getCount() > 0) {
						cursor.close();
						//更新
						db.execSQL("update tbl_papers set title=?,type=?,total=?,content=?,subjectCode=?,createTime=datetime(?) where id = ?",
								new Object[]{paper.getTitle(),paper.getType(),paper.getTotal(),paper.getContent(),paper.getSubjectCode(),paper.getCreateTime(),paper.getId()});
						db.execSQL("update PaperRecordTab set paperName = ? where paperId = ?", new Object[]{paper.getTitle(),paper.getId()});
						continue;
					}
					cursor.close();
					Object[] params = new Object[] { paper.getId(),
							paper.getTitle(), paper.getType(),
							paper.getTotal(),
							//加密试卷数据
							CryptoUtils.encrypto(paper.getId(), paper.getContent()),
							paper.getSubjectCode(), paper.getCreateTime()
							};
					db.execSQL(sql2, params);
					count++;
				}
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
				db.close();
			}
		}
		return count;
	}
	/**
	 * 按科目,类型查询试卷 不带试卷的内容
	 * @param subjectCode
	 * @param types
	 * @return
	 */
	public static ArrayList<Paper> findPapers(String subjectCode,String types,String username) {
		SQLiteDatabase db = getDatabase(username);
		//String id, String title, Integer type, Integer total, String content, String subjectCode, String createTime
		StringBuilder sql = new StringBuilder("select id,title,type,total,subjectCode,createTime from tbl_papers where 1 = 1 ");
		ArrayList<String> params = new ArrayList<String>();
		if(!StringUtils.isEmpty(subjectCode))
		{
			sql.append(" and subjectCode = ? ");
			params.add(subjectCode);
		}
		if(!StringUtils.isEmpty(types))
		{
			sql.append(" and type in (").append(types).append(")");
		}
		sql.append(" order by createTime desc");
		Cursor cursor = db.rawQuery(sql.toString(), params.toArray(new String[0]));
		if (cursor.getCount() == 0) {
			cursor.close();
			db.close();
			return null;
		}
		ArrayList<Paper> list = new ArrayList<Paper>();
		while (cursor.moveToNext()) {
			Paper p = new Paper(cursor.getString(0), cursor.getString(1),
					cursor.getInt(2), cursor.getInt(3),null, cursor.getString(4),
					cursor.getString(5));
			list.add(p);
		}
		sql = null;params.clear();params = null;
		cursor.close();
		db.close();
		return list;
	}
	/**
	 * 查询试卷的内容
	 * @param paperId
	 * @return
	 */
	public static String findPaperContent(String paperId,String username)
	{
		LogUtil.d( String.format("查询试卷[PaperId= %s]的内容",paperId));
		if(StringUtils.isEmpty(paperId)) return null;
		SQLiteDatabase db = getDatabase(username);
		Cursor cursor = db.rawQuery("select content from tbl_papers where id = ?", new String[]{paperId});
		if (cursor.getCount() == 0) {
			cursor.close();
			db.close();
			LogUtil.d( String.format("试卷[PaperId= %s]没有内容",paperId));
			return null;
		}
		cursor.moveToNext();
		String content = cursor.getString(0);
		cursor.close();
		db.close();
		LogUtil.d( String.format("试卷[PaperId= %s]已有内容",paperId));
		//解密数据的内容
		content = CryptoUtils.decrypto(paperId, content);
		//转换数据
		return content;
	}
	
	/**
	 * 插入试卷的内容
	 * @return
	 */
	public static void updatePaperContent(String paperId,String content,String username)
	{
		LogUtil.d( String.format("插入试卷[PaperId= %s]的内容",paperId));
		if (StringUtils.isEmpty(content) || StringUtils.isEmpty(paperId)) return;
		SQLiteDatabase db = getDatabase(username);
		//加密试卷的数据内容
		content = CryptoUtils.encrypto(paperId, content);
		db.execSQL("update tbl_papers set content = ? where id = ?", new Object[]{content,paperId});
		db.close();
	}
	
	//获取大题结构
	public static String getRuleList(PaperPreview paper)
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

	//查找每日一练
	public static ArrayList<Paper> findDailyPapers(long today,int dayOffset,String username) {
		LogUtil.d("查询每日一练的数据");
		SQLiteDatabase db = getDatabase(username);
		StringBuilder sql = new StringBuilder("select id,title,type,total,subjectCode,createTime from tbl_papers where 1 = 1 and type = ? and createTime > ? and createTime < ? ");
		ArrayList<String> params = new ArrayList<String>();
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(today));
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)-dayOffset, 0, 0, 0);
		params.add(String.valueOf(AppConstant.PAPER_TYPE_DAILY));
		params.add(StringUtils.toStandardDateShort(cal.getTime()));
		LogUtil.d("当天的时间1:"+params.get(1));
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)+1, 0, 0, 0);
		params.add(StringUtils.toStandardDateShort(cal.getTime()));
		LogUtil.d("当天的时间2:"+params.get(2));
		sql.append(" order by createTime desc");
		Cursor cursor = db.rawQuery(sql.toString(), params.toArray(new String[0]));
		if (cursor.getCount() == 0) {
			cursor.close();
			db.close();
			return null;
		}
		ArrayList<Paper> list = new ArrayList<Paper>();
		while (cursor.moveToNext()) {
			Paper p = new Paper(cursor.getString(0), cursor.getString(1),
					cursor.getInt(2), cursor.getInt(3),null, cursor.getString(4),
					cursor.getString(5));
			list.add(p);
		}
		sql = null;params.clear();params = null;
		cursor.close();
		db.close();
		return list;
	}
	
	/**
	 * 查询最新的试卷时间,不包括每日一练
	 * @return
	 */
	public static String findLastedPaperAddTime(String username)
	{
		LogUtil.d("查询最新的试卷时间");
		SQLiteDatabase db = getDatabase(username);
		Cursor cursor = db.rawQuery("select createTime from tbl_papers order by createTime desc limit 1 where paperType != "+AppConstant.PAPER_TYPE_DAILY,new String[0]);
		String time = null;
		if(cursor.moveToNext())
		{
			time = cursor.getString(0);
		}
		cursor.close();
		db.close();
		return time;
	}
	
	private static SQLiteDatabase getDatabase(String username)
	{
		if(username == null) return LibraryDBUtil.getDatabase();
		return UserDBManager.openDatabase(username);
	}
}