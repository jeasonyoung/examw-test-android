package com.examw.test.adapter;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.examw.test.R;
import com.examw.test.app.AppContext;
import com.examw.test.dao.IPaperItemDataDelegate;
import com.examw.test.model.PaperItemModel;
import com.examw.test.support.ItemModelSupport;
import com.examw.test.support.ItemModelSupport.PaperItemAnalysisModel;
import com.examw.test.support.ItemModelSupport.PaperItemOptModel;
import com.examw.test.support.ItemModelSupport.PaperItemTitleModel;
import com.examw.test.widget.ItemAnalysisView;
import com.examw.test.widget.ItemOptionView;
import com.examw.test.widget.ItemTitleView;

/**
 * 试卷试题数据适配器。
 * 
 * @author jeasonyoung
 * @since 2015年7月26日
 */
public class PaperItemsAdapter extends BaseAdapter {
	private static final String TAG = "PaperItemsAdapter";
	private static final ConcurrentMap<Integer,PaperItemTitleModel[]> itemCache = new ConcurrentHashMap<Integer, PaperItemTitleModel[]>();
	private final LayoutInflater mInflater;
	private PaperItemTitleModel [] itemModels;
	private boolean displayAnswer;
	/**
	 * 构造函数，
	 * @param context
	 */
	public PaperItemsAdapter(Context context){
		Log.d(TAG, "初始化...");
		this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.itemModels = new PaperItemTitleModel[0];
	}
	//加载试题缓存数据模型
	private synchronized void loadItemCacheModel(int itemOrder){
		final PaperItemTitleModel [] models = itemCache.get(itemOrder);
		if(models != null && models.length > 0){
			Log.d(TAG, "从缓存中加载试题数据[" +itemOrder+ "]...");
			//数据赋值
			this.itemModels = models;
			//刷新数据源
			this.notifyDataSetChanged();
		}
	}
	/**
	 * 加载试题数据。
	 * @param itemOrder
	 * @param itemModel
	 * @param displayAnswer
	 */
	public void loadItemModel(int itemOrder, PaperItemModel itemModel,boolean displayAnswer) {
		Log.d(TAG, "加载试题["+itemOrder+"]数据...");
		if((this.displayAnswer == displayAnswer) && itemCache.containsKey(itemOrder)){
			this.loadItemCacheModel(itemOrder);
			return;
		}
		//设置是否显示答案
		this.displayAnswer = displayAnswer;
		//清空缓存数据
		if(itemCache.size() > 0) itemCache.clear();
		//异步线程构造试题数据集合
		new AsyncTask<Object, Void, Integer>() {
			/*
			 * 异步线程后台处理数据。
			 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
			 */
			@Override
			protected Integer doInBackground(Object... params) {
				try{
					//题序
					final int pos = Math.max((Integer)params[0], 0);
					//试题数据
					final PaperItemModel model = (PaperItemModel)params[1];
					//是否显示
					final boolean display = (Boolean)params[2];
					if(model != null){
						Log.d(TAG, "异步线程加载试题数据..." + pos);
						//加载我的答案
						String myAnswers =  null;
						final IPaperItemDataDelegate dataDelegate = AppContext.getPaperDataDelegate();
						if(display && model != null && dataDelegate != null){
							myAnswers = dataDelegate.loadMyAnswer(model);
						}
						//创建试题数据模型
						final List<PaperItemTitleModel> modelArrays = ItemModelSupport.createItemModels(pos, model, display, myAnswers);
						if(modelArrays != null && modelArrays.size() > 0){
							//储存缓存
							itemCache.put(pos, modelArrays.toArray(new PaperItemTitleModel[0]));
							//
							return pos;
						}
					}
				}catch(Throwable e){
					Log.e(TAG, "异步线程加载试题数据异常:" + e.getMessage() , e);
				}
				return null;
			}
			/*
			 * 前台主线程处理数据。
			 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
			 */
			@Override
			protected void onPostExecute(Integer result) {
				Log.d(TAG, "完成试题数据模型创建..." + (result != null));
				//更新数据
				if(result != null){
					//刷新显示
					loadItemCacheModel(result);
				}
			}
		}.execute(itemOrder, itemModel, displayAnswer);
	}
	/*
	 * 获取试题数据行数。
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return (this.itemModels == null) ? -1 : this.itemModels.length;
	}
	/*
	 * 获取试题数据模型。
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		return  this.getCount() > position ? this.itemModels[position] :  null;
	}
	/*
	 * 获取试题行ID。
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}
	/*
	 * 试题内容模型类型总数。
	 * @see android.widget.BaseAdapter#getViewTypeCount()
	 */
	@Override
	public int getViewTypeCount() {
		return ItemModelType.values().length;
	}
	/*
	 * 试题内容模型类型。
	 * @see android.widget.BaseAdapter#getItemViewType(int)
	 */
	@Override
	public int getItemViewType(int position) {
		if(this.itemModels.length > position){
			final PaperItemTitleModel model = this.itemModels[position];
			if(model instanceof PaperItemAnalysisModel){
				return ItemModelType.ANALYSIS.value;
			}else if(model instanceof PaperItemOptModel){
				return ItemModelType.OPTION.value;
			}else {
				return ItemModelType.TITLE.value;
			}
		}
		return ItemModelType.NONE.value;
	}
	/*
	 * 创建行数据。
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ItemModelType viewType = ItemModelType.parse(this.getItemViewType(position));
		ViewHolder viewHolder = null;
		if(convertView == null){
			switch(viewType){
				case TITLE:{//标题
					convertView = this.mInflater.inflate(R.layout.ui_main_paper_items_title, parent, false);
					viewHolder = new ViewTitleHolder(convertView);
					convertView.setTag(viewHolder);
					break;
				}
				case OPTION:{//选项
					convertView = this.mInflater.inflate(R.layout.ui_main_paper_items_option, parent, false);
					viewHolder = new ViewOptionHolder(convertView);
					convertView.setTag(viewHolder);
					break;
				}
				case ANALYSIS:{//答案解析
					convertView = this.mInflater.inflate(R.layout.ui_main_paper_items_analysis, parent, false);
					viewHolder = new ViewAnalysisHolder(convertView);
					convertView.setTag(viewHolder);
					break;
				}
				default:break;
			}
		}else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		//加载数据..
		viewHolder.loadModelData(this.getItem(position));
		//
		return convertView;
	}
	/**
	 * 基类ViewHolder。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月27日
	 */
	private abstract  class ViewHolder{
		/**
		 *  构造函数。
		 * @param view
		 */
	  	public ViewHolder(View view){
	  		Log.d(TAG, "创建ViewHolder..." + view);
	  	};
		/**
		 * 加载数据。
		 * @param data
		 */
		public abstract void loadModelData(Object data);
	}
	/**
	 * 标题ViewHolder。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月27日
	 */
	private class ViewTitleHolder extends ViewHolder{
		private ItemTitleView itemView;
		/**
		 * 构造函数。
		 * @param view
		 */
		public ViewTitleHolder(View view) {
			super(view);
			this.itemView = (ItemTitleView)view;
			Log.d(TAG, "创建标题Holder...");
		}
		/*
		 * 加载数据。
		 * @see com.examw.test.adapter.PaperItemsAdapter.ViewHolder#loadModelData(java.lang.Object)
		 */
		@Override
		public void loadModelData(Object data) {
			if(this.itemView != null){
				this.itemView.loadModelData((PaperItemTitleModel)data);
			}
		}
	}
	/**
	 * 选项ViewHolder。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月27日
	 */
	private class ViewOptionHolder extends ViewHolder{
		private ItemOptionView itemView;
		/**
		 * 构造函数。
		 * @param view
		 */
		public ViewOptionHolder(View view) {
			super(view);
			this.itemView = (ItemOptionView)view;
			Log.d(TAG, "创建选项Holder...");
		}
		/*
		 * 加载数据。
		 * @see com.examw.test.adapter.PaperItemsAdapter.ViewHolder#loadModelData(java.lang.Object)
		 */
		@Override
		public void loadModelData(Object data) {
			 if(this.itemView != null){
				 this.itemView.loadModelData((PaperItemOptModel)data);
			 }
		}
	}
	/**
	 * 答题解析ViewHolder。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月27日
	 */
	private class ViewAnalysisHolder extends ViewHolder{
		private ItemAnalysisView itemView;
		/**
		 * 构造函数。
		 * @param view
		 */
		public ViewAnalysisHolder(View view) {
			super(view);
			this.itemView = (ItemAnalysisView)view;
			Log.d(TAG, "创建答题解析Holder...");
		}
		/*
		 * 加载数据。
		 * @see com.examw.test.adapter.PaperItemsAdapter.ViewHolder#loadModelData(java.lang.Object)
		 */
		@Override
		public void loadModelData(Object data) {
			 if(this.itemView != null){
				 this.itemView.loadModelData((PaperItemAnalysisModel)data);
			 }
		}
	}
	/**
	 * 试题内容模型类型枚举。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月27日
	 */
	private enum ItemModelType { 
		NONE(-1), TITLE(0), OPTION(1), ANALYSIS(2);
		private final int value;
		/**
		 * 构造函数。
		 * @param value
		 */
		private ItemModelType(int value){
			this.value = value;
		}
		/**
		 * 转换。
		 * @param value
		 * @return
		 */
		public static ItemModelType parse(int value){
			for(ItemModelType type : ItemModelType.values()){
				if(type.value == value) return type;
			}
			return NONE;
		}
	};
}