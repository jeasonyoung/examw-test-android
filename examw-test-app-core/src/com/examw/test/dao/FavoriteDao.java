package com.examw.test.dao;

import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.examw.test.app.AppConfig;
import com.examw.test.app.AppConstant;
import com.examw.test.db.UserDBUtil;
import com.examw.test.domain.FavoriteItem;
import com.examw.test.domain.Subject;

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
		Log.d(TAG,"收藏或取消收藏");
		SQLiteDatabase db = UserDBUtil.getDatabase();
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
		SQLiteDatabase db = UserDBUtil.getDatabase();
		Cursor cursor = db.rawQuery("select status from FavoriteTab where itemId = ? and username = ?", new String[]{itemId,username});
		if(cursor.getCount() == 0)
		{
			cursor.close();
			db.close();
			return null;
		}
		cursor.moveToNext();
		int status = cursor.getInt(0);
		cursor.close();
		db.close();
		return AppConstant.STATUS_DONE.equals(status);
	}
	public static ArrayList<Subject> getCount(ArrayList<Subject> subjects,String username)
	{
		if(username == null) return subjects;
		SQLiteDatabase db = UserDBUtil.getDatabase();
		for(Subject subject:subjects)
		{
			subject.setTotal(getCount(db,subject.getSubjectId(),username));
		}
		db.close();
		return subjects;
	}
	
	private static int getCount(SQLiteDatabase db,String subjectId,String username)
	{
		Cursor cursor = db.rawQuery("select count(distinct itemId) from FavoriteTab where subjectId = ? and username = ?", new String[]{subjectId,username});
		cursor.moveToNext();
		int sum = cursor.getInt(0);
		cursor.close();
		return sum;
	}
}
