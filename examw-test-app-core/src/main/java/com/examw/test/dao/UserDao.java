package com.examw.test.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;

import com.examw.test.db.UserDBUtil;
import com.examw.test.domain.User;
import com.examw.test.util.LogUtil;

/**
 * 用户数据DAO
 * @author fengwei.
 * @since 2014年12月1日 下午4:07:28.
 */
public class UserDao {
	/**
	 * 增加用户
	 * @param user
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static long addUser(User user) throws IllegalArgumentException, IllegalAccessException
	{
		long i = 0;
		SQLiteDatabase db = UserDBUtil.getDatabase();
		LogUtil.d( "addUser方法打开了数据库连接");
		db.beginTransaction();
		try{
			String sql = "insert into UserTab(uid,username,password,info,productUserId)values(?,?,?,?,?)";
			String pwd = new String(Base64.encode(Base64.encode(user.getPassword().getBytes(), 0), 0));
			Object[] values = new Object[] { user.getUid(),
					user.getUsername(), pwd,user.getInfo(),user.getProductUserId() };
			db.execSQL(sql, values);
			db.setTransactionSuccessful();
		}finally
		{
			db.endTransaction();
			UserDBUtil.close();
		}
		return i;
	}
	
	/**
	 * 根据username查找用户
	 * @param username
	 * @return
	 */
	public static User findByUsername(String username)
	{
		User user = null;
		Cursor cursor = null;
		try{
			SQLiteDatabase db = UserDBUtil.getDatabase();
			LogUtil.d( "findByUsername方法打开了数据库连接");
			cursor = db.rawQuery("select uid,username,password,productUserId from UserTab where username = ?", new String[]{username});
			if(cursor.moveToNext())
			{
				user = new User();
				user.setUid(cursor.getString(0));
				user.setUsername(cursor.getString(1));
				user.setPassword(cursor.getString(2));
				user.setProductUserId(cursor.getString(3));
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			if(cursor != null)
				cursor.close();
			UserDBUtil.close();
		}
		return user;
	}
	
	public static void saveOrUpdate(User user) throws IllegalArgumentException, IllegalAccessException
	{
		User user1 = null;
		SQLiteDatabase db = UserDBUtil.getDatabase();
		Cursor cursor= db.rawQuery("select uid,username,password,productUserId from UserTab where username = ?", new String[]{user.getUsername()});
		if(cursor.moveToNext())
		{
			user1 = new User();
			user1.setUid(cursor.getString(0));
			user1.setUsername(cursor.getString(1));
			user1.setPassword(cursor.getString(2));
			user1.setProductUserId(cursor.getString(3));
		}
		cursor.close();
		String pwd = new String(Base64.encode(Base64.encode(user.getPassword().getBytes(), 0), 0));
		if(user1 == null)
		{
			//插入
			db.beginTransaction();
			try{
				String sql1 = "insert into UserTab(uid,username,password,info,productUserId)values(?,?,?,?,?)";
				Object[] values = new Object[] { user.getUid(),
						user.getUsername(), pwd,user.getInfo(),user.getProductUserId() };
				db.execSQL(sql1, values);
				db.setTransactionSuccessful();
			}finally
			{
				db.endTransaction();
			}
		}else
		{
			//更新
			if(!user1.getPassword().equals(pwd))
			{
				String sql = "update UserTab set uid = ?,password = ?,productUserId = ? where username = ?";
				db.execSQL(sql, new Object[] {user.getUid(),pwd,user.getProductUserId(),user.getUsername()});
			}
		}
		UserDBUtil.close();
	}
	/**
	 * 更新上次同步的时间
	 * @param username
	 * @param lastTime
	 */
	public static void updateLastTime(String username,String lastTime,String column)
	{
		LogUtil.d(String.format("更新[%1$s]的时间[%2$s]", column,lastTime));
		SQLiteDatabase db = UserDBUtil.getDatabase();
		String sql = "update UserTab set "+column+" = datetime(?) where username = ?";
		db.execSQL(sql, new Object[] {lastTime,username});
		db.close();
		return;
	}
	/**
	 * 获取时间
	 * @param username
	 * @param column
	 * @return
	 */
	public static String getLastTime(String username,String column)
	{
		SQLiteDatabase db = UserDBUtil.getDatabase();
		String sql = "select "+column+" from UserTab where username = ?";
		Cursor cursor = db.rawQuery(sql, new String[] {username});
		String time = null;
		if(cursor.moveToNext())
		{
			time = cursor.getString(0);
		}
		cursor.close();
		db.close();
		LogUtil.d(String.format("查询[%1$s]的时间是[%2$s]", column,time));
		return time;
	}
}
