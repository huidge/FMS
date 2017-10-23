package clb.core.common.utils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.locale.converters.DateLocaleConverter;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.drools.lang.dsl.DSLMapParser.mapping_file_return;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.support.RequestContextUtils;

import clb.core.common.annotations.Title;
import clb.core.common.exceptions.CommonException;

/**
 * @name ImportUtil
 * @description 导入Excel工具类
 * @author bo.wu@hand-china.com 2017年5月8日09:12:23
 * @version 1.0
 */
public class ImportUtil {
	
	private static Logger log = LoggerFactory.getLogger(ImportUtil.class);
	
	private static final String SUCCESS_STATUS = "true";
	
	private static final String ERROR_STATUS = "false";
	
	/**
	 * 利用反射读取Excel
	 * @param T 要获取的类型
	 * @param File Excel文件
	 * @param Map 标题和Dto属性的映射关系
	 * @param String Excel文件类型：xls/xlsx 
	 * 
	 * @return List<ImportMessage> 返回包含错误信息和状态的List
	 */
	public static List<ImportMessage> getData(Class clazz,File f,Map<String,String> mapping,String fileType) throws CommonException{
		InputStream inputStream;
		Workbook wb = null;  
		//读取文件
		try{
			inputStream = new FileInputStream(f);
			//mapping中定义的标题
			String requiredTitles[] = new String[mapping.size()];
			int index = 0;
			for (Map.Entry<String,String> entry : mapping.entrySet()) {  
				requiredTitles[index] = entry.getKey();
				index ++;
			}  
			String[] realTitles = readExcelTitle(inputStream,fileType);
			if (!compareTwoArray(realTitles,requiredTitles)){
				log.error("excel文件标题不正确!");
				throw new CommonException("COMMON","excel文件标题不正确！",null);
			}
			//因为前面使用过inputStream，被关闭了，所以这里要重新初始化一次
			inputStream = new FileInputStream(f);
	        if ("xls".equals(fileType)) {  
	            wb = new HSSFWorkbook(inputStream);  
	        }  
	        else if ("xlsx".equals(fileType)) {  
	            wb = new XSSFWorkbook(inputStream);  
	        }
	        else{
	        	log.error("文件类型不匹配!");
				throw new CommonException("COMMON","文件类型不匹配！",null);
	        }
	        inputStream.close();
		}catch(Exception e){
			if(e instanceof CommonException){
				CommonException e1 = (CommonException)e;
				throw e1;
			}else{
				log.error("读取文件失败!",e);
		    	throw new CommonException("COMMON","读取文件失败!",null);
			}
			
		}
		
        //保存数据，数据类型为标题+数据
		Map<String,Map> paramData = new HashMap<>();
	    Sheet sheet1 = wb.getSheetAt(0);  
	    Row title = sheet1.getRow(0);
	    for (Row row : sheet1){
	    	if(row.getRowNum() == 0)continue;
	    	Map<String,String> excelData = new HashMap<>();
	        for (Cell cell : row) {
	        	if(title.getCell(cell.getColumnIndex()) != null){
	        		//保存标题和数据
		        	excelData.put(title.getCell(cell.getColumnIndex()).getStringCellValue(), getCellFormatValue(cell)==null?null:getCellFormatValue(cell).trim());
	        	}
	        	
	        } 
	        //将标题替换为Dto属性
	        replace(mapping,excelData);
	        paramData.put(String.valueOf(row.getRowNum()),excelData); 
	    }
	    //获取到的数据为空，说明是一个空文件
	    if(paramData.size() == 0){
	    	throw new CommonException("COMMON","该Excel无数据!",null);
	    }
	    return LoadData(clazz,paramData,mapping);
	    
	}
	
	/**
	 * 比较两个数组是否相同 
	 */
	public static boolean compareTwoArray(Object []a1,Object a2[]){
		if(a1.length != a2.length)return false;
		for(int i=0;i<a1.length;i++){
			if(!Arrays.asList(a2).contains(a1[i])){
				return false;
			}
		}
		return true;
		
	}
	
	/**
	 * 读取Excel标题数据
	 * @param  InputStream 输入流
	 * @param  String 文件类型 xls/xlsx
	 * @return 标题数组
	 */
	public static String[] readExcelTitle(InputStream is,String fileType) throws IOException {
        Workbook wb = null;  
        if (fileType.equals("xls")) {  
            wb = new HSSFWorkbook(is);  
        }  
        else if (fileType.equals("xlsx")) {  
            wb = new XSSFWorkbook(is);  
        } 
        Sheet sheet1 = wb.getSheetAt(0);
        Row row = sheet1.getRow(0);
        int colNum = row.getPhysicalNumberOfCells();
        String []title = new String[colNum];
        for (int i = 0; i < colNum; i++) {
        	title[i] = getCellFormatValue(row.getCell((short) i))==null?"":getCellFormatValue(row.getCell((short) i)).trim();
        }
        return title;
	}
 
