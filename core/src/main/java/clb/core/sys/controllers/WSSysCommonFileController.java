package clb.core.sys.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.hand.hap.core.IRequest;
import com.hand.hap.intergration.annotation.HapInbound;
import com.hand.hap.system.dto.ResponseData;

import clb.core.sys.dto.SysCommonFile;
import clb.core.sys.service.ISysCommonFileService;

@Controller
public class WSSysCommonFileController extends ClbBaseController{

	@Autowired
    private ISysCommonFileService service;
	
	@Timed
    @HapInbound(apiName = "ClbWsSysCommonFileQuery")
    @RequestMapping(value = "/api/sys/commonFileQuery", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData query(@RequestBody SysCommonFile dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
            IRequest requestContext = createRequestContext(request);
            List<SysCommonFile> sysCommonFileList = service.selectSysCommonFileInfo(requestContext,dto,page,pageSize);
            return new ResponseData(sysCommonFileList);
    }
	
	@Timed
    @HapInbound(apiName = "ClbWsSysUpdateDownloadTimes")
    @RequestMapping(value = "/api/sys/updateDownloadTimes", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData updateDownloadTimes(@RequestBody SysCommonFile dto, HttpServletRequest request) {
		IRequest requestCtx = createRequestContext(request);
    	SysCommonFile sysCommonFile = service.selectByPrimaryKey(requestCtx, dto);
    	if(sysCommonFile != null){
    		sysCommonFile.setDownloadTimes(sysCommonFile.getDownloadTimes()+1);
    		service.updateByPrimaryKeySelective(requestCtx, sysCommonFile);
    	}
        return new ResponseData();
    }
}
