package com.examw.test.dao;

/**
 * 下载结果监听。
 * 
 * @author jeasonyoung
 * @since 2015年6月27日
 */
public interface DownloadResultListener{
	/**
	 * 下载完成处理。
	 * @param result
	 * @param msg
	 */
	void onComplete(boolean result, String msg);
}