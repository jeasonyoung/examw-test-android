package com.examw.test.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.client.params.ClientPNames;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.examw.test.app.AppContext;
import com.examw.test.exception.AppException;
import com.examw.test.support.URLs;

/**
 * http工具类封装
 * 
 * @author fengwei.
 * @since 2014年11月28日 下午1:51:46.
 */
public class HttpUtils {
	private static final String TAG = "HttpUtils";
	public static final String UTF_8 = "UTF-8";
	public static final String DESC = "descend";
	public static final String ASC = "ascend";

	public static String username;
	public static String password;

	private final static int TIMEOUT_CONNECTION = 20000;
	private final static int TIMEOUT_SOCKET = 20000;
	private final static int RETRY_TIME = 3;

	private static String appCookie;
	private static String appUserAgent;

	private static final String default_utf8_charset = "UTF-8",
			authenticate_header = "WWW-Authenticate",
			authorization_header = "Authorization";
	private static final int max_http_send_count = 3;

	/**
	 * 清除cookie
	 */
	public static void cleanCookie() {
		appCookie = "";
	}

	/**
	 * 获取保存的cookie
	 * 
	 * @param appContext
	 * @return
	 */
	private static String getCookie(AppContext appContext) {
		if (appCookie == null || appCookie == "") {
			appCookie = appContext.getProperty("cookie");
		}
		return appCookie;
	}
	
