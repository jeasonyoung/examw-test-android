package com.examw.test.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.examw.test.R;
import com.examw.test.dao.PaperDao.PaperInfoModel;

/**
 * 试卷列表数据适配器。
 * 
 * @author jeasonyoung
 * @since 2015年7月3日
 */
public class PaperListAdapter extends BaseAdapter{
	private static final String TAG = "PaperListAdapter";
	private final LayoutInflater mInflater;
	private final List<PaperInfoModel> list;
	/**
	 * 构造函数。
	 * @param context
	 * @param list
	 */
	public PaperListAdapter(Context context, List<PaperInfoModel> list){
		Log.d(TAG, "初始化数据适配器...");
		this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		ViewHolder holder = null;
		if(convertView == null){
			Log.d(TAG, "新建行..." + position);
			//1.加载布局
			convertView = this.mInflater.inflate(R.layout.ui_main_home_paper_viewpager_item, parent, false);
			//2.初始化
			holder = new ViewHolder(convertView);
			//3.缓存
			convertView.setTag(holder);
		}else {
			Log.d(TAG, "重用行..." + position);
			holder = (ViewHolder)convertView.getTag();
		}
		//加载数据
		holder.loadData(this.getItem(position));
		//返回
		return convertView;
	}
	/**
	 * 行视图包装
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月3日
	 */
	private class ViewHolder{
		private TextView titleView,subjectView,totalView,timeView;
		/**
		 * 构造函数。
		 * @param convertView
		 */
		public ViewHolder(View convertView){
			//1.试卷名称
			this.titleView = (TextView)convertView.findViewById(R.id.home_paper_name);
			//2.所属科目
			this.subjectView = (TextView)convertView.findViewById(R.id.home_paper_subjectname);
			//3.试题数
			this.totalView = (TextView)convertView.findViewById(R.id.home_paper_items);
			//4.发布时间
			this.timeView = (TextView)convertView.findViewById(R.id.home_paper_createTime);
		}
		/**
		 * 加载数据。
		 * @param data
		 */
		public void loadData(Object data){
			if(data instanceof PaperInfoModel){
				//1.试卷名称
				if(this.titleView != null){
					this.titleView.setText(((PaperInfoModel)data).getTitle());
				}
				//2.所属科目
				if(this.subjectView != null){
					this.subjectView.setText("科目:" + ((PaperInfoModel)data).getSubjectName());
				}
				//3.试题数
				if(this.totalView != null){
					this.totalView.setText("科目:" + ((PaperInfoModel)data).getTotal());
				}
				//4.发布时间
				if(this.timeView != null){
					this.timeView.setText(((PaperInfoModel)data).getCreateTime());
				}
			}
		}		
	}
}