package com.examw.test.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import com.examw.test.model.sync.JSONCallback;
import com.google.gson.Gson;

import android.util.Log;

/**
 * HTTP摘要认证客户端工具类
 * 
 * @author yangyong
 * @since 2014年12月22日
 */
@SuppressWarnings("deprecation")
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
		final List<BasicNameValuePair> params = new LinkedList<>();
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
		public JSONCallback<T> downloadZipPOST(final String url, final Object parameters){
			try{
				final byte[] data = downloadWithPOST(url, parameters);
				if(data != null && data.length > 0){
					Log.d(TAG, "下载zip成功=>" + data.length);
					//json输出流
					byte[] json = null;
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
								final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
								//初始化输出流缓存
								final BufferedOutputStream outBufStream = new BufferedOutputStream(outputStream);
								final byte[] buf = new byte[1024];
								int count = 0;
								while((count = bufInputStream.read(buf, 0, buf.length)) != -1){
									outBufStream.write(buf, 0, count);
								}
								//关闭输出缓冲流
								outBufStream.close();
								//获取输出的JSON流
								json = outputStream.toByteArray();
								//关闭输出流
								outputStream.close();
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
					//解压后JSON处理
					if(json != null && json.length > 0){
						Log.d(TAG, "解压成功,开始反序列化处理!");
						return this.convert(new String(json, ENCODING));
					}
				}
			}catch(Exception e){
				Log.e(TAG, "下载zip异常:" + e.getMessage(), e);
			}
			return new JSONCallback<T>(false, "下载失败!");
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
			if(StringUtils.isBlank(json)){
				return new JSONCallback<T>(false, "服务器未响应!");
			}
			//返回类型处理
			final Type type = type(JSONCallback.class, this.clazz);
			//初始化JSON转换
			final Gson gson = new Gson();
			//返回结果
			final JSONCallback<T> result = gson.fromJson(json, type);
			///TODO:登录用户限制处理(扩展)
			return result;
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
	
	
	
	
//			default_get_method = "GET",
//			authenticate_header = "WWW-Authenticate",
//			authorization_header = "Authorization";
//	private static final int max_http_send_count = 3; 
//	/**
//	 * 发送HTTP摘要认证请求。
//	 * @param username
//	 * 用户名。
//	 * @param password
//	 * 密码。
//	 * @param method
//	 * 方法。
//	 * @param uri
//	 * 目标uri
//	 * @param data
//	 * 数据。
//	 * @return
//	 * 反馈数据。
//	 * @throws IOException
//	 */
//	public static String sendDigestRequest(String username,String password,String method,String uri,String data) throws Exception{
////		Map<String, String> headers = null;
////		if(StringUtils.isNotBlank(data)){
////			headers = new HashMap<String, String>();
////			headers.put("Content-type","application/json;charset=UTF-8");
////		}
////		return sendDigestRequest(username, password, headers, method, uri, data);
//		return null;
//	}
//	/**
//	 * 发送HTTP摘要认证请求。
//	 * @param username
//	 * 用户名。
//	 * @param password
//	 * 密码。
//	 * @param headers
//	 * 头消息集合。
//	 * @param method
//	 * 方法。
//	 * @param uri
//	 * 目标uri
//	 * @param data
//	 * 数据。
//	 * @return
//	 * @throws Exception
//	 */
//	public static String sendDigestRequest(String username, String password, Map<String, String> headers, String method, String uri, String data) throws Exception{	
////		Log.d(TAG, String.format("发送HTTP摘要认证请求:[username=%1$s][password=%2$s][method=%3$s][uri=%4$s][data=%5$s]",username,password,method,uri,data));
////		if(StringUtils.isBlank(uri)){
////			throw new Exception("uri不能为空！");
////		}
////		return sendRequest(createConnection(uri), 
////											headers,
////											new DigestAuthcProvider(username, password, (StringUtils.isBlank(method) ? default_get_method : method), uri), 
////											data);
//		return null;
//	}
//	//创建uri连接
//	private static HttpURLConnection createConnection(String uri) throws IOException{
//		return (HttpURLConnection)(new URL(uri).openConnection());
//	}
//	//发送HTTP请求。
//	private static String sendRequest(HttpURLConnection connection,Map<String, String> headers,DigestAuthcProvider provider, String data) throws IOException{
//		Log.d(TAG, "发送HTTP请求...");
//		if(connection == null) return null;
//		connection.setDoOutput(true);
//		connection.setDoInput(true);
//		//http摘要头信息
//		String authz = provider.toAuthorization();
//		if(!StringUtils.isEmpty(authz)){
//			if(headers == null) headers = new HashMap<String, String>();
//			Log.d(TAG, String.format("添加摘要认证头信息:%1$s=%2$s", authorization_header,authz));
//			//connection.addRequestProperty(authorization_header, authz);
//			headers.put(authorization_header, authz);
//		}
//		//添加头信息
//		if(headers != null && headers.size() > 0){
//			for(Entry<String, String> entry : headers.entrySet()){
//				if(StringUtils.isEmpty(entry.getKey())) continue;
//				connection.addRequestProperty(entry.getKey(), entry.getValue());
//			}
//		}
//		//设置请求方式
//		connection.setRequestMethod(provider.getMethod());
//		if(provider.getMethod().equalsIgnoreCase(default_get_method)){
//			connection.connect();
//		}
//		//当有数据需要提交时
//		if(!StringUtils.isEmpty(data)){
//			OutputStream outputStream = connection.getOutputStream();
//			//注意编码格式，防止中文乱码
//			outputStream.write(data.getBytes(default_utf8_charset));
//			outputStream.close();
//		}
//		int status = connection.getResponseCode();
//		Log.d(TAG, String.format("HTTP反馈状态：%d", status));
//		//401
//		if(status == HttpURLConnection.HTTP_UNAUTHORIZED){
//			if(provider.getNumberCount() > max_http_send_count){
//				throw new RuntimeException("HTTP摘要认证失败！");
//			}
//		  String authc = connection.getHeaderField(authenticate_header);
//		  Log.d(TAG, String.format("获取HTTP摘要认证头信息：%1$s=%2$s", authenticate_header, authc));
//		  if(StringUtils.isEmpty(authc)) throw new RuntimeException("获取摘要认证头信息失败！");
//		  provider.parser(authc);
//		  connection.disconnect();
//		  return sendRequest(createConnection(provider.getUri()), headers, provider, data);
//		}
//		//200
//		if(status == HttpURLConnection.HTTP_OK){
//			StringBuilder builder = new StringBuilder();
//			//将返回的输入流转换成字符串
//			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(),default_utf8_charset));
//			String out = null;
//			while(!StringUtils.isEmpty(out = bufferedReader.readLine())){
//				builder.append(out);
//			}
//			//释放资源
//			bufferedReader.close();
//			connection.disconnect();
//			String callback = builder.toString();
//			Log.d(TAG, "反馈数据=>" + callback);
//			return callback; 
//		}else{
//			throw new RuntimeException(String.format("%1$d:%2$s", status, connection.getResponseMessage()));
//		}
//	}
//	/**
//	 * 摘要认证提供者
//	 * 
//	 * @author yangyong
//	 * @since 2014年12月22日
//	 */
// 	 private static class DigestAuthcProvider{
//		private String username,password,realm,nonce,method,uri,qop = "auth",cnonce,opaque;
//		private int numberCount = 0;
//		/**
//		 * 构造函数。
//		 * @param username
//		 * 用户名。
//		 * @param password
//		 * 密码。
//		 * @param method
//		 * 请求方法。
//		 * @param uri
//		 * 请求地址。
//		 */
//		public DigestAuthcProvider(String username,String password,String method, String uri){
//			this.username = username;
//			this.password = password;
//			this.method = method;
//			this.uri = uri;
//		}
//		/**
//		 * 获取Uri。
//		 * @return Uri。
//		 */
//		public String getUri(){
//			return this.uri;
//		}
//		/**
//		 * 获取请求方法名称。
//		 * @return 请求方法名称。
//		 */
//		public String getMethod(){
//			return this.method;
//		}
//		/**
//		 * 获取计数器。
//		 * @return 计数器。
//		 */
//		public int getNumberCount() {
//			return numberCount;
//		}
//		/**
//		 * 分析认证头数据。
//		 * @param authz
//		 * 认证头数据。
//		 */
//		public void parser(String authc){
//			this.realm = this.getParameter(authc, "realm");
//			if(StringUtils.isEmpty(this.realm)){
//				throw new RuntimeException("从请求头信息中获取参数［realm］失败");
//			}
//			this.nonce = this.getParameter(authc, "nonce");
//			if(StringUtils.isEmpty(this.nonce)){
//				throw new RuntimeException("从请求头信息中获取参数［nonce］失败");
//			}
//			this.opaque = this.getParameter(authc, "opaque");
//			if(StringUtils.isEmpty(this.opaque)){
//				throw new RuntimeException("从请求头信息中获取参数［opaque］失败");
//			}
//			this.numberCount += 1;
//		}
//		//获取参数
//		private String getParameter(String authz,String name){
//			if(StringUtils.isEmpty(authz) || StringUtils.isEmpty(name)) return null;
//			String regex = name + "=((.+?,)|((.+?)$))";
//			Matcher m = Pattern.compile(regex).matcher(authz);
//			if(m.find()){
//				String p = m.group(1);
//				if(!StringUtils.isEmpty(p)){
//					if(p.endsWith(",")){
//						p = p.substring(0, p.length() - 1);
//					}
//					if(p.startsWith("\"")){
//						p = p.substring(1);
//					}
//					if(p.endsWith("\"")){
//						p = p.substring(0, p.length() - 1);
//					}
//					return p;
//				}
//			}
//			return null;
//		}
//		/**
//		 * 生成摘要认证应答请求头信息。
//		 * @return
//		 * 应答请求头信息。
//		 */
//		public String toAuthorization(){
//			if(StringUtils.isEmpty(this.realm) || StringUtils.isEmpty(this.nonce) || StringUtils.isEmpty(this.opaque)) return null;
//			
//			StringBuilder builder = new StringBuilder();
//			String nc = String.format(Locale.getDefault(), "%08d", this.numberCount);
//			this.cnonce = this.generateRadomCode(8);
//			Charset charset = Charset.forName(DigestClientUtil.default_utf8_charset);
//			String ha1 = DigestUtils.md5Hex((this.username + ":" + this.realm + ":" + this.password).getBytes(charset)),
//					  ha2 = DigestUtils.md5Hex((this.method + ":" + this.uri).getBytes(charset));
//			String response = DigestUtils.md5Hex((ha1 + ":" + this.nonce + ":" + nc + ":" + this.cnonce + ":" + this.qop + ":" + ha2).getBytes(charset));
//			
//			builder.append("Digest").append(" ")
//					  .append("username").append("=").append("\"").append(this.username).append("\",")
//					  .append("realm").append("=").append("\"").append(this.realm).append("\",")
//					  .append("nonce").append("=").append("\"").append(this.nonce).append("\",")
//					  .append("uri").append("=").append("\"").append(this.uri).append("\",")
//					  .append("qop").append("=").append("\"").append(this.qop).append("\",")
//					  .append("nc").append("=").append("\"").append(nc).append("\",")
//					  .append("cnonce").append("=").append("\"").append(this.cnonce).append("\",")
//					  .append("response").append("=").append("\"").append(response).append("\",")
//					  .append("opaque").append("=").append("\"").append(this.opaque).append("\"");
//			return builder.toString();
//		}
//		//创建随机数
//		private String generateRadomCode(int length){
//			if(length <= 0) return null;
//			StringBuffer radomCodeBuffer = new StringBuffer();
//			Random random = new Random(System.currentTimeMillis());
//			int i = 0;
//			while(i < length){
//				int t = random.nextInt(123);
//				if(t >= 97 || (t >= 48 && t <= 57)){
//					radomCodeBuffer.append((char)t);
//					i++;
//				}
//			}
//			return radomCodeBuffer.toString();
//		}
//	}
}