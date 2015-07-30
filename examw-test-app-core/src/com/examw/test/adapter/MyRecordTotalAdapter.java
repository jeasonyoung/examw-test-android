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
import com.examw.test.dao.PaperDao.SubjectTotalModel;

/**
 * 做题记录数据适配器。
 * 
 * @author jeasonyoung
 * @since 2015年7月6日
 */
public class MyRecordTotalAdapter extends BaseAdapter{
	private static final String TAG = "MyRecordAdapter";
	private final LayoutInflater mInflater;
	private final List<SubjectTotalModel> list;
	/**
	 * 构造函数。
	 * @param context
	 * 上下文。
	 * @param list
	 * 列表数据源。
	 */
	public MyRecordTotalAdapter(Context context, List<SubjectTotalModel> list){
		Log.d(TAG, "初始化数据适配器...");
		this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.list = list;
	}
	/*
	 * 获取行数据.
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return (this.list == null) ? 0 : this.list.size();
	}
	/*
	 * 获取行数据对象。
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		return (this.list == null) ?  null : this.list.get(position);
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
			//0.加载行布局
			convertView = this.mInflater.inflate(R.layout.ui_main_my_item, parent, false);
			//1.初始化
			holder = new ViewHolder(convertView);
			//2.缓存
			convertView.setTag(holder);
		}else {
			Log.d(TAG, "复用行..." + position);
			holder = (ViewHolder)convertView.getTag();
		}
		//加载数据
		holder.loadData(this.getItem(position));
		//返回
		return convertView;
	}
	//行视图包装类。
	private class ViewHolder{
		private TextView subjectView,totalsView;
		/**
		 * 构造函数。
		 * @param convertView
		 */
		public ViewHolder(View convertView){
			//1.科目
			this.subjectView = (TextView)convertView.findViewById(R.id.my_item_subjectname);
			//2.试题数
			this.totalsView = (TextView)convertView.findViewById(R.id.my_item_totals);
		}
		/**
		 * 加载数据。
		 * @param data
		 */
		public void loadData(Object data){
			if(data instanceof SubjectTotalModel){
				//1.科目
				if(this.subjectView != null){
					this.subjectView.setText(((SubjectTotalModel)data).getName());
				}
				//2.试题数
				if(this.totalsView != null){
					this.totalsView.setText(String.valueOf(((SubjectTotalModel)data).getTotal()));
				}
			}
		}
	}
}