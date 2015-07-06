package com.examw.test.ui;

import com.examw.test.app.UserAccount;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
/**
 * 我的用户信息Fragment。
 * 
 * @author jeasonyoung
 * @since 2015年7月6日
 */
public class MainMyUserViewFragment extends Fragment {
	private static final String TAG = "MainMyUserViewFragment";
	private final MainActivity mainActivity;
	private final UserAccount account;
	/**
	 * 构造函数。
	 * @param mainActivity
	 */
	public MainMyUserViewFragment(final MainActivity mainActivity, final UserAccount account){
		Log.d(TAG, "初始化...");
		this.mainActivity = mainActivity;
		this.account = account;
	}
	/*
	 * 加载布局xml。
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	/*
	 * 加载数据。
	 * @see android.support.v4.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
}