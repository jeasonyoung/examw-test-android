package com.examw.test.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.examw.test.R;
import com.examw.test.adapter.ChapterExpendableListAdatper;
import com.examw.test.app.AppContext;
import com.examw.test.dao.SyllabusDao;
import com.examw.test.domain.Chapter;
import com.examw.test.domain.Subject;
import com.examw.test.exception.AppException;
import com.examw.test.support.ApiClient;
import com.examw.test.util.StringUtils;

/**
 * 章节练习
 * @author fengwei.
 * @since 2014年11月26日 下午3:19:02.
 */
public class ChapterActivity extends BaseActivity implements OnClickListener{
	private static final String TAG = "ChapterActivity";
	private ExpandableListView expandView;
	private LinearLayout reloadLayout,loading,nodataLayout;
	private String subjectId,subjectName;
	private ArrayList<Chapter> chapters;
	private MyHandler mHandler;
	
//	private Button btnLast,btnShowPop;
//	private PaperRecord r;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.ui_subject_chapter);
		
		initViews();
		initData();
	}
	private void initViews()
	{
		((TextView) this.findViewById(R.id.title)).setText("章节练习");
		this.expandView = (ExpandableListView) this.findViewById(R.id.course_list);
		this.loading = (LinearLayout) this.findViewById(R.id.loadingLayout);
		this.reloadLayout = (LinearLayout) this.findViewById(R.id.reload);
		this.nodataLayout = (LinearLayout) this.findViewById(R.id.nodataLayout);
		this.findViewById(R.id.btn_goback).setOnClickListener(this);
		this.findViewById(R.id.btn_reload).setOnClickListener(this);
		expandView.setGroupIndicator(null); //去掉默认样式
	}
	private void initData()
	{
		mHandler = new MyHandler(this);
		subjectId = this.getIntent().getStringExtra("subjectId");
		subjectName = this.getIntent().getStringExtra("subjectName");
		loading.setVisibility(View.VISIBLE);
		new Thread(){
			public void run() {
				//去数据库查询章节列表
				try{
					Log.d(TAG,"查询章节信息...");
					chapters = SyllabusDao.loadAllChapters(subjectId);
					if(chapters == null || chapters.isEmpty())
					{
						try
						{
							String content = ApiClient.loadSyllabusContent((AppContext) getApplication(),subjectId);
							if (!StringUtils.isEmpty(content)) {
								chapters = SyllabusDao
										.insertSyllabusAndLoadChapters(
												new Subject(){
													private static final long serialVersionUID = 1L;
													public String getName() {return subjectName;};
													public String getSubjectId() {return subjectId;};}, 
													content);
								mHandler.sendEmptyMessage(1);
							}else
							{
								mHandler.sendEmptyMessage(0);
							}
						}catch(AppException e)
						{
							e.printStackTrace();
							Message msg = mHandler.obtainMessage();
							msg.what = -1;
							msg.obj = e;
							mHandler.sendEmptyMessage(-1);
						}
					}else
					{
						mHandler.sendEmptyMessage(1);
					}
				}catch(Exception e)
				{
					e.printStackTrace();
					mHandler.sendEmptyMessage(-2);
				}
			};
		}.start();
	}

	static class MyHandler extends Handler {
		WeakReference<ChapterActivity> weak;

		public MyHandler(ChapterActivity r) {
			weak = new WeakReference<ChapterActivity>(r);
		}

		@Override
		public void handleMessage(Message msg) {
			final ChapterActivity k = weak.get();
			k.loading.setVisibility(View.GONE);
			switch (msg.what) {
			case 0:
				k.nodataLayout.setVisibility(View.VISIBLE);
				k.reloadLayout.setVisibility(View.GONE);
				break;
			case 1:
				//初始化ExpendableView
				k.expandView.setAdapter(new ChapterExpendableListAdatper(k,k.chapters));
				break;
			case -1:
				((AppException)msg.obj).makeToast(k);
				k.reloadLayout.setVisibility(View.VISIBLE);
				k.nodataLayout.setVisibility(View.GONE);
				break;
			}
		}
	}
	private class GetSyllabusThread extends Thread {
		@Override
		public void run() {
			try {
				chapters = SyllabusDao.loadAllChapters(subjectId);
				if (chapters == null || chapters.isEmpty()) {
					String content = ApiClient.loadSyllabusContent(
							(AppContext) getApplication(), subjectId);
					if (!StringUtils.isEmpty(content)) {
						chapters = SyllabusDao.insertSyllabusAndLoadChapters(
								new Subject(){
									private static final long serialVersionUID = 1L;
									public String getName() {return subjectName;};
									public String getSubjectId() {return subjectId;};}, content);
					}
				}
				mHandler.sendEmptyMessage(1);
			} catch (Exception e) {
				e.printStackTrace();
				mHandler.sendEmptyMessage(-2);
			}
		}
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_goback:
			this.finish();
			break;
		case R.id.btn_reload:
			if (chapters == null || chapters.isEmpty()) {
				new GetSyllabusThread().start();
			}
			break;
		}
	}
}
