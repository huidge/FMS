package clb.core.production.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.hand.hap.core.IRequest;
import com.hand.hap.intergration.annotation.HapInbound;
import com.hand.hap.system.dto.ResponseData;

import clb.core.commission.dto.CmnCalc;
import clb.core.production.dto.PrdImageText;
import clb.core.production.dto.PrdItemLabels;
import clb.core.production.dto.PrdItemSubline;
import clb.core.production.dto.PrdItems;
import clb.core.production.dto.PrdPremium;
import clb.core.production.mapper.PrdItemLabelsMapper;
import clb.core.production.service.IPrdItemSublineService;
import clb.core.production.service.IPrdItemsService;
import clb.core.production.service.IPrdPremiumService;
import clb.core.sys.controllers.ClbBaseController;

@Controller
public class WsPrdItemController extends ClbBaseController {

    @Autowired
    private IPrdItemsService service;
    @Autowired
    private IPrdPremiumService prdPremiumService;

    @Autowired
    private IPrdItemSublineService prdItemSublineService;

    @Autowired
    private PrdItemLabelsMapper prdItemLabelsMapper;


    @Timed
    @HapInbound(apiName = "ClbWsPrdItemList")
    @RequestMapping(value = "/api/production/list", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData querylist(@RequestBody PrdItems dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                         @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request){
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.wsSelectAlive(dto, page, pageSize));
    }


    @Timed
    @HapInbound(apiName = "ClbWsPrdItemHeaderList")
    @RequestMapping(value = "/api/production/headerlist", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData queryHeaderlist(@RequestBody PrdItems dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request){
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.wsSelectHeaderAlive(dto, page, pageSize));
    }

    @Timed
    @HapInbound(apiName = "ClbWsPrdItemByChannel")
    @RequestMapping(value = "/api/production/queryByChannel", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData queryByChannel(@RequestBody PrdItems dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                       @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request){
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectByChannel(requestContext,dto));
    }

    @Timed
    @HapInbound(apiName = "ClbWsPrdSublineByChannel")
    @RequestMapping(value = "/api/subline/queryByChannel", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData queryByChannel(@RequestBody PrdItemSubline dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                       @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request){
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(prdItemSublineService.selectByChannel(requestContext,dto));
    }

    @Timed
    @HapInbound(apiName = "ClbWsPrdItemDetail")
    @RequestMapping(value = "/api/production/detail", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData queryDetail(@RequestBody PrdItems dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.wsSelectAlive(dto, page, pageSize));
    }


    @Timed
    @HapInbound(apiName = "ClbWsPrdImageText")
    @RequestMapping(value = "/api/production/getImageText", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData getImageText(@RequestBody PrdImageText dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                     @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.getImageText(dto, page, pageSize));
    }

    /******
     * 保费表查询接口
     * @param dto
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsPrdPremium")
    @RequestMapping(value = "/api/production/getPrdPremium", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData getPrdPremium(@RequestBody PrdPremium dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(prdPremiumService.selectAllFields(requestContext, dto, page, pageSize));
    }


    /******
     * 产品对比
     * @param dto
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsPrdCompare")
    @RequestMapping(value = "/api/public/production/prdCompare", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData prdCompare(@RequestBody List<CmnCalc> dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return service.prdCompare(requestContext,dto);
    }
    
    @Timed
    @HapInbound(apiName = "ClbWsPrdCash")
    @RequestMapping(value = "/api/public/production/queryCash", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData queryCash(@RequestBody List<CmnCalc> dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return service.queryCashValueByAge(requestContext,dto);
    }
    
    /***
     * 产品雷达图
     * @param dto
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsPrdCompare")
    @RequestMapping(value = "/api/public/production/prdRadarChart", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData prdRadarChart(@RequestBody List<CmnCalc> dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return service.prdRadarChart(requestContext,dto);
    }
    
    @Timed
    @HapInbound(apiName = "ClbWsItemLabels")
    @RequestMapping(value = "/api/production/queryItemLabels", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData queryItemLabels(@RequestBody PrdItemLabels dto, HttpServletRequest request) {
        return new ResponseData(prdItemLabelsMapper.select(dto));
    }
}