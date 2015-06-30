package com.examw.test.model.sync;

import java.io.Serializable;

import android.content.Context;
import android.util.Log;

import com.examw.test.app.AppConstant;
import com.examw.test.app.AppContext;
import com.google.gson.Gson;

/**
 * 应用客户端数据模型。
 * 
 * @author yangyong
 * @since 2015年2月4日
 */
public class AppClient implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final String TAG = "AppClient";
	private String clientId,clientName,clientVersion,clientTypeCode,clientMachine,productId;
	/**
	 * 构造函数。
	 */
	public AppClient(Context context){
		if(context == null){
			Log.d(TAG, "构造函数参数context为null");
			throw new IllegalArgumentException();
		}
		//客户端唯一标示
		this.clientId = AppConstant.APP_ID;
		//客户端类型代码
		this.clientTypeCode = String.valueOf(AppConstant.APP_TYPECODE);
		//获取当前应用对象
		AppContext appContext = (AppContext)context.getApplicationContext();
		if(appContext != null){
			//客户端名称
			this.clientName = appContext.getAppName();
			//客户端软件版本
			this.clientVersion = appContext.getVersionName();
			//设备唯一标示
			this.clientMachine = appContext.getDeviceId();
			//产品ID
			if(appContext.getCurrentSettings() != null){
				this.productId = appContext.getCurrentSettings().getProductId();
			}
		}
	}
	/**
	 * 获取客户端ID。
	 * @return 客户端ID。
	 */
	public String getClientId() {
		return clientId;
	}
	/**
	 * 获取客户端名称。
	 * @return 客户端名称。
	 */
	public String getClientName() {
		return clientName;
	}
	/**
	 * 获取客户端版本。
	 * @return 客户端版本。
	 */
	public String getClientVersion() {
		return clientVersion;
	}
	/**
	 * 获取客户端软件类型代码。
	 * @return 客户端软件类型代码。
	 */
	public String getClientTypeCode() {
		return clientTypeCode;
	}
	/**
	 * 获取客户端机器码。
	 * @return 客户端机器码。
	 */
	public String getClientMachine() {
		return clientMachine;
	}
	/**
	 * 获取产品ID。
	 * @return 产品ID。
	 */
	public String getProductId() {
		return productId;
	}
	/*
	 * 重载。
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		Log.d(TAG, "生成JSON字符串...");
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}