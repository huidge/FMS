package clb.core.common.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hap.attachment.FileInfo;
import com.hand.hap.attachment.UpConstants;
import com.hand.hap.attachment.Uploader;
import com.hand.hap.attachment.UploaderFactory;
import com.hand.hap.attachment.dto.AttachCategory;
import com.hand.hap.attachment.dto.Attachment;
import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.attachment.exception.StoragePathNotExsitException;
import com.hand.hap.attachment.exception.UniqueFileMutiException;
import com.hand.hap.attachment.mapper.AttachCategoryMapper;
import com.hand.hap.attachment.service.IAttachCategoryService;
import com.hand.hap.attachment.service.ISysFileService;
import com.hand.hap.core.BaseConstants;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.exception.TokenException;
import com.hand.hap.core.util.UploadUtil;
import com.hand.hap.security.TokenUtils;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IProfileService;

import clb.core.supplier.exceptions.SpeException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.hand.hap.core.util.UploadUtil;
import com.hand.hap.security.TokenUtils;
import com.hand.hap.attachment.FileInfo;
import com.hand.hap.attachment.UpConstants;
import com.hand.hap.attachment.Uploader;
import com.hand.hap.attachment.UploaderFactory;
import com.hand.hap.attachment.dto.AttachCategory;
import com.hand.hap.attachment.dto.Attachment;
import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.attachment.exception.StoragePathNotExsitException;
import com.hand.hap.attachment.exception.UniqueFileMutiException;

import clb.core.common.exceptions.CommonException;
import clb.core.common.service.ICommonUploadService;
import clb.core.common.service.impl.CommonUploadServiceImpl;
import clb.core.common.utils.ExportUtil;
import clb.core.common.utils.ImportUtil;
import clb.core.supplier.dto.SpeArchives;
import clb.core.supplier.exceptions.SpeException;
import clb.core.supplier.service.ISpeArchivesService;
import clb.core.supplier.service.ISpeSupplierService;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @name ExcelModelExportController
 * @description 通用Excel模板导出方法控制器层
 * @author bo.wu@hand-china.com 2017年5月12日16:34:18
 * @version 1.0 
 */
@Controller
@RequestMapping("/commons/excelmodel")
public class ExcelModelExportController extends BaseController{
	
    private static Logger logger = LoggerFactory.getLogger(ExcelModelExportController.class);

   
    /**
     * 通用Excel模板上传方法.
     *
     * @param request HttpServletRequest
     * @param request HttpServletRequest
     * @param dtoName dto全类名
     */

    @RequestMapping(value = "/export")
    public void exportExcelModel(String dtoName,String fileName,HttpServletRequest request,HttpServletResponse response) throws CommonException{
    	//创建HSSFWorkbook对象(excel的文档对象)  
        HSSFWorkbook wb = new HSSFWorkbook();  
        //建立新的sheet对象（excel的表单）  
        HSSFSheet sheet=wb.createSheet("sheet1");  
        //在sheet里创建第一行，参数为行索引(excel的行)，可以是0～65535之间的任何一个  
        HSSFRow row1=sheet.createRow(0);  
        //创建单元格（excel的单元格，参数为列索引，可以是0～255之间的任何一个  
        HSSFCell cell=row1.createCell(0);  
        //在sheet里创建第二行  
        HSSFRow row=sheet.createRow(0);    
        try{
        	//获取标题
            List<String> titles = ExportUtil.getTitles(Class.forName(dtoName), request,this.getMessageSource());
            
            if(titles != null && titles.size()>0){
            	for(int i=0;i<titles.size();i++){
            		HSSFCellStyle cellStyle = wb.createCellStyle();
                    HSSFDataFormat format = wb.createDataFormat();
                    cellStyle.setDataFormat(format.getFormat("@"));
            		//创建单元格
                    cell = row.createCell(i);
                    //设置单元格内容和格式，POI设置单元格格式，可参考http://javacrazyer.iteye.com/blog/894758和http://www.iteye.com/problems/65838
                    cell.setCellValue(titles.get(i));  
                    sheet.setDefaultColumnStyle(i,cellStyle);
                }
            }  
            OutputStream output=response.getOutputStream();  
            response.reset();  
            String name = new String(fileName.getBytes("gb2312"), "ISO8859-1");
            response.setHeader("Content-disposition", "attachment; filename="+name+".xls");  
            response.setContentType("application/msexcel");  
            response.setHeader("Accept-Ranges", "bytes");
            wb.write(output);  
            output.close();  
        }catch(Exception e){
        	logger.error("导出失败",e);
        	if(e instanceof ClassNotFoundException){
        		throw new CommonException("COMMON","类名不正确！",null);
        	}
        	else if(e instanceof IOException){
        		throw new CommonException("COMMON","生成文件失败！",null);
        	}
        	else {
        		throw new CommonException("COMMON","导出失败！",null);
        	}
        }
        	
        
        
    }
    
    
    
}