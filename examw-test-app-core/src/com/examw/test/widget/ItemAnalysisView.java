package com.examw.test.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.examw.test.R;
import com.examw.test.support.ItemModelSupport.PaperItemAnalysisModel;
import com.examw.test.support.ItemModelSupport.PaperItemOptModel;
import com.examw.test.utils.TextImgUtil;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 答案解析View
 * 
 * @author jeasonyoung
 * @since 2015年7月24日
 */
public class ItemAnalysisView extends LinearLayout{
	private static final String TAG = "ItemAnalysisView";
	private TextView rightView, resultView, analysisView;
	private final Pattern regex;
	/**
	 * 构造函数。
	 * @param context
	 * @param attrs
	 */
	public ItemAnalysisView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.d(TAG, "初始化...");
		this.regex = Pattern.compile(context.getString(R.string.main_paper_answer_regex));
	}
	/**
	 * 加载解析数据模型。
	 * @param analysisModel
	 * 解析数据模型。
	 */
	public void loadModelData(PaperItemAnalysisModel analysisModel){
		 Log.d(TAG, "加载解析数据模型... "  + analysisModel);
		//加载参考答案输入
		 if(this.rightView == null){
			this.rightView = (TextView)this.findViewById(R.id.paper_item_right);
		 }		
		//加载答案结果
		 if(this.resultView == null){
			this.resultView = (TextView)this.findViewById(R.id.paper_item_result);
		 }
		//加载答案解析
		if(this.analysisView == null){
			this.analysisView = (TextView)this.findViewById(R.id.paper_item_analysis_content);
		}
		 //设置是否显示
		 this.setVisibility((analysisModel == null) ? View.GONE :  View.VISIBLE);
		 //设置数据
		 if(analysisModel != null){
			 //参考答案/我的答案
			 final List<String> listRights = new ArrayList<String>(), listMyAnswer = new ArrayList<String>();
			 if(analysisModel.getOptions() != null){
				final List<PaperItemOptModel> optModels = analysisModel.getOptions();
				for(PaperItemOptModel optModel : optModels){
					if(optModel == null || StringUtils.isBlank(optModel.getId()) || StringUtils.isBlank(optModel.getContent())) continue;
					final String optId = optModel.getId();
					final String itemOrder = this.findItemOrder(optModel.getContent());
					if(StringUtils.isBlank(itemOrder)) continue;
					//参考答案
					if(StringUtils.isNotBlank(analysisModel.getRightAnswers()) && analysisModel.getRightAnswers().indexOf(optId) > -1){
						listRights.add(itemOrder);
					}
					//我的答案
					if(StringUtils.isNotBlank(analysisModel.getMyAnswers()) && analysisModel.getMyAnswers().indexOf(optId) > -1){
						listMyAnswer.add(itemOrder);
					}
				}
			 }
			 //设置参考答案
			 final String rightAnswer = StringUtils.join(listRights.toArray(new String[0])).toUpperCase(Locale.getDefault());
			 final String myAnswer = StringUtils.join(listMyAnswer.toArray(new String[0])).toUpperCase(Locale.getDefault());
			 this.rightView.setText(String.format(getContext().getString(R.string.main_paper_answer_right), rightAnswer));
			 //判断对错
			 this.resultView.setText(getContext().getString((StringUtils.isNotBlank(rightAnswer) &&  StringUtils.equalsIgnoreCase(rightAnswer, myAnswer)) ? R.string.main_paper_answer_my_right : R.string.main_paper_answer_my_wrong));
			 //设置解析
			 final String content = getContext().getString(R.string.main_paper_answer_analysis) + analysisModel.getContent();
			 TextImgUtil.textImageView(this.analysisView, content);
		 }
	}
	//查找题号
	private String findItemOrder(String content){
		String order = null;
		if(StringUtils.isNotBlank(content)){
			Matcher matcher = this.regex.matcher(content);
			if(matcher.find()){
				order =  matcher.group(0);
				if(StringUtils.isNotBlank(order) && order.endsWith(".")){
					order = order.substring(0, order.length() - 1);
				}
			}
		}
		return order;
	}
}