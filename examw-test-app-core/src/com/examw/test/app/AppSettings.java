package com.examw.test.app;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.examw.codec.digest.DigestUtils;
import com.google.gson.Gson;
/**
 * 应用设置。
 * 
 * @author jeasonyoung
 * @since 2015年6月25日
 */
public class AppSettings implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final String TAG = "AppSettings";
	private AppSettingsModel model;
	private SharedPreferences preferences;
	private boolean isDirty;
	/**
	 * 构造函数。
	 * @param model
	 * 应用设置数据模型。
	 */
	private AppSettings(AppSettingsModel model){
		this.model = model;
		this.isDirty = false;
	}
	/**
	 * 构造函数。
	 * @param examId
	 * 考试ID。
	 * @param examCode
	 * 考试代码。
	 * @param examName
	 * 考试名称。
	 */
	public AppSettings(String examId,String examCode,String examName){
		Log.d(TAG, String.format("初始化考试信息:%1$s,%2$s,%3$s", examId, examCode, examName));
		this.model = new AppSettingsModel();
		this.model.setExamId(examId);
		this.model.setExamCode(examCode);
		this.model.setExamName(examName);
		this.isDirty = true;
	}
	/**
	 * 添加产品。
	 * @param productId
	 * 产品ID。
	 * @param productName
	 * 产品名称
	 */
	public void addProduct(String productId, String productName){
		Log.d(TAG, String.format("添加产品:%1$s,%2$s", productId, productName));
		if(this.model == null)return;
		this.model.setProductId(productId);
		this.model.setProductName(productName);
		this.isDirty = true;
	}
	/**
	 * 获取所属考试ID。
	 * @return 所属考试ID。
	 */
	public String getExamId() {
		return (this.model == null) ? null : this.model.getExamId();
	}
	/**
	 * 获取所属考试代码。
	 * @return 所属考试代码。
	 */
	public String getExamCode() {
		return (this.model == null) ? null : this.model.getExamCode();
	}
	/**
	 * 获取所属考试名称。
	 * @return 所属考试名称。
	 */
	public String getExamName() {
		return  (this.model == null) ? null : this.model.getExamName();
	}
	/**
	 * 获取所属产品ID。
	 * @return 所属产品ID。
	 */
	public String getProductId() {
		return  (this.model == null) ? null : this.model.getProductId();
	}
	/**
	 * 获取所属产品名称。
	 * @return 所属产品名称。
	 */
	public String getProductName() {
		return (this.model == null) ? null : this.model.getProductName();
	}
	
	//加载默认首选项。
	private static SharedPreferences loadDefaultSharedPreferences(){
		try {
			Log.d(TAG, "加载默认首选项...");
			//获取当前上下文
			Context context = AppContext.getContext();
			if(context == null) return null;
			//获取默认首选项
			return PreferenceManager.getDefaultSharedPreferences(context);
		} catch (Exception e) {
			Log.e(TAG, "加载默认首选项发生异常:" + e.getMessage(), e);
		}
		return null;
	}
	/**
	 * 从配置中加载设置。
	 * @return 
	 * 设置。
	 */
	public static AppSettings loadSettingsDefaults(){
		String key = TAG;
		if(StringUtils.isNotBlank(key)){
			try {
				Log.d(TAG, "加载设置配置:" + key);
				//获取首选项
				SharedPreferences preferences = loadDefaultSharedPreferences();
				if(preferences == null){
					Log.d(TAG, "加载首选项失败!");
					return null;
				}
				if(preferences.contains(key)){
					String json = preferences.getString(key, null);
					//数据为空
					if(StringUtils.isBlank(json)){
						Log.d(TAG, "未能加载到数据:" + key);
						return null;
					}
					//进行json反序列化
					Gson gson = new Gson();
					AppSettingsModel settingsModel = gson.fromJson(json, AppSettingsModel.class);
					if(settingsModel != null){//反序列化成功
						//校验码对比
						String newCheckCode = settingsModel.createCheckCode();
						if(!StringUtils.equals(newCheckCode, settingsModel.getCheckCode())){
							Log.d(TAG, String.format("校验码不一致[%1$s/%2$s]", newCheckCode, settingsModel.getCheckCode()));
							return null;
						}
						Log.d(TAG, "设置加载成功!" + json);
						return new AppSettings(settingsModel);
					}
				}
				Log.d(TAG, "首选项中未设置配置!");
			} catch (Exception e) {
				Log.e(TAG, "加载设置配置发生异常:" + e.getMessage(), e);
			}
		}
		return null;
	}
	/**
	 * 校验是否有效。
	 * @return 
	 * 有效返回true.
	 */
	public boolean verification(){
		Log.d(TAG, "检验是否有效...");
		if(StringUtils.isBlank(this.getExamCode())){
			Log.d(TAG, "配置缺少=>考试代码!");
			return false;
		}
		if(StringUtils.isBlank(this.getExamName())){
			Log.d(TAG, "配置缺少=>考试名称!");
			return false;
		}
		if(StringUtils.isBlank(this.getProductId())){
			Log.d(TAG, "配置缺少=>产品ID!");
			return false;
		}
		if(StringUtils.isBlank(this.getProductName())){
			Log.d(TAG, "配置缺少=>产品名称!");
			return false;
		}
		return true;
	}
	/**
	 * 保存设置到配置。
	 */
	public void saveToDefaults(){
		Log.d(TAG, "保存设置数据...");
		if(this.isDirty && this.model != null){
			try {
				//数据模型处理
				AppSettingsModel settingsModel = this.model.clone();
				//生成校验码
				settingsModel.setCheckCode(settingsModel.createCheckCode());
				//json序列化
				Gson gson = new Gson();
				String json = gson.toJson(settingsModel);
				if(StringUtils.isBlank(json)){
					Log.d(TAG, "序列化JSON无数据!");
					return;
				}
				//获取首选项
				if(this.preferences == null){
					this.preferences = loadDefaultSharedPreferences();
				}
				//判断首选项
				if(this.preferences == null){
					Log.d(TAG, "加载首选项失败!");
					return;
				}
				//
				Editor editor = this.preferences.edit();
				editor.putString(TAG, json);
				if(editor.commit()){
					this.isDirty = false;
				}
			} catch (Exception e) {
				Log.e(TAG, "保存设置数据发生异常:" + e.getMessage(), e);
			}
		}
	}
	/*
	 * 重载。
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return StringUtils.join(new String[]{
				StringUtils.trimToEmpty(this.getExamId()),
				StringUtils.trimToEmpty(this.getExamCode()),
				StringUtils.trimToEmpty(this.getExamName()),
				
				StringUtils.trimToEmpty(this.getProductId()),
				StringUtils.trimToEmpty(this.getProductName())
		}, ",");
	}
	/**
	 * 应用设置数据模型。
	 * 
	 * @author jeasonyoung
	 * @since 2015年6月25日
	 */
	private class AppSettingsModel implements Serializable,Cloneable{
		private static final long serialVersionUID = 1L;
		private String examId,examCode,examName,productId,productName,checkCode;
		/**
		 * 获取考试ID。
		 * @return 考试ID。
		 */
		public String getExamId() {
			return examId;
		}
		/**
		 * 设置考试ID。
		 * @param examId 
		 *	  考试ID。
		 */
		public void setExamId(String examId) {
			this.examId = examId;
		}
		/**
		 * 获取考试代码。
		 * @return 考试代码。
		 */
		public String getExamCode() {
			return examCode;
		}
		/**
		 * 设置考试代码。
		 * @param examCode 
		 *	  考试代码。
		 */
		public void setExamCode(String examCode) {
			this.examCode = examCode;
		}
		/**
		 * 获取考试名称。
		 * @return 考试名称。
		 */
		public String getExamName() {
			return examName;
		}
		/**
		 * 设置考试名称。
		 * @param examName 
		 *	  考试名称。
		 */
		public void setExamName(String examName) {
			this.examName = examName;
		}
		/**
		 * 获取产品ID。
		 * @return 产品ID。
		 */
		public String getProductId() {
			return productId;
		}
		/**
		 * 设置产品ID。
		 * @param productId 
		 *	  产品ID。
		 */
		public void setProductId(String productId) {
			this.productId = productId;
		}
		/**
		 * 获取产品名称。
		 * @return 产品名称。
		 */
		public String getProductName() {
			return productName;
		}
		/**
		 * 设置产品名称。
		 * @param productName 
		 *	  产品名称。
		 */
		public void setProductName(String productName) {
			this.productName = productName;
		}
		/**
		 * 获取校验码。
		 * @return 校验码。
		 */
		public String getCheckCode() {
			return checkCode;
		}
		/**
		 * 设置校验码。
		 * @param checkCode 
		 *	  校验码。
		 */
		public void setCheckCode(String checkCode) {
			this.checkCode = checkCode;
		}
		/*
		 * 复制对象。
		 * @see java.lang.Object#clone()
		 */
		@Override
		public AppSettingsModel clone(){
			try {
				Log.d(TAG, "数据模型对象复制..");
				AppSettingsModel appSettingsModel = (AppSettingsModel)super.clone();
				
				appSettingsModel.setExamId(this.getExamId());
				appSettingsModel.setExamCode(this.getExamCode());
				appSettingsModel.setExamName(this.getExamName());
				
				appSettingsModel.setProductId(this.getProductId());
				appSettingsModel.setProductName(this.getProductName());
				
				appSettingsModel.setCheckCode(this.getCheckCode());
				
				return appSettingsModel;
			} catch (CloneNotSupportedException e) {
				 Log.e(TAG, "数据模型对象复制异常:" + e.getMessage(), e);
			}
			return null;
		}
		/**
		 * 创建校验码。
		 * @return
		 */
		public String createCheckCode(){
			Log.d(TAG, "生成校验码...");
			String s = StringUtils.join(new String[]{  
					StringUtils.trimToEmpty(this.getExamId()),
					StringUtils.trimToEmpty(this.getExamCode()),
					StringUtils.trimToEmpty(this.getExamName()),
					
					StringUtils.trimToEmpty(this.getProductId()),
					StringUtils.trimToEmpty(this.getProductName())
			}, "-");
			Log.d(TAG, "源字符串为:" + s);
			s = StringUtils.reverse(s);
			Log.d(TAG, "反转后的字符串:" + s);
			return DigestUtils.md5Hex(s);
		}
	}
}