package clb.core.order.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.order.dto.OrdTemplateLine;
import clb.core.order.service.IOrdTemplateLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

    @Controller
    public class OrdTemplateLineController extends BaseController{

    @Autowired
    private IOrdTemplateLineService service;


    @RequestMapping(value = "/fms/ord/template/line/query")
    @ResponseBody
    public ResponseData query(OrdTemplateLine dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext,dto,page,pageSize));
    }

    @RequestMapping(value = "/fms/ord/template/line/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<OrdTemplateLine> dto){
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/fms/ord/template/line/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<OrdTemplateLine> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
    }