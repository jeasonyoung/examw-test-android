package com.examw.test.app;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;

import com.examw.test.R;
import com.examw.test.ui.PaperActivity.PaperDataDelegate;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * 全局应用程序类：用于保存和调用全局应用配置及访问网络数据
 * @version 1.0
 * @created 2012-3-21
 */
public class AppContext extends Application {
	private static final String TAG = "AppContext";
	//全局上下文	
	private static Context mContext;
	//当前用户
	private static UserAccount currentUserAccount;
	//当前设置
	private static AppSettings currentSettings;
	//线程池
	private static ExecutorService ThreadPools = Executors.newCachedThreadPool();
	//连接管理
	private ConnectivityManager connectivityManager;
	//音频管理
	private AudioManager audioManager;
	//包信息
	private PackageInfo packageInfo;
	//电话管理
	private TelephonyManager telephonyManager;
	//试卷数据委托。
	private static  PaperDataDelegate paperDataDelegate;
	
	/**
	 * 网络类型枚举。
	 * @author jeasonyoung
	 * @since 2015年6月25日
	 */
	public enum NetType { None,Mobile,WiFi,}
	
	/**
	 * 获取试卷数据委托
	 * @return 试卷数据委托
	 */
	public static PaperDataDelegate getPaperDataDelegate() {
		return paperDataDelegate;
	}
	/**
	 * 设置试卷数据委托
	 * @param paperDataDelegate 
	 *	  试卷数据委托
	 */
	public static final synchronized void setPaperDataDelegate(PaperDataDelegate paperDataDelegate) {
		AppContext.paperDataDelegate = paperDataDelegate;
	}
	/**
	 * 获取应用全局上下文。
	 * @return 全局上下文对象。 
	 */
	public static Context getContext() {
		if (mContext == null) {
			Log.d(TAG, "获取应用全局上下文失败!");
			throw new RuntimeException("APPLICATION_CONTEXT_IS_NULL");
		}
		return mContext;
	}
	/**
	 * 是否存在SD卡。
	 * @return 存在返回true。
	 */
	public static boolean hasExistSDCard(){
		Log.d(TAG, "检测是否存在SD卡...");
		return StringUtils.equals(Environment.getExternalStorageState(), Environment.MEDIA_MOUNTED);
	}
	
