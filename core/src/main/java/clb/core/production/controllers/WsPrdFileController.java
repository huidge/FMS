package clb.core.production.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import clb.core.sys.controllers.ClbBaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.attachment.service.ISysFileService;
import com.hand.hap.core.IRequest;
import com.hand.hap.intergration.annotation.HapInbound;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;

import clb.core.production.dto.PrdItemFile;
import clb.core.production.dto.PrdItems;
import clb.core.production.service.IPrdItemFileService;

@Controller
public class WsPrdFileController extends ClbBaseController {

	@Autowired
	private IPrdItemFileService prdItemFileService;

	@Autowired
	private ISysFileService fileService;

	@Timed
	@HapInbound(apiName = "ClbWsPrdFile")
	@RequestMapping(value = "/api/query/prdFile", method = RequestMethod.POST, produces = { "application/json" })
	@ResponseBody
	public ResponseData queryPrd(@RequestBody PrdItemFile dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		List<PrdItemFile> prdItemFileList = prdItemFileService.selectByItemId(requestContext, dto, page, pageSize);
		return new ResponseData(prdItemFileList);
	}
	
	@Timed
	@HapInbound(apiName = "ClbWsPrdFileDownloadTimes")
	@RequestMapping(value = "/api/update/prdFileDownloadTimes", method = RequestMethod.POST, produces = { "application/json" })
	@ResponseBody
	public ResponseData prdFileDownloadTimes(@RequestBody PrdItemFile dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		PrdItemFile prdItemFile = prdItemFileService.selectByPrimaryKey(requestContext, dto);
		if(prdItemFile != null){
			prdItemFile.setDownloadTimes(prdItemFile.getDownloadTimes()+1);
			prdItemFileService.updateByPrimaryKeySelective(requestContext, prdItemFile);
    	}
        return new ResponseData();
	}
}
