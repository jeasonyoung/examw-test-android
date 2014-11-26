package com.examw.test.ui;

import java.lang.ref.WeakReference;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.examw.test.R;
import com.examw.test.app.AppConfig;
import com.examw.test.app.AppContext;
import com.examw.test.app.AppManager;
import com.examw.test.util.ToastUtils;
import com.examw.test.widget.BadgeView;
import com.slidingmenu.lib.SlidingMenu;

/**
 * 主界面
 * @author fengwei.
 * @since 2014年9月3日 下午3:18:19.
 */
public class MainActivity extends FragmentActivity implements OnClickListener{
	private SlidingMenu menu;
//	private LinearLayout menuLayout;
	private ImageButton menuBtn;
	private AppContext appContext;// 全局Context
	private AppConfig appConfig;
	private Handler mHandler = null;
	public static final int MAIN_SETTING = 3;
	public static final int MAIN_INDEX = 0;
	public static final int MAIN_ACCOUNT = 1;
	public static final int MAIN_ABOUT = 2;
	private int flag = MAIN_INDEX;
//	private UserDao userDao;
	private RadioButton homeBtn,moreBtn,setBtn;
	private LinearLayout footer;
	private BadgeView v;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.e("MainActivity", "onCreate");
		super.onCreate(savedInstanceState);
		// 添加Activity到堆栈
		AppManager.getAppManager().addActivity(this);
		appContext = (AppContext) getApplication();
		appConfig = AppConfig.getAppConfig(this);
		this.setContentView(R.layout.ui_main);
//		mHandler = new MyHandler(this);
		initViews();
		initFragment(flag);
		initSlidingMenu();
		this.menuBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (menu != null) {
					menu.toggle();
				}
			}
		});
		// 网络连接判断
		if (!appContext.isNetworkConnected()) {
			ToastUtils.show(this, R.string.network_not_connected);
		} else {
//			//检查数据更新
//			UpdateDataManager.getUpdateManager().checkDataUpdate(this, false);
			}
			// 是否自动登录
				// 开线程去登录
		// 启动一个登录线程
