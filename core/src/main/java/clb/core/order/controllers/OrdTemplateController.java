package clb.core.order.controllers;

import clb.core.system.dto.ClbCodeValue;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.order.dto.OrdTemplate;
import clb.core.order.dto.OrdTemplateLine;
import clb.core.order.service.IOrdTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

    @Controller
    public class OrdTemplateController extends BaseController{

    @Autowired
    private IOrdTemplateService service;


    @RequestMapping(value = "/fms/ord/template/query")
    @ResponseBody
    public ResponseData query(OrdTemplate dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryOrdTemplate(requestContext,dto,page,pageSize));
    }

    @RequestMapping(value = "/fms/ord/template/queryApplyClass")
    @ResponseBody
    public ResponseData queryApplyClass(OrdTemplate dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryApplyClass(requestContext,dto));
    }

    @RequestMapping(value = "/fms/ord/template/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<OrdTemplate> dto){
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/fms/ord/template/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<OrdTemplate> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }

    @RequestMapping(value = "/fms/ord/template/queryCodeValue")
    @ResponseBody
    public ResponseData queryCodeValue(ClbCodeValue dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                       @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryCodeValue(requestContext,dto));
    }

    @RequestMapping(value = "/fms/ord/template/queryCodeValueUnq")
    @ResponseBody
    public ResponseData queryCodeValueUnq(ClbCodeValue dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                       @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryCodeValueUnq(requestContext,dto));
    }

    @RequestMapping(value = "/fms/ord/template/submitCodeValue")
    @ResponseBody
    public ResponseData updateCodeValue(HttpServletRequest request,@RequestBody List<ClbCodeValue> dto){
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.codeBatchUpdate(requestCtx, dto));
    }
    /**
     * 通过模板类型和应用分类(售后项目)  查找对应的应用项目(售后类型)和模板类型Id
     * @param request
     * @param dto
     * @return
     */
    @RequestMapping(value = "/fms/ord/template/queryApplyItem")
    @ResponseBody
    public ResponseData queryApplyItem(HttpServletRequest request,OrdTemplate dto){
    	IRequest requestCtx = createRequestContext(request);
    	return new ResponseData(service.queryApplyItem(requestCtx,dto));
    }
    /**
     * 通过模板类型查找对应应用分类(售后项目)
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "/fms/ord/template/queryApplyClassOnOrdAfter")
    @ResponseBody
    public ResponseData queryApplyClassOnOrdAfter(OrdTemplate dto,HttpServletRequest request) {
    	IRequest requestContext = createRequestContext(request);
    	return new ResponseData(service.queryApplyClassOnOrdAfter(requestContext,dto));
    }
    /**
     * 模板名称和内容
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "/fms/ord/template/queryTemplateNameOnOrdAfter")
    @ResponseBody
    public ResponseData queryTemplateNameOnOrdAfter(OrdTemplateLine dto,HttpServletRequest request) {
    	IRequest requestContext = createRequestContext(request);
    	return new ResponseData(service.queryTemplateNameOnOrdAfter(requestContext,dto));
    }   

    }