package com.examw.test.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
/**
 * 试题数据模型。
 * 
 * @author jeasonyoung
 * @since 2015年6月27日
 */
public class PaperItemModel implements Serializable {
	private static final long serialVersionUID = 1L;
	@Expose
	private String id,content,answer,analysis;
	@Expose
	private Integer type,level,orderNo,count;
	@Expose
	private List<PaperItemModel> children;
	
	private String structureId,structureTitle,itemRecordId,paperRecordId;
	private Integer index;
	private Float structureScore,structureMin;
	private List<String> itemContentImgUrls,itemAnalysisImgUrls;
	
	/**
	 * 获取试题ID。
	 * @return 试题ID。
	 */
	public String getId() {
		return id;
	}
	/**
	 * 设置试题ID。
	 * @param id 
	 *	 试题ID。
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * 获取试题类型。
	 * @return 试题类型。
	 */
	public Integer getType() {
		return type;
	}
	/**
	 * 设置试题类型。
	 * @param type 
	 *	  试题类型。
	 */
	public void setType(Integer type) {
		this.type = type;
	}
	/**
	 * 获取试题内容。
	 * @return 试题内容。
	 */
	public String getContent() {
		return content;
	}
	/**
	 * 设置试题内容。
	 * @param content 
	 *	  试题内容。
	 */
	public void setContent(String content) {
		if(StringUtils.isNotBlank(content)){
			//图片处理
			this.itemContentImgUrls = new ArrayList<String>();
			content = findAndReplaceImgPaths(content, this.itemContentImgUrls);
		}
		this.content = content;
	}
	/**
	 * 获取试题答案。
	 * @return 试题答案。
	 */
	public String getAnswer() {
		return answer;
	}
	/**
	 * 设置试题答案。
	 * @param answer 
	 *	  试题答案。
	 */
	public void setAnswer(String answer) {
		if(StringUtils.isNotBlank(analysis)){
			//图片处理
			this.itemAnalysisImgUrls = new ArrayList<String>();
			analysis = findAndReplaceImgPaths(analysis, this.itemAnalysisImgUrls);
		}
		this.answer = answer;
	}
	/**
	 * 获取试题解析。
	 * @return 试题解析。
	 */
	public String getAnalysis() {
		return analysis;
	}
	/**
	 * 设置试题解析。
	 * @param analysis 
	 *	  试题解析。
	 */
	public void setAnalysis(String analysis) {
		this.analysis = analysis;
	}
	/**
	 * 获取难度值。
	 * @return 难度值。
	 */
	public Integer getLevel() {
		return level;
	}
	/**
	 * 设置难度值。
	 * @param level 
	 *	  难度值。
	 */
	public void setLevel(Integer level) {
		this.level = level;
	}
	/**
	 * 获取试题排序号。
	 * @return 试题排序号。
	 */
	public Integer getOrderNo() {
		return orderNo;
	}
	/**
	 * 设置试题排序号。
	 * @param orderNo 
	 *	  试题排序号。
	 */
	public void setOrderNo(Integer orderNo) {
		this.orderNo = orderNo;
	}
	/**
	 * 获取包含试题总数。
	 * @return 包含试题总数。
	 */
	public Integer getCount() {
		return count;
	}
	/**
	 * 设置包含试题总数。
	 * @param count 
	 *	  包含试题总数。
	 */
	public void setCount(Integer count) {
		this.count = count;
	}
	
