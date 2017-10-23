package clb.core.order.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.order.dto.OrdAfterFollow;
import clb.core.order.service.IOrdAfterFollowService;
import clb.core.sys.controllers.ClbBaseController;

import org.aspectj.lang.annotation.After;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

    @Controller
    public class OrdAfterFollowController extends ClbBaseController{

    @Autowired
    private IOrdAfterFollowService service;


    @RequestMapping(value = "/fms/ord/after/follow/query")
    @ResponseBody
    public ResponseData query(OrdAfterFollow dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext,dto,page,pageSize));
    }

    @RequestMapping(value = "/fms/ord/after/follow/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<OrdAfterFollow> dto){
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/fms/ord/after/follow/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,OrdAfterFollow dto){
    	IRequest requestCtx = createRequestContext(request);
    	ResponseData responseData = new ResponseData();
    	OrdAfterFollow key = service.selectByPrimaryKey(requestCtx, dto);
    	//查出最后一条记录
    	OrdAfterFollow afterFollow = service.queryLastOrdAfterFollowByAfterId(dto);
    	
    	if(afterFollow != null){
    		if(afterFollow.getAfterFollowId() != key.getAfterFollowId()){
    			responseData.setSuccess(false);
    			responseData.setMessage("已经被查看了,无法删除!");
    			return responseData;
    		}
    		if(requestCtx.getUserId() != key.getFollowUserId()){
    			responseData.setSuccess(false);
    			responseData.setMessage("不是您的记录,无法删除!");
    			return responseData;
    		}
    		service.deleteByPrimaryKey(dto);
    	}
        return responseData;
    }
    }