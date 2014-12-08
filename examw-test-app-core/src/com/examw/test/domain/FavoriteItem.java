package com.examw.test.domain;
/**
 * 
 * @author fengwei.
 * @since 2014年11月27日 下午4:37:11.
 */
public class FavoriteItem {
	private String itemId,username;
	private boolean needDelete;
	/**
	 * 获取 
	 * @return itemId
	 * 
	 */
	public String getItemId() {
		return itemId;
	}

	/**
	 * 设置 
	 * @param itemId
	 * 
	 */
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	/**
	 * 获取 
	 * @return username
	 * 
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * 设置 
	 * @param username
	 * 
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * 获取 
	 * @return needDelete
	 * 
	 */
	public boolean isNeedDelete() {
		return needDelete;
	}

	/**
	 * 设置 
	 * @param needDelete
	 * 
	 */
	public void setNeedDelete(boolean needDelete) {
		this.needDelete = needDelete;
	}
	
}
