package com.examw.test.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import com.examw.test.app.AppConstant;
import com.examw.test.app.AppContext;
import com.examw.test.model.sync.JSONCallback;
import com.google.gson.Gson;

import android.util.Log;

/**
 * HTTP摘要认证客户端工具类
 * 
 * @author yangyong
 * @since 2014年12月22日
 */
public final class DigestClientUtil {
	private static final String TAG = "DigestClientUtil";
	private static final String ENCODING = "UTF-8";
	private static final String APP_JSON = "application/json";
	private static final int TIME_OUT = 5000;
	
	/**
	 * 发送POST请求
	 * @param url
	 * @param parameters
	 * @return
	 */
	public static String sendPOST(final String url, final Map<String, Object> parameters){
		try{
			if(StringUtils.isBlank(url)) return null;
			Log.d(TAG, "发送POST请求...=>" + url);
			//初始化POST
			final HttpPost post = new HttpPost(url);
			//设置参数
			post.setEntity(new UrlEncodedFormEntity(createParameters(parameters), ENCODING));
			//发送请求
			return sendDigestRequestByString(post);
		}catch(Exception e){
			Log.e(TAG, "POST请求异常:" + e.getMessage(), e);
		}
		return null;
	}
	
	/**
	 * POST请求下载文件。
	 * @param url
	 * @param parameters
	 * @return
	 */
	public static byte[] downloadWithPOST(final String url, final Object obj){
		try{
			if(StringUtils.isBlank(url)) return null;
			Log.d(TAG, "发送POST请求下载文件...=>" + url);
			//初始化POST
			final HttpPost post = new HttpPost(url);
			//设置参数
			if(obj != null){
				//初始化JSON
				final Gson gson = new Gson();
				//JSON序列化
				final String json_data = gson.toJson(obj);
				//初始化参数
				final StringEntity s = new StringEntity(json_data, ENCODING);
				s.setContentEncoding(ENCODING);
				s.setContentType(APP_JSON);
				//设置参数
				post.setEntity(s);
			}
			//发送请求
			final HttpEntity entity = sendDigestRequest(post);
			if(entity != null) return EntityUtils.toByteArray(entity);
		}catch(Exception e){
			Log.e(TAG, "POST请求异常:" + e.getMessage(), e);
		}
		return null;
	}
	
	/**
	 * 发送JSON对象的POST请求。
	 * @param url
	 * @param obj
	 * @return
	 */
	public static String sendPOST(final String url, final Object obj){
		try{
			if(StringUtils.isBlank(url)) return null;
			Log.d(TAG, "发送POST请求...=>" + url);
			//初始化POST
			final HttpPost post = new HttpPost(url);
			if(obj != null){
				//初始化JSON
				final Gson gson = new Gson();
				//JSON序列化
				final String json_data = gson.toJson(obj);
				//初始化参数
				final StringEntity s = new StringEntity(json_data, ENCODING);
				s.setContentEncoding(ENCODING);
				s.setContentType(APP_JSON);
				//设置参数
				post.setEntity(s);
			}
			//发送请求
			return sendDigestRequestByString(post);
		}catch(Exception e){
			Log.e(TAG, "JSON-POST请求异常:" + e.getMessage(), e);
		}
		return null;
	}
	
	/**
	 * 发送GET请求。
	 * @param url
	 * @param parameters
	 * @return
	 */
	public static String sendGET(final String url, final Map<String, Object> parameters){
		try{
			if(StringUtils.isBlank(url)) return null;
			Log.d(TAG, "发送GET请求...");
			final StringBuilder uriBuilder = new StringBuilder(url);
			//参数编码
			final String param = URLEncodedUtils.format(createParameters(parameters), ENCODING);
			if(!StringUtils.isBlank(param)){
				 uriBuilder.append((url.indexOf('?') == -1 ? "?": "&"))
				 				 .append(param);
			}
			//发送请求
			return sendDigestRequestByString(new HttpGet(uriBuilder.toString()));
		}catch(Exception e){
			Log.e(TAG, "GET请求异常:" + e.getMessage(), e);
		}
		return null;
	}
	 
	//构建参数。
	private static List<BasicNameValuePair> createParameters(final Map<String, Object> parameters){
		Log.d(TAG, "构建参数...");
		final List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
		if(parameters != null && parameters.size() > 0){
			for(Entry<String, Object> entry : parameters.entrySet()){
				if(StringUtils.isBlank(entry.getKey()) || entry.getValue() == null) continue;
				params.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
			}
		}
		return params;
	}
	
	//发送认证请求返回对象。
	private static HttpEntity sendDigestRequest(HttpUriRequest method){
		try{
			if(method == null) return null;
			Log.d(TAG, "开始执行HTTP请求...");
			
			//创建认证账号密码
			final Credentials credentials = new UsernamePasswordCredentials(AppConstant.APP_API_USERNAME, AppConstant.APP_API_PASSWORD);
			//创建认证提供者
			final BasicCredentialsProvider bcp = new BasicCredentialsProvider();
			bcp.setCredentials(AuthScope.ANY, credentials);
			
			//初始化HTTP客户端
			final DefaultHttpClient client = new DefaultHttpClient();
			//设置连接超时
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, TIME_OUT);
			//设置读取超时
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, TIME_OUT);
			
			//设置认证
			client.setCredentialsProvider(bcp);
			
			//执行请求
			final HttpResponse response = client.execute(method);
			//返回结果
			final int status = response.getStatusLine().getStatusCode();
			
