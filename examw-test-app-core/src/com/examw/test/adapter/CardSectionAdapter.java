package com.examw.test.adapter;
 
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.examw.test.R;
import com.examw.test.adapter.CardSectionItemAdapter.CardItemClickListener;
import com.examw.test.ui.PaperActivity.PaperDataDelegate.AnswerCardItemModel;
import com.examw.test.ui.PaperActivity.PaperDataDelegate.AnswerCardSectionModel;
import com.examw.test.widget.ListGridView;

/**
 * 列表数据适配器。
 * 
 * @author jeasonyoung
 * @since 2015年7月28日
 */
public class CardSectionAdapter extends BaseAdapter{
	private static final String TAG = "CardListViewAdapter";
	private final LayoutInflater mInflater;
	private final List<AnswerCardSectionModel>cardSections;
	private final SparseArray<AnswerCardItemModel[]> cardSectionItems;
	private final boolean displayAnswer;
	private CardItemClickListener onCardItemClickListener;
	/**
	 * 构造函数。
	 * @param context
	 * @param cardSections
	 * @param cardSectionItems
	 */
	public CardSectionAdapter(Context context, List<AnswerCardSectionModel> cardSections, SparseArray<AnswerCardItemModel[]> cardSectionItems, boolean displayAnswer){
		Log.d(TAG, "初始化...");
		this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.cardSections = cardSections;
		this.cardSectionItems = cardSectionItems;
		this.displayAnswer = displayAnswer;
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
	 * 获取分组数据量。
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return this.cardSections == null ? 0 : this.cardSections.size();
	}
	/*
	 * 获取分组数据模型。
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		return (this.cardSections == null || this.cardSections.size() < position) ? null : this.cardSections.get(position);
	}
	//加载数据集合
	private AnswerCardItemModel[] getChilds(int group){
		return (this.cardSectionItems == null) ? null : this.cardSectionItems.get(group);
	}
	/*
	 * 获取分组数据ID。
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}
	/*
	 * 创建分组数据View。
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d(TAG, "创建答题卡分组..." + position);
		ViewSectionHolder viewHolder = null;
		if(convertView == null){
			Log.d(TAG, "新建答题卡分组..." + position);
			//初始化
			viewHolder = new ViewSectionHolder(this.mInflater);
			//加载布局视图
			convertView = this.mInflater.inflate(R.layout.ui_main_paper_card_gridview, parent, false);
			//加载组件
			viewHolder.loadViews(convertView);
			//缓存
			convertView.setTag(viewHolder);
		}else {
			Log.d(TAG, "重用答题卡分组..." + position);
			//加载缓存
			viewHolder = (ViewSectionHolder)convertView.getTag();
		}
		//加载数据
		viewHolder.loadData(this.getItem(position), this.displayAnswer, this.getChilds(position));
		//返回
		return convertView;
	}
	//
	private class ViewSectionHolder{
		private TextView titleView, descView;
		private ListGridView gridView;
		private final List<AnswerCardItemModel> list;
		private final CardSectionItemAdapter itemAdapter;
		/**
		 * 构造函数。
		 */
		public ViewSectionHolder(LayoutInflater mInflater){
			this.list = new ArrayList<AnswerCardItemModel>();
			this.itemAdapter = new CardSectionItemAdapter(mInflater, this.list);
			this.itemAdapter.setOnCardItemClickListener(onCardItemClickListener);
		}
		/**
		 * 加载视图控件。
		 * @param view
		 */
		public void loadViews(View view){
			//加载组件
			if(view == null)return;
			//标题
			this.titleView = (TextView)view.findViewById(R.id.paper_card_title);
			//描述
			this.descView = (TextView)view.findViewById(R.id.paper_card_desc);
			//gridview
			this.gridView = (ListGridView)view.findViewById(R.id.paper_card_gridview);
			this.gridView.setAdapter(this.itemAdapter);
			this.gridView.setOnItemClickListener(this.itemAdapter);
		}
		/**
		 * 加载数据。
		 * @param section
		 * @param display
		 * @param itemModels
		 */
		public void loadData(Object section, boolean display, AnswerCardItemModel[] itemModels){
			//分组数据
			if(section instanceof AnswerCardSectionModel){
				//标题
				if(this.titleView != null){
					this.titleView.setText(((AnswerCardSectionModel)section).getTitle());
				}
				//描述
				if(this.descView != null){
					this.descView.setText(((AnswerCardSectionModel)section).getDesc());
				}
			}
			//分组内容数据处理
			this.itemAdapter.setDisplayAnswer(display);
			//清空数据源
			if(this.list.size() > 0) this.list.clear();
			//装载数据
			if(itemModels != null && itemModels.length > 0){
				this.list.addAll(Arrays.asList(itemModels));
			}
			//通知适配器更新数据
			this.itemAdapter.notifyDataSetChanged();
		}
	}
}