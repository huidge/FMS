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

import clb.core.whtcustom.dto.WhtMsgDetail;
import clb.core.whtcustom.service.IWhtMsgDetailService;

    @Controller
    public class WhtMsgDetailController extends BaseController{

    @Autowired
    private IWhtMsgDetailService service;


    @RequestMapping(value = "/fms/wht/msg/detail/query")
    @ResponseBody
    public ResponseData query(WhtMsgDetail dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectAllField(requestContext,dto,page,pageSize));
    }
    
    @RequestMapping(value = "/fms/wht/msg/detail/selectTemplateName")
    @ResponseBody
    public ResponseData selectTemplateName(WhtMsgDetail dto, HttpServletRequest request) {
    	IRequest requestContext = createRequestContext(request);
    	return new ResponseData(service.selectTemplateName(requestContext,dto));
    }

    @RequestMapping(value = "/fms/wht/msg/detail/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<WhtMsgDetail> dto){
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }
    
    @RequestMapping(value = "/fms/wht/msg/detail/updateTemplate")
    @ResponseBody
    public ResponseData updateTemplate(HttpServletRequest request,@RequestBody List<WhtMsgDetail> dto){
        IRequest requestCtx = createRequestContext(request);
        WhtMsgDetail WhtMsgDetail = dto.get(0);
        WhtMsgDetail WhtMsgDetail2 = service.updateByPrimaryKey(requestCtx, WhtMsgDetail);
        List<WhtMsgDetail> list = new ArrayList<>();
        list.add(WhtMsgDetail2);
        return new ResponseData(list);
    }
    
    @RequestMapping(value="/fms/wht/msg/detail/add")
    @ResponseBody
    public ResponseData add(HttpServletRequest request, @RequestBody List<WhtMsgDetail> obj){

        IRequest requestContext = createRequestContext(request);
        WhtMsgDetail WhtMsgDetail = obj.get(0);
        WhtMsgDetail insertSelective = service.insertSelective(requestContext,WhtMsgDetail);
		List<WhtMsgDetail> list = new ArrayList<>();
		list.add(insertSelective);
        return new ResponseData(list);
    }
    
    @RequestMapping(value = "/fms/wht/msg/detail/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<WhtMsgDetail> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
    }