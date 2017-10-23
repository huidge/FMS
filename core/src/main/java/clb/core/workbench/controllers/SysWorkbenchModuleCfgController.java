package clb.core.workbench.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import clb.core.workbench.dto.SysWorkbenchModuleCfg;
import clb.core.workbench.service.ISysWorkbenchModuleCfgService;

    @Controller
    public class SysWorkbenchModuleCfgController extends BaseController{

    @Autowired
    private ISysWorkbenchModuleCfgService service;
    
    private String englishName;

    @RequestMapping(value = "/fms/sys/workbench/module/cfg/query")
    @ResponseBody
    public ResponseData query(SysWorkbenchModuleCfg dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectFunctionInfo(requestContext,dto,page,pageSize));
        //return new ResponseData(service.select(requestContext,dto,page,pageSize));
    }

    @RequestMapping(value = "/fms/sys/workbench/module/cfg/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<SysWorkbenchModuleCfg> dto){
        IRequest requestCtx = createRequestContext(request);
        SysWorkbenchModuleCfg sysWorkbenchModuleCfg = dto.get(0);
        
        englishName = sysWorkbenchModuleCfg.getEnglishName();
        String validateStr = "^[A-Za-z]+$";
        
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(englishName);
        if (m.find()) {
        	ResponseData responseData = new ResponseData();
			responseData.setSuccess(false);
			responseData.setMessage("功能英文名中包含有中文，保存失败");
			return responseData;
        }
        
        SysWorkbenchModuleCfg sysWorkbenchModuleCfg2 = service.updateByPrimaryKey(requestCtx, sysWorkbenchModuleCfg);
        List<SysWorkbenchModuleCfg> list = new ArrayList<>();
        list.add(sysWorkbenchModuleCfg2);
        return new ResponseData(list);
        //return new ResponseData(service.batchUpdate(requestCtx, dto));
    }
    
    @RequestMapping(value = "/fms/sys/workbench/module/cfg/add")
	@ResponseBody
	public ResponseData add(HttpServletRequest request, @RequestBody List<SysWorkbenchModuleCfg> dto) {
		IRequest requestCtx = createRequestContext(request);
		SysWorkbenchModuleCfg sysWorkbenchModuleCfg = dto.get(0);
		
		englishName = sysWorkbenchModuleCfg.getEnglishName();
        String validateStr = "^[A-Za-z]+$";
        
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(englishName);
        if (m.find()) {
        	ResponseData responseData = new ResponseData();
			responseData.setSuccess(false);
			responseData.setMessage("功能英文名中包含有中文，保存失败");
			return responseData;
        }
		
		SysWorkbenchModuleCfg ntnNotificationTemplate2 = service.insertSelective(requestCtx, sysWorkbenchModuleCfg);
		//下面的操作  没有意义  为了符合  内部定义的js的规范
		List<SysWorkbenchModuleCfg> list = new ArrayList<>();
		list.add(ntnNotificationTemplate2);
		return new ResponseData(list);
	}
    
    @RequestMapping(value = "/fms/sys/workbench/module/cfg/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<SysWorkbenchModuleCfg> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
    }