	/**
     * 获取单元格数据内容为字符串类型的数据
     * 
     * @param cell Excel单元格
     * @return String 单元格数据内容
     */
    private static String getCellFormatValue(Cell cell) {
    	if (cell == null) {
            return "";
        }
    	String strCell = "";
		switch (cell.getCellType()) {
        case Cell.CELL_TYPE_STRING:
            strCell = cell.getStringCellValue();
            break;
        case Cell.CELL_TYPE_NUMERIC:
        	if (HSSFDateUtil.isCellDateFormatted(cell)) {    //判断是日期类型  
        		SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");  
                Date dt = HSSFDateUtil.getJavaDate(cell.getNumericCellValue());//获取成DATE类型    
                try{
                	strCell = dateTimeFormat.format(dt);
                	int index = strCell.indexOf(" ");
        			String time = strCell.substring(index+1,strCell.length());
        			if("00:00:00".equals(time)){
        				strCell = dateFormat.format(dt); 
        			}
                }catch(Exception e){
                	strCell = dateFormat.format(dt); 
                }
                  
        	}else{
        		strCell = String.valueOf(cell.getNumericCellValue());
        	}
            break;
        case Cell.CELL_TYPE_BOOLEAN:
            strCell = String.valueOf(cell.getBooleanCellValue());
            break;
        case Cell.CELL_TYPE_BLANK:
            strCell = null;
            break;
        default:
            strCell = null;
            break;
        }
        
        if ("".equals(strCell) || strCell == null) {
            return null;
        }
        return strCell;
     }
 
     /**
     * 将从Excel中读取的数据的标题，用定义好的映射规则替换成dto属性
     * @param Map 映射规则
     * @param Map 数据  
     */
	 public static void replace(Map<String,String> mapping,Map<String,String> data){
		 for(Entry<String,String> configentry : mapping.entrySet()){
			 String configkey = configentry.getKey();
			 String value = data.get(configkey);
			 String newkey = configentry.getValue();
			 data.remove(configkey);
			 data.put(newkey,value);
		 }
	 }
	 
	 
	 /**
     * 将dto属性，用定义好的映射规则替换成Excel中读取的数据的标题
     * @param Map 映射规则
     * @param Map 数据  
     */
	 public static void recover(Map<String,String> mapping,Map<String,String> data){
		 mapping = ExportUtil.exchangeMap(mapping);
		 for(Entry<String,String> configentry : mapping.entrySet()){
			 String configkey = configentry.getKey();
			 String value = data.get(configkey);
			 String newkey = configentry.getValue();
			 data.remove(configkey);
			 data.put(newkey,value);
		 }
	 }
	 
	 /**
     * 处理数据，在数据后面加上index属性，用于前端排序
     * @param data 数据
     * @param clazz 类  
     */
	 public static void dealWithData(Map<String,String> data,Class clazz){
		 data.forEach((String k,String v)->{
			 try {
				Field field = clazz.getDeclaredField(k);
				Title title = field.getAnnotation(Title.class);
				v = v+"&"+title.index();
				}catch (Exception e) {
			 		v = v+"&"+0;
			 	}
			 	data.put(k, v);
		 });
	 }
 
