package com.examw.test.dao;

import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.examw.test.app.AppConfig;
import com.examw.test.app.AppConstant;
import com.examw.test.db.UserDBManager;
import com.examw.test.domain.FavoriteItem;
import com.examw.test.domain.Subject;
import com.examw.test.model.SimplePaper;
import com.examw.test.model.StructureInfo;
import com.examw.test.model.StructureItemInfo;
import com.examw.test.util.GsonUtil;

/**
 * 收藏DAO
 * @author fengwei.
 * @since 2014年12月11日 下午2:37:25.
 */
public class FavoriteDao {
	private static final String TAG = "FavoriteDao";
	
	/**
	 * 收藏或取消收藏
	 * @param favor
	 */
	public static void favorOrCancel(FavoriteItem favor)
	{
		if(favor == null) return;
		if(favor.getUsername() == null) return;
		Log.d(TAG,"收藏或取消收藏");
		SQLiteDatabase db = UserDBManager.openDatabase(favor.getUsername());
		Cursor cursor = db.rawQuery("select status from FavoriteTab where itemId = ? and username = ?", new String[]{favor.getItemId(),favor.getUsername()});
		if(cursor.getCount() == 0) //还没有收藏
		{
			cursor.close();
			db.execSQL("insert into FavoriteTab(itemId,subjectId,itemType,itemContent,username,userId,userAnswer,remarks,terminalId,status)values(?,?,?,?,?,?,?,?,?,?)",
					new Object[]{favor.getItemId(),favor.getSubjectId(),favor.getItemType(),favor.getItemContent(),favor.getUsername()
					,favor.getUserId(),favor.getUserAnswer(),favor.getRemarks(),AppConfig.TERMINALID,AppConstant.STATUS_DONE});
			db.close();
			return;
		}
		cursor.moveToNext();
		int status = cursor.getInt(0);
		if(AppConstant.STATUS_DONE.equals(status))
		{
			//已经收藏,取消收藏
			db.execSQL("update FavoriteTab set status = ? where itemId = ? and username = ?", 
						new Object[]{AppConstant.STATUS_NONE,favor.getItemId(),favor.getUsername()});
		}else
		{
			//收藏
			db.execSQL("update FavoriteTab set status = ? where itemId = ? and username = ?", 
					new Object[]{AppConstant.STATUS_DONE,favor.getItemId(),favor.getUsername()});
		}
		cursor.close();
		db.close();
	}
	/**
	 * 判断是否收藏
	 * @param itemId	题目ID
	 * @param username	用户名
	 * @return
	 */
	public static Boolean isCollected(String itemId,String username)
	{
		if(username == null) return null;
		SQLiteDatabase db = UserDBManager.openDatabase(username);
		Boolean status = isCollected(db,itemId,username);
		db.close();
		return status;
	}
	public static Boolean isCollected(SQLiteDatabase db,String itemId,String username)
	{
		Cursor cursor = db.rawQuery("select status from FavoriteTab where itemId = ? and username = ?", new String[]{itemId,username});
		if(cursor.getCount() == 0)
		{
			cursor.close();
			return null;
		}
		cursor.moveToNext();
		int status = cursor.getInt(0);
		cursor.close();
		return AppConstant.STATUS_DONE.equals(status);
	}
	public static ArrayList<Subject> getCount(ArrayList<Subject> subjects,String username)
	{
		Log.d(TAG,"查询各个科目的收藏情况");
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
		{
			cursor = db.rawQuery("select count(distinct itemId) from FavoriteTab where subjectId = ? and username = ?  and status = 1 ", new String[]{subjectId,username});
		}else{
			cursor = db.rawQuery("select count(distinct itemId) from FavoriteTab where subjectId = ? and username = ? and itemType = ?  and status = 1 ", new String[]{subjectId,username,String.valueOf(type)});
		}
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
	public static SimplePaper loadFavoritePaper(String subjectId,String username)
	{
		Log.d(TAG,"查询单个科目的收藏试题");
		if(username == null || subjectId==null) return null;
		SQLiteDatabase db = UserDBManager.openDatabase(username);
		int total = getCount(db,subjectId,username,null);
		if(total == 0) return null;
		SimplePaper paper = new SimplePaper();
		Cursor cursor = db.rawQuery("select itemType from FavoriteTab where subjectId = ? and username = ?  and status = 1 group by itemType order by itemType asc", new String[]{subjectId,username});
		ArrayList<StructureInfo> structures = new ArrayList<StructureInfo>();
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
		paper.setItems(loadFavoritePaperItems(db, subjectId, username));
		db.close();
		return paper;
	}
	private static ArrayList<StructureItemInfo> loadFavoritePaperItems(SQLiteDatabase db,String subjectId,String username)
	{
		Cursor cursor = db.rawQuery("select itemContent from FavoriteTab where subjectId = ? and username = ? and status = 1 order by itemType asc", new String[]{subjectId,username});
		ArrayList<StructureItemInfo> items = new ArrayList<StructureItemInfo>();
		while(cursor.moveToNext())
		{
			String content = cursor.getString(0);
			StructureItemInfo item = GsonUtil.jsonToBean(content, StructureItemInfo.class);
			item.setUserAnswer(null);
			item.setAnswerStatus(null);
			item.setIsCollected(true);
			items.add(item);
		}
		cursor.close();
		return items;
	}
	
	public static ArrayList<FavoriteItem> findAll(String username,String userId)
	{
		if(username == null || userId == null) return null;
		String time = UserDao.getLastTime(username, "lastSyncFavorTime");
		if(time == null) time = "1970-01-01 00:00:00";
		SQLiteDatabase db = UserDBManager.openDatabase(username);
		/**
		 * this.itemId = itemId;
		this.username = username;
		this.itemContent = itemContent;
		this.subjectId = subjectId;
		this.userAnswer = userAnswer;
		this.remarks = remarks;
		this.createTime = createTime;
		this.itemType = itemType;
		 */
		Cursor cursor = db.rawQuery("select itemId,username,itemContent,subjectId,userAnswer,remarks,createTime,itemType from FavoriteTab where status = 1 and createTime > ? order by createTime desc", new String[]{time});
		ArrayList<FavoriteItem> list = new  ArrayList<FavoriteItem>();
		while(cursor.moveToNext())
		{
			FavoriteItem item = new FavoriteItem(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),
					cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getInt(7));
			list.add(item);
		}
		cursor.close();
		db.close();
		return list;
	}
}
