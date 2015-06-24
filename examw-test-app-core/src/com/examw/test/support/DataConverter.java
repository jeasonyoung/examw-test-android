package com.examw.test.support;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import com.examw.test.app.AppConstant;
import com.examw.test.app.AppContext;
import com.examw.test.daonew.FavoriteDao;
import com.examw.test.domain.FavoriteItem;
import com.examw.test.domain.ItemRecord;
import com.examw.test.domain.PaperRecord;
import com.examw.test.model.ItemInfo;
import com.examw.test.model.StructureInfo;
import com.examw.test.model.StructureItemInfo;
import com.examw.test.model.UserItemFavoriteInfo;
import com.examw.test.model.UserItemRecordInfo;
import com.examw.test.model.UserPaperRecordInfo;

/**
 * 数据解析
 * 
 * @author fengwei.
 * @since 2014年12月3日 下午3:51:02.
 */
public class DataConverter {
	private static String userName;
	private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);
	private static final SimpleDateFormat formatter_upload = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ",Locale.CHINA);
	// 查询一个接一个的试题
	public static ArrayList<StructureItemInfo> findItems(
			List<StructureInfo> structures, ArrayList<ItemRecord> itemRecords,
			String username) {
		userName = username;
		ArrayList<StructureItemInfo> result = new ArrayList<StructureItemInfo>();
		if (structures == null || structures.size() == 0)
			return result;
		for (StructureInfo s : structures) {
			if (s == null)
				continue;
			getStructureItems(result, s, itemRecords);
			s.setItems(null); // 将大题的items置为空
		}
		return result;
	}

	private static void getStructureItems(ArrayList<StructureItemInfo> result,
			StructureInfo info, ArrayList<ItemRecord> itemRecords) {
		if (info.getChildren() != null && info.getChildren().size() > 0) {
			for (StructureInfo child : info.getChildren()) {
				getStructureItems(result, child, itemRecords);
			}
		} else {
			TreeSet<StructureItemInfo> items = new TreeSet<StructureItemInfo>();
			if (info.getItems() == null || info.getItems().size() == 0)
				return;
			items.addAll(info.getItems());
			for (StructureItemInfo item : items) {
				if (item.getType().equals(AppConstant.ITEM_TYPE_SHARE_TITLE)) {
					result.addAll(getShareTitleSortedChildrenList(item,
							itemRecords));
				} else if (item.getType().equals(
						AppConstant.ITEM_TYPE_SHARE_ANSWER)) {
					result.addAll(getShareAnswerSortedChildrenList(item,
							itemRecords));
				} else {
					setUserAnswer(item, itemRecords);
					result.add(item);
				}
			}
		}
	}

	// 设置用户答案
	private static void setUserAnswer(StructureItemInfo item,
			ArrayList<ItemRecord> itemRecords) {
		if (itemRecords != null && itemRecords.size() > 0) {
			for (ItemRecord info : itemRecords) {
				if (item.getId().equalsIgnoreCase(info.getItemId())) {
					item.setUserAnswer(info.getAnswer()); // 设置用户答案
					item.setUserScore(info.getScore());
					item.setAnswerStatus(info.getStatus());
					item.setRecordId(info.getRecordId());
					break;
				}
			}
		}
		// 判断是否被收藏
		isCollected(item);
	}

	// 判断是否被收藏
	private static void isCollected(StructureItemInfo item) {
		if (userName == null)
			return;
		item.setIsCollected((FavoriteDao.isCollected(item.getId(), userName)));
	}

	/*
	 * 获取共享题干题按序子题集合
	 */
	private static ArrayList<StructureItemInfo> getShareTitleSortedChildrenList(
			StructureItemInfo item, ArrayList<ItemRecord> records) {
		ArrayList<StructureItemInfo> list = new ArrayList<StructureItemInfo>();
		TreeSet<StructureItemInfo> set = new TreeSet<StructureItemInfo>();
		set.addAll(item.getChildren());
		for (StructureItemInfo info : set) {
			info.setId(item.getId() + "#" + info.getId()); // 设置ID
			info.setStructureId(item.getStructureId()); // 设置大题ID
			info.setSubjectId(item.getSubjectId()); // 设置科目ID
			info.setParentContent(item.getContent()); // 设置材料题的题干
			setUserAnswer(info, records);// 设置用户答案
			list.add(info);
		}
		return list;
	}

	/*
	 * 获取共享答案题按序子题集合
	 */
	private static ArrayList<StructureItemInfo> getShareAnswerSortedChildrenList(
			StructureItemInfo item, ArrayList<ItemRecord> records) {
		ArrayList<StructureItemInfo> list = new ArrayList<StructureItemInfo>();
		TreeSet<StructureItemInfo> set = new TreeSet<StructureItemInfo>();
		set.addAll(item.getChildren());
		StructureItemInfo last = set.last(); // 最后一个
		String parentContent = getShareAnswerContent(item, set);
		set.clear();
		set.addAll(last.getChildren());
		for (StructureItemInfo info : set) {
			info.setPid(last.getPid());
			info.setId(item.getId() + "#" + info.getId()); // 设置ID
			info.setStructureId(item.getStructureId()); // 设置大题ID
			info.setSubjectId(item.getSubjectId()); // 设置科目ID
			info.setParentContent(parentContent);
			setUserAnswer(info, records);// 设置用户答案
			list.add(info);
		}
		return list;
	}

	// 获取共享答案题的题干
	private static String getShareAnswerContent(StructureItemInfo item,
			TreeSet<StructureItemInfo> set) {
		StringBuffer builder = new StringBuffer();
		builder.append(item.getContent());
		set.remove(set.last());
		int i = 65;
		for (StructureItemInfo s : set) {
			builder.append((char) (i++)).append(s.getContent())
					.append(" <br/>");
		}
		return builder.toString();
	}

	/**
	 * 获取已经做过的题
	 * 
	 * @param tOrF
	 * @return
	 */
	public static int getHasDone(int[] tOrF) {
		if (tOrF == null)
			return 0;
		int sum = 0;
		for (int i = 0; i < tOrF.length; i++) {
			if (tOrF[i] != AppConstant.ANSWER_NONE) {
				sum++;
			}
		}
		return sum;
	}

	/**
	 * 获取已经做对的题
	 * 
	 * @param tOrF
	 * @return
	 */
	public static int getRightNum(int[] tOrF) {
		int sum = 0;
		for (int i = 0; i < tOrF.length; i++) {
			if (tOrF[i] == AppConstant.ANSWER_RIGHT) {
				sum++;
			}
		}
		return sum;
	}
	public static ArrayList<UserPaperRecordInfo> convertPaperRecords(ArrayList<PaperRecord> list)
	{
		if(list == null || list.isEmpty()) return null;
		ArrayList<UserPaperRecordInfo> result = new ArrayList<UserPaperRecordInfo>();
		for(PaperRecord r:list)
		{
			UserPaperRecordInfo info = paperRecordConvert(r);
			if(info != null)
			{
				result.add(info);
			}
		}
		return result;
	}
	
	
	private static UserPaperRecordInfo paperRecordConvert(PaperRecord data) {
		if (data == null)
			return null;
		/**
		 * private String
		 * id,userId,userName,paperId,productId,paperName,paperTypeName
		 * ,subjectId; private Integer status,terminalCode,paperType,rightNum;
		 * private Long usedTime; private BigDecimal score; private Date
		 * createTime,lastTime; private Set<UserItemRecordInfo> items; </set>
		 */
		UserPaperRecordInfo info = new UserPaperRecordInfo();
		info.setProductId(AppContext.getMetaInfo("productId"));
		info.setUserId(data.getUserId());
		info.setId(data.getRecordId());
		info.setPaperType(data.getPaperType());
		info.setPaperId(data.getPaperId()); // 试卷Id
		info.setUsedTime(data.getUsedTime().longValue());
		info.setTerminalCode(Integer.valueOf(AppContext.getMetaInfo("terminalId")));
		info.setStatus(data.getStatus()); // 刚加入未完成
		info.setScore(new BigDecimal(data.getScore()));
		info.setRightNum(data.getRightNum());
		try{
			info.setCreateTime(formatter_upload.format(formatter.parse(data.getCreateTime())));
			info.setLastTime(formatter_upload.format(formatter.parse(data.getLastTime())));
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		ArrayList<ItemRecord> items = data.getItems();
		if (items != null && !items.isEmpty()) {
			Set<UserItemRecordInfo> set = new HashSet<UserItemRecordInfo>();
			for (ItemRecord itemData : items) {
				UserItemRecordInfo itemInfo = itemRecordConvert(itemData);
				if (itemInfo != null) {
					set.add(itemInfo);
				}
			}
			info.setItems(set);
		}
		return info;
	}

	private static UserItemRecordInfo itemRecordConvert(ItemRecord data) {
		if (data == null)
			return null;
		/**
		 * private String id,structureId,itemId,itemContent,answer; private
		 * Integer status,terminalCode; private Long usedTime; private
		 * BigDecimal score; private Date createTime,lastTime;
		 */
		UserItemRecordInfo info = new UserItemRecordInfo();
		// info.setId(id);
		info.setStructureId(data.getStructureId());
		info.setItemId(data.getItemId());
		info.setItemContent(data.getItemContent());
		info.setAnswer(data.getAnswer());
		info.setStatus(data.getStatus());
		info.setTerminalCode(Integer.valueOf(AppContext.getMetaInfo("terminalId")));
		info.setScore(data.getScore());
		try{
			info.setCreateTime(formatter_upload.format(formatter.parse(data.getCreateTime())));
			info.setLastTime(formatter_upload.format(formatter.parse(data.getLastTime())));
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return info;
	}

	public static String getItemMaterial(ItemInfo info) {
		switch (info.getType()) {
		case AppConstant.ITEM_TYPE_SHARE_ANSWER:
			TreeSet<ItemInfo> set = new TreeSet<ItemInfo>();
			set.addAll(info.getChildren());
			StringBuffer builder = new StringBuffer();
			builder.append(info.getContent());
			set.remove(set.last());
			int i = 65;
			for (ItemInfo s : set) {
				builder.append((char) (i++)).append(s.getContent())
						.append(" <br/>");
			}
			set.clear();
			set = null;
			return builder.toString();
		case AppConstant.ITEM_TYPE_SHARE_TITLE:
			return info.getContent(); 
		default:
			return null;
		}
	}
	
	public static ArrayList<UserItemFavoriteInfo> convertFavors(ArrayList<FavoriteItem> list)
	{
		if(list == null || list.isEmpty()) return null;
		ArrayList<UserItemFavoriteInfo> result = new ArrayList<UserItemFavoriteInfo>();
		for(FavoriteItem r:list)
		{
			UserItemFavoriteInfo info = favorConvert(r);
			if(info != null)
			{
				result.add(info);
			}
		}
		return result;
	}

	private static UserItemFavoriteInfo favorConvert(FavoriteItem r) {
		if(r == null)
		return null;
		UserItemFavoriteInfo info = new UserItemFavoriteInfo();
//		info.setUserId(r.getUserId());
		try{
//			info.setCreateTime(formatter_upload.format(formatter.parse(r.getCreateTime())));
		}catch(Exception e)
		{
			e.printStackTrace();
			info.setCreateTime(formatter_upload.format(new Date()));
		}
		info.setItemContent(r.getItemContent());
		info.setItemId(r.getItemId());
		info.setItemType(r.getItemType());
		info.setTerminalCode(Integer.valueOf(AppContext.getMetaInfo("terminalId")));
		info.setSubjectId(r.getSubjectId());
		return info;
	}
}
