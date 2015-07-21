package com.examw.test.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.Gravity;
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
import com.examw.test.adapter.PracticeQuestionAdapter;
import com.examw.test.adapter.PracticeQuestionAdapter.AnswerViewHolder;
import com.examw.test.adapter.PracticeQuestionAdapter.ContentViewHolder;
import com.examw.test.domain.FavoriteItem;
import com.examw.test.utils.StringUtils;
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
 * 做错题,收藏等
 * 
 * @author fengwei.
 * @since 2014年12月8日 上午11:23:26.
 */
public class PaperDoPracticeActivity extends BaseActivity implements
		OnClickListener {
	//private static final String TAG = "PaperDoPracticeActivity";
	// 组件
	private ImageButton favoriteBtn, answerBtn;
	private TextView examTypeTextView;
	//private TextView timeCountDown;
	private LinearLayout nodataLayout, loadingLayout;
	private RelativeLayout ruleTitleLayout;
	// 数据
	private String username;
	private String subjectId;
	private int action;
//	private List<StructureInfo> ruleList;
//	private ArrayList<StructureItemInfo> questionList;
//	private StructureItemInfo currentQuestion;
	private Integer questionCursor;
	private FavoriteItem favor;

	// 选择弹出框
	private PopupWindow popupWindow;
	private ListView lv_group;
	private AlertDialog exitDialog;
	// 菜单弹框
	private PopupWindow menuPop;
	// 数据库操作
	private ViewFlow viewFlow;
	private PracticeQuestionAdapter questionAdapter;

	private int[] tOrF;
	private MyHandler handler;

	private PopupWindow answerSettingPop;
	private Vibrator vibrator;
	private SharedPreferences preferences;

	private QuestionMaterialLayout material;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//LogUtil.d( "考试界面启动onCreate");
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
		//LogUtil.d( "初始化组件");
		this.favoriteBtn = (ImageButton) this.findViewById(R.id.favoriteBtn); // 收藏按钮
		this.answerBtn = (ImageButton) this.findViewById(R.id.answerBtn); // 交卷按钮
		this.answerBtn.setImageResource(R.drawable.exam_answer_img);
		this.examTypeTextView = (TextView) this
				.findViewById(R.id.examTypeTextView);// 大题标题
		this.ruleTitleLayout = (RelativeLayout) this
				.findViewById(R.id.ruleTitleLayout); // 大题布局
		//this.timeCountDown = (TextView) this.findViewById(R.id.timecount_down_TextView);// 倒计时
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
				// 收藏试题
				if (favor != null && favor.isNeedDelete() != null) {
					//FavoriteDao.favorOrCancel(favor);
//					if (currentQuestion != null)
//						currentQuestion.setIsCollected(!favor.isNeedDelete());
					favor = null;
				}
//				currentQuestion = questionList.get(position);
//				if (!StringUtils.isEmpty(currentQuestion.getParentContent())) {
//					material.setVisibility(View.VISIBLE);
//					material.initData(currentQuestion.getParentContent());
//				} else {
//					material.setVisibility(View.GONE);
//				}
				//设置大题名称
//				examTypeTextView.setText(currentQuestion.getTypeName());
//				if (Boolean.TRUE.equals(currentQuestion.getIsCollected())) {
//					favoriteBtn.setImageResource(R.drawable.exam_favorited_img);
//				} else {
//					favoriteBtn.setImageResource(R.drawable.exam_favorite_img);
//				}
			}
		});
	}

	// 初始化数据
	private void initData() {
		//LogUtil.d( "初始化数据");
		Intent intent = getIntent();
		//username = ((AppContext) getApplication()).getUsername();
		action = intent.getIntExtra("action", 0);
		subjectId = intent.getStringExtra("subjectId");
		questionCursor = intent.getIntExtra("cursor", 0);
		handler = new MyHandler(this);
		// //恢复登录的状态，
		// ((AppContext) getApplication()).recoverLoginStatus();
		this.loadingLayout.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				try {
					//SimplePaper paper = null;
//					if (action == AppConstant.ACTION_FAVORITE) {
//						paper = FavoriteDao.loadFavoritePaper(subjectId, username);
//					}else if(action == AppConstant.ACTION_ERROR)
//					{
//						paper = PaperRecordDao.loadErrorPaper(subjectId, username);
//					}
//					if (paper == null) {
//						handler.sendEmptyMessage(-1);
//					}// else {
//						ruleList = paper.getRuleList();
//						questionList = paper.getItems();
//						tOrF = new int[questionList.size()];
				//	}
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
		}.start();
	}

	static class MyHandler extends Handler {
		WeakReference<PaperDoPracticeActivity> weak;

		public MyHandler(PaperDoPracticeActivity context) {
			weak = new WeakReference<PaperDoPracticeActivity>(context);
		}

		@Override
		public void handleMessage(Message msg) {
			PaperDoPracticeActivity q2 = weak.get();
			switch (msg.what) {
			case 1: // 初始化完成
				//LogUtil.d( "数据初始化完成");
//				if (q2.ruleList != null && q2.ruleList.size() > 0) {
//					q2.examTypeTextView.setText(q2.ruleList.get(0).getTitle()); // 大题名字
//				} else {
//					q2.nodataLayout.setVisibility(0);
//					return;
//				}
				// 初始化题目适配器
//				q2.questionAdapter = new PracticeQuestionAdapter(q2, q2,
//						q2.questionList, q2.username);
				q2.viewFlow.setAdapter(q2.questionAdapter);
				q2.questionCursor = msg.arg1;
				q2.loadingLayout.setVisibility(View.GONE);
				break;
			case 33: // 切题
				q2.nextQuestion(); // 自动切换到下一题
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
//		if (questionList == null || questionList.size() == 0) {
//			if (v.getId() == R.id.btn_goback) {
//				this.finish();
//			}
//			return;
//		}
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
			this.finish();
			break;
		case R.id.ruleTitleLayout: // 大题切换
//			if (ruleList != null && ruleList.size() > 0) {
//				showStructureWindow(v);
//			}
			break;
		case R.id.answerBtn: // 答案按钮
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
//		if (questionCursor == questionList.size() - 1) {
//			Toast.makeText(this, "已经是最后一题了", Toast.LENGTH_SHORT).show();
//			return;
//		}
		questionAdapter.clearCheck();
		questionCursor++;
		viewFlow.snapToNext();
	}

	// 收藏
	private void favorQuestion() {
//		currentQuestion = questionList.get(questionCursor);
//		if (favor == null) {
//			if (currentQuestion.getIsCollected() == null) {
//				favor = new FavoriteItem();
//				favor.setItemId(currentQuestion.getId());
//				//favor.setItemContent(GsonUtil.objectToJson(currentQuestion));
//				favor.setSubjectId(currentQuestion.getSubjectId());
//				favor.setItemType(currentQuestion.getType());
////				favor.setUserAnswer(currentQuestion.getUserAnswer());
//				//favor.setUsername(((AppContext) getApplication()).getUsername());
//			} else {
//				favor = new FavoriteItem();
//				favor.setItemId(currentQuestion.getId());
//				favor.setUsername(username);
//				favor.setNeedDelete(!currentQuestion.getIsCollected());
//			}
//		}
		if (favor.isNeedDelete() == null || favor.isNeedDelete()) // 需要删除表示未收藏状态
		{
			this.favoriteBtn.setImageResource(R.drawable.exam_favorited_img);
			favor.setNeedDelete(false);
			Toast.makeText(this, "收藏成功", Toast.LENGTH_SHORT).show();
			return;
		}
		this.favoriteBtn.setImageResource(R.drawable.exam_favorite_img);
//		favor.setNeedDelete(currentQuestion.getIsCollected() == null ? null
//				: true);
		Toast.makeText(this, "取消收藏", Toast.LENGTH_SHORT).show();
	}

	// 交卷或者查看隐藏答案
	@Override
	public void submitOrSeeAnswer() {
		PracticeQuestionAdapter.AnswerViewHolder holder = (AnswerViewHolder) viewFlow
				.getSelectedView().getTag(R.id.tag_second);
		//questionAdapter.showAnswer(holder, currentQuestion, currentQuestion.getUserAnswer());
		if (holder.examAnswerLayout.getVisibility() == View.VISIBLE)
			holder.examAnswerLayout.setVisibility(View.GONE);
		else
			holder.examAnswerLayout.setVisibility(View.VISIBLE);
	}

	// 保存选择题(单选和多选)答案
	public void saveChoiceAnswer(String abcd) // 1001-A&1002-B&
	{
//		currentQuestion = questionList.get(questionCursor);
//		currentQuestion.setUserAnswer(abcd);
//		PracticeQuestionAdapter.AnswerViewHolder answerHolder = (com.examw.test.adapter.PracticeQuestionAdapter.AnswerViewHolder) viewFlow
//				.getSelectedView().getTag(R.id.tag_second);
//		if (currentQuestion.isSingle()) {
//			// 显示答案
//			PracticeQuestionAdapter.ContentViewHolder contentHolder = (ContentViewHolder) viewFlow
//					.getSelectedView().getTag(R.id.tag_first);
//			contentHolder.examOption.forbidden(false);
//			contentHolder.examOption.setFontColor(PaperDoPracticeActivity.this
//					.getResources().getColor(R.color.green), currentQuestion
//					.getAnswer(), PaperDoPracticeActivity.this.getResources()
//					.getColor(R.color.red), currentQuestion.getUserAnswer(),
//					currentQuestion.getType());
//			if (!currentQuestion.getAnswer().equals(currentQuestion.getUserAnswer())) {
//				vibrate();
//				tOrF[questionCursor] = -1;
//			} else {
//				tOrF[questionCursor] = 1;
//			}
//			questionAdapter.showAnswer(answerHolder, currentQuestion, abcd);
//			answerHolder.examAnswerLayout.setVisibility(View.VISIBLE);
//		}
		
	}


	// 保存问答题答案
	public void saveTextAnswer(String txtAnswer) {
		if (!"DoExam".equals(action)) {
			return; // 非考试不必保存答案
		}
	}

	// 显示大题下拉
	@SuppressLint("InflateParams")
	private void showStructureWindow(View parent) {
		if (popupWindow == null) {
			LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			View view = layoutInflater.inflate(R.layout.pop_structure_layout, null);

			lv_group = (ListView) view.findViewById(R.id.lvGroup);
			// 加载数据
//			PopRuleListAdapter groupAdapter = new PopRuleListAdapter(this,
//					ruleList);
			//lv_group.setAdapter(groupAdapter);
			// 创建一个PopuWidow对象
			WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);

			@SuppressWarnings("deprecation")
			int width = (int) (wm.getDefaultDisplay().getWidth() / 2.4);
			popupWindow = new PopupWindow(view, width,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			// 使其聚集
			popupWindow.setFocusable(true);
			// 设置允许在外点击消失
			popupWindow.setOutsideTouchable(true);
			// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
			popupWindow.setBackgroundDrawable(new ColorDrawable(00000000));
			// 设置大题项的点击事件
			lv_group.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> adapterView, View view,
						int position, long id) {
					// 切题,改变大题名称,切到该大题第一题
					// 当前大题
//					StructureInfo rule = PaperDoPracticeActivity.this.ruleList
//							.get(position);
					int questionPosition = 0;
					for (int i = position - 1; i >= 0; i--) {
//						questionPosition += PaperDoPracticeActivity.this.ruleList
//								.get(i).getTotal();
					}
//					PaperDoPracticeActivity.this.examTypeTextView.setText(rule
//							.getTitle());
					PaperDoPracticeActivity.this.questionAdapter.clearCheck();
					PaperDoPracticeActivity.this.questionCursor = questionPosition; // cursor从0开始
					PaperDoPracticeActivity.this.viewFlow
							.setSelection(questionCursor);
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
						}

						@Override
						public void answerCard() {
							gotoAnswerCardActivity();
						}
					},false);
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
		if ("DoExam".equals(action) || "practice".equals(action)) {
			mIntent.putExtra("action", "chooseQuestion");
		} else {
			mIntent.putExtra("action", "otherChooseQuestion");
		}
		//mIntent.putExtra("ruleListJson", GsonUtil.objectToJson(ruleList));
		//mIntent.putExtra("trueOfFalse", GsonUtil.objectToJson(tOrF));
		this.startActivityForResult(mIntent, 1);
	}

	// 显示考试设置菜单项
	@SuppressLint("InflateParams")
	private void showAnswerSettingPop() {
		menuPop.dismiss();
		if (answerSettingPop == null) {
			View view = LayoutInflater.from(this).inflate(R.layout.answer_setting_pup, null);
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
							PracticeQuestionAdapter.ContentViewHolder contentHolder = (ContentViewHolder) viewFlow
									.getSelectedView().getTag(R.id.tag_first);
							PracticeQuestionAdapter.AnswerViewHolder answerHolder = (AnswerViewHolder) viewFlow
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

	// 震动
	private void vibrate() {
		if (preferences.getInt("isVibrate", 0) == 1)
			vibrator.vibrate(new long[] { 800, 50, 400, 1000 }, -1);
	}

	public int getAction() {
		return action;
	}


	@Override
	protected void onPause() {
		//LogUtil.d( "onPause");
		// 收藏试题
		if (favor != null && favor.isNeedDelete() != null) {
			//FavoriteDao.favorOrCancel(favor);
//			if (currentQuestion != null)
//				currentQuestion.setIsCollected(!favor.isNeedDelete());
			favor = null;
		}
		super.onPause();
	}

	@Override
	protected void onStop() {
		//LogUtil.d( "onStop");
		if (vibrator != null) {
			vibrator.cancel();
		}
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		//LogUtil.d( "onDestroy");
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
			action = data.getIntExtra("action",0);
			// 更新
			// questionAdapter.notifyDataSetChanged();
			viewFlow.setSelection(questionCursor);
		}else if (0 == resultCode) {
			this.finish();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}