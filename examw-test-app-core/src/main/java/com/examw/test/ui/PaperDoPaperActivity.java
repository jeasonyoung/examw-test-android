package com.examw.test.ui;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
import com.examw.test.daonew.FavoriteDao;
import com.examw.test.daonew.PaperDao;
import com.examw.test.daonew.PaperRecordDao;
import com.examw.test.domain.FavoriteItem;
import com.examw.test.domain.ItemRecord;
import com.examw.test.domain.PaperRecord;
import com.examw.test.model.PaperPreview;
import com.examw.test.model.StructureInfo;
import com.examw.test.model.StructureItemInfo;
import com.examw.test.support.DataConverter;
import com.examw.test.util.GsonUtil;
import com.examw.test.util.LogUtil;
import com.examw.test.util.StringUtils;
import com.examw.test.widget.AnswerSettingLayout;
import com.examw.test.widget.AnswerSettingLayout.FontSizeChangeListerner;
import com.examw.test.widget.AnswerSettingLayout.ItemChangeListerner;
import com.examw.test.widget.AnswerSettingLayout.LightChangeListerner;
import com.examw.test.widget.ExamMenuItemClickListener;
import com.examw.test.widget.ExamMenuPopupWindow;
import com.examw.test.widget.QuestionMaterialLayout;
import com.examw.test.widget.viewflow.ViewFlow;
import com.examw.test.widget.viewflow.ViewFlow.ViewSwitchListener;

/**
 * 试卷考试界面 继续考试,查看答案
 * @author fengwei.
 * @since 2014年12月8日 上午9:00:02.
 */
