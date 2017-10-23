package clb.core.order.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.order.dto.SysOrderCfgOperation;
import clb.core.order.dto.SysPageFieldCfg;
import clb.core.order.service.ISysOrderCfgOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

    @Controller
    public class SysOrderCfgOperationController extends BaseController{

    @Autowired
    private ISysOrderCfgOperationService service;


    @RequestMapping(value = "/fms/sys/order/cfg/operation/query")
    @ResponseBody
    public ResponseData query(SysOrderCfgOperation dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        String cfgId = request.getParameter("cfgId");
        return new ResponseData(service.selectOperationByCfgId(requestContext,Long.valueOf(cfgId),page,pageSize));
    }

    @RequestMapping(value = "/fms/sys/order/cfg/operation/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<SysOrderCfgOperation> dto){
        IRequest requestCtx = createRequestContext(request);
        String cfgId = request.getParameter("cfgId");
        for (SysOrderCfgOperation sysOrderCfgOperation : dto) {
        	sysOrderCfgOperation.setCfgId(Long.valueOf(cfgId));
		}
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/fms/sys/order/cfg/operation/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<SysOrderCfgOperation> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
    
    @RequestMapping(value = "/fms/sys/order/cfg/operation/selectOperationInfo")
    @ResponseBody
    public ResponseData selectOperationInfo(SysOrderCfgOperation dto, HttpServletRequest request) {
        String cfgId = request.getParameter("cfgId");
        return new ResponseData(service.selectOperationInfo(Long.valueOf(cfgId)));
    }
    }