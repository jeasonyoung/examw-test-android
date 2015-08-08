package com.examw.test.model;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.util.Log;

/**
 * 试卷数据模型。
 * @author fengwei.
 * @since 2014年11月27日 下午3:56:12.
 */
public class PaperModel implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final String TAG = "PaperModel";
	private String id,name,description,sourceName,areaName;
	private Integer type,time,year,total,score;
	private List<PaperStructureModel> structures;
	
	/**
	 * JSON反序列化
	 * @param json
	 * @return
	 */
	public static PaperModel fromJSON(String json){
		Log.d(TAG, "JSON反序列化试卷对象...");
		if(StringUtils.isNotBlank(json)){
			Type type = new TypeToken<PaperModel>(){}.getType();
			Gson gson = new Gson();
			return  (PaperModel)gson.fromJson(json, type);
		}
		return null;
	}
	
	/**
	 * 获取试卷ID。
	 * @return 试卷ID。
	 */
	public String getId() {
		return id;
	}
	/**
	 * 设置试卷ID。
	 * @param id 
	 *	  试卷ID。
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * 获取试卷名称。
	 * @return 试卷名称。
	 */
	public String getName() {
		return name;
	}
	/**
	 * 设置试卷名称。
	 * @param name 
	 *	  试卷名称。
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 获取试卷描述信息。
	 * @return 试卷描述信息。
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * 设置试卷描述信息。
	 * @param description 
	 *	  试卷描述信息。
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * 获取试卷来源。
	 * @return 试卷来源。
	 */
	public String getSourceName() {
		return sourceName;
	}
	/**
	 * 设置试卷来源。
	 * @param sourceName 
	 *	  试卷来源。
	 */
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}
	/**
	 * 获取所属地区。
	 * @return 所属地区。
	 */
	public String getAreaName() {
		return areaName;
	}
	/**
	 * 设置所属地区。
	 * @param areaName 
	 *	  所属地区。
	 */
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	/**
	 * 获取试卷类型。
	 * @return 试卷类型。
	 */
	public Integer getType() {
		return type;
	}
	/**
	 * 设置试卷类型。
	 * @param type 
	 *	  试卷类型。
	 */
	public void setType(Integer type) {
		this.type = type;
	}
	/**
	 * 获取考试时长。
	 * @return 考试时长。
	 */
	public Integer getTime() {
		return time;
	}
	/**
	 * 设置考试时长。
	 * @param time 
	 *	  考试时长。
	 */
	public void setTime(Integer time) {
		this.time = time;
	}
	/**
	 * 获取使用年份。
	 * @return 使用年份。
	 */
	public Integer getYear() {
		return year;
	}
	/**
	 * 设置使用年份。
	 * @param year 
	 *	  使用年份。
	 */
	public void setYear(Integer year) {
		this.year = year;
	}
	/**
	 * 获取试题总数。
	 * @return 试题总数。
	 */
	public Integer getTotal() {
		return total;
	}
	/**
	 * 设置试题总数。
	 * @param total 
	 *	  试题总数。
	 */
	public void setTotal(Integer total) {
		this.total = total;
	}
	/**
	 * 获取试卷总分。
	 * @return 试卷总分。
	 */
	public Integer getScore() {
		return score;
	}
	/**
	 * 设置试卷总分。
	 * @param score 
	 *	  试卷总分。
	 */
	public void setScore(Integer score) {
		this.score = score;
	}
	/**
	 * 获取试卷结构。
	 * @return 试卷结构。
	 */
	public List<PaperStructureModel> getStructures() {
		return structures;
	}
	/**
	 * 设置试卷结构。
	 * @param structures 
	 *	  试卷结构。
	 */
	public void setStructures(List<PaperStructureModel> structures) {
		this.structures = structures;
	}
	/**
	 * 加载试卷类型名称。
	 * @param paperType
	 * 试卷类型值。
	 * @return
	 * 试卷类型名称。
	 */
	public static String loadPaperTypeName(int paperType){
		if(paperType > 0){
			PaperType type = PaperType.values()[paperType - 1];
			if(type != null){
				return type.getName();
			}
		}
		return null;
	}
	/**
	 * 试卷类型枚举。
	 * 
	 * @author jeasonyoung
	 * @since 2015年6月27日
	 */
	public enum PaperType{
		/**
		 * 历年真题。
		 */
		Real("历年真题", 0x01),
		/**
		 * 模拟试题。
		 */
		Simu("模拟试题",0x02),
		/**
		 * 预测试题。
		 */
		Forecas("预测试题", 0x03),
		/**
		 * 练习题。
		 */
		Practice("练习题", 0x04),
		/**
		 * 章节练习。
		 */
		Chapter("章节练习", 0x05),
		/**
		 * 每日一练。
		 */
		Daily("每日一练", 0x06);
		
		private String name;
		private int value;
		/**
		 * 构造函数。
		 * @param name
		 * @param value
		 */
		private PaperType(String name, int value){
			this.name = name;
			this.value = value;
		}
		/**
		 * 获取试卷类型枚举名称。
		 * @return 枚举名称。
		 */
		public String getName() {
			return name;
		}
		/**
		 * 获取试卷类型枚举值。
		 * @return 枚举值。
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