package com.examw.test.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.examw.test.R;
import com.examw.test.app.AppContext;
import com.examw.test.dao.IPaperItemDataDelegate;
import com.examw.test.model.PaperItemModel;
import com.examw.test.model.PaperItemModel.ItemType;
import com.examw.test.support.ItemModelSupport;
import com.examw.test.support.ItemModelSupport.PaperItemAnalysisModel;
import com.examw.test.support.ItemModelSupport.PaperItemOptModel;
import com.examw.test.support.ItemModelSupport.PaperItemTitleModel;
import com.examw.test.widget.ItemAnalysisView;
import com.examw.test.widget.ItemOptionView;
import com.examw.test.widget.ItemTitleView;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

/**
 * 试卷试题数据适配器。
 * 
 * @author jeasonyoung
 * @since 2015年7月26日
 */
public class PaperItemsAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
	private static final String TAG = "PaperItemsAdapter";
	private final LayoutInflater mInflater;
	private final List<PaperItemTitleModel> dataSource;
	
	private int itemOrder;
	private PaperItemModel itemModel;
	private boolean displayAnswer;
	private ItemClickListener itemClickListener;
	/**
	 * 构造函数，
	 * @param context
	 */
	public PaperItemsAdapter(Context context){
		Log.d(TAG, "初始化...");
		this.dataSource = new ArrayList<PaperItemTitleModel>();
		this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	/**
	 * 设置试题点击事件监听器。
	 * @param itemClickListener 
	 *	  itemClickListener
	 */
	public void setItemClickListener(ItemClickListener itemClickListener) {
		this.itemClickListener = itemClickListener;
	}
	/**
	 * 加载试题数据。
	 * @param itemOrder
	 * @param itemModel
	 * @param displayAnswer
	 */
	public void loadItemModel(final int itemOrder, final PaperItemModel itemModel, final boolean displayAnswer) {
		Log.d(TAG, "加载试题["+itemOrder+"]数据...");
		this.itemOrder = itemOrder;
		this.itemModel = itemModel;
		this.displayAnswer = displayAnswer;
		//异步线程构造试题数据集合
		new AsyncTask<Void, Void, List<PaperItemTitleModel>>() {
			/*
			 * 异步线程后台处理数据。
			 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
			 */
			@Override
			protected List<PaperItemTitleModel> doInBackground(Void... params) {
				try{
					if(itemModel != null){
						Log.d(TAG, "异步线程加载试题数据..." + itemOrder);
						//加载我的答案
						String myAnswers = null;
						final IPaperItemDataDelegate dataDelegate = AppContext.getPaperDataDelegate();
						if(dataDelegate != null){
							myAnswers = dataDelegate.loadMyAnswer(itemModel);
						}
						//创建试题数据模型
						return  ItemModelSupport.createItemModels(itemOrder, itemModel, displayAnswer, myAnswers);
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
			protected void onPostExecute(List<PaperItemTitleModel> models) {
				Log.d(TAG, "完成试题数据模型创建..." + (models != null));
				//清除数据源
				dataSource.clear();
				//添加到数据源
				dataSource.addAll(models);
				//通知适配器更新
				notifyDataSetChanged();
			}
		}.execute((Void)null);
	}
	/*
	 * 选项点击处理。
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view,final int position, long id) {
		if(this.displayAnswer || this.getCount() == 0)return;
		new AsyncTask<Void, Void, String>() {
			/*
			 * 后台线程处理。
			 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
			 */
			@Override
			protected String doInBackground(Void... params) {
				try{
					Log.d(TAG, "选项点击选项["+position+"]事件处理...");
					final PaperItemOptModel optModel = (PaperItemOptModel)getItem(position);
					if(optModel == null) return null;
					//选项ID
					final String optId = optModel.getId();
					//加载我的答案
					final List<String> selectOpts = new ArrayList<String>();
					final IPaperItemDataDelegate dataDelegate = AppContext.getPaperDataDelegate();
					if(dataDelegate != null){
						final String myAnswers = dataDelegate.loadMyAnswer(itemModel);
						if(StringUtils.isNotBlank(myAnswers)){
							selectOpts.addAll(Arrays.asList(myAnswers.split(",")));
						}
					}
					//是否多选
					if(optModel.getItemType() == null ? false : optModel.getItemType() == ItemType.Multy){//多选
						if(selectOpts.contains(optId)){//二次选中则取消
							selectOpts.remove(optId);
						}else {
							selectOpts.add(optId);
						}
					}else{//单选
						selectOpts.clear();
						selectOpts.add(optId);
					}
					//返回
					return StringUtils.join(selectOpts, ",");
				}catch(Exception e){
					Log.e(TAG, "选中试题["+ itemOrder +"]选项[" + position + "]处理异常:" + e.getMessage(), e);
				}
				return null;
			}
			/*
			 * 主线程处理。
			 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
			 */
			@Override
			protected void onPostExecute(String result) {
				Log.d(TAG, "选中结果=>" + result);
				//数据源处理
				if(dataSource != null){
					//循环赋值
					for(PaperItemTitleModel model : dataSource){
						if(model == null) continue;
						if(model instanceof PaperItemOptModel){
							((PaperItemOptModel)model).setMyAnswers(result);
						}
					}
					//通知适配器更新数据
					notifyDataSetChanged();
				}
				//点击事件监听器处理
				if(itemClickListener != null){
					itemClickListener.onItemClick(itemOrder,  result);
				}
			}
		}.execute((Void)null);
	};
	/*
	 * 获取试题数据行数。
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return this.dataSource == null ? 0 : this.dataSource.size();
	}
	/*
	 * 获取试题数据模型。
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		return  this.getCount() > position ? this.dataSource.get(position):  null;
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
		if(this.getCount() > position){
			final PaperItemTitleModel model = (PaperItemTitleModel)this.getItem(position);
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
		final ItemModelType viewType = ItemModelType.parse(this.getItemViewType(position));
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
				 this.itemView.loadModelData((PaperItemOptModel)data, displayAnswer);
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
	}

	/**
	 * 试题点击事件监听器。
	 * 
	 * @author jeasonyoung
	 * @since 2015年8月1日
	 */
	public interface ItemClickListener{
		/**
		 * 试题点击事件处理。
		 * @param pos
		 * @param result
		 */
		void onItemClick(int pos, String myAnswer);
	}
}