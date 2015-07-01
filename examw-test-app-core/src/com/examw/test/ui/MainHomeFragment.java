package com.examw.test.ui;

import com.examw.test.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 首页Fragment
 * 
 * @author jeasonyoung
 * @since 2015年7月1日
 */
public class MainHomeFragment extends Fragment implements AdapterView.OnItemClickListener {
	private static final String TAG = "MainHomeFragment";
	private MainActivity mainActivity;
	private TextView tvTitle;
	private ListView listView;
	/**
	 * 构造函数。
	 * @param mainActivity
	 */
	public MainHomeFragment(MainActivity mainActivity){
		Log.d(TAG, "初始化...");
		this.mainActivity = mainActivity;
	}
	/*
	 * 加载UI。
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "装载UI...");
		//加载布局
		final View view = inflater.inflate(R.layout.ui_main_home, container, false);
		//标题
		this.tvTitle = (TextView)view.findViewById(R.id.main_home_title);
		//列表
		this.listView = (ListView)view.findViewById(R.id.list_main_home);
		//数据适配器处理
		///TODO:
		this.listView.setOnItemClickListener(this);
		//返回UI视图
		return view;
	}
	/*
	 * 加载数据处理
	 * @see android.support.v4.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
		super.onStart();
		//启动等待
		this.mainActivity.waitingViewDialog.show();
		//加载数据
	}
	/*
	 * 数据行选中事件处理
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		
	}
}