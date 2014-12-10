package com.examw.test.dao;

import java.math.BigDecimal;
import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.examw.test.app.AppConfig;
import com.examw.test.db.UserDBUtil;
import com.examw.test.domain.ItemRecord;
import com.examw.test.domain.PaperRecord;

/**
 * 考试记录DAO
 * @author fengwei.
 * @since 2014年12月5日 上午11:31:59.
 */
public class PaperRecordDao {
	private static final String TAG = "PaperRecordDao";
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
		SQLiteDatabase db = UserDBUtil.getDatabase();
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
		SQLiteDatabase db = UserDBUtil.getDatabase();
		/*
		 *  private String recordId,paperId,paperName,userId,userName,productId,terminalId,status;
			private BigDecimal score;
			private Integer usedTime,rightNum;
			private Date createTime,lastTime;
		 */
		String sql = "select recordId,paperId,paperName,paperType,userId,userName,productId,terminalId,status,score,useTime,rightNum,createTime,lastTime,torf from PaperRecordTab order by lastTime desc ";
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
	public static ArrayList<PaperRecord> findAll(String userName)
	{
		return null;
	}
	/**
	 * 根据ID查询试卷记录
	 * @param recordId
	 * @return
	 */
	public static PaperRecord findById(String recordId)
	{
		Log.d(TAG,String.format("查询[recordId = %s]的记录", recordId));
		SQLiteDatabase db = UserDBUtil.getDatabase();
		/*
		 *  private String recordId,paperId,paperName,userId,userName,productId,terminalId,status;
			private BigDecimal score;
			private Integer usedTime,rightNum;
			private Date createTime,lastTime;
		 */
		String sql = "select recordId,paperId,paperName,paperType,userId,userName,productId,terminalId,status,score,useTime,rightNum,createTime,lastTime,torf from s where recordId = ? ";
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
	 * 查询试卷最近一次的考试记录
	 * @param paperId	试卷ID
	 * @param userName	用户名
	 * @param withItems	是否带上试题
	 * @return
	 */
	public static PaperRecord findLastPaperRecord(String paperId,String userName,boolean withItems)
	{
		Log.d(TAG,String.format("查询[paperId = %1$s,userName = %2$s]的最新考试记录", paperId,userName));
		SQLiteDatabase db = UserDBUtil.getDatabase();
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
			String sqlItemRecord = "select recordId,structureId,itemId,answer,status,score from ItemRecordTab where recordId = ? order by createTime desc";
			Cursor cursorItem = db.rawQuery(sqlItemRecord, new String[]{record.getRecordId()});
			if (cursorItem.getCount() == 0) {
				cursorItem.close();
			}else{
				ArrayList<ItemRecord> items = new ArrayList<ItemRecord>();
				while (cursorItem.moveToNext()) {
					ItemRecord itemRecord = new ItemRecord(cursorItem.getString(0), cursorItem.getString(1),
							cursorItem.getString(2), cursorItem.getString(3), cursorItem.getInt(4),
							new BigDecimal(cursorItem.getDouble(5)));
					items.add(itemRecord);
				}
				record.setItems(items);
			}
			
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
		SQLiteDatabase db = UserDBUtil.getDatabase();
		String sql = "update PaperRecordTab set score = ?,useTime=?,lasttime = datetime(?),status = ?,rightNum = ?,torf = ? where recordId = ? ";
		Object[] params = new Object[] { record.getScore(), record.getUsedTime(),
									record.getLastTime(),record.getStatus(),record.getRightNum(),record.getTorf(),record.getRecordId()};
		db.execSQL(sql, params);
		if(record.getItems()!=null && record.getItems().size()>0)
		{
			db.execSQL("delete from ItemRecordTab where recordId = ? "); //先删除原来的考试记录
			ArrayList<ItemRecord> list = record.getItems();
			String insertSql = "insert into ItemRecordTab(recordId,structureId,itemId,itemContent,answer,termialId,status,score,createTime,lastTime)values(?,?,?,?,?,?,?,?,datetime(?),datetime(?))";
			for(ItemRecord item:list)
			{
				//插入试题的考试记录recordId ,structureId ,itemId ,itemContent ,answer ,termialId ,status ,score ,useTime ,createTime lastTime
				Object[] attrs = {item.getRecordId(),item.getStructureId(),item.getItemId(),item.getItemContent(),item.getAnswer(),AppConfig.TERMINALID,item.getStatus(),item.getScore().doubleValue(),item.getCreateTime(),item.getLastTime()};
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
		db.execSQL("insert into ItemRecordTab(recordId,structureId,itemId,itemContent,answer,termialId,status,score,createTime,lastTime)values(?,?,?,?,?,?,?,?,datetime(?),datetime(?))", 
				new Object[]{item.getRecordId(),item.getStructureId(),item.getItemId(),item.getItemContent(),item.getAnswer(),AppConfig.TERMINALID,item.getStatus(),item.getScore().doubleValue(),item.getCreateTime(),item.getLastTime()});
	}
	/**
	 * 插入或更新item
	 * @param item
	 */
	public static void saveOrUpdateItem(ItemRecord item)
	{
		if(item == null) return;
		SQLiteDatabase db = UserDBUtil.getDatabase();
		saveOrUpdateItem(db, item);
		db.close();
	}
}
