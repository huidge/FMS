package clb.core.order.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.order.dto.OrdBeneficiary;
import clb.core.order.service.IOrdBeneficiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

    @Controller
    public class OrdBeneficiaryController extends BaseController{

    @Autowired
    private IOrdBeneficiaryService service;


    @RequestMapping(value = "/fms/ord/beneficiary/query")
    @ResponseBody
    public ResponseData query(OrdBeneficiary dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryOrdBeneficiary(requestContext,dto,page,pageSize));
    }

    @RequestMapping(value = "/fms/ord/beneficiary/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<OrdBeneficiary> dto){
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/fms/ord/beneficiary/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<OrdBeneficiary> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
    }