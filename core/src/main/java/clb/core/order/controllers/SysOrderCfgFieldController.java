package clb.core.order.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.order.dto.SysOrderCfgField;
import clb.core.order.dto.SysPageFieldCfg;
import clb.core.order.service.ISysOrderCfgFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

    @Controller
    public class SysOrderCfgFieldController extends BaseController{

    @Autowired
    private ISysOrderCfgFieldService service;


    @RequestMapping(value = "/fms/sys/order/cfg/field/query")
    @ResponseBody
    public ResponseData query(SysOrderCfgField dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        String cfgId = request.getParameter("cfgId");
        return new ResponseData(service.selectFieldByCfgId(requestContext,Long.valueOf(cfgId),page,pageSize));
    }

    @RequestMapping(value = "/fms/sys/order/cfg/field/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<SysOrderCfgField> dto){
        IRequest requestCtx = createRequestContext(request);
        String cfgId = request.getParameter("cfgId");
        for (SysOrderCfgField sysOrderCfgField : dto) {
        	sysOrderCfgField.setCfgId(Long.valueOf(cfgId));
		}
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/fms/sys/order/cfg/field/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<SysOrderCfgField> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
    
    @RequestMapping(value = "/fms/sys/order/cfg/field/selectFieldInfo")
    @ResponseBody
    public ResponseData selectFieldInfo(SysOrderCfgField dto, HttpServletRequest request) {
        String cfgId = request.getParameter("cfgId");
        return new ResponseData(service.selectFieldInfo(Long.valueOf(cfgId)));
    }
    }