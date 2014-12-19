package com.examw.test.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.examw.test.R;
import com.examw.test.domain.Paper;
import com.examw.test.support.ApiClient;

/**
 * 每日一练
 * @author fengwei.
 * @since 2014年11月26日 下午3:20:16.
 */
public class DailyActivity extends BaseActivity implements OnClickListener {
	private static final String TAG = "DailyActivity";
	private LinearLayout loading,nodata,reload;
	private ListView paperListView;
	private List<Paper> paperList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_daily);
		initViews();
		initData();
	}
	
	private void initViews() {
		
	}
	

	private void initData() {
		final Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Log.d(TAG,"当前系统时间:"+format.format(new Date()));
				Log.d(TAG,"获取的网络时间:"+ format.format(new Date((Long)msg.obj)));
			}
		};
		new Thread(){
			public void run() {
				Message msg = handler.obtainMessage();
				msg.obj = ApiClient.getStandardTime();
				handler.sendMessage(msg);
			};
		}.start();
	}

	@Override
	public void onClick(View v) {
		
	}
	
}
