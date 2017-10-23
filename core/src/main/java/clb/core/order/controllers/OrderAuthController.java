package clb.core.order.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;

import clb.core.order.dto.SysPageFieldCfg;
import clb.core.order.service.ISysPageFieldCfgService;

@Controller
public class OrderAuthController extends BaseController{

	@Autowired
	private ISysPageFieldCfgService sysPageFieldCfgService;
	
	@RequestMapping(value = "/fms/ord/auth/query")
    @ResponseBody
    public ResponseData query(SysPageFieldCfg dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        return new ResponseData(sysPageFieldCfgService.accessMenuAuthList(dto.getRoleCode()));
    }
	
	 @RequestMapping(value = "/fms/ord/auth/submit")
	    @ResponseBody
	    public ResponseData update(HttpServletRequest request,@RequestBody List<SysPageFieldCfg> dto){
	        IRequest requestCtx = createRequestContext(request);
	        //循环传递进来的dto
	        for (SysPageFieldCfg sysPageFieldCfg : dto) {
	        	if("false".equals(sysPageFieldCfg.getQueryFlag())){
	        		sysPageFieldCfg.setQueryFlag("");
	        	}
	        	if("false".equals(sysPageFieldCfg.getUpdateFlag())){
	        		sysPageFieldCfg.setUpdateFlag("");
	        	}
	        	sysPageFieldCfgService.updateFlag(sysPageFieldCfg);
			}
	        return new ResponseData(dto);
	    }
}