	/**
	 * 获取当前用户。
	 * @return 当前用户。
	 */
	public UserAccount getCurrentUser(){
		Log.d(TAG, "获取当前用户...");
		return currentUserAccount;
	}
	/**
	 * 获取当前设置。
	 * @return 当前设置。
	 */
	public AppSettings getCurrentSettings(){
		Log.d(TAG, "获取当前设置...");
		return currentSettings;
	}
	/**
	 * 切换当前用户。
	 * @param userAccount
	 * 当前用户数据。
	 */
	public void changedCurrentUser(UserAccount userAccount){
		Log.d(TAG, "切换当前用户...");
		currentUserAccount = userAccount;
		if(currentUserAccount != null && ThreadPools != null){
			ThreadPools.execute(new Runnable() {
				@Override
				public void run() {
					try {
						Log.d(TAG, "异步线程将用户["+currentUserAccount+"]保存为当前用户...");
						currentUserAccount.saveForCurrent();
					} catch (Exception e) {
						Log.e(TAG, "用户保存为当前用户时异常:" + e.getMessage(), e);
					}
				}
			});
		}
	}
	/**
	 * 更新设置。
	 * @param appSettings
	 * 当前设置。
	 */
	public void updateSettings(AppSettings appSettings){
		Log.d(TAG, "更新设置...");
		currentSettings = appSettings;
		if(currentSettings != null &&  ThreadPools != null){
			ThreadPools.execute(new Runnable() {
				@Override
				public void run() {
					try {
						Log.d(TAG, "异步线程保存设置:" + currentSettings);
						currentSettings.saveToDefaults();
					} catch (Exception e) {
						Log.e(TAG, "设置保存为当前设置时异常:" + e.getMessage(), e);
					}
				}
			});
		}
	}
	//获取连接管理
	private ConnectivityManager getConnectivityManager(){
		if(this.connectivityManager == null){
			this.connectivityManager = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
			Log.d(TAG, "从系统服务中加载连接管理器...");
		}
		return this.connectivityManager;
	}
	//获取音频管理
	private AudioManager getAudioManager(){
		if(this.audioManager == null){
			this.audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
			Log.d(TAG, "从系统服务中加载音频管理器...");
		}
		return this.audioManager;
	}
	//获取包信息
	private PackageInfo getPackageInfo(){
		if(this.packageInfo == null){
			try {
				this.packageInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
				Log.d(TAG, "获取包信息...");
			} catch (NameNotFoundException e) {
				Log.e(TAG, "获取包信息异常:" + e.getMessage(),	 e);
			}
		}
		return this.packageInfo;
	}
	//获取电话管理器
	private TelephonyManager getTelephonyManager(){
		if(this.telephonyManager == null){
			 this.telephonyManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
			 Log.d(TAG, "从系统服务中加载电话管理器...");
		}
		return this.telephonyManager;
	}
	/**
	 * 检测网络是否可用。
	 * @return 可用返回true。
	 */
	public boolean hasNetworkConnected() {
		Log.d(TAG, "检查网络是否可用...");
		NetType type = this.getNetworkType();
		if(type != NetType.None){
			ConnectivityManager cm = this.getConnectivityManager();
			if(cm != null){
				NetworkInfo ni = cm.getActiveNetworkInfo();
				return ni != null && ni.isConnectedOrConnecting();
			}
		}
		return false;
	}
	/**
	 * 获取网络类型。
	 * @return 网路类型枚举。
	 */
	public NetType getNetworkType(){
		Log.d(TAG, "获取网络类型...");
		ConnectivityManager cm = this.getConnectivityManager();
		if(cm  != null){
			NetworkInfo nwInfo = cm.getActiveNetworkInfo();
			if(nwInfo != null){
				switch(nwInfo.getType()){
					case ConnectivityManager.TYPE_MOBILE:{
						Log.d(TAG, "网络类型:手机网络");
						//手机网络
						return NetType.Mobile;
					}
					case ConnectivityManager.TYPE_WIFI:{
						Log.d(TAG, "网络类型:Wifi");
						//Wi-Fi
						return NetType.WiFi;
					}
				}
			}
		}
		return NetType.None;
	}
	/**
	 * 检测当前系统声音是否为正常模式。
	 * @return 正常模式为True。
	 */
	public boolean hasAudio() {
		Log.d(TAG, "检测当前系统声音是否正常...");
		AudioManager am = this.getAudioManager();
		if(am != null){
			return am.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
		}
		return false;
	}
	/**
	 * 获取App名称。
	 * @return App名称。
	 */
	public String getAppName(){
		Log.d(TAG, "获取当前应用名称...");
		PackageInfo info = this.getPackageInfo();
		if(info != null && info.applicationInfo != null){
			return this.getText(info.applicationInfo.labelRes).toString();
		}
		return this.getText(R.string.app_name).toString();
	}
	/**
	 *  获取当前应用的版本代码。
	 * @return 版本代码。
	 */
	public int getVersionCode(){
		Log.d(TAG, "获取当前应用版本代码...");
		PackageInfo info = this.getPackageInfo();
		if(info != null){
			return info.versionCode; 
		}
		return -1;
	}
	/**
	 * 获取当前应用的版本号。
	 * @return 版本号。
	 */
	public String getVersionName(){
		Log.d(TAG, "获取当前应用版本名称...");
		PackageInfo info = this.getPackageInfo();
		if(info != null){
			return info.versionName;
		}
		return null;
	}
	/**
	 * 获取设备唯一标识。
	 * @return 设备唯一标识。
	 */
	public String getDeviceId() {
		Log.d(TAG, "获取设备唯一标示...");
		TelephonyManager tm = this.getTelephonyManager();
		if(tm != null){
			return tm.getDeviceId();
		}
		return null;
	}
		
	/*
	 * 重载应用创建。
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		 super.onCreate();
		 mContext = this;
		//异步加载数据.	
		 this.asyncInit.execute((Void[])null);
	 }
	//异步加载数据
	AsyncTask<Void, Void, Object[]> asyncInit =  new AsyncTask<Void, Void, Object[]>(){
		/*
		 * 后台执行。
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Object[] doInBackground(Void... params) {
			Log.d(TAG, "异步线程加载首选项数据...");
			return new Object[]{
					//加载当前用户
					UserAccount.loadCurrent(),
					//加载当前设置
					AppSettings.loadSettingsDefaults()
			};
		}
		/*
		 * 主线程处理。
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Object[] result) {
			Log.d(TAG, "主线程加载数据...");
			//设置当前用户
			currentUserAccount = (UserAccount)result[0];
			//设置当前设置
			currentSettings = (AppSettings)result[1];
		};
	};
	
	/**
	 * 系统内存不足时调用(用于释放资源)。
	 */
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mContext = getApplicationContext();
	}
}