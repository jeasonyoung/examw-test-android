package com.examw.test.ui;

import com.examw.test.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * 我的未登录Fragment
 * 
 * @author jeasonyoung
 * @since 2015年7月6日
 */
public class MainMyNoLoginViewFragment extends Fragment implements View.OnClickListener{
	private static final String TAG = "MainMyNoLoginViewFragment";
	private final MainActivity mainActivity;
	/**
	 * 构造函数。
	 * @param mainActivity
	 */
	public MainMyNoLoginViewFragment(final MainActivity mainActivity){
		Log.d(TAG, "初始化...");
		this.mainActivity = mainActivity;
	}
	/*
	 * 加载布局xml。
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		Log.d(TAG, "加载布局...");
		final View view = inflater.inflate(R.layout.ui_main_my_nologin, container, false);
		//注册按钮
		final Button btnRegister = (Button)view.findViewById(R.id.my_nologin_btnregister);
		btnRegister.setOnClickListener(this);
		//登录按钮
		final Button btnLogin = (Button)view.findViewById(R.id.my_nologin_btnlogin);
		btnLogin.setOnClickListener(this);
		//返回试题
		return view;
	}
	/*
	 * 按钮事件处理。
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		Log.d(TAG, "点击按钮:" + v.getId());
		switch(v.getId()){
			case R.id.my_nologin_btnregister:{//注册
				this.mainActivity.startActivity(new Intent(this.mainActivity, RegisterActivity.class));
				break;
			}
			case R.id.my_nologin_btnlogin:{//登录
				Toast.makeText(this.mainActivity, "登录", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	}
}