package com.examw.test.adapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.examw.test.R;
import com.examw.test.dao.PaperDao;
import com.examw.test.dao.PaperDao.PaperInfoModel;
import com.examw.test.model.PaperModel.PaperType;
import com.examw.test.ui.PaperInfoActivity;
import com.examw.test.widget.TitleProvider;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * 试卷类型数据适配器。
 * 
 * @author jeasonyoung
 * @since 2015年8月3日
 */
public class HomePaperTypeAdapter extends BaseAdapter implements TitleProvider{
	private static final String TAG = "HomePaperTypeAdapter";
	private final Context context;
	private final LayoutInflater mInflater;
	private final List<PaperType> dataSources;
	private final String subjectCode;
	/**
	 * 构造函数。
	 * @param context
	 * @param dataSources
	 */
	public HomePaperTypeAdapter(Context context, String subjectCode, List<PaperType> dataSources){
		Log.d(TAG, "初始化...");
		this.context = context;
		this.subjectCode = subjectCode;
		this.mInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.dataSources = dataSources;
	}
	/*
	 * 获取数据量。
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return (this.dataSources == null) ? 0 : this.dataSources.size();
	}
	/*
	 * 获取行数据对象。
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		return (this.getCount() > position) ? this.dataSources.get(position) : null;
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
	 * 获取行名称。
	 * @see com.examw.test.widget.TitleProvider#getTitle(int)
	 */
	@Override
	public String getTitle(int position) {
		Log.d(TAG, "加载行["+position+"]名称...");
		final PaperType type = (PaperType)this.getItem(position);
		if(type != null){
			return type.getName();
		}
		return null;
	}
	/*
	 * 创建行。
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d(TAG, "创建数据行..." + position);
		ViewHolder viewHolder = null;
		if(convertView == null){
			Log.d(TAG, "新建行..." + position);
			//加载布局
			convertView = this.mInflater.inflate(R.layout.ui_main_home_paper_viewpager, parent, false);
			//初始化
			viewHolder = new ViewHolder(convertView);
			//缓存
			convertView.setTag(viewHolder);
		}else {
			Log.d(TAG, "重建行..." + position);
			viewHolder = (ViewHolder)convertView.getTag();
		}
		//加载数据
		viewHolder.loadData(this.getItem(position));
		//返回
		return convertView;
	}
	//
	private class ViewHolder implements PullToRefreshListView.OnRefreshListener<ListView>, AdapterView.OnItemClickListener{
		public final PullToRefreshListView refreshListView;
		public final PaperListAdapter listAdapter;
		public final List<PaperInfoModel> list;
		public PaperDao dao;
		private PaperType type;
		private int index = 0;
		/**
		 * 构造函数。
		 * @param convertView
		 */
		public ViewHolder(View convertView){
			//加载列表
			this.refreshListView = (PullToRefreshListView)convertView.findViewById(R.id.list_home_papers);
			//设置刷新方向
			this.refreshListView.setMode(PullToRefreshListView.Mode.PULL_FROM_END);
			//设置刷新监听器
			this.refreshListView.setOnRefreshListener(this);
			//设置数据行监听器
			this.refreshListView.setOnItemClickListener(this);
			//初始化数据源
			this.list = new ArrayList<PaperInfoModel>();
			//初始化数据适配器
			this.listAdapter = new PaperListAdapter(context, this.list);
			//设置列表数据适配器
			final ListView listView = this.refreshListView.getRefreshableView();
			if(listView != null){
				listView.setAdapter(this.listAdapter);
			}
		}
		/**
		 * 加载数据。
		 * @param data
		 */
		public void loadData(Object data){
			Log.d(TAG, "加载行数据...");
			this.type = (PaperType)data;
			if(this.type == null) return;
			//清空数据源
			if(this.list.size() > 0) this.list.clear();
			//重载页码
			this.index = 0;
			//异步加载数据
			new LoadPaperListAsyncTask(this).execute(subjectCode, this.type, this.index);
		}
		/*
		 * 刷新加载数据。
		 * @see com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener#onRefresh(com.handmark.pulltorefresh.library.PullToRefreshBase)
		 */
		@Override
		public void onRefresh(PullToRefreshBase<ListView> arg) {
			if(this.type == null)return;
			//2.刷新数据
			this.index += 1;
			Log.d(TAG, "刷新数据页..." + this.index);
			//3.异步加载数据
			new LoadPaperListAsyncTask(this).execute(subjectCode, this.type, this.index);
		}
		/*
		 * 列表数据点击事件处理。
		 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
		 */
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Log.d(TAG, "列表数据[" + position+ "]点击事件处理...");
			Log.d(TAG, "选中数据行..." + position);
			if(this.list.size() > position - 1){
				PaperDao.PaperInfoModel data = this.list.get(position - 1);
				if(data != null){
					//初始化意图
					Intent intent = new Intent(context, PaperInfoActivity.class);
					intent.putExtra(PaperInfoActivity.INTENT_PAPERID_KEY, data.getId());
					intent.putExtra(PaperInfoActivity.INTENT_SUBJECTNAME_KEY, data.getSubjectName());
					//开启
					context.startActivity(intent);
				}
			}
		}
	}
	//异步加载试卷列表数据。
	private class LoadPaperListAsyncTask extends AsyncTask<Object, Void, PaperInfoModel[]>{
		private final WeakReference<ViewHolder> refViewHolder;
		/**
		 * 构造函数。
		 * @param refreshListView
		 */
		public LoadPaperListAsyncTask(ViewHolder viewHolder){
			this.refViewHolder = new WeakReference<ViewHolder>(viewHolder);
		}
		/*
		 * 异步加载数据。
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected PaperInfoModel[] doInBackground(Object... params) {
			try {
				Log.d(TAG, "开始异步加载试卷列表数据...");
				//科目
				final String subjectCode = (String)params[0];
				//试卷类型
				final PaperType paperType = (PaperType)params[1];
				//页码
				final int index = Math.max((Integer)params[2], 0);
				//上下文
				final ViewHolder vh = this.refViewHolder.get();
				if(vh == null) return null;
				//惰性初始化
				if(vh.dao == null){
					vh.dao = new PaperDao(context);
				}
				//查询数据
				List<PaperInfoModel> list = vh.dao.findPaperInfos(subjectCode, paperType, index);
				if(list != null && list.size() > 0){
					Log.d(TAG, "加载数据行数:" + list.size());
					return list.toArray(new PaperDao.PaperInfoModel[0]);
				}
			} catch (Exception e) {
				Log.e(TAG, "加载异步试卷列表数据异常:" + e.getMessage(), e);
			}
			return null;
		}
		/*
		 * 主线程数据处理。
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(PaperInfoModel[] result) {
			Log.d(TAG, "异步加载数据完成...");
			final ViewHolder holder = this.refViewHolder.get();
			if(holder == null)return;
			if(result != null && result.length > 0){
				//添加数据 
				holder.list.addAll(Arrays.asList(result)); 
				//通知数据适配器更新
				holder.listAdapter.notifyDataSetChanged();
			}else if(holder.index > 0){
				holder.index -= 1;
			}
			//加载数据完毕
			holder.refreshListView.onRefreshComplete();
		}
	}
}