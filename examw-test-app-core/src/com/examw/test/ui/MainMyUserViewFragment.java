package com.examw.test.ui;

import org.apache.commons.lang3.StringUtils;

import com.examw.test.R;
import com.examw.test.app.UserAccount;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 我的用户信息Fragment。
 * 
 * @author jeasonyoung
 * @since 2015年7月6日
 */
public class MainMyUserViewFragment extends Fragment implements View.OnClickListener {
	private static final String TAG = "MainMyUserViewFragment";
	private final UserAccount account;
	private TextView tvAccount,tvRegcode;
	/**
	 * 构造函数。
	 * @param mainActivity
	 */
	public MainMyUserViewFragment(final UserAccount account){
		Log.d(TAG, "初始化...");
		this.account = account;
	}
	/*
	 * 加载布局xml。
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		Log.d(TAG, "加载布局文件...");
		//加载布局文件
		final View view = inflater.inflate(R.layout.ui_main_my_userinfo, container, false);
		//用户名
		this.tvAccount = (TextView)view.findViewById(R.id.my_userinfo_account);
		//产品注册码
		this.tvRegcode = (TextView)view.findViewById(R.id.my_userinfo_regcode);
		this.tvRegcode.setOnClickListener(this);
		//
		return view;
	}
	/*
	 * 加载数据。
	 * @see android.support.v4.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
		Log.d(TAG, "加载用户数据...");
		if(this.account != null){
			//用户账号
			if(this.tvAccount != null){
				this.tvAccount.setText(this.account.getUsername());
			}
			//产品注册码
			if(this.tvRegcode != null){
				String regcode = this.account.getRegCode();
				if(StringUtils.isBlank(regcode)){
					regcode = this.getResources().getString(R.string.main_my_userinfo_regcode);
				}
				this.tvRegcode.setText(regcode);
			}
		}
		super.onStart();
	}
	/*
	 * 注册码点击事件处理
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		Log.d(TAG, "注册码点击处理...");
		Toast.makeText(this.getActivity(), "产品注册!", Toast.LENGTH_LONG).show();
		// TODO Auto-generated method stub
		//startActivity(intent);
	}
}