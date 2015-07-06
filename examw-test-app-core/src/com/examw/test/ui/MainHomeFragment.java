package com.examw.test.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.examw.test.R;
import com.examw.test.app.AppContext;
import com.examw.test.dao.PaperDao;

/**
 * 首页Fragment
 * 
 * (用于按科目统计试卷列表)
 * @author jeasonyoung
 * @since 2015年7月1日
 */
public class MainHomeFragment extends Fragment implements AdapterView.OnItemClickListener {
	private static final String TAG = "MainHomeFragment";
	private final MainActivity mainActivity;
	private PaperDao dao;
	
	private TextView tvTitle;
	private ListView listView;
	
	private List<PaperDao.SubjectTotalModel> dataSource;
	private MainHomeAdapter adapter;
	/**
	 * 构造函数。
	 * @param mainActivity
	 */
	public MainHomeFragment(final MainActivity mainActivity){
		Log.d(TAG, "初始化...");
		this.mainActivity = mainActivity;
		this.dataSource = new ArrayList<PaperDao.SubjectTotalModel>();
		this.adapter = new MainHomeAdapter(this.mainActivity, this.dataSource);
	}
	/*
	 * 加载UI。
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "装载UI...");
		//加载布局
		final View view = inflater.inflate(R.layout.ui_main_home, container, false);
		//标题
		this.tvTitle = (TextView)view.findViewById(R.id.main_home_title);
		//列表
		this.listView = (ListView)view.findViewById(R.id.list_main_home);
		//数据适配器处理
		this.listView.setAdapter(this.adapter);
		//设置行选中事件处理
		this.listView.setOnItemClickListener(this);
		//返回UI视图
		return view;
	}
	/*
	 * 加载数据处理
	 * @see android.support.v4.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
		super.onStart();
		//启动等待动画
		this.mainActivity.waitingViewDialog.show();
		//惰性初始化数据访问
		if(this.dao == null){
			this.dao = new PaperDao(this.mainActivity);
		}
		//加载数据
		new LoadDataTask().execute();
	}
	/*
	 * 数据行选中事件处理
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.d(TAG, "选中行:" + position);
		if(this.dataSource.size() >  position && this.mainActivity != null){
			//初始化试卷列表
			MainHomePaperFragment fragment = new MainHomePaperFragment(this.mainActivity, this.dataSource.get(position));
			//UI切换
			this.mainActivity.getSupportFragmentManager().beginTransaction()
			.addToBackStack(null)
			.replace(R.id.main_fragment_replace, fragment)
			.commit();
		}
	}
	/**
	 * 加载数据任务。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月1日
	 */
	private class LoadDataTask extends AsyncTask<Void, Void, String>{
		/*
		 * 后台线程加载数据.
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected String doInBackground(Void... params) {
			Log.d(TAG, "后台线程加载数据...");
			//加载产品名称
			String productName = null;
			if(mainActivity != null){
				AppContext appContext = (AppContext)mainActivity.getApplication();
				if(appContext != null && appContext.getCurrentSettings() != null){
					productName = appContext.getCurrentSettings().getProductName();
				}
			}
			//加载科目试卷统计
			dataSource.clear();
			List<PaperDao.SubjectTotalModel> list = dao.totalSubjects();
			if(list != null && list.size() > 0){
				dataSource.addAll(list);
			}
			//
			return productName;
		}
		/*
		 * 前台线程数据处理。
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(String result) {
			Log.d(TAG, "前台线程处理...");
			//关闭等待
			mainActivity.waitingViewDialog.cancel();
			//
			//1.产品名称
			tvTitle.setText(result);
			//2.通知适配器更新数据
			adapter.notifyDataSetChanged();
		}
	}
	/**
	 * 主页数据适配器。
	 * @author jeasonyoung
	 * @since 2015年7月1日
	 */
	private class MainHomeAdapter extends BaseAdapter{
		private Context context;
		private List<PaperDao.SubjectTotalModel> dataSource;
		/**
		 * 初始化。
		 * @param context
		 * @param dataSource
		 */
		public MainHomeAdapter(Context context, List<PaperDao.SubjectTotalModel> dataSource){
			Log.d(TAG, "初始化数据适配器...");
			this.context = context;
			this.dataSource = dataSource;
		}
		/*
		 * 获取数据总数。
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount() {
			return this.dataSource.size();
		}
		/*
		 * 获取数据模型对象。
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public Object getItem(int position) {
			return this.dataSource.get(position);
		}
		/*
		 * 获取索引。
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId(int position) {
			return position;
		}
		/*
		 * 创建行
		 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.d(TAG, "创建数据行..." + position);
			ItemViewWrapper wrapper = null;
			if(convertView == null){
				Log.d(TAG, "创建新行..." + position);
				//初始化
				wrapper = new ItemViewWrapper();
				//加载行布局
				convertView = LayoutInflater.from(this.context).inflate(R.layout.ui_main_home_item, parent, false);
				//1.科目名称
				wrapper.setTitle((TextView)convertView.findViewById(R.id.home_item_subjectname));
				//2.试卷总数
				wrapper.setTotal((TextView)convertView.findViewById(R.id.home_item_papers));
				//
				convertView.setTag(wrapper);
			}else {
				Log.d(TAG, "复用行..." + position);
				wrapper = (ItemViewWrapper)convertView.getTag();
			}
			//加载数据.
			Log.d(TAG, "装载行数据.. " +  position);
			PaperDao.SubjectTotalModel  model = (PaperDao.SubjectTotalModel)this.getItem(position);
			if(model != null){
				//1.科目名称
				wrapper.getTitle().setText(model.getName());
				//2.试卷总数
				wrapper.getTotal().setText(String.valueOf(model.getTotal()));
			}
			return convertView;
		}
		/**
		 * 行视图包装。
		 * 
		 * @author jeasonyoung
		 * @since 2015年7月1日
		 */
		private class ItemViewWrapper{
			private TextView title,total;
			/**
			 * 获取科目名称。
			 * @return 科目名称。
			 */
			public TextView getTitle() {
				return title;
			}
			/**
			 * 设置科目名称。
			 * @param title 
			 *	  科目名称。
			 */
			public void setTitle(TextView title) {
				this.title = title;
			}
			/**
			 * 获取试卷总数。
			 * @return 试卷总数。
			 */
			public TextView getTotal() {
				return total;
			}
			/**
			 * 设置试卷总数。
			 * @param total 
			 *	  试卷总数。
			 */
			public void setTotal(TextView total) {
				this.total = total;
			}
		}
	}
}