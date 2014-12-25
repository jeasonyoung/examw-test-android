package com.examw.test.ui;

import java.lang.ref.WeakReference;

import android.app.ProgressDialog;
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
import com.examw.test.app.AppContext;

public class SyncActivity extends BaseActivity implements OnClickListener,
		OnCheckedChangeListener {

	private CheckBox favorCB, paperCB;
	private Button sysncBtn;
	private AppContext appContext;
	private LinearLayout loadingLayout;
	private MyHandler mHandler;
	private int favorFlag, errorFlag, paperFlag;
	private ProgressDialog proDialog;
//	private ArrayList<Course> courses;

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
		favorCB.setOnCheckedChangeListener(this);
		paperCB.setOnCheckedChangeListener(this);
		((Button) findViewById(R.id.btn_goback)).setOnClickListener(this);
		sysncBtn = (Button) this.findViewById(R.id.btn_syn);
		sysncBtn.setOnClickListener(this);
		loadingLayout = (LinearLayout) this.findViewById(R.id.loadingLayout);
		((TextView) findViewById(R.id.sync_text)).setText("同步中...");
		loadingLayout.setVisibility(View.GONE);
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
	}

	private void syncTruely() {
		loadingLayout.setVisibility(View.VISIBLE);
		final String username = appContext.getUsername();
		if (favorCB.isChecked()) {
			new Thread() {
				public void run() {
					try {
						synchronized (SyncActivity.this) {
							if(favorFlag!=0) return;
//							SyncData data = ApiClient.getSyncData(appContext,
//									username, URLs.URL_SYNC_FAVOR,
//									dao.findSyncFavorData(username));
//							if (data == null) {
//								mHandler.sendEmptyMessage(0);
//								return;
//							}
//							dao.syncIntoDB(data);
							// Message msg = mHandler.obtainMessage();
							// msg.what = 2;
							// msg.obj = data;
							mHandler.sendEmptyMessage(2);
						}
					} catch (Exception e) {
						e.printStackTrace();
						mHandler.sendEmptyMessage(-2);
					}

				};
			}.start();
		}
		if (paperCB.isChecked()) {
			new Thread() {
				public void run() {
					try {
						synchronized (SyncActivity.this) {
							if(paperFlag!=0) return;
//							SyncData data = ApiClient.getSyncData(appContext,
//									username, URLs.URL_SYNC_EXAM,
//									dao.findSyncRecordData(username));
//							if (data == null) {
//								mHandler.sendEmptyMessage(0);
//								return;
//							}
//							dao.syncIntoDB(data);
							// Message msg = mHandler.obtainMessage();
							// msg.what = 4;
							// msg.obj = data;
							// mHandler.sendMessage(msg);
							mHandler.sendEmptyMessage(4);
						}
					} catch (Exception e) {
						e.printStackTrace();
						mHandler.sendEmptyMessage(-4);
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
