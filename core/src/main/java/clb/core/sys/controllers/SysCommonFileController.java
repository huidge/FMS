package clb.core.sys.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.attachment.service.ISysFileService;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;

import clb.core.pln.dto.PlnPlanLibrary;
import clb.core.sys.dto.SysCommonFile;
import clb.core.sys.service.ISysCommonFileService;

    @Controller
    public class SysCommonFileController extends BaseController{

    @Autowired
    private ISysCommonFileService service;
    
    @Autowired
    private ISysFileService fileService;


    @RequestMapping(value = "/fms/sys/common/file/query")
    @ResponseBody
    public ResponseData query(SysCommonFile dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        List<SysCommonFile> sysCommonFileList = service.selectSysCommonFileInfo(requestContext,dto,page,pageSize);
        for (SysCommonFile sysCommonFile : sysCommonFileList) {
        	if(sysCommonFile.getFileId() != null){
        		SysFile sysFile = fileService.selectByPrimaryKey(requestContext, sysCommonFile.getFileId());
        		if(sysFile!=null){
        			sysCommonFile.set_token(sysFile.get_token());
        		}
        	}
		}
        
        return new ResponseData(sysCommonFileList);
    }

    @RequestMapping(value = "/fms/sys/common/file/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<SysCommonFile> dto){
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/fms/sys/common/file/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<SysCommonFile> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
    
    /**
     * 更新下载次数
     * @param request
     * @param dto
     * @return
     */
    @RequestMapping(value = "/fms/sys/common/file/updateDownloadTimes")
    @ResponseBody
    public ResponseData updateDownloadTimes(HttpServletRequest request,SysCommonFile dto){
    	IRequest requestCtx = createRequestContext(request);
    	SysCommonFile sysCommonFile = service.selectByPrimaryKey(requestCtx, dto);
    	//后台的下载不计入次数  页面已经修改
    	//sysCommonFile.setDownloadTimes(sysCommonFile.getDownloadTimes()+1);
    	//service.updateByPrimaryKeySelective(requestCtx, sysCommonFile);
        return new ResponseData();
    }
    
    /**
     * 清空下载次数
     * @param request
     * @param dto
     * @return
     */
    @RequestMapping(value = "/fms/sys/common/file/clearDownloadTimes")
    @ResponseBody
    public ResponseData clearDownloadTimes(HttpServletRequest request,SysCommonFile dto){
    	IRequest requestCtx = createRequestContext(request);
    	//SysCommonFile sysCommonFile = service.selectByPrimaryKey(requestCtx, dto);
    	//sysCommonFile.setDownloadTimes(0L);
    	dto.setDownloadTimes(0L);
    	service.updateByPrimaryKeySelective(requestCtx, dto);
        return new ResponseData();
    }
    
   
    @RequestMapping(value = "/fms/sys/common/file/importSysFile")
    @ResponseBody
    public ResponseData importSysFile(HttpServletRequest request, SysCommonFile dto){
    	IRequest requestCtx = createRequestContext(request);
    	try {
			service.insertFileInfo(requestCtx,dto.getFileName(),dto.getFileType());
		} catch (Exception e) {
			ResponseData responseData = new ResponseData();
			responseData.setSuccess(false);
			responseData.setMessage(e.getMessage());
			return responseData;
		}
    	return new ResponseData();
    }
}