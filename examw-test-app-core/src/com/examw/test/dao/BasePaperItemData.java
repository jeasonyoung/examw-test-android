package com.examw.test.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

import com.examw.test.dao.PaperDao.ItemStatus;
import com.examw.test.model.PaperItemModel;
import com.examw.test.model.PaperModel;
import com.examw.test.model.PaperStructureModel;

/**
 * 试题数据基类
 * 
 * @author jeasonyoung
 * @since 2015年7月31日
 */
public abstract class BasePaperItemData extends PaperItemDataDelegate {
	private static final long serialVersionUID = 1L;
	private static final String TAG = "BaseItemData"; 
	/**
	 * 构造函数。
	 * @param context
	 * @param paperModel
	 * @param paperRecordId
	 */
	public BasePaperItemData(Context context, String paperRecordId){
		super(context, paperRecordId);
		Log.d(TAG, "初始化...");
	}
	/**
	 * 获取试卷数据模型对象。
	 * @return 试卷数据模型对象。
	 */
	protected abstract PaperModel getPaperModel();
	/*
	 * 试题数据集合(异步线程调用)。
	 * @see com.examw.test.dao.PaperDataDelegate#dataSourceOfPaperViews()
	 */
	@Override
	public List<PaperItemModel> dataSourceOfPaperViews() throws Exception{
		Log.d(TAG, "加载试题数据集合...");
		//返回缓存
		if(this.items != null && this.items.size() > 0) return this.items;
		//第一次加载数据
		int total = 0;
		final PaperModel paperModel = this.getPaperModel();
		if(paperModel != null && (total =paperModel.getTotal()) > 0 && paperModel.getStructures() != null){
			//初始化
			this.items = new ArrayList<PaperItemModel>(total);
			total = paperModel.getStructures().size();
			if(total > 0){
				this.cardSections = new ArrayList<PaperItemDataDelegate.AnswerCardSectionModel>(total);
				this.cardItemsMap = new SparseArray<PaperItemDataDelegate.AnswerCardItemModel[]>(total);
			}
			//装载数据
			int section = 0, order = 0;
			for(PaperStructureModel s : paperModel.getStructures()){
				if(s == null || s.getItems() == null) continue;
				//创建答题卡分组数据模型
				this.cardSections.add(new AnswerCardSectionModel(s.getTitle(), s.getDescription()));
				//初始化分组下的试题集合
				List<PaperItemDataDelegate.AnswerCardItemModel> cardItemModels = new ArrayList<PaperItemDataDelegate.AnswerCardItemModel>(s.getItems().size());
				//循环试题集合
				for(PaperItemModel item : s.getItems()){
					if(item == null || StringUtils.isBlank(item.getId())) continue;
					//设置所属试卷结构ID
					item.setStructureId(s.getId());
					//设置所属试卷结构名称
					item.setStructureTitle(s.getTitle());
					//每题得分
					item.setStructureScore(s.getScore());
					//最小得分
					item.setStructureMin(s.getMin());
					//
					for(int index = 0; index < item.getCount(); index++){
						//添加试题索引
						item.setIndex(index);
						//添加试题集合
						this.items.add(item);
						//添加到分组下的试题集合
						cardItemModels.add(new AnswerCardItemModel(order, ItemStatus.None));
						//
						order += 1;
					}
				}
				//添加答题卡试题集合
				this.cardItemsMap.append(section, cardItemModels.toArray(new AnswerCardItemModel[0]));
				//
				section += 1;
			}
			//返回试题集合
			return this.items;
		}
		return null;
	}
	/*
	 * 加载考试时长(异步线程调用)。
	 * @see com.examw.test.dao.PaperDataDelegate#timeOfPaperView()
	 */
	@Override
	public int timeOfPaperView() throws Exception {
		Log.d(TAG, "重载加载考试时长...");
		final PaperModel model = this.getPaperModel();
		if(model != null){
			return model.getTime();
		}
		return super.timeOfPaperView();
	}
	/*
	 *  更新收藏记录(异步线程调用)。
	 * @see com.examw.test.dao.PaperDataDelegate#updateFavorite(com.examw.test.model.PaperItemModel)
	 */
	@Override
	public boolean updateFavorite(PaperItemModel itemModel) throws Exception {
		Log.d(TAG, "更新试题收藏:" + this.createItemId(itemModel));
		final PaperModel model = this.getPaperModel();
		if(model != null && itemModel != null){
			return this.getDao().updateFavoriteWithPaper(model.getId(), itemModel);
		}
		return false;
	}
	/*
	 * 加载答题卡数据(异步线程调用)。
	 * @see com.examw.test.dao.PaperDataDelegate#loadAnswerCardData(java.util.List, android.util.SparseArray)
	 */
	@Override
	public void loadAnswerCardData(final List<AnswerCardSectionModel> cardSections,final SparseArray<AnswerCardItemModel[]> cardSectionItems) throws Exception {
		Log.d(TAG, "加载答题卡数据...");
		if(this.cardSections != null && this.cardItemsMap != null){
			//答题卡分组
			if(cardSections != null){
				cardSections.clear();
				cardSections.addAll(this.cardSections);
			}
			//分组试题数据
			if(cardSectionItems != null){
				cardSectionItems.clear();
				//循环
				for(int i = 0; i < this.cardItemsMap.size(); i++){
					int key = this.cardItemsMap.keyAt(i);
					AnswerCardItemModel[] models = this.cardItemsMap.get(key);
					if(StringUtils.isNotBlank(this.paperRecordId) && models != null && models.length > 0 && this.items != null){
						 for(int k = 0; k < models.length; k++){
							 if(this.items.size() > models[k].getOrder()){
								 PaperItemModel itemModel = this.items.get(models[k].getOrder());
								 if(itemModel == null) continue;
								 models[k].status = this.getDao().exitRecord(this.paperRecordId, itemModel);
							 }
						 }
					}
					cardSectionItems.put(key, models);
				}
			}
		}
	}
}