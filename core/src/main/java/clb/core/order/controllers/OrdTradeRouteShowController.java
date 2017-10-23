package clb.core.order.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.order.dto.OrdTradeRouteShow;
import clb.core.order.service.IOrdTradeRouteShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

    @Controller
    public class OrdTradeRouteShowController extends BaseController{

    @Autowired
    private IOrdTradeRouteShowService service;


    @RequestMapping(value = "/fms/ord/trade/route/show/query")
    @ResponseBody
    public ResponseData query(OrdTradeRouteShow dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext,dto,page,pageSize));
    }

    @RequestMapping(value = "/fms/ord/trade/route/show/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<OrdTradeRouteShow> dto){
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/fms/ord/trade/route/show/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<OrdTradeRouteShow> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
    }