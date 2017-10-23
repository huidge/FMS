package clb.core.common.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.hand.hap.security.TokenUtils;

import clb.core.attachment.FileInfo;
import clb.core.attachment.UpConstants;
import clb.core.attachment.Uploader;
import clb.core.attachment.UploaderFactory;
import clb.core.common.exceptions.CommonException;
import clb.core.common.service.ICommonUploadService;
import clb.core.core.util.UploadUtil;
/**
 * @name CommonUploadServiceImpl
 * @description 通用上传方法业务逻辑层
 * @author bo.wu@hand-china.com 2017年4月21日11:19:31
 * @version 1.0 
 */
@Service
@Transactional
public class CommonUploadServiceImpl implements ICommonUploadService{


	/**
     * 日志记录.
     **/
    private static Logger logger = LoggerFactory.getLogger(CommonUploadServiceImpl.class);
    
    /**
     * 提示信息名.
     */
    private static final String ROOT_CATEGORY = "CLBDATA";
    
    /**
     * 提示信息名.
     */
    private static final String MESSAGE_NAME = "message";
    
    /**
     * 提示成功.
     */
    private static final String MESG_SUCCESS = "success";
    
    /**
     * 提示信息 name.
     **/
    private static final String INFO_NAME = "info";
    
    /**
     * file对象名.
     */
    private static final String FILE_NAME = "file";
    
    @Autowired
    private IAttachCategoryService categoryService;
    
    @Autowired
    private ISysFileService fileService;
    
    @Autowired
    private MessageSource messageSource;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private AttachCategoryMapper attachCategoryMapper;

