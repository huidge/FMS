package clb.core.common.utils;

import java.awt.Color;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.runtime.directive.Parse;

import clb.core.common.exceptions.CommonException;
/**
 * @name ColorUtil
 * @description 用于将颜色字符串转换成RGB 
 * @author bo.wu@hand-china.com 2017年5月18日17:04:24
 * @version 1.0
 */
public class ColorUtil {
	

	public static int getRed(String colorString) {
		return ParseColor(colorString).getRed();
	}

	public static int getGreen(String colorString) {
		return ParseColor(colorString).getGreen();
	}

	public static int getBlue(String colorString) {
		return ParseColor(colorString).getBlue();
	}
	
	private static Color ParseColor(String colorString){
		if(colorString.contains("#")){
			colorString = StringUtils.substring(colorString,1,colorString.length());
		}
		String red = StringUtils.substring(colorString, 0, 2);
		String green = StringUtils.substring(colorString, 2, 4);
		String blue = StringUtils.substring(colorString, 4, 6);
		return new Color(Integer.parseInt(red,16),Integer.parseInt(green,16),Integer.parseInt(blue,16));
	}

}
