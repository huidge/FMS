package clb.core.common.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.support.RequestContextUtils;

import clb.core.common.annotations.Title;
import clb.core.common.exceptions.CommonException;

/**
 * @name ExportUtil
 * @description 导出Excel工具类
 * @author bo.wu@hand-china.com 2017年5月15日16:35:03
 * @version 1.0
 */
public class ExportUtil {
	
	private static Logger log = LoggerFactory.getLogger(ExportUtil.class);
	
	/**
	 * 通过Title注解获取排序后的标题和字段名的映射关系
	 * @param Class 类的Class属性
	 * @param HttpServletRequest 请求
	 * @param MessageSource 消息解析类
	 * @return List<Map<String,String>> 排序后的标题和字段名的映射关系
	 */
	public static List<Map<String,String>> getSortMapping(Class clazz,HttpServletRequest request,MessageSource messageSource){
		Map<String,String> mapping = ImportUtil.getMapping(clazz, request, messageSource);
		List<Map<String,String>> titles = new ArrayList<>();
		List<Title> originTitles = new ArrayList<>();
		Field[] fields = clazz.getDeclaredFields();
		Locale locale = RequestContextUtils.getLocale(request);
		for(int i=0;i<fields.length;i++){
    		Title title = fields[i].getAnnotation(Title.class);
    		if(title != null){
    			originTitles.add(title);
    		}
    	}
		Collections.sort(originTitles,(Title arg0, Title arg1)->arg0.index() - arg1.index());
		for(Title t:originTitles){
			Map<String,String> titleData = new HashMap<>();
			String title = messageSource.getMessage(t.title(),null,locale);
			titleData.put(title,mapping.get(title));
			//titleData = exchangeMap(titleData);
			titles.add(titleData);
		}
		return titles;
	}
	
	/**
	 * 通过Title注解获取排序后的标题
	 * @param Class 类的Class属性
	 * @param HttpServletRequest 请求
	 * @param MessageSource 消息解析类
	 * @return List<String> 标题
	 */
	public static List<String> getTitles(Class clazz,HttpServletRequest request,MessageSource messageSource){
		List<String> titles = new ArrayList<>();
		List<Title> originTitles = new ArrayList<>();
		Field[] fields = clazz.getDeclaredFields();
		Locale locale = RequestContextUtils.getLocale(request);
		for(int i=0;i<fields.length;i++){
    		Title title = fields[i].getAnnotation(Title.class);
    		if(title != null){
    			originTitles.add(title);
    		}
    	}
		Collections.sort(originTitles,(Title arg0, Title arg1)->arg0.index() - arg1.index());
		for(Title t:originTitles){
			String title = messageSource.getMessage(t.title(),null,locale);
			titles.add(title);
		}
		return titles;
	}


	/**
	 * 交换map的建和值
	 * @param Map 要交换的map
	 * @return Map 结果 
	 */
	public static Map exchangeMap(Object map){
		Map<Object,Object> data = new HashMap<>();
		Map<Object,Object> mapdata = new HashMap<>();
		mapdata = (Map<Object,Object>)map;
		mapdata.forEach((k, v) -> {
			data.put(v,k);
		});
		return data;
	}
	
	public static HSSFWorkbook ExportData(List<?> data,HttpServletRequest request,MessageSource messageSource) throws CommonException{
		//创建HSSFWorkbook对象(excel的文档对象)  
        HSSFWorkbook wb = new HSSFWorkbook();  
        //建立新的sheet对象（excel的表单）  
        HSSFSheet sheet=wb.createSheet("sheet1");
        //在sheet里创建第一行，参数为行索引(excel的行)，可以是0～65535之间的任何一个  
        HSSFRow titleRow=sheet.createRow(0);      
        List<Method> methods = new ArrayList<>();
        //输出Excel文件  
        try{
        	//获取标题
        	List<Map<String,String>> titles = getSortMapping(data.get(0).getClass(), request,messageSource);
            if(titles != null && titles.size()>0){
            	//设置标题
            	for(int i=0;i<titles.size();i++){
            		for(Entry<String,String> entry:titles.get(i).entrySet()){
            			titleRow.createCell(i).setCellValue(entry.getKey());  
                        Method method = ImportUtil.getMethod(data.get(0).getClass(),entry.getValue(),"get");
                        methods.add(method);
            		}
            		//创建单元格并设置单元格内容  
                }
            	HSSFRow rows[] =new HSSFRow[data.size()]; 
            	//设置值
            	for(int i=0;i<data.size();i++){
            		rows[i] = sheet.createRow(i+1);
            		for(int j=0;j<titles.size();j++){
            			Method method = methods.get(j);
            			Object obj = method.invoke(data.get(i));
            			String cellData = "";
            			if(obj != null){
            				String type = StringUtils.substringAfter(method.getReturnType().toString()," ");
            				if("java.util.Date".equals(type)){
            					SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            	    			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");  
            	    			Date date = (Date)obj;
            	    			cellData = dateTimeFormat.format(date);
                    			String time = StringUtils.substringAfter(cellData," ");
                    			if("00:00:00".equals(time)){
                    				cellData = dateFormat.format(date); 
                    			}
            				}
            				else cellData = obj.toString();
            			}
            			rows[i].createCell(j).setCellValue(cellData); 
            		}
            	}
            }  
         }catch(Exception e){
        	log.error("导出失败",e);
        	throw new CommonException("COMMON","导出失败！",null);
        }
		return wb;
	}

	public static void downloadFile(HSSFWorkbook wb,HttpServletResponse response,String name) throws IOException{
			OutputStream output=response.getOutputStream();  
	        response.reset();
	        response.setHeader("Content-disposition", "attachment; filename="+name+".xls");  
	        response.setContentType("application/msexcel");  
	        response.setHeader("Accept-Ranges", "bytes");
	        wb.write(output);  
	        output.close();
	}

}
