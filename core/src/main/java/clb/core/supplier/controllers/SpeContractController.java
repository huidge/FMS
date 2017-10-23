package clb.core.supplier.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.supplier.dto.SpeContract;
import clb.core.supplier.service.ISpeCommonService;
import clb.core.supplier.service.ISpeContractService;
import clb.core.system.dto.ClbCode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping(value = "/spe/contract")
public class SpeContractController extends BaseController{

    @Autowired
    private ISpeContractService service;


    @RequestMapping(value = "/query")
    @ResponseBody
    public ResponseData query(SpeContract dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext,dto,page,pageSize));
    }

    @RequestMapping(value = "/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<SpeContract> dto){
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.supplierContractBatchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<SpeContract> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
}