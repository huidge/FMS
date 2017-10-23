package clb.core.order.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.order.dto.OrdTeamVisitor;
import clb.core.order.service.IOrdTeamVisitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

    @Controller
    public class OrdTeamVisitorController extends BaseController{

    @Autowired
    private IOrdTeamVisitorService service;


    @RequestMapping(value = "/fms/ord/team/visitor/query")
    @ResponseBody
    public ResponseData query(OrdTeamVisitor dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext,dto,page,pageSize));
    }

    @RequestMapping(value = "/fms/ord/team/visitor/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<OrdTeamVisitor> dto){
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/fms/ord/team/visitor/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<OrdTeamVisitor> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
    }