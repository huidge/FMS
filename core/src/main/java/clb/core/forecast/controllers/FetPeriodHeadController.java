package clb.core.forecast.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;

import clb.core.common.utils.CommonUtil;
import clb.core.forecast.dto.FetPeriodHead;
import clb.core.forecast.service.IFetPeriodHeadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

    @Controller
    public class FetPeriodHeadController extends BaseController{

    @Autowired
    private IFetPeriodHeadService service;


    @RequestMapping(value = "/fms/fet/period/head/query")
    @ResponseBody
    public ResponseData query(FetPeriodHead dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectAllFiled(requestContext,dto,page,pageSize));
    }

    @RequestMapping(value = "/fms/fet/period/head/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<FetPeriodHead> dto){
        IRequest requestCtx = createRequestContext(request);
        ResponseData responseData = new ResponseData();
        try {
        	responseData.setRows(service.batchUpdate(requestCtx, dto));
		} catch (Exception e) {
			CommonUtil.printStackTraceToStr(e);
			responseData.setSuccess(false);
			responseData.setMessage("该关联方已有财务期间，请选择其他关联方!");
		}
        return responseData;
    }

    @RequestMapping(value = "/fms/fet/period/head/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<FetPeriodHead> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }

    @RequestMapping(value = "/fms/fet/period/head/copyPeriod" ,method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData copyPeriod(HttpServletRequest request,@RequestBody FetPeriodHead dto){
        IRequest requestCtx = createRequestContext(request);
        ResponseData responseData = new ResponseData();
        try {
            service.copyPeriod(requestCtx, dto);
        } catch (Exception e) {
            CommonUtil.printStackTraceToStr(e);
            responseData.setSuccess(false);
            responseData.setMessage("该关联方已有财务期间，请选择其他关联方!");
        }
        return responseData;
    }
    }