	 /**
	 * 加载数据并返回ImportMessage 
	 * @param Class 类的Class属性
	 * @param Map   数据Map
	 */
	public static List<ImportMessage> LoadData(Class clazz,Map<String,Map> param,Map<String,String> mapping){
		ArrayList<ImportMessage> messages = new ArrayList<>();
		Map<String,String> exchangeMapping = ExportUtil.exchangeMap(mapping);
		boolean hadData = false;
		for(Entry<String,Map> paramentry : param.entrySet()){
			ImportMessage message = new ImportMessage();
			StringBuffer stringBuffer = new StringBuffer();
			hadData = false;
			//获取 字段:值 Map
			Map<String,String> paramData = paramentry.getValue();
			Object instance = null;
			try{
				instance = clazz.newInstance();
				for(Entry<String,String> entry : paramData.entrySet()){
					//属性名
					String attrName = entry.getKey();
					//字段
					Field field = clazz.getDeclaredField(attrName);
					//获取的值
					String value = entry.getValue();
					//获取NotNull和NotEmpty注解
					NotNull notNull = field.getAnnotation(NotNull.class);
					NotEmpty notEmpty = field.getAnnotation(NotEmpty.class);
					//判断是否为空
					if(notNull != null || notEmpty != null){
						if(StringUtils.isEmpty(value) || StringUtils.isBlank(value)){
							log.error(exchangeMapping.get(attrName)+"的值不能为空!");
							stringBuffer.append(exchangeMapping.get(attrName)+"不能为空;");
						}
					}
					//数据
					Object data = null;
					if(value != null){
						//转换成指定类型
						data = tranform(field,value);
						//值类型不正确
						if(data == null){
							log.info(field.getName()+"的类型为："+field.getType()+"，值为:"+value+"，类型不正确!");
							stringBuffer.append(exchangeMapping.get(attrName)+"的值类型不正确;");
						}else{
							Method method = getMethod(clazz, attrName,"set",field.getType());
							if(method != null && data!=null){
								log.info("字段"+field.getName()+"的类型为："+field.getType()+"，值为:"+data+"调用其Set方法设置值");
								//一旦有值，就放到list中
								hadData = true;
								method.invoke(instance,data);
							}
						}
					}else{
						//如果上面的步骤任然没有值，继续设置为false
						if(!hadData)hadData = false;
					}
					
				}
			}catch(Exception e){
				log.error("获取数据失败");
				stringBuffer = new StringBuffer("获取数据失败");
			}
			
			if(hadData){
				//添加Index属性
				dealWithData(paramData,clazz);
				//将数据恢复为标题-值Map
				recover(mapping,paramData);
				//设置Excel数据
				message.setExcelData(paramData);
				//设置Dto值
				message.setData(instance);
				//设置状态
				message.setStatus(SUCCESS_STATUS);
				//错误信息
				message.setErrorMessage(stringBuffer.toString());
				//错误信息不为空
				if(!StringUtils.isEmpty(stringBuffer.toString())){
					message.setStatus(ERROR_STATUS);
				}
				messages.add(message);
			}
		}
		return messages;
	}

	/**
	 * 转换成指定类型数据
	 * @param Field 类的Field属性
	 * @param String 从Excel中读取的值
	 */
	public static Object tranform(Field filed,String value){
		if(value == null)return null;
		String type = filed.getType().toString();
		type = type.substring("class ".length());
		boolean success = true;
		Object data = null;
		if(type.equals("java.lang.String"))return value;
		try{
			//8种基本数据类型
			if(type.equals("java.lang.Byte") || type.equals("byte")){
				value = value.substring(0,value.indexOf("."));
				data = Byte.valueOf(value);
			}
			if(type.equals("java.lang.Short") || type.equals("short")){
				value = value.substring(0,value.indexOf("."));
				data = Short.valueOf(value);
			}
			if(type.equals("java.lang.Integer") || type.equals("int")){
				value = value.substring(0,value.indexOf("."));
				data = Integer.valueOf(value);
			}
			if(type.equals("java.lang.Long") || type.equals("long")){
				value = value.substring(0,value.indexOf("."));
				data = Long.valueOf(value);
			}
			if(type.equals("java.lang.Float") || type.equals("float")){
				data = Float.valueOf(value);
			}
			if(type.equals("java.lang.Double") || type.equals("double")){
				data = Double.valueOf(value);
			}
			if(type.equals("java.lang.Boolean") || type.equals("boolean")){
				data = Boolean.valueOf(value);
			}
			if(type.equals("java.lang.Character") || type.equals("char")){
				data = value.charAt(0);
			}
			
			//Date类型
			if(type.equals("java.util.Date")){
				SimpleDateFormat dateTimeFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				SimpleDateFormat dateTimeFormat2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy/MM/dd");
				try{
					data = dateTimeFormat1.parse(value);
					success = true;
				}catch(Exception e){
					success = false;
				}
				try{
					data = dateTimeFormat2.parse(value);
					success = true;
				}catch(Exception e){
					success = false;
				}
				if(!success){
					try{
						data = dateFormat1.parse(value);
						success = true;
					}catch(Exception e){
						success = false;
					}
				}
				if(!success){
					try{
						data = dateFormat2.parse(value);
						success = true;
					}catch(Exception e){
						success = false;
					}
				}
				
			}
			
			//BigDecimal
			if(type.equals("java.math.BigDecimal")){
				data = new BigDecimal(value);
			}
			
			//Timestamp
			if(type.equals("java.sql.Timestamp")){
				try{
					Timestamp.valueOf(value);
				}catch(Exception e){
						success = false;
				}
			}
		}catch(Exception e){
			success = false;
		}
		//设置值失败，类型不正确
		if(!success){
			return null;
		}
		return data;
	}
	

