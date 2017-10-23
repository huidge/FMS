package clb.core.whtcustom.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;

import clb.core.course.dto.TrnCourse;
import clb.core.whtcustom.dto.WhtCrKeyword;
import clb.core.whtcustom.dto.WhtCrKeywordRule;
import clb.core.whtcustom.service.IWhtCrKeywordRuleService;
import clb.core.whtcustom.service.IWhtCrKeywordService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

    @Controller
    public class WhtCrKeywordRuleController extends BaseController{

    @Autowired
    private IWhtCrKeywordRuleService service;
    
    @Autowired
    private IWhtCrKeywordService keywordService;


    @RequestMapping(value = "/fms/wht/cr/keyword/rule/query")
    @ResponseBody
    public ResponseData query(WhtCrKeywordRule dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext,dto,page,pageSize));
    }
    
    @RequestMapping(value = "/fms/wht/cr/keyword/rule/selectAll")
    @ResponseBody
    public ResponseData selectAll(WhtCrKeywordRule dto,HttpServletRequest request) {
    	IRequest requestContext = createRequestContext(request);
    	return new ResponseData(service.selectAll(requestContext,dto));
    }

    @RequestMapping(value = "/fms/wht/cr/keyword/rule/selectRuleId")
	@ResponseBody 
	public ResponseData selectRuleId(HttpServletRequest request,@RequestParam("ruleId") String ruleId) {
		IRequest requestCtx = createRequestContext(request);
		WhtCrKeywordRule whtCrKeywordRule = new WhtCrKeywordRule();
		whtCrKeywordRule.setRuleId(Long.parseLong(ruleId));
		List<WhtCrKeywordRule> selectAll = service.selectAll(requestCtx,whtCrKeywordRule);	
		return new ResponseData(selectAll);
	}
    
    @RequestMapping(value = "/fms/wht/cr/keyword/rule/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<WhtCrKeywordRule> dto){
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }
    
    @RequestMapping(value = "/fms/wht/cr/keyword/rule/deleteRule")
	@ResponseBody 
	public ResponseData deleteRule(HttpServletRequest request,@RequestParam("ruleId") String ruleId) {
    	IRequest requestCtx = createRequestContext(request);
		List<WhtCrKeywordRule> arrayList = new ArrayList<>();
		List<WhtCrKeyword> arrayList2 = new ArrayList<>();
		WhtCrKeywordRule WhtCrKeywordRule2 = new WhtCrKeywordRule();
		WhtCrKeywordRule2.setRuleId(Long.parseLong(ruleId));
		arrayList.add(WhtCrKeywordRule2);
		service.batchDelete(arrayList);
		
		WhtCrKeyword whtCrKeyword = new WhtCrKeyword();
		whtCrKeyword.setRuleId(Long.parseLong(ruleId));
		List<WhtCrKeyword> selectAll = keywordService.selectAll(requestCtx,whtCrKeyword);
		if(selectAll.size()>0){
			for (WhtCrKeyword whtCrKeyword2 : selectAll) {
				arrayList2.add(whtCrKeyword2);
				keywordService.batchDelete(arrayList2);
			}
		}
		return new ResponseData();
	}
    
    @RequestMapping(value = "/fms/wht/cr/keyword/rule/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<WhtCrKeywordRule> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
    }