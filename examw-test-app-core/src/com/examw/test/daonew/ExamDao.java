package com.examw.test.daonew;

import java.util.ArrayList;
import java.util.Set;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.examw.test.db.LibraryDBUtil;
import com.examw.test.db.UserDBManager;
import com.examw.test.domain.Subject;
import com.examw.test.model.sync.ExamSync;
import com.examw.test.model.sync.SubjectSync;
import com.examw.test.utils.StringUtils;

/**
 * 考试Dao
 * @author fengwei.
 * @since 2015年3月14日 下午3:08:57.
 */
public class ExamDao {
	/**
	 * 查询考试名称
	 * @return
	 */
	public static String findExamName(String username)
	{
		SQLiteDatabase db = null;
		if(username != null)
			db = UserDBManager.openDatabase(username);
		else
			db =  LibraryDBUtil.getDatabase();
		Cursor cursor = db.rawQuery("select name from tbl_exams where status = 1",new String[]{});
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
	public static ArrayList<Subject> findSubjects(String username)
	{
		//LogUtil.d("查询科目信息");
		SQLiteDatabase db = null;
		if(username != null)
			db = UserDBManager.openDatabase(username);
		else
			db =  LibraryDBUtil.getDatabase();
		Cursor cursor = db.rawQuery("select code,name from tbl_subjects where status = 1 order by code asc", new String[]{});
		if(cursor.getCount() == 0)
		{
			cursor.close();
			db.close();
			return null;
		}
		ArrayList<Subject> list = new ArrayList<Subject>();
		while (cursor.moveToNext()) {
			Subject p = new Subject(cursor.getString(0), cursor.getString(1));
			list.add(p);
		}
		cursor.close();
		db.close();
		return list;
	}
	/**
	 * 插入考试,科目信息
	 * @param result
	 */
	public static void saveSubjects(ExamSync result,String username) {
		if(result == null) return;
		Set<SubjectSync> subjects = result.getSubjects();
		if(subjects == null || subjects.size()==0) return;
		//LogUtil.d("插入考试");
		SQLiteDatabase db = UserDBManager.openDatabase(username);
		db.beginTransaction();
		db.execSQL("update tbl_exams set status = 0");
		db.execSQL("update tbl_subjects set status = 0");
		//查询考试
		Cursor cursor = db.rawQuery("select name from tbl_exams where code = ?",new String[]{result.getCode()});
		String name = "";
		if(cursor.moveToNext())
		{
			name = cursor.getString(0);
		}
		cursor.close();
		//没有考试插入
		if(StringUtils.isEmpty(name))
		{
			db.execSQL("insert into tbl_exams(code,name,abbr,status)values(?,?,?,1)", 
					new Object[]{result.getCode(),result.getName(),result.getAbbr()});
			//插入考试
			for(SubjectSync s:subjects)
			{
				db.execSQL("insert into tbl_subjects(code,name,status,examCode)values(?,?,1,?)", new Object[]{s.getCode(),s.getName(),result.getCode()});
			}
		}else
		{
			//修改状态
			db.execSQL("update tbl_exams set name = ?,abbr = ?,status = 1 where code = ?",
					new Object[]{result.getCode()});
			//修改科目
			for(SubjectSync s:subjects)
			{
				Cursor cursor1 = db.rawQuery("select name from tbl_subjects where code = ?",new String[]{s.getCode()});
				String name1 = "";
				if(cursor.moveToNext())
				{
					name1 = cursor.getString(0);
				}
				cursor1.close();
				if(StringUtils.isEmpty(name1))
				{
					db.execSQL("insert into tbl_subjects(code,name,status,examCode)values(?,?,1,?)", 
							new Object[]{s.getCode(),s.getName(),result.getCode()});
				}else
					db.execSQL("update tbl_subjects set name = ?,examCode = ?,status = 1 where code = ?", 
							new Object[]{s.getName(),result.getCode(),s.getCode()});
			}
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}
}
