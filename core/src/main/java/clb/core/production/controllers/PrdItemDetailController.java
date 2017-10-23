package clb.core.production.controllers;

import clb.core.production.dto.*;
import clb.core.production.service.*;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.exception.TokenException;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by rex.hua on 17/4/12.
 */
@Controller
@RequestMapping({"/production/itemDetail"})
public class PrdItemDetailController extends BaseController {

    @Autowired
    private IPrdItemLabelLinesService prdItemLabelLinesService;
    @Autowired
    private IPrdItemPaymodeService  prdItemPaymodeService;
    @Autowired
    private IPrdItemSecurityPlanService prdItemSecurityPlanService;
    @Autowired
    private IPrdItemSelfpayService prdItemSelfpayService;
    @Autowired
    private IPrdItemSublineService prdItemSublineService;
    @Autowired
    private IPrdImageTextService prdImageTextService;
    @Autowired
    private IPrdItemLabelsService prdItemLabelsService;

    ///////////////////////////////////////////////////////////////////////////////////////////
    // 图文版块行
    @RequestMapping(value = "/imageTextQuery")
    @ResponseBody
    public ResponseData imageTextQuery(PrdImageText dto, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        List<PrdImageText> List = prdImageTextService.query(dto);
        return new ResponseData(List);
    }

    @RequestMapping(value = "/imageTextRemove")
    @ResponseBody
    public ResponseData imageTextRemove(@RequestBody List<PrdImageText> dtoList,HttpServletRequest request) {
        prdImageTextService.batchDelete(dtoList);
        return new ResponseData();
    }

    @RequestMapping(value = "/imageTextSubmit")
    @ResponseBody
    public ResponseData imageTextSubmit(@RequestBody List<PrdImageText> dtoList, BindingResult result,HttpServletRequest request) {
        getValidator().validate(dtoList, result);
        if (result.hasErrors()) {
            ResponseData rd = new ResponseData(false);
            rd.setMessage(getErrorMessage(result, request));
            return rd;
        }
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(prdImageTextService.batchUpdate(iRequest, dtoList));
    }
    ///////////////////////////////////////////////////////////////////////////////////////////
    // 产品标签行
    @RequestMapping(value = "/tagsQuery")
    @ResponseBody
    public ResponseData tagsQuery(PrdItemLabelLines dto, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        List<PrdItemLabelLines> List = prdItemLabelLinesService.selectAlive(dto);
        return new ResponseData(List);
    }

    @RequestMapping(value = "/tagsRemove")
    @ResponseBody
    public ResponseData tagsDelete(PrdItemLabelLines dto,HttpServletRequest request) {
        //查找该产品标签的信息
        PrdItemLabelLines dto2 = new PrdItemLabelLines();
        dto2.setLabelName(dto.getLabelName());
        List<PrdItemLabelLines> dtoList = prdItemLabelLinesService.selectAlive(dto);
        //删除该产品标签
        prdItemLabelLinesService.deleteByItemLabel(dto);
        //查找所有相同labelId的产品标签
        if (dtoList.size() > 0) {
            PrdItemLabelLines dto3 = new PrdItemLabelLines();
            dto3.setLabelId(dtoList.get(0).getLabelId());
            dtoList = prdItemLabelLinesService.selectAlive(dto3);
            //若不存在该labelId的产品标签，则删除头表数据
            if (dtoList.size() == 0) {
                PrdItemLabels itemLabel = new PrdItemLabels();
                itemLabel.setLabelId(dto3.getLabelId());
                prdItemLabelsService.deleteByPrimaryKey(itemLabel);
            }
        }
        return new ResponseData();
    }

    @RequestMapping(value = "/choiceTags")
    @ResponseBody
    public ResponseData choiceTags(PrdItemLabelLines dto,HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(prdItemLabelLinesService.choiceTags(dto));
    }
    ///////////////////////////////////////////////////////////////////////////////////////////
    // 缴费方式行
    @RequestMapping(value = "/paymodeQuery")
    @ResponseBody
    public ResponseData paymodeQuery(PrdItemPaymode dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        List<PrdItemPaymode> List = prdItemPaymodeService.query(iRequest, dto, page, pagesize);
        return new ResponseData(List);
    }

    @RequestMapping(value = "/queryAllCurrency")
    @ResponseBody
    public ResponseData queryAllCurrency(PrdItemPaymode dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                     @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        List<PrdItemPaymode> List = prdItemPaymodeService.queryAllCurrency(iRequest, dto);
        return new ResponseData(List);
    }

    @RequestMapping(value = "/paymodeSubmit", method = {RequestMethod.POST})
    public ResponseData paymodeSubmit(@RequestBody List<PrdItemPaymode> paymodeSubmitList, BindingResult result, HttpServletRequest request) {
        getValidator().validate(paymodeSubmitList, result);
        if (result.hasErrors()) {
            ResponseData rd = new ResponseData(false);
            rd.setMessage(getErrorMessage(result, request));
            return rd;
        }
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(prdItemPaymodeService.batchUpdate(iRequest, paymodeSubmitList));
    }

