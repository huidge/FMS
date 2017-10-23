package clb.core.customer.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.customer.dto.CtmCustomerFamily;
import clb.core.customer.service.ICtmCustomerFamilyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequestMapping({"/fms/ctm/customer/family"})
@Controller
public class CtmCustomerFamilyController extends BaseController {

    @Autowired
    private ICtmCustomerFamilyService service;


    @RequestMapping(value = "/query")
    @ResponseBody
    public ResponseData query(CtmCustomerFamily dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request, @RequestBody List<CtmCustomerFamily> dto) {
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<CtmCustomerFamily> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }
}