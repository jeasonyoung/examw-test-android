package com.examw.test.adapter;

import java.util.List;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.examw.test.R;
import com.examw.test.model.PaperItemModel;
import com.examw.test.ui.PaperActivity;

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
		return this.list.size();
	}
	/*
	 * 获取试题对象。
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		return this.list.get(position);
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
		return this.drawItemView(position, convertView);
	}
	//创建试题View
	private View drawItemView(int position, View convertView){
		Log.d(TAG, "创建试题..." + position);
		ItemViewHolder itemViewHolder = null;
		if(convertView == null){
			Log.d(TAG, "新建试卷试题..." + position);
			//加载布局文件
			convertView = LayoutInflater.from(this.activity).inflate(R.layout.ui_main_paper_items, null, false);
			//初始化
			itemViewHolder = new ItemViewHolder();
			//加载列表数据
			itemViewHolder.itemsView = (ListView)convertView.findViewById(R.id.list_paper_item);
			//初始化列表数据适配器
			itemViewHolder.itemsAdapter = new PaperItemsAdapter(this.activity);
			//设置试题列表数据适配器
			itemViewHolder.itemsView.setAdapter(itemViewHolder.itemsAdapter);
			//保存
			convertView.setTag(itemViewHolder);
		}else {
			Log.d(TAG, "重用试卷试题..." + position);
			itemViewHolder = (ItemViewHolder)convertView.getTag();
		}
		//加载数据
		itemViewHolder.itemsAdapter.loadItemModel(position, this.list.get(position), this.activity.isDisplayAnswer());
		//添加切换动画
//		if(position > this.activity.getCurrentItemOrder()){//下一题动画
//			convertView.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.paper_item_next));
//		}else {//上一题动画
//			convertView.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.paper_item_prev));
//		}
		//返回View
		return convertView;
	}
	//
 	private class ItemViewHolder{
 		ListView itemsView;
 		PaperItemsAdapter itemsAdapter;
	}
}