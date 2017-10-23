package clb.core.supplier.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;

import clb.core.common.exceptions.CommonException;
import clb.core.supplier.dto.SpeCalendar;
import clb.core.supplier.exceptions.SpeException;
import clb.core.supplier.service.ISpeCalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping(value = "/supplier/calendar")
public class SpeCalendarController extends BaseController{

    @Autowired
    private ISpeCalendarService service;


    @RequestMapping(value = "/query")
    @ResponseBody
    public ResponseData query(SpeCalendar dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        return new ResponseData(service.selectData(dto, page,pageSize));
    }
    
    @RequestMapping(value = "/validatequery")
    @ResponseBody
    public ResponseData validatequery(SpeCalendar dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest iRequest = this.createRequestContext(request);
    	return new ResponseData(service.select(iRequest,dto, page,pageSize));
    }
    
    @RequestMapping(value = "/getAll")
    @ResponseBody
    public ResponseData getAll(HttpServletRequest request) {
    	IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectAll(requestContext));
    }

    @RequestMapping(value = "/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody SpeCalendar dto) throws CommonException{
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.calendarUpdate(request,requestCtx, dto));
    }

    @RequestMapping(value = "/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<SpeCalendar> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
}