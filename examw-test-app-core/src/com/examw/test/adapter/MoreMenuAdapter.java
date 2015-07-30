package com.examw.test.adapter;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.examw.test.R;
import com.examw.test.ui.MainMoreFragment.MenuItem;
/**
 * 数据适配器。
 * 
 * @author jeasonyoung
 * @since 2015年7月15日
 */
public class MoreMenuAdapter extends BaseAdapter{
	private static final String TAG = "MoreMenuAdapter";
	private final LayoutInflater mInflater;
	private final Resources resources;
	private final List<MenuItem> list;
	/**
	 * 构造函数。
	 * @param context
	 * @param list
	 */
	public MoreMenuAdapter(Context context, List<MenuItem> list){
		Log.d(TAG, "初始化数据适配器...");
		this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.resources = context.getResources(); 
		this.list = list;
	}
	/*
	 * 获取数据量。
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
		return (this.list == null) ? null : this.list.get(position);
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
	 * 获取View类型数。
	 * @see android.widget.BaseAdapter#getViewTypeCount()
	 */
	@Override
	public int getViewTypeCount() {
		return ItemType.values().length;
	}
	/*
	 * 获取数据行类型。
	 * @see android.widget.BaseAdapter#getItemViewType(int)
	 */
	@Override
	public int getItemViewType(int position) {
		return (this.getItem(position) == null ? ItemType.Section : ItemType.Item).value;
	}
	/*
	 * 创建行。
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d(TAG, "创建行..." + position);
		ViewSectionHolder holder = null;
		if(convertView == null){
			switch(ItemType.parse(this.getItemViewType(position))){
				case Section:{//分组
					Log.d(TAG, "创建分组行..."  + position);
					convertView = this.mInflater.inflate(R.layout.ui_main_more_section, parent, false);
					holder = new ViewSectionHolder();
					break;
				}
				case Item:{//内容
					Log.d(TAG, "创建数据行.." + position);
					convertView = this.mInflater.inflate(R.layout.ui_main_more_item, parent, false);
					holder = new ViewSectionItemHolder();
					break;
				}
			}
			//加载组件
			holder.loadViews(convertView);
			//缓存
			convertView.setTag(holder);
		}else {
			Log.d(TAG, "复用行.." + position);
			holder = (ViewSectionHolder)convertView.getTag();
		}
		//加载数据
		holder.loadData(this.getItem(position));
		//返回
		return convertView;
	}
	//加载图标
	@SuppressWarnings("deprecation")
	private Drawable loadIconDrawable(String icon){
		if(StringUtils.isNotBlank(icon) && this.resources != null){
			Drawable drawable = null;
			if(StringUtils.equalsIgnoreCase(icon, "icon_switch")){//切换产品
				drawable = this.resources.getDrawable(R.drawable.icon_switch);
			}else if(StringUtils.equalsIgnoreCase(icon, "icon_register")){//产品注册
				drawable = this.resources.getDrawable(R.drawable.icon_register);
			}else if(StringUtils.equalsIgnoreCase(icon, "icon_down")){//下载试卷
				drawable = this.resources.getDrawable(R.drawable.icon_down);
			}else if(StringUtils.equalsIgnoreCase(icon, "icon_callback")){//意见反馈
				drawable = this.resources.getDrawable(R.drawable.icon_callback);
			}else {//关于应用
				drawable = this.resources.getDrawable(R.drawable.icon_about);
			}
			if(drawable != null){
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
			}
			return drawable;
		}
		return null;
	}
	/**
	 * 分组。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月30日
	 */
	private class ViewSectionHolder{
		/**
		 * 加载View组件。
		 * @param convertView
		 */
		public void loadViews(View convertView){
			
		}
		/**
		 * 加载数据。
		 * @param data
		 */
		public void loadData(Object data){
			
		}
	}
	/**
	 * 分组内容。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月30日
	 */
	private class ViewSectionItemHolder extends ViewSectionHolder{
		private TextView itemView;
		/*
		 * 重载加载Views组件。
		 * @see com.examw.test.adapter.MoreMenuAdapter.ViewSectionHolder#loadViews(android.view.View)
		 */
		@Override
		public void loadViews(View convertView) {
			this.itemView =  (TextView)convertView.findViewById(R.id.more_item);
		}
		/*
		 * 重载加载数据。
		 * @see com.examw.test.adapter.MoreMenuAdapter.ViewSectionHolder#loadData(java.lang.Object)
		 */
		@Override
		public void loadData(Object data) {
			if((data instanceof MenuItem) && this.itemView != null){
				//图标
				final Drawable iconDrawable = loadIconDrawable(((MenuItem)data).getIcon());
				if(iconDrawable != null){
					this.itemView.setCompoundDrawables(iconDrawable, null, null, null);
					this.itemView.setCompoundDrawablePadding((int)resources.getDimension(R.dimen.img_text_padding));
				}
				//内容
				this.itemView.setText(((MenuItem)data).getName());
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