			Log.d(TAG, "请求反馈status=>" + status);
			if(status == HttpStatus.SC_OK)
				return response.getEntity();
			
		}catch(Exception e){
			Log.e(TAG, "HTTP请求异常:" + e.getMessage(), e);
		}
		return null;
	}
	
	//发送认证请求返回字符串。
	private static String sendDigestRequestByString(HttpUriRequest method){
		try {
				if(method != null){
					//发送请求
					final HttpEntity entity = sendDigestRequest(method);
					if(entity != null) return EntityUtils.toString(entity);
				}
		}  catch (Exception e) {
			Log.e(TAG, "发生异常:" + e.getMessage(), e);
		}
		return null;
	}
	
	/**
	 * JSON结果反馈。
	 * @param <T>
	 * @author jeasonyoung
	 * @since 2015年11月30日
	 */
	public static class CallbackJSON<T>{
		private Class<T> clazz;
		/**
		 * 构造函数。
		 * @param clazz
		 */
		public CallbackJSON(Class<T> clazz){
			this.clazz = clazz;
		}
		/**
		 * 发送POST请求。
		 * @param url
		 * @param parameters
		 * @return
		 */
		public JSONCallback<T> sendPOSTRequest(final String url, final Map<String, Object> parameters){
			return this.convert(sendPOST(url, parameters));
		}
		
		/**
		 * 发送JSON-POST请求
		 * @param url
		 * @param obj
		 * @return
		 */
		public JSONCallback<T> sendPOSTRequest(final String url, final Object obj){
			return this.convert(sendPOST(url, obj));
		}
		
		/**
		 * 发送GET请求。
		 * @param url
		 * @param parameters
		 * @return
		 */
		public JSONCallback<T> sendGETRequest(final String url, final Map<String, Object> parameters){
			return this.convert(sendGET(url, parameters));
		}
		
		/**
		 * 下载ZIP-POST请求。
		 * @param url
		 * @param parameters
		 * @return
		 */
		public T downloadZipPOST(final String url, final Object parameters){
			try{
				final byte[] data = downloadWithPOST(url, parameters);
				if(data != null && data.length > 0){
					Log.d(TAG, "下载zip成功=>" + data.length);
					//
					final File jsonFile = new File(AppContext.getContext().getCacheDir(), "p" + System.currentTimeMillis() + ".json");
					//
					ZipInputStream zipInputStream = null;
					BufferedInputStream bufInputStream = null;
					try{
						//初始化zip文件流
						zipInputStream = new ZipInputStream(new ByteArrayInputStream(data));
						//zip缓冲流
						bufInputStream = new BufferedInputStream(zipInputStream);
						//解压
						ZipEntry entry = null;
						while((entry = zipInputStream.getNextEntry()) != null && !entry.isDirectory()){
							final String fileName = entry.getName();
							Log.d(TAG, "解压文件=>" + fileName);
							if(StringUtils.isNotBlank(fileName) && fileName.toLowerCase(Locale.getDefault()).endsWith(".json")){
								//初始化输出流
								final FileOutputStream outputStream = new FileOutputStream(jsonFile);
								//初始化输出流缓存
								final BufferedOutputStream outBufStream = new BufferedOutputStream(outputStream);
								final byte[] buf = new byte[1024];
								int count = 0;
								while((count = bufInputStream.read(buf, 0, buf.length)) != -1){
									outBufStream.write(buf, 0, count);
								}
								//关闭输出缓冲流
								outBufStream.close();
								//关闭输出流
								outputStream.close();
								//
								Log.d(TAG, "解压后的json文件路径=>" + jsonFile.getAbsolutePath());
								break;
							}
						}
					}catch(Exception e){
						Log.e(TAG, "解压失败!" + e.getMessage(), e);
					}finally{
						//关闭输入缓冲流
						if(bufInputStream != null) bufInputStream.close();
						//关闭zip输入流
						if(zipInputStream != null) zipInputStream.close();
					}
					//反序列化
					if(jsonFile.exists() && jsonFile.isFile() && jsonFile.length() > 0){
						Log.d(TAG, "准备反序列化JSON文件=>" + jsonFile.getAbsolutePath());
						try{
							//反序列化。
							return this.convertObj(new FileReader(jsonFile));
						}catch(Exception e){
							Log.e(TAG, "反序列化JSON失败:" + e.getMessage(), e);
						}finally{
							boolean result = jsonFile.delete();
							Log.d(TAG, "删除JSON文件=>" + (result ? "成功" : "失败"));
						}
					}
				}
			}catch(Exception e){
				Log.e(TAG, "下载zip异常:" + e.getMessage(), e);
			}
			return null;
		}
		
		/**
		 * JSON转换。
		 * @param json
		 * json字符串。
		 * @return
		 * JSON对象。
		 */
		public JSONCallback<T> convert(String json){
			Log.d(TAG, "JSON字符串转换为对象...");
			if(StringUtils.isNotBlank(json)){
				//初始化JSON转换
				final Gson gson = new Gson();
				//返回结果
				final JSONCallback<T> result = gson.fromJson(json, type(JSONCallback.class, this.clazz));
				//Log.d(TAG, "JSON字符串转换为对象->成功=>" + result);
				///TODO:登录用户限制处理(扩展)
				return result;
			}
			return new JSONCallback<T>(false, "服务器未响应!");
		}
		
		/**
		 * JSON转换为对象。
		 * @param reader
		 * @return
		 */
		private T convertObj(Reader reader) {
			if(reader != null){
				//初始化JSON转换
				final Gson gson = new Gson();
				//返回结果
				return gson.fromJson(reader, this.clazz);
			}
			return  null;
		}
		
		//类型转换。
		private static ParameterizedType type(final Class<?> raw, final Type ...args){
			return new ParameterizedType() {
				
				@Override
				public Type getRawType() {
					return raw;
				}
				
				@Override
				public Type getOwnerType() {
					return null;
				}
				
				@Override
				public Type[] getActualTypeArguments() {
					return args;
				}
			};
		}
	}
}