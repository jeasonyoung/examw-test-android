package com.examw.test.support;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.examw.test.app.AppContext;
import com.examw.test.domain.Subject;
import com.examw.test.domain.User;
import com.examw.test.exception.AppException;
import com.examw.test.model.FrontPaperInfo;
import com.examw.test.model.FrontProductInfo;
import com.examw.test.model.Json;
import com.examw.test.model.SubjectInfo;
import com.examw.test.util.GsonUtil;
import com.examw.test.util.HtmlUtils;
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
	//加载每日一练
	public static ArrayList<FrontPaperInfo> getDailyPaperList(AppContext appContext) throws AppException{
		String result = HttpUtils.http_get(appContext, URLs.DAILY_PAPER_LIST);
		if(StringUtils.isEmpty(result)) return null;
		return GsonUtil.getGson().fromJson(result, new TypeToken<ArrayList<FrontPaperInfo>>(){}.getType());
	}
	//单个试卷
	public static String loadPaperContent(AppContext appContext,String paperId) throws AppException{
		String result = HttpUtils.http_get(appContext, String.format(URLs.SINGLE_PAPER,paperId));
		if(StringUtils.isEmpty(result)) return null;
		//过滤掉多余的P标签
		return HtmlUtils.filterPTag(result);
	}
	//获取科目所用大纲的内容
	public static String loadSyllabusContent(AppContext appContext,String subjectId) throws AppException{
		String result = HttpUtils.http_get(appContext, String.format(URLs.SYLLABUS,subjectId));
		if(StringUtils.isEmpty(result)) return null;
		return result;
	}
	//获取知识点的内容
	public static String loadKnowledgeContent(AppContext appContext,String chapterId) throws AppException{
		String result = HttpUtils.http_get(appContext, String.format(URLs.KNOWLEDGE,chapterId));
		if(StringUtils.isEmpty(result)) return null;
		return result;
	}
	/**
	 * 获取网络图片
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getNetBitmap(String url) throws AppException {
		// System.out.println("image_url==> "+url);
		HttpClient httpClient = null;
		GetMethod httpGet = null;
		Bitmap bitmap = null;
		int time = 0;
		do {
			try {
				httpClient = HttpUtils.getHttpClient();
				httpGet = HttpUtils.getHttpGet(url, null, null);
				int statusCode = httpClient.executeMethod(httpGet);
				if (statusCode != HttpStatus.SC_OK) {
					throw AppException.http(statusCode);
				}
				InputStream inStream = httpGet.getResponseBodyAsStream();
				bitmap = BitmapFactory.decodeStream(inStream);
				inStream.close();
				break;
			} catch (HttpException e) {
				time++;
				if (time < 3) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生致命的异常，可能是协议不对或者返回的内容有问题
				e.printStackTrace();
				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if (time < 3) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生网络异常
				e.printStackTrace();
				throw AppException.network(e);
			} finally {
				// 释放连接
				httpGet.releaseConnection();
				httpClient = null;
			}
		} while (time < 3);
		return bitmap;
	}
	/**
	 * 获取北京时间
	 * @return
	 */
	public static long getStandardTime()
	{
		HttpURLConnection uc= null;
		try
		{
			Log.d("ApiClient","获取百度的时间");
			URL url=new URL("http://www.baidu.com");//取得资源对象
			uc = (HttpURLConnection) url.openConnection();//生成连接对象
			uc.setConnectTimeout(5000);
			uc.setReadTimeout(5000);
			uc.setRequestMethod("GET");
			uc.connect(); //发出连接
			return uc.getDate(); //取得网站日期时间
		}catch(Exception e)
		{
			e.printStackTrace();
			return System.currentTimeMillis();
		}finally{
			if(uc != null)
			{
				uc.disconnect();
			}
		}
	}
}
