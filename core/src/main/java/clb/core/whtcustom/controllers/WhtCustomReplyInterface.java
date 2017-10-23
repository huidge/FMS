package clb.core.whtcustom.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;

import clb.core.sys.controllers.ClbBaseController;
import clb.core.wecorp.dto.WecorpResponseDTO;
import clb.core.whtcustom.service.IWhtCrKeywordRuleService;
import clb.core.whtcustom.service.IWhtCrKeywordService;
import clb.core.whtcustom.service.IWhtCustomReplyService;

    @Controller
    public class WhtCustomReplyInterface extends ClbBaseController{

    @Autowired
    private IWhtCustomReplyService service;
    
    @Autowired
    private IWhtCrKeywordService iWhtCrKeywordService;
    
    @Autowired
    private IWhtCrKeywordRuleService iWhtCrKeywordRuleService;


    
    @RequestMapping(value = "/api/public/fms/wht/custom/reply/subscribeReplyMsg")
    @ResponseBody
    public ResponseData subscribeReplyMsg(HttpServletRequest request, String appid) throws Exception {
    	IRequest requestContext = createRequestContext(request);
    	appid = "wx8a7d8fead458d831";
    	WecorpResponseDTO subscribeReplyMsg = service.subscribeReplyMsg( appid);
    	System.out.println(subscribeReplyMsg.toString());
    	return new ResponseData();
    }
    @RequestMapping(value = "/api/public/fms/wht/custom/reply/keywordkReplyMsg")
    @ResponseBody
    public ResponseData keywordkReplyMsg(HttpServletRequest request, String appid, String keyword) throws Exception {
    	IRequest requestContext = createRequestContext(request);
    	appid = "wx8a7d8fead458d831";
    	List<WecorpResponseDTO> keywordkReplyMsg = service.keywordkReplyMsg( appid, keyword);
    	System.out.println(keywordkReplyMsg.toString());
    	return new ResponseData();
    }

    
    }