package com.examw.test.app;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout;

import com.examw.test.R;
import com.examw.test.ui.GuideActivity;
import com.examw.test.ui.MainActivity;
import com.examw.test.util.FileUtils;
import com.examw.test.util.StringUtils;

/**
 * 应用程序启动类：显示欢迎界面并跳转到主界面
 * 
 * @author fengwei
 * 
 */
public class AppStart extends Activity {

	private static final String TAG = "AppStart";
	private boolean isFirstIn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "初始界面显示...");
		super.onCreate(savedInstanceState);
		final View view = View.inflate(this, R.layout.ui_start, null);
		LinearLayout wellcome = (LinearLayout) view
				.findViewById(R.id.app_start_view);
		check(wellcome);
		setContentView(view);
		// 渐变展示启动屏
		AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
		aa.setDuration(2000);
		view.startAnimation(aa);
		aa.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation arg0) {
				redirectTo();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}

		});
		// 兼容低版本cookie（1.5版本以下，包括1.5.0,1.5.1）
		AppContext appContext = (AppContext) getApplication();
		String cookie = appContext.getProperty("cookie");
		if (StringUtils.isEmpty(cookie)) {
			String cookie_name = appContext.getProperty("cookie_name");
			String cookie_value = appContext.getProperty("cookie_value");
			if (!StringUtils.isEmpty(cookie_name)
					&& !StringUtils.isEmpty(cookie_value)) {
				cookie = cookie_name + "=" + cookie_value;
				appContext.setProperty("cookie", cookie);
				appContext.removeProperty("cookie_domain", "cookie_name",
						"cookie_value", "cookie_version", "cookie_path");
			}
		}
	}

	/**
	 * 检查是否需要换图片
	 * 
	 * @param view
	 */
	@SuppressWarnings("deprecation")
	private void check(LinearLayout view) {
		String path = FileUtils.getAppCache(this, "welcomeback");
		List<File> files = FileUtils.listPathFiles(path);
		if (!files.isEmpty()) {
			File f = files.get(0);
			long time[] = getTime(f.getName());
			long today = StringUtils.getToday();
			if (today >= time[0] && today <= time[1]) {
				view.setBackgroundDrawable(Drawable.createFromPath(f
						.getAbsolutePath()));
			}
		}
	}

	/**
	 * 分析显示的时间
	 * 
	 * @param time
	 * @return
	 */
	private long[] getTime(String time) {
		long res[] = new long[2];
		try {
			time = time.substring(0, time.indexOf("."));
			String t[] = time.split("-");
			res[0] = Long.parseLong(t[0]);
			if (t.length >= 2) {
				res[1] = Long.parseLong(t[1]);
			} else {
				res[1] = Long.parseLong(t[0]);
			}
		} catch (Exception e) {
		}
		return res;
	}

	/**
	 * 跳转到...
	 */
	private void redirectTo() {
		Log.d(TAG, "跳转到界面...");
		// 读取SharedPreferences中需要的数据
		// 使用SharedPreferences来记录程序的使用次数
		SharedPreferences preferences = getSharedPreferences(
				AppConfig.SHAREDPREFERENCES_NAME, MODE_PRIVATE);
		// 取得相应的值，如果没有该值，说明还未写入，用true作为默认值
		String isFirst;
		try {
			isFirst = "isFirstIn" + ((AppContext) getApplication()).getVersionCode();
			isFirstIn = preferences.getBoolean(isFirst, true);
			// 判断程序与第几次运行，如果是第一次运行则跳转到引导界面，否则跳转到主界面
			if (!isFirstIn) { // 不是第一次启动
				// 检测版本更新,判断是否选中自动登录
				goMain();
			} else {
				// 转到引导界面
				//开一个线程进行数据库初始化
				AppContext ac = (AppContext) this.getApplicationContext();
				ac.new InitDataThread().start();
				goGuide();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void goMain() {
		Intent intent = new Intent(this, MainActivity.class);
		this.startActivity(intent);
		this.finish();
	}

	private void goGuide() {
		Intent intent = new Intent(this, GuideActivity.class);
		this.startActivity(intent);
		this.finish();
	}

}