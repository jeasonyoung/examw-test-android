package com.examw.test.service;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;

import com.examw.test.R;

/**
 * 下载服务
 * @author fengwei.
 * @since 2014年11月28日 下午3:22:13.
 */
public class DownloadService extends Service {
	private static final int NOTIFY_ID = 0;
	private boolean cancelled;
	private int progress;

	private Context mContext = this;

	private NotificationManager mNotificationManager;
	private Notification mNotification;
	
	private static final String apkFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/download/91up_p118.apk";

	private DownloadBinder binder = new DownloadBinder();

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@SuppressWarnings("deprecation")
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				int rate = msg.arg1;
				if (rate < 100) {
					// 更新进度
					RemoteViews contentView = mNotification.contentView;
					contentView.setTextViewText(R.id.rate, rate + "%");
					contentView.setProgressBar(R.id.progress, 100, rate, false);
				} else {
					// 下载完毕后变换通知形式
					mNotification.flags = Notification.FLAG_AUTO_CANCEL;
					mNotification.contentView = null;
					File apkfile = new File(apkFilePath);
			        if (!apkfile.exists()) {
			            return;
			        }    
			        Intent intent = new Intent(Intent.ACTION_VIEW);
			        intent.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive"); 
					// 告知已完成
//					intent.putExtra("completed", "yes");
					// 更新参数,注意flags要使用FLAG_UPDATE_CURRENT
					PendingIntent contentIntent = PendingIntent.getActivity(
							mContext, 0, intent,
							PendingIntent.FLAG_UPDATE_CURRENT);
					mNotification.setLatestEventInfo(mContext, "下载完成",
							"文件已下载完毕", contentIntent);
					stopSelf();// 停掉服务自身
				}

				// 最后别忘了通知一下,否则不会更新
				mNotificationManager.notify(NOTIFY_ID, mNotification);
				break;
			case 0:
				// 取消通知
				mNotificationManager.cancel(NOTIFY_ID);
				break;
			}
		};
	};

	@Override
	public void onCreate() {
		super.onCreate();
		mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// 返回自定义的DownloadBinder实例
		return binder;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		cancelled = true; // 取消下载线程
	}

	/**
	 * 创建通知
	 */
	@SuppressWarnings("deprecation")
	private void setUpNotification() {
		int icon = R.drawable.down2;
		CharSequence tickerText = "开始下载";
		long when = System.currentTimeMillis();
		mNotification = new Notification(icon, tickerText, when);

		// 放置在"正在运行"栏目中
		mNotification.flags = Notification.FLAG_ONGOING_EVENT;

		RemoteViews contentView = new RemoteViews(mContext.getPackageName(),
				R.layout.download_notification_layout);
		contentView.setTextViewText(R.id.fileName, "AngryBird.apk");
		// 指定个性化视图
		mNotification.contentView = contentView;
		mNotificationManager.notify(NOTIFY_ID, mNotification);
	}

	/**
	 * 下载模块
	 */
	private void startDownload() {
		cancelled = false;
		int rate = 0;
		while (!cancelled && rate < 100) {
			try {
				// 模拟下载进度
				Thread.sleep(500);
				rate = rate + 5;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.arg1 = rate;
			handler.sendMessage(msg);

			this.progress = rate;
		}
		if (cancelled) {
			Message msg = handler.obtainMessage();
			msg.what = 0;
			handler.sendMessage(msg);
		}
	}

	/**
	 * DownloadBinder中定义了一些实用的方法
	 * 
	 * @author user
	 * 
	 */
	public class DownloadBinder extends Binder {

		/**
		 * 开始下载
		 */
		public void start() {
			// 将进度归零
			progress = 0;
			// 创建通知
			setUpNotification();
			new Thread() {
				public void run() {
					// 下载
					startDownload();
				};
			}.start();
		}

		/**
		 * 获取进度
		 * 
		 * @return
		 */
		public int getProgress() {
			return progress;
		}

		/**
		 * 取消下载
		 */
		public void cancel() {
			cancelled = true;
		}

		/**
		 * 是否已被取消
		 * 
		 * @return
		 */
		public boolean isCancelled() {
			return cancelled;
		}
	}
}
