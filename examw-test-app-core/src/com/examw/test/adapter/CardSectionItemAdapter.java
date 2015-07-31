package com.examw.test.adapter;

import java.util.List;

import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.examw.test.R;
import com.examw.test.dao.IPaperItemDataDelegate.AnswerCardItemModel;

/**
 * 答题卡分组内容数据适配器。
 * 
 * @author jeasonyoung
 * @since 2015年7月28日
 */
public class CardSectionItemAdapter extends BaseAdapter implements  AdapterView.OnItemClickListener{
	private static final String TAG = "CardSectionItemAdapter";
	private final LayoutInflater mInflater;
	private final List<AnswerCardItemModel> dataSource;
	private boolean displayAnswer;
	private CardItemClickListener onCardItemClickListener;
	/**
	 * 构造函数。
	 * @param mInflater
	 * @param dataSource
	 */
	public CardSectionItemAdapter(LayoutInflater mInflater,List<AnswerCardItemModel> dataSource){
		Log.d(TAG, "初始化...");
		this.mInflater = mInflater;
		this.dataSource = dataSource;
	}
	/**
	 * 设置是否显示答案。
	 * @param displayAnswer
	 */
	public void setDisplayAnswer(boolean displayAnswer){
		if(this.displayAnswer != displayAnswer){
			this.displayAnswer = displayAnswer;
		}
	}
	/**
	 * 设置答题卡试题点击事件监听器。
	 * @param onCardItemClickListener 
	 *	  答题卡试题点击事件监听器。
	 */
	public void setOnCardItemClickListener(CardItemClickListener onCardItemClickListener) {
		this.onCardItemClickListener = onCardItemClickListener;
	}
	/*
	 * 获取数据量。
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return this.dataSource == null ? 0 : this.dataSource.size();
	}
	/*
	 * 获取数据模型。
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		return (this.dataSource == null || this.dataSource.size() < position) ? null : this.dataSource.get(position);
	}
	/*
	 * 获取数据ID。
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}
	/*
	 * 创建视图。
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d(TAG, "创建答题卡试题..." + position);
		ViewHolder viewHolder = null;
		if(convertView == null){
			Log.d(TAG, "新建答题卡试题..." + position);
			//加载布局文件
			convertView = this.mInflater.inflate(R.layout.ui_main_paper_card_gridview_item, parent, false);
			//初始化
			viewHolder = new ViewHolder(convertView);
			//缓存
			convertView.setTag(viewHolder);
		}else {
			Log.d(TAG, "重用答题卡试题..." + position);
			viewHolder = (ViewHolder)convertView.getTag();
		}
		//加载数据
		viewHolder.loadData(this.displayAnswer, this.getItem(position));
		//返回
		return convertView;
	}
	/*
	 * 点击事件处理。
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.d(TAG, "答题卡点击事件处理...." + position);
		if(this.getCount() > position && this.onCardItemClickListener != null){
			AnswerCardItemModel itemModel = (AnswerCardItemModel)this.getItem(position);
			if(itemModel != null){
				this.onCardItemClickListener.onItemClick(itemModel.getOrder());
			}
		}
	}
	/**
	 * 答题卡选项选中监听器。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月28日
	 */
	public interface CardItemClickListener{
		/**
		 * 题序被选中。
		 * @param order
		 */
		void onItemClick(int order);
	}
	
	//
	private class ViewHolder{
		private TextView itemView;
		/**
		 * 构造函数。
		 * @param view
		 */
		public ViewHolder(View view){
			//加载组件
			if(view == null)return;
			//
			this.itemView = (TextView)view.findViewById(R.id.paper_card_item);
		}
		/**
		 * 加载数据。
		 * @param data
		 */
		@SuppressWarnings("deprecation")
		public void loadData(boolean display, Object data){
			if(this.itemView == null) return;
			//加载数据
			if(data instanceof AnswerCardItemModel){
				//设置内容
				this.itemView.setText(String.valueOf(((AnswerCardItemModel)data).getOrder() + 1));
				//设置颜色
				int colorResId = -1, bgResId = -1;
				switch(((AnswerCardItemModel)data).status){
					case None:{//未做
						colorResId = R.color.card_item_non_textcolor;
						bgResId = R.drawable.card_item_non_bg;
						break;
					}
					case Right:{//做对
						colorResId = display ? R.color.card_item_right_textcolor : R.color.card_item_has_textcolor;
						bgResId = display ? R.drawable.card_item_right_bg : R.drawable.card_item_has_bg;
						break;
					}
					case Wrong:{//做错
						colorResId = display ? R.color.card_item_wrong_textcolor : R.color.card_item_has_textcolor;
						bgResId = display ? R.drawable.card_item_wrong_bg : R.drawable.card_item_has_bg;
						break;
					}
				}
				//加载资源
				if(colorResId != -1 && bgResId != -1){
					final Resources resources = this.itemView.getResources();
					this.itemView.setTextColor(resources.getColor(colorResId));
					this.itemView.setBackgroundDrawable(resources.getDrawable(bgResId));
				}
			}
		}
	}
}