	@Override
	//@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@Transactional(isolation=Isolation.SERIALIZABLE)
	public String Upload(HttpServletRequest request, Locale locale,IRequest requestContext)
			throws StoragePathNotExsitException, UniqueFileMutiException, JsonProcessingException{
		 	Map<String, Object> response = new HashMap<String, Object>();
	        Uploader uploader = UploaderFactory.getMutiUploader();
	        uploader.init(request);
	        String sourceType = "";
	        
	        /* --初始化参数-- */
	        //模块名
	        String maxSize = uploader.getParams("maxSize");
	        //不能为空
	        if(maxSize == null || "".equals(maxSize)){
	        	response.put(MESSAGE_NAME, messageSource.getMessage("spe.allowsizeparamerror", null, locale));// TYPEORKEY_EMPTY
	            response.put(INFO_NAME, messageSource.getMessage("spe.allowsizeparamerror", null, locale));
	            return objectMapper.writeValueAsString(response);
	        }
	        Long max = 0L;
	        try{
	        	max = Long.valueOf(maxSize.trim());
	        	max = max*1024;
	        }catch(Exception e){
	        	response.put(MESSAGE_NAME, messageSource.getMessage("spe.filesizenumbererror", null, locale));// TYPEORKEY_EMPTY
	            response.put(INFO_NAME, messageSource.getMessage("spe.filesizenumbererror", null, locale));
	            return objectMapper.writeValueAsString(response);
	        }
	        
	        //模块名
	        String modularName = uploader.getParams("modularName");
	        //不能为空
	        if(modularName == null || "".equals(modularName)){
	        	response.put(MESSAGE_NAME, messageSource.getMessage("spe.modelnameparamerror", null, locale));// TYPEORKEY_EMPTY
	            response.put(INFO_NAME, messageSource.getMessage("spe.modelnameparamerror", null, locale));
	            return objectMapper.writeValueAsString(response);
	        }
	        //允许上传类型
	        String allowType = uploader.getParams("allowType");
	        //不能为空
	        if(allowType != null && !"".equals(allowType)){
	        	//上传类型长度不能超过30个字符
	        	if(allowType.length() > 80){
	        		response.put(MESSAGE_NAME, messageSource.getMessage("spe.toolongerror", null, locale));// TYPEORKEY_EMPTY
		            response.put(INFO_NAME, messageSource.getMessage("spe.toolongerror", null, locale));
		            return objectMapper.writeValueAsString(response);
	        	}
	        }
	        /* --初始化参数结束-- */
	        
	        //判断上传目录是否出错
	        try{
	        	//生成目录
				requestContext.setLocale(locale.getLanguage());
	        	sourceType = createCategoryByDay(requestContext,modularName,allowType,max,request.getParameter("batch"));
	        }catch(Exception e){
				if(e instanceof CommonException){
	        		response.put(MESSAGE_NAME, messageSource.getMessage(((CommonException) e).getDescriptionKey(), null, locale));// TYPEORKEY_EMPTY
		            response.put(INFO_NAME, messageSource.getMessage(((CommonException) e).getDescriptionKey(), null, locale));
	        	}else{
	        		response.put(MESSAGE_NAME, messageSource.getMessage("spe.uploadfailed", null, locale));// TYPEORKEY_EMPTY
		            response.put(INFO_NAME, messageSource.getMessage("spe.uploadfailed", null, locale));
	        	}
	        	return objectMapper.writeValueAsString(response);
	        }
	        
	        String sourceKey = "1";
	        response.put("success", false);
	        if (StringUtils.isBlank(sourceType) || StringUtils.isBlank(sourceKey)) {
	            response.put(MESSAGE_NAME, messageSource.getMessage("hap.typeorkey_empty", null, locale));// TYPEORKEY_EMPTY
	            response.put(INFO_NAME, messageSource.getMessage("hap.typeorkey_empty", null, locale));
	            return objectMapper.writeValueAsString(response);
	        }
	        // 设置上传参数
	        AttachCategory category = UploadUtil.initUploaderParams(uploader, sourceType, requestContext, categoryService);
	        // TYPE 设置不对
	        if (category == null) {
	            response.put(MESSAGE_NAME, messageSource.getMessage("hap.type_error", null, locale));
	            response.put(INFO_NAME, messageSource.getMessage("hap.type_error", null, locale));
	            return objectMapper.writeValueAsString(response);
	        }
	        List<FileInfo> fileInfos = uploader.upload(category.getCategoryPath());
	        // 出错了
	        if (!UpConstants.SUCCESS.equals(uploader.getStatus())) {
	        	String message = fileInfos.get(0).getStatus();
	        	if("SINGLE_FILE_SIZE_MAX_ERROR".equals(message)){
	        		message = "不能超过50M";
	        	}
	        	if("FILE_DISALLOWD_ERROR".equals(message)){
	        		message = "该类型不允许上传";
	        	}
	            response.put(MESSAGE_NAME,message);
	            response.put(INFO_NAME,message);
	            return objectMapper.writeValueAsString(response);
	        } else {
	            response.put("success", true);
	            response.put(MESSAGE_NAME, messageSource.getMessage("hap.upload_success", null, locale));// uploader.getStatus()
	        }
	        // WebUploader 每次只上传一个文件
	        FileInfo f = fileInfos.get(0);
	        try {
	            SysFile sysFile = UploadUtil.genSysFile(f, requestContext.getUserId(), requestContext.getUserId());
	            Attachment attach = UploadUtil.genAttachment(category, sourceKey, requestContext.getUserId(),
	                    requestContext.getUserId());
	            // 分类如果是唯一类型
	            if (BaseConstants.YES.equals(category.getIsUnique())) {
	                sysFile = fileService.updateOrInsertFile(requestContext, attach, sysFile);
	            } else {
	                fileService.insertFileAndAttach(requestContext, attach, sysFile);
	            }
	            TokenUtils.generateAndSetToken(TokenUtils.getSecurityKey(request.getSession(false)), sysFile);
	            response.put(FILE_NAME, sysFile);
				//删除本地文件
				File file = f.getFile();
				file.delete();
	        } catch (UniqueFileMutiException ex) {
	            response.put("success", false);
	            response.put(MESSAGE_NAME, messageSource.getMessage("hap.mesg_unique", null, locale));
	            return objectMapper.writeValueAsString(response);
	        } catch (Exception e) {
	            if (logger.isErrorEnabled()) {
	                logger.error("database error", e);
	            }
	            File file = f.getFile();
	            if (file.exists()) {
	                file.delete();
	            }
	            response.put("success", false);
	            response.put(MESSAGE_NAME, messageSource.getMessage("hap.database_error", null, locale));
	            response.put(INFO_NAME, messageSource.getMessage("hap.database_error", null, locale));
	        }
	        return objectMapper.writeValueAsString(response);
		
	}
	
