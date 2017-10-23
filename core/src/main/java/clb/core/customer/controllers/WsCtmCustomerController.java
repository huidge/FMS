package clb.core.customer.controllers;

import clb.core.common.service.ISequenceService;
import clb.core.customer.dto.CtmCustomer;
import clb.core.customer.dto.CtmCustomerFamily;
import clb.core.customer.service.ICtmCustomerFamilyService;
import clb.core.customer.service.ICtmCustomerService;
import clb.core.sys.controllers.ClbBaseController;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.hand.hap.core.IRequest;
import com.hand.hap.intergration.annotation.HapInbound;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;

@Controller
public class WsCtmCustomerController extends ClbBaseController {

    @Autowired
    private ICtmCustomerService service;

    @Autowired
    private ICtmCustomerFamilyService familSservice;

    @Autowired
    ICtmCustomerFamilyService ctmCustomerFamilyService;

    /**
     * 更新/插入客户信息
     * @param dto
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsCtmCustomerSubmit")
    @RequestMapping(value = "/api/ctmCustomer/submit", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData createCustomer(@RequestBody CtmCustomer dto, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        Locale locale = RequestContextUtils.getLocale(request);

        //有客户直接返回
        List<CtmCustomer> list = service.selectByIdNumber(requestCtx, dto);
        if(list != null && list.size() > 0){
            return new ResponseData(list);
        }

        String isOK = service.checkData(dto,locale);
        ResponseData responseData = new ResponseData();
        if(!"".equals(isOK)){
            responseData.setSuccess(false);
            responseData.setMessage(isOK);
        }else{
            responseData.setRows(service.createCustomer(requestCtx,locale,dto));
        }
        return responseData;
    }



    /**
     * 查询客户列表
     *
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsCtmCustomerQuery")
    @RequestMapping(value = "/api/ctmCustomer/query", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData query(@RequestBody CtmCustomer dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.query(requestContext, dto, page, pageSize));
    }

    /**
     * 查询客户列表
     *
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsCtmCustomerWsQuery")
    @RequestMapping(value = "/api/ctmCustomer/wsQuery", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData wsQuery(@RequestBody CtmCustomer dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.wsQuery(requestContext, dto, page, pageSize));
    }


    /**
     * 查询客户详情
     *
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsCtmCustomerDetailQuery")
    @RequestMapping(value = "/api/ctmCustomer/detail/query", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData queryOne(@RequestBody CtmCustomer dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);

        List<CtmCustomer> list = service.select(requestContext, dto, page, pageSize);
        for(CtmCustomer c : list){
            CtmCustomer ctmCustomer = new CtmCustomer();
            ctmCustomer.setCustomerId(c.getCustomerId());
            List<CtmCustomerFamily> cfList = familSservice.selectByCustomerId(ctmCustomer);
            c.setCustomerFamilyList(cfList);
        }
        return new ResponseData(list);
    }


    /**
     * 查询客户家庭成员
     *
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsCtmCustomerFamilyQuery")
    @RequestMapping(value = "/api/ctmCustomer/family/query", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData queryFamily(@RequestBody CtmCustomerFamily dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                    @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);

        return new ResponseData(familSservice.select(requestContext, dto, page, pageSize));
    }



    /**
     * 删除 客户
     *
     * @param dto
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsCtmCustomerDelete")
    @RequestMapping(value = "/api/ctmCustomer/delete", method = RequestMethod.POST, produces = {"application/json" })
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

}