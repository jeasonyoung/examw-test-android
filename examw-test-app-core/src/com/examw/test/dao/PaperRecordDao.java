package com.examw.test.dao;

import java.math.BigDecimal;
import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.examw.test.app.AppConfig;
import com.examw.test.app.AppConstant;
import com.examw.test.db.UserDBManager;
import com.examw.test.domain.ItemRecord;
import com.examw.test.domain.PaperRecord;
import com.examw.test.domain.Subject;
import com.examw.test.model.SimplePaper;
import com.examw.test.model.StructureInfo;
import com.examw.test.model.StructureItemInfo;
import com.examw.test.util.GsonUtil;
import com.examw.test.util.StringUtils;

/**
 * 考试记录DAO
 * @author fengwei.
 * @since 2014年12月5日 上午11:31:59.
 */
public class PaperRecordDao {
	private static final String TAG = "PaperRecordDao";
	public static final int PAGESIZE = 20;
	/**
	 * 保存考试记录
	 * @param record
	 * @return
	 */
	public static boolean save(PaperRecord record)
	{
		/*
		 *  private String recordId,paperId,paperName,userId,userName,productId,terminalId,status;
			private BigDecimal score;
			private Integer usedTime,rightNum;
			private Date createTime,lastTime;
		 */
		if (record == null)	return false;
		if (record.getUserName() == null)	return false;
		SQLiteDatabase db = UserDBManager.openDatabase(record.getUserName());
		Log.d(TAG,"插入考试记录");
		String sql = "insert into PaperRecordTab(recordId,paperId,paperName,paperType,userId,userName,productId,terminalId,status,score,useTime,rightNum,torf) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Object[] params = new Object[] {
			record.getRecordId(),record.getPaperId(),record.getPaperName(),record.getPaperType(),
			record.getUserId(),record.getUserName(),AppConfig.PRODUCTID,AppConfig.TERMINALID,record.getStatus(),0,0,0,record.getTorf()
		};
		db.execSQL(sql, params);
		db.close();
		return true;
		
	}
	/**
	 * 查询上一次的练习
	 * @return
	 */
	public static PaperRecord findLastRecord(String userName)
	{
		Log.d(TAG,String.format("查询用户[userName = %s]的记录", userName));
		SQLiteDatabase db = UserDBManager.openDatabase(userName);
		/*
		 *  private String recordId,paperId,paperName,userId,userName,productId,terminalId,status;
			private BigDecimal score;
			private Integer usedTime,rightNum;
			private Date createTime,lastTime;
		 */
		String sql = "select recordId,paperId,paperName,paperType,userId,userName,productId,terminalId,status,score,useTime,rightNum,createTime,lastTime,torf from PaperRecordTab where userName = ? order by lastTime desc ";
		String[] params = new String[] {userName};
		Cursor cursor = db.rawQuery(sql, params);
		if (cursor.getCount() == 0) {
			cursor.close();
			db.close();
			return null;
		}
		cursor.moveToNext();
		PaperRecord record = new PaperRecord(cursor.getString(0), cursor.getString(1),
					cursor.getString(2), cursor.getInt(3), cursor.getString(4),
					cursor.getString(5), cursor.getString(6),cursor.getString(7),
					cursor.getInt(8),cursor.getDouble(9),cursor.getInt(10),cursor.getInt(11),
					cursor.getString(12),cursor.getString(13),cursor.getString(14));
		cursor.close();
		db.close();
		return record;
	}
	public static PaperRecord findLastRecord(String userName,String types)
	{
		Log.d(TAG,String.format("查询用户[userName = %s]的记录", userName));
		SQLiteDatabase db = UserDBManager.openDatabase(userName);
		/*
		 *  private String recordId,paperId,paperName,userId,userName,productId,terminalId,status;
			private BigDecimal score;
			private Integer usedTime,rightNum;
			private Date createTime,lastTime;
		 */
		String sql = "select recordId,paperId,paperName,paperType,userId,userName,productId,terminalId,status,score,useTime,rightNum,createTime,lastTime,torf from PaperRecordTab where userName = ? and paperType in("+types+") order by lastTime desc ";
		String[] params = new String[] {};
		Cursor cursor = db.rawQuery(sql, params);
		if (cursor.getCount() == 0) {
			cursor.close();
			db.close();
			return null;
		}
		cursor.moveToNext();
		PaperRecord record = new PaperRecord(cursor.getString(0), cursor.getString(1),
					cursor.getString(2), cursor.getInt(3), cursor.getString(4),
					cursor.getString(5), cursor.getString(6),cursor.getString(7),
					cursor.getInt(8),cursor.getDouble(9),cursor.getInt(10),cursor.getInt(11),
					cursor.getString(12),cursor.getString(13),cursor.getString(14));
		cursor.close();
		db.close();
		return record;
	}
	/**
	 * 查询所有的记录
	 * @return
	 */
	public static ArrayList<PaperRecord> findAll(String userName,String userId,String lastTime)
	{
		Log.d(TAG,String.format("查询用户[%1$s][%2$s]的全部记录", userName,userId));
		if(userName == null || userId == null) return null;
		if(StringUtils.isEmpty(lastTime)) lastTime = "1970-01-01 00:00:00";
		SQLiteDatabase db = UserDBManager.openDatabase(userName);
		ArrayList<PaperRecord> list = new ArrayList<PaperRecord>();
		Cursor cursor = db.rawQuery("select recordId,paperId,paperName,paperType,userId,userName,productId,terminalId,status,score,useTime,rightNum,createTime,lastTime,torf from PaperRecordTab where status = 1 and createTime > ? ", 
				new String[]{lastTime});
		while(cursor.moveToNext())
		{
			PaperRecord record = new PaperRecord(cursor.getString(0), cursor.getString(1),
					cursor.getString(2), cursor.getInt(3), cursor.getString(4),
					cursor.getString(5), cursor.getString(6),cursor.getString(7),
					cursor.getInt(8),cursor.getDouble(9),cursor.getInt(10),cursor.getInt(11),
					cursor.getString(12),cursor.getString(13),cursor.getString(14));
			record.setUserId(userId);
			record.setItems(findItemRecords(db,record.getRecordId()));
			list.add(record);
		}
		return list;
	}
	/**
	 * 根据ID查询试卷记录
	 * @param recordId
	 * @return
	 */
	public static PaperRecord findById(String userName,String recordId,boolean withItems)
	{
		Log.d(TAG,String.format("查询[recordId = %s]的记录", recordId));
		SQLiteDatabase db = UserDBManager.openDatabase(userName);
		/*
		 *  private String recordId,paperId,paperName,userId,userName,productId,terminalId,status;
			private BigDecimal score;
			private Integer usedTime,rightNum;
			private Date createTime,lastTime;
		 */
		String sql = "select recordId,paperId,paperName,paperType,userId,userName,productId,terminalId,status,score,useTime,rightNum,createTime,lastTime,torf from PaperRecordTab where recordId = ? ";
		String[] params = new String[] {recordId};
		Cursor cursor = db.rawQuery(sql, params);
		if (cursor.getCount() == 0) {
			cursor.close();
			db.close();
			return null;
		}
		cursor.moveToNext();
		PaperRecord record = new PaperRecord(cursor.getString(0), cursor.getString(1),
					cursor.getString(2), cursor.getInt(3), cursor.getString(4),
					cursor.getString(5), cursor.getString(6),cursor.getString(7),
					cursor.getInt(8),cursor.getDouble(9),cursor.getInt(10),cursor.getInt(11),
					cursor.getString(12),cursor.getString(13),cursor.getString(14));
		cursor.close();
		if(withItems)
		{
//			/recordId,structureId,itemId,itemContent,answer,termialId,status,score,useTime,createTime
			record.setItems(findItemRecords(db,record.getRecordId()));
		}
		db.close();
		return record;
	}
	private static ArrayList<ItemRecord> findItemRecords(SQLiteDatabase db,String recordId)
	{
		String sqlItemRecord = "select recordId,structureId,itemId,answer,status,score from ItemRecordTab where recordId = ? order by createTime desc";
		Cursor cursorItem = db.rawQuery(sqlItemRecord, new String[]{recordId});
		ArrayList<ItemRecord> items = null;
		if (cursorItem.getCount() > 0) {
			items = new ArrayList<ItemRecord>();
			while (cursorItem.moveToNext())
			{
				ItemRecord itemRecord = new ItemRecord(cursorItem.getString(0), cursorItem.getString(1),
						cursorItem.getString(2), cursorItem.getString(3), cursorItem.getInt(4),
						new BigDecimal(cursorItem.getDouble(5)));
				items.add(itemRecord);
			}
		}
		cursorItem.close();
		return items;
	}
	/**
	 * 查询试卷最近一次的考试记录
	 * @param paperId	试卷ID
	 * @param userName	用户名
	 * @param withItems	是否带上试题
	 * @return
	 */
	public static PaperRecord findLastPaperRecord(String paperId,String userName,boolean withItems)
	{
		Log.d(TAG,String.format("查询[paperId = %1$s,userName = %2$s]的最新考试记录", paperId,userName));
		SQLiteDatabase db = UserDBManager.openDatabase(userName);
		String sql = "select recordId,paperId,paperName,paperType,userId,userName,productId,terminalId,status,score,useTime,rightNum,createTime,lastTime,torf from PaperRecordTab where paperId = ? and userName = ?";
		String[] params = new String[] {paperId,userName};
		Cursor cursor = db.rawQuery(sql, params);
		if (cursor.getCount() == 0) {
			cursor.close();
			db.close();
			return null;
		}
		cursor.moveToNext();
		PaperRecord record = new PaperRecord(cursor.getString(0), cursor.getString(1),
					cursor.getString(2), cursor.getInt(3), cursor.getString(4),
					cursor.getString(5), cursor.getString(6),cursor.getString(7),
					cursor.getInt(8),cursor.getDouble(9),cursor.getInt(10),cursor.getInt(11),
					cursor.getString(12),cursor.getString(13),cursor.getString(14));
		cursor.close();
		//是否带上试题 
		if(withItems)
		{
//			/recordId,structureId,itemId,itemContent,answer,termialId,status,score,useTime,createTime
			record.setItems(findItemRecords(db, record.getRecordId()));
		}
		db.close();
		return record;
	}
	/**
	 * 更新考试记录
	 * @param record
	 */
	public static void updatePaperRecord(PaperRecord record){
		Log.d(TAG,"更新考试记录"); 
		SQLiteDatabase db = UserDBManager.openDatabase(record.getUserName());
		String sql = "update PaperRecordTab set score = ?,useTime=?,lasttime = datetime(?),status = ?,rightNum = ?,torf = ? where recordId = ? ";
		Object[] params = new Object[] { record.getScore(), record.getUsedTime(),
									record.getLastTime(),record.getStatus(),record.getRightNum(),record.getTorf(),record.getRecordId()};
		db.execSQL(sql, params);
		if(record.getItems()!=null && record.getItems().size()>0)
		{
			db.execSQL("delete from ItemRecordTab where recordId = ? "); //先删除原来的考试记录
			ArrayList<ItemRecord> list = record.getItems();
			String insertSql = "insert into ItemRecordTab(recordId,structureId,subjectId,username,itemId,itemContent,itemType,answer,termialId,status,score,createTime,lastTime)values(?,?,?,?,?,?,?,?,?,?,?,datetime(?),datetime(?))";
			for(ItemRecord item:list)
			{
				//插入试题的考试记录recordId ,structureId ,itemId ,itemContent ,answer ,termialId ,status ,score ,useTime ,createTime lastTime
				Object[] attrs = {item.getRecordId(),item.getStructureId(),item.getSubjectId(),item.getUserName(),item.getItemId(),item.getItemContent(),item.getItemType(),item.getAnswer(),AppConfig.TERMINALID,item.getStatus(),item.getScore().doubleValue(),item.getCreateTime(),item.getLastTime()};
				db.execSQL(insertSql, attrs);
			}
		}
		db.close();
	}
	
	private static void saveOrUpdateItem(SQLiteDatabase db,ItemRecord item)
	{
		Cursor cursor = db.rawQuery("select * from ItemRecordTab where itemId = ? and recordId = ?", new String[]{item.getItemId(),item.getRecordId()});
		if(cursor.getCount() > 0)
		{
			cursor.close();
			//更新
			db.execSQL("update ItemRecordTab set answer = ?,status = ?,score=?,lasttime = datetime(?) where itemId = ? and recordId = ?",
					new Object[]{item.getAnswer(),item.getStatus(),item.getScore().doubleValue(),item.getLastTime(),item.getItemId(),item.getRecordId()});
			return;
		}
		cursor.close();
		//插入
		db.execSQL("insert into ItemRecordTab(recordId,structureId,subjectId,username,itemId,itemContent,itemType,answer,termialId,status,score,createTime,lastTime)values(?,?,?,?,?,?,?,?,?,?,?,datetime(?),datetime(?))", 
				new Object[]{item.getRecordId(),item.getStructureId(),item.getSubjectId(),item.getUserName(),item.getItemId(),item.getItemContent(),item.getItemType(),item.getAnswer(),AppConfig.TERMINALID,item.getStatus(),item.getScore().doubleValue(),item.getCreateTime(),item.getLastTime()});
	}
	/**
	 * 插入或更新item
	 * @param item
	 */
	public static void saveOrUpdateItem(ItemRecord item)
	{
		if(item == null) return;
		SQLiteDatabase db = UserDBManager.openDatabase(item.getUserName());
		saveOrUpdateItem(db, item);
		db.close();
	}
	//查询总的个数
	public static int findRecordTotalOfUser(String username)
	{
		if(StringUtils.isEmpty(username))	return 0;
		SQLiteDatabase db = UserDBManager.openDatabase(username);
		Cursor cursor = db.rawQuery("select count(*) from PaperRecordTab where userName = ? ",  new String[] {username});
		int total = 0;
		while(cursor.moveToNext())
		{
			total = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return total;
	}
	//考试记录分页查询
	public static ArrayList<PaperRecord> findRecordsByUsername(String username,int page)
	{
		Log.d(TAG, "查询考试记录");
		if(StringUtils.isEmpty(username))	return null;
		SQLiteDatabase db = UserDBManager.openDatabase(username);
		/*
		 *  private String recordId,paperId,paperName,userId,userName,productId,terminalId,status;
			private BigDecimal score;
			private Integer usedTime,rightNum;
			private Date createTime,lastTime;
		 */
		String sql = "select recordId,paperId,paperName,paperType,userId,userName,productId,terminalId,status,score,useTime,rightNum,createTime,lastTime,torf from PaperRecordTab where userName = ? order by lastTime desc limit ? offset ?";
		String[] params = new String[] {username,PAGESIZE+"",page*PAGESIZE+""};
		Cursor cursor = db.rawQuery(sql, params);
		ArrayList<PaperRecord> list = new ArrayList<PaperRecord>();
		while(cursor.moveToNext())
		{
			PaperRecord record = new PaperRecord(cursor.getString(0), cursor.getString(1),
					cursor.getString(2), cursor.getInt(3), cursor.getString(4),
					cursor.getString(5), cursor.getString(6),cursor.getString(7),
					cursor.getInt(8),cursor.getDouble(9),cursor.getInt(10),cursor.getInt(11),
					cursor.getString(12),cursor.getString(13),cursor.getString(14));
			Log.d(TAG,record.getUsedTime()+"");
			list.add(record);
		}
		cursor.close();
		db.close();
		return list;
	}
	
	//科目总数
	public static ArrayList<Subject> getCount(ArrayList<Subject> subjects,String username)
	{
		if(username == null) return subjects;
		SQLiteDatabase db = UserDBManager.openDatabase(username);
		for(Subject subject:subjects)
		{
			subject.setTotal(getCount(db,subject.getSubjectId(),username,null));
		}
		db.close();
		return subjects;
	}
	
	private static int getCount(SQLiteDatabase db,String subjectId,String username,Integer type)
	{
		Cursor cursor = null;
		if(type == null)
			cursor = db.rawQuery("select count(distinct itemId) from ItemRecordTab where subjectId = ? and username = ? and status = ?", new String[]{subjectId,username,String.valueOf(AppConstant.ANSWER_WRONG)});
		else
			cursor = db.rawQuery("select count(distinct itemId) from ItemRecordTab where subjectId = ? and username = ? and status = ? and itemType = "+type, new String[]{subjectId,username,String.valueOf(AppConstant.ANSWER_WRONG)});
		cursor.moveToNext();
		int sum = cursor.getInt(0);
		cursor.close();
		return sum;
	}
	/**
	 * 构造一套试卷
	 * @param subjectId
	 * @param username
	 * @return
	 */
	public static SimplePaper loadErrorPaper(String subjectId,String username)
	{
		Log.d(TAG,"加载错题试卷");
		if(username == null || subjectId==null) return null;
		SQLiteDatabase db = UserDBManager.openDatabase(username);
		int total = getCount(db,subjectId,username,null);
		if(total == 0) return null;
		SimplePaper paper = new SimplePaper();
		Cursor cursor = db.rawQuery("select itemType from ItemRecordTab where subjectId = ? and username = ? and status = ? group by itemType order by itemType asc", new String[]{subjectId,username,String.valueOf(AppConstant.ANSWER_WRONG)});
		ArrayList<StructureInfo> structures = new ArrayList<StructureInfo>();
		Log.d(TAG,"加载错题试卷的大题");
		while(cursor.moveToNext())
		{
			int type = cursor.getInt(0);
			StructureInfo info = new StructureInfo();
			info.setType(type);
			info.setTitle(AppConstant.getItemTypeName(type));
			info.setTotal(getCount(db, subjectId, username, type));
			structures.add(info);
		}
		paper.setRuleList(structures);
		cursor.close();
		paper.setItems(loadErrorPaperItems(db, subjectId, username));
		db.close();
		return paper;
	}
	private static ArrayList<StructureItemInfo> loadErrorPaperItems(SQLiteDatabase db,String subjectId,String username)
	{
		Log.d(TAG,"加载错题试卷的题目的集合");
		Cursor cursor = db.rawQuery("select itemContent from ItemRecordTab where subjectId = ? and username = ? and status = ? group by itemId order by itemType asc", new String[]{subjectId,username,String.valueOf(AppConstant.ANSWER_WRONG)});
		ArrayList<StructureItemInfo> items = new ArrayList<StructureItemInfo>();
		while(cursor.moveToNext())
		{
			String content = cursor.getString(0);
			StructureItemInfo item = GsonUtil.jsonToBean(content, StructureItemInfo.class);
			item.setUserAnswer(null);
			item.setAnswerStatus(null);
			item.setIsCollected(FavoriteDao.isCollected(db, item.getId(), username));
			items.add(item);
		}
		cursor.close();
		return items;
	}
}
