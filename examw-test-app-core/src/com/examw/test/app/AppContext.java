package com.examw.test.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Properties;
import java.util.UUID;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.examw.test.dao.ProductDao;
import com.examw.test.db.LibraryDBUtil;
import com.examw.test.db.UserDBUtil;
import com.examw.test.domain.User;
import com.examw.test.exception.AppException;
import com.examw.test.model.FrontProductInfo;
import com.examw.test.support.ApiClient;
import com.examw.test.support.AssetFileManager;
import com.examw.test.util.CyptoUtils;
import com.examw.test.util.MethodsCompat;
import com.examw.test.util.StringUtils;

/**
 * 全局应用程序类：用于保存和调用全局应用配置及访问网络数据
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class AppContext extends Application {
	private static final String TAG = "APPLICATION" ;
	public static final int NETTYPE_WIFI = 0x01;
	public static final int NETTYPE_CMWAP = 0x02;
	public static final int NETTYPE_CMNET = 0x03;
	
	public static final int PAGE_SIZE = 20;//默认分页大小
	private static final int CACHE_TIME = 60*60000;//缓存失效时间
	
	private boolean login = false;	//登录状态
	public static final int LOGINING = 1;// 正在登录
	public static final int LOGIN_FAIL = -1;// 登录失败
	public static final int LOGINED = 2;// 已经登录
	public static final int UNLOGIN = 0;// 没有登录
	public static final int LOCAL_LOGINED = 3; // 本地登录
	private int loginState = 0; // 登录状态
	private boolean isAutoCheckuped, isAutoLogined, hasNewVersion, hasNewData;
	
	private String loginUid = "";	//登录用户的id
	private Hashtable<String, Object> memCacheRegion = new Hashtable<String, Object>();
	
	private String saveImagePath;//保存图片路径
	
	private String username;
	/**
	 * Global application context.
	 */
	private static Context mContext;

	/**
	 * Construct of LitePalApplication. Initialize application context.
	 */
	public AppContext() {
		mContext = this;
	}

	/**
	 * Get the global application context.
	 * @return Application context.
	 * @throws GlobalException
	 */
	public static Context getContext() {
		if (mContext == null) {
			throw new RuntimeException("APPLICATION_CONTEXT_IS_NULL");
		}
		return mContext;
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		if(StringUtils.isEmpty(username)){
			username = getProperty("user.username"); 
		}
		mContext = getApplicationContext();
	}
	
	/**
	 * 获取 登录状态
	 * @return loginState
	 * 登录状态
	 */
	public int getLoginState() {
		return loginState;
	}

	/**
	 * 设置 登录状态
	 * @param loginState
	 * 登录状态
	 */
	public void setLoginState(int loginState) {
		this.loginState = loginState;
	}
	
	public void setUsername(String username)
	{
		this.username = username;
	}
	/**
	 * 获取 用户名
	 * @return username
	 * 
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * 获取 是否自动检测更新
	 * @return isAutoCheckuped
	 * 是否自动检测更新
	 */
	public boolean isAutoCheckuped() {
		return isAutoCheckuped;
	}

	/**
	 * 设置 是否自动检测更新
	 * @param isAutoCheckuped
	 * 是否自动检测更新
	 */
	public void setAutoCheckuped(boolean isAutoCheckuped) {
		this.isAutoCheckuped = isAutoCheckuped;
	}
	
	/**
	 * 是否自动登录
	 */
	public boolean isAutoLogin() {
		String perf_autoLogin = getProperty(AppConfig.CONF_AUTOLOGIN);
		if (StringUtils.isEmpty(perf_autoLogin))
			return false;
		else
			return StringUtils.toBool(perf_autoLogin);
	}
	
	/**
	 * 获取 是否自动登录
	 * @return isAutoLogined
	 * 是否自动登录
	 */
	public boolean isAutoLogined() {
		return isAutoLogined;
	}

	/**
	 * 设置 是否自动登录
	 * @param isAutoLogined
	 * 是否自动登录
	 */
	public void setAutoLogined(boolean isAutoLogined) {
		this.isAutoLogined = isAutoLogined;
	}

	/**
	 * 获取 是否有新版本
	 * @return hasNewVersion
	 * 是否有新版本
	 */
	public boolean isHasNewVersion() {
		return hasNewVersion;
	}

	/**
	 * 设置 是否有新版本
	 * @param hasNewVersion
	 * 是否有新版本
	 */
	public void setHasNewVersion(boolean hasNewVersion) {
		this.hasNewVersion = hasNewVersion;
	}

	/**
	 * 获取 是否有新数据
	 * @return hasNewData
	 * 是否有新数据
	 */
	public boolean isHasNewData() {
		return hasNewData;
	}

	/**
	 * 设置 是否有新数据
	 * @param hasNewData
	 * 是否有新数据
	 */
	public void setHasNewData(boolean hasNewData) {
		this.hasNewData = hasNewData;
	}

	@Override
	public void onCreate() {
		Log.d(TAG,"应用Application onCreate执行....");
		super.onCreate();
        //注册App异常崩溃处理器
        //hread.setDefaultUncaughtExceptionHandler(AppException.getAppExceptionHandler());
        init();
	}

	/**
	 * 初始化
	 */
	private void init(){
		//设置保存图片的路径
		saveImagePath = getProperty(AppConfig.SAVE_IMAGE_PATH);
		if(StringUtils.isEmpty(saveImagePath)){
			setProperty(AppConfig.SAVE_IMAGE_PATH, AppConfig.DEFAULT_SAVE_IMAGE_PATH);
			saveImagePath = AppConfig.DEFAULT_SAVE_IMAGE_PATH;
		}
	}
	
	/**
	 * 检测当前系统声音是否为正常模式
	 * @return
	 */
	public boolean isAudioNormal() {
		AudioManager mAudioManager = (AudioManager)getSystemService(AUDIO_SERVICE); 
		return mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
	}
	
	/**
	 * 应用程序是否发出提示音
	 * @return
	 */
	public boolean isAppSound() {
		return isAudioNormal() && isVoice();
	}
	
	/**
	 * 检测网络是否可用
	 * @return
	 */
	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}

	/**
	 * 获取当前网络类型
	 * @return 0：没有网络   1：WIFI网络   2：WAP网络    3：NET网络
	 */
	public int getNetworkType() {
		int netType = 0;
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}		
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = networkInfo.getExtraInfo();
			if(!StringUtils.isEmpty(extraInfo)){
				if (extraInfo.toLowerCase().equals("cmnet")) {
					netType = NETTYPE_CMNET;
				} else {
					netType = NETTYPE_CMWAP;
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = NETTYPE_WIFI;
		}
		return netType;
	}
	
	/**
	 * 判断当前版本是否兼容目标版本的方法
	 * @param VersionCode
	 * @return
	 */
	public static boolean isMethodsCompat(int VersionCode) {
		int currentVersion = android.os.Build.VERSION.SDK_INT;
		return currentVersion >= VersionCode;
	}
	
	/**
	 * 获取App安装包信息
	 * @return
	 */
	public PackageInfo getPackageInfo() {
		PackageInfo info = null;
		try { 
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {    
			e.printStackTrace(System.err);
		} 
		if(info == null) info = new PackageInfo();
		return info;
	}
	
	/**
	 * 获取App唯一标识
	 * @return
	 */
	public String getAppId() {
		String uniqueID = getProperty(AppConfig.CONF_APP_UNIQUEID);
		if(StringUtils.isEmpty(uniqueID)){
			uniqueID = UUID.randomUUID().toString();
			setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
		}
		return uniqueID;
	}
	
	/**
	 * 获取当前应用的版本代码
	 * @return
	 * @throws Exception
	 */
	public int getVersionCode()
	{
		// 获取packagemanager的实例
		PackageInfo packInfo;
		try {
			packInfo = getPackageManager().getPackageInfo(getPackageName(),0);
			return packInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * 获取当前应用的版本号
	 * @return
	 * @throws Exception
	 */
	public String getVersionName()
	{
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo;
		try {
			packInfo = getPackageManager().getPackageInfo(getPackageName(),0);
			return packInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 用户是否登录
	 * @return
	 */
	public boolean isLogin() {
		return login;
	}
	
	/**
	 * 获取登录用户id
	 * @return
	 */
	public String getLoginUid() {
		return this.loginUid;
	}
	/**
	 * 是否加载显示文章图片
	 * @return
	 */
	public boolean isLoadImage()
	{
		String perf_loadimage = getProperty(AppConfig.CONF_LOAD_IMAGE);
		//默认是加载的
		if(StringUtils.isEmpty(perf_loadimage))
			return true;
		else
			return StringUtils.toBool(perf_loadimage);
	}
	
	/**
	 * 设置是否加载文章图片
	 * @param b
	 */
	public void setConfigLoadimage(boolean b)
	{
		setProperty(AppConfig.CONF_LOAD_IMAGE, String.valueOf(b));
	}
	
	/**
	 * 是否发出提示音
	 * @return
	 */
	public boolean isVoice()
	{
		String perf_voice = getProperty(AppConfig.CONF_VOICE);
		//默认是开启提示声音
		if(StringUtils.isEmpty(perf_voice))
			return true;
		else
			return StringUtils.toBool(perf_voice);
	}
	
	/**
	 * 设置是否发出提示音
	 * @param b
	 */
	public void setConfigVoice(boolean b)
	{
		setProperty(AppConfig.CONF_VOICE, String.valueOf(b));
	}
	
	/**
	 * 是否启动检查更新
	 * @return
	 */
	public boolean isCheckUp()
	{
		String perf_checkup = getProperty(AppConfig.CONF_CHECKUP);
		//默认是开启
		if(StringUtils.isEmpty(perf_checkup))
			return true;
		else
			return StringUtils.toBool(perf_checkup);
	}
	
	/**
	 * 设置启动检查更新
	 * @param b
	 */
	public void setConfigCheckUp(boolean b)
	{
		setProperty(AppConfig.CONF_CHECKUP, String.valueOf(b));
	}
	
	/**
	 * 是否左右滑动
	 * @return
	 */
	public boolean isScroll()
	{
		String perf_scroll = getProperty(AppConfig.CONF_SCROLL);
		//默认是关闭左右滑动
		if(StringUtils.isEmpty(perf_scroll))
			return false;
		else
			return StringUtils.toBool(perf_scroll);
	}
	
	/**
	 * 设置是否左右滑动
	 * @param b
	 */
	public void setConfigScroll(boolean b)
	{
		setProperty(AppConfig.CONF_SCROLL, String.valueOf(b));
	}
	
	/**
	 * 是否Https登录
	 * @return
	 */
	public boolean isHttpsLogin()
	{
		String perf_httpslogin = getProperty(AppConfig.CONF_HTTPS_LOGIN);
		//默认是http
		if(StringUtils.isEmpty(perf_httpslogin))
			return false;
		else
			return StringUtils.toBool(perf_httpslogin);
	}
	
	/**
	 * 设置是是否Https登录
	 * @param b
	 */
	public void setConfigHttpsLogin(boolean b)
	{
		setProperty(AppConfig.CONF_HTTPS_LOGIN, String.valueOf(b));
	}
	
	/**
	 * 清除保存的缓存
	 */
	public void cleanCookie()
	{
		removeProperty(AppConfig.CONF_COOKIE);
	}
	
	/**
	 * 判断缓存数据是否可读
	 * @param cachefile
	 * @return
	 */
	private boolean isReadDataCache(String cachefile)
	{
		return readObject(cachefile) != null;
	}
	
	/**
	 * 判断缓存是否存在
	 * @param cachefile
	 * @return
	 */
	private boolean isExistDataCache(String cachefile)
	{
		boolean exist = false;
		File data = getFileStreamPath(cachefile);
		if(data.exists())
			exist = true;
		return exist;
	}
	
	/**
	 * 判断缓存是否失效
	 * @param cachefile
	 * @return
	 */
	public boolean isCacheDataFailure(String cachefile)
	{
		boolean failure = false;
		File data = getFileStreamPath(cachefile);
		if(data.exists() && (System.currentTimeMillis() - data.lastModified()) > CACHE_TIME)
			failure = true;
		else if(!data.exists())
			failure = true;
		return failure;
	}
	
	/**
	 * 清除app缓存
	 */
	public void clearAppCache()
	{
		//清除webview缓存
//		File file = CacheManager.getCacheFileBaseDir();  
//		if (file != null && file.exists() && file.isDirectory()) {  
//		    for (File item : file.listFiles()) {  
//		    	item.delete();  
//		    }  
//		    file.delete();  
//		}  		  
		deleteDatabase("webview.db");  
		deleteDatabase("webview.db-shm");  
		deleteDatabase("webview.db-wal");  
		deleteDatabase("webviewCache.db");  
		deleteDatabase("webviewCache.db-shm");  
		deleteDatabase("webviewCache.db-wal");  
		//清除数据缓存
		clearCacheFolder(getFilesDir(),System.currentTimeMillis());
		clearCacheFolder(getCacheDir(),System.currentTimeMillis());
		//2.2版本才有将应用缓存转移到sd卡的功能
		if(isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)){
			clearCacheFolder(MethodsCompat.getExternalCacheDir(this),System.currentTimeMillis());
		}
		//清除编辑器保存的临时内容
		Properties props = getProperties();
		for(Object key : props.keySet()) {
			String _key = key.toString();
			if(_key.startsWith("temp"))
				removeProperty(_key);
		}
	}	
	
	/**
	 * 清除缓存目录
	 * @param dir 目录
	 * @param numDays 当前系统时间
	 * @return
	 */
	private int clearCacheFolder(File dir, long curTime) {          
	    int deletedFiles = 0;         
	    if (dir!= null && dir.isDirectory()) {             
	        try {                
	            for (File child:dir.listFiles()) {    
	                if (child.isDirectory()) {              
	                    deletedFiles += clearCacheFolder(child, curTime);          
	                }  
	                if (child.lastModified() < curTime) {     
	                    if (child.delete()) {                   
	                        deletedFiles++;           
	                    }    
	                }    
	            }             
	        } catch(Exception e) {       
	            e.printStackTrace();    
	        }     
	    }       
	    return deletedFiles;     
	}
	
	/**
	 * 将对象保存到内存缓存中
	 * @param key
	 * @param value
	 */
	public void setMemCache(String key, Object value) {
		memCacheRegion.put(key, value);
	}
	
	/**
	 * 从内存缓存中获取对象
	 * @param key
	 * @return
	 */
	public Object getMemCache(String key){
		return memCacheRegion.get(key);
	}
	
	/**
	 * 保存磁盘缓存
	 * @param key
	 * @param value
	 * @throws IOException
	 */
	public void setDiskCache(String key, String value) throws IOException {
		FileOutputStream fos = null;
		try{
			fos = openFileOutput("cache_"+key+".data", Context.MODE_PRIVATE);
			fos.write(value.getBytes());
			fos.flush();
		}finally{
			try {
				fos.close();
			} catch (Exception e) {}
		}
	}
	
	/**
	 * 获取磁盘缓存数据
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public String getDiskCache(String key) throws IOException {
		FileInputStream fis = null;
		try{
			fis = openFileInput("cache_"+key+".data");
			byte[] datas = new byte[fis.available()];
			fis.read(datas);
			return new String(datas);
		}finally{
			try {
				fis.close();
			} catch (Exception e) {}
		}
	}
	
	/**
	 * 保存对象
	 * @param ser
	 * @param file
	 * @throws IOException
	 */
	public boolean saveObject(Serializable ser, String file) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try{
			fos = openFileOutput(file, MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(ser);
			oos.flush();
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}finally{
			try {
				oos.close();
			} catch (Exception e) {}
			try {
				fos.close();
			} catch (Exception e) {}
		}
	}
	
	/**
	 * 读取对象
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public Serializable readObject(String file){
		if(!isExistDataCache(file))
			return null;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try{
			fis = openFileInput(file);
			ois = new ObjectInputStream(fis);
			return (Serializable)ois.readObject();
		}catch(FileNotFoundException e){
		}catch(Exception e){
			e.printStackTrace();
			//反序列化失败 - 删除缓存文件
			if(e instanceof InvalidClassException){
				File data = getFileStreamPath(file);
				data.delete();
			}
		}finally{
			try {
				ois.close();
			} catch (Exception e) {}
			try {
				fis.close();
			} catch (Exception e) {}
		}
		return null;
	}

	public boolean containsProperty(String key){
		Properties props = getProperties();
		 return props.containsKey(key);
	}
	
	public void setProperties(Properties ps){
		AppConfig.getAppConfig(this).set(ps);
	}

	public Properties getProperties(){
		return AppConfig.getAppConfig(this).get();
	}
	
	public void setProperty(String key,String value){
		AppConfig.getAppConfig(this).set(key, value);
	}
	
	public String getProperty(String key){
		return AppConfig.getAppConfig(this).get(key);
	}
	public void removeProperty(String...key){
		AppConfig.getAppConfig(this).remove(key);
	}
	
	/**
	 * 获取设备唯一标识
	 */
	public String getDeviceId() {
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
	}
	
	/**
	 * 获取内存中保存图片的路径
	 * @return
	 */
	public String getSaveImagePath() {
		return saveImagePath;
	}
	/**
	 * 设置内存中保存图片的路径
	 * @return
	 */
	public void setSaveImagePath(String saveImagePath) {
		this.saveImagePath = saveImagePath;
	}

	public void saveLoginInfo(final User user) {
		this.loginUid = user.getUid();
		this.loginState = LOGINED;
		this.username = user.getUsername();
		setProperty("user.uid", String.valueOf(user.getUid()));
		setProperty("user.account", user.getUsername());
		setProperty("user.pwd",CyptoUtils.encode("changheng", user.getPassword()));
	}
	public void saveLocalLoginInfo(String username) {
		this.loginState = LOCAL_LOGINED;
		this.username = username;
	}

//	public AppUpdate getAppUpdate() throws AppException {
//		AppUpdate update = null;
//		String key = "appUpdateInfo";
//		if (!isNetworkConnected()) {
//			throw AppException.http(0);
//		}
//		if (isReadDataCache(key)) // 可读
//		{
//			System.out.println("可读..........");
//			update = (AppUpdate) readObject(key);
//			if (!update.isNeedUpdate(getVersionCode())) {
//				update = ApiClient.checkVersion(this);
//				if (update != null) {
//					update.setCacheKey(key);
//					saveObject(update, key);
//				}
//			}
//		} else {
//			System.out.println("不可读...........");
//			update = ApiClient.checkVersion(this);
//			if (update != null) {
//				update.setCacheKey(key);
//				saveObject(update, key);
//			}
//		}
//		System.out.println(update);
//		return update;
//	}
	
	public FrontProductInfo getProductInfo(){
		FrontProductInfo info = null;
		String key = "productInfo";
		if (!isNetworkConnected()) {
			return null;
		}
		if (isReadDataCache(key)) // 可读
		{
			info = (FrontProductInfo) readObject(key);
		} else {
			try{
				info = ApiClient.getProductInfo(this);
				if (info != null) {
					saveObject(info, key);
				}
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return info;
	}
	//初始化数据线程
	class InitDataThread extends Thread{
		@Override
		public void run() {
			//初始化数据库
			Log.d(TAG,"初始化数据线程启动");
			Long start = System.currentTimeMillis();
			SQLiteDatabase db = UserDBUtil.getDatabase();
			db.close();
			//复制Assets中的数据
			String dbPath =AppConfig.DEFAULT_DATA_PATH + AppContext.this.getPackageName() + File.separator +"databases" + File.separator + AppConfig.DATABASE_NAME;
			//复制数据
			//AssetFileManager.copyDataBase(AppContext.this, "data/"+AppConfig.DATABASE_NAME, dbPath);
			//复制图片
			//AssetFileManager.copyImages(AppContext.this, AppConfig.DEFAULT_SAVE_IMAGE_PATH);
			//解压缩包
//			AssetFileManager.upZipFile(AppContext.this, "data/examw.zip",AppConfig.DATABASE_NAME,dbPath, AppConfig.DEFAULT_SAVE_IMAGE_PATH);
//			db = null;
//			if(!ProductDao.hasInsert())
//			{
//				try {
//					FrontProductInfo info = ApiClient.getProductInfo(AppContext.this);
//					if(info !=null)
//					{
//						saveObject(info, "productInfo");
//						setProperty("exam_name", info.getExamName());
//						ProductDao.insert(info);
//					}
//				} catch (AppException e) {
//					e.printStackTrace();
//				}
//			}
			Log.d(TAG,"初始化数据线程结束,耗时:"+(System.currentTimeMillis() - start));
		}
	}
}
