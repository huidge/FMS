package clb.core.forecast.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;

import clb.core.common.utils.CommonUtil;
import clb.core.forecast.dto.FetPeriod;
import clb.core.forecast.service.IFetPeriodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

    @Controller
    public class FetPeriodController extends BaseController{

    @Autowired
    private IFetPeriodService service;


    @RequestMapping(value = "/fms/fet/period/query")
    @ResponseBody
    public ResponseData query(FetPeriod dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext,dto,page,pageSize));
    }
    
    @RequestMapping(value = "/fms/fet/period/queryEmpty")
    @ResponseBody
    public ResponseData queryEmpty(FetPeriod dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
    		@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
    	List<FetPeriod> list = new ArrayList<>();
    	return new ResponseData(list);
    }

    @RequestMapping(value = "/fms/fet/period/submit")
    @ResponseBody
    public ResponseData submit(HttpServletRequest request,@RequestBody List<FetPeriod> dto){
    	ResponseData responseData = new ResponseData();
    	IRequest requestCtx = createRequestContext(request);
        try {
			responseData.setRows(service.addFetPeriod(requestCtx,dto));
		} catch (Exception e) {
			CommonUtil.printStackTraceToStr(e);
			responseData.setSuccess(false);
			responseData.setMessage("时间有效期重叠!!!".equals(e.getMessage())? "时间有效期重叠!!!":"期间名称重复!!!");
		}
        return responseData;
    }

    @RequestMapping(value = "/fms/fet/period/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<FetPeriod> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
    
    @RequestMapping(value = "/fms/fet/period/updatePeriodStatus")
    @ResponseBody
    public ResponseData updatePeriodStatus(HttpServletRequest request,FetPeriod dto){
    	IRequest requestCtx = createRequestContext(request);
    	FetPeriod fetPeriod = service.selectByPrimaryKey(requestCtx, dto);
    	String statusCode = fetPeriod.getStatusCode();
    		//将来
    	if (statusCode.equals("FUTURE")) {
    		dto.setStatusCode("OPEN");
    		//打开
		}else if(statusCode.equals("OPEN")){
			dto.setStatusCode("CLOSE");
			//关闭
		}else if(statusCode.equals("CLOSE")){
			dto.setStatusCode("OPEN");
		}
    	service.updateByPrimaryKeySelective(requestCtx, dto);
    	return new ResponseData();
    }
    }