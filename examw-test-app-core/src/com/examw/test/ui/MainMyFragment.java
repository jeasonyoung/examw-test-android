package com.examw.test.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.examw.test.R;
import com.examw.test.app.AppContext;
import com.examw.test.app.UserAccount;
import com.examw.test.dao.PaperDao;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
	private final RecordAdapter adapter;
	private ListView listView;
	/**
	 * 构造函数。
	 * @param mainActivity
	 */
	public MainMyFragment(final MainActivity mainActivity){
		Log.d(TAG, "初始化...");
		this.mainActivity = mainActivity;
		this.dataSource = new ArrayList<PaperDao.SubjectTotalModel>();
		this.adapter = new RecordAdapter(this.mainActivity, this.dataSource);
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
	 * 选中行事件处理。
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if(this.dataSource.size() > position){
			PaperDao.SubjectTotalModel data = this.dataSource.get(position);
			Toast.makeText(this.mainActivity, data.toString(), Toast.LENGTH_SHORT).show();
			///TODO:
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
			Log.d(TAG, "初始化");
			this.refMainActivity = new WeakReference<MainActivity>(activity);
		}
		/*
		 * 后台线程加载数据。
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected UserAccount doInBackground(Void... params) {
			Log.d(TAG, "后台线程加载当前用户数据...");
			MainActivity  activity = this.refMainActivity.get();
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
			MainActivity  activity = this.refMainActivity.get();
			if(activity != null){
				Fragment fragment = (result == null ? new MainMyNoLoginViewFragment(activity) : 
					new MainMyUserViewFragment(activity, result));
				//替换Fragment
				activity.getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.my_userinfo_replace, fragment)
				.commit();
			}
		}
	}
	/**
	 * 异步加载数据。
	 * @author jeasonyoung
	 * @since 2015年7月6日
	 */
	private class AsyncLoadTask extends AsyncTask<Void, Void, Void>{
		private final PaperDao dao;
		/**
		 * 构造函数。
		 * @param context
		 */
		public AsyncLoadTask(final Context context){
			Log.d(TAG, "初始化异步加载数据...");
			this.dao = new PaperDao(context);
		}
		/*
		 * 后台线程加载数据。
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Void doInBackground(Void... params) {
			Log.d(TAG, "后台线程加载数据...");
			//1.清空数据源
			dataSource.clear();
			//2.查询数据
			List<PaperDao.SubjectTotalModel> list = dao.totalPaperRecords();
			if(list != null && list.size() > 0){
				dataSource.addAll(list);
			}
			return null;
		}
		/*
		 * 主线程UI处理。
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Void result) {
			Log.d(TAG, "主线程UI处理...");
			//1.更新数据适配器
			adapter.notifyDataSetChanged();
			//2.关闭等待
			mainActivity.waitingViewDialog.cancel();
		}
	}
	/**
	 * 做题记录数据适配器。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月6日
	 */
	private class RecordAdapter extends BaseAdapter{
		private final Context context;
		private final List<PaperDao.SubjectTotalModel> list;
		/**
		 * 构造函数。
		 * @param context
		 * 上下文。
		 * @param list
		 * 列表数据源。
		 */
		public RecordAdapter(final Context context, final List<PaperDao.SubjectTotalModel> list){
			Log.d(TAG, "初始化数据适配器...");
			this.context = context;
			this.list = list;
		}
		/*
		 * 获取行数据.
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount() {
			return this.list.size();
		}
		/*
		 * 获取行数据对象。
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public Object getItem(int position) {
			return this.list.get(position);
		}
		/*
		 * 获取行ID。
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId(int position) {
			return position;
		}
		/*
		 * 创建行。
		 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.d(TAG, "创建行..." + position);
			ItemViewWrapper wrapper = null;
			if(convertView == null){
				Log.d(TAG, "新建行..." + position);
				wrapper = new ItemViewWrapper();
				//0.加载行布局
				convertView = LayoutInflater.from(this.context).inflate(R.layout.ui_main_my_item, parent, false);
				//1.科目
				wrapper.setSubject((TextView)convertView.findViewById(R.id.my_item_subjectname));
				//2.试题数
				wrapper.setTotals((TextView)convertView.findViewById(R.id.my_item_totals));
				//保存
				convertView.setTag(wrapper);
			}else {
				Log.d(TAG, "复用行..." + position);
				wrapper = (ItemViewWrapper)convertView.getTag();
			}
			//加载数据
			PaperDao.SubjectTotalModel data = (PaperDao.SubjectTotalModel)this.getItem(position);
			if(data != null && wrapper != null){
				//1.科目
				wrapper.getSubject().setText(data.getName());
				//2.试题数
				wrapper.getTotals().setText(String.valueOf(data.getTotal()));
			}
			return convertView;
		}
		//行视图包装类。
		private class ItemViewWrapper{
			private TextView subject,totals;
			/**
			 * 获取科目。
			 * @return 科目。
			 */
			public TextView getSubject() {
				return subject;
			}
			/**
			 * 设置科目。
			 * @param subject 
			 *	  科目。
			 */
			public void setSubject(TextView subject) {
				this.subject = subject;
			}
			/**
			 * 获取试题数。
			 * @return totals
			 */
			public TextView getTotals() {
				return totals;
			}
			/**
			 * 设置 totals
			 * @param totals 
			 *	  totals
			 */
			public void setTotals(TextView totals) {
				this.totals = totals;
			}
		}
	}
}