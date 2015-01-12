package com.examw.test.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.examw.test.R;
import com.examw.test.app.AppContext;
import com.examw.test.dao.PaperDao;

/**
 * 反馈界面
 * @author fengwei.
 * @since 2015年1月12日 下午4:25:05.
 */
public class FeedBackActivity extends BaseActivity implements OnClickListener {
	private EditText editNoteEditText;
	private TextView editSizeText;
	private String qid,username,paperId,classId;
	private final static int maxLength = 1000;
	private PaperDao dao;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.ui_feedback);
		this.editNoteEditText = (EditText) this
				.findViewById(R.id.editNoteEditText);
		this.editSizeText = (TextView) this
				.findViewById(R.id.notebook_editSizeText);
		Intent mIntent = this.getIntent();
		this.qid = mIntent.getStringExtra("qid");

		this.username = ((AppContext)getApplication()).getUsername();
		this.paperId = mIntent.getStringExtra("paperid");
		this.classId = mIntent.getStringExtra("classid");
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
		String content = this.editNoteEditText.getText().toString();
		if(content.trim().length()==0)
		{
			Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
			return;
		}
		String addTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA).format(new Date());
		this.finish();
	}
	@Override
	protected void onPause() {
		super.onPause();
	};
	@Override
	protected void onResume() {
		super.onResume();
	}
}
