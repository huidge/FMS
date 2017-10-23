package clb.core.order.controllers;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.order.dto.OrdPendingFollow;
import clb.core.order.service.IOrdPendingFollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

    @Controller
    public class OrdPendingFollowController extends BaseController{

    @Autowired
    private IOrdPendingFollowService service;


    @RequestMapping(value = "/fms/ord/pending/follow/query")
    @ResponseBody
    public ResponseData query(OrdPendingFollow dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryOrdPendingFollow(requestContext,dto,page,pageSize));
    }

    @RequestMapping(value = "/fms/ord/pending/follow/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<OrdPendingFollow> dto){
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.followBatchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/fms/ord/pending/follow/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<OrdPendingFollow> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(true);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }
}