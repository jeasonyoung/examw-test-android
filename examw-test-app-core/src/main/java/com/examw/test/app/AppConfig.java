package com.examw.test.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.examw.test.util.StringUtils;

/**
 * 应用程序配置类：用于保存用户相关信息及设置
 * 
 * @author fengwei
 * @version 1.0
 * @created 2014-11-25
 */
@SuppressLint("NewApi")
public class AppConfig {
	//产品ID
	//中级经济师
//	public static final String PRODUCTID = "615c758f-424f-4808-9528-67630125687e";
	//人力资源管理
//	public static final String PRODUCTID = "d5518100-3229-4f7a-993d-940330a3f7d5";
	//湖南会计
//	public static final String PRODUCTID = "f91e46c8-f85f-4ea6-aafe-5fcc445ecff0";
	//二级人力
//	public static final String PRODUCTID = "356ca1e5-e12b-4c16-9456-8e6618b9a28b";
	//一建,建筑工程
	public static final String PRODUCTID = "c36f8985-544b-4524-909f-fef638f710b6";//"60a4eb98-8c95-42d7-9178-d053a17f9e87";
	public static final String CODE = "1505 0761 2076 1012";
	//终端ID	
	public static final String TERMINALID = "4";
	
	public static String CLIENTKEY = "U8z2D0O5s7Li1Q3y4k6g";
	
	private final static String APP_CONFIG = "config";
	
	public static final String SHAREDPREFERENCES_NAME = "first_pref";  

	public static final String CONF_REGISTRATION = "registration_code";
	
	public final static String CONF_APP_UNIQUEID = "APP_UNIQUEID";
	public final static String CONF_COOKIE = "cookie";
	public final static String CONF_ACCESSTOKEN = "accessToken";
	public final static String CONF_ACCESSSECRET = "accessSecret";
	public final static String CONF_EXPIRESIN = "expiresIn";
	public final static String CONF_LOAD_IMAGE = "perf_loadimage";
	public final static String CONF_SCROLL = "perf_scroll";
	public final static String CONF_HTTPS_LOGIN = "perf_httpslogin";
	public final static String CONF_VOICE = "perf_voice";
	public final static String CONF_CHECKUP = "perf_checkup";
	public final static String CONF_EXAMTIME = "perf_examtime";
	public final static String CONF_AUTOLOGIN = "perf_autoLogin";
	public final static String CONF_SELECTED_COURSEID = "perf_courseids";
	public final static String CONF_EXAM_FONT_SIZE = "perf_examfontsize";
	public final static String CONF_LAST_SYNC_TIME = "perf_lastSyncTime";

	public final static String SAVE_IMAGE_PATH = "save_image_path";
	public final static String DATABASE_NAME = "examw_library.db";
	@SuppressLint("NewApi")
	public final static String DEFAULT_SAVE_IMAGE_PATH = Environment.getExternalStorageDirectory()
													+ File.separator+ "examw"+ File.separator
													+"image" + File.separator;
	@SuppressLint("NewApi")
	public final static String DEFAULT_DATA_PATH = "/data" + File.separator + Environment.getDataDirectory().getAbsolutePath() + File.separator;

	private Context mContext;
	
	private static AppConfig appConfig;

	public static AppConfig getAppConfig(Context context) {
		if (appConfig == null) {
			appConfig = new AppConfig();
			appConfig.mContext = context;
		}
		return appConfig;
	}

	/**
	 * 获取Preference设置
	 */
	public static SharedPreferences getSharedPreferences(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}

	/**
	 * 是否加载显示文章图片
	 */
	public static boolean isLoadImage(Context context) {
		return getSharedPreferences(context).getBoolean(CONF_LOAD_IMAGE, true);
	}

	public String getCookie() {
		return get(CONF_COOKIE);
	}

	public void setAccessToken(String accessToken) {
		set(CONF_ACCESSTOKEN, accessToken);
	}

	public String getAccessToken() {
		return get(CONF_ACCESSTOKEN);
	}

	public void setAccessSecret(String accessSecret) {
		set(CONF_ACCESSSECRET, accessSecret);
	}

	public String getAccessSecret() {
		return get(CONF_ACCESSSECRET);
	}

	public void setExpiresIn(long expiresIn) {
		set(CONF_EXPIRESIN, String.valueOf(expiresIn));
	}

	public long getExpiresIn() {
		return StringUtils.toLong(get(CONF_EXPIRESIN));
	}
	public long getExamTime()
	{
		return StringUtils.toLong(get(CONF_EXAMTIME));
	}
	public String getFormatExamTime()
	{
		return StringUtils.toDateStr(getExamTime());
	}
	public void setExamTime(long date)
	{
		set(CONF_EXAMTIME,String.valueOf(date));
	}
	public String get(String key) {
		Properties props = get();
		return (props != null) ? props.getProperty(key) : null;
	}

	public Properties get() {
		FileInputStream fis = null;
		Properties props = new Properties();
		try {
			// 读取files目录下的config
			// fis = activity.openFileInput(APP_CONFIG);

			// 读取app_config目录下的config
			File dirConf = mContext.getDir(APP_CONFIG, Context.MODE_PRIVATE);
			fis = new FileInputStream(dirConf.getPath() + File.separator
					+ APP_CONFIG);

			props.load(fis);
		} catch (Exception e) {
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return props;
	}

	private void setProps(Properties p) {
		FileOutputStream fos = null;
		try {
			// 把config建在files目录下
			// fos = activity.openFileOutput(APP_CONFIG, Context.MODE_PRIVATE);

			// 把config建在(自定义)app_config的目录下
			File dirConf = mContext.getDir(APP_CONFIG, Context.MODE_PRIVATE);
			File conf = new File(dirConf, APP_CONFIG);
			fos = new FileOutputStream(conf);

			p.store(fos, null);
			fos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (Exception e) {
			}
		}
	}

	public void set(Properties ps) {
		Properties props = get();
		props.putAll(ps);
		setProps(props);
	}

	public void set(String key, String value) {
		Properties props = get();
		props.setProperty(key, value);
		setProps(props);
	}

	public void remove(String... key) {
		Properties props = get();
		for (String k : key)
			props.remove(k);
		setProps(props);
	}
}
