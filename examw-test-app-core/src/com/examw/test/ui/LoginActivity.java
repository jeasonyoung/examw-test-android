package com.examw.test.ui;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.examw.test.R;
import com.examw.test.app.AppConstant;
import com.examw.test.app.AppContext;
import com.examw.test.app.UserAccount;
import com.examw.test.model.sync.JSONCallback;
import com.examw.test.model.sync.LoginUser;
import com.examw.test.support.MsgHandler;
import com.examw.test.utils.DigestClientUtil;
import com.examw.test.widget.WaitingViewDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 登录界面
 * @author fengwei.
 * @since 2014年12月1日 上午9:42:52.
 */
public class LoginActivity extends Activity implements View.OnClickListener{
	private static final String TAG = "LoginActivity";
	private MsgHandler handler;
	private WaitingViewDialog waitingViewDialog;
	private DataInputViews dataInputViews;
	/*
	 * 重载创建。
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "初始化创建Activity...");
		//加载布局...
		this.setContentView(R.layout.ui_main_my_nologin_login);
		//初始化等待动画
		this.waitingViewDialog = new WaitingViewDialog(this);
		//初始化消息处理
		this.handler = new MsgHandler(this);
		//加载导航处理
		final TextView tvTitle = (TextView)this.findViewById(R.id.title);
		if(tvTitle != null){
			tvTitle.setText(this.getResources().getString(R.string.main_my_nologin_btnLogin));
		}
		//加载返回按钮
		final View btnBack = this.findViewById(R.id.btn_goback);
		btnBack.setOnClickListener(this);
		//输入处理
		this.dataInputViews = new DataInputViews(this);
		//账号
		this.dataInputViews.setAccount((EditText)this.findViewById(R.id.login_account));
		//密码
		this.dataInputViews.setPassword((EditText)this.findViewById(R.id.login_password));
		
		//登录按钮
		final View btnSubmit = this.findViewById(R.id.login_btnSubmit);
		btnSubmit.setOnClickListener(this);
		//注册按钮
		final View btnRegister = this.findViewById(R.id.login_btnRegister);
		btnRegister.setOnClickListener(this);
	}
	/*
	 * 按钮事件处理。
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		Log.d(TAG, "按钮事件处理..." + v);
		switch(v.getId()){
			case R.id.btn_goback:{//返回
				Log.d(TAG, "返回按钮事件处理...");
				this.finish();
				break;
			}
			case R.id.login_btnSubmit:{//登录
				Log.d(TAG, "登录处理...");
				//开启等待动画
				this.waitingViewDialog.show();
				//校验输入
				if(!this.dataInputViews.verification()){
					Log.d(TAG, "输入数据校验未通过...");
					this.waitingViewDialog.cancel();
					return;
				}
				//异步登录处理
				new LoginUserDataAsyncTask(this).execute(this.dataInputViews.getLoginUser());
				break;
			}
			case R.id.login_btnRegister:{//注册
				//启动注册activity
				this.startActivity(new Intent(this, RegisterActivity.class));
				break;
			}
		}
	}
	/**
	 * 登录数据输入处理。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月14日
	 */
	private class DataInputViews implements TextView.OnEditorActionListener{
		private EditText account,password;
		private final LoginUser loginUser; 
		private final Resources resources;		
		/**
		 * 构造函数。
		 * @param context
		 * 上下文。
		 */
		public DataInputViews(Context context){
			this.loginUser = new LoginUser(context);
			this.resources = context.getResources();
		}
		/**
		 * 获取登录数据。
		 * @return loginUser
		 * 登录数据。
		 */
		public LoginUser getLoginUser() {
			return loginUser;
		}
		/**
		 * 设置用户账号输入。
		 * @param account 
		 *	  用户账号输入。
		 */
		public void setAccount(EditText account) {
			this.account = account;
			this.account.setOnEditorActionListener(this);
		}
		/**
		 * 设置用户密码输入。
		 * @param password 
		 *	  用户密码输入。
		 */
		public void setPassword(EditText password) {
			this.password = password;
			this.password.setOnEditorActionListener(this);
		}
		//正则表达式验证
		private boolean validateRegex(final String input, final String regex){
			if(StringUtils.isBlank(regex)){
				return true;
			}
			if(StringUtils.isNotBlank(input)){
				return Pattern.compile(regex).matcher(input).matches();
			}
			return false;
		}
		//校验账号
		private boolean validateAccount(){
			Log.d(TAG, "验证用户账号...");
			//1.用户名
			this.loginUser.setAccount(StringUtils.trimToEmpty(this.account.getText().toString()));
			//1.0验证为空
			if(StringUtils.isBlank(this.loginUser.getAccount())){
				Log.d(TAG, "用户名为空!");
				this.account.setError(this.resources.getString(R.string.main_my_nologin_reg_username_error_blank));
				return false;
			}
			//1.1验证格式
			if(!this.validateRegex(this.loginUser.getAccount(), this.resources.getString(R.string.main_my_nologin_reg_username_regex))){
				Log.d(TAG, "用户名格式不正确!");
				this.account.setError(this.resources.getString(R.string.main_my_nologin_reg_username_error_formatter));
				return false;
			}
			return true;
		}
		//2.验证密码
		private boolean validatePassword(){
			Log.d(TAG, "验证用户密码...");
			//2.密码
			this.loginUser.setPassword(StringUtils.trimToEmpty(this.password.getText().toString()));
			//2.0验证为空
			if(StringUtils.isBlank(this.loginUser.getPassword())){
				Log.d(TAG, "密码为空!");
				this.password.setError(this.resources.getString(R.string.main_my_nologin_reg_password_error_blank));
				return false;
			}
			//2.1验证格式
			if(!this.validateRegex(this.loginUser.getPassword(), this.resources.getString(R.string.main_my_nologin_reg_password_regex))){
				Log.d(TAG, "密码格式错误!");
				this.password.setError(this.resources.getString(R.string.main_my_nologin_reg_password_error_formatter));
				return false;
			}
			return true;
		}
		/**
		 * 校验输入。
		 * @return
		 */
		public boolean verification(){
			Log.d(TAG, "校验输入...");
			boolean result = true;
			//1.验证用户账号
			if(!(result = this.validateAccount())){
				return result;
			}
			//2.验证密码
			if(!(result = this.validatePassword())){
				return result;
			}
			//关闭键盘
			this.closeSoftInputFromView(this.password);
			return true;
		}
		//关闭键盘
		private void closeSoftInputFromView(View v){
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		}
		/*
		 * 文本编辑器处理。
		 * @see android.widget.TextView.OnEditorActionListener#onEditorAction(android.widget.TextView, int, android.view.KeyEvent)
		 */
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if(actionId == EditorInfo.IME_ACTION_GO){
				this.closeSoftInputFromView(v);
				return true;
			}
			if(actionId == EditorInfo.IME_ACTION_NEXT){
				boolean result = true;
				switch(v.getId()){
					case R.id.login_account:{//1.用户名
						if(result = this.validateAccount()){
							this.closeSoftInputFromView(v);
						}
						break;
					}
					case R.id.login_password:{//2.密码
						if(result = this.validatePassword()){
							this.closeSoftInputFromView(v);
						}
						break;
					}
				}
				return result;
			}
			return false;
		}
		
		
	}
	/**
	 * 异步登录处理。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月14日
	 */
	private class LoginUserDataAsyncTask extends AsyncTask<LoginUser, Void, Boolean>{
		private final WeakReference<Context> refContext;
		/**
		 * 构造函数。
		 * @param context
		 */
		public LoginUserDataAsyncTask(final Context context){
			this.refContext = new WeakReference<Context>(context);
		}
		/*
		 * 后台线程处理。
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Boolean doInBackground(LoginUser... params) {
			try{
				Log.d(TAG, "后台异步线程处理登录...");
				Context context = this.refContext.get();
				if(context == null){
					handler.sendMessage("获取上下文失败!");
					return false;
				}
				AppContext app = (AppContext)context.getApplicationContext();
				if(app == null){
					handler.sendMessage("获取应用上下文失败!");
					return false;
				}
				//检查网络
				if(!app.hasNetworkConnected()){//没有网络本地验证
					UserAccount account = UserAccount.loadAccount(params[0].getAccount());
					if(account == null){
						handler.sendMessage("请检查网络!");
						return false;
					}
					if(!account.validatePassword(params[0].getPassword())){
						handler.sendMessage("密码错误!");
						return false;
					}
					//设置为当前用户
					app.changedCurrentUser(account);
					Log.d(TAG, "设置当前用户:" + account);
					return true;
				}else {//网络验证用户登录
					//提交用户登录数据
					String result = DigestClientUtil.sendDigestRequest(AppConstant.APP_API_USERNAME, 
							AppConstant.APP_API_PASSWORD, "POST", AppConstant.APP_API_LOGIN_URL, params[0].toString());
					if(StringUtils.isBlank(result)){
						Log.d(TAG, "反馈数据为空!");
						handler.sendMessage("服务器未响应!");
						return false;
					}
					//反序列化反馈数据
					Gson gson = new Gson();
					Type type = new TypeToken<JSONCallback<String>>(){}.getType();
					JSONCallback<String> callback = gson.fromJson(result, type);
					if(callback.getSuccess()){
						Log.d(TAG, "验证成功!");
						//初始化用户信息
						UserAccount account = new UserAccount(callback.getData(), params[0].getAccount());
						account.updatePassword(params[0].getPassword());
						//设置为当前用户
						app.changedCurrentUser(account);
						
						handler.sendMessage("验证成功!");
						return true;
					}else {
						Log.d(TAG, "验证失败:" + callback.getMsg());
						handler.sendMessage(callback.getMsg());
						return false;
					}
				}
			}catch(Exception e){
				Log.e(TAG, "处理登录异常:" + e.getMessage(), e);
			}
			return false;
		}
		/*
		 * 前台主线处理UI
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Boolean result) {
			 Log.d(TAG, "前台主线程处理...");
			 //关闭等待动画
			 waitingViewDialog.cancel();
			 //登录成功跳转
			 if(result){
				 Log.d(TAG, "登录成功跳转到");
				 //发送广播
				 sendBroadcast(new Intent(MainMyFragment.BROADCAST_LOGIN_ACTION));
				//关闭activity
				 finish();
			 }
		}
	}
}