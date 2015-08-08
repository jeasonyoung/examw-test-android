package com.examw.test.support;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * 倒计时支持。
 * 
 * @author jeasonyoung
 * @since 2015年7月22日
 */
public class CountdownViewSupport {
	private static final String TAG = "CountdownSupport";
	private static final int time_thread_sleep = 1000;
	private final WeakReference<TextView> viewReference;
	private int times,total;
	private boolean isRun;
	private ExecutorService pools;
	private CompleteHandler completeHandler;
	/**
	 * 构造函数。
	 * @param view
	 */
	public CountdownViewSupport(TextView view){
		Log.d(TAG, "初始化...");
		this.viewReference = new WeakReference<TextView>(view);
		this.pools =  Executors.newSingleThreadExecutor();
		this.isRun = false;
	}
	/**
	 * 初始化倒计时总时间。
	 * @param totalTimeSec
	 * 倒计时总时间(秒)。
	 * @param autoHideView
	 * 自动隐藏View(当总时间<=0时).
	 */
	public void init(final int totalTimeSec, boolean autoHideView){
		Log.d(TAG, "初始化倒计时总时间...");
		if(this.isRun){
			Log.d(TAG, "倒计时正在工作，不允许初始化...");
			return;
		}
		this.total = this.times = Math.max(totalTimeSec, 0);
		if(autoHideView){
			final TextView view = this.viewReference.get();
			if(view != null){
				view.post(new Runnable() {
					/*
					 * UI主线更新。
					 * @see java.lang.Runnable#run()
					 */
					@Override
					public void run() {
						Log.d(TAG, "隐藏倒计时 TextView...");
						view.setVisibility(totalTimeSec > 0 ? View.VISIBLE : View.INVISIBLE);
						view.invalidate();
					}
				});
			}
		}
	}
	/**
	 * 开始倒计时。
	 * @param handler
	 * 完成倒计时处理(异步)。
	 */
	public void start(CompleteHandler handler){
		if(this.isRun){
			Log.d(TAG, "倒计时正在执行，不能重复开始...");
			return;
		}
		//设置完成处理函数
		this.completeHandler = handler;
		//启动倒计时
		this.resume();
	}
	/**
	 * 暂停。
	 */
	public void pause(){
		Log.d(TAG, "暂停倒计时...");
		this.stop();
	}
	/**
	 * 恢复倒计时
	 */
	public synchronized void resume(){
		Log.d(TAG, "准备开始倒计时...");
		if(this.isRun){
			Log.d(TAG, "倒计时正在计时，不允许重置!");
			return;
		}
		if(!(this.isRun = this.times > 0)){
			Log.d(TAG, "倒计时已完成!");
			return;
		}
		//启动线程执行倒计时
		this.pools.execute(new Runnable() {
			/*
			 * 倒计时线程。
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run() {
				while(isRun){
					try {
						//判断是否有效
						if(times <= 0){
							Log.d(TAG, "倒计时结束..."  + useTimes());
							//停止倒计时
							stop();
							//倒计时完成
							onCompleteHandler();
							break;
						}
						//计数器递减
						times -= 1;
						//显示倒计时处理
						timeViewHandler(times);
						//线程休眠
						Thread.sleep(time_thread_sleep);
					} catch (Exception e) {
						Log.e(TAG, "倒计时["+times+"]异常:" + e.getMessage(), e);
						pause();
					}
				}
			}
		});
	}
	/**
	 * 停止。
	 */
	public synchronized void stop(){
		Log.d(TAG, "停止倒计时...");
		this.isRun = false;
	}
	/**
	 * 用时。
	 * @return
	 */
	public int useTimes(){
		return this.total - this.times;
	}
	//倒计时完成处理。
	private void onCompleteHandler(){
		if(completeHandler != null && this.pools != null && this.viewReference != null){
			Log.d(TAG, "异步线程处理倒计时结束...");
			this.pools.execute(new Runnable() {
				@Override
				public void run() {
					try {
						final TextView view = viewReference.get();
						if(view != null){
							view.post(new Runnable() {
								/*
								 * UI主线程处理
								 * @see java.lang.Runnable#run()
								 */
								@Override
								public void run() {
									Log.d(TAG, "UI主线程处理-倒计时结束...");
									completeHandler.onComplete(useTimes());
								}
							});
						}
					} catch (Throwable e) {
						Log.e(TAG, "倒计时完成处理异常:" + e.getMessage(), e);
					}
				}
			});
		}
	}
	//时间显示处理
	private void timeViewHandler(final int viewTimes){
		if(this.viewReference == null) return;
		final TextView view = this.viewReference.get();
		if(view != null){
			view.post(new Runnable() {
				/*
				 * 时间显示处理。
				 * @see java.lang.Runnable#run()
				 */
				@Override
				public void run() {
					//拆分为时/分/秒
					int h = viewTimes / 60 / 60;
					int m = viewTimes / 60 % 60;
					int s = viewTimes % 60;
					//显示
					view.setText(String.format("%1$02d:%2$02d:%3$02d", h, m, s));
				}
			});
		}
	}
	
	/**
	 * 倒计时完成处理。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月22日
	 */
	public interface CompleteHandler{
		/**
		 * 完成倒计时处理。
		 * @param useTimes
		 */
		void onComplete(int useTimes);
	}
}