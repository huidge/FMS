package clb.core.order.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.order.dto.SysOrderFunctionCfg;
import clb.core.order.service.ISysOrderFunctionCfgService;
import clb.core.prc.dto.PrcAttributeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

    @Controller
    public class SysOrderFunctionCfgController extends BaseController{

    @Autowired
    private ISysOrderFunctionCfgService service;


    @RequestMapping(value = "/fms/sys/order/function/cfg/query")
    @ResponseBody
    public ResponseData query(SysOrderFunctionCfg dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectFunctionInfo(requestContext,dto,page,pageSize));
    }

    @RequestMapping(value = "/fms/sys/order/function/cfg/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<SysOrderFunctionCfg> dto){
        IRequest requestCtx = createRequestContext(request);
        for (SysOrderFunctionCfg sysOrderFunctionCfg : dto) {
        	if("add".equals(sysOrderFunctionCfg.get__status())){
        		List<SysOrderFunctionCfg> sysOrderFunctionCfgList = new ArrayList<SysOrderFunctionCfg>();
            	sysOrderFunctionCfgList = service.selectByFunctionId(sysOrderFunctionCfg);
            	if(sysOrderFunctionCfgList != null && sysOrderFunctionCfgList.size()>=1){
            	    ResponseData responseDataFalse = new ResponseData();
            	    responseDataFalse.setSuccess(false);
            	    responseDataFalse.setMessage("功能名称已存在");
            	    return responseDataFalse;
            	}
        	}
		}
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/fms/sys/order/function/cfg/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<SysOrderFunctionCfg> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
    
	@RequestMapping(value = "/fms/sys/order/function/cfg/queryByHeaderId", method = RequestMethod.POST)
	@ResponseBody
	public ResponseData queryByHeaderId(HttpServletRequest request) {
	    String cfgId = request.getParameter("cfgId");
		return new ResponseData(service.queryByHeaderId(Long.valueOf(cfgId)));
	}
    }