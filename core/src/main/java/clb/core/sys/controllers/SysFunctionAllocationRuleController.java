package clb.core.sys.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;

import clb.core.common.exceptions.CommonException;
import clb.core.sys.dto.SysFunctionAllocationRule;
import clb.core.sys.dto.SysFunctionHandleNotification;
import clb.core.sys.service.ISysFunctionAllocationRuleService;
import clb.core.sys.service.ISysFunctionHandleNotificationService;
/*****
 * @author tiansheng.ye
 * @Date 2017-05-25
 */
@Controller
@RequestMapping(value = "/fms/sys/function/rule")
public class SysFunctionAllocationRuleController extends BaseController{

    @Autowired
    private ISysFunctionAllocationRuleService sysFunctionAllocationRuleService;

    @Autowired
    private ISysFunctionHandleNotificationService sysFunctionHandleNotificationService;
    
    @RequestMapping(value = "/query")
    @ResponseBody
    public ResponseData query(SysFunctionAllocationRule dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        List<SysFunctionAllocationRule> data = sysFunctionAllocationRuleService.selectByDto(requestContext, dto, page, pageSize);
        return new ResponseData(data);
    }


    @RequestMapping(value = "/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<SysFunctionAllocationRule> dto, BindingResult result) throws CommonException{
    	getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData rd = new ResponseData(false);
            rd.setMessage(getErrorMessage(result, request));
            return rd;
        }
        IRequest requestCtx = createRequestContext(request);
        for(SysFunctionAllocationRule rule:dto){
        	if(rule.getRuleId()==null){
        		sysFunctionAllocationRuleService.insertSelective(requestCtx, rule);
        	}else{
        		sysFunctionAllocationRuleService.updateByPrimaryKey(requestCtx, rule);
        	}
        }
        return new ResponseData(dto);
    }

    @RequestMapping(value = "/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<SysFunctionAllocationRule> dto){
    	sysFunctionAllocationRuleService.batchDelete(dto);
        return new ResponseData();
    }
    
    
    @RequestMapping(value = "/handle/query")
    @ResponseBody
    public ResponseData handlequery(SysFunctionHandleNotification dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        List<SysFunctionHandleNotification> data = sysFunctionHandleNotificationService.select(requestContext, dto, page, pageSize);
        return new ResponseData(data);
    }


    @RequestMapping(value = "/handle/submit")
    @ResponseBody
    public ResponseData handleupdate(HttpServletRequest request,@RequestBody List<SysFunctionHandleNotification> dto) throws CommonException{
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(sysFunctionHandleNotificationService.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/handle/remove")
    @ResponseBody
    public ResponseData handledelete(HttpServletRequest request,@RequestBody List<SysFunctionHandleNotification> dto){
    	sysFunctionHandleNotificationService.batchDelete(dto);
        return new ResponseData();
    }
}