package com.examw.test.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.taptwo.android.widget.ViewFlow;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.examw.test.R;
import com.examw.test.adapter.PaperAdapter;
import com.examw.test.app.AppContext;
import com.examw.test.dao.PaperDao.ItemStatus;
import com.examw.test.model.PaperItemModel;
import com.examw.test.support.CountdownViewSupport;
import com.examw.test.widget.WaitingViewDialog;

/**
 * 试卷Activity。
 * 
 * @author jeasonyoung
 * @since 2015年7月20日
 */
public class PaperActivity extends Activity implements View.OnClickListener,ViewFlow.ViewLazyInitializeListener {
	private static final String TAG = "PaperActivity";
	public WaitingViewDialog waitingViewDialog;
	
	private boolean displayAnswer;
	private TextView titleView;
	private ImageButton btnFav, btnSubmit, btnPrev, btnNext;
	private ViewFlow viewFlow;
	private CountdownViewSupport countdownViewSupport;
	
	private final List<PaperItemModel> dataSource;
	private PaperAdapter adapter;
	
	private String title;
	
	/**
	 * 异步线程池。
	 */
	public static ExecutorService pools = Executors.newCachedThreadPool();
	/**
	 * 是否显示试题答案。
	 */
	public static final String PAPER_ITEM_ISDISPLAY_ANSWER = "paper_displayAnswer";
	/**
	 * 加载到指定的题序。
	 */
	public static final String PAPER_ITEM_ORDER = "paper_itemOrder";
	/**
	 * 加载指定的标题。
	 */
	public static final String PAPER_ITEM_TITLE = "paper_title";
	
