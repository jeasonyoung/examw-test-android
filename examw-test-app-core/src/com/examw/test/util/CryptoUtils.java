package com.examw.test.util;

import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.annotation.SuppressLint;

/**
 * 
 * @author fengwei.
 * @since 2015年3月18日 上午11:15:19.
 */
public class CryptoUtils {

	public static final String ALGORITHM_AES = "AES/CBC/PKCS7Padding";//"DES/CBC/PKCS5Padding";
	static final String CharsetUTF8 = "UTF-8";
	private static final String PREFIX = "0x";
	
    /**
     * DES算法，加密
     *
     * @param data 待加密字符串
     * @param key  加密私钥，长度不能够小于8位
     * @return 加密后的字节数组，一般结合Base64编码使用
     * @throws InvalidAlgorithmParameterException 
     * @throws Exception 
     */
    @SuppressLint("TrulyRandom")
	public static String encrypto(String pwd,String data) {
    	if(pwd == null || data == null)return null;
    	try {
    		CrptoKeyAndIv keyAndIv = createKeyAndVi(pwd);
    		Key key = new SecretKeySpec(keyAndIv.key,ALGORITHM_AES);
    		IvParameterSpec iv = new IvParameterSpec(keyAndIv.iv);
    		Cipher cipher = Cipher.getInstance(ALGORITHM_AES);
    		cipher.init(Cipher.ENCRYPT_MODE, key, iv);
    		byte[] buf = cipher.doFinal(data.getBytes(CharsetUTF8));
    		return  PREFIX + HexUtil.parseBytesHex(buf);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
    }
	public static String decrypto(String pwd,String data) {
    	if(pwd == null || data == null)return null;
    	try {
    		if(data.startsWith(PREFIX))
    		{
    			data = data.substring(2);
    		}
    		byte[] sources = HexUtil.parseHexBytes(data);
    		CrptoKeyAndIv keyAndIv = createKeyAndVi(pwd);
    		Key key = new SecretKeySpec(keyAndIv.key,ALGORITHM_AES);
    		IvParameterSpec iv = new IvParameterSpec(keyAndIv.iv);
    		Cipher cipher = Cipher.getInstance(ALGORITHM_AES);
    		cipher.init(Cipher.DECRYPT_MODE, key, iv);
    		byte[] buf = cipher.doFinal(sources);
    		return  new String(buf);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
    }
    static class CrptoKeyAndIv{
    	public byte[] key;
    	public byte[] iv;
    }
    private static CrptoKeyAndIv createKeyAndVi(String pwd) throws Exception {
    	if(pwd == null || pwd.length() == 0) return null;
    	MessageDigest messageDigest = MessageDigest.getInstance("SHA-384");
    	byte[] data = messageDigest.digest(pwd.getBytes(Charset.forName(CharsetUTF8)));
    	if(data.length >= 32){
    		CrptoKeyAndIv result = new CrptoKeyAndIv();
    		result.key = Arrays.copyOfRange(data, 0, 32);
    		result.iv = Arrays.copyOfRange(data, 0, 16);
    		return result;
    	}
    	return null;
    }
}
