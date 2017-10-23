package clb.core.common.service;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hand.hap.attachment.exception.StoragePathNotExsitException;
import com.hand.hap.attachment.exception.UniqueFileMutiException;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.core.exception.TokenException;
import com.hand.hap.system.service.IBaseService;

import clb.core.common.exceptions.CommonException;
import clb.core.supplier.dto.SpeArchives;

/**
 * @name ICommonUploadService
 * @description 通用上传方法业务逻辑层接口
 * @author bo.wu@hand-china.com 2017年4月21日11:19:31
 * @version 1.0 
 */
public interface ICommonUploadService{

	public String Upload(HttpServletRequest request, Locale locale,IRequest requestContext)
			throws StoragePathNotExsitException, UniqueFileMutiException, JsonProcessingException;
	
	public String createCategoryByDay(IRequest iRequest,String modularName,String allowType,Long maxSize,String batch)throws CommonException;
	
}