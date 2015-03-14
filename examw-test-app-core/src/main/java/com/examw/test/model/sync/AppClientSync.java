package com.examw.test.model.sync;

/**
 * 客户端同步
 * 
 * @author yangyong
 * @since 2015年2月27日
 */
public class AppClientSync extends AppClient {
	private static final long serialVersionUID = 1L;
	private String code,startTime;
	/**
	 * 构造函数。
	 */
	public AppClientSync(){};
	/**
	 * 构造函数。
	 * @param appClient
	 */
	public AppClientSync(AppClient appClient){
		this();
		if(appClient != null){
			this.setClientId(appClient.getClientId());
			this.setClientMachine(appClient.getClientMachine());
			this.setClientName(appClient.getClientName());
			this.setClientTypeCode(appClient.getClientTypeCode());
			this.setClientVersion(appClient.getClientVersion());
			this.setProductId(appClient.getProductId());
		}
	}
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
	 * 获取同步起始时间。
	 * @return 同步起始时间。
	 */
	public String getStartTime() {
		return startTime;
	}
	/**
	 * 设置同步起始时间。
	 * @param startTime 
	 *	  同步起始时间。
	 */
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
}