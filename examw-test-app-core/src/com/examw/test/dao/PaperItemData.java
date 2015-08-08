package com.examw.test.dao;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.util.Log;

import com.examw.test.model.PaperModel;
import com.examw.test.ui.PaperInfoActivity.StartType;

/**
 * 试卷接口。
 * 
 * @author jeasonyoung
 * @since 2015年7月21日
 */
public class PaperItemData extends BasePaperItemData{
	private static final long serialVersionUID = 1L;
	private static final String TAG = "PaperItemData";
	private final PaperModel paperModel;
	private final StartType paperStartType;
	/**
	 * 构造函数。
	 * @param context
	 * @param paperModel
	 * @param paperRecordId
	 * @param paperStartType
	 */
	public PaperItemData(Context context, PaperModel paperModel, String paperRecordId, StartType paperStartType){
		super(context, paperRecordId);
		Log.d(TAG, "初始化...");
		this.paperModel = paperModel;
		this.paperStartType = paperStartType;
	}
	/*
	 * 获取试卷数据模型。
	 * @see com.examw.test.dao.BasePaperItemData#getPaperModel()
	 */
	@Override
	protected PaperModel getPaperModel() {
		return this.paperModel;
	}
	/*
	 * 重载加载当前试题题序。
	 * @see com.examw.test.ui.PaperActivity.PaperDataListener#currentOrderOfPaperView()
	 */
	@Override
	public int currentOrderOfPaperView() throws Exception {
		if(StringUtils.isNotBlank(this.paperRecordId) && this.paperStartType == StartType.Continue && this.items != null && this.items.size() > 0){
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