package com.examw.test.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.examw.test.R;
import com.examw.test.adapter.MainGridAdapter;
import com.examw.test.app.AppConfig;
import com.examw.test.app.AppConstant;
import com.examw.test.app.AppContext;
import com.examw.test.util.LogUtil;
import com.examw.test.util.StringUtils;
import com.examw.test.widget.HomeGrid;

public class MainFragment extends Fragment {
	private HomeGrid g;
	private RelativeLayout setDateLayout;
	private TextView restDay, usernameTv;
	private SimpleDateFormat format;
	private AppConfig appConfig;
	private AppContext appContext;
	private ProgressDialog mProDialog;

	// 主界面适配的Activity
	private static final Class<?>[] classes = { KnowledgeActivity.class,
			ChooseSubjectActivity.class, ChooseSubjectActivity.class,
			ChooseSubjectActivity.class, ChooseSubjectActivity.class,
			DailyActivity.class, ForumActivity.class,
			PaperRecordActivity.class, ExamInfoActivity.class

	};
	private static final int[] action = { 0, AppConstant.ACTION_CHAPTER,
			AppConstant.ACTION_FAVORITE, AppConstant.ACTION_ERROR, 0, 0, 0, 0,
			0 };
	// 是否需要登录
	private static final boolean[] needLogin = { false, true, true, true, true,
			true, false, true, false };
	private boolean flag = true; // 是否登录的标识

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		appConfig = AppConfig.getAppConfig(getActivity());
		appContext = (AppContext) getActivity().getApplication();
		View v = inflater.inflate(R.layout.main_fragment, container, false);
		g = (HomeGrid) v.findViewById(R.id.gridview1);
		this.setDateLayout = (RelativeLayout) v
				.findViewById(R.id.set_date_layout);
		this.restDay = (TextView) v.findViewById(R.id.rest_day);
		this.usernameTv = (TextView) v.findViewById(R.id.usernameTv);
		this.usernameTv.setVisibility(View.GONE);
		// 下划线
		// restDay.getPaint().setFlags(
		// Paint.UNDERLINE_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
		format = new SimpleDateFormat("yyyy.MM.dd", Locale.CHINA);
		((TextView) v.findViewById(R.id.day_date)).setText(format
				.format(new Date()));
		g.setAdapter(new MainGridAdapter(getActivity()));
		g.setVisibility(View.VISIBLE);
		g.setOnItemClickListener(new ItemClickListener());
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		this.setDateLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), SetTimeActivity.class));
			}
		});
	}

	@Override
	public void onStart() {
		LogUtil.d("MainFragment onStart");
		super.onStart();
		flag = true;
		if (appConfig.getExamTime() == 0) {
			this.restDay.setText("");
		} else {
			String time = calculateRestDay(appConfig.getExamTime());
			this.restDay.setText(time);
			int dayNums = StringUtils.toInt2(time);
			int color = dayNums <= 0 ? getResources().getColor(R.color.black)
					: dayNums <= 30 ? getResources().getColor(R.color.red)
							: dayNums <= 90 ? getResources().getColor(
									R.color.blue) : getResources().getColor(
									R.color.green);
			this.restDay.setTextColor(color);
		}
		setUsername();
	}
	
	public void userLogout()
	{
		usernameTv.setText("");
		usernameTv.setVisibility(View.GONE);
	}
	
	@SuppressLint("HandlerLeak")
	private void setUsername() {
		// 开一个线程检测是不是已登录来更改主页用户名的设置
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				usernameTv.setText(appContext.getUsername());
				usernameTv.setVisibility(View.VISIBLE);
			};
		};
		new Thread() {
			public void run() {
				while (flag) {
					try {
						if (appContext.getLoginState() == AppContext.LOGINED
								|| appContext.getLoginState() == AppContext.LOCAL_LOGINED) {
							flag = false;
							handler.sendEmptyMessage(1);
						} else
							Thread.sleep(1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
	}

	// 计算日期
	private String calculateRestDay(long setTime) {
		String s = null;
		Date thatDay = new Date(setTime);
		long now = System.currentTimeMillis();
		if (thatDay.before(new Date(now))) // 在今天之前
		{
			s = "考试日期已过";
		} else {
			long i = (thatDay.getTime() - now) / 1000 / 60 / 60 / 24;
			s = i + " 天";
			if (i == 0) {
				s = "不到 1 天";
			}
		}
		return s;
	}

	// Item点击
	private class ItemClickListener implements OnItemClickListener {
		@SuppressLint("HandlerLeak")
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,
				long arg3) {
			// 下数据包
			arg1.clearFocus();
			if (needLogin[arg2]) // 要登陆才能进入
			{
				switch (appContext.getLoginState()) {
				case AppContext.LOCAL_LOGINED:
				case AppContext.LOGINED:
					startActivity(arg2, needLogin[arg2]);
					break;
				case AppContext.LOGINING:
					if (appContext.getLoginState() == AppContext.LOGINING) // 正在登录,显示progress
					{
						if (mProDialog != null) {
							mProDialog.show();
							return;
						}
						mProDialog = ProgressDialog.show(getActivity(), null,
								"正在登录，请稍后...", true, true);
						final Handler mHandler = new Handler() {
							@Override
							public void handleMessage(Message msg) {
								// TODO Auto-generated method stub
								switch (msg.what) {
								case 1:
									if (mProDialog != null) {
										mProDialog.dismiss();
									}
									startActivity(arg2, needLogin[arg2]);
									break;
								}
							}
						};
						Runnable checkIsLogin = new Runnable() {
							@Override
							public void run() {
								if (appContext.getLoginState() != AppContext.LOGINING) {
									// 去掉提示Dialog
									mHandler.sendEmptyMessage(1);
									mHandler.removeCallbacks(this);
								} else {
									// 如果没有登录完毕则等待500毫秒再次检测
									mHandler.postDelayed(this, 500);
								}
							}
						};
						mHandler.post(checkIsLogin);
					}
					break;
				default:
					startActivity(arg2, needLogin[arg2]);
					break;
				}
			} else {
				startActivity(arg2, needLogin[arg2]);
			}
		}
	}

	@Override
	public void onPause() {
		LogUtil.d("MainFragment onPause");
		flag = false;
		super.onPause();
	}

	@Override
	public void onDestroy() {
		LogUtil.d("MainFragment onDestroy");
		super.onDestroy();
	}

	private void startActivity(int arg2, boolean needCheck) {
		if (!needCheck
				|| appContext.getLoginState() == AppContext.LOCAL_LOGINED
				|| appContext.getLoginState() == AppContext.LOGINED) {
			Intent intent = new Intent(MainFragment.this.getActivity(),
					classes[arg2]);
			if (action[arg2] != 0) {
				intent.putExtra("action", action[arg2]);
			}
			startActivity(intent);
		} else {
			Intent intent2 = new Intent(MainFragment.this.getActivity(),
					LoginActivity.class);
			intent2.putExtra("className", classes[arg2].getName());
			if (action[arg2] != 0) {
				intent2.putExtra("action", action[arg2]);
			}
			startActivity(intent2);
		}
	}

//	private void showDisplayInfo() {
//		WindowManager winMgr = (WindowManager) this.getActivity().getApplicationContext()
//				.getSystemService(Context.WINDOW_SERVICE);
//		Display display = winMgr.getDefaultDisplay();
//		int height = display.getHeight();
//		int width = display.getWidth();
//		LogUtil.d("width: "+width +"; height:"+height);
//		// densityDpi = 120dpi is ldpi, densityDpi = 160dpi is mdpi,
//		// densityDpi = 240dpi is hdpi, densityDpi = 320dpi is xhdpi
//		DisplayMetrics dm = new DisplayMetrics();
//		this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
//		int densityDpi = dm.densityDpi;
//		LogUtil.d("densityDpi = "+densityDpi);
//	}
}
