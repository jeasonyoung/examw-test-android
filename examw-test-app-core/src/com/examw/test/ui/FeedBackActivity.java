package com.examw.test.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.examw.test.R;

/**
 * 意见反馈。
 * 
 * @author jeasonyoung
 * @since 2015年7月17日
 */
public class FeedBackActivity extends Activity implements View.OnClickListener, TextWatcher {
	private static final String TAG = "FeedBackActivity";
	private EditText textContent;
	private int maxLength;
	private TextView tvCount;
	private String countPrefix;
	/*
	 * 重载创建。
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "重载创建...");
		super.onCreate(savedInstanceState);
		//加载布局
		this.setContentView(R.layout.ui_main_more_feedback);
		
		//返回按钮处理
		final Button btnBack = (Button)this.findViewById(R.id.btn_goback);
		btnBack.setOnClickListener(this);
		
		//加载标题
		final TextView tvTitle = (TextView)this.findViewById(R.id.title);
		tvTitle.setText(this.getResources().getString(R.string.main_more_feedback_title));
		
		//获取最大字数
		this.maxLength = this.getResources().getInteger(R.integer.feedback_max_length);
		//获取前缀
		this.countPrefix = this.getResources().getString(R.string.main_more_feedback_count);
		
		//输入内容
		this.textContent = (EditText)this.findViewById(R.id.more_feedback);
		//设置最大输入长度
		this.textContent.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(this.maxLength) });
		//设置输入文本监听
		this.textContent.addTextChangedListener(this);
		
		//字数统计
		this.tvCount = (TextView)this.findViewById(R.id.more_feedback_count);
		this.tvCount.setText(this.countPrefix + " 0/" + this.maxLength);
		
		//提交按钮
		final Button btnSubmit = (Button)this.findViewById(R.id.more_feedback_btnSubmit);
		btnSubmit.setOnClickListener(this);
	}
	/*
	 * 输入文本变化前处理。
	 * @see android.text.TextWatcher#beforeTextChanged(java.lang.CharSequence, int, int, int)
	 */
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {  }
	/*
	 * 输入文本变化时处理。
	 * @see android.text.TextWatcher#onTextChanged(java.lang.CharSequence, int, int, int)
	 */
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		//统计字数
		this.tvCount.setText(this.countPrefix +" " + s.length() + "/" + this.maxLength);
	}
	/*
	 * 输入文本变化后处理。
	 * @see android.text.TextWatcher#afterTextChanged(android.text.Editable)
	 */
	@Override
	public void afterTextChanged(Editable s) {  }
	/*
	 * 按钮处理。
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		Log.d(TAG, "按钮处理..." + v);
		switch (v.getId()) {
			case R.id.btn_goback:{//返回按钮
				Log.d(TAG, "返回按钮事件...");
				//关闭activity
				this.finish();
				break;
			}
			case R.id.more_feedback_btnSubmit:{//提交按钮
				Log.d(TAG, "提交按钮处理...");
				//提示
				Toast.makeText(this, "致谢:您的意见就是我们进步的动力!我们将认真研究您的每一条建议,并运用于App中.", Toast.LENGTH_SHORT).show();
				//关闭activity
				this.finish();
				break;
			}
		}
	}
}