package clb.core.pln.controllers;

import clb.core.pln.dto.PlnPlanRequest;
import clb.core.pln.dto.PlnPlanSpiderSetting;
//import clb.core.pln.service.IPlnPlanSpiderService;
import clb.core.pln.service.IPlnPlanSpiderService;
import clb.core.pln.service.IPlnPlanSpiderSettingService;
import com.hand.hap.core.IRequest;
import com.hand.hap.intergration.annotation.HapInbound;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.codahale.metrics.annotation.Timed;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * @author detai.kong@hand-china.com
 * @version 1.0
 * @name PlnPlanSpiderController
 * @description:爬虫接口Controller
 * @date 2017/5/20
 */

@Controller
public class PlnPlanSpiderController extends BaseController{

    @Autowired
    private IPlnPlanSpiderService plnPlanSpiderService;

    /**
     * 爬虫计划书申请单查询
     * @param applyNumber
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsPlnRequestQuery")
    @RequestMapping(value = "/api/planRequest/query", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData query(String applyNumber) {
//        IRequest requestContext = createRequestContext(request);
        PlnPlanRequest dto = new PlnPlanRequest();
        dto.setRequestNumber(applyNumber);
        return new ResponseData(plnPlanSpiderService.queryPlanRequest(dto));
    }




    /**
     * 爬虫设置账号密码查询
     * @param itemId,sublineId,payMethod,currency,spiderName
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsPlnSpaderAuthQuery")
    @RequestMapping(value = "/api/spader/authquery", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData spaderauthquery(String itemId,String sublineId,String payMethod,String currency,String spiderName) {
//        IRequest requestContext = createRequestContext(request);
        PlnPlanSpiderSetting dto = new PlnPlanSpiderSetting();
        dto.setItemId(Long.parseLong(itemId));
        dto.setSublineId(Long.parseLong(sublineId));
        dto.setPayMethod(payMethod);
        dto.setCurrency(currency);
        dto.setSpiderProgram(spiderName);
        return new ResponseData(plnPlanSpiderService.querySpaderAuth(dto));
    }



    /*@RequestMapping(value = "/pln/plan/spader/execute")
    @ResponseBody
    public ResponseData executeSpader(PlnPlanRequest dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(plnPlanRequestService.selectPlanRequest(dto, requestContext, page, pageSize));
    }*/
}