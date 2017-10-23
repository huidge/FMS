package clb.core.production.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.attachment.service.ISysFileService;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;

import clb.core.order.dto.SysOrderCfgField;
import clb.core.production.dto.PrdItemFile;
import clb.core.production.service.IPrdItemFileService;
import clb.core.question.dto.QaGuidelineFile;

@Controller
@RequestMapping({ "/prd/item/file"})
public class PrdItemFileController extends BaseController{

	@Autowired
	private IPrdItemFileService prdItemFileService;
	
	@Autowired
    private ISysFileService fileService;
	
	
	@RequestMapping(value = "/query")
	@ResponseBody
	public ResponseData query(PrdItemFile dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		String itemId = request.getParameter("itemId");
		String type = request.getParameter("type");
		dto.setItemId(Long.valueOf(itemId));
        dto.setType(type);
		List<PrdItemFile> prdItemFileList= prdItemFileService.selectByItemId(requestContext, dto, page, pageSize);
		for (PrdItemFile prdItemFile : prdItemFileList) {
			Long fileId = prdItemFile.getFileId();
			SysFile sysFile = fileService.selectByPrimaryKey(requestContext, fileId);
			prdItemFile.set_token(sysFile.get_token());
			prdItemFile.setUploadDate(sysFile.getLastUpdateDate());
			prdItemFile.setAttribute1(prdItemFile.getLineId().toString());
		}
		return new ResponseData(prdItemFileList);
	}
	
	
	
	
	
	/**
	 * 保存上传文件的ID
	 * @param request
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "/submit", method=RequestMethod.POST)
	@ResponseBody
	public ResponseData saveFileIds(HttpServletRequest request,@RequestBody List<PrdItemFile> dto) {
		  IRequest requestCtx = createRequestContext(request);
		  String itemId = request.getParameter("itemId");
		  String type = request.getParameter("type");
		  for (PrdItemFile prdItemFile : dto) {
			  prdItemFile.setItemId(Long.valueOf(itemId));
			  prdItemFile.setType(type);
			  if(prdItemFile.getAttribute1()!=null && prdItemFile.getAttribute1() !=""){
				  prdItemFile.setLineId(Long.valueOf(prdItemFile.getAttribute1()));
			  }
		  }
	      prdItemFileService.batchUpdate(requestCtx, dto);
		  return new ResponseData(dto);
	}
	
	
	@RequestMapping(value = "/queryCompany")
	@ResponseBody
	public ResponseData queryCompany(PrdItemFile dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		String itemId = request.getParameter("itemId");
		String type = request.getParameter("type");
		dto.setItemId(Long.valueOf(itemId));
        dto.setType(type);
		List<PrdItemFile> prdItemFileList= prdItemFileService.selectByItemId(requestContext,dto, page, pageSize);
		
		for (PrdItemFile prdItemFile : prdItemFileList) {
			Long fileId = prdItemFile.getFileId();
			SysFile sysFile = fileService.selectByPrimaryKey(requestContext, fileId);
			prdItemFile.set_token(sysFile.get_token());
			prdItemFile.setAttribute1(prdItemFile.getLineId().toString());
			prdItemFile.setUploadDate(sysFile.getLastUpdateDate());
		}
		
		return new ResponseData(prdItemFileList);
	}
	
	
	
	
	
	/**
	 * 保存上传文件的ID
	 * @param request
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "/submitCompany", method=RequestMethod.POST)
	@ResponseBody
	public ResponseData submitCompany(HttpServletRequest request,@RequestBody List<PrdItemFile> dto) {
		  IRequest requestCtx = createRequestContext(request);
		  String itemId = request.getParameter("itemId");
		  String type = request.getParameter("type");
		  for (PrdItemFile prdItemFile : dto) {
			  prdItemFile.setItemId(Long.valueOf(itemId));
			  prdItemFile.setType(type);
			  if(prdItemFile.getAttribute1()!=null && prdItemFile.getAttribute1() !=""){
				  prdItemFile.setLineId(Long.valueOf(prdItemFile.getAttribute1()));
			  }
		  }
		  prdItemFileService.batchUpdate(requestCtx, dto);
		  return new ResponseData(dto);
	}
	
	 @RequestMapping(value = "/removeCompany", method=RequestMethod.POST)
	 @ResponseBody
	 public ResponseData delete(HttpServletRequest request,@RequestBody List<PrdItemFile> dto){
		 for (PrdItemFile prdItemFile : dto) {
			 prdItemFile.setLineId(Long.valueOf(prdItemFile.getAttribute1()));
		}
		 prdItemFileService.batchDelete(dto);
	     return new ResponseData();
	 }
}
