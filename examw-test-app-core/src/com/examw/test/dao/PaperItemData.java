package com.examw.test.dao;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

import com.examw.test.dao.PaperDao.ItemStatus;
import com.examw.test.model.PaperItemModel;
import com.examw.test.model.PaperModel;
import com.examw.test.model.PaperRecordModel;
import com.examw.test.model.PaperStructureModel;
import com.examw.test.ui.PaperActivity;
import com.examw.test.ui.PaperActivity.PaperDataDelegate;
import com.examw.test.ui.PaperInfoActivity.StartType;

/**
 * 试卷接口。
 * 
 * @author jeasonyoung
 * @since 2015年7月21日
 */
public class PaperItemData extends PaperActivity.PaperDataDelegate{
	private static final long serialVersionUID = 1L;
	private static final String TAG = "PaperItemData";
	
	private final WeakReference<Context> refContext;
	private final PaperModel paperModel;
	private final PaperRecordModel recordModel;
	private final StartType paperStartType;
	
	private List<PaperItemModel> items;
	private List<AnswerCardSectionModel> cardSections;
	private SparseArray<AnswerCardItemModel[]> cardItemsMap;
	private PaperDao dao;
	
	/**
	 * 构造函数。
	 * @param context
	 * @param paperModel
	 * @param recordModel
	 * @param paperStartType
	 */
	public PaperItemData(Context context, PaperModel paperModel, PaperRecordModel recordModel, StartType paperStartType){
		Log.d(TAG, "初始化...");
		this.refContext = new WeakReference<Context>(context);
		this.paperModel = paperModel;
		this.recordModel = recordModel;
		this.paperStartType = paperStartType;
	}
	/*
	 * 试题数据集合。
	 * @see com.examw.test.ui.PaperActivity.PaperDataListener#dataSourceOfPaperViews()
	 */
	@Override
	public List<PaperItemModel> dataSourceOfPaperViews() throws Exception{
		Log.d(TAG, "加载试题数据集合...");
		int total = 0;
		if(this.paperModel != null && (total = this.paperModel.getTotal()) > 0 && this.paperModel.getStructures() != null){
			//初始化
			this.items = new ArrayList<PaperItemModel>(total);
			total = this.paperModel.getStructures().size();
			if(total > 0){
				this.cardSections = new ArrayList<PaperDataDelegate.AnswerCardSectionModel>(total);
				this.cardItemsMap = new SparseArray<PaperDataDelegate.AnswerCardItemModel[]>(total);
			}
			//装载数据
			int section = 0, order = 0;
			for(PaperStructureModel s : this.paperModel.getStructures()){
				if(s == null || s.getItems() == null) continue;
				//创建答题卡分组数据模型
				this.cardSections.add(new AnswerCardSectionModel(s.getTitle(), s.getDescription()));
				//初始化分组下的试题集合
				List<PaperDataDelegate.AnswerCardItemModel> cardItemModels = new ArrayList<PaperDataDelegate.AnswerCardItemModel>(s.getItems().size());
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
	 * 重载加载当前试题题序。
	 * @see com.examw.test.ui.PaperActivity.PaperDataListener#currentOrderOfPaperView()
	 */
	@Override
	public int currentOrderOfPaperView() throws Exception {
		if(this.paperStartType == StartType.Continue && this.recordModel != null && items != null && items.size() > 0){
			Log.d(TAG, "重载加载当前试卷题序...");
			if(this.dao == null){
				this.dao = new PaperDao(this.refContext.get());
			}
			//加载试卷记录的最新试题
			String lastItemId = this.dao.loadNewItemAndIndex(this.recordModel.getId());
			if(StringUtils.isNotBlank(lastItemId)){
				//循环试题集合查找
				for(int i = 0; i < this.items.size(); i++){
					if(StringUtils.equals(lastItemId, this.createItemId(this.items.get(i)))){
						return i;
					}
				}
			}
		}
		return super.currentOrderOfPaperView();
	}
	/*
	 * 加载考试时长。
	 * @see com.examw.test.ui.PaperActivity.PaperDataListener#timeOfPaperView()
	 */
	@Override
	public int timeOfPaperView() throws Exception {
		Log.d(TAG, "重载加载考试时长...");
		if(this.paperModel != null){
			return this.paperModel.getTime();
		}
		return super.timeOfPaperView();
	}
	/*
	 * 加载试题记录中的答案。
	 * @see com.examw.test.ui.PaperActivity.PaperDataListener#loadMyAnswer(com.examw.test.model.PaperItemModel)
	 */
	@Override
	public String loadMyAnswer(PaperItemModel itemModel) throws Exception{
		Log.d(TAG, "加载试题答案:" + this.createItemId(itemModel));
		if(itemModel != null && this.recordModel != null){
			if(this.dao == null){
				this.dao = new PaperDao(this.refContext.get());
			}
			return this.dao.loadRecodAnswers(this.recordModel.getId(), itemModel);
		}
		return null;
	}
	/*
	 * 更新做题记录到数据库。
	 * @see com.examw.test.ui.PaperActivity.PaperDataListener#updateRecordAnswer(com.examw.test.model.PaperItemModel, java.lang.String, int)
	 */
	@Override
	public void updateRecordAnswer(PaperItemModel itemModel, String myAnswers, int useTimes) throws Exception{
		Log.d(TAG, "更新做题记录...");
		if(this.recordModel != null && itemModel != null && StringUtils.isNotBlank(myAnswers)){
			if(this.dao == null){
				this.dao = new PaperDao(this.refContext.get());
			}
			this.dao.addItemRecord(this.recordModel.getId(), itemModel, myAnswers, useTimes);
		}
	}
	/*
	 * 更新收藏记录。
	 * @see com.examw.test.ui.PaperActivity.PaperDataListener#updateFavorite(com.examw.test.model.PaperItemModel)
	 */
	@Override
	public boolean updateFavorite(PaperItemModel itemModel) throws Exception {
		Log.d(TAG, "更新试题收藏:" + this.createItemId(itemModel));
		if(this.paperModel != null && itemModel != null){
			if(this.dao == null){
				this.dao = new PaperDao(this.refContext.get());
			}
			return this.dao.updateFavoriteWithPaper(this.paperModel.getId(), itemModel);
		}
		return false;
	}
	/*
	 * 交卷处理。
	 * @see com.examw.test.ui.PaperActivity.PaperDataListener#submitPaper(int, com.examw.test.ui.PaperActivity.PaperDataListener.SubmitResultHandler)
	 */
	@Override
	public void submitPaper(int useTimes, SubmitResultHandler handler) throws Exception {
		Log.d(TAG, "交卷处理...");
		if(this.recordModel != null){
			if(this.dao == null){
				this.dao = new PaperDao(this.refContext.get());
			}
			this.dao.submit(this.recordModel.getId(), useTimes);
			if(handler != null){
				handler.hanlder(this.recordModel.getId());
			}
		}
	}
	/*
	 * 加载答题卡数据。
	 * @see com.examw.test.ui.PaperActivity.PaperDataListener#loadAnswerCardData(java.util.List, android.util.SparseArray)
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
				//
				if(this.dao == null){
					this.dao = new PaperDao(this.refContext.get());
				}
				//循环
				for(int i = 0; i < this.cardItemsMap.size(); i++){
					int key = this.cardItemsMap.keyAt(i);
					AnswerCardItemModel[] models = this.cardItemsMap.get(key);
					if(models != null && models.length > 0 && this.recordModel != null && this.items != null){
						 for(int k = 0; k < models.length; k++){
							 if(this.items.size() > models[k].getOrder()){
								 PaperItemModel itemModel = this.items.get(models[k].getOrder());
								 if(itemModel == null) continue;
								 models[k].status = this.dao.exitRecord(this.recordModel.getId(), itemModel);
							 }
						 }
					}
					cardSectionItems.put(key, models);
				}
			}
		}
	}
}