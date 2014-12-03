package com.examw.test.util;

import java.util.List;

import com.google.gson.Gson;

/**
 * GSON解析工具类
 * @author fengwei.
 * @since 2014年11月27日 上午11:40:37.
 */
public class GsonUtil {
	private static Gson gson;
	
	private static Gson getGson(){
		if(gson == null)
		{
			gson = new Gson();
		}
		return gson;
	}
	
	/**
	 * 对象转json字符串
	 * @param obj
	 * @return
	 */
	public static String objectToJson(Object obj)
	{
		return getGson().toJson(obj);
	}
	
	/**
	 * json字符串转对象
	 * @param jsonStr
	 * @param c
	 * @return
	 */
	public static <T>T jsonToBean(String jsonStr, Class<T> c) {
		return getGson().fromJson(jsonStr, c);
	}
	
	/**
	 * 将json格式转换成list对象，并准确指定类型
	 * @param jsonStr
	 * @param type
	 * @return
	 */
	public static List<?> jsonToList(String jsonStr, java.lang.reflect.Type type) {
		List<?> objList = null;
		if (gson != null) {
			objList = gson.fromJson(jsonStr, type);
		}
		return objList;
	}
	
	/**
	 * 将json格式转换成list对象
	 * 
	 * @param jsonStr
	 * @return
	 */
	public static List<?> jsonToList(String jsonStr) {
		List<?> objList = null;
		if (gson != null) {
			java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<?>>() {
			}.getType();
			objList = gson.fromJson(jsonStr, type);
		}
		return objList;
	}
}
