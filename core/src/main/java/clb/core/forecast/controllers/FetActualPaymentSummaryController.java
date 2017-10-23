package clb.core.forecast.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;

import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;

import clb.core.common.exceptions.CommonException;
import clb.core.common.utils.ImportUtil.ImportMessage;
import clb.core.forecast.dto.FetActualPaymentSummary;
import clb.core.forecast.service.IFetActualPaymentSummaryService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

@RequestMapping(value = "/fet/actual/payment/summary")
@Controller
public class FetActualPaymentSummaryController extends BaseController{

	private static Logger log = LoggerFactory.getLogger(FetActualPaymentSummaryController.class);

	
    @Autowired
    private IFetActualPaymentSummaryService service;


    @RequestMapping(value = "/query")
    @ResponseBody
    public ResponseData query(FetActualPaymentSummary dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
    	return new ResponseData(service.getData(iRequest,dto,page,pageSize));
    }

    @RequestMapping(value = "/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<FetActualPaymentSummary> dtos,BindingResult result){
    	getValidator().validate(dtos,result);
        if (result.hasErrors()) {
            ResponseData rs = new ResponseData(false);
            rs.setMessage(getErrorMessage(result, request));
            return rs;
        }
    	IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.actualPaymentSummaryBatchUpdate(requestCtx, dtos));
    }
    
    /**
     * 查询所有
     */
    @RequestMapping(value = "/getAll")
    @ResponseBody
    public ResponseData getAll(HttpServletRequest request) {
    	IRequest requestContext = createRequestContext(request);
    	List<FetActualPaymentSummary> data = service.selectAll(requestContext);
        List<String> receiptPeriods = new ArrayList<>();
        for(FetActualPaymentSummary d:data){
        	if(!receiptPeriods.contains(d.getPaymentPeriod().trim())){
        		receiptPeriods.add(d.getPaymentPeriod().trim());
        	}
        }
        return new ResponseData(receiptPeriods);
    }

    @RequestMapping(value = "/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<FetActualPaymentSummary> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
    
    /**
     * 加载Excel数据并校验 
     */
    @RequestMapping(value = "/loadExcel")
    @ResponseBody
    public ResponseData loadExcel(HttpServletRequest request,MultipartFile files){
    	ResponseData responseData = new ResponseData();
    	IRequest iRequest = this.createRequestContext(request);
    	try{
    		List<ImportMessage> data = service.loadExcel(iRequest,request,files);
    		responseData.setSuccess(true);         	
    		responseData.setRows(data);         	
    		responseData.setTotal((long) data.size());
        }catch(Exception e){
        	log.error("导入失败：",e);
        	responseData.setSuccess(false); 
        	if(e instanceof RuntimeException){
        		RuntimeException runtimeException = (RuntimeException)e;
        		Throwable thr = runtimeException.getCause();
        		if(thr instanceof CommonException){
        			CommonException commonException = (CommonException)thr;
        			responseData.setMessage(commonException.getDescriptionKey());
        		}else{
        			responseData.setMessage("导入失败，请联系管理员!");
        		}
        	}else if(e instanceof CommonException){
        		responseData.setMessage(((CommonException)e).getDescriptionKey());
        	}else{
        		responseData.setMessage("导入失败，请联系管理员!");
        	}
        }
    	return responseData;
    }
}