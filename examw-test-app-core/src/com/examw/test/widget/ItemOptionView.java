package com.examw.test.widget;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.examw.test.R;
import com.examw.test.model.PaperItemModel.ItemType;
import com.examw.test.support.ItemModelSupport.PaperItemOptModel;

/**
 * 试题选项。
 * 
 * @author jeasonyoung
 * @since 2015年7月24日
 */
public class ItemOptionView extends LinearLayout {
	private static final String TAG = "ItemOption";
	private TextView optView;
	/**
	 * 构造函数。
	 * @param context
	 * @param attrs
	 */
	public ItemOptionView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.d(TAG, "加载选项布局...");
	}
	/**
	 * 加载选项数据模型
	 * @param model
	 */
	public void loadModelData(PaperItemOptModel model, boolean displayAnswer){
		Log.d(TAG, "加载选项数据模型..." + model);
		//加载控件
		if(this.optView == null){
			this.optView = (TextView)this.findViewById(R.id.paper_item_option);
		}
		//加载数据
		if(model != null){
			//获取是否多选
			final boolean isMulty = (model.getItemType() == null ? false : model.getItemType() == ItemType.Multy);
			//当前选项是否选中
			final boolean selected =  StringUtils.isNotBlank(model.getMyAnswers()) ? model.getMyAnswers().indexOf(model.getId()) > -1 : false;
			//加载图标
			int icon_res_id = -1;
			if(displayAnswer){//显示答案
				//当前选项是否是正确答案
				final boolean isRight = StringUtils.isNotBlank(model.getRightAnswers()) ? model.getRightAnswers().indexOf(model.getId()) > -1 : false;
				//是否选中
				if(selected){//选中
					if(isRight){//正确
						icon_res_id = R.drawable.option_right;
					}else {//错误
						icon_res_id = R.drawable.option_error;
					}
				}else{//未选中
					if(isRight){//当前选项为正确答案
						icon_res_id = R.drawable.option_right;
					}else {//普通选项
						icon_res_id = isMulty ? R.drawable.option_multy_normal : R.drawable.option_single_normal;
					}
				}
			}else {//不显示答案
				if(selected){
					icon_res_id = isMulty ? R.drawable.option_multy_selected : R.drawable.option_single_selected;
				}else{
					icon_res_id = isMulty ? R.drawable.option_multy_normal : R.drawable.option_single_normal;
				}
			}
			//设置图标
			this.createOptionViewIcon(icon_res_id);
			//设置内容
			this.optView.setText(Html.fromHtml(model.getContent()));
		}
	}
	//设置选项图标
	@SuppressWarnings("deprecation")
	private void createOptionViewIcon(int resId){
		if(resId > -1 && this.optView != null && this.getResources() != null){
			final Drawable drawable = this.getResources().getDrawable(resId);
			if(drawable != null){
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
			}
			this.optView.setCompoundDrawables(drawable, null, null, null);
		}
	}
}