package com.examw.test.ui;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.taptwo.android.widget.ViewFlow;
import org.taptwo.android.widget.ViewFlow.ViewSwitchListener;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.examw.test.R;
import com.examw.test.adapter.PopRuleListAdapter;
import com.examw.test.adapter.QuestionAdapter;
import com.examw.test.adapter.QuestionAdapter.AnswerViewHolder;
import com.examw.test.adapter.QuestionAdapter.ContentViewHolder;
import com.examw.test.app.AppConfig;
import com.examw.test.app.AppConstant;
import com.examw.test.app.AppContext;
import com.examw.test.dao.PaperDao;
import com.examw.test.dao.PaperRecordDao;
import com.examw.test.domain.FavoriteItem;
import com.examw.test.domain.ItemRecord;
import com.examw.test.domain.PaperRecord;
import com.examw.test.model.PaperPreview;
import com.examw.test.model.StructureInfo;
import com.examw.test.model.StructureItemInfo;
import com.examw.test.support.DataConverter;
import com.examw.test.util.GsonUtil;
import com.examw.test.util.StringUtils;
import com.examw.test.widget.AnswerSettingLayout;
import com.examw.test.widget.AnswerSettingLayout.FontSizeChangeListerner;
import com.examw.test.widget.AnswerSettingLayout.ItemChangeListerner;
import com.examw.test.widget.AnswerSettingLayout.LightChangeListerner;
import com.examw.test.widget.ExamMenuItemClickListener;
import com.examw.test.widget.ExamMenuPopupWindow;
import com.examw.test.widget.QuestionMaterialLayout;

/**
 * 试卷考试界面 继续考试,查看答案
 * 
 * @author fengwei.
 * @since 2014年12月8日 上午9:00:02.
 */
public class PaperDoPaperActivity extends BaseActivity implements OnClickListener {
	private static final String TAG = "PaperDoPaperActivity";
	// 组件
	private ImageButton favoriteBtn, answerBtn;
	private TextView examTypeTextView;
	private TextView timeCountDown;
	private LinearLayout nodataLayout, loadingLayout;
	private RelativeLayout ruleTitleLayout;
	private EditText answerEditText;
	// 数据
	private String username;
	private String paperId;
	private String action;
	private StringBuilder favorQids;
	private int paperTime, time;
	private double paperScore;
	private List<StructureInfo> ruleList;
	private ArrayList<StructureItemInfo> questionList;
	private StructureItemInfo currentQuestion;
	private Integer questionCursor;
	private int initCursor;
	private StructureInfo currentRule;
	private PaperRecord record;
	private FavoriteItem favor;
	private ArrayList<ItemRecord> itemRecords;

	// 计时器
	private Handler timeHandler;
	private static boolean timerFlag = true;
	// 选择弹出框
	private PopupWindow popupWindow;
	private ListView lv_group;
	private AlertDialog exitDialog;
	// 菜单弹框
	private PopupWindow menuPop;
	// 提示界面
	private PopupWindow tipWindow;
	private Handler mHandler;
	private SharedPreferences guidefile;
	// 数据库操作
	private ViewFlow viewFlow;
	private QuestionAdapter questionAdapter;
	// private String currentAnswer; // 当前题目的答案
	// private static final String TEMPNAME = "tempName";
	private int[] tOrF;
	private MyHandler handler;

	private ProgressDialog proDialog;
	private PopupWindow answerSettingPop;
	private Vibrator vibrator;
	private SharedPreferences preferences;

