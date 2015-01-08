package com.examw.test.dao;

import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.examw.test.app.AppConfig;
import com.examw.test.db.LibraryDBUtil;
import com.examw.test.domain.Subject;
import com.examw.test.model.FrontProductInfo;

/**
 * 
 * @author fengwei.
 * @since 2014年12月3日 下午4:14:06.
 */
public class ProductDao {
	private static final String TAG = "ProductDao";
	public static boolean hasInsert(){
		Log.d(TAG,"查询产品信息是否存在");
		SQLiteDatabase db = LibraryDBUtil.getDatabase();
		Cursor cursor = db.rawQuery("select * from ProductTab where productid = ?",
				new String[] { AppConfig.PRODUCTID });
		boolean flag = cursor.getCount() > 0;
		cursor.close();
		LibraryDBUtil.close();
		return flag;
	}
	
	public static void insert(FrontProductInfo product){
		if(product == null) return;
		Log.d(TAG,"插入产品信息");
		SQLiteDatabase db = LibraryDBUtil.getDatabase();
		db.beginTransaction();
		//插产品
		db.execSQL("insert into ProductTab(productid,name,examName,info)values(?,?,?,?)", new Object[]{product.getId(),product.getName(),product.getExamName(),product.getInfo()});
		//插科目
		db.execSQL("delete from SubjectTab");
		String[] subjectIds = product.getSubjectId();
		if(subjectIds != null && subjectIds.length > 0)
		{
			Log.d(TAG,"插入科目信息");
			String[] subjectNames = product.getSubjectName();
			for(int i=0;i<subjectIds.length;i++)
			{
				db.execSQL("insert into SubjectTab(subjectId,name,orderno)values(?,?,?)", new Object[]{subjectIds[i],subjectNames[i],i});
			}
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}
	public static String findExamName()
	{
		SQLiteDatabase db = LibraryDBUtil.getDatabase();
		Cursor cursor = db.rawQuery("select examName from ProductTab where productid = ?",
				new String[] { AppConfig.PRODUCTID });
		String name = "";
		if(cursor.moveToNext())
		{
			name = cursor.getString(0);
		}
		cursor.close();
		db.close();
		return name;
	}
	/**
	 * 查询科目信息
	 * @return
	 */
	public static ArrayList<Subject> findSubjects()
	{
		Log.d(TAG,"查询科目信息");
		SQLiteDatabase db = LibraryDBUtil.getDatabase();
		Cursor cursor = db.rawQuery("select subjectid,name,orderno from SubjectTab order by orderno asc", new String[]{});
		if(cursor.getCount() == 0)
		{
			cursor.close();
			db.close();
			return null;
		}
		ArrayList<Subject> list = new ArrayList<Subject>();
		while (cursor.moveToNext()) {
			Subject p = new Subject(cursor.getString(0), cursor.getString(1),cursor.getInt(2));
			list.add(p);
		}
		cursor.close();
		db.close();
		return list;
	}

	public static void saveSubjects(ArrayList<Subject> result) {
		if(result == null || result.size() == 0) return;
		Log.d(TAG,"插入科目信息");
		SQLiteDatabase db = LibraryDBUtil.getDatabase();
		db.beginTransaction();
		db.execSQL("delete from SubjectTab");
		for(int i=0;i<result.size();i++)
		{
			Subject info = result.get(i);
			db.execSQL("insert into SubjectTab(subjectId,name,orderno)values(?,?,?)", new Object[]{info.getSubjectId(),info.getName(),i});
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}
	
}
