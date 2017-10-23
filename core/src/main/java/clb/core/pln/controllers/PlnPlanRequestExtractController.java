package clb.core.pln.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.pln.dto.PlnPlanRequestExtract;
import clb.core.pln.service.IPlnPlanRequestExtractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

    @Controller
    public class PlnPlanRequestExtractController extends BaseController{

    @Autowired
    private IPlnPlanRequestExtractService service;


    @RequestMapping(value = "/fms/pln/plan/request/extract/query")
    @ResponseBody
    public ResponseData query(PlnPlanRequestExtract dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext,dto,page,pageSize));
    }

    @RequestMapping(value = "/fms/pln/plan/request/extract/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<PlnPlanRequestExtract> dto){
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/fms/pln/plan/request/extract/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<PlnPlanRequestExtract> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
    }