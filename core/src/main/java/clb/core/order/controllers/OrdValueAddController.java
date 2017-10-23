package clb.core.order.controllers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.attachment.service.ISysFileService;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;

import clb.core.order.dto.OrdOrder;
import clb.core.order.dto.OrdTeamVisitor;
import clb.core.order.service.IOrdOrderService;
import clb.core.order.service.IOrdTeamVisitorService;
import clb.core.order.service.IOrdValueAddService;
import clb.core.production.dto.PrdItems;
import clb.core.sys.controllers.ClbBaseController;

@RequestMapping({"/fms/ord/valueAdd"})
@Controller
public class OrdValueAddController extends ClbBaseController{

    @Autowired
    private IOrdValueAddService service;
    
    @Autowired
	private IOrdOrderService ordOrderService;
    
    @Autowired
	private ISysFileService fileService;
    
    @Autowired
    private IOrdTeamVisitorService ordTeamVisitorService;


    @RequestMapping(value = "/query")
    @ResponseBody
    public ResponseData query(OrdOrder dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryValueAddByCondition(requestContext,dto,page,pageSize));
    }
    
    @RequestMapping(value = "/updateOrderByOrderId")
    @ResponseBody
    public ResponseData updateOrderByOrderId( HttpServletRequest request,@RequestBody List<OrdOrder> dto) {
    	IRequest requestContext = createRequestContext(request);
    	ResponseData responseData = new ResponseData();
    	try {
    		ordOrderService.updateByPrimaryKeySelective(requestContext, dto.get(0));
    		responseData.setRows(dto);
		} catch (Exception e) {
			responseData.setSuccess(false);
		}
    	return responseData;
    }
    
    @RequestMapping(value = "/queryOrdTeamVisitor")
    @ResponseBody
    public ResponseData queryOrdTeamVisitor(OrdTeamVisitor dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
    	IRequest requestContext = createRequestContext(request);
    	List<OrdTeamVisitor> list = service.queryOrdTeamVisitor(requestContext,dto,page,pageSize);
    	if(list != null && list.size() > 0){
    		for (OrdTeamVisitor ordTeamVisitor : list) {
    			if(ordTeamVisitor.getFileId() != null){
    				SysFile sysFile = fileService.selectByPrimaryKey(requestContext, ordTeamVisitor.getFileId());
					if (sysFile != null) {
						ordTeamVisitor.set_token(sysFile.get_token());
					} else {
						ordTeamVisitor.set_token(null);
					}
    			}
			}
    	}
    	return 	new ResponseData(list);
    }
    @RequestMapping(value = "/updateOrdTeamVisitor")
    @ResponseBody
    public ResponseData updateOrdTeamVisitor(@RequestBody List<OrdTeamVisitor> dto, HttpServletRequest request) {
    	IRequest requestContext = createRequestContext(request);
    	return new ResponseData(ordTeamVisitorService.batchUpdate(requestContext, dto));
    }
    
    @RequestMapping(value = "/updateStatus")
    @ResponseBody
    public ResponseData updateStatus(@RequestBody List<OrdOrder> dto, HttpServletRequest request) {
    	IRequest requestContext = createRequestContext(request);
    	ResponseData responseData = new ResponseData();
    	try {
			service.updateStatus(requestContext,dto.get(0),getIpAddr(request));
			responseData.setMessage("修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			responseData.setSuccess(false);
			responseData.setMessage(e.getMessage().contains("查询不到渠道用户")?e.getMessage():"未知异常,请联系管理员!");
		}
    	responseData.setRows(dto);
    	return responseData;
    }
    
    @RequestMapping(value = "/querySublineProductName")
    @ResponseBody
    public ResponseData querySublineProductName(PrdItems dto, HttpServletRequest request) {
    	IRequest requestContext = createRequestContext(request);
    	List<PrdItems> list = service.querySublineProductName(requestContext,dto);
    	if(list != null && list.size() >0){
    		return 	new ResponseData(list.get(0).getPrdItemSublineList());
    	}
    	return new ResponseData(list);
    }
    /**
     * 超时支付修改订单状态
     * @param dto
     * @param request
     * @return
     */
    @RequestMapping(value = "/updateStatusByOrderId")
    @ResponseBody
    public ResponseData updateStatusByOrderId(OrdOrder dto, HttpServletRequest request) {
    	IRequest requestContext = createRequestContext(request);
    	service.updateStatusByOrderId(requestContext, dto);
    	return new ResponseData();
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(true);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

}
