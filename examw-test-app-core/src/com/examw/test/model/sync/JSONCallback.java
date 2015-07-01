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
	private Boolean success;
	private T data;
	private String msg;
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