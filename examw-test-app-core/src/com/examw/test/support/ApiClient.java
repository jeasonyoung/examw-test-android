package com.examw.test.support;

import java.util.ArrayList;

import com.examw.test.app.AppContext;
import com.examw.test.domain.Subject;
import com.examw.test.domain.User;
import com.examw.test.exception.AppException;
import com.examw.test.model.FrontPaperInfo;
import com.examw.test.model.FrontProductInfo;
import com.examw.test.model.Json;
import com.examw.test.model.PaperPreview;
import com.examw.test.model.SubjectInfo;
import com.examw.test.util.GsonUtil;
import com.examw.test.util.HttpUtils;
import com.examw.test.util.StringUtils;
import com.google.gson.reflect.TypeToken;

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
	//获取科目集合
	public static ArrayList<Subject> getSubjectList(AppContext appContext) throws AppException{
		String result = HttpUtils.http_get(appContext, URLs.SUBJECT_LIST);
		if(StringUtils.isEmpty(result)) return null;
		ArrayList<SubjectInfo> list =  GsonUtil.getGson().fromJson(result, new TypeToken<ArrayList<SubjectInfo>>(){}.getType());
		return covertSubject(list);
	}
	//类型转换
	private static ArrayList<Subject> covertSubject(ArrayList<SubjectInfo> list)
	{
		if(list == null || list.size() ==0) return null;
		ArrayList<Subject> subjects = new ArrayList<Subject>(); 
		for(SubjectInfo info:list)
		{
			Subject data = new Subject();
			data.setName(info.getName());
			data.setSubjectId(info.getId());
			data.setOrderNo(info.getCode());
			subjects.add(data);
		}
		return subjects;
	}
	//加载试卷
	public static ArrayList<FrontPaperInfo> getPaperList(AppContext appContext) throws AppException{
		String result = HttpUtils.http_get(appContext, URLs.PAPER_LIST);
		if(StringUtils.isEmpty(result)) return null;
		return GsonUtil.getGson().fromJson(result, new TypeToken<ArrayList<FrontPaperInfo>>(){}.getType());
	}
	
	//单个试卷
	public static String loadPaperContent(AppContext appContext) throws AppException{
		String result = HttpUtils.http_get(appContext, URLs.SINGLE_PAPER);
		if(StringUtils.isEmpty(result)) return null;
		return result;
	}
}
