package com.examw.test.ui;

import java.lang.ref.WeakReference;
import java.util.regex.Pattern;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.examw.test.R;
import com.examw.test.app.AppContext;
import com.examw.test.domain.User;
import com.examw.test.exception.AppException;
import com.examw.test.model.Json;
import com.examw.test.support.ApiClient;
import com.examw.test.support.LoginTips;
import com.examw.test.util.StringUtils;
import com.examw.test.widget.ImgRightEditText;

/**
 * 注册
 * @author fengwei.
 * @since 2014年12月1日 上午11:52:53.
 */
public class RegisterActivity  extends BaseActivity implements OnClickListener{
	private TextView usernameInfo,nameInfo, pwdInfo, pwd2Info, emailInfo, phoneInfo;
	private ImgRightEditText usernameView,nameView, pwdView, pwd2View, emailView, phoneView;
	private ProgressDialog dialog;
	private Handler handler;
	private SharedPreferences abfile;
	private AppContext appContext;
	private String username,pwd,name,phone,email;
	private InputMethodManager imm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.ui_register);
		appContext = (AppContext) getApplication();
		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		initViews();
	}

	private void initViews() {
		this.nameInfo = (TextView) this.findViewById(R.id.regist_tvName);
		this.usernameInfo = (TextView) this.findViewById(R.id.regist_tvUserName);
		this.pwdInfo = (TextView) this.findViewById(R.id.regist_tvPassword);
		this.pwd2Info = (TextView) this
				.findViewById(R.id.regist_tvConfirimPass);
		this.emailInfo = (TextView) this.findViewById(R.id.regist_tvEmail);
		this.phoneInfo = (TextView) this.findViewById(R.id.regist_tvPhone);

		this.nameView = (ImgRightEditText) this
				.findViewById(R.id.regist_name);
		this.usernameView = (ImgRightEditText) this
				.findViewById(R.id.regist_etUserName);
		this.pwdView = (ImgRightEditText) this
				.findViewById(R.id.regist_etPassword);
		this.pwd2View = (ImgRightEditText) this
				.findViewById(R.id.regist_etConfirimPass);
		this.emailView = (ImgRightEditText) this
				.findViewById(R.id.regist_email);
		this.phoneView = (ImgRightEditText) this.findViewById(R.id.regist_tel);
		this.findViewById(R.id.btn_goback).setOnClickListener(this);
		this.findViewById(R.id.regist_btnSubmit).setOnClickListener(this);
		handler = new MyHandler(this);
		abfile = getSharedPreferences("abfile", 0);
		usernameView.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus)
				{
					final String str1 = usernameView.getText().toString().trim();
					boolean bool6 = Pattern.compile("^[_,0-9,a-z,A-Z]+$").matcher(str1)
							.matches();
					if (str1.equals("")) {
						showInfo("用户名不能为空!",usernameInfo,usernameView,0);
						return;
					}
					if (!bool6) {
						showInfo("用户名只能使用字母,数字,下划线'_'组成!",usernameInfo,usernameView,0);
						return;
					}
					if ((str1.length() < 4) || (str1.length()> 20))
				    {
						showInfo("用户名应在4-20位之间！",usernameInfo,usernameView,0);
						return;
				    }
					showInfo("*用户名",usernameInfo,usernameView,1);
					//开线程去验证用户名是否被占用
				}
			}
		});
		pwdView.addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				String str3 = pwdView.getText().toString().trim();
				String str4 = pwd2View.getText().toString().trim();
				if(!str4.equals(str3))
				{
					showInfo("*确认密码:两次输入不一致",pwd2Info,pwd2View,0);
				}else
				{
					showInfo("*确认密码:",pwd2Info,pwd2View,1);
				}
				boolean bool7 = Pattern.compile("^[0-9,a-z,A-Z]+$").matcher(str3).matches();
				if (str3.equals("")) {
					showInfo("密码不能为空!",pwdInfo,pwdView,0);
					return ;
				}
				if (!bool7) {
					showInfo("密码只能使用字母,数字组成!",pwdInfo,pwdView,0);
					return ;
				}
				if ((str3.length() < 4) || (str3.length()> 20)) {
					showInfo("密码4-20位字符!",pwdInfo,pwdView,0);
					return ;
				}
				showInfo("*密码:",pwdInfo,pwdView,1);
			}
		});
		pwd2View.addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(Editable s) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String str3 = pwdView.getText().toString().trim();
				String str4 = pwd2View.getText().toString().trim();
				if(!str4.equals(str3))
				{
					showInfo("*确认密码:两次输入不一致",pwd2Info,pwd2View,0);
					return;
				}else
				{
					showInfo("*确认密码:",pwd2Info,pwd2View,1);
					return;
				}
			}
		});
		nameView.addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(Editable s) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String str2 = nameView.getText().toString().trim();
