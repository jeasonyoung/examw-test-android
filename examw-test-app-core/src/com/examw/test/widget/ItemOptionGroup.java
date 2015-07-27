package com.examw.test.widget;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.examw.test.R;
import com.examw.test.adapter.PaperItemsAdapter.PaperItemOptModel;

/**
 * 试题选项集合。
 * 
 * @author jeasonyoung
 * @since 2015年7月24日
 */
public class ItemOptionGroup extends LinearLayout implements View.OnClickListener {
	private static final String TAG = "ItemOptionGroup";
	private final List<String> optSelecteds;
	private final Context context;
	private boolean isMulty;
	/**
	 * 构造函数。
	 * @param context
	 * @param attrs
	 */
	public ItemOptionGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.d(TAG, "初始化...");
		this.context = context;
		this.optSelecteds = new ArrayList<String>();
		this.setMulty(false);
	}
	/**
	 * 设置是否为多选。
	 * @param isMulty 
	 *	  是否为多选。
	 */
	public void setMulty(boolean isMulty) {
		this.isMulty = isMulty;
	}
	/**
	 * 添加选项集合。
	 * @param options
	 * 选项集合。
	 */
	public synchronized void addOptions(List<PaperItemOptModel> options) {
		if(options == null || options.size() == 0){
			Log.d(TAG, "-------------------------没有选项需要添加....");
			return;
		}
		Log.d(TAG, "开始添加选项集合...");
		int count = this.getChildCount(), size = options.size(), index = 0;
		for(index = 0; index < size; index++){
			final PaperItemOptModel model = options.get(index);
			ItemOptionView option = (ItemOptionView)this.getChildAt(index);
			if(option == null){
				Log.d(TAG, "新增选项[" + index + "]..." + model);
				this.addOption(model, index);
			}else {
				Log.d(TAG, "重用选项[" + index + "]..."  + model);
				//option.loadOptionModel(model);
			}
		}
		//移除多余的选项
		if(index < count - 1){
			for(int i = index + 1; i < count; i++){
				Log.d(TAG, "移除多余选项...." + i);
				this.removeViewAt(i);
			}
		}
	}
	//添加选项
	private void addOption(PaperItemOptModel optModel, int pos){
		Log.d(TAG, "添加选项数据模型..." + optModel);
		ItemOptionView option =  (ItemOptionView)LayoutInflater.from(context).inflate(R.layout.ui_main_paper_items_option, null, false);
		//option.setGroup(this);
		//option.loadOptionModel(optModel);
		
		ViewGroup.LayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, 
				ViewGroup.MarginLayoutParams.WRAP_CONTENT);
		
		this.addView(option, pos, lp);
	}
	/*
	 * 选项点击事件处理。
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		Log.d(TAG, "选项被选中..." + v);
		if(v instanceof ItemOptionView){
		    String itemOptId = ((ItemOptionView)v).getOptId();
		    if(StringUtils.isNotBlank(itemOptId)){
		    	if(this.isMulty){//为多选选中处理。
		    		if(this.optSelecteds.contains(itemOptId)){//已选
		    			//取消选中
		    			this.optSelecteds.remove(itemOptId);
		    			//清除选中状态
		    			((ItemOptionView)v).clearSelected();
		    		}else {//未选
		    			this.optSelecteds.add(itemOptId);
					}
		    	}else {//单选选中处理
		    		if(this.optSelecteds.size() == 0){//第一个选中
						this.optSelecteds.add(itemOptId);
						return;
					}
					if(this.optSelecteds.contains(itemOptId)){//选中自己多次
						return;
					}
					//清空选中集合。
					this.optSelecteds.clear();
					//添加到选中集合。
		    		this.optSelecteds.add(itemOptId);
		    		//循环清除别的选中
		    		for(int i = 0; i < this.getChildCount(); i++){
		    			ItemOptionView option =  (ItemOptionView)this.getChildAt(i);
		    			if(option == null || StringUtils.equals(itemOptId, option.getOptId())) continue;
		    			//清除选中
		    			option.clearSelected();
		    		}
				}
		    }
		}
	}
	/**
	 * 获取选中的选项值。
	 * @return 选中的选项值。
	 */
	public String getSelectedValue() {
		if(this.optSelecteds != null && this.optSelecteds.size() > 0){
			return StringUtils.join(this.optSelecteds, ",");
		}
		return null;
	}
}