package clb.core.supplier.controllers;

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
 * @name SpeArchivesController
 * @description 供应商档案信息控制器层
 * @author bo.wu@hand-china.com 2017年4月19日14:13:02
 * @version 1.0 
 */
@Controller
@RequestMapping("/supplier/archives")
public class SpeArchivesController extends BaseController{

    
    @Autowired
    private ISpeArchivesService speArchivesService;

    /**
     *@description 基础查询方法 
     */
    @RequestMapping(value = "/query")
    @ResponseBody
    public ResponseData query(SpeArchives dto,HttpServletRequest request) {
    	IRequest iRequest = this.createRequestContext(request);
        return new ResponseData(speArchivesService.getData(iRequest,dto));
    }

    /**
     *@description 提交数据
     */
    @RequestMapping(value = "/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<SpeArchives> dto){
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(speArchivesService.batchUpdate(requestCtx, dto));
    }

    /**
     *@description 删除数据 
     */
    @RequestMapping(value = "/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<SpeArchives> dto){
    	speArchivesService.batchDelete(dto);
        return new ResponseData();
    }
    
    /**
     * 文件删除.
     *
     * @param request HttpServletRequest
     * @param fileId  文件id
     * @param token   token
     * @return Map 结果对象
     * @throws TokenException
     */
    @RequestMapping(value = "/attach/remove")
    public Map<String, Object> remove(HttpServletRequest request, String fileId, String token) throws TokenException {
    	IRequest requestContext = createRequestContext(request);
    	return speArchivesService.Detele(request, fileId, token, requestContext);
    }
    
    
    
}