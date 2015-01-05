package com.examw.test.ui;

import java.lang.ref.WeakReference;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.examw.test.R;
import com.examw.test.app.AppConfig;
import com.examw.test.app.AppContext;
import com.examw.test.app.AppManager;
import com.examw.test.dao.UserDao;
import com.examw.test.domain.User;
import com.examw.test.model.FrontUserInfo;
import com.examw.test.model.Json;
import com.examw.test.support.ApiClient;
import com.examw.test.support.AppUpdateManager;
import com.examw.test.util.CyptoUtils;
import com.examw.test.util.StringUtils;
import com.examw.test.util.ToastUtils;
import com.examw.test.widget.BadgeView;
import com.slidingmenu.lib.SlidingMenu;

/**
 * 主界面
 * 
 * @author fengwei.
 * @since 2014年9月3日 下午3:18:19.
 */
public class MainActivity extends FragmentActivity implements OnClickListener {
	private SlidingMenu menu;
	// private LinearLayout menuLayout;
	private AppContext appContext;// 全局Context
	private AppConfig appConfig;
	private Handler mHandler = null;
	public static final int MAIN_SETTING = 3;
	public static final int MAIN_INDEX = 0;
	public static final int MAIN_ACCOUNT = 1;
	public static final int MAIN_ABOUT = 2;
	private int flag = MAIN_INDEX;
	private RadioButton homeBtn, moreBtn, setBtn;
	private LinearLayout footer;
	private BadgeView v;
	private AlertDialog exitDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.e("MainActivity", "onCreate");
		super.onCreate(savedInstanceState);
		// 添加Activity到堆栈
		AppManager.getAppManager().addActivity(this);
		appContext = (AppContext) getApplication();
		appConfig = AppConfig.getAppConfig(this);
		this.setContentView(R.layout.ui_main);
		mHandler = new MyHandler(this);
		initViews();
		initFragment(flag);
		initSlidingMenu();
		// 网络连接判断
		if (!appContext.isNetworkConnected()) {
			ToastUtils.show(this, R.string.network_not_connected);
		} else {
			// //检查数据更新
			// UpdateDataManager.getUpdateManager().checkDataUpdate(this,
			// false);

		}
		// 检查新版本
		if (appContext.isCheckUp() && !appContext.isAutoCheckuped()) {
			// 老式的自动更新
			AppUpdateManager.getUpdateManager().checkAppUpdate(this, false);
			appContext.setAutoCheckuped(true);
			// UmengUpdateAgent.update(this); //umeng update
		}
		// 是否自动登录
		if (appContext.isAutoLogin() && !appContext.isAutoLogined()) {
			// 开线程去登录
			new Thread() {
				public void run() {
					Message msg = mHandler.obtainMessage();
					String username = appConfig.get("user.account");
					String pwd = CyptoUtils.decode("changheng",
							appConfig.get("user.pwd"));
					try {
						Log.e("登录线程", "启动");
						appContext.setLoginState(AppContext.LOGINING); // 登录中
						Json result = ApiClient.login(appContext, username, pwd);
						msg.what = 1;
						if (result != null){
							//查询本地数据库用户信息
							User user = UserDao.findByUsername(username);
							if(result.isSuccess())	//远程登录成功
							{
								if(user == null)
								{
									user = (User) result.getData();
								}
								if(user.getProductUserId()==null)
								{
									FrontUserInfo userInfo = new FrontUserInfo();
									userInfo.setCode(((User) result.getData()).getUid());
									userInfo.setName(username);
									Json json = ApiClient.getProductUser(appContext, userInfo);
									//失败则登录失败
									if(json.isSuccess())
									{
										//保存至数据库
										user.setProductUserId(json.getData().toString());
										UserDao.saveOrUpdate(user);
										msg.what = 1;
									}else
									{
										msg.what = -2;	//登录失败
									}
								}
							}else	//远程登录不成功
							{
								msg.what = 0;	//登录失败
								msg.obj = result.getMsg();
							}
							msg.obj = user;
						}else	//登录失败
						{
							localLogin(msg, username, pwd);
						}
						/////////////////////////////////////////
					} catch (Exception e) {
						e.printStackTrace();
						//转本地登录
						localLogin(msg, username, pwd);
					}
					mHandler.sendMessage(msg);
				};
			}.start();
			appContext.setAutoLogined(true);
		}
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
		switch (v.getId()) {
		case R.id.btn_home:
			moreBtn.setChecked(false);
			setBtn.setChecked(false);
			if (flag == MAIN_INDEX) {
				return;
			}
			flag = MAIN_INDEX;
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.fragment_replace_layout, new MainFragment())
					.commit();
			break;
		case R.id.btn_more:
			moreBtn.setChecked(false);
			if (menu != null) {
				menu.toggle();
			}
			break;
		case R.id.btn_setting:
			moreBtn.setChecked(false);
			homeBtn.setChecked(false);
			if (this.v != null)
				this.v.hide();
			if (flag == MAIN_SETTING) {
				return;
			}
			flag = MAIN_SETTING;
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.fragment_replace_layout,
							new SettingFragment()).commit();
			break;

		}
	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	public void showContent() {
		if (menu != null) {
			menu.toggle();
		}
	}

	public void hideNewTag() {
		if (v != null) {
			v.hide();
		}
	}

	private void initViews() {
		// this.menuLayout = (LinearLayout) this.findViewById(R.id.menuLayout);
		((TextView)this.findViewById(R.id.examTitle)).setText(appConfig.get("exam_name"));
		this.moreBtn = (RadioButton) this.findViewById(R.id.btn_more);
		this.setBtn = (RadioButton) this.findViewById(R.id.btn_setting);
		this.homeBtn = (RadioButton) this.findViewById(R.id.btn_home);
		this.footer = (LinearLayout) this
				.findViewById(R.id.main_linearlayout_footer);
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
			if (flag == MAIN_INDEX) {
				ShowExitDialog();
			} else
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
		if (appContext.isHasNewVersion() || appContext.isHasNewData()) {
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

	public void showFooter(int flag) {
		this.footer.setVisibility(flag);
	}

	private static class MyHandler extends Handler {
		WeakReference<MainActivity> mActivity;

		public MyHandler(MainActivity activity) {
			mActivity = new WeakReference<MainActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			MainActivity theActivity = mActivity.get();
			switch (msg.what) {
			case 1:
				theActivity.appContext.saveLoginInfo((User)msg.obj);
				theActivity.changeMenu();
				ToastUtils.show(theActivity, "登录成功");
				break;
			case 0:
				ToastUtils.show(theActivity, msg.obj.toString());
				theActivity.appContext.setLoginState(AppContext.LOGIN_FAIL);
				theActivity.changeMenu();
				break;
			case 2:
				ToastUtils.show(theActivity, "本地登录成功");
				theActivity.appContext.saveLoginInfo((User)msg.obj);
				theActivity.appContext.setLoginState(AppContext.LOCAL_LOGINED);
				theActivity.changeMenu();
				break;
			case -2:
				ToastUtils.show(theActivity, "登录失败");
				theActivity.appContext.setLoginState(AppContext.LOGIN_FAIL);
				theActivity.changeMenu();
				break;
			}
		}
	}

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
	// private void foreachUserNotice() {
	// final int uid = appContext.getLoginUid();
	// final Handler handler = new Handler() {
	// public void handleMessage(Message msg) {
	// if (msg.what == 1) {
	// UIHelper.sendBroadCast(MainActivity.this, (Notice) msg.obj);
	// }
	// foreachUserNotice();// 回调
	// }
	// };
	// new Thread() {
	// public void run() {
	// Message msg = new Message();
	// try {
	// sleep(60 * 1000);
	// if (uid > 0) {
	// Notice notice = appContext.getUserNotice(uid);
	// msg.what = 1;
	// msg.obj = notice;
	// } else {
	// msg.what = 0;
	// }
	// } catch (AppException e) {
	// e.printStackTrace();
	// msg.what = -1;
	// } catch (Exception e) {
	// e.printStackTrace();
	// msg.what = -1;
	// }
	// handler.sendMessage(msg);
	// }
	// }.start();
	// }
	/**
	 * 本地登录（必须先在线登录一次）
	 * 
	 * @param username
	 *            用户名
	 * @param password
	 *            密码
	 * @return
	 */
	private void localLogin(Message msg,String username, String password){
		//转本地登录
		//查询本地数据库用户信息
		try{
		User user = UserDao.findByUsername(username);
		System.out.println("密码:"+password);
		System.out.println(new String(Base64.decode(
				Base64.decode(user.getPassword(), 0), 0)));
		if (user!=null && password.equals(new String(Base64.decode(
				Base64.decode(user.getPassword(), 0), 0)))) {
			Log.d("本地登录","本地登录密码能对上");
			if(user.getProductUserId()==null)
			{
				FrontUserInfo userInfo = new FrontUserInfo();
				userInfo.setCode(user.getUid());
				userInfo.setName(username);
				Json json = ApiClient.getProductUser(appContext, userInfo);
				//失败则登录失败
				if(json.isSuccess())
				{
					//保存至数据库
					user.setProductUserId(json.getData().toString());
					UserDao.saveOrUpdate(user);
					msg.what = 2;
				}else
				{
					msg.what = -2;	//登录失败
				}
			}else
			{
				msg.what = 2;
			}
			msg.obj = user;
		}else
		{
			Log.d("本地登录","本地登录失败,"+user.getProductUserId());
			//登录失败
			msg.what = -2;
		}
		}catch(Exception e)
		{
			e.printStackTrace();
			msg.what = -2;
		}
	}

	/**
	 * 显示有更新
	 */
	private void showNew() {
		if (v == null) {
			v = new BadgeView(this, setBtn);
			v.setBackgroundResource(R.drawable.ic_redpoint);
			v.setText("new");
			v.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
		}
		v.show();
	}

	/**
	 * 退出程序
	 * 
	 * @param cont
	 */
	public void ShowExitDialog() {
		if (exitDialog != null && !exitDialog.isShowing()) {
			exitDialog.show();
			return;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(R.string.app_menu_surelogout);
		builder.setPositiveButton(R.string.sure,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 退出
						AppManager.getAppManager().AppExit();
					}
				});
		builder.setNegativeButton(R.string.cancle,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		exitDialog = builder.create();
		exitDialog.show();
	}
}
