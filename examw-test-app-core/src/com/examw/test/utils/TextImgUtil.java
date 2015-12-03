package com.examw.test.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.examw.codec.digest.DigestUtils;
import com.examw.test.app.AppConstant;
import com.examw.test.app.AppContext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.util.Log;
import android.widget.TextView;

/**
 * 图文混排工具类。
 * 
 * @author jeasonyoung
 * @since 2015年12月2日
 */
public final class TextImgUtil {
	private static final String TAG = "TextImgUtil";
	private static final Pattern img_regex_pattern = Pattern.compile("<[img|IMG][^>]*src=[\'\"]([^\'\"]+)[^>]*>");
	private static final File img_dir = downloadImageRootDir();
	private static final ExecutorService img_pools = Executors.newCachedThreadPool();
	/**
	 * 图文混排。
	 * @param textView
	 * 容器视图。
	 * @param content
	 * 内容。
	 */
	public static final void textImageView(final TextView textView, final String content){
		if(textView == null) return;
		if(StringUtils.isBlank(content)){
			textView.setText("");
			return;
		}
		//html处理
		textView.setText(Html.fromHtml(content, new ImageGetter() {
			/*
			 * 加载图片。
			 * @see android.text.Html.ImageGetter#getDrawable(java.lang.String)
			 */
			@Override
			public Drawable getDrawable(String source) {
				Log.d(TAG, "加载图文图片=>" + source);
				if(StringUtils.isNotBlank(source)){
					String path = source;
					if(path.indexOf('|') > 0){
						path = path.split("\\|")[0];
					}
					//加载本地图片
					final Bitmap img = loadLocalImage(path);
					if(img != null){
						 int maxWidth =  textView.getWidth();
						 final AppContext appContext = (AppContext)AppContext.getContext();
						 if(appContext != null){
							 final Point size = appContext.getScreenSize();
							 if(size != null) maxWidth = size.x;
						 }
						 int width = img.getWidth();
						 int height = img.getHeight();
						 if(maxWidth > 0 && width > maxWidth){
							 width = maxWidth;
							 height = (int)((float)img.getWidth()/(float)img.getHeight()) * width;
						 }
						final Drawable d = new BitmapDrawable(null,img);
						d.setBounds(0, 0, width, height);
						return d;
					}
				}
				return null;
			}
			
		}, null));
	}
	
	//加载本地图片
	private static Bitmap loadLocalImage(String path){
		try{
			 if(StringUtils.isBlank(path)) return null;
			 final File imgFile = new File(path);
			 if(imgFile.exists()){//图片文件存在
				 return BitmapFactory.decodeStream(new FileInputStream(imgFile));
				 //return new BitmapDrawable(null, new FileInputStream(imgFile));
			 }
		}catch(Exception e){
			Log.e(TAG, "加载本地图片["+path+"]异常:" + e.getMessage(), e);
		}
		return null;
	}
	
	/**
	 * 查找网络图片替换成本地图片。
	 * @param content
	 * 原始内容。
	 * @return 
	 * 替换后的内容。
	 */
	public static final String findImgReplaceLocal(final String content){
		if(StringUtils.isBlank(content)) return content;
		String result = content;
		final Matcher m =	img_regex_pattern.matcher(content);
	    while(m.find()){
	    	Log.d(TAG, "匹配图片=>" + m.group());
	    	final String url = m.group(1);
	    	if(StringUtils.isBlank(url)) continue;
	    	if(url.indexOf('|') > 0){//已替换
	    		final String[] paths = url.split("\\|");
	    		final File f = new File(paths[0]);
	    		if(!f.exists()){//已下载的图片不存在
	    			//重新下载图片
	    			downloadImage(paths[1], new File(paths[0]));
	    		}
	    	}else{
	    		 final File path = new File(img_dir, DigestUtils.md5Hex(url));
	    		 final String img = "<img src='"+path.getAbsolutePath()+"|"+ url +"'/>";
	    		 Log.d(TAG, "替换的img=>" + img);
	    		 //替换
	    		 result = result.replace(m.group(), img);
	    		 //下载
	    		 downloadImage(url, path);
	    	}
	    }
		return result;
	}
	
	//下载图片
	private static final void downloadImage(final String url, final File path){
		if(StringUtils.isBlank(url) || path == null) return;
		//开启异步线程下载图片
		img_pools.execute(new Runnable() {
			/*
			 * 执行多线程异步下载图片
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run() {
				try{
					//判断图片是否存在
					if(path.exists()){//图片存在无须下载
						return;
					}
					//检查文件路径
					final File dir = path.getParentFile();
					if(!dir.exists()){
						Log.d(TAG, "创建试题图片目录=>" + dir.getAbsolutePath());
						dir.mkdirs();
					}
					//构建下载url
					String http_url = url;
					final String host = AppConstant.APP_ITEM_IMG_HOST;
					if(!url.startsWith(host)){
						http_url = host + (http_url.startsWith("/") ? http_url : "/" + http_url);
					}
					//下载图片
					Log.d(TAG, "图片下载真实地址:" + http_url);
					final URL uri = new URL(http_url);
					final HttpURLConnection conn = (HttpURLConnection)uri.openConnection();
					conn.setConnectTimeout(5000);
					conn.setRequestMethod("GET");
					if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
						//获取下载流
						final InputStream is = conn.getInputStream();
						//获取文件写入流
						final FileOutputStream fos = new FileOutputStream(path);
						//读取文件
						final byte[] buf = new byte[1024];
						int len = 0;
						while((len = is.read(buf)) != -1){
							fos.write(buf, 0, len);
						}
						//关闭下载流
						is.close();
						//关闭写入流
						fos.close();
					}
				}catch(Exception e){
					Log.e(TAG, "下载图片["+url+"]异常:" + e.getMessage(), e);
				}
			}
		});
	}
	//获取下载图片文件根目录
	private static final File downloadImageRootDir(){
		final Context context = AppContext.getContext();
		if(AppContext.hasExistSDCard()){
			return new File(new File(Environment.getExternalStorageDirectory(), context.getPackageName()), AppConstant.APP_ITEM_IMG_DIR);
		}else{
			return new File(context.getFilesDir(), AppConstant.APP_ITEM_IMG_DIR);
		}
	}
}