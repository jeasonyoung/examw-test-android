package com.examw.test.ui;

import java.lang.ref.WeakReference;

import org.apache.commons.lang3.StringUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.examw.test.R;
import com.examw.test.dao.DownloadDao;
import com.examw.test.dao.DownloadResultListener;
import com.examw.test.support.MsgHandler;
import com.examw.test.widget.WaitingViewDialog;

/**
 * 下载试卷。
 * 
 * @author jeasonyoung
 * @since 2015年7月17日
 */
public class DownloadActivity extends Activity implements View.OnClickListener{
	private static final String TAG = "DownActivity";
	private WaitingViewDialog waitingViewDialog;
	private MsgHandler msgHandler;
	/*
	 * 重载创建。
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "重载创建...");
		super.onCreate(savedInstanceState);
		//加载布局XML
		this.setContentView(R.layout.ui_main_more_download);
		//初始化等待动画
		this.waitingViewDialog = new WaitingViewDialog(this);
		//初始化消息处理
		this.msgHandler = new MsgHandler(this);
		
		//返回按钮
		final Button btnBack = (Button)this.findViewById(R.id.btn_goback);
		btnBack.setOnClickListener(this);
		
		//标题
		final TextView tvTitle = (TextView)this.findViewById(R.id.title);
		tvTitle.setText(this.getResources().getString(R.string.main_more_download_title));
		
		//下载按钮
		final Button btnDownload = (Button)this.findViewById(R.id.more_download_btn);
		btnDownload.setOnClickListener(this);
	}
	/*
	 * 按钮事件处理。
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		Log.d(TAG, "按钮事件处理...");
		switch(v.getId()){
			case R.id.btn_goback:{//返回处理
				Log.d(TAG, "返回按钮处理...");
				//关闭
				this.finish();
				break;
			}
			case R.id.more_download_btn:{//下载处理
				Log.d(TAG, "下载按钮处理...");
				//开启等待动画
				this.waitingViewDialog.show();
				//下载试卷数据
				new DownloadPapersAsyncTask(this).execute();
				break;
			}
		}
	}
	/**
	 * 下载试卷异步任务。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月17日
	 */
	private class DownloadPapersAsyncTask extends AsyncTask<Void, Void, Boolean>{
		private final WeakReference<Context> refContext;
		/**
		 * 构造函数。
		 * @param context
		 */
		public DownloadPapersAsyncTask(final Context context){
			this.refContext = new WeakReference<Context>(context);
		}
		/*
		 * 后台异步线程处理。
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				Log.d(TAG, "后台线程处理...");
				Context context = this.refContext.get();
				if(context == null){
					Log.d(TAG, "获取上下文失败...");
					msgHandler.sendMessage("获取上下文失败!");
					return false;
				}
				//下载试卷数据
				DownloadDao download = new DownloadDao(context);
				download.download(true, new DownloadResultListener() {
					/*
					 * 下载处理
					 * @see com.examw.test.dao.DownloadResultListener#onComplete(boolean, java.lang.String)
					 */
					@Override
					public void onComplete(boolean result, String msg) {
						Log.d(TAG, "下载数据:" + result + "[" + msg + "]");
						if(!result && StringUtils.isNotBlank(msg)){
							msgHandler.sendMessage(msg);
						}
					}
				});
				return true;
			} catch (Exception e) {
				Log.e(TAG, "下载试卷数据异常:" + e.getMessage(), e);
				msgHandler.sendMessage("发送异常:" + e.getMessage());
			}
			return false;
		}
		/*
		 * 前台主线UI更新
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Boolean result) {
			Log.d(TAG, "前端主线程处理...");
			//关闭等待动画
			waitingViewDialog.cancel();
			//下载完成关闭当前Activity
			if(result){
				Context context = this.refContext.get();
				if(context != null){
					Log.d(TAG, "跳转到主界面...");
					//发送广播
					sendBroadcast(new Intent(MainActivity.BROADCAST_HOME_ACTION));
				}
				//关闭当前
				finish();
			}
		}
	}
}