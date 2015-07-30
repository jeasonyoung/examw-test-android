package com.examw.test.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.examw.test.R;
import com.examw.test.adapter.PaperListAdapter;
import com.examw.test.dao.PaperDao;
import com.examw.test.model.PaperModel;
import com.examw.test.model.PaperModel.PaperType;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
/**
 * 试卷列表。
 * 
 * @author jeasonyoung
 * @since 2015年7月3日
 */
public class MainHomePaperPagerFragment  extends Fragment implements PullToRefreshListView.OnRefreshListener<ListView>,AdapterView.OnItemClickListener{
	private static final String TAG = "MainHomePaperPagerFragment";
	private final MainActivity mainActivity;
	private final PaperModel.PaperType type;
	private final PaperDao.SubjectTotalModel subject;
	
	private final List<PaperDao.PaperInfoModel> dataSource;
	private final PaperListAdapter adapter;
	
	private PullToRefreshListView pullToRefreshListView;
	private PaperDao paperDao;
	
	private int pageIndex;
	/**
	 * 构造函数。
	 * @param mainActivity
	 * @param subject
	 * @param type
	 */
	public MainHomePaperPagerFragment(MainActivity mainActivity, PaperDao.SubjectTotalModel subject, PaperModel.PaperType type){
		Log.d(TAG, "初始化－" + type);
		this.mainActivity =  mainActivity;
		this.subject = subject;
		this.type = type;
		this.dataSource = new ArrayList<PaperDao.PaperInfoModel>();
		this.adapter = new PaperListAdapter(this.mainActivity, this.dataSource);
	}
	/*
	 * 重载UI布局
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "加载布局...");
		//加载布局文件
		final View view = inflater.inflate(R.layout.ui_main_home_paper_viewpager, container, false);
		//加载列表
		this.pullToRefreshListView = (PullToRefreshListView)view.findViewById(R.id.list_home_papers);
		//设置刷新方向
		this.pullToRefreshListView.setMode(PullToRefreshListView.Mode.PULL_FROM_END);
		//设置刷新监听器
		this.pullToRefreshListView.setOnRefreshListener(this);
		//设置数据行监听器
		this.pullToRefreshListView.setOnItemClickListener(this);
		//设置数据适配器
		final ListView listView = this.pullToRefreshListView.getRefreshableView();
		listView.setAdapter(this.adapter);
		//返回UI。
		return view;
	}
	/*
	 * 重载开始加载数据。
	 * @see android.support.v4.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
		Log.d(TAG, "重载开始加载数据...");
		super.onStart();
		//1.开始等待动画
		this.mainActivity.waitingViewDialog.show();
		//2.设置页码
		this.pageIndex = 0;
		//3清空数据
		this.dataSource.clear();
		//4.异步加载数据
		new RefreshDataTask(this.mainActivity).execute(this.subject.getCode(), this.type, this.pageIndex);
	}
	/*
	 * 刷新数据处理
	 * @see com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener#onRefresh(com.handmark.pulltorefresh.library.PullToRefreshBase)
	 */
	@Override
	public void onRefresh(PullToRefreshBase<ListView> arg) {
		//1.开始等待动画
		this.mainActivity.waitingViewDialog.show();
		//2.刷新数据
		this.pageIndex += 1;
		
		Log.d(TAG, "刷新数据页..." + this.pageIndex);
		
		//3.异步加载数据
		new RefreshDataTask(this.mainActivity).execute(this.subject.getCode(), this.type, this.pageIndex);
	}
	/*
	 * 选中行事件处理。
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.d(TAG, "选中数据行..." + position);
		if(this.dataSource.size() > position - 1){
			PaperDao.PaperInfoModel data = this.dataSource.get(position - 1);
			if(data != null){
				//初始化意图
				Intent intent = new Intent(this.mainActivity, PaperInfoActivity.class);
				intent.putExtra(PaperInfoActivity.INTENT_PAPERID_KEY, data.getId());
				intent.putExtra(PaperInfoActivity.INTENT_SUBJECTNAME_KEY, data.getSubjectName());
				//开启
				this.startActivity(intent);
			}
		}
	}
	/**
	 * 刷新加载数据。
	 * @author jeasonyoung
	 * @since 2015年7月3日
	 */
	private class RefreshDataTask extends AsyncTask<Object, Void, Object>{
		private final WeakReference<Context> refContext;
		/**
		 * 构造函数。
		 * @param context
		 */
		public RefreshDataTask(Context context){
			Log.d(TAG, "初始化异步加载数据...");
			this.refContext = new WeakReference<Context>(context);
		}
		/*
		 * 后台线程加载数据。
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Object doInBackground(Object... params) {
			Log.d(TAG, "后台线程加载数据页..." + pageIndex + "-" + subject.getCode() + "-" + type);
			 try {
				 //科目
				 final String subjectCode = (String)params[0];
				 //试卷类型
				 final PaperType paperType = (PaperType)params[1];
				 //页码
				 final int index = Math.max((Integer)params[2], 0);
				 //惰性加载
				 if(paperDao == null){
					 paperDao = new PaperDao(this.refContext.get());
				 }
				//查询数据
				List<PaperDao.PaperInfoModel> list = paperDao.findPaperInfos(subjectCode, paperType, index);
				if(list != null && list.size() > 0){
					Log.d(TAG, "加载数据行数:" + list.size());
					return list.toArray(new PaperDao.PaperInfoModel[0]);
				}
			} catch (Exception e) {
				Log.e(TAG, "后台线程加载数据页[" + pageIndex + "-" + subject.getCode() + "-" + type + "]异常:" + e.getMessage() , e);
			}
			return null;
		}
		/*
		 * 前台线程更新UI
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Object result) {
			try {
				Log.d(TAG, "前台线程更新UI...");
				if(result != null){
					//添加数据
					dataSource.addAll(Arrays.asList((PaperDao.PaperInfoModel[])result));
					//通知数据适配器
					adapter.notifyDataSetChanged(); 
					//刷新
					pullToRefreshListView.onRefreshComplete();
				}else if(pageIndex > 0){
					pageIndex -= 1;
				}
				//关闭等待动画
				mainActivity.waitingViewDialog.cancel();
			} catch (Exception e) {
				Log.e(TAG, "更新数据发送异常:" + e.getMessage(), e);
			}
		}
	}
}