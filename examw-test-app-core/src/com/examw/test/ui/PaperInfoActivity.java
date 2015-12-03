package com.examw.test.ui;

import org.apache.commons.lang3.StringUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.examw.test.R;
import com.examw.test.app.AppContext;
import com.examw.test.dao.PaperDao;
import com.examw.test.dao.PaperItemData;
import com.examw.test.model.PaperModel;
import com.examw.test.model.PaperRecordModel;
import com.examw.test.widget.WaitingViewDialog;

/**
 * 试卷详情。
 * 
 * @author jeasonyoung
 * @since 2015年7月18日
 */
public class PaperInfoActivity extends Activity implements View.OnClickListener {
	private static final String TAG = "PaperInfoActivity";
	private WaitingViewDialog waitingViewDialog;
	
	private TextView tvTitle,tvSubject,tvArea,tvType,tvYear,tvTotal,tvItems,tvTimes;
	private Button btnStart,btnContinue,btnRest,btnReview;
	
	private PaperDao paperDao;
	private PaperModel paperModel;
	private PaperRecordModel recordModel;
	private StartType paperStartType = StartType.None;
	
	public static final String INTENT_PAPERID_KEY = "paperId";
	public static final String INTENT_SUBJECTNAME_KEY = "subjectName";
	
	/*
	 * 重载创建。
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "重载创建...");
		super.onCreate(savedInstanceState);
		//加载布局
		this.setContentView(R.layout.ui_main_paper_info);
		
		//初始化等待动画
		this.waitingViewDialog = new WaitingViewDialog(this);
		
		//返回按钮
		final View btnBack = this.findViewById(R.id.btn_goback);
		btnBack.setOnClickListener(this);
		//标题
		final TextView tvTitle = (TextView)this.findViewById(R.id.title);
		tvTitle.setText(this.getResources().getString(R.string.main_paperinfo_title));
		//加载控件
		//开始考试按钮
		this.btnStart = (Button)this.findViewById(R.id.main_paperinfo_btn_start);
		this.btnStart.setOnClickListener(this);
		//继续考试按钮
		this.btnContinue = (Button)this.findViewById(R.id.main_paperinfo_btn_continue);
		this.btnContinue.setOnClickListener(this);
		//重新开始按钮
		this.btnRest = (Button)this.findViewById(R.id.main_paperinfo_btn_rest);
		this.btnRest.setOnClickListener(this);
		//查看成绩按钮
		this.btnReview = (Button)this.findViewById(R.id.main_paperinfo_btn_review);
		this.btnReview.setOnClickListener(this);
		
		//1.试卷标题
		this.tvTitle = (TextView)this.findViewById(R.id.main_paperinfo_title);
		//2.所属科目
		this.tvSubject = (TextView)this.findViewById(R.id.main_paperinfo_subject);
		//3.所属地区
		this.tvArea = (TextView)this.findViewById(R.id.main_paperinfo_area);
		//4.试卷类型
		this.tvType = (TextView)this.findViewById(R.id.main_paperinfo_type);
		//5.使用年份
		this.tvYear = (TextView)this.findViewById(R.id.main_paperinfo_year);
		//6.总分
		this.tvTotal = (TextView)this.findViewById(R.id.main_paperinfo_totals);
		//7.试题数
		this.tvItems = (TextView)this.findViewById(R.id.main_paperinfo_items);
		//8.时间
		this.tvTimes = (TextView)this.findViewById(R.id.main_paperinfo_times);
	}
	/*
	 * 加载数据。
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		Log.d(TAG, "加载数据...");
		super.onStart();
		//启动等待动画
		this.waitingViewDialog.show();
		//加载数据
		Intent intent = this.getIntent();
		if(intent != null){
			new LoadPaperAsyncTask(intent.getStringExtra(INTENT_SUBJECTNAME_KEY))
			.execute(intent.getStringExtra(INTENT_PAPERID_KEY));
		}
	}
	/*
	 * 按钮点击事件处理。
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		Log.d(TAG, "按钮点击事件处理...");
		switch(v.getId()){
			case R.id.btn_goback:{
				Log.d(TAG, "返回按钮处理...");
				this.finish();
				break;
			}
			case R.id.main_paperinfo_btn_start:{//开始
				Log.d(TAG, "开始做题...");
				this.paperStartType = StartType.Start;
				//启动等待动画
				waitingViewDialog.show();
				//跳转异步处理
				new GotoPaperAsyncTask().execute(this.paperStartType);
				break;
			}
			case R.id.main_paperinfo_btn_continue:{//继续
				this.paperStartType = StartType.Continue;
				//启动等待动画
				waitingViewDialog.show();
				//跳转异步处理
				new GotoPaperAsyncTask().execute(this.paperStartType);
				break;
			}
			case R.id.main_paperinfo_btn_rest:{//重新开始
				this.paperStartType = StartType.Rest;
				//启动等待动画
				waitingViewDialog.show();
				//跳转异步处理
				new GotoPaperAsyncTask().execute(this.paperStartType);
				break;
			}
			case R.id.main_paperinfo_btn_review:{//查看成绩
				this.paperStartType = StartType.Review;
				//启动等待动画
				waitingViewDialog.show();
				//跳转异步处理
				new GotoPaperAsyncTask().execute(this.paperStartType);
				break;
			}
		}
	}
	//做题开始类型
	public enum StartType{None, Start, Continue, Rest, Review };
	/**
	 * 异步加载数据。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月18日
	 */
	private class LoadPaperAsyncTask extends AsyncTask<String, Void, Boolean>{
		private final String subjectName;
		/**
		 * 构造函数。
		 * @param subjectName
		 */
		public LoadPaperAsyncTask(String subjectName){
			this.subjectName = subjectName;
		}
		/*
		 * 后台线程加载数据。
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Boolean doInBackground(String... params) {
			try {
				Log.d(TAG, "后台线程加载试卷数据..." + params[0]);
				if(StringUtils.isNotEmpty(params[0])){
					paperDao = new PaperDao(PaperInfoActivity.this);
					//加载试卷
					paperModel = paperDao.loadPaper(params[0]);
					//加载试卷记录
					recordModel = paperDao.loadNewsRecord(params[0]);
					
					return true;
				}
			} catch (Exception e) {
				Log.e(TAG, "加载数据发生异常:" + e.getMessage(), e);
			}
			return false;
		}
		/*
		 * 前台主线程处理
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Boolean result) {
			Log.d(TAG, "前台主线程处理...");
			if(result){
				//试卷记录
				if(recordModel == null){//没有做题记录
					btnStart.setVisibility(View.VISIBLE);
					ViewGroup.LayoutParams params = btnStart.getLayoutParams();
					params.width = LinearLayout.LayoutParams.MATCH_PARENT;
					btnStart.setLayoutParams(params);
					
					btnContinue.setVisibility(View.GONE);
					btnRest.setVisibility(View.GONE);
					btnReview.setVisibility(View.GONE);
				}else if(recordModel.isStatus()) {//试卷已做完
					btnStart.setVisibility(View.VISIBLE);
					ViewGroup.LayoutParams params = btnStart.getLayoutParams();
					params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
					btnStart.setLayoutParams(params);
					btnContinue.setVisibility(View.GONE);
					btnRest.setVisibility(View.GONE);
					btnReview.setVisibility(View.VISIBLE);
				}else {//试卷未做完
					btnStart.setVisibility(View.GONE);
					btnContinue.setVisibility(View.VISIBLE);
					btnRest.setVisibility(View.VISIBLE);
					btnReview.setVisibility(View.GONE);
				}
				//当前试卷信息
				if(paperModel != null){
					//1.试卷标题
					tvTitle.setText(paperModel.getName());
					//2.所属科目
					if(StringUtils.isNotBlank(this.subjectName)){
						tvSubject.setText("所属科目:" + StringUtils.trimToEmpty(this.subjectName));
					}else {
						tvSubject.setVisibility(View.GONE);
					}
					//3.所属地区
					if(StringUtils.isNotBlank(paperModel.getAreaName())){
						tvArea.setText("所属地区:" + paperModel.getAreaName());
					}else {
						tvArea.setVisibility(View.GONE);
					}
					//4.试卷类型
					if(paperModel.getType() > 0){
						tvType.setText("试卷类型:" +PaperModel.loadPaperTypeName(paperModel.getType()));
					}else {
						tvType.setVisibility(View.GONE);
					}
					//5.使用年份
					if(paperModel.getYear() != null && paperModel.getYear() > 0){
						tvYear.setText("使用年份:" + String.valueOf(paperModel.getYear()));
					}else {
						tvYear.setVisibility(View.GONE);
					}
					//6.总分
					if(paperModel.getScore() != null && paperModel.getScore() > 0){
						tvTotal.setText("总分:" + paperModel.getScore() + " 分");
					}else {
						tvTotal.setVisibility(View.GONE);
					}
					//7.试题数
					if(paperModel.getTotal() != null){
						tvItems.setText("总题数:" + paperModel.getTotal() + " 题");
					}else {
						tvItems.setVisibility(View.GONE);
					}
					//8.时间
					if(paperModel.getTime() != null){
						tvTimes.setText("时长:" + String.valueOf(paperModel.getTime()) + " (分钟)");
					}else {
						tvTimes.setVisibility(View.GONE);
					}
				}
			}
			//关闭等待动画
			waitingViewDialog.cancel();
		}
	}
	//异步线程跳转
	private class GotoPaperAsyncTask extends AsyncTask<StartType, Void, Boolean>{
		private boolean displayAnswer;
		private StartType type;
		/*
		 * 后台线程数据处理。
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Boolean doInBackground(StartType... params) {
			try{
				Log.d(TAG, "后台数据处理跳转数据...");
				switch(this.type = params[0]){
					case None:{//未知
						return false;
					}
					case Start://开始
					case Rest:{//重新开始
						this.displayAnswer = false;
						//新增试卷记录
						recordModel = new PaperRecordModel(paperModel.getId()); 
						//初始化数据操作
						if(paperDao == null){
							paperDao = new PaperDao(PaperInfoActivity.this);
						}
						//保存到数据库
						paperDao.updatePaperRecord(recordModel);
						break;
					}
					case Continue:{//继续
						this.displayAnswer = false;
						break;
					}
					case Review:{//查看成绩
						this.displayAnswer = true;
						break;
					}
				}
				//设置试卷数据委托
				AppContext.setPaperDataDelegate(new PaperItemData(PaperInfoActivity.this, paperModel, recordModel.getId(), this.type));
				return true;
			}catch(Exception e){
				Log.d(TAG, "跳转后台线程处理异常:"+ e.getMessage(), e);
				return false;
			}
		}
		/*
		 * 跳转主线程处理。
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Boolean result) {
			Log.d(TAG, "跳转主线程处理...");
			//关闭等待动画
			if(waitingViewDialog != null){
				waitingViewDialog.cancel();
			}
			if(result){
				Intent intent = new Intent();
				if(this.type == StartType.Review){//查看成绩
					intent.setClass(PaperInfoActivity.this, PaperResultActivity.class);
					intent.putExtra(PaperResultActivity.PAPER_RECORD_ID, recordModel.getId());
				}else{
					intent.setClass(PaperInfoActivity.this, PaperActivity.class);
				}
				intent.putExtra(PaperActivity.PAPER_ITEM_ISDISPLAY_ANSWER, this.displayAnswer);
				//启动activity
				startActivity(intent);
				//关闭当前
				finish();
			}else {
				Toast.makeText(PaperInfoActivity.this, "发生未知异常!", Toast.LENGTH_LONG).show();
			}
		}
	}
}