package com.examw.test.ui;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.examw.test.R;
import com.examw.test.app.AppContext;
import com.examw.test.dao.FavoriteDao;
import com.examw.test.dao.PaperDao;
import com.examw.test.dao.PaperRecordDao;
import com.examw.test.dao.ProductDao;
import com.examw.test.dao.SyllabusDao;
import com.examw.test.dao.UserDao;
import com.examw.test.domain.FavoriteItem;
import com.examw.test.domain.PaperRecord;
import com.examw.test.domain.Subject;
import com.examw.test.exception.AppException;
import com.examw.test.model.FrontPaperInfo;
import com.examw.test.model.Json;
import com.examw.test.model.UserItemFavoriteInfo;
import com.examw.test.model.UserPaperRecordInfo;
import com.examw.test.support.ApiClient;
import com.examw.test.support.DataConverter;
import com.examw.test.support.URLs;
import com.examw.test.util.StringUtils;
import com.examw.test.util.ToastUtils;

public class SyncActivity extends BaseActivity implements OnClickListener,
		OnCheckedChangeListener {
	private static final String TAG = "SyncActivity";
	private CheckBox favorCB, paperCB;
	private Button sysncBtn,updateBtn;
	private AppContext appContext;
	private LinearLayout loadingLayout;
	private MyHandler mHandler;
	private int favorFlag, paperFlag;
	private ProgressDialog proDialog;
	private TextView syncText;
	private static final String dataDir = Environment
			.getExternalStorageDirectory().getPath()
			+ File.separator
			+ "examw" + File.separator + "image" + File.separator;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.ui_sysnc);
		mHandler = new MyHandler(this);
		appContext = (AppContext) this.getApplication();
		initViews();

	}

	private void initViews() {
		((TextView) findViewById(R.id.title)).setText("同步");
		favorCB = (CheckBox) this.findViewById(R.id.ck_bookmark);
		paperCB = (CheckBox) this.findViewById(R.id.ck_exam);
		syncText = (TextView) findViewById(R.id.sync_text);
		favorCB.setOnCheckedChangeListener(this);
		paperCB.setOnCheckedChangeListener(this);
		((Button) findViewById(R.id.btn_goback)).setOnClickListener(this);
		sysncBtn = (Button) this.findViewById(R.id.btn_syn);
		updateBtn = (Button) this.findViewById(R.id.btn_update);
		sysncBtn.setOnClickListener(this);
		updateBtn.setOnClickListener(this);
		loadingLayout = (LinearLayout) this.findViewById(R.id.loadingLayout);
		loadingLayout.setVisibility(View.GONE);
		loadingLayout.setOnClickListener(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (appContext.getLoginState() != AppContext.LOGINED) {
			sysncBtn.setText("登录后方可同步");
			updateBtn.setText("登录后方可更新");
		} else {
			sysncBtn.setText("开始同步");
			updateBtn.setText("点击更新");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_goback:
			this.finish();
			break;
		case R.id.btn_syn:
			sync();
			break;
		case R.id.btn_update:
			update();
			break;
		case R.id.loadingLayout:
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		switch (buttonView.getId()) {
		case R.id.ck_bookmark:
			favorFlag = isChecked ? 0 : 1;
			break;
		case R.id.ck_exam:
			paperFlag = isChecked ? 0 : 1;
			break;
		}
	}

	private void sync() {
		if (appContext.getLoginState() != AppContext.LOGINED) {
			//当前不是在线登陆状态
			ToastUtils.show(this, "当前不是在线登录状态,请先在线登录");
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			return;
		}
		if (!(favorCB.isChecked()|| paperCB.isChecked())) {
			Toast.makeText(this, "请选择需要同步内容", Toast.LENGTH_SHORT).show();
			return;
		}
		if (loadingLayout.getVisibility() == View.VISIBLE) {
			return;
		}
		syncTruely();
	}
	
	private void update()
	{
		syncText.setText("数据更新中...");
		final String username = appContext.getUsername();
		loadingLayout.setVisibility(View.VISIBLE);
		new Thread(){
			public void run() {
				//更新试卷
				try{
					//查询上次试卷的更新时间
					String lastTime = UserDao.getLastTime(username, "lastUpdateTime");
					//获取最新的试卷信息
					ArrayList<FrontPaperInfo> list = ApiClient.getUpdatePaperInfo(appContext,lastTime);
					//更新试卷
					if(list == null || list.size()==0)
						mHandler.sendEmptyMessage(10);
					else
					{
						PaperDao.insertPaperList(list);
						File file = new File(dataDir);
						if(!file.exists())
						{
							file.mkdirs();
						}
						//导入试卷的数据
						for(FrontPaperInfo paper:list)
						{
							String content = ApiClient.loadPaperContent(appContext,paper.getId());
							PaperDao.updatePaperContent(paper.getId(), content);
							//加载试卷的图片
							loadImage(content, dataDir);
						}
						mHandler.sendEmptyMessage(10);
					}
					
					//更新大纲以及大纲下面的试题
					//查询科目
					ArrayList<Subject> subjects = ProductDao.findSubjects();
					if(subjects!=null && subjects.size()>0)
					{
						for(Subject s:subjects)
						{
							String content = ApiClient.loadSyllabusContent(
									(AppContext) getApplication(), s.getSubjectId());
							if (!StringUtils.isEmpty(content)) {
								SyllabusDao.updateSyllabusInfo(appContext,s, content);
							}
						}
					}
					//保存上次的更新时间
					UserDao.updateLastTime(username, StringUtils.toStandardDateStr(new Date(ApiClient.getStandardTime())), "lastUpdateTime");
					mHandler.sendEmptyMessage(20);
				}catch(AppException e)
				{
					e.printStackTrace();
					Message msg = mHandler.obtainMessage();
					msg.what = -10;
					msg.obj = e;
					mHandler.sendMessage(msg);
				}catch(Exception e)
				{
					e.printStackTrace();
					mHandler.sendEmptyMessage(-20);
				}
			};
		}.start();
		
	}
	private void loadImage(String content,String imagePath)throws Exception 
	{
		Log.e("导入图片","ddddddddddd");
		//查询字符串中是否包含图片
		if(StringUtils.isEmpty(content)) return;
		Pattern ps = Pattern.compile("<img[^>]+src\\s*=\\s*[\\\\][\"]([^\"]+)[\\\\][\"][^>]*>");//<img[^<>]*src=[\'\"]([0-9A-Za-z.\\/]*)[\'\"].(.*?)>");
        Matcher m = ps.matcher(content);
        while(m.find()){
        	String url = m.group(1);
        	if(!url.startsWith("http"))
        	{
        		url = URLs.HOST + url;
        	}
        	Log.e("导入图片",url);
        	ApiClient.getNetImage(url, imagePath);
        }
	}

	private void syncTruely() {
		syncText.setText("数据同步中...");
		loadingLayout.setVisibility(View.VISIBLE);
		final String username = appContext.getUsername();
		final String userId = appContext.getProductUserId();
		if (paperCB.isChecked()) {
			new Thread() {
				public void run() {
					try {
						synchronized (SyncActivity.this) {
							if(paperFlag!=0) return;
							Log.d(TAG,"开始同步考试记录");
							//查询用户的考试记录并且转换为上传数据对象
							String lastTime = UserDao.getLastTime(username, "lastSyncPaperTime");
							ArrayList<PaperRecord> list = PaperRecordDao.findAll(username,userId,lastTime);
							Log.d(TAG,"需要同步的考试记录个数:"+list.size());
							ArrayList<UserPaperRecordInfo> records = DataConverter.convertPaperRecords(list);
							if(records == null || records.size() == 0)
							{
								mHandler.sendEmptyMessage(4);
								return;
							}
							Json json = ApiClient.updateRecords(appContext, records);
							if(json !=null && json.isSuccess())
							{
								UserDao.updateLastTime(username, StringUtils.toStandardDateStr(new Date(ApiClient.getStandardTime())), "lastSyncPaperTime");
								mHandler.sendEmptyMessage(4);
							}else
							{
								mHandler.sendEmptyMessage(-4);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						mHandler.sendEmptyMessage(-4);
					}
				};
			}.start();
		}
		if (favorCB.isChecked()) {
			new Thread() {
				public void run() {
					try {
						synchronized (SyncActivity.this) {
							if(favorFlag!=0) return;
							Log.d(TAG,"开始收藏记录上传");
							//查询需要上传的收藏记录
							ArrayList<FavoriteItem> list = FavoriteDao.findAll(username,userId);
							ArrayList<UserItemFavoriteInfo> records = DataConverter.convertFavors(list);
							if(records == null || records.size() == 0)
							{
								mHandler.sendEmptyMessage(2);
								return;
							}
							Json json = ApiClient.updateFavors(appContext, records);
							if(json !=null && json.isSuccess())
							{
								UserDao.updateLastTime(username, StringUtils.toStandardDateStr(new Date(ApiClient.getStandardTime())), "lastSyncFavorTime");
								mHandler.sendEmptyMessage(2);
							}else
							{
								mHandler.sendEmptyMessage(-2);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						mHandler.sendEmptyMessage(-2);
					}

				};
			}.start();
		}
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				loadingLayout.setVisibility(View.GONE);
				switch (msg.what) {
				case 1:
					Toast.makeText(SyncActivity.this, "同步成功",
							Toast.LENGTH_SHORT).show();
					SyncActivity.this.finish();
					break;
				case -1:
					Toast.makeText(SyncActivity.this, "同步失败,请稍后重试",
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		};
		Runnable isSyncDone = new Runnable() {
			@Override
			public void run() {
				if (favorFlag == -1 || paperFlag == -1) {
					// 同步失败
					handler.sendEmptyMessage(-1);
					// 停止检测
					handler.removeCallbacks(this);
					return;
				}
				if (favorFlag == 1 && paperFlag == 1) {
					handler.sendEmptyMessage(1);
					// 停止检测
					handler.removeCallbacks(this);
				} else {
					// 如果activity没有初始化完毕则等待5毫秒再次检测
					handler.postDelayed(this, 500);
				}
			}
		};
		// 开始检测
		handler.postDelayed(isSyncDone, 1000);
	}

	static class MyHandler extends Handler {
		WeakReference<SyncActivity> weak;

		public MyHandler(SyncActivity sync) {
			weak = new WeakReference<SyncActivity>(sync);
		}

		@Override
		public void handleMessage(Message msg) {
			SyncActivity sync = weak.get();
			switch (msg.what) {
			case 2:
				sync.favorFlag = 1;
				break;
			case 4:
				sync.paperFlag = 1;
				break;
			case -2:
				sync.favorFlag = -1;
				break;
			case -4:
				sync.paperFlag = -1;
				break;
			case 0:
				Toast.makeText(sync, "您的登录已失效,请重新登录", Toast.LENGTH_SHORT).show();
				sync.favorFlag = -1;
				sync.paperFlag = -1;
				Intent intent = new Intent(sync, LoginActivity.class);
				sync.startActivity(intent);
				break;
			case 10:
				Toast.makeText(sync, "试卷没有更新", Toast.LENGTH_SHORT).show();
				break;
			case 20:
				sync.loadingLayout.setVisibility(View.GONE);
				Toast.makeText(sync, "更新成功", Toast.LENGTH_SHORT).show();
				break;
			case -20:
				sync.loadingLayout.setVisibility(View.GONE);
				Toast.makeText(sync, "系统异常,更新失败", Toast.LENGTH_SHORT).show();
				break;
			case -10:
				sync.loadingLayout.setVisibility(View.GONE);
				AppException e = (AppException) msg.obj;
				e.makeToast(sync);
				break;
			}
		}
	}

//	private class GetDataTask extends AsyncTask<String, Void, CourseList> {
//		@Override
//		protected void onPreExecute() {
//			// TODO Auto-generated method stub
//			super.onPreExecute();
//		}
//
//		@Override
//		protected CourseList doInBackground(String... params) {
//			// TODO Auto-generated method stub
//			try {
//				CourseList result = XMLParseUtil.parseClass(ApiClient
//						.getCourseData((AppContext) (SyncActivity.this
//								.getApplication()),AreaUtils.areaCode));
//				courseDao.save(result.getClassList(), result.getChapterList());
//				return result;
//			} catch (Exception e) {
//				e.printStackTrace();
//				return null;
//			}
//		}
//
//		protected void onPostExecute(CourseList result) {
//			if (proDialog != null) {
//				proDialog.dismiss();
//			}
//			if (result == null) {
//				Toast.makeText(SyncActivity.this, "数据初始化失败",
//						Toast.LENGTH_SHORT).show();
//				proDialog = null;
//			} else {
//				SyncActivity.this.syncTruely();
//			}
//		};
//	}
}
