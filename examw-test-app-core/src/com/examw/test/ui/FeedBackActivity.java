package com.examw.test.ui;

import java.lang.ref.WeakReference;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.examw.test.R;
import com.examw.test.app.AppContext;
import com.examw.test.model.FeedBackInfo;
import com.examw.test.utils.ToastUtils;

/**
 * 反馈界面
 * @author fengwei.
 * @since 2015年1月12日 下午4:25:05.
 */
public class FeedBackActivity extends BaseActivity implements OnClickListener {
	private EditText editNoteEditText;
	private TextView editSizeText;
	private AppContext appContext;
	private final static int maxLength = 1000;
	private ProgressDialog mDialog;
	private FeedBackInfo info;
	private FeedBackHandler mHandler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.ui_feedback);
		this.editNoteEditText = (EditText) this
				.findViewById(R.id.editNoteEditText);
		this.editSizeText = (TextView) this
				.findViewById(R.id.notebook_editSizeText);
		appContext = (AppContext) this.getApplication();
		mHandler = new FeedBackHandler(this);
		this.editNoteEditText.addTextChangedListener(new TextWatcher() {
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
				FeedBackActivity.this.editSizeText.setText("已输入: "
						+ s.length() + "/" + maxLength);
			}
		});
		//设置最大长度
		this.editNoteEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
		//绑定事件
		this.findViewById(R.id.btn_goback).setOnClickListener(this);
		this.findViewById(R.id.exam_notebook_btn).setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.btn_goback:
			this.finish();
			return;
		case R.id.exam_notebook_btn:
			submit();
			return;
		}
	}
	private void submit()
	{
		//隐藏键盘
		((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(editNoteEditText.getWindowToken(), 0);
		if(mDialog != null && !mDialog.isShowing())
		{
			mDialog.show();
			if(info != null)
			{
				new FeedBackThread().start();
				return;
			}
		}
		String content = this.editNoteEditText.getText().toString();
		if(content.trim().length()==0)
		{
			Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
			return;
		}
		info = getInfo(content);
		mDialog = ProgressDialog.show(this, null,
				"提交中，请稍后...", true, true);
		new FeedBackThread().start();
	}
	static class FeedBackHandler extends Handler {
		WeakReference<FeedBackActivity> weak;

		public FeedBackHandler(FeedBackActivity context) {
			weak = new WeakReference<FeedBackActivity>(context);
		}
		
		@Override
		public void handleMessage(Message msg) {
			FeedBackActivity a = weak.get();
			a.mDialog.dismiss();
			switch(msg.what)
			{
			case 1:
				ToastUtils.show(a, "提交成功,感谢反馈");
				a.info = null;
				a.editNoteEditText.setText("");
				break;
			case 2:
				ToastUtils.show(a, "提交失败,稍后再试");
				break;
			}
		}
	}
	
	class FeedBackThread extends Thread{
		@Override
		public void run() {
			try{
				//Json result = ApiClient.feedBack(appContext, info);
				//if(result.isSuccess())
				//{
					//mHandler.sendEmptyMessage(1);
				//}else
					mHandler.sendEmptyMessage(2);
			}catch(Exception e)
			{
				mHandler.sendEmptyMessage(2);
			}
		}
	}
	@Override
	protected void onPause() {
		super.onPause();
	};
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	private FeedBackInfo getInfo(String content)
	{
		FeedBackInfo info = new FeedBackInfo();
		StringBuffer buf = new StringBuffer();
		buf.append("来自android客户端").append(appContext.getVersionName()).append("\n");
		//buf.append("产品ID:").append(AppContext.getMetaInfo("productId")).append("\n");
//		buf.append("名称:").append(ProductDao.findProductName()).append("\n");
		//buf.append("用户:").append(appContext.getUsername()).append("\n");
		buf.append("content:").append(content);
		info.setContent(buf.toString());
		//info.setTerminalCode(Integer.valueOf(AppContext.getMetaInfo("terminalId")));
		//info.setUsername(appContext.getUsername());
		buf = null;
		return info;
	}
}