//		appContext.new AutoLoginThread().start();
	}

	private void initFragment(int curFragment) {
		Fragment f = null;
		switch (curFragment) {
		case MAIN_INDEX:
			flag = MAIN_INDEX;
			f = new MainFragment();
			break;
		case MAIN_ACCOUNT:
			flag = MAIN_ACCOUNT;
			f = new UserInfoFragment();
			break;
		case MAIN_SETTING:
			f = new SettingFragment();
			flag = MAIN_SETTING;
			break;
		}
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_replace_layout, f).commit();
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.btn_home:
			moreBtn.setChecked(false);
			setBtn.setChecked(false);
			if(flag == MAIN_INDEX)
			{
				return;
			}
			flag = MAIN_INDEX;
			getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_replace_layout, new MainFragment()).commit();
			break;
		case R.id.btn_more:
			if(menu!=null)
			{
				menu.toggle();
			}
			break;
		case R.id.btn_setting:
			moreBtn.setChecked(false);
			homeBtn.setChecked(false);
			if(this.v!=null) this.v.hide();
			if(flag == MAIN_SETTING)
			{
				return;
			}
			flag = MAIN_SETTING;
			getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_replace_layout, new SettingFragment()).commit();
			break;
			
		}
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
	}
	public void showContent() {
		if (menu != null) {
			menu.toggle();
		}
	}
	public void hideNewTag()
	{
		if(v!=null)
		{
			v.hide();
		}
	}
	private void initViews() {
//		this.menuLayout = (LinearLayout) this.findViewById(R.id.menuLayout);
		this.menuBtn = (ImageButton) this.findViewById(R.id.menuBtn);
		this.moreBtn = (RadioButton) this.findViewById(R.id.btn_more);
		this.setBtn = (RadioButton) this.findViewById(R.id.btn_setting);
		this.homeBtn = (RadioButton) this.findViewById(R.id.btn_home);
		this.footer = (LinearLayout) this.findViewById(R.id.main_linearlayout_footer);
		this.moreBtn.setOnClickListener(this);
		this.setBtn.setOnClickListener(this);
		this.homeBtn.setOnClickListener(this);
	}

	// 监听按键
	public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) {
		if ((paramKeyEvent.getKeyCode() == 4)
				&& (paramKeyEvent.getRepeatCount() == 0)) {
			if (menu.isMenuShowing()) {
				menu.showContent();
				return true;
			}
			if (flag == MAIN_INDEX)
			{
//				UIHelper.Exit(this);
				ToastUtils.show(this, "退出");
				AppManager.getAppManager().AppExit(this);
			}
			else
				createMainFragment();
			return true;
		} else if ((paramKeyEvent.getKeyCode() == 82)) {
			menu.toggle();
		}
		return super.onKeyDown(paramInt, paramKeyEvent);
	}

	private void createMainFragment() {
		FragmentManager fm = getSupportFragmentManager();
		fm.beginTransaction()
				.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
				.replace(R.id.fragment_replace_layout, new MainFragment())
				.commit();
		flag = MAIN_INDEX;
		homeBtn.setChecked(true);
		setBtn.setChecked(false);
	}

	private void initSlidingMenu() {
		// 初始化滑动菜单
		Log.e("MainActivity", "initSlidingMenu");
		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.RIGHT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		//
		menu.setMenu(R.layout.menu_frame);
		// menuFragment = new SampleListFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, new MenuListFragment()).commit();
	}

	@Override
	public void onResume() {
		super.onResume();
		changeMenu();
		if(appContext.isHasNewVersion()||appContext.isHasNewData())
		{
			showNew();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public AppContext getAppContext() {
		return appContext;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 结束Activity&从堆栈中移除
		Log.e("MainActivity ", "onDestroy");
		AppManager.getAppManager().finishActivity(this);
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
		Log.e("MainActivity onActivityResult", arg1 + "");
		switch (arg1) {
		case 20: // 来自menu的登录
			if (flag != MAIN_ACCOUNT) {
				initFragment(MAIN_ACCOUNT);
				changeMenu();
				if (menu.isMenuShowing()) {
					menu.showContent();
				}
			} else {
				changeMenu();
			}
			break;
		case 30:
			if (menu.isMenuShowing()) {
				menu.toggle();
			}
			SettingFragment f = (SettingFragment) getSupportFragmentManager()
					.findFragmentById(R.id.fragment_replace_layout);
			f.setLoginTxt();
			changeMenu();
			break;
		case 0:
			break;
		}
	}

	public void changeMenu() {
		try {
			MenuListFragment f = (MenuListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.menu_frame);
			f.changeLoginState();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// menuFragment.changeLoginState();
	}
	public void showFooter(int flag)
	{
		this.footer.setVisibility(flag);
	}
//	private static class MyHandler extends Handler {
//		WeakReference<MainActivity> mActivity;
//
//		public MyHandler(MainActivity activity) {
//			mActivity = new WeakReference<MainActivity>(activity);
//		}
//
//		@Override
//		public void handleMessage(Message msg) {
//			MainActivity theActivity = mActivity.get();
//			switch (msg.what) {
//			case 1:
//				ParseResult pr = (ParseResult) msg.obj;
//				if (pr.Ok()) {
//					User user = (User) pr.obj;
//					theActivity.appContext.saveLoginInfo(user);
//					theActivity.changeMenu();
//				} else {
//					theActivity.appContext.setLoginState(AppContext.LOGIN_FAIL); // 登录失败
//					theActivity.changeMenu();
//				}
//				UIHelper.ToastMessage(theActivity, pr.getErrorMsg());
//				break;
//			case -1:
//				UIHelper.ToastMessage(theActivity, "亲,网络不给力");
//				theActivity.appContext.setLoginState(AppContext.LOGIN_FAIL);
//				theActivity.changeMenu();
//				break;
//			case 2:
//				UIHelper.ToastMessage(theActivity, "本地登录成功");
//				theActivity.appContext.setLoginState(AppContext.LOCAL_LOGINED);
//				theActivity.changeMenu();
//			}
//		}
//	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getRepeatCount() > 0
				&& event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	/**
	 * 轮询通知信息
	 */
//	private void foreachUserNotice() {
//		final int uid = appContext.getLoginUid();
//		final Handler handler = new Handler() {
//			public void handleMessage(Message msg) {
//				if (msg.what == 1) {
//					UIHelper.sendBroadCast(MainActivity.this, (Notice) msg.obj);
//				}
//				foreachUserNotice();// 回调
//			}
//		};
//		new Thread() {
//			public void run() {
//				Message msg = new Message();
//				try {
//					sleep(60 * 1000);
//					if (uid > 0) {
//						Notice notice = appContext.getUserNotice(uid);
//						msg.what = 1;
//						msg.obj = notice;
//					} else {
//						msg.what = 0;
//					}
//				} catch (AppException e) {
//					e.printStackTrace();
//					msg.what = -1;
//				} catch (Exception e) {
//					e.printStackTrace();
//					msg.what = -1;
//				}
//				handler.sendMessage(msg);
//			}
//		}.start();
//	}
	/**
	 * 本地登录（必须先在线登录一次）
	 * @param username 用户名
	 * @param password 密码
	 * @return
	 */
//	private boolean localLogin(String username, String password) {
//		if (StringUtils.isEmpty(username))
//			return false;
//		if (userDao == null) {
//			userDao = new UserDao(this);
//		}
//		User user = userDao.findByUsername(username);
////		System.out.println(user);
//		if (user != null) {
//			if (password.equals(new String(Base64.decode(
//					Base64.decode(user.getPassword(), 0), 0)))) {
//				appContext.saveLocalLoginInfo(username);
//				return true;
//			}
//		}
//		return false;
//	}
	/**
	 * 显示有更新
	 */
	private void showNew()
	{
		if(v == null)
		{
			 v = new BadgeView(this,setBtn);
			 v.setBackgroundResource(R.drawable.ic_redpoint);
			 v.setText("new");
			 v.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
		}
		v.show();
	}
}
