package com.examw.test.dao;

import java.lang.ref.WeakReference;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

import com.examw.test.model.PaperItemModel;

/**
 * 试卷数据监听者。
 * 
 * @author jeasonyoung
 * @since 2015年7月20日
 */
public abstract class PaperItemDataDelegate implements IPaperItemDataDelegate{
	private static final long serialVersionUID = 1L;
	private static final String TAG = "PaperDataDelegate";
	
	private final WeakReference<Context> refContext;
	
	protected List<PaperItemModel> items;
	protected List<AnswerCardSectionModel> cardSections;
	protected SparseArray<AnswerCardItemModel[]> cardItemsMap;
	protected final String paperRecordId;
	
	private PaperDao dao;
	/**
	 * 构造函数。
	 * @param context
	 */
	public PaperItemDataDelegate(Context context, String paperRecordId){
		Log.d(TAG, "初始化...");
		this.refContext = new WeakReference<Context>(context);
		this.paperRecordId = paperRecordId;
	}
	/**
	 * 获取数据操作对象
	 * @return
	 */
	protected PaperDao getDao(){
		synchronized (this) {
			//惰性加载
			if(this.dao == null){
				Log.d(TAG, "惰性加载数据操作对象...");
				this.dao = new PaperDao(this.refContext.get());
			}
			return this.dao;
		}
	}
	/**
	 * 计算试题记录ID。
	 * @param itemModel
	 * 试题模型。
	 * @return
	 * 试题记录ID(id + "$" + index)
	 */
	protected final String createItemId(PaperItemModel itemModel){
		if(itemModel != null){
			return itemModel.getId() + "$" + itemModel.getIndex();
		}
		return null;
	}
	/*
	 * 加载数据源(异步线程调用)。
	 * @see com.examw.test.dao.ItemDataDelegate#dataSourceOfPaperViews()
	 */
	@Override
    public abstract	List<PaperItemModel> dataSourceOfPaperViews() throws Exception;
    /*
     * 加载试题记录中的答案(异步线程调用)。
     * @see com.examw.test.dao.ItemDataDelegate#loadMyAnswer(com.examw.test.model.PaperItemModel)
     */
	@Override
	public String loadMyAnswer(PaperItemModel itemModel) throws Exception{
		Log.d(TAG, "加载试题答案:" + this.createItemId(itemModel));
		if(itemModel != null && StringUtils.isNotBlank(this.paperRecordId)){
			return this.getDao().loadRecodAnswers(this.paperRecordId, itemModel);
		}
		return null;
	}
	/*
	 * 加载答题卡数据(异步线程调用)。
	 * @see com.examw.test.dao.ItemDataDelegate#loadAnswerCardData(java.util.List, android.util.SparseArray)
	 */
	@Override
    public abstract void loadAnswerCardData(final List<AnswerCardSectionModel> cardSections, final SparseArray<AnswerCardItemModel[]> cardSectionItems) throws Exception;
	/*
	 * 加载当前试题题序(异步线程调用)。
	 * @see com.examw.test.dao.ItemDataDelegate#currentOrderOfPaperView()
	 */
	@Override
	public int currentOrderOfPaperView() throws Exception {
		return 0;
	}
	/*
	 * 获取考试时长(分钟)(异步线程调用)。
	 * @see com.examw.test.dao.ItemDataDelegate#timeOfPaperView()
	 */
	@Override
	public int timeOfPaperView() throws Exception {
		return -1;
	}
	/*
	 * 更新做题记录到SQL(异步线程中调用)
	 * @see com.examw.test.dao.ItemDataDelegate#updateRecordAnswer(com.examw.test.model.PaperItemModel, java.lang.String, int)
	 */
	@Override
	public void updateRecordAnswer(PaperItemModel itemModel, String myAnswers, int useTimes) throws Exception {
		Log.d(TAG, "更新做题记录...");
		if(itemModel != null && StringUtils.isNotBlank(this.paperRecordId) && StringUtils.isNotBlank(myAnswers)){
			this.getDao().addItemRecord(this.paperRecordId, itemModel, myAnswers, useTimes);
		}
	}
	/*
	 * 更新收藏记录(异步线程中被调用)。
	 * @see com.examw.test.dao.ItemDataDelegate#updateFavorite(com.examw.test.model.PaperItemModel)
	 */
	@Override
	public abstract boolean updateFavorite(PaperItemModel itemModel) throws Exception;
	/*
	 * 交卷处理(异步线程中被调用)。
	 * @see com.examw.test.dao.ItemDataDelegate#submitPaper(int, com.examw.test.dao.ItemDataDelegate.SubmitResultHandler)
	 */
	public void submitPaper(int useTimes, PaperItemDataDelegate.SubmitResultHandler handler) throws Exception {
		Log.d(TAG, "交卷处理...");
		if(StringUtils.isNotBlank(this.paperRecordId)){
			this.getDao().submit(this.paperRecordId, useTimes);
			if(handler != null){
				handler.hanlder(this.paperRecordId);
			}
		}
	}
}