package clb.core.order.controllers;

import clb.core.order.dto.OrdOrder;
import clb.core.system.dto.ClbUser;
import clb.core.system.service.IClbUserService;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.order.dto.OrdPending;
import clb.core.order.service.IOrdPendingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class OrdPendingController extends BaseController {

    @Autowired
    private IOrdPendingService service;

    @Autowired
    private IClbUserService userService;


    @RequestMapping(value = "/fms/ord/pending/query")
    @ResponseBody
    public ResponseData query(OrdPending dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryOrdPending(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/fms/ord/pending/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request, @RequestBody List<OrdPending> dto) {
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.pendingBatchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/fms/ord/pending/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<OrdPending> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }

    /**
     *  查询处理人
     *  处理人可选到：1）这个单的渠道，2）和当前用户关联的员工同公司的，且具有‘Pending Staff’角色的用户列表
     * @param request
     * @param ordOrder
     * @return
     */
    @RequestMapping(value = "/fms/ord/pending/queryDealUser", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public ResponseData queryDealUser(HttpServletRequest request, OrdOrder ordOrder) {
        IRequest requestCtx = createRequestContext(request);
        ordOrder.setUserId(requestCtx.getUserId());
        return new ResponseData(service.queryDealUser(ordOrder));
    }

    @RequestMapping(value = "/fms/ord/pending/queryNotClosed")
    @ResponseBody
    public ResponseData queryNotClosed(OrdPending dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                       @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryNotClosed(requestContext, dto));
    }
}