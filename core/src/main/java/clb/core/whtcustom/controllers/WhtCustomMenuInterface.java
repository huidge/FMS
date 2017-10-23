package clb.core.whtcustom.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;

import clb.core.sys.controllers.ClbBaseController;
import clb.core.wecorp.dto.WecorpResponseDTO;
import clb.core.wecorp.service.IWecorpAccountMenuService;
import clb.core.whtcustom.dto.WhtCustomMenu;
import clb.core.whtcustom.service.IWhtCustomMenuService;

    @Controller
    public class WhtCustomMenuInterface extends ClbBaseController{

    @Autowired
    private IWhtCustomMenuService service;
    
    @Autowired
    private IWecorpAccountMenuService wecorpAccountMenuService;

    
    @RequestMapping(value = "/api/public/fms/wht/custom/menu/menuClick")
    @ResponseBody
    public ResponseData menuClick(HttpServletRequest request,String appId , String key) throws Exception {
    	IRequest requestContext = createRequestContext(request);
    	WecorpResponseDTO subscribeReplyMsg = service.menuClick(appId,key);
    	System.out.println(subscribeReplyMsg.toString());
    	return new ResponseData();
    }
    
}
    
    
