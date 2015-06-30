package com.examw.test.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import android.annotation.SuppressLint;
import android.util.Log;

import com.examw.codec.binary.Hex;
import com.examw.codec.digest.DigestUtils;
/**
 * 对称加密工具类。
 * @author yangyong.
 * @since 2014-05-09.
 */
public final class AESUtils {
	private static final String TAG = "AESUtil";
	private static final String algorithm = "AES";
	private static final int KEY_SIZE = 32, BLOCK_SIZE = 16;
	/**
	 * 内容对称加密为Hex。
	 * @param content
	 * 明文内容。
	 * @param password
	 * 密码。
	 * @return
	 * 加密后的Hex字符串
	 */
	public static String encryptToHex(String content,String password){
		try {
			Log.d(TAG, "数据加密...");
			byte[] encypt = aes(Cipher.ENCRYPT_MODE, content, password);
			if(ArrayUtils.isNotEmpty(encypt)){
				return Hex.encodeHexString(encypt);
			}
		} catch (Exception e) {
			Log.e(TAG, "数据加密发生异常:" + e.getMessage(), e);
		}
		return null;
	}
	/**
	 * Hex密文解密。
	 * @param hex
	 * 密文内容。
	 * @param password
	 * 密码。
	 * @return
	 *  解密后的明文。
	 * */
	public static String decryptFromHex(String hex,String password){
		try {
			Log.d(TAG, "数据解密...");
			byte[] decypt = aes(Cipher.DECRYPT_MODE, hex, password);
			if(ArrayUtils.isNotEmpty(decypt)){
				return new String(decypt, Hex.DEFAULT_CHARSET);
			}
		} catch (Exception e) {
			Log.e(TAG, "数据解密发生异常:" + e.getMessage(), e);
		}
		return null;
	}
	
	//AES对称加密/解密
	@SuppressLint("TrulyRandom")
	private static byte[] aes(int mode,String data,String password)  throws Exception{
		if(StringUtils.isNotBlank(data) && StringUtils.isNotBlank(password)){	
			//密码进行Sha384处理变为固定长度的字节数组
			byte[] pwds = DigestUtils.sha384(password);
			//截取前32位为密钥
			SecretKeySpec keySpec = new SecretKeySpec(ArrayUtils.subarray(pwds, 0, KEY_SIZE), algorithm);
			//截取前16位为向量
			IvParameterSpec ivSpec = new IvParameterSpec(ArrayUtils.subarray(pwds, 0, BLOCK_SIZE));
			//创建密码器
			Cipher cipher = Cipher.getInstance(algorithm);
			switch(mode){
				case Cipher.ENCRYPT_MODE:{
					//加密处理
					cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
					return cipher.doFinal(data.getBytes(Hex.DEFAULT_CHARSET));
				}
				case Cipher.DECRYPT_MODE:{
					//解密处理
					cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
					byte[] arrays = Hex.decodeHex(data.toCharArray());
					return cipher.doFinal(arrays);
				}
			}
		}
		return null;
	}
}