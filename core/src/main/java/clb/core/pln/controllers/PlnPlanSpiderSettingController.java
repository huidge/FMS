package clb.core.pln.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.pln.dto.PlnPlanSpiderSetting;
import clb.core.pln.service.IPlnPlanSpiderSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

    @Controller
    public class PlnPlanSpiderSettingController extends BaseController{

    @Autowired
    private IPlnPlanSpiderSettingService service;


    @RequestMapping(value = "/fms/pln/plan/spider/setting/query")
    @ResponseBody
    public ResponseData query(PlnPlanSpiderSetting dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.findAll(requestContext,dto,page,pageSize));
    }

    @RequestMapping(value = "/fms/pln/plan/spider/setting/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<PlnPlanSpiderSetting> dto,BindingResult result){
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/fms/pln/plan/spider/setting/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<PlnPlanSpiderSetting> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
    }