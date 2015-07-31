package com.examw.test.widget;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.examw.test.R;
import com.examw.test.support.ItemModelSupport.PaperItemAnalysisModel;

/**
 * 答案解析View
 * 
 * @author jeasonyoung
 * @since 2015年7月24日
 */
public class ItemAnalysisView extends LinearLayout{
	private static final String TAG = "ItemAnalysisView";
	private TextView rightView, resultView, analysisView;
	/**
	 * 构造函数。
	 * @param context
	 * @param attrs
	 */
	public ItemAnalysisView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.d(TAG, "初始化...");
	}
	/**
	 * 加载解析数据模型。
	 * @param analysisModel
	 * 解析数据模型。
	 */
	public void loadModelData(PaperItemAnalysisModel analysisModel){
		 Log.d(TAG, "加载解析数据模型... "  + analysisModel);
		 if(this.rightView == null){
			//加载参考答案输入
			this.rightView = (TextView)this.findViewById(R.id.paper_item_right);
		 }		
		 if(this.resultView == null){
			//加载答案结果
			this.resultView = (TextView)this.findViewById(R.id.paper_item_result);
		 }		
		if(this.analysisView == null){
			//加载答案解析
			this.analysisView = (TextView)this.findViewById(R.id.paper_item_analysis_content);
		}
		 //设置是否显示
		 this.setVisibility((analysisModel == null) ? View.GONE :  View.VISIBLE);
		 //设置数据
		 if(analysisModel != null){
			 //设置参考答案
			 ///TODO:数据处理
			 this.rightView.setText("参考答案");
			 this.resultView.setText("判断对错.");
			 //设置解析
			 this.analysisView.setText(Html.fromHtml(analysisModel.getContent()));
		 }
	}
}