    @RequestMapping(value = "/paymodeRemove")
    @ResponseBody
    public ResponseData paymodeRemove(HttpServletRequest request,@RequestBody List<PrdItemPaymode> dto){
        prdItemPaymodeService.batchDelete(dto);
        return new ResponseData();
    }
    ///////////////////////////////////////////////////////////////////////////////////////////
    // 子产品行
    @RequestMapping(value = "/sublineQuery")
    @ResponseBody
    public ResponseData sublineQuery(PrdItemSubline dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                     @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        List<PrdItemSubline> List = prdItemSublineService.query(iRequest, dto, page, pagesize);
        return new ResponseData(List);
    }

    @RequestMapping(value = "/sublineSubmit", method = {RequestMethod.POST})
    public ResponseData sublineSubmit(@RequestBody List<PrdItemSubline> prdItemSublineList, BindingResult result, HttpServletRequest request) {
        getValidator().validate(prdItemSublineList, result);
        if (result.hasErrors()) {
            ResponseData rd = new ResponseData(false);
            rd.setMessage(getErrorMessage(result, request));
            return rd;
        }
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(prdItemSublineService.batchUpdate(iRequest, prdItemSublineList));
    }

    @RequestMapping(value = "/sublineRemove")
    @ResponseBody
    public ResponseData sublineRemove(HttpServletRequest request,@RequestBody List<PrdItemSubline> dto){
        prdItemSublineService.batchDelete(dto);
        return new ResponseData();
    }
    ///////////////////////////////////////////////////////////////////////////////////////////
    // 保障计划行
    @RequestMapping(value = "/securityPlanQuery")
    @ResponseBody
    public ResponseData securityPlanQuery(PrdItemSecurityPlan dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                     @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        List<PrdItemSecurityPlan> List = prdItemSecurityPlanService.query(iRequest, dto, page, pagesize);
        return new ResponseData(List);
    }

    @RequestMapping(value = "/securityLevelQuery")
    @ResponseBody
    public ResponseData querySecurityLevel(PrdItemSecurityPlan dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                          @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        List<PrdItemSecurityPlan> List = prdItemSecurityPlanService.querySecurityLevel(iRequest, dto);
        return new ResponseData(List);
    }

    @RequestMapping(value = "/securityRegionQuery")
    @ResponseBody
    public ResponseData querySecurityRegion(PrdItemSecurityPlan dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                           @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        List<PrdItemSecurityPlan> List = prdItemSecurityPlanService.querySecurityRegion(iRequest, dto);
        return new ResponseData(List);
    }

    @RequestMapping(value = "/selfpayAllQuery")
    @ResponseBody
    public ResponseData querySelfpay(PrdItemSelfpay dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                     @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        List<PrdItemSelfpay> List = prdItemSelfpayService.querySelfpay(iRequest, dto);
        return new ResponseData(List);
    }

    @RequestMapping(value = "/securityPlanSubmit", method = {RequestMethod.POST})
    public ResponseData securityPlanSubmit(@RequestBody List<PrdItemSecurityPlan> prdItemSecurityPlanList, BindingResult result, HttpServletRequest request) {
        getValidator().validate(prdItemSecurityPlanList, result);
        if (result.hasErrors()) {
            ResponseData rd = new ResponseData(false);
            rd.setMessage(getErrorMessage(result, request));
            return rd;
        }
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(prdItemSecurityPlanService.batchUpdate(iRequest, prdItemSecurityPlanList));
    }

    @RequestMapping(value = "/securityPlanRemove")
    @ResponseBody
    public ResponseData securityPlanRemove(HttpServletRequest request,@RequestBody List<PrdItemSecurityPlan> dto){
        prdItemSecurityPlanService.batchDelete(dto);
        return new ResponseData();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // 自付选项行
    @RequestMapping(value = "/selfpayQuery")
    @ResponseBody
    public ResponseData selfpayQuery(PrdItemSelfpay dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                          @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        List<PrdItemSelfpay> List = prdItemSelfpayService.query(iRequest, dto, page, pagesize);
        return new ResponseData(List);
    }

    @RequestMapping(value = "/selfpaySubmit", method = {RequestMethod.POST})
    public ResponseData selfpaySubmit(@RequestBody List<PrdItemSelfpay> prdItemSelfpayList, BindingResult result, HttpServletRequest request) {
        getValidator().validate(prdItemSelfpayList, result);
        if (result.hasErrors()) {
            ResponseData rd = new ResponseData(false);
            rd.setMessage(getErrorMessage(result, request));
            return rd;
        }
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(prdItemSelfpayService.batchUpdate(iRequest, prdItemSelfpayList));
    }

    @RequestMapping(value = "/selfpayRemove")
    @ResponseBody
    public ResponseData selfpayRemove(HttpServletRequest request,@RequestBody List<PrdItemSelfpay> dto){
        prdItemSelfpayService.batchDelete(dto);
        return new ResponseData();
    }
}