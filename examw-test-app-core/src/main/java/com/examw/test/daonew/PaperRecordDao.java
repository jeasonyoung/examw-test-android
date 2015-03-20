package com.examw.test.daonew;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.UUID;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.examw.test.app.AppConfig;
import com.examw.test.app.AppConstant;
import com.examw.test.db.UserDBManager;
import com.examw.test.domain.ItemRecord;
import com.examw.test.domain.PaperRecord;
import com.examw.test.domain.Subject;
import com.examw.test.model.SimplePaper;
import com.examw.test.model.StructureInfo;
import com.examw.test.model.StructureItemInfo;
import com.examw.test.model.sync.PaperItemRecordSync;
import com.examw.test.model.sync.PaperRecordSync;
import com.examw.test.util.CyptoUtils;
import com.examw.test.util.GsonUtil;
import com.examw.test.util.LogUtil;
import com.examw.test.util.StringUtils;

/**
 * 考试记录DAO
 * @author fengwei.
 * @since 2014年12月5日 上午11:31:59.
 */
public class PaperRecordDao {
	public static final int PAGESIZE = 20;
	private static final String DIGEST_CODE = "I9T20E5M13C3O15D4E5";
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
		LogUtil.d("插入考试记录");
		String sql = "insert into PaperRecordTab(recordId,paperId,paperName,paperType,status,score,useTime,rightNum,torf,sync) values(?,?,?,?,?,?,?,?,?,0)";
		Object[] params = new Object[] {
			record.getRecordId(),record.getPaperId(),record.getPaperName(),record.getPaperType(),
			record.getStatus(),0,0,0,record.getTorf()
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
		LogUtil.d(String.format("查询用户[userName = %s]的记录", userName));
		SQLiteDatabase db = UserDBManager.openDatabase(userName);
		/*
		 *  private String recordId,paperId,paperName,userId,userName,productId,terminalId,status;
			private BigDecimal score;
			private Integer usedTime,rightNum;
			private Date createTime,lastTime;
		 */
		String sql = "select recordId,paperId,paperName,paperType,status,score,useTime,rightNum,createTime,lastTime,torf from PaperRecordTab order by lastTime desc ";
		Cursor cursor = db.rawQuery(sql, new String[] {});
		if (cursor.getCount() == 0) {
			cursor.close();
			db.close();
			return null;
		}
		cursor.moveToNext();
		PaperRecord record = new PaperRecord(cursor.getString(0), cursor.getString(1),
					cursor.getString(2), cursor.getInt(3),
					cursor.getInt(4),cursor.getDouble(5),cursor.getInt(6),cursor.getInt(7),
					cursor.getString(8),cursor.getString(9),cursor.getString(10));
		record.setUserName(userName);
		cursor.close();
		db.close();
		return record;
	}
	public static PaperRecord findLastRecord(String userName,String types)
	{
		LogUtil.d(String.format("查询用户[userName = %s]的记录", userName));
		SQLiteDatabase db = UserDBManager.openDatabase(userName);
		/*
		 *  private String recordId,paperId,paperName,userId,userName,productId,terminalId,status;
			private BigDecimal score;
			private Integer usedTime,rightNum;
			private Date createTime,lastTime;
		 */
		String sql = "select recordId,paperId,paperName,paperType,status,score,useTime,rightNum,createTime,lastTime,torf from PaperRecordTab where paperType in("+types+") order by lastTime desc ";
		String[] params = new String[] {};
		Cursor cursor = db.rawQuery(sql, params);
		if (cursor.getCount() == 0) {
			cursor.close();
			db.close();
			return null;
		}
		cursor.moveToNext();
		PaperRecord record =  new PaperRecord(cursor.getString(0), cursor.getString(1),
				cursor.getString(2), cursor.getInt(3),
				cursor.getInt(4),cursor.getDouble(5),cursor.getInt(6),cursor.getInt(7),
				cursor.getString(8),cursor.getString(9),cursor.getString(10));
		record.setUserName(userName);
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
		LogUtil.d(String.format("查询用户[%1$s][%2$s]的全部记录", userName,userId));
		if(userName == null || userId == null) return null;
		if(StringUtils.isEmpty(lastTime)) lastTime = "1970-01-01 00:00:00";
		LogUtil.d(String.format("上次的同步时间为[%s]", lastTime));
		SQLiteDatabase db = UserDBManager.openDatabase(userName);
		ArrayList<PaperRecord> list = new ArrayList<PaperRecord>();
		Cursor cursor = db.rawQuery("select recordId,paperId,paperName,paperType,status,score,useTime,rightNum,createTime,lastTime,torf from PaperRecordTab where status = 1 and createTime > ? ", 
				new String[]{lastTime});
		while(cursor.moveToNext())
		{
			PaperRecord record =  new PaperRecord(cursor.getString(0), cursor.getString(1),
					cursor.getString(2), cursor.getInt(3),
					cursor.getInt(4),cursor.getDouble(5),cursor.getInt(6),cursor.getInt(7),
					cursor.getString(8),cursor.getString(9),cursor.getString(10));;
			record.setUserId(userId);
			record.setUserName(userName);
			record.setItems(findItemRecords(db,record.getRecordId()));
			list.add(record);
		}
		cursor.close();
		db.close();
		return list;
	}
	/**
	 * 根据ID查询试卷记录
	 * @param recordId
	 * @return
	 */
	public static PaperRecord findById(String userName,String recordId,boolean withItems)
	{
		LogUtil.d(String.format("查询[recordId = %s]的记录", recordId));
		SQLiteDatabase db = UserDBManager.openDatabase(userName);
		/*
		 *  private String recordId,paperId,paperName,userId,userName,productId,terminalId,status;
			private BigDecimal score;
			private Integer usedTime,rightNum;
			private Date createTime,lastTime;
		 */
		String sql = "select recordId,paperId,paperName,paperType,status,score,useTime,rightNum,createTime,lastTime,torf from PaperRecordTab where recordId = ? ";
		String[] params = new String[] {recordId};
		Cursor cursor = db.rawQuery(sql, params);
		if (cursor.getCount() == 0) {
			cursor.close();
			db.close();
			return null;
		}
		cursor.moveToNext();
		PaperRecord record =  new PaperRecord(cursor.getString(0), cursor.getString(1),
				cursor.getString(2), cursor.getInt(3),
				cursor.getInt(4),cursor.getDouble(5),cursor.getInt(6),cursor.getInt(7),
				cursor.getString(8),cursor.getString(9),cursor.getString(10));
		record.setUserName(userName);
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
		String sqlItemRecord = "select recordId,structureId,itemId,answer,status,score,itemContent from ItemRecordTab where recordId = ? order by createTime desc";
		Cursor cursorItem = db.rawQuery(sqlItemRecord, new String[]{recordId});
		ArrayList<ItemRecord> items = new ArrayList<ItemRecord>();
		if (cursorItem.getCount() > 0) {
			while (cursorItem.moveToNext())
			{
				ItemRecord itemRecord = new ItemRecord(cursorItem.getString(0), cursorItem.getString(1),
						cursorItem.getString(2), cursorItem.getString(3), cursorItem.getInt(4),
						new BigDecimal(cursorItem.getDouble(5)));
				itemRecord.setItemContent(CyptoUtils.decodeContent(DIGEST_CODE, cursorItem.getString(6)));
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
		LogUtil.d(String.format("查询[paperId = %1$s,userName = %2$s]的最新考试记录", paperId,userName));
		SQLiteDatabase db = UserDBManager.openDatabase(userName);
		String sql = "select recordId,paperId,paperName,paperType,status,score,useTime,rightNum,createTime,lastTime,torf from PaperRecordTab where paperId = ?";
		String[] params = new String[] {paperId};
		Cursor cursor = db.rawQuery(sql, params);
		if (cursor.getCount() == 0) {
			cursor.close();
			db.close();
			return null;
		}
		cursor.moveToNext();
		PaperRecord record =  new PaperRecord(cursor.getString(0), cursor.getString(1),
				cursor.getString(2), cursor.getInt(3),
				cursor.getInt(4),cursor.getDouble(5),cursor.getInt(6),cursor.getInt(7),
				cursor.getString(8),cursor.getString(9),cursor.getString(10));;
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
		LogUtil.d("更新考试记录"); 
		SQLiteDatabase db = UserDBManager.openDatabase(record.getUserName());
		String sql = "update PaperRecordTab set score = ?,useTime=?,lasttime = datetime(?),status = ?,rightNum = ?,torf = ?,sync = 0 where recordId = ? ";
		Object[] params = new Object[] { record.getScore(), record.getUsedTime(),
									record.getLastTime(),record.getStatus(),record.getRightNum(),record.getTorf(),record.getRecordId()};
		db.execSQL(sql, params);
		if(record.getItems()!=null && record.getItems().size()>0)
		{
			db.execSQL("delete from ItemRecordTab where recordId = ? "); //先删除原来的考试记录
			ArrayList<ItemRecord> list = record.getItems();
			String insertSql = "insert into ItemRecordTab(id,recordId,structureId,subjectId,itemId,itemContent,itemType,answer,status,score,createTime,lastTime,sync)values(?,?,?,?,?,?,?,?,?,?,datetime(?),datetime(?),0)";
			for(ItemRecord item:list)
			{
				//插入试题的考试记录recordId ,structureId ,itemId ,itemContent ,answer ,termialId ,status ,score ,useTime ,createTime lastTime
				Object[] attrs = {UUID.randomUUID().toString(),item.getRecordId(),item.getStructureId(),item.getSubjectId(),item.getItemId(),CyptoUtils.encodeContent(DIGEST_CODE, item.getItemContent()),item.getItemType(),item.getAnswer(),item.getStatus(),item.getScore().doubleValue(),item.getCreateTime(),item.getLastTime()};
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
			db.execSQL("update ItemRecordTab set sync = 0,answer = ?,status = ?,score=?,lasttime = datetime(?) where itemId = ? and recordId = ?",
					new Object[]{item.getAnswer(),item.getStatus(),item.getScore().doubleValue(),item.getLastTime(),item.getItemId(),item.getRecordId()});
			return;
		}
		cursor.close();
		//插入
		db.execSQL("insert into ItemRecordTab(id, recordId,structureId,subjectId,username,itemId,itemContent,itemType,answer,status,score,createTime,lastTime,sync)values(?,?,?,?,?,?,?,?,?,?,?,datetime(?),datetime(?),0)", 
				new Object[]{UUID.randomUUID().toString(),item.getRecordId(),item.getStructureId(),item.getSubjectId(),item.getUserName(),item.getItemId(),CyptoUtils.encodeContent(DIGEST_CODE, item.getItemContent()),item.getItemType(),item.getAnswer(),item.getStatus(),item.getScore().doubleValue(),item.getCreateTime(),item.getLastTime()});
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
		Cursor cursor = db.rawQuery("select count(*) from PaperRecordTab",  new String[] {});
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
		LogUtil.d( "查询考试记录");
		if(StringUtils.isEmpty(username))	return null;
		SQLiteDatabase db = UserDBManager.openDatabase(username);
		/*
		 *  private String recordId,paperId,paperName,userId,userName,productId,terminalId,status;
			private BigDecimal score;
			private Integer usedTime,rightNum;
			private Date createTime,lastTime;
		 */
		String sql = "select recordId,paperId,paperName,paperType,status,score,useTime,rightNum,createTime,lastTime,torf from PaperRecordTab order by lastTime desc limit ? offset ?";
		String[] params = new String[] {PAGESIZE+"",page*PAGESIZE+""};
		Cursor cursor = db.rawQuery(sql, params);
		ArrayList<PaperRecord> list = new ArrayList<PaperRecord>();
		while(cursor.moveToNext())
		{
			PaperRecord record =  new PaperRecord(cursor.getString(0), cursor.getString(1),
					cursor.getString(2), cursor.getInt(3),
					cursor.getInt(4),cursor.getDouble(5),cursor.getInt(6),cursor.getInt(7),
					cursor.getString(8),cursor.getString(9),cursor.getString(10));
			record.setUserName(username);
			LogUtil.d(record.getUsedTime()+"");
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
			subject.setTotal(getCount(db,subject.getSubjectId(),null));
		}
		db.close();
		return subjects;
	}
	
	private static int getCount(SQLiteDatabase db,String subjectId,Integer type)
	{
		Cursor cursor = null;
		if(type == null)
			cursor = db.rawQuery("select count(distinct itemId) from ItemRecordTab where subjectId = ? and status = ?", new String[]{subjectId,String.valueOf(AppConstant.ANSWER_WRONG)});
		else
			cursor = db.rawQuery("select count(distinct itemId) from ItemRecordTab where subjectId = ? and status = ? and itemType = "+type, new String[]{subjectId,String.valueOf(AppConstant.ANSWER_WRONG)});
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
		LogUtil.d("加载错题试卷");
		if(username == null || subjectId==null) return null;
		SQLiteDatabase db = UserDBManager.openDatabase(username);
		int total = getCount(db,subjectId,null);
		if(total == 0) return null;
		SimplePaper paper = new SimplePaper();
		Cursor cursor = db.rawQuery("select itemType from ItemRecordTab where subjectId = ? and status = ? group by itemType order by itemType asc", new String[]{subjectId,String.valueOf(AppConstant.ANSWER_WRONG)});
		ArrayList<StructureInfo> structures = new ArrayList<StructureInfo>();
		LogUtil.d("加载错题试卷的大题");
		while(cursor.moveToNext())
		{
			int type = cursor.getInt(0);
			StructureInfo info = new StructureInfo();
			info.setType(type);
			info.setTitle(AppConstant.getItemTypeName(type));
			info.setTotal(getCount(db, subjectId, type));
			structures.add(info);
		}
		paper.setRuleList(structures);
		cursor.close();
		paper.setItems(loadErrorPaperItems(db, subjectId));
		db.close();
		return paper;
	}
	private static ArrayList<StructureItemInfo> loadErrorPaperItems(SQLiteDatabase db,String subjectId)
	{
		LogUtil.d("加载错题试卷的题目的集合");
		Cursor cursor = db.rawQuery("select itemContent from ItemRecordTab where subjectId = ? and status = ? group by itemId order by itemType asc", 
				new String[]{subjectId,String.valueOf(AppConstant.ANSWER_WRONG)});
		ArrayList<StructureItemInfo> items = new ArrayList<StructureItemInfo>();
		while(cursor.moveToNext())
		{
			String content = cursor.getString(0);
			content = CyptoUtils.decodeContent(DIGEST_CODE, content);
			StructureItemInfo item = GsonUtil.jsonToBean(content, StructureItemInfo.class);
			item.setUserAnswer(null);
			item.setAnswerStatus(null);
			item.setIsCollected(FavoriteDao.isCollected(db, item.getId()));
			items.add(item);
		}
		cursor.close();
		return items;
	}
	
	/**
	 * 查询需要同步的数据
	 * @throws ParseException 
	 */
	public static ArrayList<PaperRecordSync> findSyncPaperRecords(String username) throws ParseException
	{
		LogUtil.d("查询需要同步的试卷数据");
		if(username == null) return null;
		SQLiteDatabase db = UserDBManager.openDatabase(username);
		ArrayList<PaperRecordSync> list = new ArrayList<PaperRecordSync>();
		String sql = "select recordId,paperId,status,score,useTime,rightNum,createTime,lastTime from PaperRecordTab where sync = 0 order by lastTime desc ";
		Cursor cursor = db.rawQuery(sql, new String[] {});
		if (cursor.getCount() == 0) {
			cursor.close();
			db.close();
			return null;
		}
		while(cursor.moveToNext())
		{
			PaperRecordSync record = new PaperRecordSync();
			record.setId(cursor.getString(0));
			record.setPaperId(cursor.getString(1));
			record.setStatus(cursor.getInt(2));
			record.setScore(new BigDecimal(cursor.getDouble(3)));
			record.setUseTimes(cursor.getInt(4));
			record.setRights(cursor.getInt(5));
			record.setCreateTime(cursor.getString(6));
			record.setLastTime(cursor.getString(7));
			list.add(record);
		}
		cursor.close();
		db.close();
		return list;
	}
	public static void updateRecords(String username)
	{
		if(username == null) return;
		SQLiteDatabase db = UserDBManager.openDatabase(username);
		db.beginTransaction();
		db.execSQL("update PaperRecordTab set sync = 1 ");
		db.execSQL("update ItemRecordTab set sync = 1 ");
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}
	public static ArrayList<PaperItemRecordSync> findSyncItemRecords(String username) throws ParseException
	{
		LogUtil.d("查询需要同步的试题数据");
		if(username == null) return null;
		SQLiteDatabase db = UserDBManager.openDatabase(username);
		ArrayList<PaperItemRecordSync> list = new ArrayList<PaperItemRecordSync>();
		String sql = "select id,recordId,structureId,itemId,itemContent,answer,status,score,useTime,createTime,lastTime from ItemRecordTab where sync = 0 order by lastTime desc ";
		Cursor cursor = db.rawQuery(sql, new String[] {});
		if (cursor.getCount() == 0) {
			cursor.close();
			db.close();
			return null;
		}
		while(cursor.moveToNext())
		{
			PaperItemRecordSync record = new PaperItemRecordSync();
			record.setId(cursor.getString(0));
			record.setPaperRecordId(cursor.getString(1));
			record.setStructureId(cursor.getString(2));
			record.setItemId(cursor.getString(3));
			//解密数据
			record.setContent(CyptoUtils.decodeContent(DIGEST_CODE, cursor.getString(4)));
			record.setAnswer(cursor.getString(5));
			record.setStatus(cursor.getInt(6));
			record.setScore(new BigDecimal(cursor.getDouble(7)));
			record.setUseTimes(cursor.getInt(8));
			record.setCreateTime(cursor.getString(9));
			record.setLastTime(cursor.getString(10));
			list.add(record);
		}
		cursor.close();
		db.close();
		return list;
	}
}
