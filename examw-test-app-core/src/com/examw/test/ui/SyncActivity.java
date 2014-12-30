package com.examw.test.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.examw.test.dao.PaperRecordDao;
import com.examw.test.dao.UserDao;
import com.examw.test.domain.FavoriteItem;
import com.examw.test.domain.PaperRecord;
import com.examw.test.model.Json;
import com.examw.test.model.UserItemFavoriteInfo;
import com.examw.test.model.UserPaperRecordInfo;
import com.examw.test.support.ApiClient;
import com.examw.test.support.DataConverter;
import com.examw.test.util.StringUtils;

public class SyncActivity extends BaseActivity implements OnClickListener,
		OnCheckedChangeListener {
	private static final String TAG = "SyncActivity";
	private CheckBox favorCB, paperCB;
	private Button sysncBtn,updateBtn;
	private AppContext appContext;
	private LinearLayout loadingLayout;
	private MyHandler mHandler;
	private int favorFlag, errorFlag, paperFlag;
	private ProgressDialog proDialog;
	private TextView syncText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		super.onStart();
		if (appContext.getLoginState() != AppContext.LOGINED) {
			sysncBtn.setText("登录后方可同步");
		} else {
			sysncBtn.setText("开始同步");
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
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
//		courses = courseDao.findAllClass();
//		if (courses == null || courses.size() == 0) {
//			// 开线程去获取数据
//			if (proDialog == null) {
//				proDialog = ProgressDialog.show(SyncActivity.this, null,
//						"数据初始化...", true, true);
//				proDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//				new GetDataTask().execute();
//			} else {
//				proDialog.show();
//			}
//		} else {
//			syncTruely();
//		}
		syncTruely();
//		loadingLayout.setVisibility(View.VISIBLE);
	}
	
	private void update()
	{
		syncText.setText("数据更新中...");
		loadingLayout.setVisibility(View.VISIBLE);
		
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
								mHandler.sendEmptyMessage(0);
								return;
							}
							Json json = ApiClient.updateRecords(appContext, records);
							if(json !=null && json.isSuccess())
							{
								UserDao.updateLastTime(username, StringUtils.toStandardDateStr(new Date()), "lastSyncPaperTime");
							}
							Message msg = mHandler.obtainMessage();
							msg.what = 4;
							msg.obj = json;
							mHandler.sendMessage(msg);
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
							Log.d(TAG,"开始收藏记录");
							//查询需要上传的收藏记录
							ArrayList<FavoriteItem> list = FavoriteDao.findAll(username,userId);
							Log.d(TAG,"需要同步的考试记录个数:"+list.size());
							ArrayList<UserItemFavoriteInfo> records = DataConverter.convertFavors(list);
							if(records == null || records.size() == 0)
							{
								mHandler.sendEmptyMessage(0);
								return;
							}
							Json json = ApiClient.updateFavors(appContext, records);
							if(json !=null && json.isSuccess())
							{
								UserDao.updateLastTime(username, StringUtils.toStandardDateStr(new Date()), "lastSyncFavorTime");
							}
							Message msg = mHandler.obtainMessage();
							msg.what = 2;
							msg.obj = json;
							mHandler.sendMessage(msg);
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
					Toast.makeText(SyncActivity.this, "同步失败",
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		};
		Runnable isSyncDone = new Runnable() {
			@Override
			public void run() {
				if (favorFlag == -1 || errorFlag == -1 || paperFlag == -1) {
					// 同步失败
					handler.sendEmptyMessage(-1);
					// 停止检测
					handler.removeCallbacks(this);
					return;
				}
				if (favorFlag == 1 && errorFlag == 1 && paperFlag == 1) {
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
			// TODO Auto-generated constructor stub
			weak = new WeakReference<SyncActivity>(sync);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			SyncActivity sync = weak.get();
			switch (msg.what) {
			case 2:
				// try {
				// sync.dao.syncIntoDB((SyncData) msg.obj);
				sync.favorFlag = 1;
				// } catch (Exception e) {
				// sync.favorFlag = -1;
				// }
				break;
			case 3:
				// try {
				// sync.dao.syncIntoDB((SyncData) msg.obj);
				sync.errorFlag = 1;
				// } catch (Exception e) {
				// sync.errorFlag = -1;
				// }
				break;
			case 4:
				// try {
				// sync.dao.syncIntoDB((SyncData) msg.obj);
				sync.paperFlag = 1;
				// } catch (Exception e) {
				// sync.paperFlag = -1;
				// }
				break;
			case -2:
				sync.favorFlag = -1;
				break;
			case -3:
				sync.errorFlag = -1;
				break;
			case -4:
				sync.paperFlag = -1;
				break;
			case 0:
				Toast.makeText(sync, "您的登录已失效,请重新登录", Toast.LENGTH_SHORT).show();
				sync.favorFlag=-1;
				sync.errorFlag = -1;
				sync.paperFlag = -1;
				Intent intent = new Intent(sync, LoginActivity.class);
				sync.startActivity(intent);
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
