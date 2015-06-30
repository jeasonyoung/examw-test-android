package com.examw.test.model.sync;

import com.examw.test.app.AppContext;
import com.google.gson.Gson;

import android.content.Context;
import android.util.Log;

/**
 * 客户端同步请求数据模型。
 * 
 * @author yangyong
 * @since 2015年2月27日
 */
public class AppClientSync extends AppClient {
	private static final long serialVersionUID = 1L;
	private static final String TAG = "AppClientSync";
	private String code,startTime;
	private boolean ignoreCode;
	/**
	 * 构造函数。
	 * @param context
	 */
	public AppClientSync(Context context) {
		super(context);
		Log.d(TAG, "初始化...");
		AppContext appContext = (AppContext)context.getApplicationContext();
		if(appContext != null && appContext.getCurrentUser() != null){
			Log.d(TAG, "加载产品注册码...");
			this.code = appContext.getCurrentUser().getRegCode();
		}
	}
	/**
	 * 获取注册码。
	 * @return 注册码。
	 */
	public String getCode() {
		return code;
	}
	/**
	 * 获取是否忽略注册码。
	 * @return 是否忽略注册码。
	 */
	public boolean isIgnoreCode() {
		return ignoreCode;
	}
	/**
	 * 设置是否忽略注册码。
	 * @param ignoreCode 
	 *	  是否忽略注册码。
	 */
	public void setIgnoreCode(boolean ignoreCode) {
		this.ignoreCode = ignoreCode;
	}
	/**
	 * 获取同步起始时间。
	 * @return 同步起始时间。
	 */
	public String getStartTime() {
		return startTime;
	}
	/**
	 * 设置同步起始时间。
	 * @param startTime 
	 *	  同步起始时间。
	 */
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	/*
	 * 重载。
	 * @see com.examw.test.model.sync.AppClient#toString()
	 */
	@Override
	public String toString() {
		Log.d(TAG, "生成JSON字符串...");
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}