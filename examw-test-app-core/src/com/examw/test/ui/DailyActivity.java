package com.examw.test.ui;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.examw.test.R;
import com.examw.test.adapter.DailyDateAdapter;
import com.examw.test.adapter.PaperListAdapter;
import com.examw.test.app.AppContext;
import com.examw.test.dao.PaperDao;
import com.examw.test.domain.Paper;
import com.examw.test.model.DateInfo;
import com.examw.test.model.FrontPaperInfo;
import com.examw.test.support.ApiClient;
import com.examw.test.widget.DateHorizontalScrollView;

/**
 * 每日一练
 * @author fengwei.
 * @since 2014年11月26日 下午3:20:16.
 */
public class DailyActivity extends BaseActivity implements OnClickListener,OnGestureListener {
	private static final String TAG = "DailyActivity";
	private LinearLayout loadingLayout, nodataLayout, reloadLayout;
	private ListView paperListView;
	private ArrayList<Paper> paperList;
	private PaperListAdapter mAdapter;
	private Handler handler;
	private long today;
	private int currentDayOrder;
	
	private DateHorizontalScrollView mHorizontalScrollView;
	private ArrayList<DateInfo> weekdays;
	//手势
	private GestureDetector mGestureDetector; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_daily);
		initViews();
		//
		mGestureDetector = new GestureDetector(this,this); 
		initData();
	}

	private void initViews() {
		loadingLayout = (LinearLayout) this.findViewById(R.id.loadingLayout);
		nodataLayout = (LinearLayout) this.findViewById(R.id.nodataLayout);
		reloadLayout = (LinearLayout) this.findViewById(R.id.reload);
		paperListView = (ListView) this.findViewById(R.id.practiceListView);
		mHorizontalScrollView = (DateHorizontalScrollView) this
				.findViewById(R.id.horizontal_scrollview);
		((TextView) this.findViewById(R.id.title)).setText("每日一练");
		this.paperListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(DailyActivity.this,
						PaperInfoActivity.class);
				intent.putExtra("paperId", paperList.get(arg2).getPaperId());
				DailyActivity.this.startActivity(intent);
			}
		});
		mHorizontalScrollView
				.setOnItemClickListener(new DateHorizontalScrollView.OnItemClickListener() {
					@Override
					public void click(int position) {
						findDailyPaper();
					}
				});
	}

	private void initData() {
		handler = new MyHandler(this);
		loadingLayout.setVisibility(View.VISIBLE);
		new GetPaperListThread().start();
	}

	@Override
	public void onClick(View v) {

	}

	private class GetPaperListThread extends Thread {
		@Override
		public void run() {
			if (today == 0) {
				today = ApiClient.getStandardTime();
				handler.sendEmptyMessage(2);
			}
			paperList = PaperDao.findDailyPapers(today, currentDayOrder);
			if (paperList != null && paperList.size() > 0) {
				// 本地数据库中有试卷
				handler.sendEmptyMessage(1);
			} else {
				// 本地数据库中没有试卷,访问网络
				try {
					ArrayList<FrontPaperInfo> list = ApiClient
							.getDailyPaperList((AppContext) getApplication());
					PaperDao.insertPaperList(list);
					if (list == null || list.size() == 0)
						handler.sendEmptyMessage(2);
					else {
						paperList = PaperDao.findDailyPapers(today,
								currentDayOrder);
						handler.sendEmptyMessage(1);
					}
				} catch (Exception e) {
					e.printStackTrace();
					Message msg = handler.obtainMessage();
					msg.what = -1;
					handler.sendMessage(msg);
				}
			}
		}
	}

	static class MyHandler extends Handler {
		WeakReference<DailyActivity> mActivity;

		MyHandler(DailyActivity activity) {
			mActivity = new WeakReference<DailyActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			DailyActivity theActivity = mActivity.get();
			switch (msg.what) {
			case 1:
				if (theActivity.paperList != null
						&& theActivity.paperList.size() > 0) {
					theActivity.mAdapter = new PaperListAdapter(theActivity,
							theActivity.paperList);
					theActivity.paperListView.setAdapter(theActivity.mAdapter);
				} else {
					theActivity.nodataLayout.setVisibility(View.VISIBLE);// 无数据显示
				}
				theActivity.loadingLayout.setVisibility(View.GONE);
				break;
			case 2:
				theActivity.buildDailyDate();
				break;
			case -1:
				// 连不上,
				theActivity.loadingLayout.setVisibility(View.GONE);
				theActivity.nodataLayout.setVisibility(View.GONE);
				theActivity.reloadLayout.setVisibility(View.VISIBLE);// 无数据显示
				Toast.makeText(theActivity, "暂时连不上服务器,请稍候", Toast.LENGTH_SHORT)
						.show();// 提示
				break;
			case -2:
				// 连不上,
				theActivity.loadingLayout.setVisibility(View.GONE);
				theActivity.nodataLayout.setVisibility(View.GONE);
				theActivity.reloadLayout.setVisibility(View.VISIBLE);// 无数据显示
				Toast.makeText(theActivity, "查询数据出错", Toast.LENGTH_SHORT)
						.show();// 提示
				break;
			}
		}
	}

	private void buildDailyDate() {
		Log.d(TAG,"构造滑动星期条");
		weekdays = new ArrayList<DateInfo>();
		SimpleDateFormat weekFormat = new SimpleDateFormat("EEE",Locale.CHINA);
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd",Locale.CHINA);
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(today));
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
				cal.get(Calendar.DAY_OF_MONTH) - 7, 0, 0, 0);
		for (int i = 0; i < 7; i++) {
			cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
					cal.get(Calendar.DAY_OF_MONTH) + 1, 0, 0, 0);
			weekdays.add(new DateInfo(i, weekFormat.format(cal.getTime()),
					dateFormat.format(cal.getTime()),cal.getTime()));
		}
		mHorizontalScrollView.setAdapter(new DailyDateAdapter(this,weekdays));
		mHorizontalScrollView.post(new Runnable() {
			@Override
			public void run() {
				mHorizontalScrollView.startAnimation(6); //滚动到最后
			}
		});
	}
	
	private void findDailyPaper()
	{
		loadingLayout.setVisibility(View.VISIBLE);
		nodataLayout.setVisibility(View.GONE);
		reloadLayout.setVisibility(View.GONE);
		today = weekdays.get(mHorizontalScrollView.getCurrentPosition()).getDate().getTime();
		new Thread(){
			public void run() {
				try
				{
					Thread.sleep(1000);
					paperList = PaperDao.findDailyPapers(today,
							currentDayOrder);
					handler.sendEmptyMessage(1);
				}catch(Exception e)
				{
					e.printStackTrace();
					handler.sendEmptyMessage(-2);
				}
			};
		}.start();
	}
	//手势
	@Override
	public boolean onDown(MotionEvent e) {
		
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (e1.getX() - e2.getX() > 80) {
			//向左滑
			mHorizontalScrollView.startAnimation(mHorizontalScrollView.getPrevPosition());
		} else if (e1.getX() - e2.getX() < -80) {
			mHorizontalScrollView.startAnimation(mHorizontalScrollView.getNextPosition());
		}
		findDailyPaper();
		return false;
	}
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		mGestureDetector.onTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}
}
