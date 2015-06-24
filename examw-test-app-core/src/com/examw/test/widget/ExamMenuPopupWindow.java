package com.examw.test.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.examw.test.R;

/**
 * 考试答题设置
 * @author fengwei.
 * @since 2014年12月9日 上午8:50:30.
 */
public class ExamMenuPopupWindow extends PopupWindow {
	
	public ExamMenuPopupWindow(Context context) {
	    super(context);
	}
	
	public ExamMenuPopupWindow(Context context,ExamMenuItemClickListener itemClickListener,boolean restart) {
		super(context);
		View v = LayoutInflater.from(context).inflate(
				R.layout.pop_question_more_menu, null);
		ListView listView = (ListView) v
				.findViewById(R.id.question_menu_listView1);
		SampleAdapter adapter = new SampleAdapter(context);
		adapter.add(new SampleItem("答题卡", R.drawable.btn_answer_card));
		adapter.add(new SampleItem("答题设置", R.drawable.btn_settting));
		if(restart)
			adapter.add(new SampleItem("重新开始", R.drawable.btn_restart));
		// adapter.add(new SampleItem("笔记", R.drawable.btn_analyze));
		// adapter.add(new SampleItem("截图",R.drawable.btn_analyze));
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(itemClickListener);
		// 设置SelectPicPopupWindow的View  
        this.setContentView(v);  
        // 设置SelectPicPopupWindow弹出窗体的宽  
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);  
        // 设置SelectPicPopupWindow弹出窗体的高  
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);  
        // 设置SelectPicPopupWindow弹出窗体可点击  
        this.setFocusable(true);  
        this.setOutsideTouchable(true);  
        // 刷新状态  
        this.update();  
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作  
        this.setBackgroundDrawable(new ColorDrawable(0000000000));  
        // mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);  
        // 设置SelectPicPopupWindow弹出窗体动画效果  
        this.setAnimationStyle(R.style.AnimationLeftTopScale);
	}
	
	//菜单项
	private class SampleItem {
		public String tag;
		public int iconRes;

		public SampleItem(String tag, int iconRes) {
			this.tag = tag;
			this.iconRes = iconRes;
		}
	}
	
	//菜单项适配器
	public class SampleAdapter extends ArrayAdapter<SampleItem> {

		public SampleAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.pop_question_more_item, null);
				holder.title = (TextView) convertView
						.findViewById(R.id.menu_item);
				convertView.setTag(holder);

			}
			holder = (ViewHolder) convertView.getTag();
			holder.title.setCompoundDrawablesWithIntrinsicBounds(
					getItem(position).iconRes, 0, 0, 0);
			holder.title.setText(getItem(position).tag);
			return convertView;
		}

		class ViewHolder {
			TextView title;
		}
	}
}
