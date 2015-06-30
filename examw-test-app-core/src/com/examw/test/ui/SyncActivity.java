package com.examw.test.ui;

import java.lang.ref.WeakReference;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.examw.test.exception.AppException;
import com.examw.test.model.sync.AppClientPush;
import com.examw.test.model.sync.AppClientSync;
import com.examw.test.model.sync.FavoriteSync;

public class SyncActivity extends BaseActivity implements OnClickListener,
		OnCheckedChangeListener {
	private CheckBox favorCB, paperCB;
	private Button sysncBtn,updateBtn;
//	private AppContext appContext;
	//private AppConfig appConfig;
	private LinearLayout loadingLayout;
	private MyHandler mHandler;
	private int favorFlag, paperFlag;
	private TextView syncText;
	private String code;
//	private static final String dataDir = Environment
//			.getExternalStorageDirectory().getPath()
//			+ File.separator
//			+ "examw" + File.separator + "image" + File.separator;
//	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.ui_sysnc);
		mHandler = new MyHandler(this);
	//	appContext = (AppContext) this.getApplication();
		//appConfig = AppConfig.getAppConfig(this);
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
//		if (appContext.getLoginState() != AppContext.LOGINED) {
//			sysncBtn.setText("登录后方可同步");
//			updateBtn.setText("登录后方可更新");
//		} else {
//			sysncBtn.setText("开始同步");
//			updateBtn.setText("点击更新");
//		}
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
		if(!check())
		{
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
	
	private boolean check()
	{
//		if (appContext.getLoginState() != AppContext.LOGINED) {
//			//当前不是在线登陆状态
//			ToastUtils.show(this, "当前不是在线登录状态,请先在线登录");
//			Intent intent = new Intent(this, LoginActivity.class);
//			startActivity(intent);
//			return false;
//		}
//		if(appConfig.get(appContext.getUsername() + "_code") == null)
//		{
//			ToastUtils.show(this, "请先激活注册码!");
//			this.startActivity(new Intent(this,RegisterCodeActivity.class));
//			return false;
//		}
//		code = appConfig.get(appContext.getUsername() + "_code");
		return true;
	}
	
	private void update()
	{
		if(!check())
		{
			return;
		}
		if (loadingLayout.getVisibility() == View.VISIBLE) {
			return;
		}
		syncText.setText("数据更新中...");
		//final String username = appContext.getUsername();
		loadingLayout.setVisibility(View.VISIBLE);
		new Thread(){
			public void run() {
				//更新试卷
				try{
					//查询上次试卷的更新时间
					//String lastTime = UserDao.getLastTime(username, "lastUpdateTime");
					//获取最新的试卷信息
					//AppClientSync req = new AppClientSync();
					//req.setCode(code);
					//req.setProductId(AppContext.getMetaInfo("productId"));
					//req.setStartTime(lastTime);
					//ExamSync result = ApiClient.getExams(appContext, req);
					//ExamDao.saveSubjects(result,username);
					//导入试卷
				//	ArrayList<PaperSync> list = ApiClient.getPapers((AppContext)getApplication(),req);
					//更新试卷
//					if(list == null || list.size()==0)
//						mHandler.sendEmptyMessage(10);
//					else
//					{
//						PaperDao.insertPapers(list,username);
//						File file = new File(dataDir);
//						if(!file.exists())
//						{
//							file.mkdirs();
//						}
//						//导入试卷的数据
//						for(PaperSync paper:list)
//						{
//							//加载试卷的图片
//							loadImage(paper.getContent(), dataDir);
//						}
//						mHandler.sendEmptyMessage(10);
//					}
					//更新大纲以及大纲下面的试题
					//查询科目
//					ArrayList<Subject> subjects = ProductDao.findSubjects();
//					if(subjects!=null && subjects.size()>0)
//					{
//						for(Subject s:subjects)
//						{
//							String content = ApiClient.loadSyllabusContent(
//									(AppContext) getApplication(), s.getSubjectId());
//							if (!StringUtils.isEmpty(content)) {
//								SyllabusDao.updateSyllabusInfo(appContext,s, content);
//							}
//						}
//					}
//					//保存上次的更新时间
					//UserDao.updateLastTime(username, StringUtils.toStandardDateStr(new Date(ApiClient.getStandardTime())), "lastUpdateTime");
					mHandler.sendEmptyMessage(20);
				}catch(Exception e)
				{
					e.printStackTrace();
					Message msg = mHandler.obtainMessage();
					msg.what = -10;
					msg.obj = e;
					mHandler.sendMessage(msg);
				}
					//catch(Exception e)
//				{
//					e.printStackTrace();
//					mHandler.sendEmptyMessage(-20);
//				}
			};
		}.start();
	}

//	private void loadImage(String content,String imagePath)throws Exception 
//	{
//		Log.e("导入图片","ddddddddddd");
//		//查询字符串中是否包含图片
//		if(StringUtils.isEmpty(content)) return;
//		Pattern ps = Pattern.compile("<img[^>]+src\\s*=\\s*[\\\\][\"]([^\"]+)[\\\\][\"][^>]*>");//<img[^<>]*src=[\'\"]([0-9A-Za-z.\\/]*)[\'\"].(.*?)>");
//        Matcher m = ps.matcher(content);
//        while(m.find()){
//        	String url = m.group(1);
//        	if(!url.startsWith("http"))
//        	{
//        	//	url = URLs.HOST + url;
//        	}
//        	Log.e("导入图片",url);
//        //	ApiClient.getNetImage(url, imagePath);
//        }
//	}

	private void syncTruely() {
		syncText.setText("数据同步中...");
		loadingLayout.setVisibility(View.VISIBLE);
	//	final String username = appContext.getUsername();
		//final String userId = appContext.getProductUserId();
		if (paperCB.isChecked()) {
			new Thread() {
				public void run() {
					try {
						synchronized (SyncActivity.this) {
							if(paperFlag!=0) return;
						//	LogUtil.d("开始同步考试记录");
							//查询用户的考试记录并且转换为上传数据对象
							//ArrayList<PaperRecordSync> list = PaperRecordDao.findSyncPaperRecords(username);
							//if(list == null || list.size() == 0)
							{
								mHandler.sendEmptyMessage(4);
								return;
							}
							//LogUtil.d("需要同步的考试记录个数:"+list.size());
							//AppClientPush<PaperRecordSync> syncPapers= new AppClientPush<PaperRecordSync>();
							//syncPapers.setClientTypeCode(AppContext.getMetaInfo("terminalId"));
							//syncPapers.setCode(code);
							//syncPapers.setProductId(AppContext.getMetaInfo("productId"));
							//syncPapers.setUserId(userId);
							//syncPapers.setRecords(list);
							//Json json1 = ApiClient.syncRecords(appContext, URLs.PAPER_RECORD_SYNC, syncPapers);
							
							//试题记录
							//ArrayList<PaperItemRecordSync> itemRecords = PaperRecordDao.findSyncItemRecords(username);
							//AppClientPush<PaperItemRecordSync> syncItems= new AppClientPush<PaperItemRecordSync>();
							//syncItems.setClientTypeCode(AppContext.getMetaInfo("terminalId"));
							//syncItems.setCode(code);
							//syncItems.setProductId(AppContext.getMetaInfo("productId"));
							//syncItems.setUserId(userId);
							//syncItems.setRecords(itemRecords);
							//Json json2 = ApiClient.syncRecords(appContext, URLs.ITEM_RECORD_SYNC, syncItems);
							//if((json1 !=null && json1.isSuccess())&&(json2 !=null && json2.isSuccess()))
//							{
//								//更新表
//								PaperRecordDao.updateRecords(username);
//								mHandler.sendEmptyMessage(4);
//							}else
//							{
//								mHandler.sendEmptyMessage(-4);
//							}
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
							//LogUtil.d("开始收藏记录上传");
							//查询需要上传的收藏记录
//							ArrayList<FavoriteSync> list = FavoriteDao.findFavorites(username);
//							if(list == null || list.size() == 0)
//							{
//								mHandler.sendEmptyMessage(2);
//								return;
//							}
						//	LogUtil.d("需要同步的收藏个数:"+list.size());
							//AppClientPush<FavoriteSync> syncPapers= new AppClientPush<FavoriteSync>();
							//syncPapers.setClientTypeCode(AppContext.getMetaInfo("terminalId"));
							//syncPapers.setCode(code);
							//syncPapers.setProductId(AppContext.getMetaInfo("productId"));
						//	syncPapers.setUserId(userId);
							//syncPapers.setRecords(list);
							//Json json = ApiClient.syncRecords(appContext, URLs.FAVORITE_SYNC, syncPapers);
//							if(json !=null && json.isSuccess())
//							{
//								FavoriteDao.deleteTruely(username);
//								mHandler.sendEmptyMessage(2);
//							}else
//							{
//								mHandler.sendEmptyMessage(-2);
//							}
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
				sync.loadingLayout.setVisibility(View.GONE);
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
