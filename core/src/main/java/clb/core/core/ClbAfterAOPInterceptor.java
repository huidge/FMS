package clb.core.core;

import clb.core.common.utils.AESUtil;
import clb.core.common.utils.CommonUtil;
import clb.core.core.annotation.SecurityField;
import com.hand.hap.system.dto.BaseDTO;
import com.hand.hap.system.dto.DTOClassInfo;

import com.hand.hap.system.dto.ResponseData;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.AfterReturningAdvice;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ClbAfterAOPInterceptor implements AfterReturningAdvice {

	private Logger logger = LoggerFactory.getLogger(ClbAfterAOPInterceptor.class);

	public void afterReturning(Object value, Method method, Object[] args, Object instance) {
		try {
			Class[] types = method.getParameterTypes();

			if (value instanceof ResponseData) {
				value = ((ResponseData) value).getRows();
			}

			// 判断service执行前做select时执行解密
			if (value instanceof List || value instanceof Collection || value instanceof BaseDTO) {

				boolean isLov = false;
				Class clazz = Method.class;
				Field field = clazz.getDeclaredField("clazz");
				field.setAccessible(true);
				Class<?> currentClass = (Class<?>) field.get(method);
				if (currentClass.getName().toUpperCase().indexOf("LOVSERVICE") != -1) {
					isLov = true;
				}
				if (isLov) {
					ArrayList<HashMap> data = (ArrayList<HashMap>) value;
					if (CollectionUtils.isNotEmpty(data)) {
						if (data.get(0) instanceof HashMap) {
							for (int i = 0; i < data.size(); i++) {
								HashMap<String, String> temp = data.get(i);
								for (HashMap.Entry<String, String> entry : temp.entrySet()) {
									if ((entry.getKey().toUpperCase().equals("PHONE"))
											|| (entry.getKey().toUpperCase().equals("PHONECODE")) 
											|| (entry.getKey().toUpperCase().equals("APPLICANTPHONE")) 
											|| (entry.getKey().toUpperCase().equals("INSURANTPHONE"))) {
										try {
											String fieldNameSecritValue = null;
											fieldNameSecritValue = AESUtil.decrypt("CLB", entry.getValue());
											entry.setValue(fieldNameSecritValue);
										} catch (Exception e) {
											if (logger.isErrorEnabled()) {
//												logger.error(e.getMessage(), e);
											}
										}
									}
								}
							}
						}
					}
				}else if(value instanceof BaseDTO){
					decrypt(value);
				}else if(value instanceof Collection){
					Iterator baseDTO = ((Collection) value).iterator();
					while (baseDTO.hasNext()) {
						Object oldDto = baseDTO.next();
						decrypt(oldDto);
					}
				}else if(value instanceof List){
					Iterator baseDTO = ((List) value).iterator();
					while (baseDTO.hasNext()) {
						Object oldDto = baseDTO.next();
						decrypt(oldDto);
					}
				}

			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
//				logger.error(e.getMessage(), e);
			}
		}
	}

	private void decrypt(Object oldDto){
		if (oldDto instanceof BaseDTO) {
			try {
				DTOClassInfo.getEntityFields(oldDto.getClass());
				DTOClassInfo.getEntityFields(oldDto.getClass()).forEach((fieldName, entityField) -> {
					if (entityField.getAnnotation(SecurityField.class) != null) {
						try {
							String fieldNameValue = CommonUtil.classGetMethod(oldDto, fieldName);
							String fieldNameSecritValue = null;
							fieldNameSecritValue = AESUtil.decrypt("CLB", fieldNameValue);
							Object newDto = CommonUtil.classSetMethod(oldDto, fieldName,
									fieldNameSecritValue);
						} catch (Exception e) {
							if (logger.isErrorEnabled()) {
								//logger.error(e./(), e);
							}
						}
					}
				});
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					//logger.error(e.getMessage(), e);
				}
			}
		}
	}
}