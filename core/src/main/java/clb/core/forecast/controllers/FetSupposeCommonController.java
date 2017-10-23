package clb.core.forecast.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;

import clb.core.common.exceptions.CommonException;
import clb.core.common.utils.SpringConfigTool;
import clb.core.forecast.cache.ImportMessageCache;
import clb.core.forecast.dto.FetSupposeCommon;
import clb.core.forecast.dto.FetSupposeMessage;
import clb.core.forecast.dto.FetSupposeUpdateVersion;
import clb.core.forecast.service.IFetSupposeCommonService;

@Controller
@RequestMapping(value = "/fet/suppose/common")
public class FetSupposeCommonController extends BaseController{
	
	private static Logger log = LoggerFactory.getLogger(FetSupposeCommonController.class);
   

    @Autowired
	private ImportMessageCache importMessageCache;

    /**
     * 批量更新版本
     * @throws CommonException 
     */
    @RequestMapping(value = "/batchUpdateVersion")
    @ResponseBody
    public ResponseData batchUpdateVersion(@RequestBody List<FetSupposeCommon> supposeCommons,HttpServletRequest request) throws CommonException {
    	IRequest iRequest = createRequestContext(request);
    	IFetSupposeCommonService service = (IFetSupposeCommonService) SpringConfigTool.getBean("FetSupposeCommonServiceImpl");
    	List<FetSupposeMessage> errorMessage = service.batchUpdateVersion(iRequest,supposeCommons);
    	ResponseData responseData = new ResponseData();
    	responseData.setSuccess(true);
    	responseData.setRows(errorMessage);
    	responseData.setMessage("更新成功，本次更新"+iRequest.getAttribute("updates")+"条数据");
        return responseData;
    }
    
    /**
     * 更新全部版本 
     * @throws CommonException 
     */
    @RequestMapping(value = "/updateAllVersion")
    @ResponseBody
    public ResponseData UpdateAllVersion(@RequestBody FetSupposeUpdateVersion param,HttpServletRequest request) throws CommonException {
    	IRequest iRequest = createRequestContext(request);
    	IFetSupposeCommonService service = (IFetSupposeCommonService) SpringConfigTool.getBean("FetSupposeCommonServiceImpl");
    	List<FetSupposeMessage> errorMessage = service.UpdateAllVersion(iRequest,param);
    	ResponseData responseData = new ResponseData();
    	responseData.setSuccess(true);
    	responseData.setRows(errorMessage);
    	responseData.setMessage("更新成功，本次更新"+iRequest.getAttribute("updates")+"条数据");
        return responseData;
    }
    
     /**
     * 删除Redis缓存 
     */
    @RequestMapping(value = "/removeRedisData")
    @ResponseBody
    public void delete(String key){
    	importMessageCache.remove(key);
    }
    
    
}