package com.examw.test.model.sync;

import java.io.Serializable;
import java.util.List;

import android.content.Context;

import com.examw.test.app.AppContext;
import com.google.gson.Gson;

/**
 * 客户端推送数据
 * 
 * @author yangyong
 * @since 2015年3月9日
 */
public  class AppClientPush<T extends Serializable> extends AppClient  {
	private static final long serialVersionUID = 1L;
	private String code,userId;
	private List<T> records;
	/**
	 * 构造函数。
	 * @param context
	 */
	public AppClientPush(Context context) {
		super(context);
		//加载应用上下文
		AppContext appContext = (AppContext)context.getApplicationContext();
		if(appContext != null && appContext.getCurrentUser() != null){
			//当前用户
			this.userId = appContext.getCurrentUser().getUserId();
			//产品注册码
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
	 * 获取当前用户ID。
	 * @return 当前用户ID。
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * 获取记录集合。
	 * @return 记录集合。
	 */
	public List<T> getRecords() {
		return records;
	}
	/**
	 * 设置记录集合。
	 * @param records 
	 *	  记录集合。
	 */
	public void setRecords(List<T> records) {
		this.records = records;
	}
	/*
	 * 重载。
	 * @see com.examw.test.model.sync.AppClient#toString()
	 */
	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}