package clb.core.forecast.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;

import clb.core.common.exceptions.CommonException;
import clb.core.forecast.dto.FetChannelCheck;
import clb.core.forecast.service.IFetChannelCheckService;

@Controller
@RequestMapping(value = "/fet/channel/check")
public class FetChannelCheckController extends BaseController{

    @Autowired
    private IFetChannelCheckService service;

    /**
     * 普通查询 
     */
    @RequestMapping(value = "/query")
    @ResponseBody
    public ResponseData query(FetChannelCheck dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
    	return new ResponseData(service.getData(iRequest,dto,page,pageSize));
    }
    
    /**
     * 汇总查询 
     */
    @RequestMapping(value = "/summaryQuery")
    @ResponseBody
    public ResponseData summaryQuery(FetChannelCheck dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize,HttpServletRequest request) {
    	IRequest iRequest = createRequestContext(request);
        return new ResponseData(service.summaryQuery(iRequest,dto,page,pageSize));
    }
    
    /**
     * 导出数据
     * @throws CommonException 
     */
    @RequestMapping(value = "/exportData")
    @ResponseBody
    public void exportData(@RequestParam Map<String, String> params,HttpServletRequest request,HttpServletResponse response) throws CommonException {
    	IRequest iRequest = createRequestContext(request);
    	String sqlId = params.get("sqlId");
    	service.exportData(iRequest, sqlId, params,request,response);
    }
    
    /**
     * 查询所有
     */
    @RequestMapping(value = "/getAll")
    @ResponseBody
    public ResponseData getAll(HttpServletRequest request) {
    	IRequest requestContext = createRequestContext(request);
    	List<FetChannelCheck> data = service.selectAll(requestContext);
        List<String> receiptPeriods = new ArrayList<>();
        for(FetChannelCheck d:data){
        	if(!receiptPeriods.contains(d.getCheckPeriod().trim())){
        		receiptPeriods.add(d.getCheckPeriod().trim());
        	}
        }
        return new ResponseData(receiptPeriods);
    }
    
    /**
     * 确认
     */
    @RequestMapping(value = "/ensure")
    @ResponseBody
    public ResponseData enSure(@RequestBody FetChannelCheck dto,HttpServletRequest request) {
    	IRequest requestContext = createRequestContext(request);
    	return new ResponseData(service.enSure(requestContext,dto));
    }
    
}