package com.examw.test.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.GridView;

/**
 * 列表下GridView的扩展。
 * 
 * @author jeasonyoung
 * @since 2015年7月27日
 */
public class ListGridView extends GridView{
	private static final String TAG = "ListGridView";
	/**
	 * 构造函数。
	 * @param context
	 * @param attrs
	 */
	public ListGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.d(TAG, "初始化...");
	}
	/*
	 * 重载。
	 * @see android.widget.GridView#onMeasure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Log.d(TAG, "重载解决纵向滚动条问题...");
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST); 
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}