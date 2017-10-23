package clb.core.system.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;

import clb.core.common.dto.ImportResponse;

@Controller
public class ClbImportController extends BaseController{

	public static Map<String, Object> importDate=new HashMap<String, Object>();;
	
	@RequestMapping(value="/clb/getVerificationData")
	@ResponseBody
	public ResponseData getVerificationData(HttpServletRequest request,String importNum) throws IOException{
		Map<String, Object> importDate=ClbImportController.importDate;
		if(importDate.get(importNum)!=null){
			Map<String, Object> map=(Map<String, Object>) importDate.get(importNum);
			ResponseData responseDate=(ResponseData) map.get("responseDate");
			return responseDate;
		}else{
			return new ResponseData(false);
		}
	}
    
    @RequestMapping(value="/clb/getImportResult")
	@ResponseBody
	public ResponseData getImportResult(HttpServletRequest request,String importNum) throws IOException{
    	Map<String, Object> importDate=ClbImportController.importDate;
		if(importDate.get(importNum)!=null){
			ResponseData responseData= (ResponseData)importDate.get(importNum);
			importDate.remove(importNum);
			return responseData;
		}else{
			return new ResponseData(false);
		}
	}

    @RequestMapping(value="/clb/removeImportResult")
	@ResponseBody
	public ResponseData removeImportResult(HttpServletRequest request,String importNum) throws IOException{
    	Map<String, Object> importDate=ClbImportController.importDate;
    	importDate.remove(importNum);
		return new ResponseData(true);
	}
    
	public Map<String, Object> getImportDate() {
		return importDate;
	}

	public void setImportDate(Map<String, Object> importDate) {
		this.importDate = importDate;
	}
    
    
}
