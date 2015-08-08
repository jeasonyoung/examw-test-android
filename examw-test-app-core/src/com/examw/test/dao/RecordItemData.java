package com.examw.test.dao;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.util.Log;

import com.examw.test.model.PaperModel;

/**
 * 做题记录试题数据接口实现。
 * 
 * @author jeasonyoung
 * @since 2015年7月31日
 */
public class RecordItemData extends BasePaperItemData {
	private static final long serialVersionUID = 1L;
	private static final String TAG = "RecordItemData";

	private final boolean isContinue;
	private final String paperId;
	private PaperModel paperModel;
	/**
	 * 构造函数。
	 * @param context
	 * @param paperId
	 * @param paperRecordId
	 * @param isContinue
	 */
	public RecordItemData(Context context, String paperId, String paperRecordId, boolean isContinue) {
		super(context, paperRecordId);
		Log.d(TAG, "初始化...");
		this.paperId = paperId;
		this.isContinue = isContinue;
	}
	/*
	 * 获取试卷数据模型。
	 * @see com.examw.test.dao.BasePaperItemData#getPaperModel()
	 */
	@Override
	protected synchronized PaperModel getPaperModel() {
		Log.d(TAG, "获取试卷数据模型..." + this.paperRecordId);
		if(this.paperModel == null && StringUtils.isNotBlank(this.paperId)){
			this.paperModel = this.getDao().loadPaper(this.paperId);
		}
		return this.paperModel;
	}
	/*
	 * 重载加载当前试题题序。
	 * @see com.examw.test.dao.PaperItemDataDelegate#currentOrderOfPaperView()
	 */
	@Override
	public int currentOrderOfPaperView() throws Exception {
		if(StringUtils.isNotBlank(this.paperRecordId) && this.isContinue && this.items != null && this.items.size() > 0){
			Log.d(TAG, "重载加载当前试卷题序...");
			//加载试卷记录的最新试题
			String lastItemId = this.getDao().loadNewItemAndIndex(this.paperRecordId);
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
}