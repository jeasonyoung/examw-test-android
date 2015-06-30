package com.examw.test.ui;

import java.lang.ref.WeakReference;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.examw.test.R;
import com.examw.test.app.AppConstant;
import com.examw.test.app.AppContext;
import com.examw.test.daonew.PaperDao;
import com.examw.test.daonew.PaperRecordDao;
import com.examw.test.domain.PaperRecord;
import com.examw.test.exception.AppException;
import com.examw.test.model.PaperPreview;
import com.examw.test.model.StructureInfo;
import com.examw.test.utils.StringUtils;

/**
 * 试卷信息页
 * @author fengwei.
 * @since 2014年11月28日 下午5:13:13.
 */
public class PaperInfoActivity extends BaseActivity implements OnClickListener {
	private LinearLayout ruleInfo,loading;
	private TextView totalNum, ruleSize, paperScore, paperTime;
	private Button startBtn;
	private Button restarBtn;
	private String paperId;
	private Handler handler;
	private String username;
	private PaperRecord record;
	private PaperPreview paper;
	private AppContext appContext;
	private List<StructureInfo> ruleList;
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
		this.loading = (LinearLayout) this.findViewById(R.id.loadingLayout);
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
		//username = appContext.getUsername();
		loading.setVisibility(View.VISIBLE);
		handler = new MyHandler(this);
		// 开线程 findRuleList
		new Thread() {
			public void run() {
				/**
				 * 先去数据库中查询
				 */
				String content = PaperDao.findPaperContent(paperId,username);
				record = PaperRecordDao.findLastPaperRecord(paperId, username,false);
				if(StringUtils.isEmpty(content))
				{
					try{
						//content = ApiClient.loadPaperContent(appContext,paperId);
						PaperDao.updatePaperContent(paperId, content,username);
					}catch(Exception e)
					{
						e.printStackTrace();
						handler.sendEmptyMessage(-1);
					}
				}
				//paper = GsonUtil.getGson().fromJson(content, PaperPreview.class);
				handler.sendEmptyMessage(1);
			};
		}.start();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_goback:
			this.finish();
			return;
		case R.id.btn_pratice:	//继续考试,查看成绩,开始考试
			gotoDoExamActivity();
			return;
		case R.id.btn_restart:	//重新开始
			restart();
			return;
		}
	}
	
	/**
	 * 重新开始
	 */
	private void restart() {
		Intent mIntent = new Intent(this, PaperDoPaperActivity.class);
		//mIntent.putExtra("action", AppConstant.ACTION_DO_EXAM);
		mIntent.putExtra("paperId", record.getPaperId());
		this.startActivity(mIntent);
		this.finish();
	}

	private void gotoDoExamActivity() {
		Intent intent = null;
//		if(record!=null && AppConstant.STATUS_DONE.equals(record.getStatus()))	//已经交卷
//		{
//			intent = new Intent(this, AnswerCardActivity.class);
//			intent.putExtra("paperId", paper.getId());
//			//intent.putExtra("ruleListJson", GsonUtil.objectToJson(ruleList));
//			intent.putExtra("trueOfFalse", record.getTorf());
//			intent.putExtra("action", AppConstant.ACTION_SHOW_ANSWER);
//			intent.putExtra("paperScore", paper.getScore().doubleValue());
//			intent.putExtra("paperTime", paper.getTime());
//			intent.putExtra("paperType", paper.getType());
//			intent.putExtra("username", username);
//			intent.putExtra("useTime", record.getUsedTime()%60==0?record.getUsedTime()/60:record.getUsedTime()/60+1);
//			intent.putExtra("userScore", record.getScore()); // 本次得分
//		}else{
//			intent = new Intent(this, PaperDoPaperActivity.class);
//			intent.putExtra("paperId", paper.getId());
//			intent.putExtra("action", AppConstant.ACTION_DO_EXAM);
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
				theActivity.ruleList = theActivity.paper.getStructures();
				if (theActivity.ruleList.size() == 0) {
					Toast.makeText(theActivity, "暂时没有试题数据", Toast.LENGTH_SHORT)
							.show();
				} else {
					theActivity.initTextView(theActivity.ruleList);
				}
				theActivity.loading.setVisibility(View.GONE);
				break;
			case -2:
				Toast.makeText(theActivity,
						((AppException) msg.obj).getMessage(),
						Toast.LENGTH_SHORT).show();
				theActivity.loading.setVisibility(View.GONE);
				break;
			case -1:
				// 连不上,
				Toast.makeText(theActivity, "连不上服务器", Toast.LENGTH_SHORT)
						.show();
				theActivity.loading.setVisibility(View.GONE);
				break;
			}
		}
	}
	
	private void initTextView(List<StructureInfo> rules) {
		((TextView) this.findViewById(R.id.papertitle)).setText(paper.getName());
		int length = rules.size();
		this.ruleSize.setText(length + "");
		//每日一练,没有总分和时间
//		if(paper.getType().equals(AppConstant.PAPER_TYPE_DAILY))
//		{
//			this.findViewById(R.id.scoreTimeLayout).setVisibility(View.GONE);
//		}else{
//			this.paperScore.setText(paper.getScore() + "");
//			this.paperTime.setText(paper.getTime() + "");
//		}
//		if (record != null && AppConstant.STATUS_NONE.equals(record.getStatus())) {
//			this.startBtn.setText("继续考试");
//		} else if (record != null && AppConstant.STATUS_DONE.equals(record.getStatus())) {
//			this.startBtn.setText("查看成绩");
//		} else {
//			this.startBtn.setText("开始考试");
//			this.restarBtn.setVisibility(View.GONE);
//		}
		this.totalNum.setText(paper.getTotal()+"");
		for (int i = 0; i < length; i++) {
			StructureInfo r = rules.get(i);
			View v = LayoutInflater.from(this).inflate(R.layout.item_structure_info,null);
			TextView ruleTitle = (TextView) v.findViewById(R.id.ruleTitle);
			ruleTitle.setText("第" + (i + 1) + "大题 " + r.getTitle());
			TextView ruleTitleInfo = (TextView) v.findViewById(R.id.ruleTitleInfo);
			ruleTitleInfo.setText("说明:" + r.getDescription());
			r.setItems(null);
			this.ruleInfo.addView(v, i);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
