package clb.core.whtcustom.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;

import clb.core.course.dto.TrnCourse;
import clb.core.customer.dto.CtmCustomer;
import clb.core.customer.dto.CtmCustomerFamily;
import clb.core.wecorp.dto.WecorpResponseDTO;
import clb.core.whtcustom.dto.WhtCrKeyword;
import clb.core.whtcustom.dto.WhtCrKeywordRule;
import clb.core.whtcustom.dto.WhtCustomReply;
import clb.core.whtcustom.dto.WhtOfficialAccountCfg;
import clb.core.whtcustom.service.IWhtCrKeywordRuleService;
import clb.core.whtcustom.service.IWhtCrKeywordService;
import clb.core.whtcustom.service.IWhtCustomReplyService;

    @Controller
    public class WhtCustomReplyController extends BaseController{

    @Autowired
    private IWhtCustomReplyService service;
    
    @Autowired
    private IWhtCrKeywordService iWhtCrKeywordService;
    
    @Autowired
    private IWhtCrKeywordRuleService iWhtCrKeywordRuleService;


    @RequestMapping(value = "/fms/wht/custom/reply/query")
    @ResponseBody
    public ResponseData query(WhtCustomReply dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        //查询规则
        List<WhtCustomReply> WhtCustomReply = service.selectAllField(requestContext,dto,page,pageSize);
        /*if(WhtCustomReply.size()>0){
        	for (WhtCustomReply whtCustomReply2 : WhtCustomReply) {
        		WhtCrKeywordRule whtCrKeywordRule = new WhtCrKeywordRule();
        		whtCrKeywordRule.setAccountNum(dto.getAccountNum());
        		//查询关键字
        		List<WhtCrKeywordRule> selectAll = iWhtCrKeywordRuleService.selectAll(requestContext, whtCrKeywordRule);
        		if(selectAll.size()>0){
    			//if(selectAll.size()>0 && selectAll.size()<2){
        			for (WhtCrKeywordRule whtCrKeywordRule2 : selectAll) {
        				//规则
        				whtCustomReply2.setRuleId(whtCrKeywordRule2.getRuleId());
        				whtCustomReply2.setRuleName(whtCrKeywordRule2.getRuleName());
        				whtCustomReply2.setRulePictureId(whtCrKeywordRule2.getRulePictureId());
        				whtCustomReply2.setRuleVoiceId(whtCrKeywordRule2.getRuleVoiceId());
        				whtCustomReply2.setRuleVideoId(whtCrKeywordRule2.getRuleVideoId());
        				whtCustomReply2.setRulePictureText(whtCrKeywordRule2.getRulePictureText());
        				whtCustomReply2.setReplyAllFlag(whtCrKeywordRule2.getReplyAllFlag());
        				whtCustomReply2.setContent(whtCrKeywordRule2.getContent());
        				//关键字
        				WhtCrKeyword whtCrKeyword = new WhtCrKeyword();
        				whtCrKeyword.setRuleId(whtCrKeywordRule2.getRuleId());
        				List<WhtCrKeyword> selectAll2 = iWhtCrKeywordService.selectAll(requestContext, whtCrKeyword);
        				if(selectAll2.size()>0){
    						whtCustomReply2.setWhtCrKeywordList(selectAll2);
        				}
        			}
        		}*/
        		/*else if(selectAll.size()>1){
        			for (WhtCrKeywordRule whtCrKeywordRule2 : selectAll) {
        				WhtCustomReply whtCustomReply3 = new WhtCustomReply();
        				//规则
        				whtCustomReply3.setRuleId(whtCrKeywordRule2.getRuleId());
        				whtCustomReply3.setRuleName(whtCrKeywordRule2.getRuleName());
        				whtCustomReply3.setRulePictureId(whtCrKeywordRule2.getRulePictureId());
        				whtCustomReply3.setRuleVoiceId(whtCrKeywordRule2.getRuleVoiceId());
        				whtCustomReply3.setRuleVideoId(whtCrKeywordRule2.getRuleVideoId());
        				whtCustomReply3.setRulePictureText(whtCrKeywordRule2.getRulePictureText());
        				whtCustomReply3.setReplyAllFlag(whtCrKeywordRule2.getReplyAllFlag());
        				whtCustomReply3.setContent(whtCrKeywordRule2.getContent());
        				//关键字
        				WhtCrKeyword whtCrKeyword = new WhtCrKeyword();
        				whtCrKeyword.setRuleId(whtCrKeywordRule2.getRuleId());
        				List<WhtCrKeyword> selectAll2 = iWhtCrKeywordService.selectAll(requestContext, whtCrKeyword);
        				if(selectAll2.size()>0){
    						whtCustomReply3.setWhtCrKeywordList(selectAll2);
        				}
        				WhtCustomReply.add(whtCustomReply3);
        			}
        		}*/
        	/*}
        }else{
        	WhtCrKeywordRule whtCrKeywordRule = new WhtCrKeywordRule();
    		whtCrKeywordRule.setAccountNum(dto.getAccountNum());
    		List<WhtCrKeywordRule> selectAll = iWhtCrKeywordRuleService.selectAll(requestContext, whtCrKeywordRule);
    		if(selectAll.size()>0){
    			for (WhtCrKeywordRule whtCrKeywordRule2 : selectAll) {
    				//规则
    				WhtCustomReply whtCustomReply2 = new WhtCustomReply();
    				whtCustomReply2.setRuleId(whtCrKeywordRule2.getRuleId());
    				whtCustomReply2.setRuleName(whtCrKeywordRule2.getRuleName());
    				whtCustomReply2.setRulePictureId(whtCrKeywordRule2.getRulePictureId());
    				whtCustomReply2.setRuleVoiceId(whtCrKeywordRule2.getRuleVoiceId());
    				whtCustomReply2.setRuleVideoId(whtCrKeywordRule2.getRuleVideoId());
    				whtCustomReply2.setRulePictureText(whtCrKeywordRule2.getRulePictureText());
    				whtCustomReply2.setReplyAllFlag(whtCrKeywordRule2.getReplyAllFlag());
    				whtCustomReply2.setContent(whtCrKeywordRule2.getContent());
    				//关键字
    				WhtCrKeyword whtCrKeyword = new WhtCrKeyword();
    				whtCrKeyword.setRuleId(whtCrKeywordRule2.getRuleId());
    				List<WhtCrKeyword> selectAll2 = iWhtCrKeywordService.selectAll(requestContext, whtCrKeyword);
    				if(selectAll2.size()>0){
						whtCustomReply2.setWhtCrKeywordList(selectAll2);
						WhtCustomReply.add(whtCustomReply2);
    				}
    			}
    		}else{
    			WhtCustomReply whtCustomReply3 = new WhtCustomReply();
    			whtCustomReply3.setAccountNum(dto.getAccountNum());
    			WhtCustomReply.add(whtCustomReply3);
    		}
        }*/
        return new ResponseData(WhtCustomReply);
    }
    
    @RequestMapping(value = "/fms/wht/custom/reply/selectAll")
    @ResponseBody
    public ResponseData selectAll(WhtCustomReply dto, HttpServletRequest request) {
    	IRequest requestContext = createRequestContext(request);
    	return new ResponseData(service.selectAll(requestContext,dto));
    }
    
    /*@RequestMapping(value = "/fms/wht/custom/reply/subscribeReplyMsg")
    @ResponseBody
    public ResponseData subscribeReplyMsg(HttpServletRequest request, String appid) {
    	IRequest requestContext = createRequestContext(request);
    	WecorpResponseDTO subscribeReplyMsg = service.subscribeReplyMsg(requestContext, appid);
    	System.out.println(subscribeReplyMsg.toString());
    	return new ResponseData();
    }
    @RequestMapping(value = "/fms/wht/custom/reply/keywordkReplyMsg")
    @ResponseBody
    public ResponseData keywordkReplyMsg(HttpServletRequest request, String appid, String keyword) {
    	IRequest requestContext = createRequestContext(request);
    	WecorpResponseDTO keywordkReplyMsg = service.keywordkReplyMsg(requestContext, appid, keyword);
    	System.out.println(keywordkReplyMsg.toString());
    	return new ResponseData();
    }*/

    @RequestMapping(value = "/fms/wht/custom/reply/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<WhtCustomReply> dto){
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }
    
    @RequestMapping(value="/fms/wht/custom/reply/addReplay")
    @ResponseBody
    public ResponseData addReplay(HttpServletRequest request, @RequestBody List<WhtCustomReply> obj){
    	IRequest requestContext = createRequestContext(request);
    	WhtCustomReply whtCustomReply = obj.get(0);
    	WhtCustomReply whtCustomReply2 = new WhtCustomReply();
    	whtCustomReply2.setOriginalId(whtCustomReply.getOriginalId());
    	List<WhtCustomReply> selectAll = service.selectAll(requestContext,whtCustomReply2);
    	System.out.println(selectAll.size());
    	List<WhtCustomReply> list = new ArrayList<>();
    	if(selectAll.size()>0){
    		WhtCustomReply whtCustomReply3 = selectAll.get(0);
    		whtCustomReply.setReplyId(whtCustomReply3.getReplyId());
    		WhtCustomReply updateByPrimaryKeySelective = service.updateByPrimaryKeySelective(requestContext, whtCustomReply);
    		list.add(updateByPrimaryKeySelective);
    		return new ResponseData(list);
    	}else{
    		WhtCustomReply insertSelective = service.insertSelective(requestContext,whtCustomReply);
    		list.add(insertSelective);
    		return new ResponseData(list);
    	}
    	//return new ResponseData();
    }
    
    @RequestMapping(value="/fms/wht/custom/reply/addRule")
    @ResponseBody
    public ResponseData addRule(HttpServletRequest request, @RequestBody List<WhtCustomReply> obj){
        IRequest requestContext = createRequestContext(request);
        WhtCrKeyword whtCrKeyword = new WhtCrKeyword();
        WhtCrKeywordRule whtCrKeywordRule = new WhtCrKeywordRule();
        WhtCrKeywordRule insertSelective = null;
        WhtCustomReply whtCustomReply = obj.get(0);
        List<WhtCrKeywordRule> list = new ArrayList<>();
        //规则
        if(whtCustomReply.getWhtCrKeywordRuleList().size()>0){
    		for (WhtCrKeywordRule whtCrKeywordRule2 : whtCustomReply.getWhtCrKeywordRuleList()) {
    			whtCrKeywordRule.setOriginalId(whtCustomReply.getOriginalId());
    	        whtCrKeywordRule.setAccountNum(whtCustomReply.getAccountNum());
    			whtCrKeywordRule.setRuleName(whtCrKeywordRule2.getRuleName());
    			whtCrKeywordRule.setReplyAllFlag(whtCrKeywordRule2.getReplyAllFlag());
    			whtCrKeywordRule.setContent(whtCrKeywordRule2.getContent());
    			whtCrKeywordRule.setRulePictureId(whtCrKeywordRule2.getRulePictureId());
    			whtCrKeywordRule.setRuleVoiceId(whtCrKeywordRule2.getRuleVoiceId());
    			whtCrKeywordRule.setRuleVideoId(whtCrKeywordRule2.getRuleVideoId());
    			whtCrKeywordRule.setRulePictureText(whtCrKeywordRule2.getRulePictureText());
    			whtCrKeywordRule.setWhtRuleAttachContent(whtCrKeywordRule2.getWhtRuleAttachContent());
    			insertSelective = iWhtCrKeywordRuleService.insertSelective(requestContext,whtCrKeywordRule);
    			list.add(insertSelective);
    		}
    	}
        //关键字
        if(insertSelective.getRuleId() != null){
        	for (WhtCrKeyword whtCrKeyword2 : whtCustomReply.getWhtCrKeywordList()) {
        		whtCrKeyword.setRuleId(insertSelective.getRuleId());
        		whtCrKeyword.setKeywordValue(whtCrKeyword2.getKeywordValue());
        		whtCrKeyword.setMatchingFlag(whtCrKeyword2.getMatchingFlag());
        		iWhtCrKeywordService.insertSelective(requestContext,whtCrKeyword);
        	}
        }
        return new ResponseData(list);
    }
    
    @RequestMapping(value="/fms/wht/custom/reply/editRule")
    @ResponseBody
    public ResponseData editRule(HttpServletRequest request, @RequestBody List<WhtCustomReply> obj){
    	IRequest requestContext = createRequestContext(request);
    	WhtCrKeyword whtCrKeyword = new WhtCrKeyword();
    	WhtCrKeywordRule whtCrKeywordRule = new WhtCrKeywordRule();
    	WhtCustomReply whtCustomReply = obj.get(0);
    	List<WhtCrKeywordRule> list = new ArrayList<>();
    	//关键字
    	if(whtCustomReply.getWhtCrKeywordList().size()>0){
    		Long ruleId = whtCustomReply.getWhtCrKeywordRuleList().get(0).getRuleId();
			for (WhtCrKeyword whtCrKeyword2 : whtCustomReply.getWhtCrKeywordList()) {
				if(whtCrKeyword2.getKeywordId() != null){
					whtCrKeyword.setKeywordId(whtCrKeyword2.getKeywordId());
					whtCrKeyword.setKeywordValue(whtCrKeyword2.getKeywordValue());
					whtCrKeyword.setMatchingFlag(whtCrKeyword2.getMatchingFlag());
					iWhtCrKeywordService.updateByPrimaryKeySelective(requestContext,whtCrKeyword);
				}else{
					WhtCrKeyword whtCrKeywordNew = new WhtCrKeyword();
					whtCrKeywordNew.setRuleId(ruleId);
					whtCrKeywordNew.setKeywordValue(whtCrKeyword2.getKeywordValue());
					whtCrKeywordNew.setMatchingFlag(whtCrKeyword2.getMatchingFlag());
					iWhtCrKeywordService.insertSelective(requestContext,whtCrKeywordNew);
				}
    		}
    	}
    	if(whtCustomReply.getWhtCrKeywordRuleList().size()>0){
    		for (WhtCrKeywordRule whtCrKeywordRule2 : whtCustomReply.getWhtCrKeywordRuleList()) {
    			whtCrKeywordRule.setRuleId(whtCrKeywordRule2.getRuleId());
    			whtCrKeywordRule.setRuleName(whtCrKeywordRule2.getRuleName());
    			whtCrKeywordRule.setReplyAllFlag(whtCrKeywordRule2.getReplyAllFlag());
    			whtCrKeywordRule.setContent(whtCrKeywordRule2.getContent());
    			whtCrKeywordRule.setRulePictureId(whtCrKeywordRule2.getRulePictureId());
    			whtCrKeywordRule.setRuleVoiceId(whtCrKeywordRule2.getRuleVoiceId());
    			whtCrKeywordRule.setRuleVideoId(whtCrKeywordRule2.getRuleVideoId());
    			whtCrKeywordRule.setRulePictureText(whtCrKeywordRule2.getRulePictureText());
    			whtCrKeywordRule.setWhtRuleAttachContent(whtCrKeywordRule2.getWhtRuleAttachContent());
    			WhtCrKeywordRule updateByPrimaryKeySelective = iWhtCrKeywordRuleService.updateByPrimaryKeySelective(requestContext,whtCrKeywordRule);
    			list.add(updateByPrimaryKeySelective);
    		}
    	}
    	return new ResponseData(list);
    }
    
    @RequestMapping(value = "/fms/wht/custom/reply/deleteReply")
	@ResponseBody 
	public ResponseData deleteReply(HttpServletRequest request,@RequestParam("originalId") String originalId) {
    	IRequest requestCtx = createRequestContext(request);
		List<WhtCustomReply> arrayList2 = new ArrayList<>();
		WhtCustomReply WhtCustomReply = new WhtCustomReply();
		WhtCustomReply.setOriginalId(originalId);
		List<WhtCustomReply> selectAll = service.selectAll(requestCtx,WhtCustomReply);
		if(selectAll.size()>0){
			for (WhtCustomReply WhtCustomReply2 : selectAll) {
				arrayList2.add(WhtCustomReply2);
				service.batchDelete(arrayList2);
			}
		}
		return new ResponseData();
	}
    
    @RequestMapping(value = "/fms/wht/custom/reply/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<WhtCustomReply> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
    }