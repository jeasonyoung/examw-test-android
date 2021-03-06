package com.examw.test.widget;

import com.examw.test.R;
import com.examw.test.support.ItemModelSupport.PaperItemTitleModel;
import com.examw.test.utils.TextImgUtil;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 *  试题标题。
 * 
 * @author jeasonyoung
 * @since 2015年7月27日
 */
public class ItemTitleView extends LinearLayout {
	private static final String TAG = "ItemTitleView";
	private TextView titleView;
	/**
	 * 构造函数。
	 * @param context
	 * @param attrs
	 */
	public ItemTitleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.d(TAG, "初始化...");
	}
	/**
	 * 加载模型数据。
	 * @param model
	 */
	public void loadModelData(PaperItemTitleModel model){
		Log.d(TAG, "加载模型数据:" + model);
		//加载控件。
		if(this.titleView == null){
			this.titleView = (TextView)this.findViewById(R.id.paper_item_title);
			//装饰
			new ZoomTextView(this.titleView);
		}
		//加载数据。
		if(model != null){
			final String content = model.getOrder() > 0 ? model.getOrder() + "." + model.getContent() :  model.getContent();
			TextImgUtil.textImageView(this.titleView, content);
		}else{
			this.titleView.setText("");
		}
	}
}