package com.examw.test.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
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
public class PaperRecordActivity extends Activity implements View.OnClickListener,PullToRefreshListView.OnRefreshListener<ListView>,AdapterView.OnItemClickListener{
	private static final String TAG = "PaperRecordActivity";
	private WaitingViewDialog waitingViewDialog;
	private PullToRefreshListView pullToRefreshListView;
	private int pageIndex;
	
	private final List<PaperRecordModel> dataSource;
	private PaperRecordAdapter adapter;
	private PaperDao paperDao;
	/**
	 * 科目代码。
	 */
	public static final String PAPER_SUBJECT_CODE = "subject_code";
	/**
	 * 科目标题。
	 */
	public static final String PAPER_SUBJECT_TITLE = "subject_title";
	
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
		final TextView titleView = (TextView)this.findViewById(R.id.title);
		//设置标题
		titleView.setText(this.getIntent().getStringExtra(PAPER_SUBJECT_TITLE));
		
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
		//设置数据适配器
		final ListView listView = this.pullToRefreshListView.getRefreshableView();
		listView.setAdapter(this.adapter);
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
		//3清空数据
		this.dataSource.clear();
		//4.异步加载数据
		
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
		//new RefreshDataTask().execute();
	}
	/*
	 * 选中行事件处理。
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.d(TAG, "选中行事件处理..." + position);
		
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
		 * (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			return null;
		}
		/*
		 * (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}
	}
}