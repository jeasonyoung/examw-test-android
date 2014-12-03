package com.examw.test.support;

import com.examw.test.app.AppContext;
import com.examw.test.domain.User;
import com.examw.test.exception.AppException;
import com.examw.test.model.FrontProductInfo;
import com.examw.test.model.Json;
import com.examw.test.util.GsonUtil;
import com.examw.test.util.HttpUtils;
import com.examw.test.util.StringUtils;

/**
 * API接口
 * @author fengwei.
 * @since 2014年12月1日 下午2:43:17.
 */
public class ApiClient {
	//登录
	public static Json login(AppContext appContext,String username,String password) throws AppException{
		//TODO 模拟登录
//		String result;
//		result = HttpUtils.http_get(appContext, URLs.LOGIN);
//		if(StringUtils.isEmpty(result)) return null;
		Json json = new Json();
		json.setSuccess(true);
		json.setMsg("登录成功");
		User user = new User();
		user.setUid("112358");
		user.setUsername("hahaha");
		user.setPassword("123456");
		json.setData(user);
		return json;
	}
	
	//获取产品信息
	public static FrontProductInfo getProductInfo(AppContext appContext) throws AppException {
		String result = HttpUtils.http_get(appContext, URLs.PRODUCT_INFO);
		if(StringUtils.isEmpty(result)) return null;
		FrontProductInfo info = GsonUtil.jsonToBean(result, FrontProductInfo.class);
		info.setInfo(result);
		return info;
	}
}
