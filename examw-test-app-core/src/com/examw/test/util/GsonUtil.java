package com.examw.test.util;

import com.google.gson.Gson;

/**
 * GSON解析工具类
 * @author fengwei.
 * @since 2014年11月27日 上午11:40:37.
 */
public class GsonUtil {
	private static Gson gson;
	
	public synchronized static Gson getGson(){
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
}
