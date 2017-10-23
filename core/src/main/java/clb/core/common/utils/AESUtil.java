package clb.core.common.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.regex.Pattern;

/**
 * AES加密算法
 */
public class AESUtil {

	public static final long IV = 97531;

	// 可选加密算法AES/DES
	public static final String encryptType = "AES";


	private static byte[] wrap(String algo,byte[] key,byte[] iv,byte[] data,int mode) throws Exception
	{
		Cipher cipher = Cipher.getInstance(algo + "/OFB/NoPadding");
		SecretKeySpec skey = new SecretKeySpec(key,0,Math.min(key.length,Cipher.getMaxAllowedKeyLength(algo)/8),algo);
		IvParameterSpec ivParam = new IvParameterSpec(iv,0,cipher.getBlockSize());
		cipher.init(mode,skey,ivParam);
		return cipher.doFinal(data);
	}
	private static byte[] encrypt(byte[] key,byte[] iv,byte[] data) throws Exception
	{
		return wrap(encryptType,key,iv,data,Cipher.ENCRYPT_MODE);
	}
	private static byte[] decrypt(byte[] key,byte[] iv,byte[] data) throws Exception
	{
		return wrap(encryptType,key,iv,data,Cipher.DECRYPT_MODE);
	}
	private static byte[] key(String key) throws Exception
	{
		return MessageDigest.getInstance("MD5").digest(key.getBytes("UTF-8"));
	}
	private static byte[] iv(long iv)
	{
		byte[] b = new byte[32];
		ByteBuffer.wrap(b).putLong(iv);
		return b;
	}
	public static String encrypt(String key,String text) throws Exception
	{
		if(getBase64(getFromBase64(text)).equals(text)){
			return text;
		}
//		if(Pattern.matches("^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)$",text)){
//			return text;
//		}
		return getBase64(parseByte2HexStr(encrypt(key(key),iv(IV),text.getBytes("UTF-8"))));
	}
	public static String decrypt(String key, String data) throws Exception
	{
		return (new String(decrypt(key(key),iv(IV),parseHexStr2Byte(getFromBase64(data))),"UTF-8"));
	}

	/**将二进制转换成16进制
	 * @param buf
	 * @return
	 */
	public static String parseByte2HexStr(byte buf[]) {

		System.out.println(buf);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}
	/**将16进制转换为二进制
	 * @param hexStr
	 * @return
	 */
	public static byte[] parseHexStr2Byte(String hexStr) {
		if (hexStr.length() < 1)
			return null;
		byte[] result = new byte[hexStr.length()/2];
		for (int i = 0;i< hexStr.length()/2; i++) {
			int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);
			int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}

	// Base64加密
	public static String getBase64(String str) {
		byte[] b = null;
		String s = null;
		try {
			b = str.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (b != null) {
			s = new BASE64Encoder().encode(b);
		}
		return s;
	}

	// Base64解密
	public static String getFromBase64(String s) {
		byte[] b = null;
		String result = null;
		if (s != null) {
			BASE64Decoder decoder = new BASE64Decoder();
			try {
				b = decoder.decodeBuffer(s);
				result = new String(b, "utf-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

//	public static void main(String[] args) throws Exception {
//		System.out.printf("");
//		String algo = "AES";
//		String data = encrypt("mypassword","18627104934");
//		System.out.println(data);
//		String text = decrypt("mypassword",data);
//		System.out.println(text);
//	}

}
