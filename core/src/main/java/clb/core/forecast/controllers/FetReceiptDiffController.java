package clb.core.forecast.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;

import clb.core.common.exceptions.CommonException;
import clb.core.forecast.dto.FetReceiptDiff;
import clb.core.forecast.dto.FetReceivable;
import clb.core.forecast.service.IFetReceiptDiffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/fet/receiptdiff")
public class FetReceiptDiffController extends BaseController{

    @Autowired
    private IFetReceiptDiffService service;

    /**
     * 普通查询 
     */
    @RequestMapping(value = "/query")
    @ResponseBody
    public ResponseData query(FetReceiptDiff dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
    	return new ResponseData(service.getData(iRequest,dto,page,pageSize));
    }
    
    /**
     * 汇总查询 
     */
    @RequestMapping(value = "/summaryQuery")
    @ResponseBody
    public ResponseData summaryQuery(FetReceiptDiff dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
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
    	List<FetReceiptDiff> data = service.selectAll(requestContext);
        List<String> receiptPeriods = new ArrayList<>();
        for(FetReceiptDiff d:data){
        	if(!receiptPeriods.contains(d.getReceiptPeriod().trim())){
        		receiptPeriods.add(d.getReceiptPeriod().trim());
        	}
        }
        return new ResponseData(receiptPeriods);
    }

    @RequestMapping(value = "/updateVersion")
    @ResponseBody
    public ResponseData updateVersion(HttpServletRequest request,@RequestBody List<FetReceiptDiff> dto) throws CommonException{
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdateVesion(requestCtx, dto));
    }
    
    @RequestMapping(value = "/updateAllVersion")
    @ResponseBody
    public ResponseData updateAllVersion(HttpServletRequest request,Long paymentCompanyId,Long periodId) throws CommonException{
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.updateAllVersion(requestCtx,paymentCompanyId,periodId));
    }
    
    @RequestMapping(value = "/mergeOffset")
    @ResponseBody
    public ResponseData mergeOffset(HttpServletRequest request,@RequestBody List<FetReceiptDiff> dto) throws CommonException{
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.mergeOffset(requestCtx, dto));
    }
}