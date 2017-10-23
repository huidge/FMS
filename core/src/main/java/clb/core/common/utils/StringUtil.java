package clb.core.common.utils;

import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
	
	public static String getHostFromURL(String url){
		String host = "";
		Pattern p = Pattern.compile("(?<=//|)((\\w)+\\.)+\\w+");  
        Matcher matcher = p.matcher(url);  
        if (matcher.find()) {  
            host = matcher.group();  
        }  
		return host; 
	}

	public static String listConvertStr(List<String>numList){
		String numStr="";
		for(int i=0;i<numList.size();i++){
			if(i==(numList.size()-1)){
				numStr+=numList.get(i);
			}else{
				numStr=numStr+numList.get(i)+",";
			}
		}
		return numStr;
	}
	
	public static boolean isNumeric(String s) {
	    if (s != null && !"".equals(s.trim()))
	        return s.matches("^[0-9]*$");
	    else
	        return false;
	}

	/**
	  * 生成随机数<br>
	  * GUID： 即Globally Unique Identifier（全球唯一标识符） 也称作 UUID(Universally Unique
	  * IDentifier) 。
	  *
	  * 所以GUID就是UUID。
	  *
	  * GUID是一个128位长的数字，一般用16进制表示。算法的核心思想是结合机器的网卡、当地时间、一个随即数来生成GUID。
	  *
	  * 从理论上讲，如果一台机器每秒产生10000000个GUID，则可以保证（概率意义上）3240年不重复。
	  *
	  * @return
	  */
	public static String randomUUID() {
		return UUID.randomUUID().toString().replace("-", "");
	}
}
