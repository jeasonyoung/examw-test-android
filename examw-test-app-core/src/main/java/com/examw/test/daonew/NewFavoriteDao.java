package com.examw.test.daonew;

import java.util.ArrayList;
import java.util.UUID;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.examw.test.app.AppConstant;
import com.examw.test.db.UserDBManager;
import com.examw.test.domain.FavoriteItem;
import com.examw.test.domain.Subject;
import com.examw.test.model.SimplePaper;
import com.examw.test.model.StructureInfo;
import com.examw.test.model.StructureItemInfo;
import com.examw.test.util.CryptoUtils;
import com.examw.test.util.GsonUtil;
import com.examw.test.util.LogUtil;
import com.examw.test.util.StringUtils;

/**
 * 
 * @author fengwei.
 * @since 2015年3月18日 下午2:16:31.
 */
public class NewFavoriteDao {
	private static final String DIGEST_CODE = "F6A1V22O15R18I9T20E5";
	/**
	 * 收藏或取消收藏
	 * @param favor
	 */
	public static void favorOrCancel(FavoriteItem favor)
	{
		if(favor == null) return;
		if(favor.getUsername() == null) return;
		LogUtil.d("收藏或取消收藏");
		SQLiteDatabase db = UserDBManager.openDatabase(favor.getUsername());
		Cursor cursor = db.rawQuery("select status from tbl_favorites where itemId = ? and username = ?", new String[]{favor.getItemId(),favor.getUsername()});
		if(cursor.getCount() == 0) //还没有收藏
		{
			cursor.close();
			db.execSQL("insert into tbl_favorites(id,subjectCode,itemId,itemType,content,remarks,status,sync)",
					new Object[]{UUID.randomUUID().toString(),favor.getSubjectId(),favor.getItemId(),favor.getItemType(),
					//TODO 加密试题数据
					CryptoUtils.encrypto(DIGEST_CODE, favor.getItemContent()),favor.getRemarks(),
					AppConstant.STATUS_DONE,AppConstant.SYNC_DONE});
			db.close();
			return;
		}
		cursor.moveToNext();
		int status = cursor.getInt(0);
		if(AppConstant.STATUS_DONE.equals(status))
		{
			//已经收藏,取消收藏
			db.execSQL("update tbl_favorites set status = ? where itemId = ?", 
						new Object[]{AppConstant.STATUS_NONE,favor.getItemId()});
		}else
		{
			//收藏
			db.execSQL("update tbl_favorites set status = ? where itemId = ?", 
					new Object[]{AppConstant.STATUS_DONE,favor.getItemId()});
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
		Cursor cursor = db.rawQuery("select status from tbl_favorites where itemId = ?", new String[]{itemId});
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
		LogUtil.d("查询各个科目的收藏情况");
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
		{
			cursor = db.rawQuery("select count(distinct itemId) from tbl_favorites where subjectCode = ? and status = 1 ", new String[]{subjectId});
		}else{
			cursor = db.rawQuery("select count(distinct itemId) from tbl_favorites where subjectCode = ? and itemType = ?  and status = 1 ", new String[]{subjectId,String.valueOf(type)});
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
		LogUtil.d("查询单个科目的收藏试题");
		if(username == null || subjectId==null) return null;
		SQLiteDatabase db = UserDBManager.openDatabase(username);
		int total = getCount(db,subjectId,null);
		if(total == 0) return null;
		SimplePaper paper = new SimplePaper();
		Cursor cursor = db.rawQuery("select itemType from tbl_favorites where subjectCode = ? and status = 1 group by itemType order by itemType asc", new String[]{subjectId});
		ArrayList<StructureInfo> structures = new ArrayList<StructureInfo>();
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
		paper.setItems(loadFavoritePaperItems(db, subjectId, username));
		db.close();
		return paper;
	}
	private static ArrayList<StructureItemInfo> loadFavoritePaperItems(SQLiteDatabase db,String subjectId,String username)
	{
		Cursor cursor = db.rawQuery("select content from tbl_favorites where subjectCode = ? and status = 1 order by itemType asc", new String[]{subjectId});
		ArrayList<StructureItemInfo> items = new ArrayList<StructureItemInfo>();
		while(cursor.moveToNext())
		{
			String content = cursor.getString(0);
			//TODO 解密试题数据
			content = CryptoUtils.decrypto(DIGEST_CODE, content);
			StructureItemInfo item = GsonUtil.jsonToBean(content, StructureItemInfo.class);
			item.setUserAnswer(null);
			item.setAnswerStatus(null);
			item.setIsCollected(true);
			items.add(item);
		}
		cursor.close();
		return items;
	}
	
	/**
	 * 查询需要同步的数据
	 * @param username
	 * @param userId
	 * @return
	 */
	public static ArrayList<FavoriteItem> findNeedSyncFavorites(String username,String userId)
	{
		if(username == null || userId == null) return null;
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
		Cursor cursor = db.rawQuery("select id,subjectCode,itemId,itemType,content,remarks,status,sync from tbl_favorites where sync = 0",new String[]{});
		ArrayList<FavoriteItem> list = new  ArrayList<FavoriteItem>();
		while(cursor.moveToNext())
		{
			FavoriteItem item = new FavoriteItem();
			//item.setId(cursor.getString(0));
			item.setSubjectId(cursor.getString(1));
			item.setItemId(cursor.getString(2));
			item.setItemType(cursor.getInt(3));
			item.setRemarks(cursor.getString(4));
			item.setStatus(cursor.getInt(5));
			//item.setSync(cursor.getInt(6));
			item.setUsername(username);
			list.add(item);
		}
		cursor.close();
		db.close();
		return list;
	}
	/**
	 * 真正删除
	 * @param username
	 */
	public static void deleteTruely(String username)
	{
		LogUtil.d("查询单个科目的收藏试题");
		if(StringUtils.isEmpty(username)) return;
		SQLiteDatabase db = UserDBManager.openDatabase(username);
		db.beginTransaction();
		db.execSQL("delete from tbl_favorites where status = 0");
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}
}
