package com.examw.test.dao;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.examw.test.app.AppConstant;
import com.examw.test.app.AppContext;
import com.examw.test.db.DbHelpers;
import com.examw.test.model.sync.AppClientSync;
import com.examw.test.model.sync.ExamSync;
import com.examw.test.model.sync.JSONCallback;
import com.examw.test.model.sync.PaperSync;
import com.examw.test.model.sync.SubjectSync;
import com.examw.test.utils.DigestClientUtil;
import com.examw.test.utils.PaperUtils;

/**
 * 下载服务器数据。
 * 
 * @author jeasonyoung
 * @since 2015年6月27日
 */
public class DownloadDao implements Serializable{
	private static final long serialVersionUID = 1L;
	private static final String TAG = "DownloadDao";
	private Context context;
	private DbHelpers dbHelpers;
	/**
	 * 构造函数。
	 * @param context
	 */
	public DownloadDao(Context context){
		Log.d(TAG, "初始化...");
		if(context == null){
			Log.d(TAG, "上下文为空!");
			throw  new IllegalArgumentException("context");
		}
		this.context = context;
	}
	
	/**
	 * 下载数据处理。
	 * @param ignoreCode
	 * 忽略注册码。
	 * @param handler
	 * 下载处理。
	 */
	public void download(boolean ignoreCode, final DownloadResultListener handler){
		Log.d(TAG, "下载服务器数据...");
		//获取全局应用上下文。
		AppContext appContext = (AppContext)this.context.getApplicationContext();
		if(appContext == null){
			Log.d(TAG, "获取全局应用上下文失败!");
			if(handler != null){
				handler.onComplete(false, "初始化失败!");
			}
			return;
		}
		//1.检查网络
		if(!appContext.hasNetworkConnected()){
			Log.d(TAG, "网络不存在!");
			if(handler != null){
				handler.onComplete(false, "请检查网络!");
			}
			return;
		}
		//2.初始化请求参数
		final AppClientSync clientSync = new AppClientSync(this.context);
		clientSync.setIgnoreCode(ignoreCode);
		Log.d(TAG, "初始化请求参数...");
		//3.下载考试科目
		this.downloadSubject(clientSync, new DownloadResultListener(){
			/*
			 * 重载下载处理。
			 * @see com.examw.test.dao.DownloadDao.DownloadResultListener#onComplete(boolean, java.lang.String)
			 */
			@Override
			public void onComplete(boolean result, String msg) {
				if(!result){
					Log.d(TAG, "下载考试科目数据失败...");
					if(handler != null){
						handler.onComplete(result, msg);
					}
					return;
				}
				//4.下载试卷
				downloadPapers(clientSync, handler);
			}
		});
	}
	
	/**
	 * POST请求数据。
	 * @param url
	 * @param reqParameter
	 * @return
	 * @throws Exception
	 */
	private static String postReqData(String url, AppClientSync reqParameter) throws Exception{
		Log.d(TAG, "POST请求数据:" + url);
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-type","application/json;charset=UTF-8");
		
		String result = DigestClientUtil.sendDigestRequest(AppConstant.APP_API_USERNAME, AppConstant.APP_API_PASSWORD, headers,
				"POST", url, reqParameter.toString());
		Log.d(TAG, "服务器反馈数据:" + result);
		return StringUtils.isBlank(result) ? null : result;
	}
	
