package com.examw.test.ui;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.examw.test.R;
import com.examw.test.domain.Chapter;
import com.examw.test.domain.PaperRecord;

/**
 * 章节练习
 * @author fengwei.
 * @since 2014年11月26日 下午3:19:02.
 */
public class ChapterActivity extends BaseActivity implements OnClickListener{
	private static final String TAG = "ChapterActivity";
	private ExpandableListView expandView;
	private LinearLayout reloadLayout;
	private ProgressDialog proDialog;
	private ArrayList<Chapter> chapters;
	
	private Button btnLast,btnShowPop;
	private PaperRecord r;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.ui_choose_subject);
		
		initViews();
		initData();
	}
	private void initViews()
	{
		((TextView) this.findViewById(R.id.title)).setText("章节练习");
		this.expandView = (ExpandableListView) this.findViewById(R.id.course_list);
		this.reloadLayout = (LinearLayout) this.findViewById(R.id.reload);
		this.findViewById(R.id.btn_goback).setOnClickListener(this);
		this.findViewById(R.id.btn_reload).setOnClickListener(this);
		this.btnShowPop = (Button) this.findViewById(R.id.showPop);
		this.btnShowPop.getBackground().setAlpha(200);
		this.btnShowPop.setOnClickListener(this);
		expandView.setGroupIndicator(null); //去掉默认样式
	}
	private void initData()
	{
		
	}
	@Override
	public void onClick(View v) {
		
	}
}
