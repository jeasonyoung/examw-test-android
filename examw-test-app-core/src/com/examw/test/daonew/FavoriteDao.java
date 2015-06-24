package com.examw.test.daonew;

import java.text.ParseException;
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
import com.examw.test.model.sync.FavoriteSync;
import com.examw.test.util.CyptoUtils;
import com.examw.test.util.GsonUtil;
import com.examw.test.util.LogUtil;
import com.examw.test.util.StringUtils;

/**
 * 收藏DAO
 * 
 * @author fengwei.
 * @since 2014年12月11日 下午2:37:25.
 */
public class FavoriteDao {
	private static final String DIGEST_CODE = "F6A1V22O15R18I9T20E5";

	/**
	 * 收藏或取消收藏
	 * 
	 * @param favor
	 */
	public static void favorOrCancel(FavoriteItem favor) {
		if (favor == null)
			return;
		if (favor.getUsername() == null)
			return;
		LogUtil.d("收藏或取消收藏");
		SQLiteDatabase db = UserDBManager.openDatabase(favor.getUsername());
		Cursor cursor = db.rawQuery("select status from FavoriteTab where itemId = ? ", new String[] { favor.getItemId()});
		if (cursor.getCount() == 0) // 还没有收藏
		{
			cursor.close();
			db.execSQL("insert into FavoriteTab(id,itemId,subjectId,itemType,itemContent,userAnswer,remarks,status,sync)values(?,?,?,?,?,?,?,?,0)", 
					new Object[] {UUID.randomUUID().toString(),favor.getItemId(), favor.getSubjectId(), favor.getItemType(), 
					CyptoUtils.encodeContent(DIGEST_CODE, favor.getItemContent()), 
					favor.getUserAnswer(), favor.getRemarks(), AppConstant.STATUS_DONE });
			db.close();
			return;
		}
		cursor.moveToNext();
		int status = cursor.getInt(0);
		if (AppConstant.STATUS_DONE.equals(status)) {
			// 已经收藏,取消收藏
			db.execSQL("update FavoriteTab set status = ?, sync = 0 where itemId = ?", new Object[] { AppConstant.STATUS_NONE, favor.getItemId()});
		} else {
			// 收藏
			db.execSQL("update FavoriteTab set status = ?, sync = 0 where itemId = ?", new Object[] { AppConstant.STATUS_DONE, favor.getItemId()});
		}
		cursor.close();
		db.close();
	}

	/**
	 * 判断是否收藏
	 * 
	 * @param itemId
	 *            题目ID
	 * @param username
	 *            用户名
	 * @return
	 */
	public static Boolean isCollected(String itemId, String username) {
		if (username == null)
			return null;
		SQLiteDatabase db = UserDBManager.openDatabase(username);
		Boolean status = isCollected(db, itemId);
		db.close();
		return status;
	}

	public static Boolean isCollected(SQLiteDatabase db, String itemId) {
		Cursor cursor = db.rawQuery("select status from FavoriteTab where itemId = ?", new String[] { itemId});
		if (cursor.getCount() == 0) {
			cursor.close();
			return null;
		}
		cursor.moveToNext();
		int status = cursor.getInt(0);
		cursor.close();
		return AppConstant.STATUS_DONE.equals(status);
	}

	public static ArrayList<Subject> getCount(ArrayList<Subject> subjects, String username) {
		LogUtil.d("查询各个科目的收藏情况");
		if (username == null)
			return subjects;
		SQLiteDatabase db = UserDBManager.openDatabase(username);
		for (Subject subject : subjects) {
			subject.setTotal(getCount(db, subject.getSubjectId(), null));
		}
		db.close();
		return subjects;
	}

	private static int getCount(SQLiteDatabase db, String subjectId, Integer type) {
		Cursor cursor = null;
		if (type == null) {
			cursor = db.rawQuery("select count(distinct itemId) from FavoriteTab where subjectId = ? and status = 1 ", new String[] { subjectId });
		} else {
			cursor = db.rawQuery("select count(distinct itemId) from FavoriteTab where subjectId = ? and itemType = ? and status = 1 ", new String[] { subjectId, String.valueOf(type) });
		}
		cursor.moveToNext();
		int sum = cursor.getInt(0);
		cursor.close();
		return sum;
	}

