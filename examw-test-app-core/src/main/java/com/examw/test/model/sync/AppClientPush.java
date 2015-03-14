package com.examw.test.model.sync;

import java.io.Serializable;
import java.util.List;

/**
 * 客户端推送数据
 * 
 * @author yangyong
 * @since 2015年3月9日
 */
public  class AppClientPush<T extends Serializable> extends AppClient  {
	private static final long serialVersionUID = 1L;
	private String code,userId;
	private List<T> records;
	/**
	 * 获取注册码。
	 * @return 注册码。
	 */
	public String getCode() {
		return code;
	}
	/**
	 * 设置注册码。
	 * @param code 
	 *	  注册码。
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * 获取当前用户ID。
	 * @return 当前用户ID。
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * 设置当前用户ID。
	 * @param userId 
	 *	  当前用户ID。
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	/**
	 * 获取记录集合。
	 * @return 记录集合。
	 */
	public List<T> getRecords() {
		return records;
	}
	/**
	 * 设置记录集合。
	 * @param records 
	 *	  记录集合。
	 */
	public void setRecords(List<T> records) {
		this.records = records;
	}
	/**
	 * 将对象转换为客户端同步对象。
	 * @return
	 */
	public AppClientSync toAppClientSync(){
		AppClientSync appClientSync = new AppClientSync(this);
		appClientSync.setCode(this.getCode());
		return appClientSync;
	}
	/*
	 * 重载
	 * @see com.examw.test.model.api.AppClient#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append(",")
				   .append("code").append("=").append(this.getCode()).append(",")
				   .append("userId").append("=").append(this.getUserId()).append(",")
				   .append("records").append("=").append("[]");
		return builder.toString();
	}
}