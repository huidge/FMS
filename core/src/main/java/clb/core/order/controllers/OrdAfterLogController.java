package clb.core.order.controllers;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;

import clb.core.order.dto.OrdAfterLog;
import clb.core.order.service.IOrdAfterLogService;

    @Controller
    public class OrdAfterLogController extends BaseController{

    @Autowired
    private IOrdAfterLogService service;


    @RequestMapping(value = "/fms/ord/after/log/query")
    @ResponseBody
    public ResponseData query(OrdAfterLog dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        //下载50条  sql里面控制
        return new ResponseData(service.query(requestContext,dto));
    }

    @RequestMapping(value = "/fms/ord/after/log/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<OrdAfterLog> dto){
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }
    
    @RequestMapping(value = "/fms/ord/after/log/add")
    @ResponseBody
    public ResponseData add(HttpServletRequest request,@RequestBody List<OrdAfterLog> dto){
    	IRequest requestCtx = createRequestContext(request);
    	OrdAfterLog ordAfterLog = dto.get(0);
    	ResponseData responseData = new ResponseData();
    	try {
    		ordAfterLog.setStatusDate(new Date());
    		ordAfterLog.setComment(ordAfterLog.getExpressCompany()+":"+ordAfterLog.getExpressNum());
    		ordAfterLog.setObjectVersionNumber(1L);
        	service.insertSelective(requestCtx,ordAfterLog);
		} catch (Exception e) {
			responseData.setSuccess(false);
		}
    	responseData.setRows(dto);
    	return responseData;
    }

    @RequestMapping(value = "/fms/ord/after/log/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<OrdAfterLog> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
    }