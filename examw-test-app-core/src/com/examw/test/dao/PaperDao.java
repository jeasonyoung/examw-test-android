package com.examw.test.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.examw.test.db.DbHelpers;
import com.examw.test.model.sync.SubjectSync;

/**
 * 试卷数据Dao
 * 
 * @author jeasonyoung
 * @since 2015年7月1日
 */
public class PaperDao {
	private static final String TAG = "PaperDao";
	private Context context;
	private DbHelpers dbHelpers;
	/**
	 * 构造函数。
	 * @param context
	 */
	public PaperDao(Context context){
		Log.d(TAG, "初始化...");
		if(context == null){
			Log.d(TAG, "上下文为空!");
			throw  new IllegalArgumentException("context");
		}
		this.context = context;
	}
	//获取数据操作工具对象。
	private DbHelpers getDbHelpers(){
		//惰性初始化数据操作工具
		if(this.dbHelpers == null){
			Log.d(TAG, "初始化");
			this.dbHelpers = new DbHelpers(this.context);
		}
		return this.dbHelpers;
	}
	/**
	 * 统计科目试卷。
	 * @return
	 */
	public List<SubjectTotalModel> totalSubjects(){
		Log.d(TAG, "统计科目试卷...");
		List<SubjectTotalModel> list = new ArrayList<PaperDao.SubjectTotalModel>();
		SQLiteDatabase db = null;
		try {
			final String sql = "SELECT a.code,a.name,COUNT(b.id) AS total FROM tbl_subjects a LEFT OUTER JOIN tbl_papers b ON b.subjectCode = a.code WHERE a.status = 1 GROUP BY a.code,a.name";
			db =  this.getDbHelpers().getWritableDatabase();
			Log.d(TAG, "exec-sql:" + sql);
			Cursor cursor =  db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				//初始化数据模型
				SubjectTotalModel data = new SubjectTotalModel();
				data.setCode(cursor.getString(0));
				data.setName(cursor.getString(1));
				data.setTotal(cursor.getInt(2));
				list.add(data);
			}
			cursor.close();
		} catch (Exception e) {
			 Log.e(TAG, "统计科目试卷发生异常:" + e.getMessage(), e);
		}finally{
			if(db != null){
				db.close();
			}
		}
		return list;
	}
	
	/**
	 * 科目试卷统计。
	 * @author jeasonyoung
	 * @since 2015年7月1日
	 */
	public class SubjectTotalModel extends SubjectSync{
		private static final long serialVersionUID = 1L;
		private int total;
		/**
		 * 获取试卷统计。
		 * @return 试卷统计。
		 */
		public int getTotal() {
			return total;
		}
		/**
		 * 设置试卷统计。
		 * @param total 
		 *	  试卷统计。
		 */
		public void setTotal(int total) {
			this.total = total;
		}
		
	}

}