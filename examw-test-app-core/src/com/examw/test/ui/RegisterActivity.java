package com.examw.test.ui;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.examw.test.R;
import com.examw.test.app.AppConstant;
import com.examw.test.app.AppContext;
import com.examw.test.model.sync.JSONCallback;
import com.examw.test.model.sync.RegisterUser;
import com.examw.test.support.MsgHandler;
import com.examw.test.utils.DigestClientUtil;
import com.examw.test.widget.WaitingViewDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 用户注册。
 * 
 * @author jeasonyoung
 * @since 2015年7月8日
 */
public class RegisterActivity  extends Activity implements View.OnClickListener{
	private static final String TAG = "RegisterActivity";
	private DataInputViews dataInputViews;
	private MsgHandler handler;
	private WaitingViewDialog waitingViewDialog;
	/*
	 * 重载。
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "初始化创建Activity...");
		//加载数据文件
		this.setContentView(R.layout.ui_main_my_nologin_register);
		//初始化等待动画
		this.waitingViewDialog = new WaitingViewDialog(this);
		//初始化消息处理
		this.handler = new MsgHandler(this);
		//标题
		final TextView tvTitle = (TextView)this.findViewById(R.id.title);
		if(tvTitle != null){
			tvTitle.setText(getResources().getString(R.string.main_my_nologin_btnReg));
		}
		//返回按钮
		final Button btn = (Button)this.findViewById(R.id.btn_goback);
		btn.setOnClickListener(this);
		//0.初始化
		this.dataInputViews = new DataInputViews(this);
		//1.用户名
		this.dataInputViews.setUsername((EditText)this.findViewById(R.id.register_username));
		//2.密码
		this.dataInputViews.setPassword((EditText)this.findViewById(R.id.register_pwd));
		//3.重复密码
		this.dataInputViews.setRepassword((EditText)this.findViewById(R.id.register_repwd));
		//4.姓名
		this.dataInputViews.setName((EditText)this.findViewById(R.id.register_name));
		//5.电子邮箱
		this.dataInputViews.setEmail((EditText)this.findViewById(R.id.register_e_mail));
		//6.手机号码
		this.dataInputViews.setPhone((EditText)this.findViewById(R.id.register_tel));
		
		//注册按钮
		final Button btnSubmit = (Button)this.findViewById(R.id.register_btnSubmit);
		btnSubmit.setOnClickListener(this);
	}
	/*
	 * 按钮事件处理。
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		Log.d(TAG, "按钮事件处理..." + v);
		switch(v.getId()){
			case R.id.btn_goback:{//返回按钮处理
				Log.d(TAG, "返回按钮事件处理...");
				this.finish();
				break;
			}
			case R.id.register_btnSubmit:{//注册按钮处理
				//开启等待动画
				this.waitingViewDialog.show();
				//校验输入
				if(!this.dataInputViews.verification()){
					Log.d(TAG, "输入数据校验未通过...");
					this.waitingViewDialog.cancel();
					return;
				}
				//异步提交数据
				new RegisterUserDataAsyncTask().execute(this.dataInputViews.getRegisterUser());
				break;
			}
		}
	}
	/**
	 * 注册数据输入UI处理。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月9日
	 */
	private class DataInputViews implements TextView.OnEditorActionListener{
		private EditText username,password,repassword,name,email,phone;
		private final RegisterUser regUser;
		private final Resources resources;
		/**
		 * 构造函数。
		 * @param context
		 */
		public DataInputViews(Context context){
			Log.d(TAG, "初始化注册数据...");
			this.regUser = new RegisterUser(context);
			this.resources =context.getResources();
		}
		/**
		 * 设置用户名。
		 * @param username 
		 *	  用户名。
		 */
		public void setUsername(EditText username) {
			this.username = username;
			this.username.setOnEditorActionListener(this);
		}
		/**
		 * 设置密码。
		 * @param password 
		 *	  密码。
		 */
		public void setPassword(EditText password) {
			this.password = password;
			this.password.setOnEditorActionListener(this);
		}
		/**
		 * 设置重复密码。
		 * @param repassword 
		 *	  重复密码。
		 */
		public void setRepassword(EditText repassword) {
			this.repassword = repassword;
			this.repassword.setOnEditorActionListener(this);
		}
		/**
		 * 设置姓名。
		 * @param name 
		 *	  姓名。
		 */
		public void setName(EditText name) {
			this.name = name;
			this.name.setOnEditorActionListener(this);
		}
		/**
		 * 设置电子邮箱。
		 * @param email 
		 *	  电子邮箱。
		 */
		public void setEmail(EditText email) {
			this.email = email;
			this.email.setOnEditorActionListener(this);
		}
		/**
		 * 设置电话号码。
		 * @param phone 
		 *	  电话号码。
		 */
		public void setPhone(EditText phone) {
			this.phone = phone;
			this.phone.setOnEditorActionListener(this);
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
		//1.验证用户账号
		private boolean validateAccount(){
			Log.d(TAG, "验证用户账号...");
			//1.用户名
			this.regUser.setAccount(StringUtils.trimToEmpty(this.username.getText().toString()));
			//1.0验证为空
			if(StringUtils.isBlank(this.regUser.getAccount())){
				Log.d(TAG, "用户名为空!");
				this.username.setError(this.resources.getString(R.string.main_my_nologin_reg_username_error_blank));
				return false;
			}
			//1.1验证格式
			if(!this.validateRegex(this.regUser.getAccount(), this.resources.getString(R.string.main_my_nologin_reg_username_regex))){
				Log.d(TAG, "用户名格式不正确!");
				this.username.setError(this.resources.getString(R.string.main_my_nologin_reg_username_error_formatter));
				return false;
			}
			return true;
		}
		//2.验证密码
		private boolean validatePassword(){
			Log.d(TAG, "验证用户密码...");
			//2.密码
			this.regUser.setPassword(StringUtils.trimToEmpty(this.password.getText().toString()));
			//2.0验证为空
			if(StringUtils.isBlank(this.regUser.getPassword())){
				Log.d(TAG, "密码为空!");
				this.password.setError(this.resources.getString(R.string.main_my_nologin_reg_password_error_blank));
				return false;
			}
			//2.1验证格式
			if(!this.validateRegex(this.regUser.getPassword(), this.resources.getString(R.string.main_my_nologin_reg_password_regex))){
				Log.d(TAG, "密码格式错误!");
				this.password.setError(this.resources.getString(R.string.main_my_nologin_reg_password_error_formatter));
				return false;
			}
			return true;
		}
		//3.重复密码
		private boolean validateRePassword(){
			Log.d(TAG, "验证用户重复密码...");
			//3.重复密码
			String repwd = StringUtils.trimToEmpty(this.repassword.getText().toString());
			//3.0验证为空
			if(StringUtils.isBlank(repwd)){
				Log.d(TAG, "重复密码为空!");
				this.repassword.setError(this.resources.getString(R.string.main_my_nologin_reg_repassword_error_blank));
				return false;
			}
			//3.1比较密码是否一致
			if(!StringUtils.equals(this.regUser.getPassword(), repwd)){
				Log.d(TAG, "密码不一致!");
				this.repassword.setError(this.resources.getString(R.string.main_my_nologin_reg_repassword_error_equals));
				return false;
			}
			return true;
		}
		//4.验证用户姓名
		private boolean validateName(){
			Log.d(TAG, "验证用户姓名...");
			this.regUser.setUsername(StringUtils.trimToEmpty(this.name.getText().toString()));
			//4.0验证为空
			if(StringUtils.isBlank(this.regUser.getUsername())){
				Log.d(TAG, "真实姓名为空!");
				this.name.setError(this.resources.getString(R.string.main_my_nologin_reg_name_error_blank));
				return false;
			}
			//4.1验证格式
			if(!this.validateRegex(this.regUser.getUsername(), this.resources.getString(R.string.main_my_nologin_reg_name_regex))){
				Log.d(TAG, "姓名应为汉字!");
				this.name.setError(this.resources.getString(R.string.main_my_nologin_reg_name_error_formatter));
				return false;
			}
			return true;
		}
		//5.验证电子邮箱
		private boolean validateEmail(){
			Log.d(TAG, "验证电子邮箱...");
			this.regUser.setEmail(StringUtils.trimToEmpty(this.email.getText().toString()));
			//5.0验证为空
			if(StringUtils.isBlank(this.regUser.getEmail())){
				Log.d(TAG, "电子邮箱为空!");
				this.email.setError(this.resources.getString(R.string.main_my_nologin_reg_email_error_blank));
				return false;
			}
			//5.1验证格式
			if(!this.validateRegex(this.regUser.getEmail(), this.resources.getString(R.string.main_my_nologin_reg_email_regex))){
				Log.d(TAG, "电子邮箱格式不正确!");
				this.email.setError(this.resources.getString(R.string.main_my_nologin_reg_email_error_formatter));
				return false;
			}
			return true;
		}
		//6.验证电话号码
		private boolean validatePhone(){
			Log.d(TAG, "验证电话号码...");
			this.regUser.setPhone(StringUtils.trimToEmpty(this.phone.getText().toString()));
			//6.0验证为空
			if(StringUtils.isBlank(this.regUser.getPhone())){	
				Log.d(TAG, "电话号码为空!");
				this.phone.setError(this.resources.getString(R.string.main_my_nologin_reg_phone_error_blank));
				return false;
			}
			//6.1验证格式
			if(!this.validateRegex(this.regUser.getPhone(), this.resources.getString(R.string.main_my_nologin_reg_phone_regex))){
				Log.d(TAG, "电话号码格式不正确!");
				this.phone.setError(this.resources.getString(R.string.main_my_nologin_reg_phone_error_formatter));
				return false;
			}
			return true;
		}
		/**
		 * 校验输入。
		 * @return
		 */
		public boolean verification(){
			Log.d(TAG, "验证注册输入..");
			boolean result = true;
			//1.验证用户账号
			if(!(result = this.validateAccount())){
				return result;
			}
			//2.验证密码
			if(!(result = this.validatePassword())){
				return result;
			}
			//3.重复密码
			if(!(result = this.validateRePassword())){
				return result;
			}
			//4.验证用户姓名
			if(!(result = this.validateName())){
				return result;
			}
			//5.验证电子邮箱
			if(!(result = this.validateEmail())){
				return result;
			}
			//6.验证电话号码
			if(!(result = this.validatePhone())){
				return result;
			}
			//关闭键盘
			this.closeSoftInputFromView(this.phone);
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
					case R.id.register_username:{//1.用户名
						if(result = this.validateAccount()){
							this.closeSoftInputFromView(v);
						}
						break;
					}
					case R.id.register_pwd:{//2.密码
						if(result = this.validatePassword()){
							this.closeSoftInputFromView(v);
						}
						break;
					}
					case R.id.register_repwd:{//3.重复密码
						if(result = this.validateRePassword()){
							this.closeSoftInputFromView(v);
						}
						break;
					}
					case R.id.register_name:{//4.姓名
						if(result = this.validateName()){
							this.closeSoftInputFromView(v);
						}
						break;
					}
					case R.id.register_e_mail:{//5.电子邮箱
						if(result = this.validateEmail()){
							this.closeSoftInputFromView(v);
						}
						break;
					}
					case R.id.register_tel:{//6.手机号码
						if(result = this.validatePhone()){
							this.closeSoftInputFromView(v);
						}
						break;
					}
				}
				return result;
			}
			return false;
		}
		
