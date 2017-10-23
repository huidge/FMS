package clb.core.notification.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.notification.dto.NtnTemplateConcern;
import clb.core.notification.service.INtnTemplateConcernService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

    @Controller
    public class NtnTemplateConcernController extends BaseController{

    @Autowired
    private INtnTemplateConcernService service;


    @RequestMapping(value = "/fms/ntn/template/concern/query")
    @ResponseBody
    public ResponseData query(NtnTemplateConcern dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext,dto,page,pageSize));
    }

    @RequestMapping(value = "/fms/ntn/template/concern/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<NtnTemplateConcern> dto){
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/fms/ntn/template/concern/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<NtnTemplateConcern> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
    }