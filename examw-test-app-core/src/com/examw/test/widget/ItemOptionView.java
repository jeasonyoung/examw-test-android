package com.examw.test.widget;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.examw.test.R;
import com.examw.test.support.ItemModelSupport.PaperItemOptModel;

/**
 * 试题选项。
 * 
 * @author jeasonyoung
 * @since 2015年7月24日
 */
public class ItemOptionView extends LinearLayout implements View.OnClickListener{
	private static final String TAG = "ItemOption";
	private TextView optView;
	private String optId;
	private boolean selected;
	/**
	 * 构造函数。
	 * @param context
	 * @param attrs
	 */
	public ItemOptionView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.d(TAG, "加载选项布局...");
		this.selected = false;
	}
	/**
	 * 获取选项ID。
	 * @return 选项ID。
	 */
	public String getOptId() {
		return optId;
	}
	/**
	 * 加载选项数据模型
	 * @param model
	 */
	public void loadModelData(PaperItemOptModel model){
		Log.d(TAG, "加载选项数据模型..." + model);
		//加载控件
		if(this.optView == null){
			this.optView = (TextView)this.findViewById(R.id.paper_item_option);
		}
		//加载数据
		if(model != null){
			this.optId = model.getId();
			this.optView.setText(Html.fromHtml(model.getContent()));
			this.optView.setOnClickListener(this);
			//清空选中
			this.clearSelected();
		}else {
			this.clean();
		}
	}
	/**
	 * 清空数据
	 */
	public void clean(){
		Log.d(TAG, "清空数据");
		this.optId = null;
		this.optView.setText("");
		this.selected = false;
	}
	/**
	 * 取消被选中。
	 */
	public void clearSelected(){
		if(!this.selected){
			return;
		}
		//TODO:更换选中图片
		//发起重绘UI
		//this.optView.invalidate();
		this.selected = false;
	}
	/*
	 * 重载选项被选中。
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		this.selected = true;
	}
}