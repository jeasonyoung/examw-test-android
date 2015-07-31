package com.examw.test.ui;

import java.util.ArrayList;
import java.util.Arrays;
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
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.examw.test.R;
import com.examw.test.adapter.PaperAdapter;
import com.examw.test.app.AppContext;
import com.examw.test.dao.IPaperItemDataDelegate;
import com.examw.test.dao.IPaperItemDataDelegate.SubmitResultHandler;
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
	
	private boolean displayAnswer, hasTitle;
	private TextView titleView;
	private ViewFlow viewFlow;
	private CountdownViewSupport countdownViewSupport;
	
	private final List<PaperItemModel> dataSource;
	private PaperAdapter adapter;
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
		final View btnPrev = this.findViewById(R.id.main_paper_prev);
		btnPrev.setOnClickListener(this);
		//倒计时View
		final TextView timeView = (TextView)this.findViewById(R.id.main_paper_time);
		this.countdownViewSupport = new CountdownViewSupport(timeView);
		//收藏按钮
		final View btnFav = this.findViewById(R.id.main_paper_fav);
		btnFav.setOnClickListener(this);
		//提交按钮
		final View btnSubmit = this.findViewById(R.id.main_paper_submit);
		btnSubmit.setOnClickListener(this);
		//下一题按钮处理
		final View btnNext = this.findViewById(R.id.main_paper_next);
		btnNext.setOnClickListener(this);
		
		//获取意图
		final Intent intent = this.getIntent();
		if(intent != null){
			//获取是否显示答案
			this.displayAnswer = intent.getBooleanExtra(PAPER_ITEM_ISDISPLAY_ANSWER, false);
			//获取标题
			final String title = intent.getStringExtra(PAPER_ITEM_TITLE);
			if((this.hasTitle = StringUtils.isNotBlank(title)) && this.titleView != null){
				this.titleView.setText(title);
			}
		}
		//设置按钮状态
		btnFav.setVisibility(this.isDisplayAnswer() ? View.VISIBLE : View.INVISIBLE);
		btnSubmit.setVisibility(this.isDisplayAnswer() ? View.INVISIBLE : View.VISIBLE);
		timeView.setVisibility(this.isDisplayAnswer() ?  View.INVISIBLE : View.VISIBLE);
	}
	/*
	 * 重载切换视图。
	 * @see org.taptwo.android.widget.ViewFlow.ViewSwitchListener#onSwitched(android.view.View, int)
	 */
	@Override
	public void onViewLazyInitialize(View view, int position) {
		Log.d(TAG, "ViewFlow惰性加载试题..." + position);
		if(!this.hasTitle && this.titleView != null && this.dataSource.size() >  position){
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
		
		int order = -1;
		if(this.getIntent() != null){
			order = this.getIntent().getIntExtra(PAPER_ITEM_ORDER, order);
		}
		//异步加载数据
		new LoadDataAsyncTask(order).execute();
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
		if(!this.isDisplayAnswer() && (event.getKeyCode() == KeyEvent.KEYCODE_BACK)){
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
				if(this.isDisplayAnswer()){
					this.finish();
				}else{
					this.backExitHandler();
				}
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
				if(this.isDisplayAnswer()){
					this.favoriteHandler(v, this.getCurrentItemOrder());
				}
				break;
			}
			case R.id.main_paper_submit:{//提交按钮处理
				Log.d(TAG, "提交按钮处理...");
				if(!this.isDisplayAnswer()){
					this.submitHandler();
				}
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
	private void favoriteHandler(final View btnView, final int pos){
		final ImageButton favoriteImageButton = (ImageButton)btnView;
		if(favoriteImageButton == null) return;
		//异步线程处理
		pools.execute(new Runnable() {
			@Override
			public void run() {
				try {
					if(dataSource == null || dataSource.size() < pos){
						return;
					}
					Log.d(TAG, "异步开始收藏处理试题:" + (pos + 1));
					PaperItemModel itemModel = dataSource.get(pos);
					if(itemModel == null){
						Log.d(TAG, "试题[" + (pos + 1) + "]不存在!");
						return;
					}
					//
					final IPaperItemDataDelegate dataDelegate = AppContext.getPaperDataDelegate();
					if(dataDelegate == null){
						Log.d(TAG, "数据委托不存在!");
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
					final IPaperItemDataDelegate dataDelegate = AppContext.getPaperDataDelegate();
					if(dataDelegate != null){
						//提交试卷
						dataDelegate.submitPaper(useTimes, new SubmitResultHandler(){
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
	 * 异步加载数据。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月20日
	 */
	private class LoadDataAsyncTask extends AsyncTask<Void, Void, Object>{
		private int totalTime = 0;
		private int order;
		/**
		 * 构造函数。
		 * @param order
		 */
		public LoadDataAsyncTask(int order){
			Log.d(TAG, "异步加载数据初始化...");
			this.order = order;
		}
		/*
		 *后台线程异步处理。
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Object doInBackground(Void... params) {
			try{
				Log.d(TAG, "后台线程异步加载处理...");
				//2.数据监听者类
			    final IPaperItemDataDelegate dataDelegate =  AppContext.getPaperDataDelegate();
				if(dataDelegate == null){
					Log.d(TAG, "未获取到试卷数据委托!");
					return null;
				}
				//3.获取考试时长
				this.totalTime = dataDelegate.timeOfPaperView();
				//4.加载试题数据
				Log.d(TAG, "准备加载试题数据...");
				//4.2添加数据
				List<PaperItemModel> list = dataDelegate.dataSourceOfPaperViews();
				//获取当前试题
				this.order = Math.max(this.order, dataDelegate.currentOrderOfPaperView());
				//返回数据
				return (list == null || list.size() == 0) ? null : list.toArray(new PaperItemModel[0]);
			}catch(Exception e){
				Log.e(TAG, "后台线程异步加载异常:" + e.getMessage(), e);
			}
			return null;
		}
		/*
		 * 前台主线程处理。
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		 @Override
		protected void onPostExecute(Object result) {
			 if(result != null){
				//清空试卷数据
				dataSource.clear();
				dataSource.addAll(Arrays.asList((PaperItemModel[])result));
				 //提醒适配器更新
				 Log.d(TAG, "通知试卷数据适配器更新...");
				 adapter.notifyDataSetChanged();
				 //设置选中的题序	  
				viewFlow.setSelection(this.order); 
			 }
			 //关闭等待动画
			 waitingViewDialog.cancel();
			 //
			 if(!isDisplayAnswer()){
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
	}
}