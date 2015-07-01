package com.examw.test.support;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

/**
 * 主线程消息处理。
 * 
 * @author jeasonyoung
 * @since 2015年6月29日
 */
public class MsgHandler extends Handler{
	private Context context;
	/**
	 *  构造函数。
	 * @param context
	 */
	public MsgHandler(Context context){
		this.context = context;
	}
	/*
	 * 重载。
	 * @see android.os.Handler#handleMessage(android.os.Message)
	 */
	@Override
	public void handleMessage(Message msg) {
		if(msg.obj instanceof String){
			Toast.makeText(this.context, (String)msg.obj, Toast.LENGTH_SHORT).show();
			return;
		}
		super.handleMessage(msg);
	}
	
	/**
	 * 发送消息
	 * @param msg
	 */
	public void sendMessage(String msg){
		if(StringUtils.isBlank(msg))return;
		Message message = new Message();
		message.what = 0;
		message.obj = msg;
		
		this.sendMessage(message);
	}
}