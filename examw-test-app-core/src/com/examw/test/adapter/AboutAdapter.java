package com.examw.test.adapter;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.examw.test.R;

/**
 * 关于的数据适配器。
 * 
 * @author jeasonyoung
 * @since 2015年7月16日
 */
public class AboutAdapter extends BaseAdapter{
	private static final String TAG = "AboutAdapter";
	private final List<String> list;
	private final LayoutInflater mInflater;
	/**
	 * 构造函数。
	 * @param context
	 * 上下文。
	 * @param list
	 * 数据源。
	 */
	public AboutAdapter(Context context, List<String> list){
		Log.d(TAG, "初始化...");
		this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.list = list;
	}
	/*
	 * 获取数据量。
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return this.list.size();
	}
	/*
	 * 获取行数据。
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		return this.list.get(position);
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
	 * 行视图类型数。
	 * @see android.widget.BaseAdapter#getViewTypeCount()
	 */
	@Override
	public int getViewTypeCount() {
		return ItemType.values().length;
	}
	/*
	 * 获取数据行视图类型。
	 * @see android.widget.BaseAdapter#getItemViewType(int)
	 */
	@Override
	public int getItemViewType(int position) {
		return StringUtils.isBlank((String)this.getItem(position)) ? ItemType.Section.value : ItemType.Item.value;
 	}
	
	/*
	 * 创建数据行。
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d(TAG, "创建行..." + position);
		ViewSectionHolder viewHolder = null;
		if(convertView == null){
			Log.d(TAG, "新建行..." + position);
			switch(ItemType.parse(this.getItemViewType(position))){
				case Section:{//分组
					convertView = this.loadSectionView(this.mInflater, parent);
					viewHolder = this.createSectionHolder(convertView);
					break;
				}
				case Item:{//内容
					convertView = this.loadSectionItemView(this.mInflater, parent);
					viewHolder = this.createSectionItemHolder(convertView);
					break;
				}
			}
			//缓存
			convertView.setTag(viewHolder);
		}else {
			Log.d(TAG, "重用行..." + position);
			viewHolder = (ViewSectionHolder)convertView.getTag();
		}
		//加载数据。
		viewHolder.loadData(this.getItem(position));
		//返回
		return convertView;
	}
	/**
	 * 加载分组View
	 * @param inflater
	 * @param parent
	 * @return
	 */
	protected View loadSectionView(LayoutInflater inflater, ViewGroup parent){
		Log.d(TAG, "加载分组布局...");
		return inflater.inflate(R.layout.ui_main_more_section, parent, false);
	}
	/**
	 * 创建分组ViewHolder
	 * @param convertView
	 * @return
	 */
	protected ViewSectionHolder createSectionHolder(View convertView){
		Log.d(TAG, "创建分组ViewHolder对象...");
		ViewSectionHolder holder = new ViewSectionHolder();
		holder.loadViews(convertView);
		return holder;
	}
	/**
	 * 加载分组内容View
	 * @param inflater
	 * @param parent
	 * @return
	 */
	protected View loadSectionItemView(LayoutInflater inflater, ViewGroup parent){
		Log.d(TAG, "加载分组内容布局...");
		return inflater.inflate(R.layout.ui_main_more_item, parent, false);
	}
	/**
	 * 创建分组内容ViewHolder
	 * @param convertView
	 * @return
	 */
	protected ViewSectionHolder createSectionItemHolder(View convertView){
		Log.d(TAG, "创建分组内容ViewHolder对象...");
		ViewSectionItemHolder holder = new ViewSectionItemHolder();
		holder.loadViews(convertView);
		return holder;
	}
	/**
	 * 分组ViewHolder。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月29日
	 */
	protected class ViewSectionHolder{
		/**
		 * 加载View。
		 * @param view
		 */
		public void loadViews(View view){
			
		}
		/**
		 * 加载数据。
		 * @param data
		 */
		public void loadData(Object data) {
			
		}
	}
	/**
	 * 分组内容ViewHolder
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月29日
	 */
	protected class ViewSectionItemHolder extends ViewSectionHolder{
		protected TextView itemView;
		/*
		 * 重载加载Views。
		 * @see com.examw.test.adapter.AboutAdapter.ViewSectionHolder#loadViews(android.view.View)
		 */
		@Override
		public void loadViews(View view) {
			this.itemView = (TextView)view.findViewById(R.layout.ui_main_more_item);
		}
		/*
		 * 重载加载数据。
		 * @see com.examw.test.ui.AboutAdapter.ViewSectionHolder#loadData(java.lang.String)
		 */
		@Override
		public void loadData(Object data) {
			if(this.itemView != null){
				this.itemView.setText(Html.fromHtml((String)data));
			}
		}
	}
	/**
	 * 类型枚举。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月29日
	 */
	private enum ItemType{ 
		Section(0), Item(1);
		private int value;
		/**
		 * 构造函数。
		 * @param value
		 */
		private ItemType(int value){
			this.value = value;
		}
		/**
		 * 转换。
		 * @param value
		 * @return
		 */
		public static ItemType parse(int value){
			for(ItemType type : ItemType.values()){
				if(type.value == value){
					return type;
				}
			}
			return Section;
		}
	};
}