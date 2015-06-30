package com.examw.test.model.sync;

import java.io.Serializable;
import java.lang.reflect.Type;

import org.apache.commons.lang3.StringUtils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * HTTP反馈数据模型。
 * 
 * @author jeasonyoung
 * @since 2015年6月29日
 */
public class JSONCallback<T> implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final String TAG = "JSONCallback<T>";
	private Boolean success;
	private T data;
	private String msg;
	/**
	 * 构造函数。
	 * @param json
	 */
	public JSONCallback(String json){
		Log.d(TAG, "开始从JSON反序列化对象...");
		if(StringUtils.isBlank(json)){
			Log.d(TAG, "json字符串为空!");
			throw new IllegalArgumentException("json字符串为空!");
		}
		Type type = new TypeToken<JSONCallback<T>>(){}.getType();
		Gson gson = new Gson();
		JSONCallback<T> obj = gson.fromJson(json, type);
		if(obj == null){
			Log.d(TAG, "JSON反序列化对象失败!");
			throw new RuntimeException("JSON反序列化对象失败!");
		}
		//赋值
		this.success = obj.getSuccess();
		this.data = obj.getData();
		this.msg = obj.getMsg();
	}
	
	/**
	 * 获取是否成功。
	 * @return 是否成功。
	 */
	public Boolean getSuccess() {
		return success;
	}
	/**
	 * 设置是否成功。
	 * @param success 
	 *	  是否成功。
	 */
	public void setSuccess(Boolean success) {
		this.success = success;
	}
	/**
	 * 获取反馈数据。
	 * @return 反馈数据。
	 */
	public T getData() {
		return data;
	}
	/**
	 * 设置反馈数据。
	 * @param data 
	 *	  反馈数据。
	 */
	public void setData(T data) {
		this.data = data;
	}
	/**
	 * 获取反馈消息。
	 * @return 反馈消息。
	 */
	public String getMsg() {
		return msg;
	}
	/**
	 * 设置反馈消息。
	 * @param msg 
	 *	  反馈消息。
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}
}