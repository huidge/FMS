package clb.core.workbench.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.exception.BaseException;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;

import clb.core.system.dto.ClbUser;
import clb.core.workbench.dto.SysWorkbenchRole;
import clb.core.workbench.service.ISysWorkbenchRoleService;

    @Controller
    public class SysWorkbenchRoleController extends BaseController{

    @Autowired
    private ISysWorkbenchRoleService service;

    private List<SysWorkbenchRole> lists;
    
    @RequestMapping(value = "/fms/sys/workbench/role/query")
    @ResponseBody
    public ResponseData query(SysWorkbenchRole dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        lists = service.selectAllFields(requestContext, dto, page, pageSize);
        return new ResponseData(lists);
        //return new ResponseData(service.select(requestContext,dto,page,pageSize));
    }

    @RequestMapping(value = "/fms/sys/workbench/role/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<SysWorkbenchRole> dto){
        IRequest requestCtx = createRequestContext(request);
        
        List<ClbUser> arrayList = new ArrayList<>();
        for(SysWorkbenchRole sysWorkbenchRole : dto){
        	if(sysWorkbenchRole.getRoleId() != null || "".equals(sysWorkbenchRole.getRoleId())){
        		for(SysWorkbenchRole user : lists){
        			if(user.getRoleId().equals(sysWorkbenchRole.getRoleId()) ){
        				ResponseData responseData = new ResponseData();
        		            responseData.setMessage("用户角色不能重复！");
        		            responseData.setSuccess(false);
        		            return responseData;
        			}
        		}
        	}
        }
        
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }
    
    @ResponseBody
    @RequestMapping(value="/fms/sys/workbench/role/add")
    public ResponseData add(HttpServletRequest request, @RequestBody SysWorkbenchRole obj, BindingResult result) throws BaseException {

        getValidator().validate(obj, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestContext = createRequestContext(request);
        String cfgId = request.getParameter("cfgId");
        if(cfgId != null && "".equals(cfgId)){
        	obj.setCfgId(Long.valueOf(cfgId));
        }
        service.insertSelective(requestContext, obj);
        return new ResponseData();
    }
    
    @RequestMapping(value = "/fms/sys/workbench/role/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<SysWorkbenchRole> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
    }