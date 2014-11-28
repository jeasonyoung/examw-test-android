package com.examw.test.support;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * 返回按钮的事件
 * @author fengwei.
 * @since 2014年11月28日 下午5:11:53.
 */
public class ReturnBtnClickListener implements OnClickListener {
	private Activity context;
	public ReturnBtnClickListener(Activity context) {
		this.context = context;
	}
	@Override
	public void onClick(View v) {
		if(context!=null)
		{
			context.finish();
			context=null;
		}
	}
}