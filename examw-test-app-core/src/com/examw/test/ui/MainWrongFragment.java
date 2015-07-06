package com.examw.test.ui;

import java.util.ArrayList;
import java.util.List;

import com.examw.test.R;
import com.examw.test.dao.PaperDao;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 错题Fragment
 * 
 * @author jeasonyoung
 * @since 2015年7月4日
 */
public class MainWrongFragment extends Fragment implements RadioGroup.OnCheckedChangeListener,AdapterView.OnItemClickListener {
	private static final String TAG = "MainWrongFragment";
	private final MainActivity mainActivity;
	private final PaperDao dao;
	
	private WrongOption option = WrongOption.Wrong;
	private List<PaperDao.SubjectTotalModel> dataSource;
	private WrongAdapter adapter;
	private ListView listView;
	/**
	 * 构造函数。
	 * @param mainActivity
	 */
	public MainWrongFragment(final MainActivity mainActivity){
		Log.d(TAG, "初始化...");
		this.mainActivity = mainActivity;
		this.dataSource = new ArrayList<PaperDao.SubjectTotalModel>();
		this.adapter = new WrongAdapter(this.mainActivity, this.dataSource);
		this.dao = new PaperDao(this.mainActivity);
	}
	/*
	 * 加载UI。
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "加载UI布局...");
		//加载错题UI布局
		final View view = inflater.inflate(R.layout.ui_main_wrong, container, false);
		//标题选项
		final RadioGroup options = (RadioGroup)view.findViewById(R.id.main_wrong_options);
		options.setOnCheckedChangeListener(this);
		//数据列表
		this.listView = (ListView)view.findViewById(R.id.list_main_wrong);
		//设置列表数据源
		this.listView.setAdapter(this.adapter);
		//设置行点击事件处理
		this.listView.setOnItemClickListener(this);
		//返回视图
		return view;
	}
	/*
	 * 加载数据处理。
	 * @see android.support.v4.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
		super.onStart();
		//启动等待动画
		this.mainActivity.waitingViewDialog.show();
		//加载数据处理
		new AsynLoadTask().execute();
	}
	/*
	 * 标题选中处理
	 * @see android.widget.RadioGroup.OnCheckedChangeListener#onCheckedChanged(android.widget.RadioGroup, int)
	 */
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		Log.d(TAG, "标题选项处理:" + checkedId);
		//启动等待动画
		this.mainActivity.waitingViewDialog.show();
		//选项转换		
		switch(checkedId){
			case R.id.main_wrong_options_favorite:{//收藏
				this.option = WrongOption.Favorite;
				break;
			}
			case R.id.main_wrong_options_wrong:
			default:
			{
				this.option = WrongOption.Wrong;
			}
		}
		//加载数据处理
		new AsynLoadTask().execute();
	}
	/*
	 * 数据项选中事件处理
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.d(TAG, "选中行..." + position);
		if(this.dataSource.size() > position){
			PaperDao.SubjectTotalModel data = this.dataSource.get(position);
			if(data != null){
				Toast.makeText(this.mainActivity, data.toString(), Toast.LENGTH_SHORT).show();
			}
		}
	}
	//选项枚举
	private enum WrongOption { Wrong, Favorite };
	/**
	 * 异步线程加载数据。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月6日
	 */
	private class AsynLoadTask extends AsyncTask<Void, Void, Void>{
		/*
		 * 台线程数据处理。
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Void doInBackground(Void... params) {
			Log.d(TAG, "开始后台线程数据处理...");
			//清空数据源
			dataSource.clear();
			//加载数据
			switch(option){
				case Wrong:{//错题数据
					List<PaperDao.SubjectTotalModel> list = dao.totalSubjectWrongRecords();
					if(list != null && list.size() > 0){
						dataSource.addAll(list);
					}
					break;
				}
				case Favorite:{//收藏数据
					List<PaperDao.SubjectTotalModel> list = dao.totalFavoriteRecords();
					if(list != null && list.size() > 0){
						dataSource.addAll(list);
					}
					break;
				}
			}
			return null;
		}
		/*
		 * 主线程UI处理。
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Void result) {
			Log.d(TAG, "主线程UI处理...");
			//数据源更新
			adapter.notifyDataSetChanged();
			//关闭等待动画
			mainActivity.waitingViewDialog.cancel();
		};
	}
	/**
	 * 错题数据适配器。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月6日
	 */
	private class WrongAdapter extends BaseAdapter{
		private final Context context;
		private final List<PaperDao.SubjectTotalModel> list;
		/**
		 * 构造函数。
		 * @param context
		 * 上下文。
		 * @param list
		 * 列表数据源。
		 */
		public WrongAdapter(final Context context,final List<PaperDao.SubjectTotalModel> list){
			this.context = context;
			this.list = list;
		}
		/*
		 * 获取数据行数。
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount() {
			return this.list.size();
		}
		/*
		 * 获取行数据对象。
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public Object getItem(int position) {
			if(this.list.size() > position){
				return this.list.get(position);
			}
			return null;
		}
		/*
		 * 获取行ID。
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId(int position) {
			return position;
		}
		/*
		 * 创建行。
		 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.d(TAG, "创建行..." + position);
			ItemViewWrapper wrapper = null;
			if(convertView == null){
				Log.d(TAG, "新建行..." + position);
				//0.加载列表布局
				convertView = LayoutInflater.from(this.context).inflate(R.layout.ui_main_wrong_item, parent, false);
				wrapper = new ItemViewWrapper();
				//1.科目
				wrapper.setSubject((TextView)convertView.findViewById(R.id.wrong_item_subjectname));
				//2.试题数
				wrapper.setTotals((TextView)convertView.findViewById(R.id.wrong_item_totals));
				//保存
				convertView.setTag(wrapper);
			}else {
				Log.d(TAG, "重用行..." + position);
				//加载ui 
				wrapper = (ItemViewWrapper)convertView.getTag();
			}
			//加载数据
			PaperDao.SubjectTotalModel data = (PaperDao.SubjectTotalModel)this.getItem(position);
			if(data != null && wrapper != null){
				//科目
				wrapper.getSubject().setText(data.getName());
				//试题数
				wrapper.getTotals().setText("("+data.getTotal()+")");
			}
			return convertView;
		}
		/**
		 * 行视图包装器。
		 * 
		 * @author jeasonyoung
		 * @since 2015年7月6日
		 */
		private class ItemViewWrapper{
			private TextView subject,totals;
			/**
			 * 获取科目名称。
			 * @return 科目名称。
			 */
			public TextView getSubject() {
				return subject;
			}
			/**
			 * 设置科目名称。
			 * @param subject 
			 *	  科目名称。
			 */
			public void setSubject(TextView subject) {
				this.subject = subject;
			}
			/**
			 * 获取试题数。
			 * @return 试题数。
			 */
			public TextView getTotals() {
				return totals;
			}
			/**
			 * 设置试题数。
			 * @param totals 
			 *	  试题数。
			 */
			public void setTotals(TextView totals) {
				this.totals = totals;
			}
		}
	}
}