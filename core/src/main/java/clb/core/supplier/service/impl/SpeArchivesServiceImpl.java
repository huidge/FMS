package clb.core.supplier.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
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
import com.hand.hap.attachment.mapper.SysFileMapper;
import com.hand.hap.attachment.service.IAttachCategoryService;
import com.hand.hap.attachment.service.ISysFileService;
import com.hand.hap.core.BaseConstants;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.exception.TokenException;
import com.hand.hap.core.util.UploadUtil;
import com.hand.hap.security.TokenUtils;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.service.IProfileService;
import com.hand.hap.system.service.impl.BaseServiceImpl;

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

import clb.core.supplier.controllers.SpeArchivesController;
import clb.core.supplier.dto.SpeArchives;
import clb.core.supplier.exceptions.SpeException;
import clb.core.supplier.mapper.SpeArchivesMapper;
import clb.core.supplier.service.ISpeArchivesService;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @name SpeArchivesServiceImpl
 * @description 供应商档案信息业务逻辑层
 * @author bo.wu@hand-china.com 2017年4月18日19:36:25
 * @version 1.0 
 */
@Service
@Transactional
public class SpeArchivesServiceImpl extends BaseServiceImpl<SpeArchives> implements ISpeArchivesService{


	/**
     * 日志记录.
     **/
    private static Logger logger = LoggerFactory.getLogger(SpeArchivesServiceImpl.class);
    
    
    /**
     * 提示信息名.
     */
    private static final String MESSAGE_NAME = "message";
    
    /**
     * 提示成功.
     */
    private static final String MESG_SUCCESS = "success";
    
    @Autowired
    private ISysFileService fileService;
    
	@Autowired
	private SpeArchivesMapper speArchivesMapper;
	
	@Override
	public List<SpeArchives> getData(IRequest requestContext,SpeArchives dto) {
		List<SpeArchives> archives = speArchivesMapper.select(dto);
		for(SpeArchives data:archives){
			SysFile file = new SysFile();
			file = fileService.selectByPrimaryKey(requestContext, data.getFileId());
			data.setFileSize(file.getFileSize());
			data.setUploadDate(file.getUploadDate());
			data.setFilePath(file.getFilePath());
			data.set_token(file.get_token());
		}
		return archives;
	}

	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Map<String, Object> Detele(HttpServletRequest request,String fileId, String token,IRequest requestContext)
			throws TokenException {
		Map<String, Object> response = new HashMap<String, Object>();
        SysFile file = new SysFile();
        file.setFileId(Long.valueOf(fileId));
        file.set_token(token);
        TokenUtils.checkToken(request.getSession(false), file);
        file = fileService.delete(requestContext, file);
        SpeArchives data = new SpeArchives();
        data.setFileId(Long.valueOf(fileId));
        data = speArchivesMapper.selectOne(data);
        this.deleteByPrimaryKey(data);
        response.put(MESSAGE_NAME, MESG_SUCCESS);
        return response;
	}

}