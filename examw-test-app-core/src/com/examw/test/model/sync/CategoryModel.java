package com.examw.test.model.sync;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import com.examw.test.app.AppContext;
import com.examw.test.utils.PaperUtils;

import android.content.Context;
import android.util.Log;
/**
 * 考试分类数据模型。
 * 
 * @author jeasonyoung
 * @since 2015年6月26日
 */
public class CategoryModel implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final String TAG = "Category";
	private String id,code,name,abbr;
	private List<ExamModel> exams;
	/**
	 * 获取分类ID。
	 * @return 分类ID。
	 */
	public String getId() {
		return id;
	}
	/**
	 * 设置分类ID。
	 * @param id 
	 *	  分类ID。
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * 获取分类代码。
	 * @return 分类代码。
	 */
	public String getCode() {
		return code;
	}
	/**
	 * 设置分类代码。
	 * @param code 
	 *	  分类代码。
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * 获取分类名称。
	 * @return 分类名称。
	 */
	public String getName() {
		return name;
	}
	/**
	 * 设置分类名称。
	 * @param name 
	 *	  分类名称。
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 获取分类EN简称。
	 * @return 分类名称。
	 */
	public String getAbbr() {
		return abbr;
	}
	/**
	 * 设置分类名称。
	 * @param abbr 
	 *	  分类名称。
	 */
	public void setAbbr(String abbr) {
		this.abbr = abbr;
	}
	/**
	 * 获取考试集合。
	 * @return 考试集合。
	 */
	public List<ExamModel> getExams() {
		return exams;
	}
	/**
	 * 设置考试集合。
	 * @param exams 
	 *	  考试集合。
	 */
	public void setExams(List<ExamModel> exams) {
		this.exams = exams;
	}
	/*
	 * 重载。
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return StringUtils.join(new String[]{
				this.getId(),
				this.getCode(),
				this.getName(),
				this.getAbbr()
		}, ",");
	}
	
	//创建本地存储文件名。
	private static String createLocalFileName(){
		//日期格式化
		final SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMddHH", Locale.getDefault());
		//本地文件名
		return String.format("CategoriesLocalData_%s.json", dtFormat.format(new Date()));
	}
	/**
	 * 从本地文件中加载数据集合。
	 * @return
	 */
	public static List<CategoryModel> categoriesFromLocal(){
		try {
			Log.d(TAG, "从本地文件中加载考试分类数据集合...");
			//获取应用上下文
			final Context context = AppContext.getContext();
			//本地存储文件
			final File localFile = new File(context.getFilesDir(), createLocalFileName());
			Log.d(TAG, "加载本地存储路径:" + localFile.getAbsolutePath());
			//文件是否存在
			if(localFile.exists()){
				final CategoryModel[] categories = PaperUtils.<CategoryModel[]>fromJSON(CategoryModel[].class, new FileReader(localFile));
				if(categories != null && categories.length > 0){
					return Arrays.<CategoryModel>asList(categories);
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "从本地文件中加载考试分类异常:" + e.getMessage(), e);
		}
		return null;
	}
	/**
	 * 保存数据到本地。
	 * @param categories
	 * @return
	 */
	public static boolean saveLocal(List<CategoryModel> categories){
		try {
			Log.d(TAG, "准备将数据保存到本地...");
			if(categories != null && categories.size() > 0){
				//获取应用上下文
				final Context context = AppContext.getContext();
				//本地存储文件
				final File localFile = new File(context.getFilesDir(), createLocalFileName());
				Log.d(TAG, "本地存储路径:" + localFile.getAbsolutePath());
				final FileWriter writer =  new FileWriter(localFile,false);
				PaperUtils.<CategoryModel[]>toJSON(categories.toArray(new CategoryModel[0]), writer);
				writer.close();
				return true;
			}
		} catch (Exception e) {
			Log.e(TAG, "保存数据到本地时异常:" + e.getMessage(), e);
		}
		return false;
	}
}