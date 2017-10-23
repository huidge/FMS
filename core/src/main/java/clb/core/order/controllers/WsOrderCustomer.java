package clb.core.order.controllers;

import clb.core.order.dto.OrdCustomer;
import clb.core.order.service.IOrdCustomerService;
import clb.core.sys.controllers.ClbBaseController;
import com.codahale.metrics.annotation.Timed;
import com.hand.hap.core.IRequest;
import com.hand.hap.intergration.annotation.HapInbound;
import com.hand.hap.system.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by xiaoyong.luo@hand-china.com on 2017/6/15.
 */
@Controller
public class WsOrderCustomer extends ClbBaseController {

    @Autowired
    private IOrdCustomerService service;


    /**
     *  预约保单信息查询
     *
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsOrdCustomerQuery")
    @RequestMapping(value = "/api/ordCustomer/query", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public ResponseData query(@RequestBody OrdCustomer dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryWsOrdCustomer(requestContext,dto));
    }
}
