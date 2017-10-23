package clb.core.pln.controllers;

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

import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.attachment.service.ISysFileService;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;

import clb.core.pln.dto.PlnPlanLibrary;
import clb.core.pln.service.IPlnPlanLibraryService;

@Controller
public class PlnPlanLibraryController extends BaseController{

    @Autowired
    private IPlnPlanLibraryService service;
    
    @Autowired
    private ISysFileService fileService;

    @RequestMapping(value = "/fms/pln/plan/library/query")
    @ResponseBody
    public ResponseData query(PlnPlanLibrary dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        if(dto.getAge() != null){
            if(dto.getAge()==115){
            	dto.setAge(Long.valueOf(15));
            }
        }
        dto.setOrderBy2("按照id倒序");
        List<PlnPlanLibrary> PlnPlanLibraryList = service.selectLibraryInfo(requestContext,dto,page,pageSize);
        for (PlnPlanLibrary plnPlanLibrary : PlnPlanLibraryList) {
        	SysFile sysFile = fileService.selectByPrimaryKey(requestContext, plnPlanLibrary.getFileId());
        	if(sysFile != null){
        		plnPlanLibrary.set_token(sysFile.get_token());
        	}
		}
        return new ResponseData(PlnPlanLibraryList);
    }

    @RequestMapping(value = "/fms/pln/plan/library/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<PlnPlanLibrary> dto,BindingResult result){
    	getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
    	IRequest requestCtx = createRequestContext(request);
    	
    	for (PlnPlanLibrary plnPlanLibrary : dto) {
    		PlnPlanLibrary plnPlanLibraryQery = new PlnPlanLibrary();
    		plnPlanLibraryQery = service.selectLibraryByConditionForBack(plnPlanLibrary);
    		if("add".equals(plnPlanLibrary.get__status())){
    			if(plnPlanLibraryQery!=null){
        			ResponseData responseData = new ResponseData(false);
    	            responseData.setMessage("计划书库信息已存在！");
    	            return responseData;
        		}
    		}else{
    			if(plnPlanLibraryQery!=null){
    			   if(plnPlanLibraryQery.getLibraryId().longValue() != plnPlanLibrary.getLibraryId().longValue()){
    				  ResponseData responseData = new ResponseData(false);
       	              responseData.setMessage("计划书库信息已存在！");
       	              return responseData;
    			   }
    			}
    		}
    		
		}
    	
    	for (PlnPlanLibrary plnPlanLibrary : dto) {
			if("add".equals(plnPlanLibrary.get__status())){
				if(plnPlanLibrary.getFileId() == null){
					ResponseData responseData = new ResponseData(false);
		            responseData.setMessage("新建记录文件必须上传！");
		            return responseData;
				}
				plnPlanLibrary.setObjectVersionNumber(1L);
				service.insert(requestCtx, plnPlanLibrary);
			}
			if("update".equals(plnPlanLibrary.get__status())){
				service.updateByPrimaryKey(requestCtx, plnPlanLibrary);
			}
		}
        return new ResponseData(dto);
    }

    @RequestMapping(value = "/fms/pln/plan/library/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<PlnPlanLibrary> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
    
    
    @RequestMapping(value = "/fms/pln/plan/library/queryCurrencyByItem" )
    @ResponseBody
    public ResponseData queryCurrencyByItem(HttpServletRequest request, PlnPlanLibrary dto){
        return new ResponseData(service.selectListByCodeAndItem(dto.getCode(), dto.getItemId()));
    }
    
    
    @RequestMapping(value = "/fms/pln/plan/library/queryPaymethodByItem" )
    @ResponseBody
    public ResponseData queryPaymethodByItem(HttpServletRequest request, PlnPlanLibrary dto){
    	IRequest requestCtx = createRequestContext(request);
    	return new ResponseData(service.queryPaymethodByItem(requestCtx,dto));
    }
    
    @RequestMapping(value = "/fms/pln/plan/library/querySecurityLevelByItem" )
    @ResponseBody
    public ResponseData querySecurityLevelByItem(HttpServletRequest request, PlnPlanLibrary dto){
    	IRequest requestCtx = createRequestContext(request);
    	return new ResponseData(service.querySecurityLevelByItem(dto.getItemId()));
    }
    
    @RequestMapping(value = "/fms/pln/plan/library/querySecurityAreaByItem" )
    @ResponseBody
    public ResponseData querySecurityAreaByItem(HttpServletRequest request, PlnPlanLibrary dto){
    	IRequest requestCtx = createRequestContext(request);
    	return new ResponseData(service.querySecurityAreaByItem(dto.getItemId(),dto.getSecurityLevel()));
    }
    
    /**
     * 计划书库导入程序
     * @param request
     * @param dto
     * @return
     */
    @RequestMapping(value = "/fms/pln/plan/library/importPlanInfo" )
    @ResponseBody
    public ResponseData importPlanInfo(HttpServletRequest request, PlnPlanLibrary dto){
    	IRequest requestCtx = createRequestContext(request);
    	service.importPlanInfo(requestCtx, dto);
    	return new ResponseData();
    }
   
}