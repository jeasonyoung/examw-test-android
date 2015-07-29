package com.examw.test.adapter;

import java.util.List;

import com.examw.test.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 试卷结果数据适配器。
 * 
 * @author jeasonyoung
 * @since 2015年7月29日
 */
public class PaperResultAdapter extends AboutAdapter {
	private static final String TAG = "PaperResultAdapter";
	/**
	 * 构造函数。
	 * @param context
	 * @param list
	 */
	public PaperResultAdapter(Context context, List<String> list) {
		super(context, list);
		Log.d(TAG, "初始化...");
	}
	/*
	 * 加载试卷结果
	 * @see com.examw.test.adapter.AboutAdapter#loadSectionItemView(android.view.LayoutInflater, android.view.ViewGroup)
	 */
	@Override
	protected View loadSectionItemView(LayoutInflater inflater, ViewGroup parent) {
		Log.d(TAG, "加载试卷结果行布局...");
		return inflater.inflate(R.layout.ui_main_paper_result_item, parent, false);
	}
	/*
	 * 创建试卷结果行对象。
	 * @see com.examw.test.adapter.AboutAdapter#createSectionItemHolder(android.view.View)
	 */
	@Override
	protected ViewSectionHolder createSectionItemHolder(View convertView) {
		Log.d(TAG, "创建试卷结果行对象...");
		ViewResultItemHolder holder = new ViewResultItemHolder();
		holder.loadViews(convertView);
		return holder;
	}
	//
	private class ViewResultItemHolder extends ViewSectionItemHolder{
		/*
		 * 加载试题Views。
		 * @see com.examw.test.adapter.AboutAdapter.ViewSectionItemHolder#loadViews(android.view.View)
		 */
		@Override
		public void loadViews(View view) {
			this.itemView = (TextView)view.findViewById(R.id.paper_result_item);
		}
	}
}