	/**
	 * 构造函数。
	 */
	public PaperActivity(){
		Log.d(TAG, "初始化...");
		this.dataSource = new ArrayList<PaperItemModel>();
	}
	/**
	 * 获取是否显示答案。
	 * @return 是否显示答案。
	 */
	public boolean isDisplayAnswer() {
		return displayAnswer;
	}
	/**
	 * 获取当前试题。
	 * @return 当前试题。
	 */
	private int getCurrentItemOrder() {
		return this.viewFlow.getSelectedItemPosition();
	}
	/*
	 * 重载创建。
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "重载创建...");
		super.onCreate(savedInstanceState);
		//加载试卷布局		
		this.setContentView(R.layout.ui_main_paper);
		//返回按钮
		final View btnBack = this.findViewById(R.id.main_paper_back);
		btnBack.setOnClickListener(this);
		//标题
		this.titleView = (TextView)this.findViewById(R.id.main_paper_title);
		//答题卡按钮
		final View btnCard = this.findViewById(R.id.main_paper_card);
		btnCard.setOnClickListener(this);
		
		//初始化等待动画
		this.waitingViewDialog = new WaitingViewDialog(this);
		
		//试题视图
		this.viewFlow = (ViewFlow)this.findViewById(R.id.main_paper_viewflow);
		//初始化数据适配器
		this.adapter =  new PaperAdapter(this, this.dataSource);
		//设置数据适配器
		this.viewFlow.setAdapter(this.adapter);
		this.viewFlow.setOnViewLazyInitializeListener(this);
		
		//上一题按钮
		this.btnPrev = (ImageButton)this.findViewById(R.id.main_paper_prev);
		btnPrev.setOnClickListener(this);
		//倒计时View
		final TextView timeView = (TextView)this.findViewById(R.id.main_paper_time);
		this.countdownViewSupport = new CountdownViewSupport(timeView);
		//收藏按钮
		this.btnFav = (ImageButton)this.findViewById(R.id.main_paper_fav);
		this.btnFav.setOnClickListener(this);
		//提交按钮
		this.btnSubmit = (ImageButton)this.findViewById(R.id.main_paper_submit);
		this.btnSubmit.setOnClickListener(this);
		//下一题按钮处理
		this.btnNext = (ImageButton)this.findViewById(R.id.main_paper_next);
		this.btnNext.setOnClickListener(this);
	}
	/*
	 * 重载切换视图。
	 * @see org.taptwo.android.widget.ViewFlow.ViewSwitchListener#onSwitched(android.view.View, int)
	 */
	@Override
	public void onViewLazyInitialize(View view, int position) {
		Log.d(TAG, "ViewFlow惰性加载试题..." + position);
		if(StringUtils.isBlank(this.title) &&  this.titleView != null && this.dataSource.size() >  position){
			PaperItemModel itemModel = this.dataSource.get(position);
			if(itemModel != null){
				this.titleView.setText(itemModel.getStructureTitle());
			}
		}
	}
	/*
	 * 重载开始。
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		Log.d(TAG, "重载开始...");
		super.onStart();
		//启动等待动画
		this.waitingViewDialog.show();
		//获取意图
		final Intent intent = this.getIntent();
		if(intent != null){
			//获取标题
			this.title = intent.getStringExtra(PAPER_ITEM_TITLE);
			if(StringUtils.isNotBlank(this.title) && this.titleView != null){
				this.titleView.setText(this.title);
			}
		}
		//初始化数据
		new InitDataAsyncTask().execute(intent);
	}
	/*
	 * 重载重新开始。
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		Log.d(TAG, "重载重新开始...");
		//倒计时重新开始计算
		if(countdownViewSupport != null){
			countdownViewSupport.resume();
		}
		super.onResume();
	}
	/*
	 * 重载暂停。
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		Log.d(TAG, "重载暂停...");
		//倒计时暂停
		if(countdownViewSupport != null){
			countdownViewSupport.pause();
		}
		super.onPause();
	}
	/*
	 * 重载停止。
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		Log.d(TAG, "重载停止...");
		//倒计时停止
		if(countdownViewSupport != null){
			countdownViewSupport.stop();
		}
		super.onStop();
	}
	/*
	 * 处理键盘输入。
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
			Log.d(TAG, "键盘返回退出处理..." + event);
			this.backExitHandler();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	/*
	 * 按钮事件处理。
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		Log.d(TAG, "按钮事件处理..." + v);
		switch(v.getId()){
			case R.id.main_paper_back:{//返回按钮处理
				Log.d(TAG, "返回按钮处理...");
				this.backExitHandler();
				break;
			}
			case R.id.main_paper_card:{//答题卡按钮处理
				Log.d(TAG, "答题卡按钮处理...");
				//开启模块模式打开答题卡Activity。
				Intent intent = new Intent(this, PaperCardActivity.class);
				intent.putExtra(PAPER_ITEM_ISDISPLAY_ANSWER, this.isDisplayAnswer());
				this.startActivityForResult(intent, 0);
				break;
			}
			case R.id.main_paper_prev:{//上一题按钮处理
				Log.d(TAG, "上一题按钮处理...");
				if(this.getCurrentItemOrder() <= 0){
					Toast.makeText(this, "已经是第一题了", Toast.LENGTH_SHORT).show();
				}else{
					this.viewFlow.setSelection(this.getCurrentItemOrder() - 1);
				}
				break;
			}
			case R.id.main_paper_next:{//下一题按钮处理
				Log.d(TAG, "下一题按钮处理...");
				if(this.getCurrentItemOrder() >= this.dataSource.size() - 1){
					Toast.makeText(this, "已经是最后一题了", Toast.LENGTH_SHORT).show();
				}else {
					this.viewFlow.setSelection(this.getCurrentItemOrder() + 1);
				}
				break;
			}
			case R.id.main_paper_fav:{//收藏按钮处理
				Log.d(TAG, "收藏按钮处理...");
				this.favoriteHandler(this.btnFav, this.getCurrentItemOrder());
				break;
			}
			case R.id.main_paper_submit:{//提交按钮处理
				Log.d(TAG, "提交按钮处理...");
				this.submitHandler();
				break;
			}
		}
	}
	
	/*
	 * 回调方式来Activity获取返回的结果。
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "Activity返回结果处理..." + resultCode);
		if(resultCode == Activity.RESULT_OK && data != null){
			int order = data.getIntExtra(PAPER_ITEM_ORDER, -1);
			if(order > -1){
				Log.d(TAG, "跳转到试题..." + (order + 1));
				this.viewFlow.setSelection(order);
			}
		}
	}
	
	//收藏处理
	private void favoriteHandler(final ImageButton favoriteImageButton, final int pos){
		final PaperDataDelegate dataDelegate = AppContext.getPaperDataDelegate();
		if(dataDelegate == null || this.dataSource.size() < pos){
			return;
		}
		//异步线程处理
		pools.execute(new Runnable() {
			@Override
			public void run() {
				try {
					Log.d(TAG, "异步开始收藏处理试题:" + (pos + 1));
					PaperItemModel itemModel = dataSource.get(pos);
					if(itemModel == null){
						Log.d(TAG, "试题[" + (pos + 1) + "]不存在!");
						return;
					}
					final boolean result = dataDelegate.updateFavorite(itemModel);
					//更新收藏夹图片
					if(favoriteImageButton != null){
						favoriteImageButton.post(new Runnable() {
							@SuppressWarnings("deprecation")
							@Override
							public void run() {
								int resId = (result ? R.drawable.paper_btn_fav_highlight : R.drawable.paper_btn_fav_normal); 
								Log.d(TAG, "更新收藏状态[" + result+ "]图片..." + resId);
								Drawable drawable = favoriteImageButton.getResources().getDrawable(resId);
								if(drawable != null){
									favoriteImageButton.setImageDrawable(drawable);
								}
							}
						});
					}
				} catch (Exception e) {
					Log.e(TAG, "收藏第[" + (pos + 1)+ "]题处理异常:" + e.getMessage(), e);
				}
			}
		});
	}
	/**
	 * 交卷处理
	 */
	private void submitHandler(){
		Log.d(TAG, "交卷处理...");
		new AlertDialog.Builder(this)
		.setIcon(android.R.drawable.ic_dialog_info)
		.setTitle(R.string.main_paper_submit_alert_title)
		.setMessage(R.string.main_paper_submit_alert_msg)
		.setNegativeButton(R.string.main_paper_submit_alert_btnCancel, new DialogInterface.OnClickListener() {
			/*
			 * 取消按钮点击事件。
			 * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface, int)
			 */
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.d(TAG, "交卷处理取消...");
				dialog.dismiss();
			}
		})
		.setPositiveButton(R.string.main_paper_submit_alert_btnSubmit, new DialogInterface.OnClickListener() {
			/*
			 * 确定按钮点击事件。
			 * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface, int)
			 */
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.d(TAG, "确认交卷处理...");
				dialog.dismiss();
				//停止倒计时
				countdownViewSupport.stop();
				//交卷处理
				commitPaperHandler(countdownViewSupport.useTimes());
			}
		}).show();
	}
	//交卷处理。
	private void commitPaperHandler(final int useTimes) {
		//获取上下文
		final Context context = PaperActivity.this;
		//获取消息内容
		String msg = context.getResources().getString(R.string.main_paper_submit_process);
		//初始化进度对话框
		final ProgressDialog progressDialog = ProgressDialog.show(context, null, msg, true, true);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		//开始异步线程处理交卷
		new AsyncTask<Void, Void, Boolean>(){
			private String recordId;
			/*
			 * 后台线程进行交卷处理
			 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
			 */
			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					Log.d(TAG, "后台线程进行交卷处理...");
					final PaperDataDelegate dataDelegate = AppContext.getPaperDataDelegate();
					if(dataDelegate != null){
						//提交试卷
						dataDelegate.submitPaper(useTimes, new PaperDataDelegate.SubmitResultHandler(){
							
							@Override
							public void hanlder(String paperRecordId) {
								recordId = paperRecordId;
							}
						});
						return StringUtils.isNotBlank(this.recordId);
					}
				} catch (Exception e) {
					Log.e(TAG, "后台线程进行交卷处理异常:" + e.getMessage(), e);
				}
				return null;
			}
			/*
			 * 前台主线线程处理
			 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
			 */
			@Override
			protected void onPostExecute(Boolean result) {
				Log.d(TAG, "交卷前台主线处理..." + this.recordId);
				//关闭等待进度
				progressDialog.dismiss();
				//
				if(!result){
					Toast.makeText(context, R.string.main_paper_submit_error, Toast.LENGTH_SHORT).show();
				}else {
					//跳转至试卷结果Activity
					Intent intent = new Intent(PaperActivity.this, PaperResultActivity.class);
					intent.putExtra(PaperResultActivity.PAPER_RECORD_ID, this.recordId);
					startActivity(intent);
					//关闭当前Activity
					finish();
				}
			};
		}.executeOnExecutor(pools, (Void[])null);
	}
	/**
	 * 返回退出处理。
	 */
	private void backExitHandler(){
		Log.d(TAG, "返回退出处理...");
		new AlertDialog.Builder(this)
		.setIcon(android.R.drawable.ic_dialog_info)
		.setTitle(R.string.main_paper_exit_alert_title)
		.setMessage(R.string.main_paper_exit_alert_msg)
		.setNegativeButton(R.string.main_paper_exit_alert_btnCancel, new DialogInterface.OnClickListener() {
			/*
			 * 取消退出。
			 * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface, int)
			 */
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.d(TAG, "取消退出处理...");
				dialog.dismiss();
			}
		})
		.setPositiveButton(R.string.main_paper_exit_alert_btnSubmit, new DialogInterface.OnClickListener() {
			/*
			 * 交卷处理。
			 * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface, int)
			 */
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.d(TAG, "交卷退出处理...");
				dialog.dismiss();
				//交卷处理
				submitHandler();
			}
		})
		.setNeutralButton(R.string.main_paper_exit_alert_btnConfirm, new DialogInterface.OnClickListener() {
			/*
			 * 下次再做处理。
			 * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface, int)
			 */
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.d(TAG, "下次再做处理...");
				dialog.dismiss();
				//关闭当前Activity
				finish();
			}
		}).show();
	}

	
	/**
	 * 异步初始化数据。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月20日
	 */
	private class InitDataAsyncTask extends AsyncTask<Intent, Void, Boolean>{
		private int totalTime = 0, order;
		/*
		 *后台线程异步处理。
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Boolean doInBackground(Intent... params) {
			try{
				Log.d(TAG, "后台线程异步初始化处理...");
				//0.获取意图
				if(params[0] == null){
					Log.d(TAG, "获取意图失败!");
					return false;
				}
				//1.数据监听者类
			    final PaperDataDelegate dataDelegate =  AppContext.getPaperDataDelegate();
				if(dataDelegate == null){
					Log.d(TAG, "未获取到试卷数据委托!");
					return false;
				}
				//2.是否显示答案
				displayAnswer = params[0].getBooleanExtra(PAPER_ITEM_ISDISPLAY_ANSWER, false);
				Log.d(TAG, "加载是否显示答案..." + displayAnswer);
				//3.指定题序
				this.order = params[0].getIntExtra(PAPER_ITEM_ORDER, 0);
				Log.d(TAG, "加载指定题序..." + this.order);
				//4.加载试题数据
				Log.d(TAG, "准备加载试题数据...");
				//4.1清空试卷数据
				dataSource.clear();
				//4.2添加数据
				List<PaperItemModel> list = dataDelegate.dataSourceOfPaperViews();
				int count = 0;
				if(list != null && (count = list.size()) > 0){
					dataSource.addAll(list);
				}
				//4.3获取考试时长
				this.totalTime = dataDelegate.timeOfPaperView();
				Log.d(TAG, "共加载试题数据..." + count);
			}catch(Exception e){
				Log.e(TAG, "初始化数据异常:" + e.getMessage(), e);
				return false;
			}
			return true;
		}
		/*
		 * 前台主线程处理。
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		 @Override
		protected void onPostExecute(Boolean result) {
			 if(result){
				 //提醒适配器更新
				 Log.d(TAG, "试卷数据适配器更新...");
				 adapter.notifyDataSetChanged();
				 //设置选中的题序	 
				 if(this.order > 0){
					 Log.d(TAG, "设置加载试题...." + this.order);
					 viewFlow.setSelection(this.order);
				 }
			 }
			 //关闭等待动画
			 waitingViewDialog.cancel();
			 //设置收藏按钮是否可见
			 if(btnFav != null){
				 btnFav.setVisibility(displayAnswer ? View.VISIBLE : View.INVISIBLE);
			 }
			 //设置提交按钮是否可见
			 if(btnSubmit != null){
				 btnSubmit.setVisibility(displayAnswer ? View.INVISIBLE : View.VISIBLE);
			 }
			 //初始化倒计时
			 countdownViewSupport.init(this.totalTime* 60, true);
			 //启动倒计时
			 countdownViewSupport.start(new CountdownViewSupport.CompleteHandler() {
				/*
				 * 倒计时结束处理。
				 * @see com.examw.test.support.CountdownViewSupport.CompleteHandler#onComplete(int)
				 */
				@Override
				public void onComplete(int useTimes) {
					Log.d(TAG, "倒计时结束处理,-自动交卷..");
					commitPaperHandler(useTimes);
				}
			});
		}
	}
	/**
	 * 试卷数据监听者。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月20日
	 */
	public static abstract class PaperDataDelegate implements Serializable{
		private static final long serialVersionUID = 1L;
		/**
		 * 计算试题记录ID。
		 * @param itemModel
		 * 试题模型。
		 * @return
		 * 试题记录ID(id + "$" + index)
		 */
		protected final String createItemId(PaperItemModel itemModel){
			if(itemModel != null){
				return itemModel.getId() + "$" + itemModel.getIndex();
			}
			return null;
		}
		/**
		 * 加载数据源(异步线程调用)。
		 * @return
		 * 试题集合。
		 */
	    public abstract	List<PaperItemModel> dataSourceOfPaperViews() throws Exception;
		/**
		 * 加载试题答案(异步线程调用)。
		 * @param itemModel
		 * 试题。
		 * @return 记录的答案
		 */
	    public abstract String loadMyAnswer(PaperItemModel itemModel) throws Exception;
		/**
		 * 加载答题卡数据(异步线程调用)
		 * @param cardSections
		 * @param cardSectionItems
		 * @throws Exception
		 */
	    public abstract void loadAnswerCardData(final List<AnswerCardSectionModel> cardSections, final SparseArray<AnswerCardItemModel[]> cardSectionItems) throws Exception;
		/**
		 *加载当前试题题序。
		 * @return
		 * 题序。
		 */
		public int currentOrderOfPaperView() throws Exception {
			return 0;
		}
		/**
		 * 获取考试时长(分钟)。
		 * @return
		 * 考试时长(分钟)。
		 */
		public int timeOfPaperView() throws Exception {
			return -1;
		}
		/**
		 * 更新做题记录到SQL(异步线程中调用)
		 * @param itemModel
		 * 试题。
		 * @param myAnswers
		 * 答案。
		 * @param useTimes
		 * 用时。
		 */
		public void updateRecordAnswer(PaperItemModel itemModel, String myAnswers, int useTimes) throws Exception {
			
		}
		/**
		 * 更新收藏记录(异步线程中被调用)。
		 * @param itemModel
		 * 试题。
		 * @return
		 * true - 已收藏, false - 未收藏。
		 */
		public boolean updateFavorite(PaperItemModel itemModel) throws Exception {
			return false;
		}
		/**
		 * 交卷处理。
		 * @param useTimes
		 * 用时。
		 * @param handler
		 * 交卷结果处理。
		 */
		public void submitPaper(int useTimes, SubmitResultHandler handler) throws Exception {
			
		}
		/**
		 * 交卷结果处理。
		 * 
		 * @author jeasonyoung
		 * @since 2015年7月20日
		 */
		public interface SubmitResultHandler{
			/**
			 * 处理。
			 * @param paperRecordId
			 * 试卷记录ID。
			 */
			void hanlder(String paperRecordId);
		}
		/**
		 * 答题卡分组数据模型。
		 * 
		 * @author jeasonyoung
		 * @since 2015年7月21日
		 */
		public final class AnswerCardSectionModel implements Serializable{
			private static final long serialVersionUID = 1L;
			private String title,desc;
			/**
			 * 构造函数。
			 * @param title
			 * 结构名称。
			 * @param desc
			 * 结构描述。
			 */
			public AnswerCardSectionModel(String title,String desc){
				this.title = title;
				this.desc = desc;
			}
			/**
			 * 获取结构名称。
			 * @return 结构名称。
			 */
			public String getTitle() {
				return title;
			}
			/**
			 * 获取结构描述。
			 * @return 结构描述。
			 */
			public String getDesc() {
				return desc;
			}
		}
		/**
		 * 答案卡试题数据模型。
		 * 
		 * @author jeasonyoung
		 * @since 2015年7月21日
		 */
		public final class AnswerCardItemModel implements Serializable{
			private static final long serialVersionUID = 1L;
			private int order;
			/**
			 * 构造函数。
			 * @param order
			 * 题序。
			 * @param status
			 * 状态。
			 */
			public AnswerCardItemModel(int order, ItemStatus status){
				this.order = order;
				this.status = status;
			}
			/**
			 * 是否显示答案。
			 */
			public boolean displayAnswer;
			/**
			 * 试题状态。
			 */
			public ItemStatus status;
			/**
			 * 获取题序。
			 * @return 题序。
			 */
			public int getOrder() {
				return order;
			}
		}
	}
}