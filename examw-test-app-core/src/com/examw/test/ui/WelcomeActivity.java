package com.examw.test.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.examw.test.R;
import com.examw.test.app.AppContext;
/**
 * 启动欢迎界面。
 * 
 * @author jeasonyoung
 * @since 2015年6月26日
 */
public class WelcomeActivity extends Activity {
	private static final String TAG = "WelcomeActivity";
	private static final int WATTING = 2 * 1000;
	/*
	 * 重载创建。
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate...");
		//加载布局xml
		this.setContentView(R.layout.ui_welcome);
		//加载View
		final View view = this.findViewById(R.id.app_welcome_view);
		//加载动画处理
		Animation am =  AnimationUtils.loadAnimation(this, R.anim.welcome_alpha);
		if(am != null && view != null){
			//启动动画
			view.startAnimation(am);
		}
		//初始化业务数据
		this.initAsyncTask.execute((Void[])null);
	}
	//初始化异步业务处理
	private AsyncTask<Void, Void, Boolean> initAsyncTask = new AsyncTask<Void, Void, Boolean>(){
		/*
		 * 后台异步线程处理。
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Boolean doInBackground(Void... params) {
			Log.d(TAG, "后台异步线程处理...");
			try {
				Log.d(TAG, "线程等待...");
				Thread.sleep(WATTING);
				Log.d(TAG, "线程等待结束");
			} catch (InterruptedException e) {
				Log.e(TAG, "线程等待异常:" + e.getMessage(), e);
			}
			//获取应用
			Log.d(TAG, "获取当前应用...");
			AppContext appContext = (AppContext)getApplication();
			//检查应用当前
			if(appContext != null && appContext.getCurrentSettings() != null){
				Log.d(TAG, "检查当前配置是否有效...");
				//判断配置是否有效
				return  appContext.getCurrentSettings().verification();
			}
			return false;
		}
		/*
		 * 主线程处理。
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Boolean result) {
			Log.d(TAG, "主线程处理:" + result);
			Intent doIntent; 
			if(result){
				Log.d(TAG, "跳转到主界面...");
				doIntent = new Intent(getApplicationContext(), MainActivity.class);
			}else {
				Log.d(TAG, "跳转到产品选择界面...");
				doIntent = new Intent(getApplicationContext(), SwitchActivity.class);
			}
			//跳转界面
			startActivity(doIntent);
			//结束当前Activity
			finish();
		};
	};
}