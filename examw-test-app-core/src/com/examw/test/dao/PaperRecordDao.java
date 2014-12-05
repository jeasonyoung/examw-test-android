package com.examw.test.dao;

import java.util.ArrayList;
import java.util.UUID;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.examw.test.app.AppConfig;
import com.examw.test.db.LibraryDBUtil;
import com.examw.test.db.UserDBUtil;
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
	public static boolean saveOrUpdate(PaperRecord record)
	{
		/*
		 *  private String recordId,paperId,paperName,userId,userName,productId,terminalId,status;
			private BigDecimal score;
			private Integer usedTime,rightNum;
			private Date createTime,lastTime;
		 */
		if (record == null)	return false;
		SQLiteDatabase db = UserDBUtil.getDatabase();
		Cursor cursor = db
				.rawQuery("select * from PaperRecordTab where paperId = ? and userName = ?",
						new String[] { record.getPaperId(), record.getUserName() });
		if (cursor.getCount() > 0) {
			Log.d(TAG,"更新考试记录");
			cursor.close();
			String sql = "update PaperRecordTab set score = ?,useTime=?,lasttime = datetime(?),status = ?,rightNum = ? where paperid = ? and username = ? ";
			Object[] params = new Object[] { record.getScore(), record.getUsedTime(),
										record.getLastTime(),record.getStatus(),record.getRightNum()};
			db.execSQL(sql, params);
			db.close();
			return true;
		}
		Log.d(TAG,"插入考试记录");
		cursor.close();
		String sql = "insert into PaperRecordTab(recordId,paperId,paperName,paperType,userId,userName,productId,terminalId,status,score,useTime,rightNum) values(?,?,?,?,?,?,?,?,?,?,?,?)";
		Object[] params = new Object[] {
			UUID.randomUUID().toString(),record.getPaperId(),record.getPaperName(),record.getPaperType(),
			record.getUserId(),record.getUserName(),AppConfig.PRODUCTID,AppConfig.TERMINALID,record.getStatus(),0,0,0
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
		String sql = "select recordId,paperId,paperName,paperType,userId,userName,productId,terminalId,status,score,useTime,rightNum,createTime,lastTime from PaperRecordTab order by lastTime desc ";
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
					cursor.getString(12),cursor.getString(13));
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
		String sql = "select recordId,paperId,paperName,paperType,userId,userName,productId,terminalId,status,score,useTime,rightNum,createTime,lastTime from s where recordId = ? ";
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
					cursor.getString(12),cursor.getString(13));
		cursor.close();
		db.close();
		return record;
	}
	
	public static PaperRecord findLastPaperRecord(String paperId,String userName)
	{
		Log.d(TAG,String.format("查询[paperId = %1$s,userName = %2$s]的最新考试记录", paperId,userName));
		SQLiteDatabase db = LibraryDBUtil.getDatabase();
		String sql = "select recordId,paperId,paperName,paperType,userId,userName,productId,terminalId,status,score,useTime,rightNum,createTime,lastTime from PaperRecordTab where paperId = ? and userName = ?";
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
					cursor.getString(12),cursor.getString(13));
		cursor.close();
		db.close();
		return record;
	}
}
