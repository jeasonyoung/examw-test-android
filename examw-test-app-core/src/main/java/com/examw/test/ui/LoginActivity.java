package com.examw.test.ui;

import java.lang.ref.WeakReference;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.examw.test.R;
import com.examw.test.app.AppConfig;
import com.examw.test.app.AppContext;
import com.examw.test.dao.UserDao;
import com.examw.test.domain.User;
import com.examw.test.model.FrontUserInfo;
import com.examw.test.model.Json;
import com.examw.test.support.ApiClient;
import com.examw.test.support.LoginTips;
import com.examw.test.util.LogUtil;
import com.examw.test.util.ToastUtils;

/**
 * 登录界面
 * @author fengwei.
 * @since 2014年12月1日 上午9:42:52.
 */
public class LoginActivity extends BaseActivity implements TextWatcher,
		OnClickListener {
	private static final String TAG = "LoginActivity";
	private AutoCompleteTextView usernameText;
	private String[] items;// 适配autoCompleteTextView的数据
	private EditText pwdText;
	private ProgressDialog o;
	private Handler handler;
	private CheckBox rememeberCheck;
	private CheckBox autoLogin;
	private Button localLoginBtn;
	private String username;
	private String password;
	private SharedPreferences share;
	private SharedPreferences share2;
	private AppConfig appConfig;
	private AppContext appContext;
	private int curLoginType;
	private Class<?> fromClass;
	private String actionName;
	private InputMethodManager imm;
	public final static int LOGIN_OTHER = 0x00;
	public final static int LOGIN_MAIN = 0x01;
	public final static int LOGIN_SETTING = 0x02;
	public final static int LOGIN_POST_PUB = 0x05;
	public final static int LOGIN_POST_REPLY = 0x06;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_login);
		appConfig = AppConfig.getAppConfig(this);
		appContext = (AppContext) this.getApplication();
		curLoginType = this.getIntent().getIntExtra("loginFrom", LOGIN_OTHER);
		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		try {
			fromClass = Class.forName(this.getIntent().getStringExtra(
					"className"));
			actionName = this.getIntent().getStringExtra("actionName");
		} catch (Exception e) {
			e.printStackTrace();
		}
		initView();
	}

	@Override
	public void afterTextChanged(Editable s) {
		String name = usernameText.getText().toString();
		pwdText.setText(new String(Base64.decode(
				Base64.decode(share.getString(name, ""), 0), 0)));
		if (pwdText.getText().toString().length() > 0)
			pwdText.requestFocus();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	// click事件
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_goback:
			this.finish();
			break;
		case R.id.registerBtn:
			gotoRegister();
			break;
		case R.id.btnLogin:
			login();
			break;
		case R.id.btnLocalLogin:
			localLogin();
			break;
		}
	}

	// 登录方法
	private void login() {
		// 隐藏键盘
		if (imm.isActive(usernameText)) {
			imm.hideSoftInputFromWindow(usernameText.getWindowToken(), 0);
		}
		if (imm.isActive(pwdText)) {
			imm.hideSoftInputFromWindow(pwdText.getWindowToken(), 0);
		}
		username = usernameText.getText().toString().trim();
		password = pwdText.getText().toString().trim();
		// 检查输入
		if (checkInput(username, password)) {
			// 检查网络
			if (appContext.isNetworkConnected()) {
				// 提示登陆中
				if (appContext.getLoginState() == AppContext.LOGINING) {
					if (o != null) {
						o.show();
						return;
					}
				}
				o = ProgressDialog.show(LoginActivity.this, null, "登录中请稍候",
						true, true);
				o.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				new Thread() {
					public void run() {
						try {
							LogUtil.d("开线程进行登录");
							//设置登录状态为正在登陆
							appContext.setLoginState(AppContext.LOGINING);// 正在登录
							//解析登陆返回结果
							Json result = ApiClient.login_proxy(appContext, username, password);
							Message message = handler.obtainMessage();
							if(result == null)
							{
								message.what = 0;
								message.obj = "用户登录失败";
								handler.sendMessage(message); //登录失败
								return;
							}
							if (result.isSuccess()) { // 登陆成功
								//远程去获取productUser的信息并且保存
								//User user = (User) result.getData();
								//FrontUserInfo userInfo = new FrontUserInfo();
								//userInfo.setCode(user.getUid());
								//userInfo.setName(username);
								//Json json = ApiClient.getProductUser(appContext, userInfo);
								//if(json.isSuccess())
								//{
								// 是否记住我
								if (isRememberMe()) {
									saveSharePreferences();
								}
								// 保存是否自动登录的信息
								saveAutoLoginPreferences(isAutoLogin());
								// 保存信息至数据库[创建属于用户的数据库]
								//修改为代理登陆start
								User user = new User();
								user.setUsername(username);
								user.setPassword(password);
								user.setProductUserId(result.getData().toString());
								//修改为代理登陆end
								saveToLocaleDB(user);
								message.what = 1;
								message.obj = user;
								//}else
								//{
								//	message.what = 0;
								//	message.obj = "获取用户ID失败";
								//}
							}else{
								message.what = 0;
								//message.obj = LoginTips.getLoginTip((Integer) result.getData(), null);
								message.obj = result.getMsg();
							}
							handler.sendMessage(message); //登录失败
						} catch (Exception e) {
							e.printStackTrace();
							handler.sendEmptyMessage(-1); // 连接问题
						}
					};
				}.start();
			} else {
				ToastUtils.show(this, "无法连接,请检查网络...");
				if (appContext.getLoginState() != AppContext.LOCAL_LOGINED
						|| "sysnc".equals(curLoginType)) // 本地已经登录就不再显示
					localLoginBtn.setVisibility(View.VISIBLE);
			}
		}
	}
	private void localLogin() {
		username = usernameText.getText().toString().trim();
		password = pwdText.getText().toString().trim();
		if (checkInput(username, password)) {
			// String name = usernameText.getText().toString();
			User user = UserDao.findByUsername(username);
			if (user != null) {
				String password = pwdText.getText().toString();
				if (password.equals(user.getPassword())) {
					appContext.saveLocalLoginInfo(username);
					ToastUtils.show(this,"本地登录成功");
					if (fromClass == null) {
						LoginActivity.this.finish(); // 找不到类直接finish
					} else {
						this.startActivity();
					}
				} else {
					ToastUtils.show(this,"请先在线登录");
				}
			} else {
				ToastUtils.show(this,"请先在线登录");
			}
		}
	}
	// 检查输入 check input
	private boolean checkInput(String username, String password) {
		if ("".equals(username.trim()) || "".equals(password.trim())) {
			Toast.makeText(LoginActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		return true;
	}
	// 是否记住密码
	private boolean isRememberMe() {
		return rememeberCheck.isChecked();
	}
	// 是否自动登录
	private boolean isAutoLogin() {
		return autoLogin.isChecked();
	}
	// 保存信息
	private void saveSharePreferences() {
		share.edit()
				.putString(
						usernameText.getText().toString(),
						Base64.encodeToString(
								Base64.encode(password.getBytes(), 0), 0))
				.commit();
		share2.edit().putString("n", usernameText.getText().toString())
				.commit();
		share2.edit()
				.putString(
						"p",
						Base64.encodeToString(
								Base64.encode(password.getBytes(), 0), 0))
				.commit();
	}
	// 保存自动登录信息
	private void saveAutoLoginPreferences(boolean flag) {
		appConfig.set(AppConfig.CONF_AUTOLOGIN, String.valueOf(flag));
	}
	// 注册
	private void gotoRegister() {
		this.startActivity(new Intent(this, RegisterActivity.class));
	}

	// 保存用户信息至本地数据库
	public void saveToLocaleDB(User user) {
		try {
			UserDao.saveOrUpdate(user);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	// 初始化组件
	private void initView() {
		usernameText = (AutoCompleteTextView) this
				.findViewById(R.id.usernameText);// 用户名
		pwdText = (EditText) this.findViewById(R.id.pwdText);// 密码
		this.findViewById(R.id.btn_goback).setOnClickListener(this);
		((TextView) this.findViewById(R.id.title)).setText("登录");
		this.findViewById(R.id.btnLogin).setOnClickListener(this);// 登录按钮
		localLoginBtn = (Button) this.findViewById(R.id.btnLocalLogin); // 本地登录
		rememeberCheck = (CheckBox) this.findViewById(R.id.rememeberCheck);// 记住密码
		autoLogin = (CheckBox) this.findViewById(R.id.cbAutoLogin); // 自动登录
		this.findViewById(R.id.registerBtn).setOnClickListener(this);
		// userdao = new UserDao(new MyDBHelper(this)); //������ݿ�
		share = getSharedPreferences("passwordfile", 0);
		share2 = getSharedPreferences("abfile", 0);
		items = share.getAll().keySet().toArray(new String[0]);
		usernameText.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, items));
		pwdText.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_GO)
					// 去登陆
					login();
				return true;
			}
		});
		usernameText.addTextChangedListener(this);
		localLoginBtn.setOnClickListener(this);
		handler = new MyHandler(this);
	}

	static class MyHandler extends Handler {
		WeakReference<LoginActivity> mActivity;

		public MyHandler(LoginActivity activity) {
			mActivity = new WeakReference<LoginActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			LoginActivity login = mActivity.get();
			if (login.o != null && login.o.isShowing()) {
				login.o.dismiss();
			}
			switch (msg.what) {
			case 1:
				// 登录成功
				User result = (User) msg.obj;
					login.appContext.saveLoginInfo(result);
					int code = 0;
					if (login.curLoginType == LOGIN_MAIN) {
						code = 20;
						Intent intent = new Intent();
						login.setResult(code, intent);
						login.finish();
					} else if (login.curLoginType == LOGIN_SETTING) {
						code = 30;
						Intent intent = new Intent();
						login.setResult(code, intent);
						login.finish();
					}
//					else if (login.curLoginType == LOGIN_POST_PUB) {
//						Intent intent = new Intent(login,
//								ForumPostPubActivity.class);
//						login.startActivity(intent);
//						login.finish();
//					} 
					else if (login.curLoginType == LOGIN_POST_REPLY) {
						code = 40;
						Intent intent = new Intent();
						login.setResult(code, intent);
						login.finish();
					} else {
						// 登录之后,
						if (login.fromClass == null) {
							login.finish(); // 找不到类直接finish
						} else{
							login.startActivity();
						}
					}
					break;
			case 0:
				// 修改登录状态
				login.appContext.setLoginState(AppContext.LOGIN_FAIL);
				ToastUtils.show(login, "登录失败 "+msg.obj);
				break;
			case -1:
				// 修改登录状态
				login.appContext.setLoginState(AppContext.LOGIN_FAIL);
				Toast.makeText(login, "无法连接服务器", Toast.LENGTH_SHORT).show();
				login.showLocalLoginBtn();
				break;
			}
		}
	}
	private void showLocalLoginBtn() {
		User user = UserDao.findByUsername(username);
		if (user != null)
			localLoginBtn.setVisibility(View.VISIBLE);
	}
	private void startActivity()
	{
		Intent intent = new Intent(this, this.fromClass);
		if (this.actionName != null) {
			intent.putExtra("actionName", this.actionName);
		}
		this.startActivity(intent);
		this.finish();
	}
	// 初始化输入框
	@Override
	public void onResume() {
		super.onResume();
		this.usernameText.setText(share2.getString("n", ""));
		String pwd = share2.getString("p", "");
		this.pwdText
				.setText(new String(Base64.decode(Base64.decode(pwd, 0), 0)));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (o != null) {
			o.dismiss();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}
}
