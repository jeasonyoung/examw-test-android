package com.examw.test.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.examw.test.R;
import com.examw.test.domain.Chapter;
import com.examw.test.ui.ChapterDetailActivity;

/**
 * 章节折叠适配器
 * 
 * @author fengwei.
 * @since 2014年12月18日 下午2:18:16.
 */
public class ChapterExpendableListAdatper extends BaseExpandableListAdapter {
	private Context context;
	private ArrayList<Chapter> groups;

	public ChapterExpendableListAdatper(Context context,
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
	
	@SuppressWarnings("deprecation")
	public ExpandableListView getExpandableListView() {
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
		ExpandableListView superTreeView = new ExpandableListView(context);
		superTreeView.setLayoutParams(lp);
		superTreeView.setGroupIndicator(null); //去掉默认样式
		superTreeView.setChildDivider(context.getResources().getDrawable(R.drawable.topic_driver));
		superTreeView.setDividerHeight(1);
		return superTreeView;
	}
	
	// 获得指定子项的view组件
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		final ExpandableListView treeView = getExpandableListView();
		final ChapterChildExpendableListAdatper childAdatper = new ChapterChildExpendableListAdatper(this.context,null);
		ArrayList<Chapter> chapters = new ArrayList<Chapter>();
		final Chapter node = groups.get(groupPosition).getChildren().get(childPosition);
		chapters.add(node);
		childAdatper.updateChapters(chapters);
		treeView.setAdapter(childAdatper);
		//关键点：取得选中的二级树形菜单的父子节点,结果返回给外部回调函数
		treeView.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				if(node.getChildren().isEmpty())
				{
					Intent intent = new Intent(context,ChapterDetailActivity.class);
					intent.putExtra("chapterId", node.getChapterId());
					context.startActivity(intent);
					return true; //不展开
				}
				return false; //展开
				
			}
		});
		treeView.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				Intent intent = new Intent(context,ChapterDetailActivity.class);
				intent.putExtra("chapterPid", node.getChapterId());
				intent.putExtra("chapterName", node.getChildren().get(childPosition).getTitle());
				intent.putExtra("chapterId", node.getChildren().get(childPosition).getChapterId());
				context.startActivity(intent);
				return false;
			}
		});
		
		/**
		 * 关键点：第二级菜单展开时通过取得节点数来设置第三级菜单的大小
		 */
		treeView.setOnGroupExpandListener(new OnGroupExpandListener() {
			@Override
			public void onGroupExpand(int groupPosition) {
				
				AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						(node.getChildren().size()+1)*ChapterChildExpendableListAdatper.ItemHeight + 10);
				treeView.setLayoutParams(lp);
			}
		});
		
		/**
		 * 第二级菜单回收时设置为标准Item大小
		 */
		treeView.setOnGroupCollapseListener(new OnGroupCollapseListener() {
			@Override
			public void onGroupCollapse(int groupPosition) {
				
				AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ChapterChildExpendableListAdatper.ItemHeight);
				treeView.setLayoutParams(lp);
			}
		});
		treeView.setPadding(ChapterChildExpendableListAdatper.PaddingLeft, 0, 0, 0);
		return treeView;
//		ViewHolder holder = null;
//		if (convertView == null) {
//			convertView = LayoutInflater.from(context).inflate(
//					R.layout.item_choose_subject_list, null);
//			holder = new ViewHolder();
//			holder.txt = (TextView) convertView.findViewById(R.id.list_title);
//			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
//					RelativeLayout.LayoutParams.WRAP_CONTENT,
//					RelativeLayout.LayoutParams.WRAP_CONTENT); // , 1是可选写的
//			lp.setMargins(15, 0, 0, 0);
//			holder.txt.setLayoutParams(lp);
//			convertView.setTag(holder);
//		} else {
//			holder = (ViewHolder) convertView.getTag();
//		}
//		holder.txt.setText(getChild(groupPosition, childPosition).toString());
//		return convertView;
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
		//convertView = LayoutInflater.from(context).inflate(R.layout.item_choose_subject_list, null);
		convertView = LayoutInflater.from(context).inflate(R.layout.item_choose_subject_list, parent, false);
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
