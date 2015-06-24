package com.examw.test.ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import android.os.Bundle;
import android.widget.TextView;

import com.examw.test.R;
import com.examw.test.support.ReturnBtnClickListener;

/**
 * 隐私协议
 * 
 * @author fengwei.
 * @since 2014年11月29日 下午3:17:59.
 */
public class PrivacyAgreementActivity extends BaseActivity {
	private TextView content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_privacy_agreement);
		initView();
	}

	private void initView() {
		((TextView) findViewById(R.id.title)).setText("隐私协议");
		findViewById(R.id.btn_goback).setOnClickListener(
				new ReturnBtnClickListener(this));
		content = (TextView) findViewById(R.id.contentTv);
		content.setText(getFromAssets("privacy").replaceAll("COMPANY_NAME",
				getResources().getString(R.string.company_name)));
	}
	//读取资源文件
	private String getFromAssets(String fileName) {
		try {
			InputStreamReader inputReader = new InputStreamReader(
					getResources().getAssets().open(fileName));
			BufferedReader bufReader = new BufferedReader(inputReader);
			String line = "";
			String Result = "";
			while ((line = bufReader.readLine()) != null)
				Result += (line + "\n");
			bufReader.close();
			inputReader.close();
			return Result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
