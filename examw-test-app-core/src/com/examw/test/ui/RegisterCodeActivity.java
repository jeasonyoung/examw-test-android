package com.examw.test.ui;

import java.lang.ref.WeakReference;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.examw.test.R;
import com.examw.test.app.AppContext;
import com.examw.test.model.sync.AppRegister;
import com.examw.test.utils.StringUtils;
import com.examw.test.utils.ToastUtils;

/**
 * 注册码
 * @author fengwei.
 * @since 2015年3月19日 下午2:33:00.
 */
public class RegisterCodeActivity extends BaseActivity implements OnClickListener{
	private EditText codeText;
	//private AppConfig appConfig;
	private AppContext appContext;
	private LinearLayout loadingLayout;
	private Button submitBtn;
	private MyHandler mHandler;
	private String code;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.ui_code_register);
		//appConfig = AppConfig.getAppConfig(this);
		appContext = (AppContext) this.getApplication();
		mHandler = new MyHandler(this);
		initView();
	}
	
	private void initView()
	{
		this.codeText = (EditText) this.findViewById(R.id.codeText);
		this.submitBtn = (Button) this.findViewById(R.id.btn_coderegister);
		((Button) findViewById(R.id.btn_goback)).setOnClickListener(this);
		this.submitBtn.setOnClickListener(this);
		loadingLayout = (LinearLayout) this.findViewById(R.id.loadingLayout);
		loadingLayout.setVisibility(View.GONE);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.btn_goback:
			this.finish();
			break;
		case R.id.btn_coderegister:
			register();
			break;
		}
	}
	
	private void register()
	{
		code = codeText.getText().toString().trim();
		if(StringUtils.isEmpty(code))
		{
			ToastUtils.show(this, "请输入注册码!");
			return;
		}
		loadingLayout.setVisibility(View.VISIBLE);
		new Thread(){
			public void run() {
				//AppRegister req = new AppRegister();
				//req.setProductId(AppContext.getMetaInfo("productId"));
				//req.setClientMachine(appContext.getDeviceId());
				//req.setClientTypeCode(AppContext.getMetaInfo("terminalId"));
				//req.setUserId(appContext.getProductUserId());
				//req.setCode(code);
				try{
					//Json json = ApiClient.registerCode(appContext, req);
//					if(json.isSuccess())
//					{
//						mHandler.sendEmptyMessage(1);
//					}else
//					{
//						Message msg = mHandler.obtainMessage();
//						msg.what = 0;
//						msg.obj = json.getMsg();
//						mHandler.sendMessage(msg);
//					}
				}catch(Exception e)
				{
					mHandler.sendEmptyMessage(-1);
				}
			};
		}.start();
	}
	
	static class MyHandler extends Handler {
		WeakReference<RegisterCodeActivity> weak;

		public MyHandler(RegisterCodeActivity sync) {
			weak = new WeakReference<RegisterCodeActivity>(sync);
		}

		@Override
		public void handleMessage(Message msg) {
			RegisterCodeActivity sync = weak.get();
			sync.loadingLayout.setVisibility(View.GONE);
			switch (msg.what) {
			case 1:
				ToastUtils.show(sync, "注册码激活成功");
				//sync.appConfig.set(sync.appContext.getUsername()+"_code", sync.code);
				sync.finish();
				break;
			case 0:
				ToastUtils.show(sync, "注册码激活失败,"+msg.obj);
				break;
			case -1:
				ToastUtils.show(sync, "注册码激活异常");
				break;
			}
		}
	}
			
}
