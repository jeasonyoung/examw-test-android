package com.examw.test.model.sync;

import java.io.Serializable;

/**
 * 客户端应用。
 * 
 * @author yangyong
 * @since 2015年2月4日
 */
public class AppClient implements Serializable {
	private static final long serialVersionUID = 1L;
	private String clientId,clientName,clientVersion,clientTypeCode,clientMachine,productId;
	/**
	 * 获取客户端ID。
	 * @return 客户端ID。
	 */
	public String getClientId() {
		return clientId;
	}
	/**
	 * 设置客户端ID。
	 * @param clientId 
	 *	  客户端ID。
	 */
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	/**
	 * 获取客户端名称。
	 * @return 客户端名称。
	 */
	public String getClientName() {
		return clientName;
	}
	/**
	 * 设置客户端名称。
	 * @param clientName 
	 *	  客户端名称。
	 */
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	/**
	 * 获取客户端版本。
	 * @return 客户端版本。
	 */
	public String getClientVersion() {
		return clientVersion;
	}
	/**
	 * 设置客户端版本。
	 * @param clientVersion 
	 *	  客户端版本。
	 */
	public void setClientVersion(String clientVersion) {
		this.clientVersion = clientVersion;
	}
	/**
	 * 获取客户端软件类型代码。
	 * @return 客户端软件类型代码。
	 */
	public String getClientTypeCode() {
		return clientTypeCode;
	}
	/**
	 * 设置客户端软件类型代码。
	 * @param clientTypeCode
	 *	  客户端软件类型类型代码。。
	 */
	public void setClientTypeCode(String clientTypeCode) {
		this.clientTypeCode = clientTypeCode;
	}
	/**
	 * 获取客户端机器码。
	 * @return 客户端机器码。
	 */
	public String getClientMachine() {
		return clientMachine;
	}
	/**
	 * 设置客户端机器码。
	 * @param clientMachine 
	 *	  客户端机器码。
	 */
	public void setClientMachine(String clientMachine) {
		this.clientMachine = clientMachine;
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
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("clientId").append(":").append(this.clientId).append(",")
				   .append("clientName").append(":").append(this.clientName).append(",")
				   .append("clientVersion").append(":").append(this.clientVersion).append(",")
				   .append("clientTypeCode").append(":").append(this.clientTypeCode).append(",")
				   .append("clientMachine").append(":").append(this.clientMachine).append(",")
				   .append("productId").append(":").append(this.productId);
		return builder.toString();
	}
}