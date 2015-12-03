package com.examw.test.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.examw.test.model.PaperItemModel;
import com.examw.test.model.PaperItemModel.ItemType;
import com.examw.test.utils.TextImgUtil;

import android.util.Log;

/**
 * 试题数据模型支持。
 * 
 * @author jeasonyoung
 * @since 2015年7月31日
 */
public final class ItemModelSupport {
	private static final String TAG = "ItemModelSupport";
	/**
	 * 创建试题数据模型。
	 * @param order
	 * 题序。
	 * @param itemModel
	 * 试题数据模型。
	 * @param displayAnswer
	 * 是否显示答案。
	 * @param myAnswers
	 * 我的答案。
	 * @return
	 * 
	 */
	public static synchronized List<PaperItemTitleModel> createItemModels(final int order, final PaperItemModel itemModel, 
			final boolean displayAnswer, final String myAnswers){
		Log.d(TAG, "创建试题数据模型");
		if(itemModel != null  && itemModel.getType() != null && itemModel.getType() >= 0){
			//选项集合
			final ItemType type = ItemType.parse(itemModel.getType()); 
			switch(type){
				case Single://单选
				case Multy://多选
				case Uncertain://不定向选
				{
					 return createChoiceItem(order, type, itemModel, displayAnswer, myAnswers);
				}
				case Judge:{//判断题
					return createJudgeItem(order, type, itemModel, displayAnswer, myAnswers);
				}
				case Qanda:{//问答题
					return createQandaItem(order, type, itemModel, displayAnswer);
				}
				case ShareTitle:{//共享题干题
					return createShareTitle(order, type, itemModel, displayAnswer, myAnswers);
				}
				case ShareAnswer:{//共享答案题
					return createShareAnswerItem(order, type, itemModel, displayAnswer, myAnswers);
				}
				default:break;
			}
		}
		return null;
	}
	/**
	 * 创建试题选项数据模型集合。
	 * @param options
	 * @param type
	 * @param displayAnswer
	 * @param rightAnswers
	 * @param myAnswers
	 * @return
	 */
	private static List<PaperItemOptModel> createOptions(final List<PaperItemModel> options, final ItemType type, 
			final boolean displayAnswer, final String rightAnswers,final String myAnswers){
		int len = 0;
		if(options != null && (len = options.size()) > 0){
			final List<PaperItemOptModel> opts = new ArrayList<PaperItemOptModel>(len);
			//选项
			for(PaperItemModel item : options){
				if(item == null) continue;
				//创建选项
				final PaperItemOptModel optModel = new PaperItemOptModel(item);
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
	/**
	 * 创建选择题数据模型。
	 * @param order
	 * @param type
	 * @param itemModel
	 * @param displayAnswer
	 * @param myAnswers
	 * @return
	 */
	private static List<PaperItemTitleModel> createChoiceItem(final int order, final ItemType type, final PaperItemModel itemModel, 
			final boolean displayAnswer, final String myAnswers) {
		Log.d(TAG, "创建选择题...");
		//创建结果集合
		final List<PaperItemTitleModel> list = new ArrayList<PaperItemTitleModel>();
		//标题
		final PaperItemTitleModel titleModel = new PaperItemTitleModel(itemModel);
		titleModel.setOrder(order + 1);
		//添加到数据源
		list.add(titleModel);
		//选项
		final List<PaperItemOptModel> optModels = createOptions(itemModel.getChildren(), type, displayAnswer, itemModel.getAnswer(), myAnswers);
		if(optModels != null && optModels.size() > 0){
			//添加到数据源
			list.addAll(optModels);
		}
		//答案解析
		if(displayAnswer){
			final PaperItemAnalysisModel analysisModel = new PaperItemAnalysisModel(itemModel, optModels);
			analysisModel.setMyAnswers(myAnswers);
			//添加到数据源
			list.add(analysisModel);
		}
		return list;
	}
	/**
	 * 创建判断题数据模型。
	 * @param order
	 * @param type
	 * @param itemModel
	 * @param displayAnswer
	 * @param myAnswers
	 * @return
	 */
	private static List<PaperItemTitleModel> createJudgeItem(final int order, final ItemType type, final PaperItemModel itemModel, 
			final boolean displayAnswer, final String myAnswers) {
		Log.d(TAG, "创建判断题...");
		//创建结果集合
		final List<PaperItemTitleModel> list = new ArrayList<PaperItemTitleModel>();
		//标题
		final PaperItemTitleModel titleModel = new PaperItemTitleModel(itemModel);
		titleModel.setOrder(order + 1);
		//添加到数据源
		list.add(titleModel);
		
		//判断选项初始化
		//1.正确答案
		final PaperItemModel optRightModel = new PaperItemModel();
		optRightModel.setId(String.valueOf(PaperItemModel.ItemJudgeAnswer.Right.getValue()));
		optRightModel.setContent(PaperItemModel.ItemJudgeAnswer.Right.getName());
		
		//2.错误答案
		final PaperItemModel optWrongModel = new PaperItemModel();
		optWrongModel.setId(String.valueOf(PaperItemModel.ItemJudgeAnswer.Wrong.getValue()));
		optWrongModel.setContent(PaperItemModel.ItemJudgeAnswer.Wrong.getName());
		
		//选项
		final List<PaperItemOptModel> optModels = createOptions(Arrays.asList(new PaperItemModel[] {optRightModel, optWrongModel}), 
				type, displayAnswer, itemModel.getAnswer(), myAnswers);
		if(optModels != null && optModels.size() > 0){
			//添加到数据源
			list.addAll(optModels);
		}
		//答案解析
		if(displayAnswer){
			final PaperItemAnalysisModel analysisModel = new PaperItemAnalysisModel(itemModel, optModels);
			analysisModel.setMyAnswers(myAnswers);
			//添加到数据源
			list.add(analysisModel);
		}
		return list;
	}
	/**
	 * 创建问答题数据模型。
	 * @param order
	 * @param type
	 * @param itemModel
	 * @param displayAnswer
	 * @return
	 */
	private static List<PaperItemTitleModel> createQandaItem(final int order, final ItemType type, final PaperItemModel itemModel, final boolean displayAnswer) {
		Log.d(TAG, "创建问答题...");
		//创建结果集合
		final List<PaperItemTitleModel> list = new ArrayList<PaperItemTitleModel>();
		
		//标题
		final PaperItemTitleModel titleModel = new PaperItemTitleModel(itemModel);
		titleModel.setOrder(order + 1);
		//添加到数据源
		list.add(titleModel);
		
		//答案解析
		if(displayAnswer){
			final PaperItemAnalysisModel analysisModel = new PaperItemAnalysisModel(itemModel, null);
			analysisModel.setMyAnswers(null);
			//添加到数据源
			list.add(analysisModel);
		}
		return list;
	}
	/**
	 * 创建共享题干题数据模型。
	 * @param order
	 * @param type
	 * @param itemModel
	 * @param displayAnswer
	 * @param myAnswers
	 * @return
	 */
	private static List<PaperItemTitleModel> createShareTitle(final int order,final  ItemType type, final PaperItemModel itemModel, 
			final boolean displayAnswer, final String myAnswers) {
		Log.d(TAG, "创建共享题干...");
		//创建结果集合
		final List<PaperItemTitleModel> list = new ArrayList<PaperItemTitleModel>();
		//标题
		final PaperItemTitleModel titleModel = new PaperItemTitleModel(itemModel);
		titleModel.setOrder(0);
		//添加到数据源
		list.add(titleModel);
		//
		final int index = itemModel.getIndex();
		if(itemModel.getChildren() != null && itemModel.getChildren().size() > index){
			final PaperItemModel child = itemModel.getChildren().get(index);
			if(child != null){
				//子标题
				final PaperItemTitleModel subTitleModel = new PaperItemTitleModel(itemModel);
				subTitleModel.setOrder(order + 1);
				//添加到数据源
				list.add(subTitleModel);
				//选项
				final List<PaperItemOptModel> optModels = createOptions(child.getChildren(), type, displayAnswer, child.getAnswer(), myAnswers);
				if(optModels != null && optModels.size() > 0){
					//添加到数据源
					list.addAll(optModels);
				}
				//答案解析
				if(displayAnswer){
					final PaperItemAnalysisModel analysisModel = new PaperItemAnalysisModel(child, optModels);
					analysisModel.setMyAnswers(myAnswers);
					//添加到数据源
					list.add(analysisModel);
				}
			}
		}
		return list;
	}
	/**
	 * 创建共享答案题数据模型。
	 * @param order
	 * @param type
	 * @param itemModel
	 * @param displayAnswer
	 * @param myAnswers
	 * @return
	 */
	private static List<PaperItemTitleModel> createShareAnswerItem(final int order,final ItemType type,final PaperItemModel itemModel,
			final boolean displayAnswer, final String myAnswers) {
		Log.d(TAG, "创建共享题干...");
		//创建结果集合
		final List<PaperItemTitleModel> list = new ArrayList<PaperItemTitleModel>();
		
		//标题
		if(StringUtils.isNotBlank(itemModel.getContent())){
			final PaperItemTitleModel titleModel = new PaperItemTitleModel(itemModel);
			titleModel.setOrder(0);
			//添加到数据源
			list.add(titleModel);
		}
		//子题
		if(itemModel.getChildren() != null && itemModel.getChildren().size() > 0){
			int max = 0, index = itemModel.getIndex();
			PaperItemModel p = null;
			//构建选项，查找根
			final List<PaperItemModel> optItemModels = new ArrayList<PaperItemModel>();
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
				final PaperItemModel subItemModel = p.getChildren().get(index);
				if(subItemModel != null && subItemModel.getType() != null){
					//子标题
					final PaperItemTitleModel titleModel = new PaperItemTitleModel(subItemModel);
					titleModel.setOrder(order + 1);
					//添加到数据源
					list.add(titleModel);
					//选项
					final List<PaperItemOptModel> optModels = createOptions(optItemModels, ItemType.values()[subItemModel.getType() - 1], displayAnswer, subItemModel.getAnswer(), myAnswers);
					if(optModels != null && optModels.size() > 0){
						//添加到数据源
						list.addAll(optModels);
					}
					//答案解析
					if(displayAnswer){
						final PaperItemAnalysisModel analysisModel = new PaperItemAnalysisModel(subItemModel, optModels);
						analysisModel.setMyAnswers(myAnswers);
						//添加到数据源
						list.add(analysisModel);
					}
				}
			}
		}
		return list;
	}
	/**
	 * 试题标题数据模型。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月23日
	 */
	public static class PaperItemTitleModel implements Serializable{
		private static final long serialVersionUID = 1L;
		private String id;
		private int order;
		private ItemType itemType;
		private String content;
		/**
		 * 构造函数。
		 * @param itemModel
		 */
		public PaperItemTitleModel(PaperItemModel itemModel){
			if(itemModel != null){
				this.id = itemModel.getId();
				this.setOrder(itemModel.getOrderNo());
				this.setItemType(itemModel.getType() == null ? null : ItemType.parse(itemModel.getType()));
				this.setContent(itemModel.getContent());
			}
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
			this.content = TextImgUtil.findImgReplaceLocal(content);
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
	}
	/**
	 * 试题选项数据模型。
	 * 
	 * @author jeasonyoung
	 * @since 2015年7月23日
	 */
	public static class PaperItemOptModel extends PaperItemTitleModel{
		private static final long serialVersionUID = 1L;
		private String rightAnswers,myAnswers;
		private boolean display;
		/**
		 * 构造函数。
		 * @param itemModel
		 */
		public PaperItemOptModel(PaperItemModel itemModel) {
			super(itemModel);
			if(itemModel != null){
				this.setRightAnswers(itemModel.getAnswer());
			}
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
		 * @param options
		 */
		public PaperItemAnalysisModel(PaperItemModel itemModel, List<PaperItemOptModel> options) {
			super(itemModel);
			if(itemModel != null){
				this.setContent(itemModel.getAnalysis());
			}
			this.options = options;
		}
		/**
		 * 获取选项集合。
		 * @return options
		 */
		public List<PaperItemOptModel> getOptions() {
			return options;
		}
	}
}