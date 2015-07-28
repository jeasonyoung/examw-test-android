package com.examw.test.adapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.examw.test.R;
import com.examw.test.app.AppContext;
import com.examw.test.model.PaperItemModel;
import com.examw.test.model.PaperItemModel.ItemType;
import com.examw.test.ui.PaperActivity.PaperDataDelegate;
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
	private static final SparseArray<PaperItemTitleModel[]> itemCache = new SparseArray<PaperItemTitleModel[]>();
	private final LayoutInflater mInflater;
	private PaperItemTitleModel [] itemModels;
	private boolean displayAnswer;
	/**
	 * 构造函数。
	 * @param context
	 */
	public PaperItemsAdapter(Context context){
		Log.d(TAG, "初始化...");
		this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.itemModels = new PaperItemTitleModel[0];
	}
	//加载试题缓存数据模型
	private void loadItemCacheModel(int itemOrder){
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
		if((this.displayAnswer == displayAnswer) && itemCache.indexOfKey(itemOrder) > -1){
			this.loadItemCacheModel(itemOrder);
			return;
		}
		//设置是否显示答案
		this.displayAnswer = displayAnswer;
		//异步线程构造试题数据集合
		new AsyncTask<Object, Void, Object>() {
			private int pos;
			private PaperItemModel model;
			private boolean display;
			/*
			 * 异步线程后台处理数据。
			 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
			 */
			@Override
			protected Object doInBackground(Object... params) {
				try{
					//题序
					this.pos = Math.max((Integer)params[0], 0);
					//试题数据
					this.model = (PaperItemModel)params[1];
					//是否显示
					this.display = (Boolean)params[2];
					if(this.model != null){
						Log.d(TAG, "异步线程加载试题数据..." + this.pos);
						//加载我的答案
						String myAnswers =  null;
						final PaperDataDelegate dataDelegate = AppContext.getPaperDataDelegate();
						if(this.display && this.model != null && dataDelegate != null){
							myAnswers = dataDelegate.loadMyAnswer(this.model);
						}
						//创建试题数据模型
						List<PaperItemTitleModel> models = createItemModels(this.pos, this.model, this.display, myAnswers);
						return (models == null || models.size() == 0) ? null : models.toArray(new PaperItemTitleModel[0]);
					}
				}catch(Throwable e){
					Log.e(TAG, "异步线程加载试题["+this.pos+"]数据异常:" + e.getMessage() , e);
				}
				return null;
			}
			/*
			 * 前台主线程处理数据。
			 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
			 */
			@Override
			protected void onPostExecute(Object result) {
				Log.d(TAG, "完成试题["+ this.pos +"]数据模型创建..." + (result != null));
				//更新数据
				if(result != null){
					//储存缓存
					itemCache.put(this.pos, (PaperItemTitleModel[])result);
					//刷新显示
					loadItemCacheModel(this.pos);
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
		return (this.itemModels == null) ? 0 : this.itemModels.length;
	}
	/*
	 * 获取试题数据模型。
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		return (this.itemModels == null) ? null : this.itemModels[position];
	}
	/*
	 * 获取试题行ID。
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}
	//创建试题数据模型
	private synchronized List<PaperItemTitleModel> createItemModels(int order, PaperItemModel itemModel, boolean displayAnswer, String myAnswers){
		Log.d(TAG, "创建试题数据模型");
		if(itemModel != null  && itemModel.getType() != null && itemModel.getType() >= 0){
			//选项集合
			ItemType type = ItemType.values()[itemModel.getType() - 1];
			switch(type){
				case Single://单选
				case Multy://多选
				case Uncertain://不定向选
				{
					 return this.createChoiceItem(order, type, itemModel, displayAnswer, myAnswers);
				}
				case Judge:{//判断题
					return this.createJudgeItem(order, type, itemModel, displayAnswer, myAnswers);
				}
				case Qanda:{//问答题
					return this.createQandaItem(order, type, itemModel, displayAnswer);
				}
				case ShareTitle:{//共享题干题
					return this.createShareTitle(order, type, itemModel, displayAnswer, myAnswers);
				}
				case ShareAnswer:{//共享答案题
					return this.createShareAnswerItem(order, type, itemModel, displayAnswer, myAnswers);
				}
				default:break;
			}
		}
		return null;
	}
	//创建试题选项数据集合。
	private List<PaperItemOptModel> createOptions(List<PaperItemModel> options, ItemType type, boolean displayAnswer, String rightAnswers, String myAnswers){
		int len = 0;
		if(options != null && (len = options.size()) > 0){
			List<PaperItemOptModel> opts = new ArrayList<PaperItemOptModel>(len);
			//选项
			for(PaperItemModel item : options){
				if(item == null) continue;
				//创建选项
				PaperItemOptModel optModel = new PaperItemOptModel(item);
				optModel.setItemType(type);
				optModel.setRightAnswers(rightAnswers);
				optModel.setMyAnswers(myAnswers);
				optModel.setDisplay(displayAnswer);
				//添加选项集合
				opts.add(optModel);
			}
			//
			return opts;
		}
		return null;
	}
	//创建选择题。
	private List<PaperItemTitleModel> createChoiceItem(int order, ItemType type,  PaperItemModel itemModel, boolean displayAnswer, String myAnswers) {
		Log.d(TAG, "创建选择题...");
		//创建结果集合
		List<PaperItemTitleModel> list = new ArrayList<PaperItemTitleModel>();
		//标题
		PaperItemTitleModel titleModel = new PaperItemTitleModel(itemModel);
		titleModel.setOrder(order + 1);
		//添加到数据源
		list.add(titleModel);
		//选项
		List<PaperItemOptModel>  optModels = this.createOptions(itemModel.getChildren(), type, displayAnswer, itemModel.getAnswer(), myAnswers);
		if(optModels != null && optModels.size() > 0){
			//添加到数据源
			list.addAll(optModels);
		}
		//答案解析
		if(displayAnswer && optModels != null && optModels.size() > 0){
			PaperItemAnalysisModel analysisModel = new PaperItemAnalysisModel(itemModel);
			analysisModel.setOptions(optModels);
			analysisModel.setMyAnswers(myAnswers);
			//添加到数据源
			list.add(analysisModel);
		}
		return list;
	}
	//创建判断题。
	private List<PaperItemTitleModel> createJudgeItem(int order, ItemType type, PaperItemModel itemModel, boolean displayAnswer, String myAnswers) {
		Log.d(TAG, "创建判断题...");
		//创建结果集合
		List<PaperItemTitleModel> list = new ArrayList<PaperItemTitleModel>();
		//标题
		PaperItemTitleModel titleModel = new PaperItemTitleModel(itemModel);
		titleModel.setOrder(order + 1);
		//添加到数据源
		list.add(titleModel);
		
		//判断选项初始化
		//1.正确答案
		PaperItemModel optRightModel = new PaperItemModel();
		optRightModel.setId(String.valueOf(PaperItemModel.ItemJudgeAnswer.Right.getValue()));
		optRightModel.setContent(PaperItemModel.ItemJudgeAnswer.Right.getName());
		
		//2.错误答案
		PaperItemModel optWrongModel = new PaperItemModel();
		optWrongModel.setId(String.valueOf(PaperItemModel.ItemJudgeAnswer.Wrong.getValue()));
		optWrongModel.setContent(PaperItemModel.ItemJudgeAnswer.Wrong.getName());
		
		//选项
		List<PaperItemOptModel> optModels = this.createOptions(Arrays.asList(new PaperItemModel[] {optRightModel, optWrongModel}), 
				type, displayAnswer, itemModel.getAnswer(), myAnswers);
		if(optModels != null && optModels.size() > 0){
			//添加到数据源
			list.addAll(optModels);
		}
		//答案解析
		if(displayAnswer && optModels != null && optModels.size() > 0){
			PaperItemAnalysisModel analysisModel = new PaperItemAnalysisModel(itemModel);
			analysisModel.setOptions(optModels);
			analysisModel.setMyAnswers(myAnswers);
			//添加到数据源
			list.add(analysisModel);
		}
		return list;
	}
	//创建问答题
	private List<PaperItemTitleModel> createQandaItem(int order, ItemType type, PaperItemModel itemModel, boolean displayAnswer) {
		Log.d(TAG, "创建问答题...");
		//创建结果集合
		List<PaperItemTitleModel> list = new ArrayList<PaperItemTitleModel>();
		
		//标题
		PaperItemTitleModel titleModel = new PaperItemTitleModel(itemModel);
		titleModel.setOrder(order + 1);
		//添加到数据源
		list.add(titleModel);
		
		//答案解析
		if(displayAnswer){
			PaperItemAnalysisModel analysisModel = new PaperItemAnalysisModel(itemModel);
			analysisModel.setOptions(null);
			analysisModel.setMyAnswers(null);
			//添加到数据源
			list.add(analysisModel);
		}
		return list;
	}
	//创建共享题干
	private List<PaperItemTitleModel> createShareTitle(int order, ItemType type, PaperItemModel itemModel, boolean displayAnswer, String myAnswers) {
		Log.d(TAG, "创建共享题干...");
		//创建结果集合
		List<PaperItemTitleModel> list = new ArrayList<PaperItemTitleModel>();
		//标题
		PaperItemTitleModel titleModel = new PaperItemTitleModel(itemModel);
		titleModel.setOrder(0);
		//添加到数据源
		list.add(titleModel);
		//
		int index = itemModel.getIndex();
		if(itemModel.getChildren() != null && itemModel.getChildren().size() > index){
			PaperItemModel child = itemModel.getChildren().get(index);
			if(child != null){
				//子标题
				PaperItemTitleModel subTitleModel = new PaperItemTitleModel(itemModel);
				subTitleModel.setOrder(order + 1);
				//添加到数据源
				list.add(subTitleModel);
				//选项
				List<PaperItemOptModel> optModels = this.createOptions(child.getChildren(), type, displayAnswer, child.getAnswer(), myAnswers);
				if(optModels != null && optModels.size() > 0){
					//添加到数据源
					list.addAll(optModels);
				}
				//答案解析
				if(displayAnswer && optModels != null && optModels.size() > 0){
					PaperItemAnalysisModel analysisModel = new PaperItemAnalysisModel(child);
					analysisModel.setOptions(optModels);
					analysisModel.setMyAnswers(myAnswers);
					//添加到数据源
					list.add(analysisModel);
				}
			}
		}
		return list;
	}
	//创建共享答案题
	private List<PaperItemTitleModel> createShareAnswerItem(int order, ItemType type, PaperItemModel itemModel, boolean displayAnswer, String myAnswers) {
		Log.d(TAG, "创建共享题干...");
		//创建结果集合
		List<PaperItemTitleModel> list = new ArrayList<PaperItemTitleModel>();
		
		//标题
		if(StringUtils.isNotBlank(itemModel.getContent())){
			PaperItemTitleModel titleModel = new PaperItemTitleModel(itemModel);
			titleModel.setOrder(0);
			//添加到数据源
			list.add(titleModel);
		}
		//子题
		if(itemModel.getChildren() != null && itemModel.getChildren().size() > 0){
			int max = 0, index = itemModel.getIndex();
			PaperItemModel p = null;
			//构建选项，查找根
			List<PaperItemModel> optItemModels = new ArrayList<PaperItemModel>();
			for(PaperItemModel child : itemModel.getChildren()){
				if(child == null) continue;
				if(child.getOrderNo() > max){
					if(p != null){
						optItemModels.add(child);
					}
					p = child;
					max = child.getOrderNo();
				}else {
					optItemModels.add(child);
				}
			}
			//拼接试题
			if(p != null && p.getChildren() != null && p.getChildren().size() > index){
				PaperItemModel subItemModel = p.getChildren().get(index);
				if(subItemModel != null && subItemModel.getType() != null){
					//子标题
					PaperItemTitleModel titleModel = new PaperItemTitleModel(subItemModel);
					titleModel.setOrder(order + 1);
					//添加到数据源
					list.add(titleModel);
					//选项
					List<PaperItemOptModel> optModels = this.createOptions(optItemModels, ItemType.values()[subItemModel.getType() - 1], displayAnswer, subItemModel.getAnswer(), myAnswers);
					if(optModels != null && optModels.size() > 0){
						//添加到数据源
						list.addAll(optModels);
					}
					//答案解析
					if(displayAnswer && optItemModels != null && optItemModels.size() > 0){
						PaperItemAnalysisModel analysisModel = new PaperItemAnalysisModel(subItemModel);
						analysisModel.setOptions(optModels);
						analysisModel.setMyAnswers(myAnswers);
						//添加到数据源
						list.add(analysisModel);
					}
				}
			}
		}
		return list;
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
				if(type.value == value){
					return type;
				}
			}
			return NONE;
		}
	};
	/**
	 * 试题标题数据模型。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月23日
	 */
	public static class PaperItemTitleModel implements Serializable{
		private static final long serialVersionUID = 1L;
		private String id,content;
		private ItemType itemType;
		private  int order;
		private List<String> images;
		/**
		 * 构造函数。
		 * @param itemModel
		 */
		public PaperItemTitleModel(PaperItemModel itemModel){
			this.id = itemModel.getId();
			if(itemModel.getType() != null){
				this.setItemType(ItemType.values()[itemModel.getType() - 1]);
			}
			this.setContent(itemModel.getContent());
			this.setOrder(itemModel.getOrderNo());
			this.setImages(itemModel.getItemContentImgUrls()); 
		}
		/**
		 * 获取ID。
		 * @return ID。
		 */
		public String getId() {
			return id;
		}
		/**
		 * 获取内容。
		 * @return 内容。
		 */
		public String getContent() {
			return content;
		}
		/**
		 * 设置内容。
		 * @param content
		 * 内容。
		 */
		protected void setContent(String content){
			this.content = content;
		}
		/**
		 * 获取题型。
		 * @return 题型。
		 */
		public ItemType getItemType() {
			return itemType;
		}
		/**
		 * 设置题型。
		 * @param itemType 
		 *	  题型。
		 */
		public void setItemType(ItemType itemType) {
			this.itemType = itemType;
		}
		/**
		 * 获取题序号。
		 * @return 题序号。
		 */
		public int getOrder() {
			return order;
		}
		/**
		 * 设置题序号。
		 * @param order 
		 *	  题序号。
		 */
		public void setOrder(int order) {
			this.order = order;
		}
		/**
		 * 获取图片集合。
		 * @return 图片集合。
		 */
		public List<String> getImages() {
			return images;
		}
		/**
		 * 设置图片集合。
		 * @param images 
		 *	  图片集合。
		 */
		public void setImages(List<String> images) {
			this.images = images;
		}
		/*
		 * 重载。
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return this.content;
		}
	}
	/**
	 * 试题选项数据模型。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月23日
	 */
	public static class PaperItemOptModel extends PaperItemTitleModel{
		private static final long serialVersionUID = 1L;
		private String myAnswers,rightAnswers;
		private boolean display;
		/**
		 * 构造函数。
		 * @param itemModel
		 */
		public PaperItemOptModel(PaperItemModel itemModel) {
			super(itemModel);
			this.setRightAnswers(itemModel.getAnswer()); 
		}
		/**
		 * 获取我的答案。
		 * @return 我的答案。
		 */
		public String getMyAnswers() {
			return myAnswers;
		}
		/**
		 * 设置我的答案。
		 * @param myAnswers 
		 *	  我的答案。
		 */
		public void setMyAnswers(String myAnswers) {
			this.myAnswers = myAnswers;
		}
		/**
		 * 获取正确答案。
		 * @return 正确答案。
		 */
		public String getRightAnswers() {
			return rightAnswers;
		}
		/**
		 * 设置正确答案。
		 * @param rightAnswers 
		 *	  正确答案。
		 */
		public void setRightAnswers(String rightAnswers) {
			this.rightAnswers = rightAnswers;
		}
		/**
		 * 获取是否显示答案。
		 * @return  是否显示答案。
		 */
		public boolean isDisplay() {
			return display;
		}
		/**
		 * 设置是否显示答案。
		 * @param display 
		 *	   是否显示答案。
		 */
		public void setDisplay(boolean display) {
			this.display = display;
		}
	}
	/**
	 * 试题答案解析数据模型。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月23日
	 */
	public static class PaperItemAnalysisModel extends PaperItemOptModel{
		private static final long serialVersionUID = 1L;
		private List<PaperItemOptModel> options;
		/**
		 * 构造函数。
		 * @param itemModel
		 */
		public PaperItemAnalysisModel(PaperItemModel itemModel) {
			super(itemModel);
			this.setContent(itemModel.getAnalysis());
			this.setImages(itemModel.getItemAnalysisImgUrls());
		}
		/**
		 * 获取选项集合。
		 * @return options
		 */
		public List<PaperItemOptModel> getOptions() {
			return options;
		}
		/**
		 * 设置 options
		 * @param options 
		 *	  options
		 */
		public void setOptions(List<PaperItemOptModel> options) {
			this.options = options;
		}
	}
}