	private QuestionMaterialLayout material;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG,"考试界面启动");
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.ui_do_real_paper);
		preferences = this.getSharedPreferences("wdkaoshi", 0);
		if (preferences.getInt("isFullScreen", 0) == 1) {
			setFullScreen();
		}
		initView();
		initData();
		// 数据初始化
	}

	private static boolean isfull = false;

	// 全屏设置和退出全屏
	private void setFullScreen() {
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		isfull = true;
	}

	private void quitFullScreen() {
		final WindowManager.LayoutParams attrs = getWindow().getAttributes();
		attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setAttributes(attrs);
		getWindow()
				.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		// requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		isfull = false;
	}

	public void changescreen() {
		if (isfull == true) {
			quitFullScreen();
		} else {
			setFullScreen();
		}
	}

	// 取得主界面的组件,只取得不操作
	private void initView() {
		Log.d(TAG,"初始化组件");
		this.favoriteBtn = (ImageButton) this.findViewById(R.id.favoriteBtn); // 收藏按钮
		this.answerBtn = (ImageButton) this.findViewById(R.id.answerBtn); // 交卷按钮
		this.examTypeTextView = (TextView) this
				.findViewById(R.id.examTypeTextView);// 大题标题
		this.ruleTitleLayout = (RelativeLayout) this.findViewById(R.id.ruleTitleLayout); // 大题布局
		this.timeCountDown = (TextView) this
				.findViewById(R.id.timecount_down_TextView);// 倒计时
		this.viewFlow = (ViewFlow) this.findViewById(R.id.viewflow); // 滑动切题组件
		this.nodataLayout = (LinearLayout) this.findViewById(R.id.nodataLayout); // 无数据显示
		this.nodataLayout.setVisibility(View.GONE);
		this.loadingLayout = (LinearLayout) this
				.findViewById(R.id.loadingLayout); // 加载中
		this.findViewById(R.id.previousBtn).setOnClickListener(this);// 上一题
		this.findViewById(R.id.nextBtn).setOnClickListener(this);// 下一题
		this.findViewById(R.id.btn_goback).setOnClickListener(this);// 返回按钮
		this.findViewById(R.id.btn_more).setOnClickListener(this);// 小菜单
		this.answerBtn.setOnClickListener(this);// 交卷或者查看答案
		this.favoriteBtn.setOnClickListener(this);// 收藏
		this.material = (QuestionMaterialLayout) this
				.findViewById(R.id.questionMaterial); // 案例材料的位置
		this.ruleTitleLayout.setOnClickListener(this);
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE); // 实例化震动
		// 切题
		viewFlow.setOnViewSwitchListener(new ViewSwitchListener() {
			@Override
			public void onSwitched(View view, int position) {
				questionCursor = position;
				currentQuestion = questionList.get(position);
				if (!StringUtils.isEmpty(currentQuestion.getParentContent())) {
					material.setVisibility(View.VISIBLE);
					material.initData(currentQuestion.getParentContent());
				} else {
					material.setVisibility(View.GONE);
				}
				if (ruleList != null && ruleList.size() > 0) {
					StructureInfo currentRule = getCurrentRule(currentQuestion);
					examTypeTextView.setText(currentRule.getTitle());
				}
				if (currentQuestion.getIsCollected()) {
					favoriteBtn.setImageResource(R.drawable.exam_favorited_img);
				} else {
					favoriteBtn.setImageResource(R.drawable.exam_favorite_img);
				}
//				if ("practice".equals(action)) {
//					// 有答案了,禁止选择,没答案继续选择
//					if (currentQuestion.getUserAnswer() != null) {
//						((QuestionAdapter.ContentViewHolder) view
//								.getTag(R.id.tag_first)).examOption
//								.forbidden(false);
//						// questionAdapter.setRadioEnable(((QuestionAdapter.ContentViewHolder)view.getTag(R.id.tag_first)).examOption,
//						// false);
//					} else {
//						((QuestionAdapter.ContentViewHolder) view
//								.getTag(R.id.tag_first)).examOption
//								.forbidden(true);
//						// questionAdapter.setRadioEnable(((QuestionAdapter.ContentViewHolder)view.getTag(R.id.tag_first)).examOption,
//						// true);
//					}
//				}
				if (favor!= null && favor.getItemId() != null) {
					// 收藏一个
					System.out.println("收藏或者取消了收藏");
					new Thread() {
						public void run() {
							// if (favor.isNeedDelete()) {
							// dao.deleteFavor(favor);
							// } else
							// dao.insertFavor(favor);
							// favor.setQid(null);
						};
					}.start();
				}
			}

			private StructureInfo getCurrentRule(
					StructureItemInfo currentQuestion) {
				for (StructureInfo r : ruleList) {
					if (r.getId().equals(currentQuestion.getStructureId())) {
						return r;
					}
				}
				return null;
			}
		});
	}

	// 初始化数据
	private void initData() {
		// 数据初始化
		guidefile = this.getSharedPreferences("guidefile", 0);
		action = getIntent().getStringExtra("action");
		timeHandler = new TimerHandler(this);
		mHandler = new Handler();
		handler = new MyHandler(this);

		Intent intent = getIntent();
		paperId = intent.getStringExtra("paperId");
		username = ((AppContext) getApplication()).getUsername();
		action = intent.getStringExtra("action");
		questionCursor = intent.getIntExtra("cursor", 0);
		// //恢复登录的状态，
		// ((AppContext) getApplication()).recoverLoginStatus();
		this.loadingLayout.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				try {
					String content = PaperDao.findPaperContent(paperId);
					record = PaperRecordDao.findLastPaperRecord(paperId, username, true);
					if (StringUtils.isEmpty(content)) {
						handler.sendEmptyMessage(-1);
						return;
					}
					PaperPreview paper = GsonUtil.jsonToBean(content,
							PaperPreview.class);
					if (record == null) // 保存考试记录
					{
						record = new PaperRecord();
						record.setRecordId(UUID.randomUUID().toString());
						record.setUserName(username);
						record.setPaperId(paperId);
						record.setUsedTime(0);
						record.setStatus(AppConstant.STATUS_NONE);
						record.setScore(0.0);
						record.setPaperName(paper.getName());
						record.setCreateTime(StringUtils
								.toStandardDateStr(new Date()));
						record.setTerminalId(AppConfig.TERMINALID);
						record.setLastTime(record.getCreateTime());
						record.setItems(new ArrayList<ItemRecord>());
					}
					itemRecords = record.getItems();	//考试题目记录
					ruleList = paper.getStructures();
					// 初始化试题数据
					questionList = DataConverter.findItems(ruleList,itemRecords);
					tOrF = new int[questionList.size()];	//对错
					paperTime = paper.getTime() * 60 - record.getUsedTime(); //考试剩余时间
					paperScore = paper.getScore().doubleValue();
					time = paper.getTime() * 60;
					Message msg = handler.obtainMessage();
					msg.what = 1;
					msg.arg1 = questionCursor;
					handler.sendMessage(msg);
				} catch (Exception e) {
					handler.sendEmptyMessage(-1);
					return;
				}
			};
		}.start();
	}

	// 计时器线程
	private class TimerThread extends Thread {
		@Override
		public void run() {
			while (timerFlag) {
				timeHandler.sendEmptyMessage(1);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	// 计时器处理器
	private static class TimerHandler extends Handler {
		private WeakReference<PaperDoPaperActivity> weak;

		public TimerHandler(PaperDoPaperActivity a) {
			this.weak = new WeakReference<PaperDoPaperActivity>(a);
		}

		@Override
		public void handleMessage(Message msg) {
			PaperDoPaperActivity theActivity = weak.get();
			switch (msg.what) {
			case 1:
				if (theActivity.paperTime > 0)
					theActivity.paperTime--;
				theActivity.timeCountDown.setText(getTimeText(theActivity.paperTime));
				if (theActivity.paperTime == 0) {
					// 交卷
					timerFlag = false;
					Toast.makeText(theActivity, "Time Over", Toast.LENGTH_LONG)
							.show();
					theActivity.submitExam();
				}
				break;
			case 10:
				if (theActivity.proDialog != null) {
					theActivity.proDialog.dismiss();
				}
				//TODO 启动答题总结界面
//				theActivity.gotoChooseActivity2(); // 答题总结界面
				break;
			}
		}

		private String getTimeText(int count) {
			int h = count / 60 / 60;
			int m = count / 60 % 60;
			int s = count % 60;
			return (h > 0 ? h : 0) + ":" + (m > 9 ? m : "0" + m) + ":"
					+ (s > 9 ? s : "0" + s);
		}
	}

	static class MyHandler extends Handler {
		WeakReference<PaperDoPaperActivity> weak;

		public MyHandler(PaperDoPaperActivity context) {
			weak = new WeakReference<PaperDoPaperActivity>(context);
		}

		@Override
		public void handleMessage(Message msg) {
			PaperDoPaperActivity q2 = weak.get();
			switch (msg.what) {
			case 1:
				q2.questionAdapter = new QuestionAdapter(q2, q2,q2.questionList, q2.username, q2.paperId);
				q2.viewFlow.setAdapter(q2.questionAdapter);
				if ("DoExam".equals(q2.action)
						|| "showQuestionWithAnswer".equals(q2.action)) {
					if (q2.ruleList != null && q2.ruleList.size() > 0) {
						q2.currentRule = q2.ruleList.get(0);
						q2.examTypeTextView.setText(q2.currentRule.getTitle()); // 大题名字
//						q2.ruleTypeLayout.setOnClickListener(q2);
						q2.questionCursor = msg.arg1;
						q2.viewFlow.setSelection(q2.questionCursor);
					} else {
						q2.nodataLayout.setVisibility(0);
					}
				}
				q2.loadingLayout.setVisibility(View.GONE);
				int firstExam = q2.guidefile.getInt("isFirstExam", 0);
				if (firstExam == 0 && "DoExam".equals(q2.action)) {
					// q2.openPopupwin();
				}
				q2.new TimerThread().start();	//时间线程启动
				break;
			case 33:	//切题
				if (q2.questionCursor < q2.questionList.size() - 1) {
					q2.viewFlow.setSelection(++q2.questionCursor);
				}
				break;
			case -1:
				q2.loadingLayout.setVisibility(View.GONE);
				q2.nodataLayout.setVisibility(View.VISIBLE);
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (questionList == null || questionList.size() == 0) {
			if (v.getId() == R.id.btn_goback) {
				this.finish();
			}
			return;
		}
		switch (v.getId()) {
		case R.id.btn_more:		//小菜单
			showMenuPop(v);
			break;
		case R.id.previousBtn:	//上一题
			preQuestion();
			break;
		case R.id.nextBtn:		//下一题
			nextQuestion();
			break;
		case R.id.favoriteBtn:	//收藏
			favorQuestion();
			break;
		case R.id.btn_goback:	//返回
			if ("DoExam".equals(action)) {
				showDialog();
			} else {
				this.finish();
			}
			break;
		case R.id.ruleTitleLayout:	//大题切换
			if (ruleList != null && ruleList.size() > 0) {
				showStructureWindow(v);
			}
			break;
		}
	}

	// 上一题
	private void preQuestion() {
		if (questionCursor == 0) {
			Toast.makeText(this, "已经是第一题了", Toast.LENGTH_SHORT).show();
			return;
		}
		questionAdapter.clearCheck();
		questionCursor--;
		viewFlow.setSelection(questionCursor);
	}

	// 下一题
	private void nextQuestion() {
		if (questionCursor == questionList.size() - 1) {
			Toast.makeText(this, "已经是最后一题了", Toast.LENGTH_SHORT).show();
			return;
		}
		questionAdapter.clearCheck();
		questionCursor++;
		viewFlow.setSelection(questionCursor);
	}

	// 收藏
	private void favorQuestion() {
		currentQuestion = questionList.get(questionCursor);
		String qid = currentQuestion.getId();
		favor.setItemId(qid);
		favor.setUsername(((AppContext) getApplication()).getUsername());
		if ("myFavors".equals(action)) {
			// 表示已经收藏了,现在要取消收藏
			if (favorQids.indexOf(qid) == -1) {
				Toast.makeText(this, "已经取消", Toast.LENGTH_SHORT).show();
				return;
			}
			this.favoriteBtn.setImageResource(R.drawable.exam_favorite_img);
			// dao.deleteFavor(favor);
			favor.setNeedDelete(true);
			favorQids.replace(favorQids.indexOf(qid), favorQids.indexOf(qid)
					+ qid.length() + 1, "");
			Toast.makeText(this, "取消成功,下次不再显示", Toast.LENGTH_SHORT).show();
			return;
		}
		if (favorQids.indexOf(qid) != -1) {
			this.favoriteBtn.setImageResource(R.drawable.exam_favorite_img);
			favorQids.replace(favorQids.indexOf(qid), favorQids.indexOf(qid)
					+ qid.length() + 1, "");
			favor.setNeedDelete(true);
			Toast.makeText(this, "取消收藏", Toast.LENGTH_SHORT).show();
			return;
		} else {
			// 没收藏,要收藏
			this.favoriteBtn.setImageResource(R.drawable.exam_favorited_img);
			// dao.insertFavor(favor);
			favorQids.append(qid).append(",");
			Toast.makeText(this, "收藏成功", Toast.LENGTH_SHORT).show();
		}
	}

	// 保存选择题(单选和多选)答案
	public void saveChoiceAnswer(String abcd) // 1001-A&1002-B&
	{
		currentQuestion = questionList.get(questionCursor);
		// 判断题改变答案 A为对的,B为错的
		if (currentQuestion.getType().equals(AppConstant.ITEM_TYPE_JUDGE)) {
			abcd = "A".equals(abcd) ? "1" : "0";
		}
		//保存答案,或者去除答案
		currentQuestion.setUserAnswer(abcd);
		//取出题目记录或者新增一个题目记录
		ItemRecord currentRecord = getItemRecord();
		currentRecord.setAnswer(abcd);
		//判断题目的对错
		judgeItemIsRight(currentRecord, currentQuestion, currentRule.getMin(), currentRule.getScore());
		
		// 每做完5道题自动保存答案
		if (itemRecords.size() % 5 == 0) {
			//TODO 保存答案
		}
		//单选,判断自动切题
		if (currentQuestion.isSingle()) {
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					handler.sendEmptyMessage(33);
				}
			}, 500);

		}
	}
	//获取或更新考试记录
	private ItemRecord getItemRecord()
	{
		ItemRecord currentRecord = null;
		for(ItemRecord ir:itemRecords)
		{
			if(ir.getItemId().equals(currentQuestion.getId()))
			{
				currentRecord = ir;
				break;
			}
		}
		if(currentRecord == null)
		{
			currentRecord = new ItemRecord();
			currentRecord.setItemId(currentQuestion.getId());
			currentRecord.setItemContent(GsonUtil.objectToJson(currentQuestion));
			currentRecord.setCreateTime(StringUtils.toStandardDateStr(new Date()));
			currentRecord.setRecordId(record.getRecordId());
			currentRecord.setTerminalId(AppConfig.TERMINALID);
			currentRecord.setStructureId(currentQuestion.getStructureId());
			itemRecords.add(currentRecord);
		}
		return currentRecord;
	}
	/*
	* 判断题目是对是错
	*/
	private boolean judgeItemIsRight(ItemRecord itemRecord,StructureItemInfo item,BigDecimal min, BigDecimal per) {
		//用户没有作答
		String answer  = itemRecord.getAnswer();
		if (StringUtils.isEmpty(answer)) {
			itemRecord.setScore(min); // 得0分或者负分
			itemRecord.setStatus(AppConstant.ANSWER_NONE); // 没有作答
			return false;
		}
		//题目没有答案
		if(StringUtils.isEmpty(item.getAnalysis())){	//没有正确答案(题目有问题)
			itemRecord.setScore(BigDecimal.ZERO); // 得0分或者负分
			itemRecord.setStatus(AppConstant.ANSWER_WRONG); // 算答错
			tOrF[questionCursor] = AppConstant.ANSWER_WRONG;	//对错表
			return false;
		}
		//单选题或判断题
		if(item.isSingle()) {
			if (itemRecord.getAnswer().equals(answer)) // 答对
			{
				itemRecord.setScore(per); // 得标准分
				itemRecord.setStatus(AppConstant.ANSWER_RIGHT);// 答对
				tOrF[questionCursor] = AppConstant.ANSWER_RIGHT;
				return true;
			} else {
				itemRecord.setScore((min.compareTo(BigDecimal.ZERO) == 1) ? BigDecimal.ZERO : min); // 得0分或者负分
				itemRecord.setStatus(AppConstant.ANSWER_WRONG); // 答错
				tOrF[questionCursor] = AppConstant.ANSWER_WRONG;
				return false;
			}
		}
		//多选或不定项
		if(item.isMulty()) {
			String[] arr = answer.split(",");
			int total = 0;
			String trueAnswer = item.getAnswer();
			for (String a : arr) {
				if (trueAnswer.indexOf(a) == -1) { // 包含有错误答案
					itemRecord.setScore((min.compareTo(BigDecimal.ZERO) == 1) ? BigDecimal.ZERO : min); // 得0分或者负分
					itemRecord.setStatus(AppConstant.ANSWER_WRONG); // 答错
					tOrF[questionCursor] = AppConstant.ANSWER_WRONG;
					return false;
				} else {
					total++;
				}
			}
			if (total == answer.split(",").length) { // 全对,得满分
				itemRecord.setScore(per); // 得标准分
				itemRecord.setStatus(AppConstant.ANSWER_RIGHT);// 答对
				tOrF[questionCursor] = AppConstant.ANSWER_RIGHT;
				return true;
			} else { 
				itemRecord.setScore((min.compareTo(BigDecimal.ZERO) == -1) ? BigDecimal.ZERO : min.multiply(new BigDecimal(total))); // 得0分或者负分
				if(itemRecord.getScore().compareTo(per)==1) itemRecord.setScore(per); //得分不能超过该题总分
				itemRecord.setStatus(AppConstant.ANSWER_LESS); // 少选
				tOrF[questionCursor] = AppConstant.ANSWER_LESS;
				return false;
			}
		}
		return true;
	}
	// 保存问答题答案
	public void saveTextAnswer(String txtAnswer) {
		if (!"DoExam".equals(action)) {
			return; // 非考试不必保存答案
		}
		//TODO 保存问答题答案
		//单个题目记录的保存or修改
		currentQuestion.setUserAnswer(txtAnswer);
		ItemRecord itemRecord = getItemRecord();
		itemRecord.setAnswer(txtAnswer);
		Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
	}

	// 交卷,评判分
	private void submitPaper() {
		/**
			 * 
			 */
		// if (record.getTempAnswer() == null
		// || "".equals(record.getTempAnswer().trim())) {
		// Toast.makeText(this, "还没做交毛卷啊", Toast.LENGTH_SHORT).show();
		// return;
		// }
//		try {
			double score = 0; // 总分
			double score1 = 0; // 答错扣分的情况
			double score2 = 0; // 计算大题的临时变量
			StringBuffer buf = new StringBuffer();
			StringBuffer scoreBuf = new StringBuffer("eachScore&");
			StringBuffer errorBuf = new StringBuffer();
			
			//TODO 交卷判分
			
//			for (int k = 0; k < ruleList.size(); k++) // 循环大题
//			{
//				StructureInfo r = ruleList.get(k);
//				double fen = r.getScoreForEach();// 每题的分数
//				String fenRule = r.getScoreSet();// 判分规则 0|N表示每题多少分就是多少分，
//													// 1|N,表示答对一个选项得N分，全部答对得该题的满分
//													// 2|N,表示打错扣N分,最少得0分
//				for (int j = 0; j < questionList.size(); j++) // 循环题目
//				{
//					ExamQuestion q = questionList.get(j);
//					double tempScore = 0;
//					// if (q.getRuleId().equals(r.getRuleId())) //
//					// 属于该大题的题目，按该规则进行判分
//					if (r.getContainQids2().contains("," + q.getQid() + ",")) {
//						System.out.println(q.getAnswer() + ", userAnswer:"
//								+ q.getUserAnswer());
//						tOrF[j] = q.getUserAnswer() == null ? 0 : q.getAnswer()
//								.equals(q.getUserAnswer()) ? 1 : -1;
//						if (q.getUserAnswer() != null
//								&& (!q.getAnswer().equals(q.getUserAnswer()))) {
//							errorBuf.append(q.getQid()).append("_")
//									.append(q.getUserAnswer()).append("_")
//									.append(q.getAnswer()).append(":");
//							ExamErrorQuestion error = new ExamErrorQuestion(
//									q.getQid(), username, paperid,
//									q.getUserAnswer());
//							dao.insertError(error);
//						}
//						if (fenRule.startsWith("0|")) // 答错不扣分，全对才得满分
//						{
//							if (q.getAnswer().equals(q.getUserAnswer())) {
//								score = score + fen; // 得分
//								tempScore = fen;
//							}
//						} else if (fenRule.startsWith("1|"))// 答对一个选项得多少分
//						{
//							String answer = q.getAnswer();
//							String userAnswer = q.getUserAnswer() == null ? "@"
//									: q.getUserAnswer();
//							if (answer.contains(userAnswer)) { // 包含答案算分
//								if (answer.equals(userAnswer)) {
//									score = score + fen;
//									tempScore = fen;
//								} else {
//									String[] ua = userAnswer.split("[,]"); // 少选得分，是每个选项的得分还是只要是少选就得多少分
//									double fen1 = Double.parseDouble(fenRule
//											.split("[|]")[1]) * ua.length;
//									score = score + fen1;
//									tempScore = fen1;
//								}
//							}
//						} else if (fenRule.startsWith("2|"))// 答错扣分
//						{
//							if (q.getAnswer().equals(q.getUserAnswer())) // 答对
//							{
//								score1 = score1
//										+ Double.parseDouble(fenRule
//												.split("[|]")[1]);
//								tempScore = Double.parseDouble(fenRule
//										.split("[|]")[1]);
//							} else // 答错
//							{
//								score1 = score1
//										- Double.parseDouble(fenRule
//												.split("[|]")[1]);
//								tempScore = 0 - Double.parseDouble(fenRule
//										.split("[|]")[1]);
//							}
//						}
//						scoreBuf.append(r.getRuleId()).append("-")
//								.append(q.getQid()).append("-")
//								.append(tempScore).append("&"); // 每道题的得分
//					}
//				}
////				if (errorBuf.length() > 0) {
////					final String error = errorBuf.substring(0,
////							errorBuf.length() - 1); // 11.11修改
////					new Thread() {
////						public void run() {
////							((AppContext) getApplication()).uploadError(error,paperId);
////						};
////					}.start();
////				}
//				// 每大题得分
//				if (fenRule.startsWith("2|")) {
//					buf.append(r.getRuleId());
//					buf.append("=");
//					buf.append(score1 > 0 ? score1 : 0);
//					buf.append(";");
//				} else {
//					buf.append(r.getRuleId());
//					buf.append("=");
//					score2 = score - score2;
//					buf.append(score2);
//					buf.append(";");
//					score2 = score;
//				}
//			}
//			score = score1 > 0 ? (score + score1) : score;
//			// 学员答案存进去
//			record.setScore(score);
//			System.out.println("scoreBuf = " + scoreBuf.toString());
//			// 更新record记录
//			// record.setRcdScoreForEachQuestion(scoreBuf.toString());//每题的得分情况
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
	//清除用户的答案
	private void clearUserAnswer(){
		for(StructureItemInfo info : questionList)
		{
			info.setUserAnswer(null);
			info.setAnswerStatus(AppConstant.ANSWER_NONE);
		}
		itemRecords.clear(); //清除记录
	}
	//显示大题下拉
	private void showStructureWindow(View parent) {
		if (popupWindow == null) {
			LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			View view = layoutInflater.inflate(R.layout.pop_structure_layout, null);

			lv_group = (ListView) view.findViewById(R.id.lvGroup);
			// 加载数据
			PopRuleListAdapter groupAdapter = new PopRuleListAdapter(this,
					ruleList);
			lv_group.setAdapter(groupAdapter);
			// 创建一个PopuWidow对象
			WindowManager wm = (WindowManager) this
					.getSystemService(Context.WINDOW_SERVICE);

			int width = (int) (wm.getDefaultDisplay().getWidth() / 2.4);
			popupWindow = new PopupWindow(view, width,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		// 使其聚集
		popupWindow.setFocusable(true);
		// 设置允许在外点击消失
		popupWindow.setOutsideTouchable(true);
		// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
		popupWindow.setBackgroundDrawable(new ColorDrawable(00000000));
		// 显示的位置为:屏幕的宽度的一半-PopupWindow的高度的一半
		int xPos = parent.getWidth() / 2 - popupWindow.getWidth() / 2;
		popupWindow.showAsDropDown(parent, xPos, -5);

		lv_group.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				// 切题,改变大题名称,切到该大题第一题
				// 当前大题
				StructureInfo rule = PaperDoPaperActivity.this.ruleList.get(position);
				int questionPosition = 0;
				for (int i = position - 1; i >= 0; i--) {
					questionPosition += PaperDoPaperActivity.this.ruleList
							.get(i).getTotal();
				}
				PaperDoPaperActivity.this.examTypeTextView.setText(rule.getTitle());
				PaperDoPaperActivity.this.questionAdapter.clearCheck();
				PaperDoPaperActivity.this.questionCursor = questionPosition; // cursor从0开始
				PaperDoPaperActivity.this.viewFlow.setSelection(questionCursor);
				if (popupWindow != null) {
					popupWindow.dismiss();
				}
			}
		});
	}

	// 退出考试(不交卷直接退出)
	private void exitExam() {
		// 更新一次record
		timerFlag = false;
		record.setLastTime(StringUtils.toStandardDateStr(new Date()));
		record.setStatus(AppConstant.STATUS_DONE);
		record.setUsedTime((time - paperTime));
//		PaperRecordDao.saveOrUpdateRecord(record);
		this.exitDialog.dismiss();
		this.finish();
	}

	// 交卷
	private void submitExam() {
		if (this.exitDialog != null && this.exitDialog.isShowing()) {
			this.exitDialog.dismiss();
		}
		if (record.getItems() == null || record.getItems().size() == 0) {
			Toast.makeText(this, "亲,您一题都没有做吖!", Toast.LENGTH_SHORT).show();
			return;
		}
		timerFlag = false;
		if (proDialog == null) {
			proDialog = ProgressDialog.show(this, null, "正在交卷...", true, true);
			proDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		} else {
			proDialog.show();
		}
		new Thread() {
			public void run() {
				submitPaper();// 交卷
				// 更新记录,转到 选题界面
				record.setLastTime(StringUtils.toStandardDateStr(new Date()));
				record.setStatus(AppConstant.STATUS_DONE);
				record.setUsedTime((time - paperTime));
				//TODO 保存考试记录
//				PaperRecordDao.saveOrUpdateRecord(record);
				timeHandler.sendEmptyMessage(10);
			};
		}.start();
	}

	// 按返回键,提示
	public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) {
		if ((paramKeyEvent.getKeyCode() == 4)
				&& (paramKeyEvent.getRepeatCount() == 0)) {
			if ("DoExam".equals(action)) {
				showDialog();
				return true;
			}
		}
		return super.onKeyDown(paramInt, paramKeyEvent);
	}

	// 显示退出考试对话框
	private void showDialog() {
		if (exitDialog == null) {
			AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
			localBuilder.setTitle("退出").setMessage("是否退出考试?");
			localBuilder.setPositiveButton("交卷",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							submitPaper();
							dialog.dismiss();
						}
					});
			localBuilder.setNeutralButton("下次再做",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							exitExam();
							dialog.dismiss();
						}
					});
			localBuilder.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			exitDialog = localBuilder.create();
		}
		exitDialog.show();
	}
	//显示考试设置菜单项
	private void showMenuPop(View parent){
		if(menuPop == null){
			menuPop = new ExamMenuPopupWindow(this,new ExamMenuItemClickListener() {
				@Override
				public void setting() {
					showAnswerSettingPop();
				}
				
				@Override
				public void restart() {
					restartExam();
				}
				
				@Override
				public void answerCard() {
					gotoAnswerCardActivity();
				}
			});
		}
		if (menuPop.isShowing())
		{
			menuPop.dismiss();
			return;
		}
		menuPop.showAsDropDown(parent, 0, -10);
	}
	//跳转到答题卡界面
	private void gotoAnswerCardActivity(){
		menuPop.dismiss();
		Intent mIntent = new Intent(this, AnswerCardActivity.class);
		// 绑数据
		if ("DoExam".equals(action) || "practice".equals(action)) {
			mIntent.putExtra("action", "chooseQuestion");
			mIntent.putExtra("ruleListJson", GsonUtil.objectToJson(ruleList));
			mIntent.putExtra("trueOfFalse", GsonUtil.objectToJson(tOrF));
		} else {
			mIntent.putExtra("action", "otherChooseQuestion");
//			mIntent.putExtra("questionList", gson.toJson(questionList));
		}
		this.startActivityForResult(mIntent, 1);
	}
	//显示考试设置菜单项
	private void showAnswerSettingPop() {
		menuPop.dismiss();
		if (answerSettingPop == null) {
			View view = LayoutInflater.from(this).inflate(
					R.layout.answer_setting_pup, null);
			AnswerSettingLayout setLayout = (AnswerSettingLayout) view
					.findViewById(R.id.answer_set_view);
			setLayout.setOnLightChangeListerner(new LightChangeListerner() {
				@Override
				public void onLightChangeClick(int paramInt) {
					Window localWindow = getWindow();
					WindowManager.LayoutParams localLayoutParams = localWindow
							.getAttributes();
					float f = paramInt / 255.0F;
					localLayoutParams.screenBrightness = f;
					localWindow.setAttributes(localLayoutParams);
				}
			});
			setLayout
					.setOnFontSizeChangeListener(new FontSizeChangeListerner() {
						@Override
						public void changeSize(float size) {
							QuestionAdapter.ContentViewHolder contentHolder = (ContentViewHolder) viewFlow
									.getSelectedView().getTag(R.id.tag_first);
							QuestionAdapter.AnswerViewHolder answerHolder = (AnswerViewHolder) viewFlow
									.getSelectedView().getTag(R.id.tag_second);
							questionAdapter.setFontSize(contentHolder,
									answerHolder, size);
						}
					});
			setLayout.setOnItemChangeListener(new ItemChangeListerner() {
				@Override
				public void onItemClick(int paramInt) {
					changescreen();
				}
			});
			answerSettingPop = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			// 使其聚集
			answerSettingPop.setFocusable(true);
			// 设置允许在外点击消失
			answerSettingPop.setOutsideTouchable(true);

			// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
			answerSettingPop.setBackgroundDrawable(new ColorDrawable(0000000000));
			answerSettingPop.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss() {
					// TODO Auto-generated method stub
					if (preferences.getFloat("fontsize", 16.0f) != 16.0f) {
						// 重新绘制
						questionAdapter.notifyDataSetChanged();
					}
				}
			});
		}
		answerSettingPop.showAtLocation(findViewById(R.id.parent), Gravity.CENTER, 0, 0);
	}

	/*
	 * 重置答题记录,答案记录,跳回到第一题
	 */
	private void restartExam() {
		menuPop.dismiss();
		// showDialog,是否确定
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("是否重新开始?");
		builder.setCancelable(true);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO 清除答案,返回耗时
				questionCursor = 0;
				questionAdapter.notifyDataSetChanged();
				viewFlow.setSelection(questionCursor);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
	//震动
	private void vibrate(){
		if (preferences.getInt("isVibrate", 0) == 1)
			vibrator.vibrate(new long[] { 800, 50, 400, 1000 }, -1);
	}
	public String getAction(){
		return action;
	}
	
	@Override
	protected void onStart() {
		if ("DoExam".equals(action)) {
			this.answerBtn.setImageResource(R.drawable.exam_submit_img);
		} else {
			this.answerBtn.setImageResource(R.drawable.exam_answer_img);
		}
		super.onStart();
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "onResume");
		if ("DoExam".equals(action) && timerFlag == false) {
			timerFlag = true;
			new TimerThread().start();
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		// 保存记录
		if (record != null) {
			record.setLastTime(StringUtils.toStandardDateStr(new Date()));
			//保存考试记录
		}
		timerFlag = false;
		super.onPause();
	}

	private int calcErrorNum(int[] arr) {
		if (tOrF == null)
			return 0;
		int sum = 0;
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == -1) {
				sum++;
			}
		}
		return sum;
	}

	@Override
	protected void onStop() {
		if (vibrator != null) {
			vibrator.cancel();
		}
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		if (exitDialog != null) {
			exitDialog.dismiss();
		}
		//TODO 是否收藏
		super.onDestroy();
	}
	
	//接收来自答题卡页面的消息
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (20 == resultCode) {
			// 更换试题,当前试题
			String ruleTitle = data.getStringExtra("ruleTitle");
			this.examTypeTextView.setText(ruleTitle);
			questionCursor = data.getIntExtra("cursor", 0);
			action = data.getStringExtra("action");
			// 更新
			data.setAction(action);
			System.out.println("data.getAction = " + action);
			questionAdapter.notifyDataSetChanged();
			viewFlow.setSelection(questionCursor);
		} else if (30 == resultCode) {
			action = "DoExam";
			questionCursor = 0;
			clearUserAnswer();
		} else if (0 == resultCode) {
			this.finish();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
