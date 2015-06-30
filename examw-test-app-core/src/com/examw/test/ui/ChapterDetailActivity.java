package com.examw.test.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.examw.test.R;
import com.examw.test.app.AppConstant;
import com.examw.test.domain.Chapter;
import com.examw.test.domain.PaperModel;
import com.examw.test.model.StructureInfo;
import com.examw.test.model.StructureItemInfo;
import com.examw.test.widget.NewDataToast;

/**
 * 
 * @author fengwei.
 * @since 2014年12月19日 下午1:49:41.
 */
public class ChapterDetailActivity extends BaseActivity implements
		OnClickListener {
	//private TextView chapterTitleTv, answeredSizeTv, questionSumTv,errorSizeTv, accuracyTv;
	private ListView knowledgeLv;
	private Handler handler;
	// data
	//private String chapterId, chapterPid, chapterName;
	private PaperModel paper;
	//private PaperRecord record;
	private ArrayList<Chapter> chapters;
	private int /*errorNum, */questionCursor;
	private List<StructureItemInfo> questionList;
	private List<StructureInfo> ruleList;
	//private AppContext appContext;
	private LinearLayout header,/*empty,*/loading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.ui_chapter_practice_info);
		initViews();
//		initData1();
	}

	@SuppressLint("InflateParams")
	private void initViews() {
		((TextView) findViewById(R.id.title)).setText("章节详情");
		header = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.practice_info_header, null);
		loading = (LinearLayout) this.findViewById(R.id.loadingLayout);
//		empty = (LinearLayout) this.findViewById(R.id.empty);
//		chapterTitleTv = (TextView) header.findViewById(R.id.chapterTitle);
//		answeredSizeTv = (TextView) header.findViewById(R.id.answeredSize);
//		questionSumTv = (TextView) header.findViewById(R.id.questionNumTotal);
//		errorSizeTv = (TextView) header.findViewById(R.id.errorSize);
//		accuracyTv = (TextView) header.findViewById(R.id.accuracy);
		knowledgeLv = (ListView) findViewById(R.id.knowledge_listView);
		findViewById(R.id.btn_goback).setOnClickListener(this);
		header.findViewById(R.id.btn_practice).setOnClickListener(this);
		findViewById(R.id.btn_refresh).setOnClickListener(this);
		knowledgeLv.addHeaderView(header);
		knowledgeLv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Chapter node = chapters.get(arg2);
				Intent intent = new Intent(ChapterDetailActivity.this,KnowledgeDetailActivity.class);
				intent.putExtra("chapterId",node.getChapterId());
				intent.putExtra("chapterPid",node.getPid());
				ChapterDetailActivity.this.startActivity(intent);
				startActivity(intent);
			}
		});
	}

//	private void initData1() {
//		Intent intent = getIntent();
//		chapterName = intent.getStringExtra("chapterName");
//		chapterId = intent.getStringExtra("chapterId");
//		chapterPid = intent.getStringExtra("chapterPid");
//		chapterTitleTv.setText(chapterName);
//		handler = new MyHandler(this);
//	}

//	private void initData2() {
//		appContext = (AppContext) getApplication();
//
//		// //恢复登录的状态，
////		appContext.recoverLoginStatus();
//		loading.setVisibility(View.VISIBLE);
//		new Thread() {
//			public void run() {	
////				chapters = SyllabusDao.loadChapters(chapterId);
//			};
//		}.start();
//	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
//		initData2();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_goback:
			this.finish();
			break;
		case R.id.btn_practice:
			practice();
			break;
		case R.id.btn_refresh:
			refresh();
			break;
		}
	}

	private void refresh() {
		loading.setVisibility(View.VISIBLE);
		if (paper == null) {
			NewDataToast.makeText(this, "没有更新", false).show();
			return;// 还没有练习过，已经是最新
		}
		new Thread() {
			public void run() {
				try {
					// 先不做更新
					Thread.sleep(2000);
					handler.sendEmptyMessage(-8);
					/*
					 * //加一个时间的参数 ArrayList<ExamQuestion> list =
					 * XMLParseUtil.parseQuestionList(ApiClient
					 * .getChapterQuestionList(appContext,
					 * chapterid,paper.getAddDate(),AreaUtils.areaCode)); if
					 * (list == null || list.size() == 0) {
					 * handler.sendEmptyMessage(-8); } else { refreshRule(list);
					 * dao.insertQuestions(list);
					 * paper.setPaperSorce(paper.getPaperSorce()+list.size());
					 * dao.updatePraticePaper(paper);
					 * dao.insertRules(ruleList,zuheid);
					 * handler.sendEmptyMessage(8); }
					 */
				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(-4);
				}
			};
		}.start();
	}

	private void practice() {
		if (paper == null) {
			Toast.makeText(this, "暂无试题", Toast.LENGTH_SHORT).show();
			return;
		}
		// 获取题目数据
		if (questionList != null) {
			handler.sendEmptyMessage(4);
			return;
		}
		loading.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				//TODO 查询试题
			};
		}.start();
	}


	static class MyHandler extends Handler {
		private WeakReference<ChapterDetailActivity> weak;

		public MyHandler(ChapterDetailActivity activity) {
			// TODO Auto-generated constructor stub
			weak = new WeakReference<ChapterDetailActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			ChapterDetailActivity qpi = weak.get();
			qpi.loading.setVisibility(View.GONE);
			switch (msg.what) {
			case 1:
				//TODO 初始化头部数据
				
				//TODO 初始化知识点数据
				if (qpi.chapters != null && qpi.chapters.size() > 0) {
					qpi.knowledgeLv.setAdapter(new ArrayAdapter<Chapter>(qpi,R.layout.pop_rule_item,R.id.groupItem,qpi.chapters));
				} else {
					qpi.knowledgeLv.setAdapter(new ArrayAdapter<Chapter>(qpi,R.layout.pop_rule_item,R.id.groupItem,qpi.chapters));
				}
				break;
			case -1:
				Toast.makeText(qpi, "亲,网络不给力吖!", Toast.LENGTH_SHORT).show();
				qpi.knowledgeLv.setAdapter(new ArrayAdapter<Chapter>(qpi,R.layout.pop_rule_item,R.id.groupItem,qpi.chapters));
				break;
			case -2:
				Toast.makeText(qpi, "获取数据出问题", Toast.LENGTH_SHORT).show();
				qpi.knowledgeLv.setAdapter(new ArrayAdapter<Chapter>(qpi,R.layout.pop_rule_item,R.id.groupItem,qpi.chapters));
				break;
			case 4:
				Intent intent = new Intent(qpi, PaperDoPracticeActivity.class);
//				intent.putExtra("paperId", qpi.zuheid);
				//intent.putExtra("action", AppConstant.ACTION_DO_PRACTICE);
				intent.putExtra("cursor", qpi.questionCursor);
				qpi.startActivity(intent);
				break;
			case 0:
				Toast.makeText(qpi, "暂无题目数据", Toast.LENGTH_SHORT).show();
				break;
			case -4:
				Toast.makeText(qpi, "获取数据出问题", Toast.LENGTH_SHORT).show();
				break;
			case -8:
				NewDataToast.makeText(qpi, "没有更新", false).show();
				break;
			case 8:
				NewDataToast.makeText(qpi, "更新成功", false).show();
//				qpi.questionSumTv.setText(qpi.paper.getScore() + "");
				break;
			}
		}
	}

	@Override
	protected void onDestroy() {
		if (chapters != null)
			chapters.clear();
		if (questionList != null)
			questionList.clear();
		if (ruleList != null)
			ruleList.clear();
		super.onDestroy();
	}
}