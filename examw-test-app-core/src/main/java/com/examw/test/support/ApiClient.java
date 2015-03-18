package com.examw.test.support;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.examw.test.app.AppConfig;
import com.examw.test.app.AppContext;
import com.examw.test.domain.Subject;
import com.examw.test.domain.User;
import com.examw.test.exception.AppException;
import com.examw.test.model.FeedBackInfo;
import com.examw.test.model.FrontPaperInfo;
import com.examw.test.model.FrontProductInfo;
import com.examw.test.model.FrontUserInfo;
import com.examw.test.model.Json;
import com.examw.test.model.LoginUser;
import com.examw.test.model.RegisterUser;
import com.examw.test.model.RemoteUserInfo;
import com.examw.test.model.SubjectInfo;
import com.examw.test.model.UserItemFavoriteInfo;
import com.examw.test.model.UserPaperRecordInfo;
import com.examw.test.model.sync.AppClientSync;
import com.examw.test.model.sync.ExamSync;
import com.examw.test.model.sync.PaperSync;
import com.examw.test.util.GsonUtil;
import com.examw.test.util.HtmlUtils;
import com.examw.test.util.HttpUtils;
import com.examw.test.util.LogUtil;
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
		/*
		 *  SubSource = "Mobile"  提交来源
			ClientNo 客户端唯一标示
			Md5Str 32位加密字符串
			ClientKey 跟注册KEY一样
			UserName  用户名
			PassWord  密码
			CheckType = "RegUser" 注册
			CheckType = "Login"  登录
			Md5Str = MD5(UserName&"#"&PassWord&"#"&ClientKey&"#"&SubSource&"#"&ClientNo)
		 */
		String md5Str = TaoBaoMD5.sign(username+"#"+password+"#"+AppConfig.CLIENTKEY+"#Mobile#"+appContext.getDeviceId(), "", "GBK");
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("SubSource", "Mobile");
		params.put("ClientNo", appContext.getDeviceId());
		params.put("Md5Str", md5Str);
		params.put("UserName", username);
		params.put("PassWord", password);
		params.put("CheckType", "Login");
		String result = HttpUtils.login(appContext, URLs.LOGIN, params);
		if(result == null || "".equals(result.trim())) return null;
		Json json = new Json();
		try
		{
			//返回数字的都是没有登录成功的
			Integer code = Integer.parseInt(result);
			json.setSuccess(false);
			json.setData(code);
		}catch(Exception e)
		{
			json.setSuccess(true);
			//解析字符串
			if(result.startsWith("jsonpLogin"))
			{
				RemoteUserInfo userInfo = GsonUtil.jsonToBean(result.substring(11, result.length()-2), RemoteUserInfo.class);
				User user = new User();
				user.setInfo(result.substring(11, result.length()-2));
				user.setUsername(userInfo.getUserName());
				user.setUid(userInfo.getUid());
				user.setPassword(password);
				json.setData(user);
			}else
			{
				return null;
			}
		}
		return json;
	}
	//注册
	public static Json register(AppContext appContext,String username,String password,String phone,String name,String email)throws AppException, UnsupportedEncodingException
	{
		/*
		 * UserName  用户名
			PassWord   第一次密码
			repsw  重复输入密码
			e_mail 邮箱
			r_name 真实姓名
			Mobile 手机号
			DoMain 频道  (固定值 根据考试来填写)
			SubSource = "Mobile"  提交来源
			ClientNo 客户端唯一标示
			Client 客户端名称、版本
			Version 软件版本（我们自己的客户端版本）
			Md5Str 32位加密字符串
			ClientKey = "U8z2D0O5s7Li1Q3y4k6g"
			Md5Str = MD5(UserName&"#"&PassWord&"#"&repsw&"#"&e_mail&"#"&ClientKey&r_name&"#"&Mobile&"#"&DoMain&"#"&SubSource&"#"&ClientNo)
		 */
		String md5Str = TaoBaoMD5.sign(username+"#"+password+"#"+password+"#"+email+"#"+AppConfig.CLIENTKEY + "#" +name +
					"#"+phone+"#jzs1#Mobile#"+appContext.getDeviceId(), "", "GBK");
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("SubSource", "Mobile");
		params.put("ClientNo", appContext.getDeviceId());
		params.put("Md5Str", md5Str);
		params.put("UserName", username);
		params.put("PassWord", password);
		params.put("repsw", password);
		params.put("Mobile", phone);
		params.put("DoMain","jzs1");
		params.put("e_mail", email);
		params.put("r_name", HttpUtils.toUnicodeString(name));
		params.put("Client", appContext.getOsVersionName());
		params.put("Version", appContext.getVersionName());
		params.put("CheckType", "RegUser");
		String result = HttpUtils.register(appContext, URLs.REGIST, params);
		if(result == null || "".equals(result.trim())) return null;
		Json json = new Json();
		try
		{
			//返回数字的都是没有登录成功的
			Integer code = Integer.parseInt(result);
			json.setSuccess(false);
			json.setData(code);
		}catch(Exception e)
		{
			json.setSuccess(true);
			//解析字符串
			if(result.startsWith("jsonpLogin"))
			{
				RemoteUserInfo userInfo = GsonUtil.jsonToBean(result.substring(11, result.length()-2), RemoteUserInfo.class);
				User user = new User();
				user.setInfo(result.substring(11, result.length()-2));
				user.setUsername(userInfo.getUserName());
				user.setUid(userInfo.getUid());
				user.setPassword(password);
				json.setData(user);
			}else
			{
				return null;
			}
		}
		return json;
	}
	/*****************************************************************
	 *	登录注册的代理实现  start
	 ************************************************************/
	public static Json login_proxy(AppContext appContext,String username,String password) throws AppException{
		LoginUser user = new LoginUser();
		user.setAccount(username);
		user.setPassword(password);
		user.setClientId(appContext.getDeviceId());
		String result = HttpUtils.http_post(appContext, URLs.LOGIN_PROXY,user);
		if(StringUtils.isEmpty(result)) return null;
		return GsonUtil.jsonToBean(result, Json.class);
	}
	public static Json register_proxy(AppContext appContext,String username,String password,String phone,String name,String email)throws AppException, UnsupportedEncodingException
	{
		RegisterUser user = new RegisterUser();
		user.setAccount(username);
		user.setPassword(password);
		user.setClientId(appContext.getDeviceId());
		user.setClientName(appContext.getOsVersionName());
		user.setClientVersion(appContext.getVersionName());
		user.setEmail(email);
		user.setPhone(phone);
		user.setUsername(name);
		user.setChannel("jzs1");
		String result = HttpUtils.http_post(appContext, "http://192.168.1.246:8080/examw-test/api/m/user/register" ,user);
		if(StringUtils.isEmpty(result)) return null;
		return GsonUtil.jsonToBean(result, Json.class);
	}
	/*****************************************************************
	 *	登录注册的代理实现  end
	 *****************************************************************/
	
	
	/**
	 * 获取产品用户的信息
	 * @param appContext
	 * @param user
	 * @return
	 * @throws AppException
	 */
	public static Json getProductUser(AppContext appContext,FrontUserInfo user) throws AppException
	{
		String result = HttpUtils.http_post(appContext, URLs.VERIFYUSER,user);
		if(StringUtils.isEmpty(result)) return null;
		return GsonUtil.jsonToBean(result, Json.class);
	}
	public static Json feedBack(AppContext appContext,FeedBackInfo feedback) throws AppException
	{
		String result = HttpUtils.http_post(appContext, URLs.FEEDBACK,feedback);
		if(StringUtils.isEmpty(result)) return null;
		return GsonUtil.jsonToBean(result, Json.class);
	}
	public static Json updateRecords(AppContext appContext,ArrayList<UserPaperRecordInfo> reocrds) throws AppException
	{
		String result = HttpUtils.http_post(appContext, URLs.UPLOAD_RECORDS,reocrds);
		if(StringUtils.isEmpty(result)) return null;
		return GsonUtil.jsonToBean(result, Json.class);
	}
	
	public static Json updateFavors(AppContext appContext,
			ArrayList<UserItemFavoriteInfo> records) throws AppException{
		String result = HttpUtils.http_post(appContext, URLs.UPLOAD_FAVORS,records);
		if(StringUtils.isEmpty(result)) return null;
		return GsonUtil.jsonToBean(result, Json.class);
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
	
	/**
	 * 获取试卷的更新
	 * @param lastTime
	 * @return
	 * @throws AppException
	 */
	public static ArrayList<FrontPaperInfo> getUpdatePaperInfo(AppContext appContext,String lastTime) throws AppException{
		String url = URLs.PAPER_UPDATE_LIST + StringUtils.toDateLong(lastTime);
		String result = HttpUtils.http_get(appContext,url);
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
	//获取知识点下的试题
	public static String loadSyllabusItems(AppContext appContext, String syllabusId)  throws AppException{
		String result = HttpUtils.http_get(appContext, String.format(URLs.SYLLABUS_ITEMS,syllabusId));
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
	 * 获取网络图片,并且保存到指定路径
	 * @param url
	 * @return
	 */
	public static void getNetImage(String url,String path) throws AppException {
		HttpClient httpClient = null;
		GetMethod httpGet = null;
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
				String fileName = url.substring(url.lastIndexOf("/")+1);
				FileOutputStream fs = new FileOutputStream(path+fileName);
				byte[] buffer = new byte[4*1024];
				int byteread = 0;
				while ((byteread = inStream.read(buffer)) != -1) {
					fs.write(buffer, 0, byteread);
				}
				fs.flush();
				fs.close();
				inStream.close();
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
			LogUtil.d("ApiClient"+"获取百度的时间");
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
	public static void logout(AppContext appContext, String username) {
		appContext.cleanLoginInfo();
	}
	
	/**
	 * 获取试卷
	 * @param appContext
	 * @param req
	 * @return
	 * @throws AppException
	 */
	public static ArrayList<PaperSync> getPapers(AppContext appContext,AppClientSync req) throws AppException{
		String result = HttpUtils.http_post(appContext, URLs.PAPER_SYNC,req);
		if(StringUtils.isEmpty(result)) return null;
		Json json = GsonUtil.getGson().fromJson(result,Json.class);
		if(json.isSuccess()){
			result = GsonUtil.getGson().toJson(json.getData());
			return GsonUtil.getGson().fromJson(result, new TypeToken<ArrayList<PaperSync>>(){}.getType());
		}
		return null;
	}
	
	/**
	 * 获取考试
	 * @param appContext
	 * @param req
	 * @return
	 * @throws AppException
	 */
	public static ExamSync getExams(AppContext appContext,AppClientSync req) throws AppException{
		String result = HttpUtils.http_post(appContext, URLs.EXAM_SUBJECT_SYNC,req);
		if(StringUtils.isEmpty(result)) return null;
		Json json = GsonUtil.getGson().fromJson(result,Json.class);
		if(json.isSuccess()) 
		{
			result = GsonUtil.getGson().toJson(json.getData());
			return GsonUtil.getGson().fromJson(result, ExamSync.class);
		}
		return null;
	}
}
