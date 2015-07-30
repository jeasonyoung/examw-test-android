package com.examw.test.ui;

import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.examw.test.R;
import com.examw.test.adapter.MoreMenuAdapter;
import com.examw.test.app.AppContext;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
/**
 * 更多模块Fragment。
 * 
 * @author jeasonyoung
 * @since 2015年7月6日
 */
public class MainMoreFragment extends Fragment implements AdapterView.OnItemClickListener,View.OnClickListener{
	static final String TAG = "MainMoreFragment";
	private final MainActivity mainActivity;
	private final List<MenuItem> dataSource;
	private final MoreMenuAdapter adapter;
	/**
	 * 构造函数。
	 * @param mainActivity
	 */
	public MainMoreFragment(final MainActivity mainActivity){
		Log.d(TAG, "初始化...");
		this.mainActivity = mainActivity;
		this.dataSource = new ArrayList<MainMoreFragment.MenuItem>();
		this.adapter = new MoreMenuAdapter(this.mainActivity, this.dataSource);
	}
	/*
	 * 加载布局。
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "加载布局文件...");
		//加载布局
		final View view = inflater.inflate(R.layout.ui_main_more, container, false);
		//加载数据列表
		final ListView listView = (ListView)view.findViewById(R.id.list_more_menus);
		//设置数据适配器
		listView.setAdapter(this.adapter);
		//设置数据项监听
		listView.setOnItemClickListener(this);
		//退出登录按钮
		final View btnLogout = view.findViewById(R.id.main_more_btnLogout);
		//按钮点击事件处理
		btnLogout.setOnClickListener(this);
		//是否显示
		boolean hasLogin = false;
		AppContext app = (AppContext)this.mainActivity.getApplicationContext();
		if(app != null){
			hasLogin = (app.getCurrentUser() != null);
		}
		btnLogout.setVisibility(hasLogin ? View.VISIBLE : View.INVISIBLE);
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
		//异步加载数据
		new LoadDataAsyncTask().execute(this.mainActivity);
	}
	/*
	 * 列表选项点击事件处理。
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.d(TAG, "点击列表选项..." + position);
		if(this.dataSource.size() > position){
			MenuItem item = this.dataSource.get(position);
			if(item != null && StringUtils.isNotBlank(item.getActivity())){
				try {
					String className = this.mainActivity.getPackageName() + item.getActivity();
					Log.d(TAG, "反射Activity:" + className);
					Class<?> cls = Class.forName(className);
					this.startActivity(new Intent(this.mainActivity, cls));
					//切换产品关闭当前Activity
					if(StringUtils.equalsIgnoreCase(item.getIcon(), "icon_switch")){
						//关闭Activity
						this.mainActivity.finish();
					}
				} catch (Exception e) {
					Log.e(TAG, "反射类["+item.getActivity()+"]异常:" + e.getMessage(), e);
					Toast.makeText(this.mainActivity, "加载["+item.getActivity()+"]异常:" + e.getMessage(), Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
	/*
	 * 用户退出处理。
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		Log.d(TAG, "注销用户...");
		if(this.mainActivity != null){
			AppContext app = (AppContext)this.mainActivity.getApplicationContext();
			if(app != null){
				//注销当前用户
				app.changedCurrentUser(null);
				//刷新Fragment
				this.onStart();
			}
		}
	}
	/**
	 * 异步加载数据。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月16日
	 */
	private class LoadDataAsyncTask extends AsyncTask<Object, Void, Object>{
		/*
		 * 异步线程数据处理。
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Object doInBackground(Object... params) {
			try{
				Log.d(TAG, "开始后台异步线程处理...");
				//获取上下文
				final Context context = (Context)params[0];
				if(context == null){
					Log.d(TAG, "获取上下文失败!");
					return false;
				}
				//加载菜单JSON文件
				List<MenuGroup> list = null;
				try {
					//初始化数据类型
					Type type = new TypeToken<List<MenuGroup>>(){}.getType();
					Gson gson = new Gson();
					//加载文件数据流
					InputStreamReader reader = new InputStreamReader(context.getAssets().open("more_settings_menus.json"));
					//JSON反序列化
					list = gson.fromJson(reader, type);
					//关闭数据流
					reader.close();
				} catch (Exception e) {
					Log.e(TAG, "反序列化JSON文件异常:" + e.getMessage(), e);
				}
				//菜单数据处理。
				if(list != null && list.size() > 0){
					//分组排序
					Collections.sort(list, new Comparator<MenuGroup>(){
						@Override
						public int compare(MenuGroup lhs, MenuGroup rhs) { return lhs.getName() - rhs.getName(); }
					});
					//处理结果数据
					List<MenuItem> result = new ArrayList<MenuItem>();
					//循环分组
					for(MenuGroup group : list){
						if(group == null || group.getItems() == null) continue;
						//添加分隔
						result.add(null);
						//分组下的内容
						for(MenuItem item : group.getItems()){
							if(item == null || !item.isStatus()) continue;
							result.add(item);
						}
					}
					//返回
					return result.toArray(new MenuItem[0]);
				}
			}catch(Exception e){
				Log.e(TAG, "异步加载数据异常:" + e.getMessage(), e);
			}	
			return null;
		}
		/*
		 * 前端主线程UI处理
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Object result) {
			Log.d(TAG, "前端主线处理...");
			if(result != null){
				//清空源数据
				dataSource.clear();
				//添加数据
				dataSource.addAll(Arrays.asList((MenuItem[])result));
				//通知适配器更新数据
				adapter.notifyDataSetChanged();
			}
			//关闭等待动画
			mainActivity.waitingViewDialog.cancel();
		}
	}
	/**
	 * 菜单分组。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月16日
	 */
	private class MenuGroup implements Serializable{
		private static final long serialVersionUID = 1L;
		private int name;
		private MenuItem[] items;
		/**
		 * 获取分组。
		 * @return 分组。
		 */
		public int getName() {
			return name;
		}
		/**
		 * 获取菜单项集合。
		 * @return 菜单项集合。
		 */
		public MenuItem[] getItems() {
			if(this.items != null && this.items.length > 0){
				List<MenuItem> list = new ArrayList<MainMoreFragment.MenuItem>();
				//剔除不显示的
				for(MenuItem item : this.items){
					if(item != null && item.isStatus()){
						list.add(item);
					}
				}
				//排序
				if(list.size() > 1){
					Collections.sort(list, new Comparator<MenuItem>(){
						/*
						 * 排序处理。
						 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
						 */
						@Override
						public int compare(MenuItem lhs, MenuItem rhs) {
							return lhs.getOrder() - rhs.getOrder();
						}
					});
				}
				//输出
				return list.toArray(new MenuItem[0]);
			}
			return this.items;
		}
	}
	/**
	 * 菜单数据项
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月16日
	 */
	public class MenuItem implements Serializable{
		private static final long serialVersionUID = 1L;
		private String name,icon,activity;
		private boolean status;
		private int order;
		/**
		 * 获取菜单名称。
		 * @return 菜单名称。
		 */
		public String getName() {
			return name;
		}
		/**
		 * 获取菜单图标。
		 * @return 菜单图标。
		 */
		public String getIcon() {
			return icon;
		}
		/**
		 * 获取菜单Activity。
		 * @return 菜单Activity。
		 */
		public String getActivity() {
			return activity;
		}
		/**
		 * 获取菜单状态。
		 * @return 菜单状态。
		 */
		public boolean isStatus() {
			return status;
		}
		/**
		 * 获取菜单排序。
		 * @return 菜单排序。
		 */
		public int getOrder() {
			return order;
		}
		/*
		 * 重载生成JSON串。
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			Gson gson = new Gson();
			return gson.toJson(this);
		}
	}
}