package com.examw.test.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.examw.test.R;
import com.examw.test.adapter.PaperResultAdapter;
import com.examw.test.dao.PaperDao;
import com.examw.test.dao.PaperDao.PaperResultModel;
import com.examw.test.widget.WaitingViewDialog;

/**
 * 试卷结果Activity。
 * 
 * @author jeasonyoung
 * @since 2015年7月29日
 */
public class PaperResultActivity extends Activity implements View.OnClickListener{
	private static final String TAG = "PaperResultActivity";
	private WaitingViewDialog waitingViewDialog;
	
	private final List<String> dataSource;
	private PaperResultAdapter adapter;
	private String paperRecordId;
	/**
	 * 试卷记录ID。
	 */
	public static final String PAPER_RECORD_ID = "paper_record_id";
	/**
	 * 构造函数。
	 */
	public PaperResultActivity(){
		this.dataSource = new ArrayList<String>();
	}
	/*
	 * 重载创建。
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "重载创建...");
		super.onCreate(savedInstanceState);
		//加载布局
		this.setContentView(R.layout.ui_main_paper_result);
		
		//初始化等待动画
		this.waitingViewDialog = new WaitingViewDialog(this);
		
		//加载标题
		final TextView titleView = (TextView)this.findViewById(R.id.title);
		titleView.setText(R.string.main_paper_result_title);
		
		//返回按钮
		final View btnBack = this.findViewById(R.id.btn_goback);
		btnBack.setOnClickListener(this);
		
		//结果列表
		final ListView listView = (ListView)this.findViewById(R.id.list_main_paper_result);
		//初始化数据适配器
		this.adapter = new PaperResultAdapter(this, this.dataSource);
		//设置列表数据适配器
		listView.setAdapter(this.adapter);
		
		//查看试题按钮
		final View btnReview = this.findViewById(R.id.paper_result_btnReview);
		btnReview.setOnClickListener(this);
	}
	/*
	 * 重载开始。
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		Log.d(TAG, "重载开始加载数据...");
		super.onStart();
		//加载数据
		this.paperRecordId = this.getIntent().getStringExtra(PAPER_RECORD_ID);
		//开始等待动画
		this.waitingViewDialog.show();
		//异步加载数据
		new LoadDataAsyncTask(this).execute(this.paperRecordId);
	}
	/*
	 * 按钮事件处理。
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		Log.d(TAG, "按钮事件处理..." + v);
		switch(v.getId()){
			case R.id.btn_goback:{//返回事件处理
				 Log.d(TAG, "返回事件处理...");
				 this.finish();
				 break;
			}
			case R.id.paper_result_btnReview:{//查看试题按钮
				Log.d(TAG, "查看试题按钮事件处理...");
				//设置意图
				Intent intent = new Intent(this, PaperActivity.class);
				intent.putExtra(PaperActivity.PAPER_ITEM_ISDISPLAY_ANSWER, true);
				//启动activity
				this.startActivity(intent);
				//关闭当前
				this.finish();
				break;
			}
		}
	}
	//异步加载数据。
	private class LoadDataAsyncTask extends AsyncTask<Object, Void, Object>{
		private WeakReference<Context> refContext;
		/**
		 * 构造函数。
		 * @param context
		 */
		public LoadDataAsyncTask(Context context){
			this.refContext = new WeakReference<Context>(context);
		}
		/*
		 * 后台异步线程处理。
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Object doInBackground(Object... params) {
			try{
				Log.d(TAG, "后台线程加载数据...");
				final Context context = this.refContext.get();
				if(context != null){
					//初始化数据操作
					PaperDao dao = new PaperDao(context);
					//加载试卷记录数据
					PaperResultModel resultModel = dao.loadPaperRecordResult((String)params[0]);
					if(resultModel != null){
						//初始化结果数据
						List<String> result = new ArrayList<String>(5);
						result.add(null);
						//得分
						result.add(String.format("得分:<font color='red'>%.2f</font>", resultModel.getScore()));
						result.add(null);
						//总题数
						result.add(String.format("共:%d题", resultModel.getTotal()));
						//做对
						result.add(String.format("做对:%d题", resultModel.getRights()));
						//做错
						result.add(String.format("做错:%d题", resultModel.getErrors()));
						//未做
						result.add(String.format("未做:%d题", resultModel.getNots()));
						result.add(null);
						//共用时
						result.add(String.format("共用时:%d''", resultModel.getUseTimes()));
						result.add(String.format("完成时间:%s", resultModel.getLastTime()));
						//
						return result.toArray(new String[0]);
					}
				}
			}catch(Exception e){
				Log.e(TAG, "后台异步加载数据异常:" + e.getMessage(), e);
			}
			return null;
		}
		/*
		 * 前台主线程处理。
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Object result) {
			Log.d(TAG, "前台数据处理...");
			if(result != null){
				//清空数据源
				if(dataSource.size() > 0) dataSource.clear();
				//加载数据
				dataSource.addAll(Arrays.asList((String[])result));
				//通知数据适配器更新
				adapter.notifyDataSetChanged();
			}
			//关闭等待动画
			waitingViewDialog.cancel();
		}
	}
}