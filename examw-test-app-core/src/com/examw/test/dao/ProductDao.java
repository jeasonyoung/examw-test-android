package com.examw.test.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.examw.test.app.AppConfig;
import com.examw.test.db.LibraryDBUtil;
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
		db.execSQL("insert into ProductTab(productid,name,info)values(?,?,?)", new Object[]{product.getId(),product.getName(),product.getInfo()});
		//插科目
		db.execSQL("delete from SubjectTab");
		String[] subjectIds = product.getSubjectId();
		if(subjectIds != null && subjectIds.length > 0)
		{
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
}
