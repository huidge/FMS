package clb.core.common.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Configuration;
import freemarker.template.Template;
/*****
 * @author tiansheng.ye
 * @Date 2017-05-17
 */
@SuppressWarnings("rawtypes")
public class CommonUtil {

	private static Logger logger=LoggerFactory.getLogger(CommonUtil.class);

	public static final String updateType = "UPDATE";
	public static final String insertType = "INSERT";
	public static final String selectType = "SELECT";
	public static final String queryType = "QUERY";


	/***
	 * 打印异常日志
	 * @param e
	 */
	public static void printStackTraceToStr(Exception e) {
		logger.error("系统报错:", e);
	}
	/*****
	 * 堆栈错误信息转换成字符串
	 * @param e
	 * @return
	 */
	public static String retStackTraceToStr(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}
	
	/*******
	 * 替换模板信息 freemarker
	 * @param content 内容
	 * @param data 替换信息
	 * @return
	 */
	public static String translateData(String content, Map data){
		String  ret="";
        if (content != null) {
        	try (StringWriter out = new StringWriter()) {
        		@SuppressWarnings("deprecation")
				Configuration config = new Configuration();
        		config.setDefaultEncoding("utf-8");
        		// classic compatible,是${abc}允许出现空值的
        		config.setClassicCompatible(true);
        		Template template = new Template(null, content, config);
        		template.process(data, out);
        		ret= out.toString();
        	} catch (Exception e) {
        		printStackTraceToStr(e);
        	}
        }
		return ret;
    }
	/***
	 * 随机生成 num 为随机数
	 * @param num
	 * @return
	 */
	public static String ramdomCode(int num){
		StringBuffer code=new StringBuffer();
		for(int i=0;i<num;i++){
			code.append(new Random().nextInt(9));
		}
		return code.toString();
	}
	
	public static String getMethodValue(Object c,String fieldCode){
		Field[ ] fields = c.getClass().getDeclaredFields( );
		String value=null;
		for(Field field:fields){
			//setAccessible( true ) :类中的成员变量为private,故必须进行此操作 
			field.setAccessible( true );
			try {
				if(field.get(c)!=null && !field.get(c).toString().equals("null")){
					if(field.getName().equals(fieldCode)){
						value= field.get(c)+"";
						break;
					}
				}
			} catch (Exception e) {
				printStackTraceToStr(e);
			}
		}
		return value;
	}
	/******
	 * 生产流水单号
	 * @return
	 */
	public static String getTradeNo(){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format(new Date())+(new Random().nextInt(89)+10);
	}
	
