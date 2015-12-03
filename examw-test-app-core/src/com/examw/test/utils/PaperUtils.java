package com.examw.test.utils;

import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
	
	/**
	 * 将json字符串转换成对象。
	 * @param clazz
	 * 对象类型。
	 * @param json
	 * json字符串
	 * @return
	 * 对象实例。
	 */
	public static final <T> T fromJSON(final Class<T> clazz, final String json){
		Log.d(TAG, "将json字符串转换成对象=>" + clazz);
		if(clazz == null || StringUtils.isBlank(json)) return null;
		return fromJSON(clazz, new StringReader(json));
	}
	
	/**
	 * 读取json字符串转换成对象。
	 * @param clazz
	 * @param json
	 * @return
	 */
	public static final <T> T fromJSON(final Class<T> clazz, final Reader json){
		Log.d(TAG, "将json字符串转换成对象=>" + clazz);
		if(clazz == null || json == null) return null;
		final Gson gson = new Gson();
		return gson.fromJson(json, clazz);
	}
	
	/**
	 * 将对象转换成JSON串。
	 * @param obj
	 * 对象实例。
	 * @return
	 * JSON串。
	 */
	public static final <T extends Serializable> String toJSONWithoutExposeAnnotation(final T obj){
		Log.d(TAG, "将对象转换成JSON串...");
		if(obj == null) return null;
		final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
				 .serializeNulls()
				 .setDateFormat("yyyy-MM-dd HH:mm:ss")
				 .setPrettyPrinting()
				 .create();
		return gson.toJson(obj);
	}
	
	/**
	 * 将对象转换成JSON串。
	 * @param obj
	 * 对象实例。
	 * @return
	 * JSON串。
	 */
	public static final <T extends Serializable> String toJSON(final T obj){
		Log.d(TAG, "将对象转换成JSON串...");
		if(obj == null) return null;
		final Gson gson = new Gson();
		return gson.toJson(obj);
	}
	/**
	 * 将对象转换成JSON串。
	 * @param obj
	 * 对象实例。
	 * @param writer
	 *  JSON串。
	 */
	public static final <T extends Serializable>void toJSON(final T obj, final Appendable writer){
		Log.d(TAG, "将对象转换成JSON串...");
		if(obj == null || writer == null) return;
		final Gson gson = new Gson();
		gson.toJson(obj, writer);
	}
}