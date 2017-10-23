package clb.core.pln.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.pln.dto.PlnPlanRequestAdtlRisk;
import clb.core.pln.service.IPlnPlanRequestAdtlRiskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

    @Controller
    public class PlnPlanRequestAdtlRiskController extends BaseController{

    @Autowired
    private IPlnPlanRequestAdtlRiskService service;


    @RequestMapping(value = "/fms/pln/plan/request/adtl/risk/query")
    @ResponseBody
    public ResponseData query(PlnPlanRequestAdtlRisk dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext,dto,page,pageSize));
    }

    @RequestMapping(value = "/fms/pln/plan/request/adtl/risk/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<PlnPlanRequestAdtlRisk> dto){
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/fms/pln/plan/request/adtl/risk/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<PlnPlanRequestAdtlRisk> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
    }