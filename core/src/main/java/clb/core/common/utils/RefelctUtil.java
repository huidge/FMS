package clb.core.common.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import clb.core.common.exceptions.CommonException;
import clb.core.forecast.service.impl.FetActualPaymentServiceImpl;

public class RefelctUtil {
	
	private static Logger log = LoggerFactory.getLogger(RefelctUtil.class);

	
	/**
	 * 获取字段值，无视限定符
	 * @param 字段名
	 * @param 实例
	 **/
	public static Object getFieldValueIgnoreCheck(String fieldName,Object obj){
		Class clazz = obj.getClass();
		Field field = null;
		Object value = null;
		try {
			field = clazz.getDeclaredField(fieldName);
		} catch (Exception e) {
			log.error("获取字段时发生错误:",e);
		}
		field.setAccessible(true);
		try {
			value = field.get(obj);
		} catch (Exception e) {
			log.error("获取字段的值时发生错误:",e);
		}
		return value;
	}
	
	/**
	 * 获取List中值与当前参数相同的元素
	 * @param <T>
	 * @param <T>
	 * 
	 * @param List 待循环的List
	 * @param T 实例
	 * @param String[] 字段
	 **/
	public static <T> List<T> getListDataByFields(List<T> data,T param,String... fields){
		List<T> values = new ArrayList<>();
		Boolean isContain = true;
		if(fields.length == 0)return values;
		for(T t:data){
			isContain = true;
			for(String field:fields){
				Object obj1 = getFieldValueIgnoreCheck(field,t);
				Object obj2 = getFieldValueIgnoreCheck(field,param);
				if(obj1 != null && obj2 != null){
					String d1 = obj1.toString();
					String d2 = obj2.toString();
					if(!d1.equals(d2)){
						isContain = false;
						break;
					}
				}else if(obj1 == null && obj2 == null){
					continue;
				}else{
					isContain = false;
					break;
				}
			}
			if(isContain){
				values.add(t);
			}
		}
		return values;
	}

}
