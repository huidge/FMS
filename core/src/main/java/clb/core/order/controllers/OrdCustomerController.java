package clb.core.order.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.order.dto.OrdCustomer;
import clb.core.order.service.IOrdCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

    @Controller
    public class OrdCustomerController extends BaseController{

    @Autowired
    private IOrdCustomerService service;


    @RequestMapping(value = "/fms/ord/customer/query")
    @ResponseBody
    public ResponseData query(OrdCustomer dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryOrdCustomer(requestContext,dto,page,pageSize));
    }

    @RequestMapping(value = "/fms/ord/customer/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<OrdCustomer> dto){
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchSubmit(requestCtx, dto));
    }

    @RequestMapping(value = "/fms/ord/customer/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<OrdCustomer> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
    }