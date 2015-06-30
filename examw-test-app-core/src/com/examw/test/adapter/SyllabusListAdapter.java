package com.examw.test.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.examw.test.R;
import com.examw.test.domain.Chapter;
import com.examw.test.support.SyllabusHelper;

/**
 * 大纲适配器
 * @author fengwei.
 * @since 2014年12月15日 下午4:53:06.
 */
public class SyllabusListAdapter extends BaseAdapter {

	protected Context mContext;
	/**
	 * 存储所有可见的Node
	 */
	protected ArrayList<Chapter> mNodes;
	protected LayoutInflater mInflater;
	/**
	 * 存储所有的Node
	 */
	protected ArrayList<Chapter> mAllNodes;

	/**
	 * 点击的回调接口
	 */
	private OnSyllabusNodeClickListener onSyllabusNodeClickListener;

	public interface OnSyllabusNodeClickListener {
		void onClick(Chapter node, int position);
	}

	public void setOnSyllabusNodeClickListener(
			OnSyllabusNodeClickListener onSyllabusNodeClickListener) {
		this.onSyllabusNodeClickListener = onSyllabusNodeClickListener;
	}
	
	/**
	 * 
	 * @param mTree
	 * @param context
	 * @param datas
	 * @param defaultExpandLevel
	 *            默认展开几级树
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public SyllabusListAdapter(ListView mTree, Context context, ArrayList<Chapter> datas) 
	{
		//long start = System.currentTimeMillis();
		mContext = context;
		/**
		 * 对所有的Node进行排序
		 */
//		mAllNodes = SyllabusHelper.getSortedNodes(datas, defaultExpandLevel);
		mAllNodes = SyllabusHelper.getSortedNodes(datas, 1);
		/**
		 * 过滤出可见的Node
		 */
		mNodes = SyllabusHelper.filterVisibleNode(mAllNodes);
		mInflater = LayoutInflater.from(context);

		/**
		 * 设置节点点击时，可以展开以及关闭；并且将ItemClick事件继续往外公布
		 */
		mTree.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				expandOrCollapse(position);

				if (onSyllabusNodeClickListener != null)
				{
					onSyllabusNodeClickListener.onClick(mNodes.get(position),
							position);
				}
			}

		});
		//LogUtil.d("章节适配器初始化耗时:"+(System.currentTimeMillis()-start));
	}

	/**
	 * 相应ListView的点击事件 展开或关闭某节点
	 * 
	 * @param position
	 */
	public void expandOrCollapse(int position)
	{
		Chapter n = mNodes.get(position);

		if (n != null)// 排除传入参数错误异常
		{
			if (!n.isLeaf())
			{
				n.setExpand(!n.isExpand());
				mNodes = SyllabusHelper.filterVisibleNode(mAllNodes);
				notifyDataSetChanged();// 刷新视图
			}
		}
	}
	
	@Override
	public int getCount()
	{
		return mNodes.size();
	}

	@Override
	public Object getItem(int position)
	{
		return mNodes.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		Chapter node = mNodes.get(position);
		convertView = getConvertView(node, position, convertView, parent);
		// 设置内边距
		convertView.setPadding(node.getLevel() * 30, 3, 3, 3);
		return convertView;
	}

	public View getConvertView(Chapter node, int position,
			View convertView, ViewGroup parent)
	{
		
		ViewHolder viewHolder = null;
		if (convertView == null)
		{
			convertView = mInflater.inflate(R.layout.chapter_list_item, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.icon = (ImageView) convertView
					.findViewById(R.id.id_treenode_icon);
			viewHolder.label = (TextView) convertView
					.findViewById(R.id.id_treenode_label);
			convertView.setTag(viewHolder);

		} else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (node.getIcon() == -1)
		{
			viewHolder.icon.setVisibility(View.INVISIBLE);
		} else
		{
			viewHolder.icon.setVisibility(View.VISIBLE);
			viewHolder.icon.setImageResource(node.getIcon());
		}

		viewHolder.label.setText(node.getTitle());
		
		
		return convertView;
	}

	private final class ViewHolder
	{
		ImageView icon;
		TextView label;
	}
}