		/**
		 * 获取注册数据。
		 * @return
		 */
		public RegisterUser getRegisterUser(){
			Log.d(TAG, "获取注册数据...");
			return this.regUser;
		}
	}
	/**
	 * 异步用户注册。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月13日
	 */
	private class RegisterUserDataAsyncTask extends AsyncTask<RegisterUser, Void, Boolean>{
		/*
		 * 重载后台数据加载。
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Boolean doInBackground(RegisterUser... params) {
			try {
				//0.初始化应用
				AppContext app = (AppContext)getApplicationContext();
				if(app == null){
					Log.d(TAG, "获取全局应用上下文失败!");
					return false;
				}
				//1.检查网络
				if(!app.hasNetworkConnected()){
					Log.d(TAG, "网络不存在!");
					handler.sendMessage("请检查网络!");
					return false;
				}
				//2.提交注册数据
				String result = DigestClientUtil.sendDigestRequest(AppConstant.APP_API_USERNAME, AppConstant.APP_API_PASSWORD, "POST",
						AppConstant.APP_API_REGISTER_URL, params[0].toString());
				if(StringUtils.isBlank(result)){
					Log.d(TAG, "反馈数据为空!");
					return false;
				}
				//3.反序列化反馈数据
				Gson gson = new Gson();
				Type type = new TypeToken<JSONCallback<Object>>(){}.getType();
				JSONCallback<Object> callback = gson.fromJson(result, type);
				if(callback.getSuccess()){
					Log.d(TAG, "注册成功!");
					handler.sendMessage("注册成功!");
					return true;
				}else {
					Log.d(TAG, "注册失败:" + callback.getMsg());
					handler.sendMessage(callback.getMsg());
					return false;
				}
			} catch (Exception e) {
				Log.e(TAG, "提交注册数据异常:" + e.getMessage(), e);
			}
			return false;
		}
		/*
		 * 重载主线程UI更新。
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Boolean result) {
			//0.关闭等待动画
			waitingViewDialog.cancel();
			//1.注册成功,activity跳转
			if(result){
				//跳转到登陆界面
				startActivity(new Intent(getApplicationContext(), LoginActivity.class));
				//关闭当前activity
				finish();
			}
		}
	}
}