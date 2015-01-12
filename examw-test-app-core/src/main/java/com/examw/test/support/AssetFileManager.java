package com.examw.test.support;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.examw.test.util.FileUtils;
import com.examw.test.util.LogUtil;

/**
 * 资源文件管理
 * @author fengwei.
 * @since 2014年12月26日 下午1:54:19.
 */
public class AssetFileManager {
	/**
	 * 解压文件
	 * @param is
	 * @param targetDir
	 */
	public static void Unzip(InputStream is, String targetDir) {
		if(is == null) return;
 		int BUFFER = 4096; // 这里缓冲区我们使用4KB，
 		String strEntry; // 保存每个zip的条目名称
 		try {
 			BufferedOutputStream dest = null; // 缓冲输出流
// 			FileInputStream fis = new FileInputStream(is);
 			ZipInputStream zis = new ZipInputStream(
 					new BufferedInputStream(is));
 			ZipEntry entry; // 每个zip条目的实例
 			while ((entry = zis.getNextEntry()) != null) {
 				try {
 					int count;
 					byte data[] = new byte[BUFFER];
 					strEntry = entry.getName();

 					File entryFile = new File(targetDir  + "/" + strEntry);
 					File entryDir = new File(entryFile.getParent());
 					if (!entryDir.exists()) {
 						entryDir.mkdirs();
 					}

 					FileOutputStream fos = new FileOutputStream(entryFile);
 					dest = new BufferedOutputStream(fos, BUFFER);
 					while ((count = zis.read(data, 0, BUFFER)) != -1) {
 						dest.write(data, 0, count);
 					}
 					dest.flush();
 					dest.close();
 				} catch (Exception ex) {
 					ex.printStackTrace();
 				}
 			}
 			zis.close();
 		} catch (Exception cwj) {
 			cwj.printStackTrace();
 		}
 	}
	
	/**
	 * 复制数据文件到文件安装位置
	 */
	public static void copyDataBase(Context context,String fileName,String dbPath)
	{
		LogUtil.d( String.format("复制[%1$s]数据库到指定应用数据位置[%2$s]",fileName,dbPath));
		AssetManager assetManager = context.getAssets();
		try
		{
			FileUtils.copyFile(assetManager.open(fileName), dbPath);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 复制图片文件夹
	 */
	public static void copyImages(Context context,String imagePath)
	{
		AssetManager assetManager = context.getAssets();
		try
		{
			String[] filePaths = assetManager.list("data/image");
			if(!new File(imagePath).exists())
			{
				new File(imagePath).mkdirs();
			}
			for(String path:filePaths)
			{
				LogUtil.d(path);
				FileUtils.copyFile(assetManager.open("data/image/"+path), imagePath + path);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 解压ZIP包并复制其中的数据
	 * @param context
	 * @param dbPath
	 * @param imagePath
	 */
	public static void upZipFile(Context context,String zipName,String dbName,String dbPath,String imagePath)
	{
		int BUFFER = 4096; // 这里缓冲区我们使用4KB，
 		String strEntry; // 保存每个zip的条目名称
 		AssetManager assetManager = context.getAssets();
 		try {
 			BufferedOutputStream dest = null; // 缓冲输出流
// 			FileInputStream fis = new FileInputStream(is);
 			ZipInputStream zis = new ZipInputStream(
 					new BufferedInputStream(assetManager.open(zipName)));
 			ZipEntry entry; // 每个zip条目的实例
 			while ((entry = zis.getNextEntry()) != null) {
 				try {
 					if(entry.isDirectory()) continue;
 					int count;
 					byte data[] = new byte[BUFFER];
 					strEntry = entry.getName();
 					File targetFile = new File(imagePath);
 					if(!targetFile.exists())
 					{
 						targetFile.mkdirs();
 					}
 					FileOutputStream fos;
 					if(strEntry.equals(dbName))
 					{
 						 fos = new FileOutputStream(dbPath);
 					}else
 					{
 						fos = new FileOutputStream(imagePath + strEntry.substring(strEntry.lastIndexOf("/")+1));
 					}
 					dest = new BufferedOutputStream(fos, BUFFER);
 					while ((count = zis.read(data, 0, BUFFER)) != -1) {
 						dest.write(data, 0, count);
 					}
 					dest.flush();
 					dest.close();
 				} catch (Exception ex) {
 					ex.printStackTrace();
 				}
 			}
 			zis.close();
 		} catch (Exception cwj) {
 			cwj.printStackTrace();
 		}
	}
}
