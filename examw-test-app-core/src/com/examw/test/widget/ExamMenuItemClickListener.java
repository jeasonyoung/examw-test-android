package com.examw.test.widget;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 
 * @author fengwei.
 * @since 2014年12月9日 上午9:22:08.
 */
public abstract class ExamMenuItemClickListener implements OnItemClickListener{
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id){
		switch (position) {
		case 0:
			restart();
			break;
		case 1:
			// 答题卡,选题
			answerCard();
			break;
		case 2:
			// 设置
			setting();
			break;
		}
	}
	public abstract void restart();
	public abstract void answerCard();
	public abstract void setting();
}
