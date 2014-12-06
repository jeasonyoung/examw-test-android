package com.examw.test.ui;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.examw.test.R;
import com.examw.test.app.AppContext;
import com.examw.test.dao.PaperDao;
import com.examw.test.dao.PaperRecordDao;
import com.examw.test.domain.Paper;
import com.examw.test.domain.PaperRecord;
import com.examw.test.exception.AppException;
import com.examw.test.model.PaperPreview;
import com.examw.test.model.StructureInfo;
import com.examw.test.support.ApiClient;
import com.examw.test.util.GsonUtil;
import com.examw.test.util.StringUtils;
import com.google.gson.Gson;

/**
 * 试卷信息页
 * @author fengwei.
 * @since 2014年11月28日 下午5:13:13.
 */
public class PaperInfoActivity extends BaseActivity implements OnClickListener {
	private static final String TAG = "PaperInfoActivity";
	private LinearLayout ruleInfo;
	private TextView totalNum, ruleSize, paperScore, paperTime;
	private Button startBtn;
	private Button restarBtn;
	private String paperId;
	private ProgressDialog dialog;
	private Handler handler;
	private String username;
	private PaperRecord record;
	private PaperPreview paper;
	private int tempTime;
	private AppContext appContext;
	private SparseBooleanArray isDone = new SparseBooleanArray();
	private int[] tOrF;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.ui_paper_info);
		initViews();
		initViewData();
	}

	private void initViews() {
		this.ruleSize = (TextView) this.findViewById(R.id.rulesize);
		this.totalNum = (TextView) this.findViewById(R.id.questionNumTotal);
		this.paperScore = (TextView) this.findViewById(R.id.paperscore);
		this.paperTime = (TextView) this.findViewById(R.id.papertime);
		this.ruleInfo = (LinearLayout) this.findViewById(R.id.ruleInfoLayout);
		this.startBtn = (Button) this.findViewById(R.id.btn_pratice);
		this.restarBtn = (Button) this.findViewById(R.id.btn_restart);
		this.findViewById(R.id.btn_goback).setOnClickListener(this);
		this.startBtn.setOnClickListener(this);
		this.restarBtn.setOnClickListener(this);
		((TextView) findViewById(R.id.title)).setText("试卷详情");
	}

	private void initViewData() {
		Intent intent = this.getIntent();
		paperId = intent.getStringExtra("paperId");
		appContext = (AppContext) getApplication();
		// //恢复登录的状态，
//		appContext.recoverLoginStatus();
		username = appContext.getUsername();
		dialog = ProgressDialog.show(PaperInfoActivity.this, null,"加载中请稍候...", true, true);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		handler = new MyHandler(this);
		// 开线程 findRuleList
		new Thread() {
			public void run() {
				/**
				 * 先去数据库中查询
				 */
				String content = PaperDao.findPaperContent(paperId);
				record = PaperRecordDao.findLastPaperRecord(paperId, username);
				if(StringUtils.isEmpty(content))
				{
					try{
						content = ApiClient.loadPaperContent(appContext,paperId);
						PaperDao.updatePaperContent(paperId, content);
					}catch(AppException e)
					{
						e.printStackTrace();
						handler.sendEmptyMessage(-1);
					}
				}
				paper = GsonUtil.getGson().fromJson(content, PaperPreview.class);
				handler.sendEmptyMessage(1);
			};
		}.start();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_goback:
			this.finish();
			return;
		case R.id.btn_pratice:	//继续考试,查看成绩,开始考试
			gotoDoExamActivity();
			return;
		case R.id.btn_restart:	//重新开始
			gotoChooseActivity();
			return;
		}
	}
	
	
	private void gotoChooseActivity() {
		Intent mIntent = new Intent(this, PaperInfoActivity.class);
		mIntent.putExtra("action", "DoExam");
		mIntent.putExtra("paperName", paper.getName());
		mIntent.putExtra("paperId", record.getPaperId());
		mIntent.putExtra("username", username);
		mIntent.putExtra("tempTime", 0);
//		mIntent.putExtra("paperTime", record.getPapertime());
//		mIntent.putExtra("paperScore", record.getPaperscore());
//		record.setTempAnswer("");
//		record.setIsDone(null);
//		record.setTempTime(record.getPapertime() * 60);
//		dao.saveOrUpdateRecord(record);
//		mIntent.putExtra("tOrF", gson.toJson(tOrF));
		this.startActivity(mIntent);
		this.finish();
	}

	private void gotoDoExamActivity() {
//		if (questionList == null || questionList.size() == 0) {
//			Toast.makeText(this, "没有题目数据暂时不能练习", Toast.LENGTH_SHORT).show();
//			return;
//		}
		if (dialog != null) {
			dialog.show();
		}
		Intent intent = null;
//		if (record != null && record.getAnswers() != null) {
//			// SparseBooleanArray isDone = new SparseBooleanArray();
//			// addAnswer(isDone,questionList,record.getAnswers());
//			intent = new Intent(this, PaperInfoActivity.class);
//			intent.putExtra("action", "showResult");
//			intent.putExtra("ruleListJson", gson.toJson(ruleList));
//			intent.putExtra("tOrF", gson.toJson(tOrF));
//			intent.putExtra("paperScore", record.getPaperscore());
//			intent.putExtra("paperTime", record.getPapertime());
//			intent.putExtra("username", username);
//			intent.putExtra("paperid", record.getPaperId());
//			intent.putExtra("useTime", record.getUseTime());
//			intent.putExtra("record", gson.toJson(record));
//			intent.putExtra("isDone", gson.toJson(isDone));
//			intent.putExtra("userScore", record.getScore()); // 本次得分
//		} else {
//			intent = new Intent(this, QuestionDoExamActivity2.class);
//			intent.putExtra("paperName", paper.getPaperName());
//			intent.putExtra("paperId", paper.getPaperId());
//			intent.putExtra("paperTime", paper.getPaperTime());
//			intent.putExtra("tempTime", tempTime);
//			intent.putExtra("paperScore", paper.getPaperSorce());
//			intent.putExtra("action", "DoExam");
//			Gson gson = new Gson();
//			intent.putExtra("ruleListJson", gson.toJson(ruleList));
//			// intent.putExtra("questionListJson", gson.toJson(questionList));
//			intent.putExtra("tOrF", gson.toJson(tOrF));
//			intent.putExtra("username", username);
//		}
		this.startActivity(intent);
		this.finish(); // 结束生命
	}

	static class MyHandler extends Handler {
		WeakReference<PaperInfoActivity> mActivity;

		MyHandler(PaperInfoActivity activity) {
			mActivity = new WeakReference<PaperInfoActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			PaperInfoActivity theActivity = mActivity.get();
			switch (msg.what) {
			case 1:
				theActivity.dialog.dismiss();
				List<StructureInfo> rules = theActivity.paper.getStructures();
				if (rules.size() == 0) {
					Toast.makeText(theActivity, "暂时没有试题数据", Toast.LENGTH_SHORT)
							.show();
				} else {
					theActivity.initTextView(rules);
				}
				break;
			case -2:
				theActivity.dialog.dismiss();
				Toast.makeText(theActivity,
						((AppException) msg.obj).getMessage(),
						Toast.LENGTH_SHORT).show();
				break;
			case -1:
				// 连不上,
				theActivity.dialog.dismiss();
				Toast.makeText(theActivity, "连不上服务器", Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	}
	private void initTextView(List<StructureInfo> rules) {
		((TextView) this.findViewById(R.id.papertitle)).setText(paper.getName());
		this.paperScore.setText(paper.getScore() + "");
		this.paperTime.setText(paper.getTime() + "");
		int length = rules.size();
		this.ruleSize.setText(length + "");
		int total_n = 0;
		if (record != null && PaperRecord.STATUS_NONE.equals(record.getStatus())) {
			this.startBtn.setText("继续考试");
			this.tempTime = record.getUsedTime();
		} else if (record != null && PaperRecord.STATUS_DONE.equals(record.getStatus())) {
			this.startBtn.setText("查看成绩");
		} else {
			this.startBtn.setText("开始考试");
			this.restarBtn.setVisibility(View.GONE);
		}
		this.totalNum.setText(total_n + "");
		for (int i = 0; i < length; i++) {
			StructureInfo r = rules.get(i);
			total_n += r.getTotal();
			View v = LayoutInflater.from(this).inflate(R.layout.item_structure_info,null);
			TextView ruleTitle = (TextView) v.findViewById(R.id.ruleTitle);
			ruleTitle.setText("第" + (i + 1) + "大题" + r.getTitle());
			TextView ruleTitleInfo = (TextView) v.findViewById(R.id.ruleTitleInfo);
			ruleTitleInfo.setText("说明:" + r.getDescription());
			this.ruleInfo.addView(v, i);
		}
	}

	@Override
	protected void onDestroy() {
		if (dialog != null) {
			dialog.dismiss();
		}
		super.onDestroy();
	}
}
