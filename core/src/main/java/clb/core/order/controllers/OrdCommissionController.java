package clb.core.order.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.order.dto.OrdCommission;
import clb.core.order.service.IOrdCommissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

    @Controller
    public class OrdCommissionController extends BaseController{

    @Autowired
    private IOrdCommissionService service;


    @RequestMapping(value = "/fms/ord/commission/query")
    @ResponseBody
    public ResponseData query(OrdCommission dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryOrdCommission(requestContext,dto,page,pageSize));
    }

    @RequestMapping(value = "/fms/ord/commission/queryCompany")
    @ResponseBody
    public ResponseData queryCompany(OrdCommission dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                     @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryCompany(requestContext,dto));
    }

    @RequestMapping(value = "/fms/ord/commission/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<OrdCommission> dto){
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/fms/ord/commission/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<OrdCommission> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }

     @RequestMapping(value = "/fms/ord/commission/queryManualFlag")
     @ResponseBody
     public ResponseData queryManualFlag(OrdCommission dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                         @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
         IRequest requestContext = createRequestContext(request);
         return new ResponseData(service.queryManualFlag(requestContext,dto));
     }

}