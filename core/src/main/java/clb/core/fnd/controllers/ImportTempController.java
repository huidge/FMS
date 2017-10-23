package clb.core.fnd.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hap.core.IRequest;
import com.hand.hap.excel.dto.ColumnInfo;
import com.hand.hap.excel.dto.ExportConfig;
import com.hand.hap.system.dto.ResponseData;

import clb.core.common.utils.CommonUtil;
import clb.core.fnd.dto.ImportTemp;
import clb.core.fnd.service.IImportTempService;
import clb.core.fnd.utils.Constants;
import clb.core.fnd.utils.OssHelper;
import clb.core.fnd.utils.ValidationTableException;
import clb.core.sys.controllers.ClbBaseController;
import net.sf.json.JSONObject;
/**
 * 
 * @name ImportTempController
 * @author 
 * @description 通用Excel上传
 * @version
 */

@Controller
public class ImportTempController extends ClbBaseController {
	
	@Autowired
	private IImportTempService importTempService;
	@Autowired
	private ObjectMapper objectMapper;
	/*@Autowired
	private OssHelper ossHelper;*/
	
	/**
	 * 获取错误消息
	 * 
	 * @param
	 * @param page
	 * @param pagesize
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "fnd/importTemp/queryerror", method = RequestMethod.POST)
	@ResponseBody
	public ResponseData selectErrorMessage(@ModelAttribute ImportTemp importTemp,
			@RequestParam(defaultValue = DEFAULT_PAGE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {

		IRequest requestContext = createRequestContext(request);
		return new ResponseData(
				importTempService.selectErrorMessage(importTemp.getImportBatchId(), page, pagesize, requestContext));
	}

	/**
	 * 获取导入数据
	 * 
	 * @param
	 * @param page
	 * @param pagesize
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "fnd/importTemp/queryImportData", method = RequestMethod.POST)
	@ResponseBody
	public ResponseData selectImportData(ImportTemp importTemp,
			@RequestParam(defaultValue = DEFAULT_PAGE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		return new ResponseData(
				importTempService.selectImportData(importTemp.getImportBatchId(), page, pagesize, requestContext));
	}
	
	/**
	 * @Description: 历史数据导入模板下载 
	 * @param request
	 * @param config
	 * @param httpServletResponse
	 * @throws Exception void
	 */
	@RequestMapping(value = "fnd/importTemp/downloadTemplate")
	public void downloadErrorMessage(HttpServletRequest request,@RequestParam String config, HttpServletResponse httpServletResponse) throws Exception {
		IRequest requestContext = createRequestContext(request);
		try {
			JavaType type = objectMapper.getTypeFactory().constructParametrizedType(ExportConfig.class,
					ExportConfig.class,Map.class, ColumnInfo.class);
			ExportConfig<Map<String,String>, ColumnInfo> exportConfig = objectMapper.readValue(config, type);
			importTempService.downloadExcel(exportConfig, request, httpServletResponse, requestContext);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Title: downloadErrorMessage 
	 * @Description: 下载错误信息 
	 * @param 
	 * @return ResponseData
	 * @author xiang.ding@hand-china.com
	 */
	@RequestMapping(value = "fnd/importTemp/downloadErrorMessage")
	public void downloadErrorMessage(HttpServletRequest request, Long importBatchId,
			HttpServletResponse httpServletResponse) throws Exception {
		IRequest requestContext = createRequestContext(request);
		List<ImportTemp> importTemps = importTempService.selectImportData(importBatchId, 1, Integer.MAX_VALUE, requestContext);
		try {
			importTempService.downloadErrorExcel(importTemps, request, httpServletResponse, requestContext);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @Description: 从excel文件中导入数据到importTemp临时表。返回importBatchId批次号。
	 * @param request
	 * @return ResponseData
	 * @throws FileUploadException 
	 */
	@RequestMapping(value = "fnd/importTemp/importFromExcel", method = RequestMethod.POST)
	public ResponseData importFromExcel(HttpServletRequest request, @RequestParam MultipartFile[] file) throws FileUploadException {
		IRequest requestContext = createRequestContext(request);

		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		//不是文件上传
		if (!isMultipart) {
			ResponseData responseData = new ResponseData(false);
			String errorMessage = this.getMessageSource().getMessage(Constants.NO_FILE, null,RequestContextUtils.getLocale(request));
			responseData.setMessage(errorMessage);
			return responseData;
		}

		MultipartFile fileItem = (new OssHelper()).initFileItem(file);
		
		List<ImportTemp> importTempList = null;

		List<String> importBatchIdList = new ArrayList<String>();

		ResponseData rd = null;
		try {
			importTempList = importTempService.batchCreateFromExcel(requestContext, fileItem);
		} catch (Exception e) {
			e.printStackTrace();
			String code = this.getMessageSource().getMessage((null != e.getMessage() && e.getMessage().equals(Constants.SERIAL_NUMBER_ERROR)) ?
				e.getMessage() : Constants.UPLOAD_ERROR, null,RequestContextUtils.getLocale(request));
			rd = new ResponseData(false);
			rd.setMessage(code);
			return rd;
		}
		importBatchIdList.add(importTempList.get(0).getImportBatchId().toString());
		
		rd = new ResponseData(importBatchIdList);
		return rd;
	}
	
	/**
	 * @Description: 导入数据 
	 * @param importBatchId
	 * @param
	 * @param
	 * @param request
	 * @return
	 * @throws Exception ResponseData
	 */
	@RequestMapping(value = "fnd/importTemp/importData", method = RequestMethod.POST)
	public ResponseData importData(@RequestParam Long importBatchId, @RequestParam String className, HttpServletRequest request) throws Exception {
		IRequest requestContext = createRequestContext(request);
		Locale locale = RequestContextUtils.getLocale(request);
		//清空
		request.removeAttribute(importBatchId+"");
		//另起线程
		OtherThread threa=new OtherThread(request,requestContext,locale,importBatchId,className);
		new Thread(threa).start();

		return new ResponseData(true);

	}
	/*****
	 * 定时监控返回值
	 * @param importBatchId
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "fnd/importTemp/getImportData", method = RequestMethod.POST)
	public JSONObject importData(@RequestParam Long importBatchId, HttpServletRequest request) throws Exception {
		JSONObject responseData=getSessionBean("import_"+importBatchId);
		if(responseData==null){
			responseData =new JSONObject();
			responseData.put("success", true);
			responseData.put("code", "-1");
			return responseData;
		}else{
			return responseData;
		}

	}
	
	class OtherThread implements Runnable{
		private HttpServletRequest request;
		private IRequest requestContext ;
		private Long importBatchId;
		private String className;
		private Locale locale;
		
		public OtherThread(HttpServletRequest request,IRequest requestContext,Locale locale,Long importBatchId,String className){
			this.request=request;
			this.requestContext=requestContext;
			this.locale=locale;
			this.importBatchId=importBatchId;
			this.className=className;
		}
		
		@Override
		public void run() {
			JSONObject responseData =new JSONObject();
			responseData.put("success", true);
			responseData.put("code", "0");
			try {
				importTempService.importData(requestContext, importBatchId, className);
				Long errorCount = importTempService.selectErrorCount(importBatchId);

				if (errorCount != 0) {
					responseData.put("success", false);
					responseData.put("code", "E");//导入验证未通过
					String errorMessage =  getMessageSource().getMessage(Constants.EXIST_ERROR, null, locale)
							+ String.valueOf(errorCount);
					responseData.put("message", errorMessage+"");
				}
			} catch (ValidationTableException e) {
				responseData.put("success", false);
				responseData.put("code", "N");
				String errorMessage = "";
				if(null != e.getCode() && !e.getCode().equals("")){
					errorMessage = getMessageSource().getMessage( e.getCode(), null, locale);
				}else{
					errorMessage = getMessageSource().getMessage( Constants.IMPORT_EXCEPTION, null, locale);
				}
				responseData.put("message", errorMessage+"");
			} catch (Exception e) {
				responseData.put("success", false);
				responseData.put("code", "N");
				String errorMessage = getMessageSource().getMessage( Constants.IMPORT_EXCEPTION, null, locale);
				responseData.put("message", errorMessage+"");
				CommonUtil.printStackTraceToStr(e);
			} 
			saveSessionRedis("import_"+importBatchId, responseData);
		}
		
	}
}
