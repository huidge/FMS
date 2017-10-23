package clb.core.commission.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.commission.dto.CmnTradeRoute;
import clb.core.commission.service.ICmnTradeRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

    @Controller
    public class CmnTradeRouteController extends BaseController{

    @Autowired
    private ICmnTradeRouteService service;


    @RequestMapping(value = "/fms/cmn/trade/route/query")
    @ResponseBody
    public ResponseData query(CmnTradeRoute dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext,dto,page,pageSize));
    }

    @RequestMapping(value = "/fms/cmn/trade/route/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<CmnTradeRoute> dto){
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/fms/cmn/trade/route/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<CmnTradeRoute> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
    }