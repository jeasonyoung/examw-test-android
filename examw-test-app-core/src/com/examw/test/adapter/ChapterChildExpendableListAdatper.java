package com.examw.test.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.examw.test.R;
import com.examw.test.domain.Chapter;

/**
 * 章节折叠适配器
 * 
 * @author fengwei.
 * @since 2014年12月18日 下午2:18:16.
 */
public class ChapterChildExpendableListAdatper extends BaseExpandableListAdapter {
	public static final int ItemHeight = 48;
	public static final int PaddingLeft = 18;
	
	private Context context;
	private ArrayList<Chapter> groups;

	public ChapterChildExpendableListAdatper(Context context,
			ArrayList<Chapter> group) {
		this.context = context;
		this.groups = group;
	}

	// 获得指定组中的指定索引的子选项数据
	public Object getChild(int groupPosition, int childPosition) {
		try {
			return groups.get(groupPosition).getChildren().get(childPosition);
		} catch (Exception e) {
			return null;
		}
	}

	// 获得指定子项的ID
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}
	
	// 获得指定子项的view组件
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_choose_subject_list, null);
			holder = new ViewHolder();
			holder.txt = (TextView) convertView.findViewById(R.id.list_title);
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT); // , 1是可选写的
			lp.setMargins(15, 0, 0, 0);
			holder.txt.setLayoutParams(lp);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.txt.setText(getChild(groupPosition, childPosition).toString());
		return convertView;
	}

	// 取得指定组中所有子项的个数
	public int getChildrenCount(int groupPosition) {
		try {
			return groups.get(groupPosition).getChildren().size();
		} catch (Exception e) {
			return 0;
		}
	}

	// 取得指定组的数据
	public Object getGroup(int groupPosition) {
		return groups.get(groupPosition);
	}

	// 取得指定组的个数
	public int getGroupCount() {
		return groups.size();
	}

	// 取得指定索引的ID
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	// 取得指定组的View组件
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(context).inflate(
				R.layout.item_choose_subject_list, null);
		TextView txt = (TextView) convertView.findViewById(R.id.list_title);
		txt.setText(groups.get(groupPosition).getTitle());
		// txt.setTextSize(convertView.getResources().getDimension(R.dimen.text_medium_size));
		ImageView arrow = (ImageView) convertView.findViewById(R.id.arrow);
		if (isExpanded) {
			arrow.setImageResource(R.drawable.ic_arrow_down);
		} else {
			arrow.setImageResource(R.drawable.ic_arrow);
		}
		return convertView;
	}

	// 如果返回true表示子项和组的ID始终表示一个固定的组件对象
	public boolean hasStableIds() {
		return true;
	}

	// 判断指定的子选择项是否被选择
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	static class ViewHolder {
		TextView txt;
		ImageView icon;
	}
}