	//下载考试科目数据。
	private void downloadSubject(AppClientSync reqParameter, DownloadResultListener handler){
		try {
			Log.d(TAG, "开始下载考试科目数据...");
			String result = postReqData(AppConstant.APP_API_SUBJECTS_URL, reqParameter);
			if(StringUtils.isBlank(result)){
				Log.d(TAG, "反馈数据为空!");
				if(handler != null){
					handler.onComplete(false, "服务器未响应!");
				}
				return;
			}
			//初始化反馈数据模型
			JSONCallback<ExamSync> callback =  new JSONCallback<ExamSync>(result);
			if(!callback.getSuccess()){
				Log.d(TAG, "服务器发生异常:" + callback.getMsg());
				if(handler != null){
					handler.onComplete(false, callback.getMsg());
				}
				return;
			}
			ExamSync data = callback.getData();
			if(data == null || data.getSubjects() == null || data.getSubjects().size() == 0){
				Log.d(TAG, "未获取考试科目数据!");
				if(handler != null){
					handler.onComplete(false, "未获取考试科目数据!");
				}
				return;
			}
			//科目数据存储
			this.updateSubjectToDb(data);
			//更新完成
			if(handler != null){
				handler.onComplete(true, null);
			}
		} catch (Exception e) {
			Log.e(TAG, "下载科目数据异常:" + e.getMessage(), e);
			if(handler != null){
				handler.onComplete(false, "下载科目数据失败!");
			}
		}
	}
	//更新考试科目数据
	private void updateSubjectToDb(ExamSync examSync){
		//初始化数据操作对象。
		SQLiteDatabase db = null;
		 try {
			 Log.d(TAG, "开始更新科目数据...");
			//初始化数据帮助类。
			if(this.dbHelpers == null){
				this.dbHelpers = new DbHelpers(this.context);
			}
			 db =  this.dbHelpers.getWritableDatabase();
			 //重置已有科目状态
			 db.execSQL("update tbl_subjects set status = 0 ");
			 //科目数据更新
			 Log.d(TAG, "需要更新科目数据:" + examSync.getSubjects().size());
			 //循环更新科目数据
			 for(SubjectSync subject : examSync.getSubjects()){
				 if(subject == null) continue;
				 this.updateSubject(db, examSync.getCode(), subject);
			 }
		} catch (Exception e) {
			 Log.e(TAG, "更新考试科目数据异常:" + e.getMessage(), e);
		}finally{
			//关闭数据操作
			if(db != null){
				db.close();
			}
		}
	}
	//更新科目数据
	private void updateSubject(SQLiteDatabase db,String examCode, SubjectSync subject){
		try {
			Log.d(TAG, "开始更新科目:" + subject);
			if(db == null || subject == null){
				Log.d(TAG, "数据操作对象或科目数据为null...");
				return;
			}
			if(StringUtils.isBlank(examCode)){
				Log.d(TAG, "考试代码为空!");
				return;
			}
			if(StringUtils.isBlank(subject.getCode())){
				Log.d(TAG, "考试科目代码为空!");
				return;
			}
			
			//查询数据是否存在
			final String query_sql = "SELECT COUNT(*) FROM tbl_subjects WHERE code = ? AND examCode = ?";
			
			//1.查询数据是否存在
			boolean isExsits = false;
			Cursor cursor = db.rawQuery(query_sql, new String[]{subject.getCode(), examCode});
			while(cursor.moveToNext()){
				isExsits = (cursor.getInt(0) > 0);
				break;
			}
			cursor.close();
			//
			final String tableName = "tbl_subjects";
			int result = 0;
			//2.数据存在则更新，不存在则插入
			if(isExsits){
				//2.1存在则更新
				ContentValues values = new ContentValues();
				values.put("name", subject.getName());
				values.put("status", true);
				//更新考试科目
				result = db.update(tableName, values, "code = ? AND examCode = ?", new String[]{subject.getCode(), examCode});
				//
				Log.d(TAG, "更新科目数据:" + result);
			}else {
				//2.2不存在则插入
				ContentValues values = new ContentValues();
				values.put("code", subject.getCode());
				values.put("name", subject.getName());
				values.put("status", true);
				values.put("examCode", examCode);
				//插入考试科目
				result = (int)db.insert(tableName, null, values);
				//
				Log.d(TAG, "插入科目数据:" + result);
			}
		} catch (Exception e) {
			Log.d(TAG, "更新科目["+ subject+"]异常:" + e.getMessage(), e);
		}
	}
	
