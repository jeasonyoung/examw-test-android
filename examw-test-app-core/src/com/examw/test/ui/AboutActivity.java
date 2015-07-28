package com.examw.test.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.examw.test.R;
import com.examw.test.app.AppContext;
import com.examw.test.app.AppSettings;
import com.examw.test.app.UserAccount;
import com.examw.test.widget.WaitingViewDialog;

/**
 * 关于应用。
 * 
 * @author jeasonyoung
 * @since 2015年7月16日
 */
public class AboutActivity extends Activity implements View.OnClickListener{
	private static final String TAG = "AboutActivity";
	private WaitingViewDialog waitingViewDialog;
	private final List<String> dataSource;
	private final AboutAdapter adapter;
	/**
	 * 构造函数。
	 */
	public AboutActivity(){
		Log.d(TAG, "初始化...");
		this.dataSource = new ArrayList<String>();
		this.adapter = new AboutAdapter(this, this.dataSource);
	}
	/*
	 * 重载创建。
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "重载创建...");
		//加载布局xml
		this.setContentView(R.layout.ui_main_more_about);
		//初始化等待动画
		this.waitingViewDialog = new WaitingViewDialog(this);
		//标题
		final TextView tvTitle = (TextView)this.findViewById(R.id.title);
		tvTitle.setText(this.getResources().getString(R.string.main_more_about_title));
		//返回按钮
		final View btnBack = this.findViewById(R.id.btn_goback);
		//设置点击事件监听
		btnBack.setOnClickListener(this);
		//列表
		final ListView listView = (ListView)this.findViewById(R.id.list_more_abouts);
		//设置数据适配器
		listView.setAdapter(this.adapter);
	}
	/*
	 * 重载加载数据。
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG, "加载数据...");
		//开启等待动画
		this.waitingViewDialog.show();
		//异步加载数据
		new LoadDataAsyncTask(this).execute();
	}
	/*
	 * 返回按钮处理。
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		Log.d(TAG, "返回按钮处理..." + v);
		this.finish();
	}
	/**
	 * 数据适配器。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月16日
	 */
	private class AboutAdapter extends BaseAdapter{
		private final Context context;
		private final List<String> list;
		/**
		 * 构造函数。
		 * @param context
		 * 上下文。
		 * @param list
		 * 数据源。
		 */
		public AboutAdapter(Context context, List<String> list){
			this.context = context;
			this.list = list;
		}
		/*
		 * 获取数据量。
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount() {
			return this.list.size();
		}
		/*
		 * 获取行数据。
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
		 * 创建数据行。
		 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.d(TAG, "创建行..." + position);
			String data = (String)this.getItem(position);
			if(StringUtils.isBlank(data)){
				Log.d(TAG, "创建行分隔..." + position);
				return LayoutInflater.from(this.context).inflate(R.layout.ui_main_more_section, parent, false);
			}else {
				TextView itemView = null;
				if(convertView != null){
					Log.d(TAG, "复用数据行.." + position);
					itemView = (TextView)convertView.getTag();
				}
				if(itemView == null){
					Log.d(TAG, "创建数据行.." + position);
					convertView = LayoutInflater.from(this.context).inflate(R.layout.ui_main_more_item, parent, false);
					itemView = (TextView)convertView.findViewById(R.id.more_item);
					convertView.setTag(itemView);
				}
				//加载数据
				//内容
				itemView.setText(data);
				return convertView;
			}
		}
	}

	/**
	 * 加载异步数据。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月16日
	 */
	private class LoadDataAsyncTask extends AsyncTask<Void, Void, Void>{
		private final WeakReference<Context> refContext;
		/**
		 * 构造函数。
		 * @param context
		 * 上下文。
		 */
		public LoadDataAsyncTask(final Context context){
			this.refContext = new WeakReference<Context>(context);
		}
		/*
		 * 后台异步线程加载数据。
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Void doInBackground(Void... params) {
			Log.d(TAG, "后台异步线程加载数据...");
			//获取上下文
			Context context = this.refContext.get();
			if(context != null){
				//清空数据源
				dataSource.clear();
				//获取应用
				AppContext app = (AppContext)context.getApplicationContext();
				if(app != null){
					//1.应用名称
					dataSource.add(app.getAppName());
					//2.分隔
					dataSource.add(null);
					//应用设置
					AppSettings settings = app.getCurrentSettings();
					if(settings != null){
						//3.所属考试
						dataSource.add("所属考试:" + StringUtils.trimToEmpty(settings.getExamName()));
						//4.所属产品
						dataSource.add("所属产品:" + StringUtils.trimToEmpty(settings.getProductName()));
					}
					//5.分隔
					dataSource.add(null);
					//当前用户
					UserAccount account = app.getCurrentUser();
					if(account != null){
						//6.产品注册码
						dataSource.add("产品注册码:" + StringUtils.trimToEmpty(account.getRegCode()));
						//7.当前用户
						dataSource.add("当前用户:" + StringUtils.trimToEmpty(account.getUsername()));
					}
				}
			}
			return null;
		}
		/*
		 * 前台主线程更新UI。
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Void result) {
			Log.d(TAG, "主线程更新UI...");
			//关闭等待动画
			waitingViewDialog.cancel();
			//通知适配器刷新数据
			adapter.notifyDataSetChanged();
		}
	}
}