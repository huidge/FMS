package clb.core.forecast.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;

import clb.core.common.exceptions.CommonException;
import clb.core.common.utils.ImportUtil.ImportMessage;
import clb.core.forecast.dto.FetActualReceipt;
import clb.core.forecast.service.IFetActualReceiptService;
import clb.core.forecast.service.impl.FetActualReceiptServiceImpl;
import clb.core.supplier.exceptions.SpeException;

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

@Controller
@RequestMapping(value = "/fet/actual/receipt")
public class FetActualReceiptController extends BaseController{
	private static Logger log = LoggerFactory.getLogger(FetActualReceiptController.class);


    @Autowired
    private IFetActualReceiptService service;


    /**
     * 普通查询 
     */
    @RequestMapping(value = "/query")
    @ResponseBody
    public ResponseData query(FetActualReceipt dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize,HttpServletRequest request) {
    	IRequest iRequest = createRequestContext(request);
    	return new ResponseData(service.getData(iRequest,dto,page,pageSize));
    }
    

    /**
     * 更新&删除 
     */
    @RequestMapping(value = "/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<FetActualReceipt> dto) throws CommonException{
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.fetActualReceiptBatchUpdate(requestCtx, dto));
    }
    
    
    @RequestMapping(value = "/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<FetActualReceipt> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
}