	//下载试卷。
	private void downloadPapers(AppClientSync reqParameter, DownloadResultListener handler){
		try {
			Log.d(TAG, "开始下载试卷...");
			//初始化数据工具
			if(this.dbHelpers == null){
				this.dbHelpers = new DbHelpers(this.context);
			}
			//表名称
			final String tableName = "tbl_papers";
			//查询试卷中最新的试卷发布时间
			String lastTime = null;
			SQLiteDatabase  db = this.dbHelpers.getReadableDatabase();
			Cursor cursor =	db.query(tableName, new String[]{"createTime"}, null, null, null, null, "createTime desc", "0,1");
			while(cursor.moveToNext()){
				lastTime = cursor.getString(0);
			}
			db.close();
			//设置下载试卷的开始时间
			reqParameter.setStartTime(StringUtils.trimToEmpty(lastTime));
			//下载试卷数据
			String result = postReqData(AppConstant.APP_API_PAPERS_URL, reqParameter);
			if(StringUtils.isBlank(result)){
				Log.d(TAG, "下载试卷反馈数据为空!");
				if(handler != null){
					handler.onComplete(false, "服务器未响应!");
				}
				return;
			}
			//反馈数据模型反序列化
			JSONCallback<List<PaperSync>> callback  = new JSONCallback<List<PaperSync>>(result);
			if(!callback.getSuccess()){
				Log.d(TAG, "下载试卷失败:" + callback.getMsg());
				if(handler != null){
					handler.onComplete(false, callback.getMsg());
				}
				return;
			}
			//试卷数据集合
			List<PaperSync> papers = callback.getData();
			if(papers == null || papers.size() == 0){
				Log.d(TAG, "未下载到试卷数据!");
				if(handler != null){
					handler.onComplete(false, "未有新试卷数据更新!");
				}
				return;
			}
			//更新试卷数据库
			this.updatePapersToDb(tableName, papers);
			//更新完成
			if(handler != null){
				handler.onComplete(true, null);
			}
		} catch (Exception e) {
			Log.e(TAG, "下载试卷异常:" + e.getMessage(), e);
		}
	}
	//更新试卷到数据库
	private void updatePapersToDb(String tableName, List<PaperSync> papers){
		//初始化数据操作对象。
		SQLiteDatabase db = null;
		 try {
			 Log.d(TAG, "开始更新试卷数据...:" + papers.size());
			//初始化数据帮助类。
			if(this.dbHelpers == null){
				this.dbHelpers = new DbHelpers(this.context);
			}
			//获取数据操作对象
			db =  this.dbHelpers.getWritableDatabase();
			//循环更新试卷数据
			for(PaperSync p : papers){
				if(p == null) continue;
				this.updatePapersToDb(db, tableName, p);
			}
		} catch (Exception e) {
			 Log.e(TAG, "更新试卷数据异常:" + e.getMessage(), e);
		}finally{
			//关闭数据操作
			if(db != null){
				db.close();
			}
		}
	}
	//更新试卷。
	private void updatePapersToDb(SQLiteDatabase db, String tableName, PaperSync paper){
		try {
			Log.d(TAG, "更新试卷:" + paper.getTitle());
			//查询数据是否存在SQL
			final String query_sql = " SELECT COUNT(*) FROM tbl_papers WHERE id = ? ";
			boolean isExits = false;
			Cursor cursor = db.rawQuery(query_sql, new String[]{ paper.getId() });
			while(cursor.moveToNext()){
				isExits = cursor.getInt(0) > 0;
				break;
			}
			cursor.close();
			//加密试题内容
			String encryptContent = PaperUtils.encryptContent(paper.getContent(), paper.getId());
			//根据试卷是否存在来判定更新或插入
			int result  = 0;
			if(isExits){//1.试卷存在则更新。
				ContentValues values = new ContentValues();
				values.put("title", paper.getTitle());
				values.put("type", paper.getType());
				values.put("total", paper.getTotal());
				values.put("content", encryptContent);
				values.put("createTime", paper.getCreateTime());
				values.put("subjectCode", paper.getSubjectCode());
				//更新试卷
				result = db.update(tableName, values, "id = ?", new String[]{ paper.getId() });
				//
				Log.d(TAG, "更新试卷:" + result);
			}else {//2.试卷不存在则插入
				ContentValues values = new ContentValues();
				values.put("id", paper.getId());
				values.put("title", paper.getTitle());
				values.put("type", paper.getType());
				values.put("total", paper.getTotal());
				values.put("content", encryptContent);
				values.put("createTime", paper.getCreateTime());
				values.put("subjectCode", paper.getSubjectCode());
				//插入试卷
				result = (int)db.insert(tableName, null, values);
				//
				Log.d(TAG, "插入试卷:" + result);
			}
		} catch (Exception e) {
			 Log.e(TAG, "更新试卷["+paper.getTitle()+"]异常:" + e.getMessage(), e);
		}
	}
}