	/**
	 * 通过字段名获取基本get/set方法
	 * @param Class 类的Class属性
	 * @param String 字段名
	 * @param String 方法类型：get/set 
	 * @param paramTypes 方法参数
	 */
	public static Method getMethod(Class clazz,String filed,String methodType,Class<?>... paramTypes){
		Method methods[] = clazz.getDeclaredMethods();
		for(Method method:methods){
			String methodName = method.getName();
			String requiredMethodName = methodType+filed.trim().toLowerCase();
			if(methodName.toLowerCase().equals(requiredMethodName)){
				Class[] parameters = method.getParameterTypes();
				if(compareTwoArray(parameters,paramTypes)){
					return method;
				}
			}
		}
		return null;
	}
	
	/**
	 * 通过字段名获取字段
	 * @param Class 类的Class属性
	 * @param String 字段名
	 */
	public static Field getField(Class clazz,String filedName){
		Field fields[] = clazz.getDeclaredFields();
		for(Field field:fields){
			String requiredFieldName = field.getName();
			if(requiredFieldName.toLowerCase().equals(filedName.toLowerCase())){
				return field;
			}
		}
		return null;
	}
	
	/**
	 * 校验值是否唯一
	 * @param clazz 类对象
	 * @param list 要校验的List
	 * @param data 值
	 * @param keys key值 
	 * @return boolean 是否唯一
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public static boolean validateUnique(Class clazz,List<?> list,Object data,String... keys) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		Method getMethods[] = new Method[keys.length];
		for(int i=0;i<keys.length;i++){
			Method method = getMethod(clazz,keys[i],"get");
			getMethods[i] = method;
		}
		int index = list.indexOf(data);
		if(index == -1)return true;
		int power = 0;
		for(int i=0;i<index;i++){
			power = 0;
			for(Method method:getMethods){
				if(method.invoke(list.get(i)).toString().equals(method.invoke(data).toString())){
					power = power+1;
				}
			}
			if(power == getMethods.length)return false;
		}
		return true;
	}
	
	/**
	 * 通过Title注解获取标题和字段映射关系
	 * @param Class 类的Class属性
	 * @param HttpServletRequest 请求
	 * @param MessageSource 消息解析类
	 * @return Map 标题和字段的对应关系
	 */
	public static HashMap<String,String> getMapping(Class clazz,HttpServletRequest request,MessageSource messageSource){
		HashMap<String,String> mapping = new HashMap<>();
		Field[] fields = clazz.getDeclaredFields();
		Locale locale = RequestContextUtils.getLocale(request);
		for(int i=0;i<fields.length;i++){
    		Title title = fields[i].getAnnotation(Title.class);
    		if(title != null){
    			mapping.put(messageSource.getMessage(title.title().trim(),null,locale),fields[i].getName());
    		}
    	}
		return mapping;
	}
	
	/**
	 * @name ImportMessage
	 * @description 封装Excel数据和错误信息，返回Excel标题和值，以及状态和错误信息 实现序列化接口，使其可以保存到Session中
	 * @author bo.wu@hand-china.com 2017年5月22日19:15:26
	 * @version 1.0
	 */
	public static class ImportMessage implements Serializable{
		
		private static final long serialVersionUID = -7226830654660555498L;
		//状态
		String status;
		//错误信息
		String errorMessage;
		//标题和值，以及index属性，用于前端排序
		Map<String,String> excelData;
		//数据
		Object data;
		//保存Id
		Long Id;
		//操作类型(插入/更新 用于多线程下判断是插入还是更新操作)
		String operateFlag;
		//缓存数据库数据
		Object objectCache;
		//更新标识
		Boolean updateFlag;
		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getErrorMessage() {
			return errorMessage;
		}

		public void setErrorMessage(String errorMessage) {
			this.errorMessage = errorMessage;
		}

		public Map<String, String> getExcelData() {
			return excelData;
		}

		public void setExcelData(Map<String, String> excelData) {
			this.excelData = excelData;
		}

		public Object getData() {
			return data;
		}

		public void setData(Object data) {
			this.data = data;
		}

		public Long getId() {
			return Id;
		}

		public void setId(Long id) {
			Id = id;
		}

		public String getOperateFlag() {
			return operateFlag;
		}

		public void setOperateFlag(String operateFlag) {
			this.operateFlag = operateFlag;
		}

		public Object getObjectCache() {
			return objectCache;
		}

		public void setObjectCache(Object objectCache) {
			this.objectCache = objectCache;
		}

		public Boolean getUpdateFlag() {
			return updateFlag;
		}

		public void setUpdateFlag(Boolean updateFlag) {
			this.updateFlag = updateFlag;
		}
		
		
	};
	
	
}
