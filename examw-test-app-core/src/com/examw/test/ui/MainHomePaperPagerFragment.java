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
import android.widget.Toast;

import com.examw.test.R;
import com.examw.test.dao.PaperDao;
import com.examw.test.model.PaperModel;
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
	private final PaperAdapter adapter;
	private PullToRefreshListView listView;
	
	private int pageIndex;
	/**
	 * 构造函数。
	 * @param mainActivity
	 * @param subject
	 * @param type
	 */
	public MainHomePaperPagerFragment(final MainActivity mainActivity,
			final PaperDao.SubjectTotalModel subject, final PaperModel.PaperType type){
		Log.d(TAG, "初始化－" + type);
		this.mainActivity =  mainActivity;
		this.subject = subject;
		this.type = type;
		this.dataSource = new ArrayList<PaperDao.PaperInfoModel>();
		this.adapter = new PaperAdapter(this.mainActivity, this.dataSource);
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
		this.listView = (PullToRefreshListView)view.findViewById(R.id.list_home_papers);
		//设置数据适配器
		this.listView.setAdapter(this.adapter);
		//设置刷新方向
		this.listView.setMode(PullToRefreshListView.Mode.PULL_FROM_END);
		//设置刷新监听器
		this.listView.setOnRefreshListener(this);
		//设置数据行监听器
		this.listView.setOnItemClickListener(this);
		//返回UI。
		return view;
	}
	/*
	 * 重载开始。
	 * @see android.support.v4.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
		super.onStart();
		//1.开始等待动画
		this.mainActivity.waitingViewDialog.show();
		//2.加载数据
		this.pageIndex = 0;
		new RefreshDataTask(null).execute();
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
		new RefreshDataTask(arg).execute();
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
			
			Toast.makeText(this.mainActivity, "选中行["+position+"]数据:" + data, Toast.LENGTH_SHORT).show();
		}
	}
	/**
	 * 刷新加载数据。
	 * @author jeasonyoung
	 * @since 2015年7月3日
	 */
	private class RefreshDataTask extends AsyncTask<Void, Void, Void>{
		private final PullToRefreshBase<?> pullToRefresh;
		/**
		 * 构造函数。
		 * @param pullToRefresh
		 */
		public RefreshDataTask(PullToRefreshBase<?> pullToRefresh){
			this.pullToRefresh = pullToRefresh;
		}
		/*
		 * 后台线程加载数据。
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Void doInBackground(Void... params) {
			Log.d(TAG, "后台线程加载数据页..." + pageIndex + "-" + subject.getCode() + "-" + type);
			 try {
				//查询数据
				PaperDao paperDao = new PaperDao(mainActivity);
				List<PaperDao.PaperInfoModel> list = paperDao.findPaperInfos(subject.getCode(), type, pageIndex);
				if(list != null && list.size() > 0){
					Log.d(TAG, "加载数据行数:" + list.size());
					//添加到数据源
					dataSource.addAll(list);
				}else if(pageIndex > 0) {
					Log.d(TAG, "没有加载到数据....");
					pageIndex -= 1;
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
		protected void onPostExecute(Void result) {
			Log.d(TAG, "前台线程更新UI...");
			//0.关闭等待动画
			mainActivity.waitingViewDialog.cancel();
			//1.通知适配器更新
			adapter.notifyDataSetChanged();
			//2.刷新完成
			if(this.pullToRefresh != null){
				pullToRefresh.onRefreshComplete();
			}
		}
	}
	
	/**
	 * 试卷列表数据适配器。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月3日
	 */
	private class PaperAdapter extends BaseAdapter{
		private Context context;
		private List<PaperDao.PaperInfoModel> list;
		/**
		 * 构造函数。
		 * @param context
		 * @param list
		 */
		public PaperAdapter(Context context, List<PaperDao.PaperInfoModel> list){
			Log.d(TAG, "初始化数据适配器...");
			this.context = context;
			this.list = list;
		}
		/*
		 * 重载获取总行数。
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount() {
			return this.list.size();
		}
		/*
		 * 重载获取指定行数据。
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public Object getItem(int position) {
			return this.list.get(position);
		}
		/*
		 * 重载。
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId(int position) {
			return position;
		}
		/*
		 * 获取行
		 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.d(TAG, "创建行..." + position);
			ItemViewWrapper wrapper = null;
			if(convertView == null){
				Log.d(TAG, "新建行..." + position);
				//0.新建包装类
				wrapper = new ItemViewWrapper();
				//1.加载布局
				convertView = LayoutInflater.from(this.context).inflate(R.layout.ui_main_home_paper_viewpager_item, parent, false);
				//2.试卷名称
				wrapper.setTitle((TextView)convertView.findViewById(R.id.home_paper_name));
				//3.所属科目
				wrapper.setSubject((TextView)convertView.findViewById(R.id.home_paper_subjectname));
				//4.试题数
				wrapper.setTotal((TextView)convertView.findViewById(R.id.home_paper_items));
				//5.发布时间
				wrapper.setTime((TextView)convertView.findViewById(R.id.home_paper_createTime));
				//保存
				convertView.setTag(wrapper);
			}else {
				Log.d(TAG, "重用行..." + position);
				wrapper = (ItemViewWrapper)convertView.getTag();
			}
			//加载数据
			PaperDao.PaperInfoModel data = (PaperDao.PaperInfoModel)this.getItem(position);
			//1.试卷名称
			wrapper.getTitle().setText(data.getTitle());
			//2.所属科目
			wrapper.getSubject().setText("科目:" + data.getSubjectName());
			//3.试题数
			wrapper.getTotal().setText("试题:" + data.getTotal());
			//4.发布时间
			wrapper.getTime().setText(data.getCreateTime());
			//
			return convertView;
		}
		/**
		 * 行视图包装
		 * 
		 * @author jeasonyoung
		 * @since 2015年7月3日
		 */
		private class ItemViewWrapper{
			private TextView title,subject,total,time;
			/**
			 * 获取标题。
			 * @return 标题。
			 */
			public TextView getTitle() {
				return title;
			}
			/**
			 * 设置标题。
			 * @param title 
			 *	  标题。
			 */
			public void setTitle(TextView title) {
				this.title = title;
			}
			/**
			 * 获取科目。
			 * @return 科目。
			 */
			public TextView getSubject() {
				return subject;
			}
			/**
			 * 设置科目。
			 * @param subject 
			 *	  科目。
			 */
			public void setSubject(TextView subject) {
				this.subject = subject;
			}
			/**
			 * 获取试题数。
			 * @return 试题数。
			 */
			public TextView getTotal() {
				return total;
			}
			/**
			 * 设置试题数。
			 * @param total 
			 *	  试题数。
			 */
			public void setTotal(TextView total) {
				this.total = total;
			}
			/**
			 * 获取发布时间。
			 * @return 发布时间。
			 */
			public TextView getTime() {
				return time;
			}
			/**
			 * 设置发布时间。
			 * @param time 
			 *	  发布时间。
			 */
			public void setTime(TextView time) {
				this.time = time;
			}
		}
	}
}