package clb.core.whtcustom.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.whtcustom.dto.WhtCrKeyword;
import clb.core.whtcustom.dto.WhtCrKeywordRule;
import clb.core.whtcustom.dto.WhtCustomReply;
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
    public class WhtCrKeywordController extends BaseController{

    @Autowired
    private IWhtCrKeywordService service;


    @RequestMapping(value = "/fms/wht/cr/keyword/query")
    @ResponseBody
    public ResponseData query(WhtCrKeyword dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext,dto,page,pageSize));
    }
    
    @RequestMapping(value = "/fms/wht/cr/keyword/selectAll")
    @ResponseBody
    public ResponseData selectAll(WhtCrKeyword dto, HttpServletRequest request) {
    	IRequest requestContext = createRequestContext(request);
    	return new ResponseData(service.selectAll(requestContext,dto));
    }
    
    @RequestMapping(value = "/fms/wht/cr/keyword/selectRuleId")
	@ResponseBody 
	public ResponseData selectRuleId(HttpServletRequest request,@RequestParam("ruleId") String ruleId) {
		IRequest requestCtx = createRequestContext(request);
		WhtCrKeyword WhtCrKeyword = new WhtCrKeyword();
		WhtCrKeyword.setRuleId(Long.parseLong(ruleId));
		List<WhtCrKeyword> selectAll = service.selectAll(requestCtx,WhtCrKeyword);	
		return new ResponseData(selectAll);
	}
    
    @RequestMapping(value = "/fms/wht/cr/keyword/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<WhtCrKeyword> dto){
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }
    
    @RequestMapping(value = "/fms/wht/cr/keyword/deleteKeyword")
	@ResponseBody 
	public ResponseData deleteKeyword(HttpServletRequest request,@RequestParam("keywordId") String keywordId) {
		List<WhtCrKeyword> arrayList = new ArrayList<>();
		WhtCrKeyword whtCrKeyword2 = new WhtCrKeyword();
		whtCrKeyword2.setKeywordId(Long.parseLong(keywordId));
		arrayList.add(whtCrKeyword2);
		service.batchDelete(arrayList);
		return new ResponseData();
	}
    
    @RequestMapping(value = "/fms/wht/cr/keyword/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<WhtCrKeyword> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
    }