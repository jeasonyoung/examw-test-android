package com.examw.test.adapter;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.examw.test.R;
import com.examw.test.app.AppContext;
import com.examw.test.dao.IPaperItemDataDelegate;
import com.examw.test.model.PaperItemModel;
import com.examw.test.ui.PaperActivity;

/**
 *试卷数据适配器。
 * 
 * @author jeasonyoung
 * @since 2015年7月20日
 */
public final class PaperAdapter extends BaseAdapter implements PaperItemsAdapter.ItemClickListener{
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
			convertView = LayoutInflater.from(this.activity).inflate(R.layout.ui_main_paper_items, parent, false);
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
	/*
	 * 试题选中事件处理。
	 * @see com.examw.test.adapter.PaperItemsAdapter.ItemClickListener#onItemClick(int, java.lang.String)
	 */
	@Override
	public void onItemClick(final int pos, final String myAnswers) {
		Log.d(TAG, "试题[" + (pos + 1) +"]选中...");
		final PaperItemModel itemModel = (PaperItemModel)this.getItem(pos);
		if(itemModel == null) return;
		
		final int waitTime = this.activity.getResources().getInteger(R.integer.paper_item_update_time);
		final long useTimes = (System.currentTimeMillis() - this.activity.getStartTime()) / 1000;
		
		 new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
					Log.d(TAG, "开始线程等待...");
					//更新做题记录 
					updateItemRecord(pos, itemModel, myAnswers, useTimes);
					//线程等待时间
					Thread.sleep(waitTime * 1000);
				} catch (InterruptedException e) {
					Log.e(TAG, "线程等待异常:" + e.getMessage(), e);
				}
				return null;
			}
			@Override
			protected void onPostExecute(Void result) {
				//单选题下一题
				if(itemModel.getType() == PaperItemModel.ItemType.Single.getValue()){
					Log.d(TAG, "跳转到下一题...");
					activity.nextItem();
				}
			}
		}.executeOnExecutor(PaperActivity.pools, (Void[])null);
	}
	//更新做题记录
	private void updateItemRecord(final  int pos, final PaperItemModel itemModel,final String myAnswers, final long useTimes){
		//异步线程保存做题记录
		PaperActivity.pools.execute(new Runnable() {
			@Override
			public void run() {
				try {
					Log.d(TAG, "后台线程保存做题["+pos+"]记录...");
					final IPaperItemDataDelegate dataDelegate = AppContext.getPaperDataDelegate();
					if(dataDelegate != null){	
						dataDelegate.updateRecordAnswer(itemModel, myAnswers, (int)useTimes);
					}
				} catch (Throwable e) {
					Log.e(TAG, "保存做题["+pos+"]记录异常:" + e.getMessage(), e);
				}
			}
		});
	}
	//
 	private class ViewHolder{
 		private ListView itemsView;
 		private PaperItemsAdapter itemsAdapter;
 		/**
 		 * 构造函数。
 		 * @param context
 		 * @param convertView
 		 */
 		public ViewHolder(Context context, View convertView){
 			//1.加载列表布局
 			this.itemsView = (ListView)convertView.findViewById(R.id.list_paper_item);
 			//2.初始化数据适配器
 			this.itemsAdapter = new PaperItemsAdapter(context);
 			//2.设置点击事件监听器
 			this.itemsAdapter.setItemClickListener(PaperAdapter.this);
 			//3.设置列表数据适配器
 			this.itemsView.setAdapter(this.itemsAdapter);
 			//4.设置数据项点击事件监听
 			this.itemsView.setOnItemClickListener(this.itemsAdapter);
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