	/**
	 * 构造一套试卷
	 * 
	 * @param subjectId
	 * @param username
	 * @return
	 */
	public static SimplePaper loadFavoritePaper(String subjectId, String username) {
		LogUtil.d("查询单个科目的收藏试题");
		if (username == null || subjectId == null)
			return null;
		SQLiteDatabase db = UserDBManager.openDatabase(username);
		int total = getCount(db, subjectId, null);
		if (total == 0)
			return null;
		SimplePaper paper = new SimplePaper();
		Cursor cursor = db.rawQuery("select itemType from FavoriteTab where subjectId = ? and status = 1 group by itemType order by itemType asc", new String[] { subjectId });
		ArrayList<StructureInfo> structures = new ArrayList<StructureInfo>();
		while (cursor.moveToNext()) {
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

	private static ArrayList<StructureItemInfo> loadFavoritePaperItems(SQLiteDatabase db, String subjectId, String username) {
		Cursor cursor = db.rawQuery("select itemContent from FavoriteTab where subjectId = ? and status = 1 order by itemType asc", new String[] { subjectId });
		ArrayList<StructureItemInfo> items = new ArrayList<StructureItemInfo>();
		while (cursor.moveToNext()) {
			String content = cursor.getString(0);
			content = CyptoUtils.decodeContent(DIGEST_CODE, content);
			StructureItemInfo item = GsonUtil.jsonToBean(content, StructureItemInfo.class);
			item.setUserAnswer(null);
			item.setAnswerStatus(null);
			item.setIsCollected(true);
			items.add(item);
		}
		cursor.close();
		return items;
	}

	public static ArrayList<FavoriteItem> findAll(String username, String userId) {
		if (username == null || userId == null)
			return null;
		String time = UserDao.getLastTime(username, "lastSyncFavorTime");
		if (time == null)
			time = "1970-01-01 00:00:00";
		SQLiteDatabase db = UserDBManager.openDatabase(username);
		/**
		 * this.itemId = itemId; this.username = username; this.itemContent =
		 * itemContent; this.subjectId = subjectId; this.userAnswer =
		 * userAnswer; this.remarks = remarks; this.createTime = createTime;
		 * this.itemType = itemType;
		 */
		Cursor cursor = db.rawQuery("select itemId,username,itemContent,subjectId,userAnswer,remarks,createTime,itemType from FavoriteTab where status = 1 and createTime > ? order by createTime desc", new String[] { time });
		ArrayList<FavoriteItem> list = new ArrayList<FavoriteItem>();
		while (cursor.moveToNext()) {
			FavoriteItem item = new FavoriteItem(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getInt(7));
			item.setUserId(userId);
			item.setItemContent(CyptoUtils.decodeContent(DIGEST_CODE, item.getItemContent()));
			list.add(item);
		}
		cursor.close();
		db.close();
		return list;
	}
	
	/**
	 * 查询需要上传的数据
	 * @return
	 * @throws ParseException 
	 */
	public static ArrayList<FavoriteSync> findFavorites(String username) throws ParseException
	{
		SQLiteDatabase db = UserDBManager.openDatabase(username);
		Cursor cursor = db.rawQuery("select itemId,itemType,remarks,subjectId,status,itemContent,createTime from FavoriteTab where status = 1 and sync = 0", new String[] {});
		ArrayList<FavoriteSync> list = new ArrayList<FavoriteSync>();
		while (cursor.moveToNext()) {
			FavoriteSync item = new FavoriteSync();
			item.setItemId(cursor.getString(0));
			item.setItemType(cursor.getInt(1));
			item.setRemarks(cursor.getString(2));
			item.setSubjectId(cursor.getString(3));
			item.setStatus(cursor.getInt(4));
			item.setContent(CyptoUtils.decodeContent(DIGEST_CODE, cursor.getString(5)));
			item.setCreateTime(cursor.getString(6));
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
		db.execSQL("update FavoriteTab set sync = 1");
		db.execSQL("delete from FavoriteTab where status = 0");
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}
}