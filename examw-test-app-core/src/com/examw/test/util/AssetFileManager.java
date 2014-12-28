package com.examw.test.util;

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

/**
 * 资源文件管理
 * @author fengwei.
 * @since 2014年12月26日 下午1:54:19.
 */
public class AssetFileManager {
	public static InputStream getAssetFileStream(Context context,String fileName)
	{
		AssetManager assetManager = context.getAssets();
		try
		{
			return assetManager.open(fileName);
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			assetManager.close();
		}
		return null;
	}
	
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
 					Log.i("Unzip: ", "=" + entry);
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
}