	/**
	 * 获取所属试卷结构ID。
	 * @return 所属试卷结构ID。
	 */
	public String getStructureId() {
		return structureId;
	}
	/**
	 * 设置所属试卷结构ID。
	 * @param structureId 
	 *	  所属试卷结构ID。
	 */
	public void setStructureId(String structureId) {
		this.structureId = structureId;
	}
	/**
	 * 获取所属试卷结构名称。
	 * @return 所属试卷结构名称。
	 */
	public String getStructureTitle() {
		return structureTitle;
	}
	/**
	 * 设置所属试卷结构名称。
	 * @param structureTitle 
	 *	  所属试卷结构名称。
	 */
	public void setStructureTitle(String structureTitle) {
		this.structureTitle = structureTitle;
	}
	/**
	 * 获取每题分数。
	 * @return 每题分数。
	 */
	public Float getStructureScore() {
		return structureScore;
	}
	/**
	 * 设置每题分数。
	 * @param structureScore 
	 *	  每题分数。
	 */
	public void setStructureScore(Float structureScore) {
		this.structureScore = structureScore;
	}
	/**
	 * 获取每题最少得分。
	 * @return 每题最少得分。
	 */
	public Float getStructureMin() {
		return structureMin;
	}
	/**
	 * 设置每题最少得分。
	 * @param structureMin 
	 *	  每题最少得分。
	 */
	public void setStructureMin(Float structureMin) {
		this.structureMin = structureMin;
	}
	/**
	 * 获取试题记录ID。
	 * @return 试题记录ID。
	 */
	public String getItemRecordId() {
		return itemRecordId;
	}
	/**
	 * 设置试题记录ID。
	 * @param itemRecordId 
	 *	  试题记录ID。
	 */
	public void setItemRecordId(String itemRecordId) {
		this.itemRecordId = itemRecordId;
	}
	/**
	 * 获取所属试卷记录ID。
	 * @return 所属试卷记录ID。
	 */
	public String getPaperRecordId() {
		return paperRecordId;
	}
	/**
	 * 设置所属试卷记录ID。
	 * @param paperRecordId 
	 *	  所属试卷记录ID。
	 */
	public void setPaperRecordId(String paperRecordId) {
		this.paperRecordId = paperRecordId;
	}
	/**
	 * 获取试题内容图片Urls。
	 * @return 试题内容图片Urls。
	 */
	public List<String> getItemContentImgUrls() {
		return itemContentImgUrls;
	}
	/**
	 * 获取试题解析图片集合。
	 * @return 试题解析图片集合。
	 */
	public List<String> getItemAnalysisImgUrls() {
		return itemAnalysisImgUrls;
	}
	/**
	 * 获取试题索引。
	 * @return 试题索引。
	 */
	public Integer getIndex() {
		return index;
	}
	/**
	 * 设置试题索引。
	 * @param index 
	 *	  试题索引。
	 */
	public void setIndex(Integer index) {
		this.index = index;
	}
	/*
	 * 重载JSON字符串。
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
				.serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss").setPrettyPrinting().create();
		return gson.toJson(this);
	}
	
	//查找并替换图片路径
	private static String findAndReplaceImgPaths(String content, List<String> outList){
		if(StringUtils.isNotBlank(content)){ 
			Pattern regex = Pattern.compile("(<[img|IMG].+?[/]?>)");
			Matcher m = regex.matcher(content);
			if(m.find()){
				String img = m.group(1);
				content = m.replaceFirst("");
				if(StringUtils.isBlank(img)){
					return content;
				}
				Pattern urlRegex = Pattern.compile("[src|SRC]=\"(.+?)\"");
				m = urlRegex.matcher(img);
				if(m.find()){
					String imgUrl = m.group(1);
					if(StringUtils.isNotBlank(imgUrl) && outList != null){
						outList.add(imgUrl);
					}
					return content;
				}
			}
		}
		return content;
	}
	
	/**
	 *  加载题型名称。
	 * @param itemType
	 * @return
	 */
	public static String loadItemTypeName(int itemType){
		if(itemType > 0){
			ItemType type = ItemType.values()[itemType - 1];
			if(type != null){
				return type.getName();
			}
		}
		return null;
	}
	/**
	 * 加载判断题答案名称。
	 * @param itemJudgeAnswer
	 * @return
	 */
	public static String loadItemJudgeAnswerName(int itemJudgeAnswer){
		ItemJudgeAnswer answer = ItemJudgeAnswer.values()[itemJudgeAnswer];
		if(answer != null){
			return answer.getName();
		}
		return null;
	}
	
	/**
	 * 试题题型枚举。
	 * 
	 * @author jeasonyoung
	 * @since 2015年6月27日
	 */
	public enum ItemType{
		/**
		 * 单选。
		 */
		Single("单选",0x01),
		/**
		 * 多选。
		 */
		Multy("多选",0x02),
		/**
		 * 不定向选择。
		 */
		Uncertain("不定向选择",0x03),
		/**
		 * 判断题。
		 */
		Judge("判断题",0x04),
		/**
		 * 问答题。
		 */
		Qanda("问答题", 0x05),
		/**
		 * 共享题干题。
		 */
		ShareTitle("共享题干题", 0x06),
		/**
		 * 共享答案题。
		 */
		ShareAnswer("共享答案题", 0x07);
		
		private int value;
		private String name;
		/**
		 * 构造函数。
		 * @param name
		 * @param value
		 */
		private ItemType(String name,int value){
			this.name = name;
			this.value = value;
		}
		/**
		 * 获取题型名称。
		 * @return 题型名称。
		 */
		public String getName() {
			return name;
		}
		/**
		 * 获取题型值。
		 * @return 题型值。
		 */
		public int getValue() {
			return value;
		}
		/*
		 * 重载。
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			return String.format(Locale.getDefault(), "[%1$d.%2$s]", this.value, this.name);
		}
	}
	/**
	 * 判断题答案枚举。
	 * 
	 * @author jeasonyoung
	 * @since 2015年6月27日
	 */
	public enum ItemJudgeAnswer{
		/**
		 * 错误。
		 */
		Wrong("错误", 0x00),
		/**
		 * 正确。
		 */
		Right("正确", 0x01);
		
		private int value;
		private String name;
		/**
		 * 构造函数。
		 * @param name
		 * @param value
		 */
		private ItemJudgeAnswer(String name,int value){
			this.name = name;
			this.value = value;
		}
		/**
		 * 获取题型名称。
		 * @return 题型名称。
		 */
		public String getName() {
			return name;
		}
		/**
		 * 获取题型值。
		 * @return 题型值。
		 */
		public int getValue() {
			return value;
		}
		/*
		 * 重载。
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			return String.format(Locale.getDefault(), "[%1$d.%2$s]", this.value, this.name);
		}
	}
}