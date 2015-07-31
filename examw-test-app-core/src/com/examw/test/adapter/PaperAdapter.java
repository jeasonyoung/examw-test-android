package com.examw.test.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.examw.test.R;
import com.examw.test.model.PaperItemModel;
import com.examw.test.ui.PaperActivity;
import com.examw.test.widget.ViewFlowInListView;

/**
 *试卷数据适配器。
 * 
 * @author jeasonyoung
 * @since 2015年7月20日
 */
public final class PaperAdapter extends BaseAdapter{
	private static final String TAG = "PaperAdapter";
	private final List<PaperItemModel> list;
	private final PaperActivity activity;
	/**
	 * 构造函数。
	 * @param context
	 */
	public PaperAdapter(PaperActivity activity, List<PaperItemModel> list){
		Log.d(TAG, "初始化试卷数据适配器...");
		this.activity = activity;
		this.list = list;
	}
	/*
	 * 获取试题数。
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return (this.list == null) ? -1 : this.list.size();
	}
	/*
	 * 获取试题对象。
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		return  this.getCount() > position ? this.list.get(position) : null;
	}
	/*
	 * 获取试题默认ID。
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}
	/*
	 * 创建试题
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d(TAG, "加载试题..." + position);
		ViewHolder itemViewHolder = null;
		if(convertView == null){
			Log.d(TAG, "新建试卷试题..." + position);
			//1.加载布局文件
			convertView = LayoutInflater.from(this.activity).inflate(R.layout.ui_main_paper_items, null, false);
			//2.初始化
			itemViewHolder = new ViewHolder(this.activity, convertView);
			//3.缓存
			convertView.setTag(itemViewHolder);
		}else {
			Log.d(TAG, "重用试卷试题..." + position);
			itemViewHolder = (ViewHolder)convertView.getTag();
		}
		//加载数据
		itemViewHolder.loadData(position, this.getItem(position), this.activity.isDisplayAnswer());
		//返回View
		return convertView;
	}
	//
 	private class ViewHolder{
 		private ViewFlowInListView itemsView;
 		private PaperItemsAdapter itemsAdapter;
 		/**
 		 * 构造函数。
 		 * @param context
 		 * @param convertView
 		 */
 		public ViewHolder(Context context, View convertView){
 			//1.加载列表布局
 			this.itemsView = (ViewFlowInListView)convertView.findViewById(R.id.list_paper_item);
 			//2.初始化数据适配器
 			this.itemsAdapter = new PaperItemsAdapter(context);
 			//3.设置列表数据适配器
 			this.itemsView.setAdapter(this.itemsAdapter);
 		}
 		/**
 		 * 加载数据。
 		 * @param itemOrder
 		 * @param data
 		 * @param displayAnswer
 		 */
 		public void loadData(int itemOrder, Object data, boolean displayAnswer){
 			this.itemsAdapter.loadItemModel(itemOrder, (PaperItemModel)data, displayAnswer);
 		}
	}
}