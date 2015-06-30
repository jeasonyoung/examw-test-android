package com.examw.test.utils;

import org.apache.commons.lang3.StringUtils;

import android.util.Log;

/**
 * 试卷工具类。
 * 
 * @author jeasonyoung
 * @since 2015年6月29日
 */
public final class PaperUtils {
	private static final String TAG = "PaperUtils";
	private static final String ENCRYPT_PREFIX = "0x";
	/**
	 * 加密试卷/试题内容。
	 * @param content
	 * 明文内容。
	 * @param password
	 * 加密密码。
	 * @return
	 * Hex密文。
	 */
	public static String encryptContent(String content, String password){
		Log.d(TAG, "加密内容...");
		//检查内容
		if(StringUtils.isBlank(content)){
			Log.d(TAG, "内容为空!");
			return content;
		}
		//检查密码
		if(StringUtils.isBlank(password)){
			Log.d(TAG, "密码为空!");
			return content;
		}
		return ENCRYPT_PREFIX + AESUtils.encryptToHex(content, password);
	}
	/**
	 * 解密试卷/试题内容。
	 * @param hex
	 * Hex密文。
	 * @param password
	 * 解密密钥。
	 * @return
	 * 解密后的明文。
	 */
	public static String  decryptContent(String hex, String password){
		Log.d(TAG, "解密内容...");
		//hex密文
		if(StringUtils.isBlank(hex)){
			Log.d(TAG, "Hex密文为空!");
			return hex;
		}
		//检查是否为密文
		if(!hex.startsWith(ENCRYPT_PREFIX)){
			Log.d(TAG, "Hex中没有密文标示!");
			return hex;
		}
		//去除密文标示
		String content = hex.substring(ENCRYPT_PREFIX.length());
		//解密密文
		return AESUtils.decryptFromHex(content, password);
	}
}