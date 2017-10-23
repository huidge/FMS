package clb.core.production.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
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

import clb.core.common.service.ISequenceService;
import clb.core.common.utils.CommonUtil;
import clb.core.supplier.dto.SpeSupplier;
import clb.core.supplier.dto.SpeSupplierLine;
import clb.core.supplier.service.ISpeSupplierLineService;
import clb.core.supplier.service.ISpeSupplierService;

@Controller
public class PrdSupplierController extends BaseController{

	@Autowired
	private ISpeSupplierService speSupplierService;
	@Autowired
	private ISpeSupplierLineService speSupplierLineService;
	@Autowired
	private ISequenceService sequenceService;
	@Autowired
	private ISysFileService sysFileService;
	
	@RequestMapping(value = "/production/supplier/query")
    @ResponseBody
    public ResponseData query(SpeSupplier dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        dto.setType("PC");
        List<SpeSupplier>list= speSupplierService.selectByNameAndSort(dto, page, pageSize);
        if(dto.getSupplierId()!=null){
        	for(SpeSupplier vo:list){
        		if(vo.getFileId()!=null){
        			SysFile sysFile=sysFileService.selectByPrimaryKey(requestContext, vo.getFileId());
        			vo.setSysFile(sysFile);
        		}
        	}
        }
        return new ResponseData(list);
    }
	
	@RequestMapping(value = "/production/supplier/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request, @RequestBody SpeSupplier dto) {
		ResponseData response=new ResponseData(true);
		IRequest requestCtx = createRequestContext(request);
		List<SpeSupplier> list = speSupplierService.selectByName(dto);
	   
		dto.setType("PC");
        try {
			if(dto.getSupplierId()==null){
				 //判断产品公司是否存在
				if(list != null && list.size()>=1){
				      response=new ResponseData(false);
				      response.setMessage("供应商已存在！");
				      return response;
				}
				String code = sequenceService.getSequence("SUPPLIER");
				dto.setSupplierCode(code);
				dto.setType("PC");
				speSupplierService.insertSelective(requestCtx, dto);
			}else{
//				SpeSupplier speSupplier = new SpeSupplier();
//				speSupplier = speSupplierService.selectByPrimaryKey(requestCtx, dto);
//				speSupplier.setSerialNumber(dto.getSerialNumber());
				speSupplierService.updateByPrimaryKey(requestCtx, dto);
			}
		} catch (Exception e) {
			CommonUtil.printStackTraceToStr(e);
			response.setSuccess(false);
		    response.setMessage("产品公司或序号出现重复！");
		}
        response.setCode(dto.getSupplierId()+"");
        return response;
    }
	
	
	@RequestMapping(value = {"/production/supplierLine/query"}, method = {RequestMethod.POST})
    @ResponseBody
    public ResponseData query(SpeSupplierLine dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        if(dto.getSupplierId()==null || dto.getSupplierId()==-1){
        	return new ResponseData(new ArrayList<SpeSupplierLine>());
        }
        return new ResponseData(speSupplierLineService.select(iRequest, dto, page, pagesize));
    }

    /**
     * 保存产品
     * @param request
     * @return
     */
    @RequestMapping(value = {"/production/supplierLine/submit"}, method = {RequestMethod.POST})
    @ResponseBody
    public ResponseData submit(HttpServletRequest request, @RequestBody List<SpeSupplierLine> dtoList, BindingResult result)  {
        getValidator().validate(dtoList, result);
        if (result.hasErrors()) {
          ResponseData rd = new ResponseData(false);
          rd.setMessage(getErrorMessage(result, request));
          return rd;
        }
        IRequest iRequest = createRequestContext(request);
        for(SpeSupplierLine line:dtoList){
        	if(line.getSupplierId()==null || line.getSupplierId()==-1){
        		String supplierId=request.getParameter("supplierId");
        		if(StringUtils.isBlank(supplierId) || supplierId.equals("-1")){
        			throw new RuntimeException("联系地址保存失败，供应商ID不能为空！");
        		}else{
        			line.setSupplierId(Long.valueOf(supplierId));
        		}
            }
        }
        return new ResponseData(speSupplierLineService.batchUpdate(iRequest, dtoList));
    }

    @RequestMapping(value = "/production/supplierLine/remove")
    public ResponseData delete(HttpServletRequest request, @RequestBody List<SpeSupplierLine> dtoList){
    	speSupplierLineService.batchDelete(dtoList);
        return new ResponseData();
    }
}
