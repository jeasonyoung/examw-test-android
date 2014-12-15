package com.examw.test.adapter;

import java.util.List;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.examw.test.model.SyllabusInfo;

/**
 * 
 * @author fengwei.
 * @since 2014年12月15日 下午4:53:06.
 */
public class SyllabusListAdapter extends BaseAdapter {

	protected Context mContext;
	/**
	 * 存储所有可见的Node
	 */
	protected List<SyllabusInfo> mNodes;
	protected LayoutInflater mInflater;
	/**
	 * 存储所有的Node
	 */
	protected List<SyllabusInfo> mAllNodes;

	/**
	 * 点击的回调接口
	 */
	private OnSyllabusNodeClickListener onSyllabusNodeClickListener;

	public interface OnSyllabusNodeClickListener {
		void onClick(SyllabusInfo node, int position);
	}

	public void OnSyllabusNodeClickListener(
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
	public SyllabusListAdapter(ListView mTree, Context context, List<SyllabusInfo> datas,
			int defaultExpandLevel) throws IllegalArgumentException,
			IllegalAccessException
	{
		mContext = context;
		/**
		 * 对所有的Node进行排序
		 */
		//TODO 
//		mAllNodes = TreeHelper.getSortedNodes(datas, defaultExpandLevel);
		/**
		 * 过滤出可见的Node
		 */
//		mNodes = TreeHelper.filterVisibleNode(mAllNodes);
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

	}

	/**
	 * 相应ListView的点击事件 展开或关闭某节点
	 * 
	 * @param position
	 */
	public void expandOrCollapse(int position)
	{
		SyllabusInfo n = mNodes.get(position);

		if (n != null)// 排除传入参数错误异常
		{
//			if (!n.isLeaf())
//			{
//				n.setExpand(!n.isExpand());
//				mNodes = TreeHelper.filterVisibleNode(mAllNodes);
//				notifyDataSetChanged();// 刷新视图
//			}
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
		SyllabusInfo node = mNodes.get(position);
		convertView = getConvertView(node, position, convertView, parent);
		// 设置内边距
//		convertView.setPadding(node.getLevel() * 30, 3, 3, 3);
		return convertView;
	}

	public View getConvertView(SyllabusInfo node, int position,
			View convertView, ViewGroup parent)
	{
		return null;
	}

}