	/**
     * 按天创建目录 
     * @throws CommonException 
     */
    public String createCategoryByDay(IRequest iRequest,String modularName,String allowType,Long maxSize,String batch) throws CommonException{
    	//结果
    	AttachCategory resData = new AttachCategory();
    	//查询根路径Id
    	AttachCategory attachCategory = new AttachCategory();
    	attachCategory.setCategoryName(ROOT_CATEGORY);
    	attachCategory.setParentCategoryId(-1L);
    	if (iRequest.getLocale() == null || "zh".equals(iRequest.getLocale())){
			Locale locale = new Locale("zh_CN");
			iRequest.setLocale(locale.getLanguage());
		}


    	//校验是否创建根目录
    	try{
    		attachCategory = attachCategoryMapper.selectOne(attachCategory);
    		if(attachCategory == null || attachCategory.getCategoryId() == null){
        		throw new CommonException("COMMON","spe.rootnameerror", null);
        	}
    	}catch(Exception e){
    		throw new CommonException("COMMON","spe.rootsamename", null);
    	}
    	//根目录Id
    	Long parentCategoryId = attachCategory.getCategoryId();
    	//根目录路径
    	String rootCategory = attachCategory.getCategoryPath();
    	if(rootCategory == null || "".equals(rootCategory)){
    		throw new CommonException("COMMON","spe.rootnotexist", null);
    	}
    	/* --开始创建父路径--*/
    	//添加乐观锁,防止生成同一个目录
    	List<AttachCategory> param = new ArrayList<>();
    	if(StringUtils.isBlank(batch) || !batch.equals("batch")){
    		int num = attachCategoryMapper.updateByPrimaryKeySelective(attachCategory);
    		if(num == 0){
    			throw new CommonException("COMMON","spe.notallowtogether",null);
    		}
    	}else{
    		Long pcId=attachCategory.getCategoryId();
			attachCategory = new AttachCategory();
			attachCategory.setCategoryName(modularName);
	    	attachCategory.setParentCategoryId(pcId);
	    	attachCategory = attachCategoryMapper.select(attachCategory).get(0);
	    	pcId=attachCategory.getCategoryId();
	    	attachCategory = new AttachCategory();
	    	attachCategory.setParentCategoryId(pcId);
	    	Calendar cal = Calendar.getInstance();
	    	String year = String.valueOf(cal.get(1));
			String month = String.valueOf(cal.get(2) + 1);
			String day = String.valueOf(cal.get(5));
			String categoryName = year+month+day;
			resData.setCategoryName(categoryName);
	    	return attachCategoryMapper.select(attachCategory).get(0).getSourceType();
    	}
		//清空变量，用于查询
    	attachCategory = new AttachCategory();
    	attachCategory.setCategoryName(modularName);
    	attachCategory.setParentCategoryId(parentCategoryId);
    	List<AttachCategory>attaList = attachCategoryMapper.select(attachCategory);
    	if(CollectionUtils.isEmpty(attaList)){
    		attachCategory=null;
    	}else{
    		attachCategory=attaList.get(0);
    	}
    	//已存在，不创建，否则创建新目录
    	if(attachCategory != null){
    		parentCategoryId = attachCategory.getCategoryId();
    		rootCategory = rootCategory+attachCategory.getCategoryPath();
		}else{
			param.clear();
			attachCategory = new AttachCategory();
			attachCategory.setCategoryName(modularName);
			attachCategory.setCategoryPath("/"+modularName);
			attachCategory.setAllowedFileType(null);
			attachCategory.setAllowedFileSize(null);
			attachCategory.setDescription(modularName);
			attachCategory.setSourceType(modularName);
			attachCategory.setIsUnique("N");
			attachCategory.setLeafFlag("0");
			attachCategory.setParentCategoryId(parentCategoryId);
			param.add(attachCategory);
			param = categoryService.batchUpdate(iRequest,param);
			parentCategoryId = attachCategory.getCategoryId();
    		rootCategory = rootCategory+param.get(0).getCategoryPath();
		}
    	/* --父路径创建完成-- */
    	
    	//初始化返回结果
    	resData = new AttachCategory();
    	//查询当天的目录是否创建
    	Calendar cal = Calendar.getInstance();
		String year = String.valueOf(cal.get(1));
		String month = String.valueOf(cal.get(2) + 1);
		String day = String.valueOf(cal.get(5));
		String categoryName = year+month+day;
		resData.setCategoryName(categoryName);
		resData.setParentCategoryId(parentCategoryId);
		List<AttachCategory>attaList1 =  attachCategoryMapper.select(resData);
    	if(CollectionUtils.isEmpty(attaList1)){
    		resData=null;
    	}else{
    		resData=attaList1.get(0);
    	}
		if(resData != null){
			return resData.getSourceType();
		}else{
			param.clear();
			resData = new AttachCategory();
			resData.setCategoryName(categoryName);
			resData.setCategoryPath(rootCategory+"/"+categoryName);
			resData.setAllowedFileType("");
			//先定为20M
			resData.setAllowedFileSize(maxSize);
			resData.setDescription(categoryName);
			resData.setSourceType(modularName+categoryName+"FILES");
			resData.setIsUnique("N");
			resData.setLeafFlag("1");
			resData.setParentCategoryId(parentCategoryId);
			param.add(resData);
			categoryService.batchUpdate(iRequest,param);
		}
		return resData.getSourceType();
    	
    }
    
	public Map<String, Object> Detele(HttpServletRequest request,String fileId, String token,IRequest requestContext)
			throws TokenException {
		Map<String, Object> response = new HashMap<String, Object>();
        SysFile file = new SysFile();
        file.setFileId(Long.valueOf(fileId));
        file.set_token(token);
        TokenUtils.checkToken(request.getSession(false), file);
        file = fileService.delete(requestContext, file);
        response.put(MESSAGE_NAME, MESG_SUCCESS);
        return response;
	}

}