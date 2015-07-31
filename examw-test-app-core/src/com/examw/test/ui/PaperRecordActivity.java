package com.examw.test.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.examw.test.R;
import com.examw.test.adapter.PaperRecordAdapter;
import com.examw.test.dao.PaperDao;
import com.examw.test.model.PaperRecordModel;
import com.examw.test.widget.WaitingViewDialog;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * 做题记录Activity。
 * 
 * @author jeasonyoung
 * @since 2015年7月30日
 */
public class PaperRecordActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener,
	AdapterView.OnItemLongClickListener,PullToRefreshListView.OnRefreshListener<ListView> {
	private static final String TAG = "PaperRecordActivity";
	private WaitingViewDialog waitingViewDialog;
	private TextView titleView;
	private PullToRefreshListView pullToRefreshListView;
	private String subjectCode,subjectName;
	private int pageIndex, paperTotals;
	
	private final List<PaperRecordModel> dataSource;
	private PaperRecordAdapter adapter;
	private PaperDao paperDao;
	/**
	 * 科目代码。
	 */
	public static final String PAPER_SUBJECT_CODE = "subject_code";
	/**
	 * 科目名称。
	 */
	public static final String PAPER_SUBJECT_NAME = "subject_name";
	/**
	 * 科目试卷统计
	 */
	public static final String PAPER_SUBJECT_TOTALS = "subject_totals";
	
	/**
	 * 构造函数。
	 */
	public PaperRecordActivity(){
		Log.d(TAG, "初始化...");
		this.pageIndex = 0;
		this.dataSource = new ArrayList<PaperRecordModel>();
	}
	/*
	 * 重载创建。
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "重载创建...");
		super.onCreate(savedInstanceState);
		//设置布局
		this.setContentView(R.layout.ui_main_paper_record);
		//初始化等待动画
		this.waitingViewDialog = new WaitingViewDialog(this);
		
		//加载返回按钮
		final View btnBack = this.findViewById(R.id.btn_goback);
		btnBack.setOnClickListener(this);
		//加载标题
		this.titleView = (TextView)this.findViewById(R.id.title);
		
		//加载列表
		this.pullToRefreshListView = (PullToRefreshListView)this.findViewById(R.id.list_main_paper_record);
		//设置刷新方向
		this.pullToRefreshListView.setMode(PullToRefreshListView.Mode.PULL_FROM_END);
		//设置刷新监听器
		this.pullToRefreshListView.setOnRefreshListener(this);
		//设置数据行监听器
		this.pullToRefreshListView.setOnItemClickListener(this);
		//初始化数据适配器
		this.adapter = new PaperRecordAdapter(this, this.dataSource);
		//获取listview
		final ListView listView = this.pullToRefreshListView.getRefreshableView();
		//设置数据适配器
		listView.setAdapter(this.adapter);
		//设置数据行长按监听器
		listView.setOnItemLongClickListener(this);
	}
	//设置标题
	private void setActivityTitle(String subjectName, int subjectTotals){
		if(this.titleView != null){
			this.titleView.setText(String.format("%1$s(%2$d)", subjectName, Math.max(subjectTotals,0)));
		}
	}
	/*
	 * 重载加载数据。
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		Log.d(TAG, "重载加载数据...");
		super.onStart();
		//1.开始等待动画
		this.waitingViewDialog.show();
		//2.设置页码
		this.pageIndex = 0;
		//3.加载科目代码
		if(this.getIntent() != null){
			this.subjectCode = this.getIntent().getStringExtra(PAPER_SUBJECT_CODE);
			this.subjectName = this.getIntent().getStringExtra(PAPER_SUBJECT_NAME);
			this.paperTotals = this.getIntent().getIntExtra(PAPER_SUBJECT_TOTALS, 0);
			this.setActivityTitle(this.subjectName, this.paperTotals);
		}
		//3清空数据
		this.dataSource.clear();
		//4.异步加载数据
		new RefreshDataAsyncTask(this).execute(this.subjectCode, this.pageIndex);
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
		}
	}
	/*
	 * 刷新数据处理
	 * @see com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener#onRefresh(com.handmark.pulltorefresh.library.PullToRefreshBase)
	 */
	@Override
	public void onRefresh(PullToRefreshBase<ListView> arg0) {
		Log.d(TAG, "刷新数据处理...");
		//1.开始等待动画
		this.waitingViewDialog.show();
		//2.刷新数据
		this.pageIndex += 1;
		Log.d(TAG, "刷新数据页..." + this.pageIndex);
		//3.异步加载数据
		new RefreshDataAsyncTask(this).execute(this.subjectCode, this.pageIndex);
	}
	/*
	 * 长按事件处理。
	 * @see android.widget.AdapterView.OnItemLongClickListener#onItemLongClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		final int pos = position - 1;
		if(dataSource.size() < pos)return false;
		Log.d(TAG, "长按数据行处理..." + position);
		new AlertDialog.Builder(this)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setTitle(R.string.main_paper_record_delete_title)
		.setMessage(R.string.main_paper_record_delete_msg)
		.setNegativeButton(R.string.main_paper_record_delete_btnCancel, new DialogInterface.OnClickListener() {
			/*
			 * 取消退出。
			 * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface, int)
			 */
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.d(TAG, "取消删除...");
				dialog.dismiss();
			}
		})
		.setPositiveButton(R.string.main_paper_record_delete_btnSubmit, new DialogInterface.OnClickListener() {
			/*
			 * 交卷处理。
			 * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface, int)
			 */
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.d(TAG, "确认删除处理...");
				dialog.dismiss();
				//开启等待动画
				waitingViewDialog.show();
				//从数据源取出数据
				final PaperRecordModel recordModel = dataSource.get(pos);
				if(recordModel != null){
					//异步线程处理从数据库中删除。
					PaperActivity.pools.execute(new Runnable() {
						@Override
						public void run() {
							try {
								Log.d(TAG, "异步线程删除做题记录..." + pos);
								//惰性初始化
								if(paperDao == null){
									paperDao = new PaperDao(PaperRecordActivity.this);
								}
								//删除数据
								paperDao.deletePaperRecord(recordModel.getId());
							} catch (Exception e) {
								Log.e(TAG, "删除做题记录数据[" +recordModel+"]异常:" + e.getMessage(), e);
							}
						}
					});
				}
				//删除数据源中的记录
				dataSource.remove(pos);
				//设置标题
				setActivityTitle(subjectName, --paperTotals);
				//通知数据适配器刷新数据
				adapter.notifyDataSetChanged();
				//关闭等待动画
				waitingViewDialog.cancel();
			}
		}).show();
		return true;
	}
	/*
	 * 选中行事件处理。
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.d(TAG, "选中行事件处理..." + position);
		if(this.dataSource.size() > position){
			final PaperRecordModel model = this.dataSource.get(position);
			if(model == null)return;
			///TODO:
			
		}
	}
	/**
	 * 异步加载数据。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月30日
	 */
	private class RefreshDataAsyncTask extends AsyncTask<Object, Void, Object>{
		private final WeakReference<Context> refContext;
		/**
		 * 构造函数。
		 * @param context
		 */
		public RefreshDataAsyncTask(Context context){
			Log.d(TAG, "初始化异步加载数据...");
			this.refContext = new WeakReference<Context>(context);
		}
		/*
		 * 后台线程数据处理。
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Object doInBackground(Object... params) {
			try {
				Log.d(TAG, "后台线程数据处理...");
				//加载参数
				final String subjectCode = (String)params[0];
				final int index = (Integer)params[1];
				//惰性加载
				if(paperDao == null){
					paperDao = new PaperDao(this.refContext.get());
				}
				//查询数据
				final List<PaperRecordModel> list = paperDao.loadPaperRecords(subjectCode, index);
				if(list != null && list.size() > 0){
					return list.toArray(new PaperRecordModel[0]);
				}
			} catch (Throwable e) {
				Log.e(TAG, "后台线程数据处理异常:" + e.getMessage(), e);
			}
			return null;
		}
		/*
		 * 前端主线程处理。
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Object result) {
			Log.d(TAG, "前端主线程处理...");
			if(result != null){
				//添加数据
				dataSource.addAll(Arrays.asList((PaperRecordModel[])result));
				//通知数据适配器
				adapter.notifyDataSetChanged(); 
			}else if(pageIndex > 0){
				pageIndex -= 1;
			}
			//刷新
			pullToRefreshListView.onRefreshComplete();
			//关闭等待动画
			waitingViewDialog.cancel();
		}
	}
}