package com.examw.test.daonew;

import java.util.ArrayList;
import java.util.Set;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.examw.test.db.LibraryDBUtil;
import com.examw.test.domain.Subject;
import com.examw.test.model.sync.ExamSync;
import com.examw.test.model.sync.SubjectSync;
import com.examw.test.util.LogUtil;

/**
 * 
 * @author fengwei.
 * @since 2015年3月14日 下午3:08:57.
 */
public class ExamDao {
	/**
	 * 查询考试名称
	 * @return
	 */
	public static String findExamName()
	{
		SQLiteDatabase db = LibraryDBUtil.getDatabase();
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
	public static ArrayList<Subject> findSubjects()
	{
		LogUtil.d("查询科目信息");
		SQLiteDatabase db = LibraryDBUtil.getDatabase();
		Cursor cursor = db.rawQuery("select code,name from tbl_subjects order by code asc", new String[]{});
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
	 * 插入考试科目信息
	 * @param result
	 */
	public static void saveSubjects(ExamSync result) {
		if(result == null) return;
		Set<SubjectSync> subjects = result.getSubjects();
		if(subjects == null || subjects.size()==0) return;
		LogUtil.d("插入考试");
		SQLiteDatabase db = LibraryDBUtil.getDatabase();
		db.beginTransaction();
		db.execSQL("delete from tbl_exams");
		db.execSQL("insert into tbl_exams(code,name,abbr,status)values(?,?,?,1)", 
				new Object[]{result.getCode(),result.getName(),result.getAbbr()});
		db.execSQL("delete from tbl_subjects");
		for(SubjectSync s:subjects)
		{
			db.execSQL("insert into tbl_subjects(code,name,status,examCode)values(?,?,1,?)", new Object[]{s.getCode(),s.getName(),result.getCode()});
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}
}
