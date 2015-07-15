package com.examw.test.support;

import java.lang.ref.WeakReference;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

/**
 * 主线程消息处理。
 * 
 * @author jeasonyoung
 * @since 2015年6月29日
 */
public class MsgHandler extends Handler{
	private static final String TAG = "MsgHandler";
	private final WeakReference<Context> reference;
	/**
	 *  构造函数。
	 * @param context
	 */
	public MsgHandler(Context context){
		Log.d(TAG, "初始化...");
		this.reference = new WeakReference<Context>(context);
	}
	/*
	 * 重载。
	 * @see android.os.Handler#handleMessage(android.os.Message)
	 */
	@Override
	public void handleMessage(Message msg) {
		Log.d(TAG, "消息处理..." + msg);
		if(msg.obj instanceof String){
			final Context context = this.reference.get();
			if(context != null){
				Toast.makeText(context, (String)msg.obj, Toast.LENGTH_SHORT).show();
			}
			return;
		}
		super.handleMessage(msg);
	}
	
	/**
	 * 发送消息
	 * @param msg
	 */
	public void sendMessage(String msg){
		Log.d(TAG, "发送消息:" + msg);
		if(StringUtils.isBlank(msg))return;
		Message message = new Message();
		message.what = 0;
		message.obj = msg;
		this.sendMessage(message);
	}
}