//				boolean bool1 = Pattern
//						.compile(
//								"^([a-z0-9A-Z]+[-|_\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$")
//						.matcher(str2).matches();
				boolean bool1 = Pattern.compile("^[\\u4e00-\\u9fa5]{2,6}$").matcher(str2).matches();
				if (str2.equals("")) {
					showInfo("姓名不能为空!",nameInfo,nameView,0);
					return ;
				}
				if (!bool1) {
					showInfo("姓名格式错误!",nameInfo,nameView,0);
					return ;
				}
				showInfo("*真实姓名:",nameInfo,nameView,1);
			}
		});
		emailView.addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(Editable s) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String str2 = emailView.getText().toString().trim();
				if (str2.equals("")) {
					showInfo("email不能为空!",emailInfo,emailView,0);
					return ;
				}
				if (!StringUtils.isEmail(str2)) {
					showInfo("email格式错误!",emailInfo,emailView,0);
					return ;
				}
				showInfo("*Email:",emailInfo,emailView,1);
			}
		});
		phoneView.addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(Editable s) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String str5 = phoneView.getText().toString().trim();
				boolean bool3 = Pattern.compile("^[1][3,4,5,6,8]\\d{9}$")
						.matcher(str5).matches();
				if("".equals(str5))
				{
					showInfo("手机号不能为空!",phoneInfo,phoneView,0);
					return ;
				}
				if(!bool3)
				{
					showInfo("手机号格式错误!",phoneInfo,phoneView,0);
					return ;
				}
				showInfo("手机号码",phoneInfo,phoneView,1);
			}
		});
		phoneView.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				if(actionId == EditorInfo.IME_ACTION_GO)
				{
					register();
				}
				return true;
			}
		});
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.btn_goback:
			this.finish();
			break;
		case R.id.regist_btnSubmit:
			register();
			break;
		}
	}
	private void register()
	{
		//隐藏软键盘
		imm.hideSoftInputFromWindow(phoneView.getWindowToken(), 0);
		//检查登录
		if(checkInput())
		{
			//开线程去登陆
			//检查网络
			if(!checkNetWork())
			{
				return;
			}
			dialog = ProgressDialog.show(RegisterActivity.this, null, "注册中请稍候",
					true, true);
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			new Thread(){
				public void run() {
					Json result = null;
					Message msg = handler.obtainMessage();
					try
					{
						result = ApiClient.register(appContext, username, pwd, phone,name,email);
						msg.what = 1;
						msg.obj = result;
						handler.sendMessage(msg);
					}catch(Exception e)
					{
						e.printStackTrace();
						msg.what = -1;
						msg.obj = e;
						handler.sendMessage(msg);
					}
				};
			}.start();
		}
	}
	private boolean checkInput() {
		username = this.usernameView.getText().toString().trim();
		name = this.nameView.getText().toString().trim();
		pwd = this.pwdView.getText().toString().trim();
		String str4 = this.pwd2View.getText().toString().trim();
		phone = this.phoneView.getText().toString().trim();
		email = this.emailView.getText().toString().trim();
		boolean bool6 = Pattern.compile("^[_,0-9,a-z,A-Z]+$").matcher(username)
				.matches();
		if (username.equals("")) {
			showMsg("用户名不能为空!");
			return false;
		}
		if (!bool6) {
			showMsg("用户名只能使用字母,数字,下划线'_'组成!");
			return false;
		}
		if ((username.length() < 4) || (username.length()> 20))
	    {
			showMsg("会员名应在4-18位之间！");
			return false;
	    }
		boolean bool7 = Pattern.compile("^[0-9,a-z,A-Z]+$").matcher(pwd).matches();
		if (pwd.equals("")) {
			showMsg("密码不能为空!");
			return false;
		}
		if (!bool7) {
			showMsg("密码只能使用字母,数字组成!");
			return false;
		}
		if ((pwd.length() < 4) || (pwd.length()> 15)) {
			showMsg("密码4-15位字符!");
			return false;
		}
		if(!pwd.equals(str4))
		{
			showMsg("两次密码输入不一致!");
			return false;
		}
		boolean bool1 = Pattern
				.compile(
						"^([\\u4e00-\\u9fa5]+|([a-z]+\\s?)+)$")
				.matcher(name).matches();
		if (name.equals("")) {
			showMsg("姓名不能为空!");
			return false;
		}
		if (!bool1) {
			showMsg("姓名格式错误!");
			return false;
		}
		boolean bool3 = Pattern.compile("^[1][3,4,5,6,7,8]\\d{9}$")
				.matcher(phone).matches();
		if("".equals(phone))
		{
			showMsg("手机号不能为空!");
			return false;
		}
		if(!bool3)
		{
			showMsg("手机号格式错误!");
			return false;
		}
		if("".equals(email))
		{
			showMsg("Email不能为空!");
			return false;
		}
		if(!StringUtils.isEmail(email))
		{
			showMsg("Email格式错误!");
			return false;
		}
		return true;
	}
	// 检查网络
		private boolean checkNetWork() {
			ConnectivityManager manager = (ConnectivityManager) this
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = manager.getActiveNetworkInfo();
			if (info == null || !info.isConnected()) {
				showMsg("请检查网络");
				return false;
			}
			return true;
		}
	private void showMsg(String msg) {
		Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
	}
	private void showInfo(String msg,TextView info,ImgRightEditText view,int i)
	{
		if(1==i)	//对的
		{
			info.setText(msg);
			info.setTextColor(getResources().getColor(R.color.black));
			view.setRightImg(R.drawable.can_regeist);
		}else
		{
			info.setText(msg);
			info.setTextColor(getResources().getColor(R.color.red));
			view.setRightImg(0);
		}
	}
	static class MyHandler extends Handler
	{
		WeakReference<RegisterActivity> mActivity;
		public MyHandler(RegisterActivity activity) {
			mActivity = new WeakReference<RegisterActivity>(activity);
		 }
		public MyHandler() {
			// TODO Auto-generated constructor stub
		}
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			RegisterActivity r = mActivity.get();
			if(r.dialog!=null)
			{
				r.dialog.dismiss();
			}
			switch(msg.what)
			{
			case 1:
				//
				Json result = (Json)msg.obj;
				if(result.isSuccess())	//注册成功
				{
					User u = (User) (result.getData());
					Toast.makeText(r, "注册成功,请登录", Toast.LENGTH_SHORT).show();
					r.abfile.edit().putString("n",u.getUsername()).commit();
					r.abfile.edit().putString("p","").commit();
//					Intent intent = new Intent(r,LoginActivity.class);
//					r.startActivity(intent);
					//本来就是从登陆过来的,直接finish
					r.finish();
				}else
				{
					String wrongMsg = "";
					try{
						wrongMsg = ","+LoginTips.getRegisterTip((Integer) result.getData());
					}catch(Exception e){}
					Toast.makeText(r, "注册失败"+wrongMsg, Toast.LENGTH_SHORT).show();
				}
				break;
			case -1:
				((AppException)msg.obj).makeToast(r);
				break;
			}
		}
	}
}
