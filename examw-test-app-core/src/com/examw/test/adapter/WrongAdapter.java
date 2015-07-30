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
import com.examw.test.dao.PaperDao;

/**
 * 错题数据适配器。
 * 
 * @author jeasonyoung
 * @since 2015年7月6日
 */
public class WrongAdapter extends BaseAdapter{
	private static final String TAG = "WrongAdapter";
	private final List<PaperDao.SubjectTotalModel> list;
	private final LayoutInflater mInflater;
	/**
	 * 构造函数。
	 * @param context
	 * 上下文。
	 * @param list
	 * 列表数据源。
	 */
	public WrongAdapter(Context context, List<PaperDao.SubjectTotalModel> list){
		Log.d(TAG, "初始化...");
		this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		ViewHolder holder = null;
		if(convertView == null){
			Log.d(TAG, "新建行..." + position);
			//0.加载列表布局
			convertView = this.mInflater.inflate(R.layout.ui_main_wrong_item, parent, false);
			holder = new ViewHolder(convertView);
			//保存
			convertView.setTag(holder);
		}else {
			Log.d(TAG, "重用行..." + position);
			//加载ui 
			holder = (ViewHolder)convertView.getTag();
		}
		//加载数据
		 holder.loadData(this.getItem(position));
		//返回
		return convertView;
	}
	/**
	 * 行视图包装器。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月6日
	 */
	private class ViewHolder{
		private TextView subjectView,totalsView;
		/**
		 * 构造函数。
		 * @param convertView
		 */
		public ViewHolder(View convertView){
			//1.科目
			this.subjectView = (TextView)convertView.findViewById(R.id.wrong_item_subjectname);
			//2.试题数
			this.totalsView = (TextView)convertView.findViewById(R.id.wrong_item_totals);
		}
		/**
		 * 加载数据。
		 * @param data
		 */
		public void loadData(Object data){
			PaperDao.SubjectTotalModel model = (PaperDao.SubjectTotalModel)data;
			if(model == null) return;
			//设置科目
			if(this.subjectView != null){
				this.subjectView.setText(model.getName());
			}
			//设置试题数
			if(this.totalsView != null){
				this.totalsView.setText("("+model.getTotal()+")");
			}
		}
	}
}