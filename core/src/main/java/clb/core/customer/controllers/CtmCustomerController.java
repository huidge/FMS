package clb.core.customer.controllers;

import clb.core.common.exceptions.CommonException;
import clb.core.common.service.ISequenceService;
import clb.core.customer.dto.CtmCustomerFamily;
import clb.core.customer.service.ICtmCustomerFamilyService;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.customer.dto.CtmCustomer;
import clb.core.customer.service.ICtmCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping({"/fms/ctm/customer"})
public class CtmCustomerController extends BaseController {

    @Autowired
    private ICtmCustomerService service;

    @Autowired
    ICtmCustomerFamilyService ctmCustomerFamilyService;

    @Autowired
    ISequenceService sequenceService;

    private final static String CUSTOMER_CODE = "CUSTOMER";

    @RequestMapping(value = "/queryAll")
    @ResponseBody
    public ResponseData queryAll(CtmCustomer dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                 @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.query(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/queryAllCustomer")
    @ResponseBody
    public ResponseData queryAllCustomer(CtmCustomer dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                         @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryAllCustomer(requestContext, dto));
    }

    @RequestMapping(value = "submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request, @RequestBody List<CtmCustomer> dto) {
        IRequest requestCtx = createRequestContext(request);
        //如果是新增记录就获取客户流水编号
        if(dto != null && dto.size() > 0){
            for(int i = 0 ; i < dto.size(); i++){
                if(dto.get(i).getCustomerId() == null || "add".equals(dto.get(i).get__status())){
                    dto.get(i).setCustomerCode(sequenceService.getSequence(CUSTOMER_CODE));
                }
            }
        }
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<CtmCustomer> dto) {
        //先将头表数据删除
        service.batchDelete(dto);
        //再将行表数据删除
        if(dto != null && dto.size() > 0){
            for(CtmCustomer customer : dto){
                List<CtmCustomerFamily> customerFamilys = ctmCustomerFamilyService.selectByCustomerId(customer);
                ctmCustomerFamilyService.batchDelete(customerFamilys);
            }
        }
        return new ResponseData();
    }

    @RequestMapping(value = "query")
    @ResponseBody
    public ResponseData query(CtmCustomer dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext, dto, page, pageSize));
    }
}