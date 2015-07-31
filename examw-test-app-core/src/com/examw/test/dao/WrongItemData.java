package com.examw.test.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

import com.examw.test.dao.PaperDao.ItemStatus;
import com.examw.test.model.PaperItemModel;
import com.examw.test.ui.MainWrongFragment.WrongOption;

/**
 * 错题模块试题数据接口实现。
 * 
 * @author jeasonyoung
 * @since 2015年7月30日
 */
public class WrongItemData extends PaperItemDataDelegate {
	private static final long serialVersionUID = 1L;
	private static final String TAG = "WrongItemData";
	
	private final String subjectCode;
	private final WrongOption option;
	
	/**
	 * 构造函数。
	 * @param context
	 * @param subjectCode
	 * @param option
	 */
	public WrongItemData(Context context,String subjectCode, WrongOption option){
		super(context, null);
		Log.d(TAG, "初始化...");
		this.subjectCode = subjectCode;
		this.option = option;
	}
	/*
	 * 加载试题数据(异步线程中调用)。
	 * @see com.examw.test.ui.PaperActivity.PaperDataDelegate#dataSourceOfPaperViews()
	 */
	@Override
	public List<PaperItemModel> dataSourceOfPaperViews() throws Exception {
		Log.d(TAG, "加载试题数据...");
		if(this.items != null && this.items.size() > 0) return this.items;
		//加载数据
		this.items = new ArrayList<PaperItemModel>();
		//
		switch(this.option){
			case Wrong:{//错题
				Log.d(TAG, "加载错题...");
				this.items = this.getDao().loadWrongItems(this.subjectCode);
				break;
			}
			case Favorite:{//收藏
				Log.d(TAG, "加载收藏...");
				this.items = this.getDao().loadFavoriteItems(this.subjectCode);
				break;
			}
		}
		if(this.items != null && this.items.size() > 0){
			//按题型分类
			SparseArray<List<PaperItemModel>> itemTypeArrays = new SparseArray<List<PaperItemModel>>();
			for(PaperItemModel itemModel : this.items){
				if(itemModel == null) continue;
				final int itemType = itemModel.getType();
				List<PaperItemModel> list = itemTypeArrays.get(itemType);
				if(list == null){
					list = new ArrayList<PaperItemModel>();
				}
				list.add(itemModel);
				itemTypeArrays.put(itemType, list);
			}
			//按题型排序
			final int size = itemTypeArrays.size();
			final int itemTypes [] = new int[size];
			for(int i = 0; i < size; i++){
				itemTypes[i] = itemTypeArrays.keyAt(i);
			}
			//排序
			Arrays.sort(itemTypes);
			//分组添加
			this.cardSections = new ArrayList<AnswerCardSectionModel>(size);
			this.cardItemsMap = new SparseArray<AnswerCardItemModel[]>(size);
			int order = 0;
			for(int i = 0; i < size; i++){
				//分组
				this.cardSections.add(new AnswerCardSectionModel(String.format("%1$d.%2$s", i+1, PaperItemModel.loadItemTypeName(itemTypes[i])), null));
				List<PaperItemModel> itemModels = itemTypeArrays.get(itemTypes[i]);
				if(itemModels != null && itemModels.size() > 0){
					final List<AnswerCardItemModel> cardItemModels = new ArrayList<AnswerCardItemModel>(itemModels.size());
					for(PaperItemModel itemModel : itemModels){
						if(itemModel == null)continue;
						cardItemModels.add(new AnswerCardItemModel(order,  ItemStatus.None));
						order += 1;
					}
					//
					this.cardItemsMap.put(i, cardItemModels.toArray(new AnswerCardItemModel[0]));
				}
			}
		}
		return this.items;
	}
	/*
	 * 加载试题答案(异步线程中调用)。
	 * @see com.examw.test.ui.PaperActivity.PaperDataDelegate#loadMyAnswer(com.examw.test.model.PaperItemModel)
	 */
	@Override
	public String loadMyAnswer(PaperItemModel itemModel) throws Exception {
		Log.d(TAG, "加载["+this.option+"]试题答案..."  + this.createItemId(itemModel));
		if(itemModel != null && StringUtils.isNotBlank(itemModel.getPaperRecordId())){
			//加载试题答案
			return this.getDao().loadRecodAnswers(itemModel.getPaperRecordId(), itemModel);
		}
		return null;
	}
	/*
	 * 更新做题记录(异步线程中调用)。
	 * @see com.examw.test.ui.PaperActivity.PaperDataDelegate#updateRecordAnswer(com.examw.test.model.PaperItemModel, java.lang.String, int)
	 */
	@Override
	public void updateRecordAnswer(PaperItemModel itemModel, String myAnswers, int useTimes) throws Exception {
		Log.d(TAG, "更新["+this.option+"]做题记录..." + this.createItemId(itemModel));
		if(itemModel != null && StringUtils.isNotBlank(itemModel.getPaperRecordId())){
			//更新做题记录
			this.getDao().addItemRecord(itemModel.getPaperRecordId(), itemModel, myAnswers, useTimes);
		}
	}
	/*
	 * 更新收藏(异步线程中调用)。
	 * @see com.examw.test.ui.PaperActivity.PaperDataDelegate#updateFavorite(com.examw.test.model.PaperItemModel)
	 */
	@Override
	public boolean updateFavorite(PaperItemModel itemModel) throws Exception {
		Log.d(TAG, "更新["+this.option+"]收藏..." + this.createItemId(itemModel));
		if(StringUtils.isNotBlank(this.subjectCode) && itemModel != null){
			//更新收藏
			return this.getDao().updateFavoriteWithSubject(this.subjectCode, itemModel);
		}
		return false;
	}
	/*
	 * 加载答题卡数据(异步线程中被调用)
	 * @see com.examw.test.ui.PaperActivity.PaperDataDelegate#loadAnswerCardData(java.util.List, android.util.SparseArray)
	 */
	@Override
	public void loadAnswerCardData(List<AnswerCardSectionModel> cardSections, SparseArray<AnswerCardItemModel[]> cardSectionItems) throws Exception {
		Log.d(TAG, "加载答题卡数据["+this.option+"]...");
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
					if(models != null && models.length > 0 && this.items != null){
						 for(int k = 0; k < models.length; k++){
							 if(this.items.size() > models[k].getOrder()){
								 PaperItemModel itemModel = this.items.get(models[k].getOrder());
								 if(itemModel == null || StringUtils.isBlank(itemModel.getPaperRecordId())){
									 continue;
								 }
								 models[k].status = this.getDao().exitRecord(itemModel.getPaperRecordId(), itemModel);
							 }
						 }
					}
					cardSectionItems.put(key, models);
				}
			}
		}
	}
}