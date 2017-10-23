package clb.core.common.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileNameUtil {
	
	private static Logger log = LoggerFactory.getLogger(FileNameUtil.class);

	
	//处理文件名，使其兼容操作系统的文件名
	public static String encodeToFileName(String fileName){
		MessageDigest md5 =null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			log.error("获取MD5工具失败...",e);
			return "";
		}
		byte[] filebytes = md5.digest(fileName.getBytes());
		StringBuffer hexValue = new StringBuffer();  
	    for (int i = 0; i < filebytes.length; i++){  
	         int val = ((int) filebytes[i]) & 0xff;  
	         if (val < 16)hexValue.append("0");  
	         hexValue.append(Integer.toHexString(val));  
	    }  
	    return hexValue.toString();
	}

}