	/******
	 * Class转为map
	 */
	public static Map<String, Object> classToMap(Object o){
		Field[ ] fields = o.getClass().getDeclaredFields( );
		Map<String, Object>map=new HashMap<String, Object>();
		try {
			for(Field field:fields){
				//setAccessible( true ) :类中的成员变量为private,故必须进行此操作 
				field.setAccessible( true );
				map.put(field.getName(), field.get(o));
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return map;
	}
	/******
	 * Class转为map ,map的 value都是string
	 */
	public static Map<String, String> classToMapOfValueStr(Object o){
		Field[ ] fields = o.getClass().getDeclaredFields( );
		Map<String, String>map=new HashMap<String, String>();
		try {
			for(Field field:fields){
				//setAccessible( true ) :类中的成员变量为private,故必须进行此操作 
				field.setAccessible( true );
				map.put(field.getName(), field.get(o)+"");
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return map;
	}
	/******
	 * Class转为map
	 */
	public static Map<String, String> classToMapIgnoreNull(Object o){
		Field[ ] fields = o.getClass().getDeclaredFields( );
		Map<String, String>map=new HashMap<String, String>();
		try {
			for(Field field:fields){
				//setAccessible( true ) :类中的成员变量为private,故必须进行此操作 
				field.setAccessible( true );
				if(field.get(o)!=null && !field.get(o).toString().toLowerCase().equals("null")){
					map.put(field.getName(), field.get(o)+"");
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	/** 
     * 将map转变成Class 
     * @param map 
     * @param cls 
     * @return 
     * @throws Exception 
     */  
	public static <T> T mapToClass(Map<String, String> map,Class<T> cls){  
		try {
			T mycls = cls.newInstance();
			Field[ ] fields = cls.getDeclaredFields( );
			for(Field field:fields){
				//setAccessible( true ) :类中的成员变量为private,故必须进行此操作 
				field.setAccessible( true );
				if(map.get(field.getName())!=null){
					String firstLetter = field.getName().substring(0, 1).toUpperCase();  
	                String lastLetter = field.getName().substring(1); 
					String setter = "set" + firstLetter + lastLetter;  
	                Method method = mycls.getClass().getMethod(setter, new Class[] {field.getType()}); 
	                if(field.getType().getName().lastIndexOf("String")>0){
	                	method.invoke(mycls, map.get(field.getName())+"");
	                }else if(field.getType().getName().lastIndexOf("Long")>0){
	                	method.invoke(mycls, Long.valueOf(map.get(field.getName())+""));
	                }else if(field.getType().getName().lastIndexOf("Boolean")>0){
	                	method.invoke(mycls, Boolean.valueOf(map.get(field.getName())+""));
	                }else if(field.getType().getName().lastIndexOf("Double")>0){
	                	method.invoke(mycls, Double.valueOf(map.get(field.getName())+""));
	                }else if(field.getType().getName().lastIndexOf("Integer")>0){
	                	method.invoke(mycls, Integer.valueOf(map.get(field.getName())+""));
	                }else if(field.getType().getName().lastIndexOf("Short")>0){
	                	method.invoke(mycls, Short.valueOf(map.get(field.getName())+""));
	                }else if(field.getType().getName().lastIndexOf("Byte")>0){
	                	method.invoke(mycls, Byte.valueOf(map.get(field.getName())+""));
	                }
				}
			}
			return mycls;
		} catch (Exception e) {
			printStackTraceToStr(e);
		}
        return null;  
    }

	/**
	 * Map转换层Bean，使用泛型免去了类型转换的麻烦。
	 * @param <T>
	 * @param map
	 * @param class1
	 * @return
	 */
	public static <T> T map2Bean(Map<String, String> map, Class<T> class1) {
		T bean = null;
		try {
			bean = class1.newInstance();
			BeanUtils.populate(bean, map);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return bean;
	}


	/**
     * 检测邮箱地址是否合法 
     * @param email 
     * @return true合法 false不合法 
     */  
    public static boolean isEmail(String email){  
          if (null==email || "".equals(email)) return false;    
          Pattern p =  Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");//复杂匹配  
          Matcher m = p.matcher(email);  
          return m.matches();  
    }


	/**
	 * @Description:
	 *	 	通过两个对象的传入,进行两个对象的赋值,
	 * 		共2个参数,把第一个对象中的参数传递给第二个对象
	 * @author:  Rex.Hua
	 * @version:
	 * @Company:
	 * @date:    2017年7月24日
	 */
	public static<T> void setValue(T model,T target) throws Exception {
		//获得实体类
		Class clazz = model.getClass();
		//查看有那些字段
		Field[] fields = clazz.getDeclaredFields();
		//遍历
		for (Field field : fields) {
			//属性的名字
			String fieldName = field.getName();
			//属性的类型
			//Class<?> type = field.getType();
			//再得到getter方法的名字
			String getMethodName = "get" + (fieldName.charAt(0)+"").toUpperCase() + fieldName.substring(1);
			Method getMethod = clazz.getMethod(getMethodName);
			//判断model的哪个属性是否为空,利用get方法
			Object object = getMethod.invoke(model);
			if(object!=null) {
				//不为空就进行赋值
				// 通过属性名，来获取对应的setXXX的名字
				String setMethodName = "set" + (fieldName.charAt(0)+"").toUpperCase() + fieldName.substring(1);
				//这是set方法
				Method setMethod = clazz.getMethod(setMethodName, field.getType());
				//这里需要进行类型的强制转换吗?
				setMethod.invoke(target, object);
			}
		}
	}

	/******
	 * 给class的某个成员set 值
	 * @param cls
	 * @param setmethod
	 * @param value
	 * @return
	 */
    public static Object classSetMethod(Object cls,String setmethod,String value){  
		try {
			Field[ ] fields = cls.getClass().getDeclaredFields( );
			for(Field field:fields){
				//setAccessible( true ) :类中的成员变量为private,故必须进行此操作 
				field.setAccessible( true );
				if(field.getName().toUpperCase().equals(setmethod.toUpperCase())){
					String firstLetter = field.getName().substring(0, 1).toUpperCase();  
	                String lastLetter = field.getName().substring(1); 
					String setter = "set" + firstLetter + lastLetter;  
	                Method method = cls.getClass().getMethod(setter, new Class[] {field.getType()}); 
	                if(field.getType().getName().lastIndexOf("String")>0){
	                	method.invoke(cls, value);
	                }else if(field.getType().getName().lastIndexOf("Long")>0){
	                	method.invoke(cls, Long.valueOf(value));
	                }else if(field.getType().getName().lastIndexOf("Boolean")>0){
	                	method.invoke(cls, Boolean.valueOf(value));
	                }else if(field.getType().getName().lastIndexOf("Double")>0){
	                	method.invoke(cls, Double.valueOf(value));
	                }else if(field.getType().getName().lastIndexOf("Integer")>0){
	                	method.invoke(cls, Integer.valueOf(value));
	                }else if(field.getType().getName().lastIndexOf("Short")>0){
	                	method.invoke(cls, Short.valueOf(value));
	                }else if(field.getType().getName().lastIndexOf("Byte")>0){
	                	method.invoke(cls, Byte.valueOf(value));
	                }
	                return cls;
				}
			}
		} catch (Exception e) {
			printStackTraceToStr(e);
		}
        return null;  
    }
	/******
	 * 获取class的某个成员get 值
	 * @param cls
	 * @param setmethod
	 * @param
	 * @return String
	 */
	public static String classGetMethod(Object cls,String setmethod){
		try {
			Field[ ] fields = cls.getClass().getDeclaredFields( );
			String value = "";
			for(Field field:fields){
				//setAccessible( true ) :类中的成员变量为private,故必须进行此操作
				field.setAccessible( true );
				if(field.getName().equals(setmethod)){
					String firstLetter = field.getName().substring(0, 1).toUpperCase();
					String lastLetter = field.getName().substring(1);
					String getter = "get" + firstLetter + lastLetter;
					Method method = cls.getClass().getMethod(getter);

					if(field.getType().getName().lastIndexOf("String")>0){
						value = (String) method.invoke(cls);
					}else if(field.getType().getName().lastIndexOf("Long")>0){
						value = String.valueOf(method.invoke(cls));
					}else if(field.getType().getName().lastIndexOf("Boolean")>0){
						value = String.valueOf(method.invoke(cls));
					}else if(field.getType().getName().lastIndexOf("Double")>0){
						value = String.valueOf(method.invoke(cls));
					}else if(field.getType().getName().lastIndexOf("Integer")>0){
						value = String.valueOf(method.invoke(cls));
					}else if(field.getType().getName().lastIndexOf("Short")>0){
						value = method.invoke(cls).toString();
					}else if(field.getType().getName().lastIndexOf("Byte")>0){
						value = method.invoke(cls).toString();
					}
					return value;
				}
			}
		} catch (Exception e) {
			printStackTraceToStr(e);
		}
		return null;
	}

	// 如果为空返回0
	public static BigDecimal getZeroIfNull(BigDecimal num) {
		if (num == null) return  new BigDecimal("0");
		return num;
	}
}