public class PaperDoPaperActivity extends BaseActivity implements
		OnClickListener {
	// 组件
	private ImageButton favoriteBtn, answerBtn;
	private TextView examTypeTextView;
	private TextView timeCountDown;
	private LinearLayout nodataLayout, loadingLayout;
	private RelativeLayout ruleTitleLayout;
	// 数据
	private String username;
	private String paperId;
	private String recordId;
	private int action;
	private int paperTime, time;
	private Integer paperType;
	private double paperScore;
	private List<StructureInfo> ruleList;
	private ArrayList<StructureItemInfo> questionList;
	private StructureItemInfo currentQuestion;
	private Integer questionCursor;
	private StructureInfo currentRule;
	private PaperRecord record;
	private FavoriteItem favor;
	private ArrayList<ItemRecord> itemRecords;

	// 计时器
	private Handler timeHandler;
	private boolean timerFlag = true;
	private boolean hasRecordSaved = false;
	// 选择弹出框
	private PopupWindow popupWindow;
	private ListView lv_group;
	private AlertDialog exitDialog;
	// 菜单弹框
	private PopupWindow menuPop;
	// 提示界面
	private PopupWindow tipWindow;
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
		LogUtil.d( "考试界面启动onCreate");
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.ui_do_real_paper);
		preferences = this.getSharedPreferences("wdkaoshi", 0);
		if (preferences.getInt("isFullScreen", 0) == 1) {
			setFullScreen();
		}
		timerFlag = true;
		hasRecordSaved = false;
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
		LogUtil.d( "初始化组件");
		this.favoriteBtn = (ImageButton) this.findViewById(R.id.favoriteBtn); // 收藏按钮
		this.answerBtn = (ImageButton) this.findViewById(R.id.answerBtn); // 交卷按钮
		this.examTypeTextView = (TextView) this
				.findViewById(R.id.examTypeTextView);// 大题标题
		this.ruleTitleLayout = (RelativeLayout) this
				.findViewById(R.id.ruleTitleLayout); // 大题布局
		this.timeCountDown = (TextView) this
				.findViewById(R.id.timecount_down_TextView);// 倒计时
		this.timeCountDown.setVisibility(View.VISIBLE);
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
				//收藏试题
				if (favor != null && favor.isNeedDelete() != null) {
					FavoriteDao.favorOrCancel(favor);
					if(currentQuestion!=null) currentQuestion.setIsCollected(!favor.isNeedDelete());
					favor = null;
				}
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
				if (Boolean.TRUE.equals(currentQuestion.getIsCollected())) {
					favoriteBtn.setImageResource(R.drawable.exam_favorited_img);
				} else {
					favoriteBtn.setImageResource(R.drawable.exam_favorite_img);
				}
				// 非做题模式,禁止选择选项
				if (action != AppConstant.ACTION_DO_EXAM) {
					// 禁止选择
					((QuestionAdapter.ContentViewHolder) view
							.getTag(R.id.tag_first)).examOption
							.forbidden(false);
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
		LogUtil.d("初始化数据");
		// 数据初始化
		guidefile = this.getSharedPreferences("guidefile", 0);
		timeHandler = new TimerHandler(this);
		handler = new MyHandler(this);

		Intent intent = getIntent();
		paperId = intent.getStringExtra("paperId");
		recordId = intent.getStringExtra("recordId");
		username = ((AppContext) getApplication()).getUsername();
		action = intent.getIntExtra("action",AppConstant.ACTION_DO_EXAM);
		questionCursor = intent.getIntExtra("cursor", 0);
		// //恢复登录的状态，
		// ((AppContext) getApplication()).recoverLoginStatus();
		this.loadingLayout.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				try {
					String content = PaperDao.findPaperContent(paperId,username);
					if(!StringUtils.isEmpty(recordId))
					{
						record = PaperRecordDao.findById(username,recordId,true);
					}else
						record = PaperRecordDao.findLastPaperRecord(paperId,username, true);
					if (StringUtils.isEmpty(content)) {
						LogUtil.d("没有内容");
						handler.sendEmptyMessage(-1);
						return;
					}
					PaperPreview paper = GsonUtil.jsonToBean(content,PaperPreview.class);
					//是做题模式,若找到最新的记录已经做完则添加新的纪录
					if (action == AppConstant.ACTION_DO_EXAM && (record == null || AppConstant.STATUS_DONE.equals(record.getStatus()))) // 保存考试记录
					{
						record = new PaperRecord();
						record.setRecordId(UUID.randomUUID().toString());
						record.setUserName(username);
						record.setPaperId(paperId);
						record.setUsedTime(0);
						record.setStatus(AppConstant.STATUS_NONE);
						record.setScore(0.0);
						record.setPaperName(paper.getName());
						record.setPaperType(paper.getType());
						record.setCreateTime(StringUtils
								.toStandardDateStr(new Date()));
						record.setTerminalId(AppConfig.TERMINALID);
						record.setLastTime(record.getCreateTime());
						record.setItems(new ArrayList<ItemRecord>());
						//保存考试记录
						PaperRecordDao.save(record);
					}
					itemRecords = record.getItems(); // 考试题目记录
					ruleList = paper.getStructures();
					// 初始化试题数据
					questionList = DataConverter.findItems(ruleList,itemRecords,username);
					//初始化答题情况
					if(!StringUtils.isEmpty(record.getTorf()))
						tOrF = GsonUtil.jsonToBean(record.getTorf(), int[].class);
					else
						tOrF = new int[questionList.size()]; // 对错
					//如果是每日一练,不用计算时间
					if(paper.getType().equals(AppConstant.PAPER_TYPE_DAILY))
					{
						paperTime = record.getUsedTime();
					}else
					{
						paperTime = paper.getTime() * 60 - record.getUsedTime(); // 考试剩余时间
						time = paper.getTime() * 60;
					}
					paperScore = paper.getScore()==null?paper.getTotal():paper.getScore().doubleValue();
					paperType = paper.getType();
					if(action == AppConstant.ACTION_DO_EXAM)
					{
						questionCursor = getInitCursor(tOrF);	//初始化选项
					}
					Message msg = handler.obtainMessage();
					msg.what = 1;
					msg.arg1 = questionCursor;
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(-1);
					return;
				}
			};
			private int getInitCursor(int[] torf)
			{
				for(int i=0;i<torf.length;i++)
				{
					if(torf[i] == 0 )
						return i; 
				}
				return 0;
			}
		}.start();
	}

	// 计时器线程
	private class TimerThread extends Thread {
		@Override
		public void run() {
			while (timerFlag) {
				if(paperType.equals(AppConstant.PAPER_TYPE_DAILY))
				{
					timeHandler.sendEmptyMessage(2);
				}else
					timeHandler.sendEmptyMessage(1);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
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
				theActivity.timeCountDown
						.setText(getTimeText(theActivity.paperTime));
				if (theActivity.paperTime == 0) {
					// 交卷
					theActivity.timerFlag = false;
					Toast.makeText(theActivity, "Time Over", Toast.LENGTH_LONG)
							.show();
					theActivity.submitExam();
				}
				break;
			case 2:
				theActivity.paperTime++;
				theActivity.timeCountDown
						.setText(getTimeText(theActivity.paperTime));
				break;
			case 10:
				if (theActivity.proDialog != null) {
					theActivity.proDialog.dismiss();
				}
				// 启动答题总结界面
				theActivity.gotoAnswerSummaryActivity(); // 答题总结界面
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
			case 1:		//初始化完成
				LogUtil.d("数据初始化完成");
				if (q2.action == AppConstant.ACTION_DO_EXAM || q2.action == AppConstant.ACTION_SHOW_ANSWER) {
					if (q2.ruleList != null && q2.ruleList.size() > 0) {
						q2.currentRule = q2.ruleList.get(0);
						q2.examTypeTextView.setText(q2.currentRule.getTitle()); // 大题名字
					} else {
						q2.nodataLayout.setVisibility(0);
						return;
					}
				}
				//初始化题目适配器
				q2.questionAdapter = new QuestionAdapter(q2, q2,q2.questionList, q2.username, q2.paperId);
				q2.viewFlow.setAdapter(q2.questionAdapter);
				q2.questionCursor = msg.arg1;
				// 初始化已经选择了0
				if(q2.questionCursor!=0)
					q2.viewFlow.setSelection(q2.questionCursor);
				q2.loadingLayout.setVisibility(View.GONE);
				//若第一次考试,显示提示
				int firstExam = q2.guidefile.getInt("isFirstExam", 0);
				if (firstExam == 0 && q2.action == AppConstant.ACTION_DO_EXAM) {
					// q2.openPopupwin();
				}
				//如果是考试,则启动时间线程
				if(q2.action == AppConstant.ACTION_DO_EXAM)
					q2.new TimerThread().start(); // 时间线程启动
				break;
			case 33: // 切题
				q2.nextQuestion();	//自动切换到下一题
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
		case R.id.btn_more: // 小菜单
			showMenuPop(v);
			break;
		case R.id.previousBtn: // 上一题
			preQuestion();
			break;
		case R.id.nextBtn: // 下一题
			nextQuestion();
			break;
		case R.id.favoriteBtn: // 收藏
			favorQuestion();
			break;
		case R.id.btn_goback: // 返回
			if (action == AppConstant.ACTION_DO_EXAM || ruleList == null || ruleList.size() == 0) {
				showDialog();
			} else {
				this.finish();
			}
			break;
		case R.id.ruleTitleLayout: // 大题切换
			if (ruleList != null && ruleList.size() > 0) {
				showStructureWindow(v);
			}
			break;
		case R.id.answerBtn:	//答案按钮
			submitOrSeeAnswer();
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
		viewFlow.snapToPrevious();
	}

	// 下一题
	private void nextQuestion() {
		if (questionCursor == questionList.size() - 1) {
			Toast.makeText(this, "已经是最后一题了", Toast.LENGTH_SHORT).show();
			return;
		}
		questionAdapter.clearCheck();
		questionCursor++;
		viewFlow.snapToNext();
	}

	// 收藏
	private void favorQuestion() {
		currentQuestion = questionList.get(questionCursor);
		if(favor == null)
		{
			if(currentQuestion.getIsCollected() == null)
			{
				favor = new FavoriteItem();
				favor.setItemId(currentQuestion.getId());
				favor.setItemContent(GsonUtil.objectToJson(currentQuestion));
				favor.setSubjectId(currentQuestion.getSubjectId());
				favor.setItemType(currentQuestion.getType());
				favor.setUserAnswer(currentQuestion.getUserAnswer());
				favor.setUsername(((AppContext) getApplication()).getUsername());
			}else{
				favor = new FavoriteItem();
				favor.setItemId(currentQuestion.getId());
				favor.setUsername(username);
				favor.setNeedDelete(!currentQuestion.getIsCollected());
			}
		}
		if(favor.isNeedDelete() == null || favor.isNeedDelete())	//需要删除表示未收藏状态
		{
			this.favoriteBtn.setImageResource(R.drawable.exam_favorited_img);
			favor.setNeedDelete(false);
			Toast.makeText(this, "收藏成功", Toast.LENGTH_SHORT).show();
			return;
		}
		this.favoriteBtn.setImageResource(R.drawable.exam_favorite_img);
		favor.setNeedDelete(currentQuestion.getIsCollected() == null?null:true);
		Toast.makeText(this, "取消收藏", Toast.LENGTH_SHORT).show();
	}
	//交卷或者查看隐藏答案
	@Override
	public void submitOrSeeAnswer() {
		if (action == AppConstant.ACTION_DO_EXAM) {
			submitExam();
		}else 
		{
			QuestionAdapter.AnswerViewHolder holder = (AnswerViewHolder) viewFlow
					.getSelectedView().getTag(R.id.tag_second);
			if (holder.examAnswerLayout.getVisibility() == View.VISIBLE)
				holder.examAnswerLayout.setVisibility(View.GONE);
			else
				holder.examAnswerLayout.setVisibility(View.VISIBLE);
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
		// 保存答案,或者去除答案
		currentQuestion.setUserAnswer(abcd);
		// 取出题目记录或者新增一个题目记录
		ItemRecord currentRecord = getItemRecord();
		currentRecord.setAnswer(abcd);
		currentRecord.setLastTime(StringUtils.toStandardDateStr(new Date()));
		// 判断题目的对错
		judgeItemIsRight(currentRecord, currentQuestion, currentRule.getMin(),
				currentRule.getScore());

		// 每做完5道题自动保存答案 [暂不处理]
//		if (itemRecords.size() % 5 == 0) {
//			// TODO 保存答案
//		}
		// 单选,判断自动切题
		if (currentQuestion.isSingle()) {
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					handler.sendEmptyMessage(33);
				}
			}, 500);

		}
	}

	// 获取或更新考试记录
	private ItemRecord getItemRecord() {
		ItemRecord currentRecord = null;
		for (ItemRecord ir : itemRecords) {
			if (ir.getItemId().equals(currentQuestion.getId())) {
				currentRecord = ir;
				break;
			}
		}
		if (currentRecord == null) {
			currentRecord = new ItemRecord();
			currentRecord.setItemId(currentQuestion.getId());
			currentRecord.setSubjectId(currentQuestion.getSubjectId());
			currentRecord.setItemType(currentQuestion.getType());
			currentRecord.setUserName(username);
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
	private boolean judgeItemIsRight(ItemRecord itemRecord,
			StructureItemInfo item, BigDecimal min, BigDecimal per) {
		min = min==null?BigDecimal.ZERO:min;
		per = per==null?BigDecimal.ZERO:per;
		// 用户没有作答
		String answer = itemRecord.getAnswer();
		if (StringUtils.isEmpty(answer)) {
			itemRecord.setScore(min); // 得0分或者负分
			itemRecord.setStatus(AppConstant.ANSWER_NONE); // 没有作答
			return false;
		}
		// 题目没有答案
		if (StringUtils.isEmpty(item.getAnswer())) { // 没有正确答案(题目有问题)
			itemRecord.setScore(BigDecimal.ZERO); // 得0分或者负分
			itemRecord.setStatus(AppConstant.ANSWER_WRONG); // 算答错
			tOrF[questionCursor] = AppConstant.ANSWER_WRONG; // 对错表
			return false;
		}
		// 单选题或判断题
		if (item.isSingle()) {
			if (answer.equals(item.getAnswer())) // 答对
			{
				itemRecord.setScore(per); // 得标准分
				itemRecord.setStatus(AppConstant.ANSWER_RIGHT);// 答对
				tOrF[questionCursor] = AppConstant.ANSWER_RIGHT;
				return true;
			} else {
				itemRecord
						.setScore((min.compareTo(BigDecimal.ZERO) == 1) ? BigDecimal.ZERO
								: min); // 得0分或者负分
				itemRecord.setStatus(AppConstant.ANSWER_WRONG); // 答错
				tOrF[questionCursor] = AppConstant.ANSWER_WRONG;
				return false;
			}
		}
		// 多选或不定项
		if (item.isMulty()) {
			String[] arr = answer.split(",");
			int total = 0;
			String trueAnswer = item.getAnswer();
			for (String a : arr) {
				if (trueAnswer.indexOf(a) == -1) { // 包含有错误答案
					itemRecord
							.setScore((min.compareTo(BigDecimal.ZERO) == 1) ? BigDecimal.ZERO
									: min); // 得0分或者负分
					itemRecord.setStatus(AppConstant.ANSWER_WRONG); // 答错
					tOrF[questionCursor] = AppConstant.ANSWER_WRONG;
					return false;
				} else {
					total++;
				}
			}
			if (total == arr.length) { // 全对,得满分
				itemRecord.setScore(per); // 得标准分
				itemRecord.setStatus(AppConstant.ANSWER_RIGHT);// 答对
				tOrF[questionCursor] = AppConstant.ANSWER_RIGHT;
				return true;
			} else {
				itemRecord
						.setScore((min.compareTo(BigDecimal.ZERO) == -1) ? BigDecimal.ZERO
								: min.multiply(new BigDecimal(total))); // 得0分或者负分
				if (itemRecord.getScore().compareTo(per) == 1)
					itemRecord.setScore(per); // 得分不能超过该题总分
				itemRecord.setStatus(AppConstant.ANSWER_LESS); // 少选
				tOrF[questionCursor] = AppConstant.ANSWER_LESS;
				return false;
			}
		}
		return true;
	}

	// 保存问答题答案
	public void saveTextAnswer(String txtAnswer) {
		if (action != AppConstant.ACTION_DO_EXAM) {
			return; // 非考试不必保存答案
		}
		// TODO 保存问答题答案
		// 单个题目记录的保存or修改
		currentQuestion.setUserAnswer(txtAnswer);
		ItemRecord itemRecord = getItemRecord();
		itemRecord.setLastTime(StringUtils.toStandardDateStr(new Date()));
		itemRecord.setAnswer(txtAnswer);
		itemRecord.setScore(BigDecimal.ZERO);
		itemRecord.setStatus(AppConstant.ANSWER_WRONG); // 少选
		Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
	}
	private void  submitDailyPractice()
	{
		if(itemRecords ==null || itemRecords.size()==0)
		{
			record.setRightNum(0);
			record.setScore(0.0);
			record.setStatus(AppConstant.STATUS_NONE);
			record.setTorf(GsonUtil.objectToJson(tOrF));
			return;
		}
		int rightNum = 0;
		int sum = 0;
		for(int i:tOrF)
		{
			if(i == AppConstant.ANSWER_RIGHT)
			{
				rightNum ++ ;
			}
			if(i != AppConstant.ANSWER_NONE)
			{
				sum++;
			}
		}
		record.setRightNum(rightNum);
		record.setScore((double) sum);
		record.setTorf(GsonUtil.objectToJson(tOrF));
		if(sum == paperScore)
			record.setStatus(AppConstant.STATUS_DONE);
		else
			record.setStatus(AppConstant.STATUS_NONE);
		return;
	}
	// 交卷,评判分
	private void submitPaper() {
		if(itemRecords ==null || itemRecords.size()==0)
		{
			record.setRightNum(0);
			record.setScore(0.0);
			record.setStatus(AppConstant.STATUS_DONE);
			record.setTorf(GsonUtil.objectToJson(tOrF));
			return;
		}
		BigDecimal userScore = BigDecimal.ZERO;
		// TODO 交卷判分
		for(int k =0;k<ruleList.size();k++)
		{
			BigDecimal ruleScore = BigDecimal.ZERO;
			StructureInfo r = ruleList.get(k);
			for(ItemRecord item:itemRecords)
			{
				if(r.getId().equals(item.getStructureId()))
				{
					ruleScore = ruleScore.add(item.getScore());
				}else if(r.getMin()!=null && r.getMin().compareTo(BigDecimal.ZERO) == -1)
				{
					//答错和不答都扣分
					ruleScore =  ruleScore.add(r.getMin());
				}
			}
			if(ruleScore.compareTo(BigDecimal.ZERO)==-1) ruleScore = BigDecimal.ZERO;
			userScore = userScore.add(ruleScore);
		}
		record.setRightNum(DataConverter.getRightNum(tOrF));
		record.setScore(userScore.doubleValue());
		record.setTorf(GsonUtil.objectToJson(tOrF));
		record.setStatus(AppConstant.STATUS_DONE);
	}

	// 清除用户的答案
	private void clearUserAnswer() {
		for (StructureItemInfo info : questionList) {
			info.setUserAnswer(null);
			info.setAnswerStatus(AppConstant.ANSWER_NONE);
		}
		itemRecords.clear(); // 清除记录
	}

	// 显示大题下拉
	private void showStructureWindow(View parent) {
		if (popupWindow == null) {
			LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			View view = layoutInflater.inflate(R.layout.pop_structure_layout,
					null);

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
			// 使其聚集
			popupWindow.setFocusable(true);
			// 设置允许在外点击消失
			popupWindow.setOutsideTouchable(true);
			// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
			popupWindow.setBackgroundDrawable(new ColorDrawable(00000000));
			//设置大题项的点击事件
			lv_group.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> adapterView, View view,
						int position, long id) {
					// 切题,改变大题名称,切到该大题第一题
					// 当前大题
					StructureInfo rule = PaperDoPaperActivity.this.ruleList
							.get(position);
					int questionPosition = 0;
					for (int i = position - 1; i >= 0; i--) {
						questionPosition += PaperDoPaperActivity.this.ruleList.get(
								i).getTotal();
					}
					PaperDoPaperActivity.this.examTypeTextView.setText(rule
							.getTitle());
					PaperDoPaperActivity.this.questionAdapter.clearCheck();
					PaperDoPaperActivity.this.questionCursor = questionPosition; // cursor从0开始
					PaperDoPaperActivity.this.viewFlow.setSelection(questionCursor);
					if (popupWindow != null) {
						popupWindow.dismiss();
					}
				}
			});
		}
		// 显示的位置为:屏幕的宽度的一半-PopupWindow的高度的一半
		int xPos = parent.getWidth() / 2 - popupWindow.getWidth() / 2;
		popupWindow.showAsDropDown(parent, xPos, -5);
	}

	// 退出考试(不交卷直接退出)
	private void exitExam() {
		// 更新一次record
		timerFlag = false;
		record.setLastTime(StringUtils.toStandardDateStr(new Date()));
		record.setStatus(AppConstant.STATUS_NONE);	//还是未做完状态
		record.setUsedTime(time == 0?paperTime:(time - paperTime));	//每日一练只记录时间
		record.setItems(itemRecords);
		record.setTorf(GsonUtil.objectToJson(tOrF));
		hasRecordSaved = true;
		//TODO 需不需要另加线程
		PaperRecordDao.updatePaperRecord(record);
		this.exitDialog.dismiss();
		this.finish();
	}

	// 交卷
	private void submitExam() {
		if (this.exitDialog != null && this.exitDialog.isShowing()) {
			this.exitDialog.dismiss();
		}
		if ((record.getItems() == null || record.getItems().size() == 0)
				&& paperTime != 0) {
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
		hasRecordSaved = true;
		new Thread() {
			public void run() {
				if(paperType.equals(AppConstant.PAPER_TYPE_DAILY)){
					//交卷判断
					submitDailyPractice();
				}else
					submitPaper();// 交卷
				// 更新记录,转到 选题界面
				record.setLastTime(StringUtils.toStandardDateStr(new Date()));
				record.setUsedTime(time == 0?paperTime:(time - paperTime));
				record.setItems(itemRecords);
				// 保存考试记录
				PaperRecordDao.updatePaperRecord(record);
				timeHandler.sendEmptyMessage(10);
			};
		}.start();
	}

	// 按返回键,提示
	public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) {
		if ((paramKeyEvent.getKeyCode() == 4)
				&& (paramKeyEvent.getRepeatCount() == 0)) {
			if (action == AppConstant.ACTION_DO_EXAM || ruleList == null || ruleList.size() == 0) {
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
							submitExam();
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

	// 显示考试设置菜单项
	private void showMenuPop(View parent) {
		if (menuPop == null) {
			menuPop = new ExamMenuPopupWindow(this,
					new ExamMenuItemClickListener() {
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
					},true);
		}
		if (menuPop.isShowing()) {
			menuPop.dismiss();
			return;
		}
		menuPop.showAsDropDown(parent, 0, -10);
	}

	// 跳转到答题卡界面
	private void gotoAnswerCardActivity() {
		menuPop.dismiss();
		Intent mIntent = new Intent(this, AnswerCardActivity.class);
		// 绑数据
		if (action == AppConstant.ACTION_DO_EXAM || action == AppConstant.ACTION_DO_PRACTICE) {
			mIntent.putExtra("action", AppConstant.ACTION_CHOOSE_ITEM);
		} else {
			mIntent.putExtra("action", AppConstant.ACTION_CHOOSE_ITEM_WITH_ANSWER);
		}
		mIntent.putExtra("ruleListJson", GsonUtil.objectToJson(ruleList));
		mIntent.putExtra("trueOfFalse", GsonUtil.objectToJson(tOrF));
		this.startActivityForResult(mIntent, 1);
	}
	
	// 跳转到答题总结界面
	private void gotoAnswerSummaryActivity(){
		Intent mIntent = new Intent(this, AnswerCardActivity.class);
		// 绑数据
		mIntent.putExtra("action", AppConstant.ACTION_SUBMIT);
		mIntent.putExtra("ruleListJson", GsonUtil.objectToJson(ruleList));
		mIntent.putExtra("trueOfFalse", GsonUtil.objectToJson(tOrF));
		mIntent.putExtra("paperScore", paperScore);
		mIntent.putExtra("paperTime", time / 60);
		mIntent.putExtra("username", username);
		mIntent.putExtra("paperid", paperId);
		mIntent.putExtra("paperType", paperType);
		mIntent.putExtra("useTime", record.getUsedTime()%60==0?record.getUsedTime()/60:record.getUsedTime()/60+1);
		mIntent.putExtra("userScore", record.getScore()); // 本次得分
		this.startActivityForResult(mIntent, 1);
	}
	// 显示考试设置菜单项
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
			answerSettingPop
					.setBackgroundDrawable(new ColorDrawable(0000000000));
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
		answerSettingPop.showAtLocation(findViewById(R.id.parent),
				Gravity.CENTER, 0, 0);
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
				clearUserAnswer();
				questionCursor = 0;
				action = AppConstant.ACTION_DO_EXAM;
				answerBtn.setImageResource(R.drawable.exam_submit_img);
				// questionAdapter.notifyDataSetChanged();
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

	// 震动
	private void vibrate() {
		if (preferences.getInt("isVibrate", 0) == 1)
			vibrator.vibrate(new long[] { 800, 50, 400, 1000 }, -1);
	}

	public int getAction() {
		return action;
	}

	@Override
	protected void onStart() {
		LogUtil.d( "onStart");
		if (action == AppConstant.ACTION_DO_EXAM) {
			this.answerBtn.setImageResource(R.drawable.exam_submit_img);
		} else {
			this.answerBtn.setImageResource(R.drawable.exam_answer_img);
		}
		super.onStart();
	}

	@Override
	protected void onResume() {
		LogUtil.d( "onResume, timerFlag="+timerFlag);
		if (action == AppConstant.ACTION_DO_EXAM && !timerFlag) {
			LogUtil.d( "onResume,启动计时线程");
			timerFlag = true;
			new TimerThread().start();
		}
		hasRecordSaved = false;
		super.onResume();
	}

	@Override
	protected void onPause() {
		LogUtil.d( "onPause");
		// 保存记录,不重复保存
		if (record != null && action == AppConstant.ACTION_DO_EXAM && !hasRecordSaved) {
			record.setLastTime(StringUtils.toStandardDateStr(new Date()));
			record.setItems(itemRecords);
			record.setUsedTime(time == 0?paperTime:(time - paperTime));
			record.setTorf(GsonUtil.objectToJson(tOrF));
			PaperRecordDao.updatePaperRecord(record);
			// 保存考试记录
		}
		//收藏试题
		if (favor != null && favor.isNeedDelete() != null) {
			FavoriteDao.favorOrCancel(favor);
			if(currentQuestion!=null) currentQuestion.setIsCollected(!favor.isNeedDelete());
			favor = null;
		}
		timerFlag = false;
		super.onPause();
	}

	@Override
	protected void onStop() {
		LogUtil.d( "onStop");
		if (vibrator != null) {
			vibrator.cancel();
		}
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		LogUtil.d( "onDestroy");
		if (exitDialog != null) {
			exitDialog.dismiss();
		}
		// TODO 是否收藏
		super.onDestroy();
	}

	// 接收来自答题卡页面的消息
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (20 == resultCode) {
			// 更换试题,当前试题
			String ruleTitle = data.getStringExtra("ruleTitle");
			this.examTypeTextView.setText(ruleTitle);
			questionCursor = data.getIntExtra("cursor", 0);
			action = data.getIntExtra("action",AppConstant.ACTION_DO_EXAM);
			// 更新
			viewFlow.setSelection(questionCursor);
		} else if (30 == resultCode) {
			//重做
			action = AppConstant.ACTION_DO_EXAM;
			questionCursor = 0;
			clearUserAnswer();
		}else if (40 == resultCode) {
			//继续做题
			action = AppConstant.ACTION_DO_EXAM;
			questionCursor = 0;
		}else if (0 == resultCode) {
			this.finish();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
