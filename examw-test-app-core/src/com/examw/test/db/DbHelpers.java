package com.examw.test.db;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.examw.test.app.AppContext;
import com.examw.test.utils.FileUtils;

/**
 * 数据操作工具类。
 * 
 * @author jeasonyoung
 * @since 2015年6月25日
 */
public class DbHelpers implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final String TAG = "DbHelpers";
	private static final String dbName = "test.db";
	private static final int dbVersion = 1;
	private MySQLiteHelper sqliteHelper;
	/**
	 * 构造函数。
	 * @param context
	 */
	public DbHelpers(Context context){
		this.sqliteHelper = new MySQLiteHelper(new DatabaseContext(context), dbName, null, dbVersion);
	}
	
	/**
	 * 获取可写数据库对象。
	 * @return 可写数据库对象。
	 */
	public SQLiteDatabase getWritableDatabase(){
		return this.sqliteHelper.getWritableDatabase();
	}
	/**
	 * 获取只读数据库对象。
	 * @return 只读数据库对象。
	 */
	public SQLiteDatabase getReadableDatabase() {
		return this.sqliteHelper.getReadableDatabase();
	}
	/**
	 * 数据库上下文包装类。
	 * 
	 * @author jeasonyoung
	 * @since 2015年6月25日
	 */
	private class DatabaseContext extends ContextWrapper{
		private Context mContext;
		private String username,productId;
		
		private static final String no_login = "_nologin_";//未登录用户名
		private static final String dbName_format = "exm_$%1$s$%2$s";//数据库名称格式
		/**
		 * 构造函数。
		 * @param base
		 */
		public DatabaseContext(Context base) {
			super(base);
			Log.d(TAG, "初始化数据库上下文包装类....");
			this.mContext = base;
			AppContext appContext = (AppContext)this.mContext.getApplicationContext();
			if(appContext != null){
				Log.d(TAG, "加载当前数据...");
				//当前产品
				if(appContext.getCurrentSettings() != null){
					this.productId = appContext.getCurrentSettings().getProductId();
					Log.d(TAG, "加载当前产品ID:" + this.productId);
				}
				//当前用户
				if(appContext.getCurrentUser() != null){
					this.username = appContext.getCurrentUser().getUsername();
					Log.d(TAG, "加载当前用户:" + this.username);
				}
			}
		}
		/*
		 * 获取数据库路径，如果不存在，则创建对象。
		 * @see android.content.ContextWrapper#getDatabasePath(java.lang.String)
		 */
		@Override
		public File getDatabasePath(String name) {
			Log.d(TAG, "开始获取数据库文件路径...");
			//用户键
			String userKey = StringUtils.isNotBlank(this.username) ?  DigestUtils.md5Hex(this.username) : no_login;
			//db文件名
			String dbName = String.format(dbName_format, userKey, StringUtils.trimToEmpty(this.productId))  + "_" + name;
			//root
			String root = this.mContext.getFilesDir().getAbsolutePath();
			if(AppContext.hasExistSDCard()){
				//存在SD卡上
				root = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + this.mContext.getPackageName();
			}
			//判断目录是否存在，不存在则创建目录
			File dirFile = new File(root);
			if(!dirFile.exists()){
				dirFile.mkdirs();
			}
			//数据库文件全路径
			File path = new File(root, dbName);
			//判断数据库文件是否存在
			if(!path.exists()){
				//数据库文件不存在
				if(StringUtils.isNotBlank(this.username)){
					//检查未登录的数据库文件是否存在
					String nologDbName = String.format(dbName_format, no_login, StringUtils.trimToEmpty(this.productId))  + "_" + name;
					//未登录的数据库文件路径
					File nologinDbPath = new File(root, nologDbName);
					if(nologinDbPath.exists()){
						//复制未登录的数据库文件
						FileUtils.copyFile(nologinDbPath, path, true);
						return path;
					}
				}
				//创建新文件
				try {
					Log.d(TAG, "创建数据文件:" + path.getAbsolutePath());
					path.createNewFile();
				} catch (IOException e) {
					Log.e(TAG, "创建数据库文件异常:" + e.getMessage(), e);
				}
			}
			return path;
		}
		/*
		 * 重载用来打开SD卡上的数据库(android 2.3及以下会调用这个方法)。
		 * @see android.content.ContextWrapper#openOrCreateDatabase(java.lang.String, int, android.database.sqlite.SQLiteDatabase.CursorFactory)
		 */
		@Override
		public SQLiteDatabase openOrCreateDatabase(String name, int mode, CursorFactory factory) {
			Log.d(TAG, "创建数据库 sdk <2.3...");
			return SQLiteDatabase.openOrCreateDatabase(this.getDatabasePath(name), factory);
		}
		/*
		 * 重载用来打开SD卡上的数据库(android 4.0会调用这个方法)。
		 * @see android.content.ContextWrapper#openOrCreateDatabase(java.lang.String, int, android.database.sqlite.SQLiteDatabase.CursorFactory, android.database.DatabaseErrorHandler)
		 */
		@Override
		public SQLiteDatabase openOrCreateDatabase(String name, int mode, CursorFactory factory, DatabaseErrorHandler errorHandler) {
			Log.d(TAG, "创建数据库 sdk 4.0+ ..");
			return SQLiteDatabase.openOrCreateDatabase(this.getDatabasePath(name), factory);
		}
	}
	/**
	 * 内置SQLite帮助类。
	 * 
	 * @author jeasonyoung
	 * @since 2015年6月25日
	 */
	private class MySQLiteHelper extends SQLiteOpenHelper{
		/**
		 * 构造函数。
		 * @param context
		 * @param name
		 * @param factory
		 * @param version
		 */
		public MySQLiteHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}
		/*
		 * 重载创建。
		 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.d(TAG, "开始创建数据库表结构...");
			String[] sqlArrays = this.CreateDbTableSql();
			if(ArrayUtils.isNotEmpty(sqlArrays)){
				for(String sql : sqlArrays){
					if(StringUtils.isBlank(sql)) continue;
					try{
						Log.d(TAG, "执行create-SQL:" + sql);
						db.execSQL(sql);
					}catch(Exception e){
						Log.e(TAG, "执行脚本发生异常:" + e.getMessage(), e);
					}
				}
			}
		}
		//创建数据库表结构SQL。
		private String[] CreateDbTableSql(){
			Log.d(TAG, "创建数据库表结构SQL...");
			return new String[]{
					//1.创建科目表[status(0-不可用,1-可用)]
					"CREATE TABLE tbl_subjects(code TEXT,name TEXT,status INTEGER DEFAULT 1,examCode INTEGER DEFAULT 0, CONSTRAINT PK_tbl_subjects PRIMARY KEY(code,examCode));",
					//2.创建试卷表
					"CREATE TABLE tbl_papers(id TEXT PRIMARY KEY,title TEXT,type INTEGER DEFAULT 0,total INTEGER DEFAULT 0,content TEXT,createTime TIMESTAMP DEFAULT (datetime('now', 'localtime')),subjectCode TEXT);",
					//3.创建试题收藏表[status(0-删除，1-收藏)]
					"CREATE TABLE tbl_favorites(id TEXT PRIMARY KEY,subjectCode TEXT,itemId TEXT,itemType INTEGER DEFAULT 0,content TEXT,status INTEGER DEFAULT 1,createTime TIMESTAMP DEFAULT (datetime('now', 'localtime')),sync INTEGER DEFAULT 0);",
					//4.创建做卷记录表[status(0-未做完，1-已做完)][sync(0-未同步,1-已同步)]
					"CREATE TABLE tbl_paperRecords(id TEXT PRIMARY KEY,paperId TEXT,status INTEGER DEFAULT 0,score FLOAT DEFAULT 0,rights INTEGER DEFAULT 0,useTimes INTEGER DEFAULT 0,createTime TIMESTAMP DEFAULT (datetime('now', 'localtime')),lastTime TIMESTAMP DEFAULT (datetime('now', 'localtime')),sync INTEGER DEFAULT 0);",
					//5.创建做题记录表[status(0-错误，1-正确)]
					"CREATE TABLE tbl_itemRecords(id TEXT PRIMARY KEY,paperRecordId TEXT,structureId TEXT,itemId TEXT,itemType INTEGER DEFAULT 0,content TEXT,answer TEXT,status INTEGER DEFAULT 0,score FLOAT DEFAULT 0,useTimes INTEGER DEFAULT 0,createTime TIMESTAMP DEFAULT (datetime('now', 'localtime')),lastTime TIMESTAMP DEFAULT (datetime('now', 'localtime')),sync INTEGER DEFAULT 0);"
			};
		}
		/*
		 * 重载更新。
		 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			
		}
	}
}