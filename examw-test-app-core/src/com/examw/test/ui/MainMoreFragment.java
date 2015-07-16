package com.examw.test.ui;

import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.examw.test.R;
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
	private static final String TAG = "MainMoreFragment";
	private final MainActivity mainActivity;
	private final List<MenuItem> dataSource;
	private final MoreMenuAdapter adapter;
	
	private Button btnLogout;
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
		this.btnLogout = (Button)view.findViewById(R.id.main_more_btnLogout);
		//按钮点击事件处理
		this.btnLogout.setOnClickListener(this);
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
		new LoadDataAsyncTask(this.mainActivity).execute();
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
					Log.d(TAG, "反射Activity:" + item.getActivity());
					Class<?> cls = Class.forName(item.getActivity());
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
	 * 数据适配器。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月15日
	 */
	private class MoreMenuAdapter extends BaseAdapter{
		private final Context context;
		private final List<MenuItem> list;
		private final Resources resources;
		/**
		 * 构造函数。
		 * @param context
		 * @param list
		 */
		private MoreMenuAdapter(final Context context,final List<MenuItem> list){
			Log.d(TAG, "初始化数据适配器...");
			this.context = context;
			this.resources = this.context.getResources();
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
			MenuItem item = (MenuItem)this.getItem(position);
			if(item == null){//分组行
				Log.d(TAG, "创建分组行..."  + position);
				return LayoutInflater.from(this.context).inflate(R.layout.ui_main_more_section, parent, false);
			}else {//数据行
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
				//图标
				Drawable iconDrawable = this.loadIconDrawable(item.getIcon());
				if(iconDrawable != null){
					itemView.setCompoundDrawables(iconDrawable, null, null, null);
					itemView.setCompoundDrawablePadding((int)this.resources.getDimension(R.dimen.img_text_padding));
				}
				//内容
				itemView.setText(item.getName());
				return convertView;
			}
		}
		//加载图标
		@SuppressWarnings("deprecation")
		private Drawable loadIconDrawable(String icon){
			if(StringUtils.isNotBlank(icon) && this.resources != null){
				Drawable drawable = null;
				if(StringUtils.equalsIgnoreCase(icon, "icon_switch")){//切换产品
					drawable = this.resources.getDrawable(R.drawable.icon_switch);
				}else if(StringUtils.equalsIgnoreCase(icon, "icon_register")){//产品注册
					drawable = this.resources.getDrawable(R.drawable.icon_register);
				}else if(StringUtils.equalsIgnoreCase(icon, "icon_down")){//下载试卷
					drawable = this.resources.getDrawable(R.drawable.icon_down);
				}else if(StringUtils.equalsIgnoreCase(icon, "icon_callback")){//意见反馈
					drawable = this.resources.getDrawable(R.drawable.icon_callback);
				}else {//关于应用
					drawable = this.resources.getDrawable(R.drawable.icon_about);
				}
				if(drawable != null){
					drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
				}
				return drawable;
			}
			return null;
		}
	}
	/**
	 * 异步加载数据。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月16日
	 */
	private class LoadDataAsyncTask extends AsyncTask<Void, Void, Boolean>{
		private final WeakReference<Context> refContext;
		/**
		 * 构造函数。
		 * @param context
		 * 上下文。
		 */
		public LoadDataAsyncTask(final Context context){
			Log.d(TAG, "初始化异步数据处理...");
			this.refContext = new WeakReference<Context>(context);
		}
		/*
		 * 异步线程数据处理。
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Boolean doInBackground(Void... params) {
			try{
				Log.d(TAG, "开始后台异步线程处理...");
				//获取上下文
				Context context = this.refContext.get();
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
				int count = 0;
				if(list != null && (count = list.size()) > 0){
					//清空数据源
					dataSource.clear();
					//分组排序
					Collections.sort(list, new Comparator<MenuGroup>(){
						/*
						 * 排序处理
						 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
						 */
						@Override
						public int compare(MenuGroup lhs, MenuGroup rhs) {
							return lhs.getName() - rhs.getName();
						}
					});
					//数据添加
					for(int i = 0; i < count; i++){
						MenuGroup group = list.get(i);
						if(group != null && group.getItems() != null && group.getItems().length > 0){
							if(i > 0){
								//添加分组
								dataSource.add(null);
							}
							//菜单数据项
							for(MenuItem item : group.getItems()){
								if(item == null || !item.isStatus()) continue;
								dataSource.add(item);
							}
						}
					}
				}
				//检查用户是否登录
				AppContext app = (AppContext)context.getApplicationContext();
				if(app != null){
					return (app.getCurrentUser() != null);
				}
			}catch(Exception e){
				Log.e(TAG, "异步加载数据异常:" + e.getMessage(), e);
			}	
			return false;
		}
		/*
		 * 前端主线程UI处理
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Boolean result) {
			//更新注销按钮状态
			if(btnLogout != null){
				btnLogout.setVisibility(result ? View.VISIBLE : View.INVISIBLE);
			}
			//通知适配器更新数据
			if(adapter != null){
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
	private class MenuItem implements Serializable{
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