	/**
	 * 获取配置文件中的校验用户
	 * @param appContext
	 */
	private static void getDigestUser(AppContext appContext) {
		if(username == null || password == null)
		{
			ApplicationInfo appInfo;
			try {
				appInfo = appContext.getPackageManager()
						.getApplicationInfo(appContext.getPackageName(),
								PackageManager.GET_META_DATA);
				username = appInfo.metaData.getString("username");
				password = appInfo.metaData.getString("password");
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		
	}

	/**
	 * 获取用户的客户端信息
	 * 
	 * @param appContext
	 * @return
	 */
	private static String getUserAgent(AppContext appContext) {
		if (appUserAgent == null || appUserAgent == "") {
			StringBuilder ua = new StringBuilder("examw.com");
			ua.append('/' + appContext.getPackageInfo().versionName + '_'
					+ appContext.getPackageInfo().versionCode);// App版本
			ua.append("/Android");// 手机系统平台
			ua.append("/" + android.os.Build.VERSION.RELEASE);// 手机系统版本
			ua.append("/" + android.os.Build.MODEL); // 手机型号
			ua.append("/" + appContext.getAppId());// 客户端唯一标识
			appUserAgent = ua.toString();
		}
		return appUserAgent;
	}

	/**
	 * 获取httpClient (默认一些设置)
	 * 
	 * @return
	 */
	public static HttpClient getHttpClient() {
		HttpClient httpClient = new HttpClient();
		// 设置 HttpClient 接收 Cookie,用与浏览器一样的策略
		httpClient.getParams().setCookiePolicy(
				CookiePolicy.BROWSER_COMPATIBILITY);
		// 设置 默认的超时重试处理策略
		httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());
		// 设置 连接超时时间
		httpClient.getHttpConnectionManager().getParams()
				.setConnectionTimeout(TIMEOUT_CONNECTION);
		// 设置 读数据超时时间
		httpClient.getHttpConnectionManager().getParams()
				.setSoTimeout(TIMEOUT_SOCKET);
		// 设置 字符集
		httpClient.getParams().setContentCharset(UTF_8);
		// 设置重定向
		httpClient.getParams().setParameter(
				ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
		return httpClient;
	}

	public static GetMethod getHttpGet(String url, String cookie,
			String userAgent) {
		GetMethod httpGet = new GetMethod(url);
		// 设置 请求超时时间
		httpGet.getParams().setSoTimeout(TIMEOUT_SOCKET);
		httpGet.setRequestHeader("Host", URLs.HOST);
		httpGet.setRequestHeader("Connection", "Keep-Alive");
		httpGet.setRequestHeader("Cookie", cookie);
		httpGet.setRequestHeader("User-Agent", userAgent);
		return httpGet;
	}

	private static PostMethod getHttpPost(String url, String cookie,
			String userAgent) {
		PostMethod httpPost = new PostMethod(url);
		// 设置 请求超时时间
		httpPost.getParams().setSoTimeout(TIMEOUT_SOCKET);
		httpPost.setRequestHeader("Host", URLs.HOST);
		httpPost.setRequestHeader("Connection", "Keep-Alive");
		httpPost.setRequestHeader("Cookie", cookie);
		httpPost.setRequestHeader("User-Agent", userAgent);
		return httpPost;
	}

	public static String _MakeURL(String p_url, Map<String, Object> params) {
		StringBuilder url = new StringBuilder(p_url);
		if (url.indexOf("?") < 0)
			url.append('?');

		for (String name : params.keySet()) {
			url.append('&');
			url.append(name);
			url.append('=');
			url.append(String.valueOf(params.get(name)));
			// 不做URLEncoder处理
			// url.append(URLEncoder.encode(String.valueOf(params.get(name)),
			// UTF_8));
		}

		return url.toString().replace("?&", "?");
	}

	/**
	 * get请求URL
	 * 
	 * @param url
	 * @throws AppException
	 */
	private static String _get(AppContext appContext, String url,
			DigestAuthcProvider provider) throws AppException {
		String cookie = getCookie(appContext);
		String userAgent = getUserAgent(appContext);
		HttpClient httpClient = null;
		GetMethod httpGet = null;
		String responseBody = "";
		try {
			httpClient = getHttpClient();
			httpGet = getHttpGet(url, cookie, userAgent);
			String authz = provider.toAuthorization();
			if (!StringUtils.isEmpty(authz)) {
				httpGet.setRequestHeader(authorization_header, authz);
			}
			int status = httpClient.executeMethod(httpGet);
			// 401
			if (status == HttpURLConnection.HTTP_UNAUTHORIZED) {
				if (provider.getNumberCount() > max_http_send_count) {
					throw AppException.http(status);
				}
				String authc = httpGet.getResponseHeader(authenticate_header)
						.getValue();
				Log.d(TAG, String.format("获取HTTP摘要认证头信息：%1$s=%2$s",
						authenticate_header, authc));
				if (StringUtils.isEmpty(authc))
					throw new RuntimeException("获取摘要认证头信息失败！");
				provider.parser(authc);
				httpGet.releaseConnection();
				httpClient = null;
				return _get(appContext, url, provider);
			}
			// 200
			if (status == HttpURLConnection.HTTP_OK) {
				responseBody = changeInputStream2String(httpGet
						.getResponseBodyAsStream());
				httpGet.releaseConnection();
				httpClient = null;
				return responseBody;
			} else {
				Log.d(TAG,"返回的状态码:"+status);
				throw AppException.http(status);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw AppException.http(e);
		} catch (Exception e) {
			e.printStackTrace();
			throw AppException.http(e);
		}
	}

	public static String http_get(AppContext appContext, String url)
			throws AppException {
		int time = 0;
		String responseBody = "";
		getDigestUser(appContext);
		do {
			Log.d(TAG, String.format("正在进行第[%d]次请求", time));
			try {
				DigestAuthcProvider provider = new DigestAuthcProvider(
						username, password, "GET", url);
				responseBody = _get(appContext, url, provider);
				break;
			} catch (Exception e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生网络异常
				e.printStackTrace();
				throw AppException.network(e);
			}
		} while (time < RETRY_TIME);
		Log.d(TAG, "响应:" + responseBody);
		return responseBody;
	}
	
	public static String http_post(AppContext appContext, String url,Object obj)
			throws AppException {
		Log.d(TAG,"post请求地址:"+url);
		int time = 0;
		String responseBody = "";
		getDigestUser(appContext);
		do {
			Log.d(TAG, String.format("正在进行第[%1$d]次请求 [url = %2$s]", time,url));
			try {
				DigestAuthcProvider provider = new DigestAuthcProvider(
						username, password, "GET", url);
				responseBody = _post(appContext, url,obj, provider);
				break;
			} catch (Exception e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生网络异常
				e.printStackTrace();
				throw AppException.network(e);
			}
		} while (time < RETRY_TIME);
		Log.d(TAG, "响应:" + responseBody);
		return responseBody;
	}
	/**
	 * 传递对象
	 * @param appContext
	 * @param url
	 * @param obj
	 * @param provider
	 * @return
	 * @throws AppException
	 */
	public static String _post(AppContext appContext, String url,Object obj,DigestAuthcProvider provider) throws AppException
	{
		String cookie = getCookie(appContext);
		String userAgent = getUserAgent(appContext);

		HttpClient httpClient = null;
		PostMethod httpPost = null;
		String responseBody = "";
		try {
			httpClient = getHttpClient();
			httpClient = getHttpClient();
			httpPost = getHttpPost(url, cookie, userAgent);
			String authz = provider.toAuthorization();
			httpPost.setRequestEntity(new StringRequestEntity(GsonUtil.objectToJson(obj),"application/json","UTF-8"));
			if (!StringUtils.isEmpty(authz)) {
				httpPost.setRequestHeader(authorization_header, authz);
			}
			httpPost.setRequestHeader("Content-type","application/json;charset=UTF-8");
			int status = httpClient.executeMethod(httpPost);
			// 401
			if (status == HttpURLConnection.HTTP_UNAUTHORIZED) {
				if (provider.getNumberCount() > max_http_send_count) {
					throw AppException.http(status);
				}
				String authc = httpPost.getResponseHeader(authenticate_header)
						.getValue();
				Log.d(TAG, String.format("获取HTTP摘要认证头信息：%1$s=%2$s",
						authenticate_header, authc));
				if (StringUtils.isEmpty(authc))
					throw new RuntimeException("获取摘要认证头信息失败！");
				provider.parser(authc);
				httpPost.releaseConnection();
				httpClient = null;
				return _post(appContext, url,obj, provider);
			}
			// 200
			if (status == HttpURLConnection.HTTP_OK) {
				responseBody = changeInputStream2String(httpPost
						.getResponseBodyAsStream());
				httpPost.releaseConnection();
				httpClient = null;
				return responseBody;
			} else {
				throw AppException.http(status);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw AppException.http(e);
		} catch (Exception e) {
			e.printStackTrace();
			throw AppException.http(e);
		}
	}
	/**
	 * 公用post方法
	 * 
	 * @param url
	 * @param params
	 * @param files
	 * @throws AppException
	 */
	public static String _post(AppContext appContext, String url,
			Map<String, Object> params, Map<String, File> files,
			DigestAuthcProvider provider) throws AppException {
		// System.out.println("post_url==> "+url);
		String cookie = getCookie(appContext);
		String userAgent = getUserAgent(appContext);

		HttpClient httpClient = null;
		PostMethod httpPost = null;

		// post表单参数处理
		int length = (params == null ? 0 : params.size())
				+ (files == null ? 0 : files.size());
		Part[] parts = new Part[length];
		int i = 0;
		if (params != null)
			for (String name : params.keySet()) {
				parts[i++] = new StringPart(name, String.valueOf(params
						.get(name)), UTF_8);
				// System.out.println("post_key==> "+name+"    value==>"+String.valueOf(params.get(name)));
			}
		if (files != null)
			for (String file : files.keySet()) {
				try {
					parts[i++] = new FilePart(file, files.get(file));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				// System.out.println("post_key_file==> "+file);
			}
		String responseBody = "";
		try {
			httpClient = getHttpClient();
			httpClient = getHttpClient();
			httpPost = getHttpPost(url, cookie, userAgent);
			String authz = provider.toAuthorization();
			httpPost.setRequestEntity(new MultipartRequestEntity(parts,
					httpPost.getParams()));
			if (!StringUtils.isEmpty(authz)) {
				httpPost.setRequestHeader(authorization_header, authz);
			}
			int status = httpClient.executeMethod(httpPost);
			// 401
			if (status == HttpURLConnection.HTTP_UNAUTHORIZED) {
				if (provider.getNumberCount() > max_http_send_count) {
					throw AppException.http(status);
				}
				String authc = httpPost.getResponseHeader(authenticate_header)
						.getValue();
				Log.d(TAG, String.format("获取HTTP摘要认证头信息：%1$s=%2$s",
						authenticate_header, authc));
				if (StringUtils.isEmpty(authc))
					throw new RuntimeException("获取摘要认证头信息失败！");
				provider.parser(authc);
				httpPost.releaseConnection();
				httpClient = null;
				return _get(appContext, url, provider);
			}
			// 200
			if (status == HttpURLConnection.HTTP_OK) {
				responseBody = changeInputStream2String(httpPost
						.getResponseBodyAsStream());
				httpPost.releaseConnection();
				httpClient = null;
				return responseBody;
			} else {
				throw AppException.http(status);
			}
		} catch (IOException e) {
			throw AppException.http(e);
		} catch (Exception e) {
			throw AppException.http(e);
		}
	}

	private static String changeInputStream2String(InputStream in) {
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		try {
			StringBuffer buf = new StringBuffer();
			String line = null;
			while ((line = br.readLine()) != null) {
				buf.append(line);
			}
			return buf.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 获取网络图片
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getNetBitmap(String url) throws AppException {
		HttpClient httpClient = null;
		GetMethod httpGet = null;
		Bitmap bitmap = null;
		int time = 0;
		do {
			try {
				httpClient = getHttpClient();
				httpGet = getHttpGet(url, null, null);
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
				if (time < RETRY_TIME) {
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
				if (time < RETRY_TIME) {
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
		} while (time < RETRY_TIME);
		return bitmap;
	}

	/**
	 * 摘要认证提供者
	 * 
	 * @author yangyong
	 * @since 2014年12月22日
	 */
	static class DigestAuthcProvider {
		private String username, password, realm, nonce, method, uri,
				qop = "auth", cnonce, opaque;
		private int numberCount = 0;

		/**
		 * 构造函数。
		 * 
		 * @param username
		 *            用户名。
		 * @param password
		 *            密码。
		 * @param method
		 *            请求方法。
		 * @param uri
		 *            请求地址。
		 */
		public DigestAuthcProvider(String username, String password,
				String method, String uri) {
			this.username = username;
			this.password = password;
			this.method = method;
			this.uri = uri;
		}

		/**
		 * 获取Uri。
		 * 
		 * @return Uri。
		 */
		public String getUri() {
			return this.uri;
		}

		/**
		 * 获取请求方法名称。
		 * 
		 * @return 请求方法名称。
		 */
		public String getMethod() {
			return this.method;
		}

		/**
		 * 获取计数器。
		 * 
		 * @return 计数器。
		 */
		public int getNumberCount() {
			return numberCount;
		}

		/**
		 * 分析认证头数据。
		 * 
		 * @param authz
		 *            认证头数据。
		 */
		public void parser(String authc) {
			this.realm = this.getParameter(authc, "realm");
			if (StringUtils.isEmpty(this.realm)) {
				throw new RuntimeException("从请求头信息中获取参数［realm］失败");
			}
			this.nonce = this.getParameter(authc, "nonce");
			if (StringUtils.isEmpty(this.nonce)) {
				throw new RuntimeException("从请求头信息中获取参数［nonce］失败");
			}
			this.opaque = this.getParameter(authc, "opaque");
			if (StringUtils.isEmpty(this.opaque)) {
				throw new RuntimeException("从请求头信息中获取参数［opaque］失败");
			}
			this.numberCount += 1;
		}

		// 获取参数
		private String getParameter(String authz, String name) {
			if (StringUtils.isEmpty(authz) || StringUtils.isEmpty(name))
				return null;
			String regex = name + "=((.+?,)|((.+?)$))";
			Matcher m = Pattern.compile(regex).matcher(authz);
			if (m.find()) {
				String p = m.group(1);
				if (!StringUtils.isEmpty(p)) {
					if (p.endsWith(",")) {
						p = p.substring(0, p.length() - 1);
					}
					if (p.startsWith("\"")) {
						p = p.substring(1);
					}
					if (p.endsWith("\"")) {
						p = p.substring(0, p.length() - 1);
					}
					return p;
				}
			}
			return null;
		}

		/**
		 * 生成摘要认证应答请求头信息。
		 * 
		 * @return 应答请求头信息。
		 */
		public String toAuthorization() {
			if (StringUtils.isEmpty(this.realm)
					|| StringUtils.isEmpty(this.nonce)
					|| StringUtils.isEmpty(this.opaque))
				return null;

			StringBuilder builder = new StringBuilder();
			String nc = String.format(Locale.CHINA,"%08d", this.numberCount);
			this.cnonce = this.generateRadomCode(8);
			Charset charset = Charset.forName(HttpUtils.default_utf8_charset);
			String ha1 = DigestUtils.md5DigestAsHex((this.username + ":"
					+ this.realm + ":" + this.password).getBytes(charset)), ha2 = DigestUtils
					.md5DigestAsHex((this.method + ":" + this.uri)
							.getBytes(charset));
			String response = DigestUtils.md5DigestAsHex((ha1 + ":"
					+ this.nonce + ":" + nc + ":" + this.cnonce + ":"
					+ this.qop + ":" + ha2).getBytes(charset));

			builder.append("Digest").append(" ").append("username").append("=")
					.append("\"").append(this.username).append("\",")
					.append("realm").append("=").append("\"")
					.append(this.realm).append("\",").append("nonce")
					.append("=").append("\"").append(this.nonce).append("\",")
					.append("uri").append("=").append("\"").append(this.uri)
					.append("\",").append("qop").append("=").append("\"")
					.append(this.qop).append("\",").append("nc").append("=")
					.append("\"").append(nc).append("\",").append("cnonce")
					.append("=").append("\"").append(this.cnonce).append("\",")
					.append("response").append("=").append("\"")
					.append(response).append("\",").append("opaque")
					.append("=").append("\"").append(this.opaque).append("\"");
			return builder.toString();
		}

		// 创建随机数
		private String generateRadomCode(int length) {
			if (length <= 0)
				return null;
			StringBuffer radomCodeBuffer = new StringBuffer();
			Random random = new Random(System.currentTimeMillis());
			int i = 0;
			while (i < length) {
				int t = random.nextInt(123);
				if (t >= 97 || (t >= 48 && t <= 57)) {
					radomCodeBuffer.append((char) t);
					i++;
				}
			}
			return radomCodeBuffer.toString();
		}
	}

	public static String login(AppContext appContext,String url,Map<String,Object> params) throws AppException
	{
		String cookie = getCookie(appContext);
		String userAgent = getUserAgent(appContext);

		HttpClient httpClient = null;
		GetMethod httpGet = null;
		url = _MakeURL(url, params);
		Log.d(TAG,url);
		String responseBody = "";
		int time = 0;
		do {
			try {
				httpClient = getHttpClient();
				httpGet = getHttpGet(url, cookie, userAgent);
				int statusCode = httpClient.executeMethod(httpGet);
				if (statusCode != HttpStatus.SC_OK) {
					throw AppException.http(statusCode);
				}
				// ///////////////////////////////////////
				InputStream in = httpGet.getResponseBodyAsStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(in,"GBK"));
				try {
					StringBuffer buf = new StringBuffer();
					String line = null;
					while ((line = br.readLine()) != null) {
						buf.append(line);
					}
				responseBody = buf.toString();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				break;
			} catch (HttpException e) {
				e.printStackTrace();
				time++;
				if (time < RETRY_TIME) {
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
				e.printStackTrace();
				time++;
				if (time < RETRY_TIME) {
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
				if (httpGet != null)
					httpGet.releaseConnection();
				httpClient = null;
			}
		} while (time < RETRY_TIME);
		return responseBody;
	}
}