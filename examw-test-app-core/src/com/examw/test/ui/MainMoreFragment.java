package com.examw.test.ui;

import com.examw.test.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 更多模块Fragment。
 * 
 * @author jeasonyoung
 * @since 2015年7月6日
 */
public class MainMoreFragment extends Fragment {
	private static final String TAG = "MainMoreFragment";
	private final MainActivity mainActivity;
	/**
	 * 构造函数。
	 * @param mainActivity
	 */
	public MainMoreFragment(final MainActivity mainActivity){
		Log.d(TAG, "初始化...");
		this.mainActivity = mainActivity;
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
		//
	}
}
