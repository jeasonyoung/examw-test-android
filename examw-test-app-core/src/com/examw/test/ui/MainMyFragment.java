package com.examw.test.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.examw.test.R;
import com.examw.test.adapter.MyRecordTotalAdapter;
import com.examw.test.app.AppContext;
import com.examw.test.app.UserAccount;
import com.examw.test.dao.PaperDao;

/**
 * 我的Fragment。
 * 
 * @author jeasonyoung
 * @since 2015年7月6日
 */
public class MainMyFragment extends Fragment implements AdapterView.OnItemClickListener {
	private static final String TAG = "MainMyFragment";
	private final MainActivity mainActivity;
	private final List<PaperDao.SubjectTotalModel> dataSource;
	private final MyRecordTotalAdapter adapter;
	private ListView listView;
	private ReceiveBroadCast receiveBroadCast;
	/**
	 * 广播ACTION
	 */
	public static final String BROADCAST_LOGIN_ACTION = "com.examw.test.login_success";
	/**
	 * 构造函数。
	 * @param mainActivity
	 */
	public MainMyFragment(final MainActivity mainActivity){
		Log.d(TAG, "初始化...");
		this.mainActivity = mainActivity;
		this.dataSource = new ArrayList<PaperDao.SubjectTotalModel>();
		this.adapter = new MyRecordTotalAdapter(this.mainActivity, this.dataSource);
	}
	/*
	 * 加载布局。
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "加载布局文件...");
		//加载布局
		final View view = inflater.inflate(R.layout.ui_main_my, container, false);
		//加载用户信息
		new AsyncLoadUserTask(this.mainActivity).execute();
		//加载做题记录列表
		this.listView = (ListView)view.findViewById(R.id.list_my_records);
		//设置数据源
		this.listView.setAdapter(this.adapter);
		//设置行事件监听器
		this.listView.setOnItemClickListener(this);
		Log.d(TAG, "注册广播...");
		//初始化广播接收器
		this.receiveBroadCast = new ReceiveBroadCast(this.mainActivity);
		//注册广播
		this.mainActivity.registerReceiver(this.receiveBroadCast, new IntentFilter(BROADCAST_LOGIN_ACTION));
		//返回
		return view;
	}
	/*
	 * 加载数据处理。
	 * @see android.support.v4.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
		super.onStart();
		//启动等待动画
		this.mainActivity.waitingViewDialog.show();
		//加载数据
		new AsyncLoadTask(this.mainActivity).execute();
	}
	/*
	 * 重载销毁。
	 * @see android.support.v4.app.Fragment#onDestroyView()
	 */
	@Override
	public void onDestroyView() {
		if(this.receiveBroadCast != null){
			Log.d(TAG, "注销广播...");
			this.getActivity().unregisterReceiver(this.receiveBroadCast);
		}
		super.onDestroyView();
	}
	/*
	 * 选中行事件处理。
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if(this.dataSource.size() > position){
			PaperDao.SubjectTotalModel data = this.dataSource.get(position);
			if(data != null && data.getTotal() > 0){
				//意图
				Intent intent = new Intent(this.mainActivity, PaperRecordActivity.class);
				intent.putExtra(PaperRecordActivity.PAPER_SUBJECT_CODE, data.getCode());
				intent.putExtra(PaperRecordActivity.PAPER_SUBJECT_TITLE, String.format("%1$s(%2$d)", data.getName(), data.getTotal()));
				//
				this.startActivity(intent);
			}
		}
	}
	/**
	 * 接收广播处理。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月14日
	 */
	private class ReceiveBroadCast extends BroadcastReceiver{
		private final WeakReference<MainActivity> refActivity;
		/**
		 * 构造函数。
		 * @param activity
		 */
		public ReceiveBroadCast(MainActivity activity){
			Log.d(TAG, "初始化广播处理...");
			this.refActivity = new WeakReference<MainActivity>(activity);
		}
		/*
		 * 接收广播处理。
		 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
		 */
		@Override
		public void onReceive(Context context, Intent intent) {
			if(this.refActivity.get() != null && StringUtils.equals(intent.getAction(), BROADCAST_LOGIN_ACTION)){
				Log.d(TAG, "接收广播处理...");
				//加载用户信息
				new AsyncLoadUserTask(this.refActivity.get()).execute();
			}
		}
	}
	/**
	 * 异步加载用户信息。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月6日
	 */
	private class AsyncLoadUserTask extends AsyncTask<Void, Void, UserAccount>{
		private final WeakReference<MainActivity> refMainActivity;
		/**
		 * 构造函数。
		 * @param activity
		 */
		public AsyncLoadUserTask(final MainActivity activity){
			Log.d(TAG, "初始化加载用户...");
			this.refMainActivity = new WeakReference<MainActivity>(activity);
		}
		/*
		 * 后台线程加载数据。
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected UserAccount doInBackground(Void... params) {
			Log.d(TAG, "后台线程加载当前用户数据...");
			final MainActivity  activity = this.refMainActivity.get();
			if(activity != null){
				AppContext app = (AppContext)activity.getApplication();
				if(app != null){
					return app.getCurrentUser();
				}
			}
			return null;
		}
		/*
		 * 前台UI处理。
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(UserAccount result) {
			Log.d(TAG, "当前用户前台UI处理..." + result);
			//加载fragment
			final MainActivity  activity = this.refMainActivity.get();
			if(activity != null){
				Fragment fragment = (result == null ? new MainMyNoLoginViewFragment(activity) : 
					new MainMyUserViewFragment(result));
				//替换Fragment
				activity.getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.my_userinfo_replace, fragment)
				.commitAllowingStateLoss();
			}
		}
	}
	/**
	 * 异步加载数据。
	 * @author jeasonyoung
	 * @since 2015年7月6日
	 */
	private class AsyncLoadTask extends AsyncTask<Void, Void, Object>{
		private final WeakReference<Context> refContext;
		/**
		 * 构造函数。
		 * @param context
		 */
		public AsyncLoadTask(Context context){
			Log.d(TAG, "初始化异步加载数据...");
			this.refContext = new WeakReference<Context>(context);
		}
		/*
		 * 后台线程加载数据。
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Object doInBackground(Void... params) {
			try{
				Log.d(TAG, "后台线程加载数据...");
				if(this.refContext.get() != null){
					final PaperDao dao = new PaperDao(this.refContext.get());
					List<PaperDao.SubjectTotalModel> result = dao.totalPaperRecords();
					return (result == null || result.size() == 0) ? null : result.toArray(new PaperDao.SubjectTotalModel[0]);
				}
			}catch(Throwable e){
				Log.e(TAG, "后台线程加载数据异常:" + e.getMessage(), e);
			}
			return null;
		}
		/*
		 * 主线程UI处理。
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Object result) {
			Log.d(TAG, "主线程UI处理...");
			if(result != null){
				//1.清空数据源
				dataSource.clear();
				//2.添加数据
				dataSource.addAll(Arrays.asList((PaperDao.SubjectTotalModel[])result));
				//3.更新数据适配器
				adapter.notifyDataSetChanged();
			}
			//4.关闭等待
			mainActivity.waitingViewDialog.cancel();
		}
	}
}