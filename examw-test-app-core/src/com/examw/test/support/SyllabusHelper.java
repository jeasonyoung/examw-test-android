package com.examw.test.support;

import java.util.ArrayList;

import com.examw.test.R;
import com.examw.test.domain.Chapter;

/**
 * 
 * @author fengwei.
 * @since 2014年12月16日 上午8:54:54.
 */
public class SyllabusHelper {
	/**
	 * 传入我们的普通bean，转化为我们排序后的Node
	 * 
	 * @param datas
	 * @param defaultExpandLevel
	 * @return
	 */
	public static ArrayList<Chapter> getSortedNodes(ArrayList<Chapter> datas,int defaultExpandLevel) 
	{
		ArrayList<Chapter> result = new ArrayList<Chapter>();
		// 将用户数据转化为List<Node>
		// 拿到根节点
		// 排序以及设置Node间关系
		for (Chapter node : datas)
		{
			addNode(result, node, defaultExpandLevel, 1);
		}
		return result;
	}

	/**
	 * 过滤出所有可见的Node
	 * 
	 * @param nodes
	 * @return
	 */
	public static ArrayList<Chapter> filterVisibleNode(ArrayList<Chapter> nodes)
	{
		ArrayList<Chapter> result = new ArrayList<Chapter>();

		for (Chapter node : nodes)
		{
			// 如果为跟节点，或者上层目录为展开状态
			if (node.isRoot() || node.isParentExpand())
			{
				setNodeIcon(node);
				result.add(node);
			}
		}
		return result;
	}
//	private static void filterChildren(ArrayList<Chapter> result,Chapter parent)
//	{
//		if(!parent.getChildren().isEmpty())
//		{
//			for(Chapter child:parent.getChildren())
//			{
//				if (child.isParentExpand())
//				{
//					setNodeIcon(child);
//					result.add(child);
//				}
//			}
//		}
//	}
	
	
//	/**
//	 * 将我们的数据转化为树的节点
//	 * 
//	 * @param datas
//	 * @return
//	 */
//	private static ArrayList<Chapter> convertChapter(ArrayList<Chapter> datas)
//	{
//		/**
//		 * 设置Node间，父子关系;让每两个节点都比较一次，即可设置其中的关系
//		 */
//		for (int i = 0; i < datas.size(); i++)
//		{
//			Chapter n = datas.get(i);
//			for (int j = i + 1; j < datas.size(); j++)
//			{
//				Chapter m = datas.get(j);
//				if (m.getPid() == n.getChapterId())
//				{
//					n.getChildren().add(m);
//					m.setParent(n);
//				} else if (m.getChapterId() == n.getPid())
//				{
//					m.getChildren().add(n);
//					n.setParent(m);
//				}
//				Collections.sort(m.getChildren());
//			}
//		}
//		
//		// 设置图片
//		for (Chapter n : datas)
//		{
//			setNodeIcon(n);
//		}
//		Collections.sort(datas);
//		return datas;
//	}
	/**
	 * 把一个节点上的所有的内容都挂上去
	 */
	private static void addNode(ArrayList<Chapter> nodes, Chapter node,
			int defaultExpandLeval, int currentLevel)
	{
		nodes.add(node);
		if (defaultExpandLeval >= currentLevel)
		{
			node.setExpand(true);
		}

		if (node.isLeaf())
			return;
		for (int i = 0; i < node.getChildren().size(); i++)
		{
			addNode(nodes, node.getChildren().get(i), defaultExpandLeval,currentLevel + 1);
		}
	}

	/**
	 * 设置节点的图标
	 * 
	 * @param node
	 */
	private static void setNodeIcon(Chapter node)
	{
		if (node.getChildren().size() > 0 && node.isExpand())
		{
			node.setIcon(R.drawable.tree_ex);
		} else if (node.getChildren().size() > 0 && !node.isExpand())
		{
			node.setIcon(R.drawable.tree_ec);
		} else
			node.setIcon(-1);

	}
}
