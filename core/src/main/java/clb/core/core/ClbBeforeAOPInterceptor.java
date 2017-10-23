package clb.core.core;


import clb.core.common.utils.AESUtil;
import clb.core.common.utils.CommonUtil;
import clb.core.core.annotation.SecurityField;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.IRequestAware;
import com.hand.hap.core.annotation.MultiLanguageField;
import com.hand.hap.core.annotation.StdWho;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.mybatis.entity.EntityField;
import com.hand.hap.system.dto.BaseDTO;
import com.hand.hap.system.dto.DTOClassInfo;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.MethodBeforeAdvice;

import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;


public class ClbBeforeAOPInterceptor implements MethodBeforeAdvice {
   
	private Logger logger = LoggerFactory.getLogger(ClbBeforeAOPInterceptor.class);

	public void before(Method method, Object[] args, Object instance)
			throws Throwable {
		try {
			Class[] types = method.getParameterTypes();
			//判断service执行前做update/insert时执行加密
			for (int i = 0; i < types.length; ++i) {
				if (args[i] instanceof HashMap) {
					boolean isLov = false;
					Class clazz = Method.class;
					Field field = clazz.getDeclaredField("clazz");
					field.setAccessible(true);
					Class<?> currentClass = (Class<?>) field.get(method);
					if (currentClass.getName().toUpperCase().indexOf("LOVSERVICE") != -1) {
						isLov = true;
					}
					if (isLov) {
						LinkedHashMap data = (LinkedHashMap) args[i];
						Iterator<Map.Entry> iterator= data.entrySet().iterator();
						while(iterator.hasNext())
						{
							Map.Entry entry = iterator.next();
							if ((entry.getKey().toString().toUpperCase().equals("PHONE"))
									|| (entry.getKey().toString().toUpperCase().equals("PHONECODE"))
									|| (entry.getKey().toString().toUpperCase().equals("APPLICANTPHONE")) 
									|| (entry.getKey().toString().toUpperCase().equals("INSURANTPHONE"))) {
								try {
									String fieldNameSecritValue = null;
									fieldNameSecritValue = AESUtil.encrypt("CLB", entry.getValue().toString());
									entry.setValue(fieldNameSecritValue);
								} catch (Exception e) {
									if (logger.isErrorEnabled()) {
//										logger.error(e.getMessage(), e);
									}
								}
							}
						}
					}
				} else if (args[i] instanceof Collection) {
					Iterator baseDTO = ((Collection) args[i]).iterator();
					while (baseDTO.hasNext()) {
						Object oldDto = baseDTO.next();
						encrypt(oldDto);
					}
				} else if (args[i] instanceof BaseDTO) {
					encrypt(args[i]);
				}
			}
		}catch (Exception e) {
			if (logger.isErrorEnabled()) {
//				logger.error(e.getMessage(), e);
			}
		}
	}

	private void encrypt(Object oldDto){
		if (oldDto instanceof BaseDTO) {
			try {
				DTOClassInfo.getEntityFields(oldDto.getClass()).forEach((fieldName, entityField) -> {
					if (entityField == null) return;
					if (entityField.getAnnotation(SecurityField.class) != null) {
						String fieldNameValue = CommonUtil.classGetMethod(oldDto, fieldName);
//									EntityField[] key =  DTOClassInfo.getIdFields(oldDto.getClass()) ;
//									String keyValue = CommonUtil.classGetMethod(oldDto,key[0].getName());
						String fieldNameSecritValue = null;
						try {
							fieldNameSecritValue = AESUtil.encrypt("CLB", fieldNameValue);
						} catch (Exception e) {
							if (logger.isErrorEnabled()) {
								//logger.error(e.getMessage(), e);
							}
						}
						Object newDto = CommonUtil.classSetMethod(oldDto, fieldName, fieldNameSecritValue);
					}
				});
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
//					logger.error(e.getMessage(), e);
				}
			}
		}
	}
}    