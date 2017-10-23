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
import clb.core.supplier.dto.SpeArchives;
import clb.core.supplier.exceptions.SpeException;
import clb.core.supplier.service.ISpeArchivesService;
import clb.core.supplier.service.ISpeSupplierService;

import org.apache.commons.lang.StringUtils;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @name CommonUploadController
 * @description 通用上传方法控制器层
 * @author bo.wu@hand-china.com 2017年4月21日11:19:31
 * @version 1.0 
 */
@Controller
@RequestMapping("/commons/attach")
public class CommonUploadController extends BaseController{

    
    @Autowired
    private ICommonUploadService uploadService;
    @Autowired
    private ISysFileService fileService;
    
    /**
     * 通用附件上传方法.
     *
     * @param request HttpServletRequest
     * @return Map 返回结果对象
     * @throws StoragePathNotExsitException 存储路径不存在异常
     * @throws UniqueFileMutiException      附件不唯一异常
     * @throws JsonProcessingException
     * 
     * 如果是普通上传方法，需要带参数：
     * @param modularName:模块名
     * @param allowType:允许上传类型
     * @param maxSize:允许上传文件大小(单位为kb)
     * 如果要覆盖上传文件，需要在上面的参数基础上，加上参数：
     * @param fileId:文件Id
     * @param token:文件token
     */

    @RequestMapping(value = "/upload", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
    public String upload(HttpServletRequest request)
            throws StoragePathNotExsitException, UniqueFileMutiException, JsonProcessingException{
    	IRequest requestContext = createRequestContext(request);
    	Locale locale = RequestContextUtils.getLocale(request);
    	return uploadService.Upload(request,locale,requestContext);
    }
    
    /**
     * 创建目录
     * @param request
     * @return
     */
    @RequestMapping(value = "/createCategoryByDay", method = RequestMethod.POST)
    public void createCategoryByDay(HttpServletRequest request) throws Exception{
    	IRequest requestContext = createRequestContext(request);
    	uploadService.createCategoryByDay(requestContext,request.getParameter("modularName"),request.getParameter("allowType"),Long.valueOf(request.getParameter("maxSize")),null);
    }
    
    /**
     * 校验文件是否存在
     * @param fileId:文件Id
     * @param token:文件token
     * @throws CommonException token值不正确
     */

    @RequestMapping(value = "/validateFile", method = RequestMethod.POST)
    public ResponseData validateFile(HttpServletRequest request,String fileId,String token)
            throws StoragePathNotExsitException, UniqueFileMutiException, JsonProcessingException, CommonException{
    	IRequest requestContext = createRequestContext(request);
    	SysFile file = new SysFile();
        file.setFileId(Long.valueOf(fileId));
        file.set_token(token);
    	try{
    		TokenUtils.checkToken(request.getSession(false), file);
    	}catch(TokenException e){
    		throw new CommonException("COMMON","spe.tokenerror",null);
    	}
    	SysFile sysFile = fileService.selectByPrimaryKey(requestContext, Long.valueOf(fileId));
    	if(sysFile == null){
    		return new ResponseData(false);
    	}else{
    		return new ResponseData(true);
    	}
    }
    
    
    
}