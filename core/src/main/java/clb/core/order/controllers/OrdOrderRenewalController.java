package clb.core.order.controllers;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.exception.FieldRequiredException;
import com.hand.hap.job.exception.JobException;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;

import clb.core.common.utils.CommonUtil;
import clb.core.notification.service.INtnNotificationService;
import clb.core.order.dto.OrdOrder;
import clb.core.order.dto.OrdOrderRenewal;
import clb.core.order.service.IOrdAfterService;
import clb.core.order.service.IOrdOrderRenewalService;
import clb.core.order.service.IOrdOrderService;
import clb.core.system.dto.ClbUser;
import clb.core.system.service.IClbUserService;

    @Controller
    public class OrdOrderRenewalController extends BaseController{

    @Autowired
    private IOrdOrderRenewalService service;
    
    @Autowired
    private IOrdAfterService ordAfterService;
    
    @Autowired
    private IOrdOrderService ordOrderService;
    
    @Autowired
    private INtnNotificationService ntnNotificationService;
    
    @Autowired
    private IClbUserService clbUserService; 
    
    @Autowired
    private Scheduler quartzScheduler;


    @RequestMapping(value = "/fms/ord/OrderRenewal/query")
    @ResponseBody
    public ResponseData query(OrdOrderRenewal dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(ordAfterService.queryOrdRenewalList(requestContext,dto,page,pageSize));
    }

    @RequestMapping(value = "/fms/ord/OrderRenewal/queryByOrder")
    @ResponseBody
    public ResponseData queryByOrder(OrdOrderRenewal dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryRenewalByOrder(requestContext,dto,page,pageSize));
    }

    /**
     * 保单信息
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "/fms/ord/OrderRenewal/queryOrderByOrdOrderId")
    @ResponseBody
    public ResponseData queryOrderByOrdOrderId(OrdOrder dto, HttpServletRequest request) {
    	IRequest requestContext = createRequestContext(request);
    	return new ResponseData(ordAfterService.queryOrderByOrdOrderId(requestContext,dto));
    }
    /**
     * 续保信息
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "/fms/ord/OrderRenewal/queryOrderByOrderId")
    @ResponseBody
    public ResponseData queryOrderByOrderId(OrdOrder dto, HttpServletRequest request) {
    	IRequest requestContext = createRequestContext(request);
    	return new ResponseData(ordAfterService.queryOrderByOrdOrderId(requestContext,dto));
    }
    /**
     * 续保跟进===>续保表格
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "/fms/ord/OrderRenewal/queryRenewalGrid")
    @ResponseBody
    public ResponseData queryRenewalGrid(OrdOrderRenewal dto, HttpServletRequest request,@RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
    	IRequest requestContext = createRequestContext(request);
    	return new ResponseData(ordAfterService.queryRenewalByOrderId(requestContext,dto,page,pageSize));
    }

    @RequestMapping(value = "/fms/ord/OrderRenewal/save")
    @ResponseBody
    public ResponseData save(HttpServletRequest request,@RequestBody List<OrdOrder> dto){
        IRequest requestCtx = createRequestContext(request);
        OrdOrder order = ordOrderService.updateByPrimaryKeySelective(requestCtx, dto.get(0));
        List<OrdOrder> orders = new ArrayList<>();
        orders.add(order);
        return new ResponseData(orders);
    }

    @RequestMapping(value = "/fms/ord/OrderRenewal/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<OrdOrderRenewal> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
    /**
     * 发送通知
     * @param request
     * @param dto
     * @return
     */
    @RequestMapping(value = "/fms/ord/OrderRenewal/sendNotice")
    @ResponseBody
    public ResponseData sendNotice(HttpServletRequest request,@RequestBody OrdOrderRenewal dto){
    	IRequest requestCtx = createRequestContext(request);
    	ResponseData responseData = new ResponseData();
    	try {
			service.sendNotice(requestCtx,dto);
			return responseData;
		} catch (Exception e) {
			e.printStackTrace();
			responseData.setSuccess(false);
			return responseData;
		}
    }
    
    /**
     * 修改订单状态  将待确认失效的订单和该订单下的附加险的状态改为订单失效
     * @param request
     * @param dto
     * @return
     */
    @RequestMapping(value = "/fms/ord/OrderRenewal/updateStatusByOrderIds")
    @ResponseBody
    public ResponseData updateStatusByOrderIds(HttpServletRequest request,@RequestParam("orderIds[]") List<Long> orderIds){
    	IRequest requestCtx = createRequestContext(request);
		service.updateStatusByOrderIds(requestCtx,orderIds);
    	return new ResponseData();
    }
    /**
     * 修改订单状态  对线下操作成功的订单  修改其状态
     * @param request
     * @param dto
     * @return
     */
    @RequestMapping(value = "/fms/ord/OrderRenewal/updateStatusByOrderId")
    @ResponseBody
    public ResponseData updateStatusByOrderId(HttpServletRequest request,OrdOrderRenewal dto){
    	IRequest requestCtx = createRequestContext(request);
    	service.updateRenewalByOrderId(requestCtx, dto);
    	return new ResponseData();
    }
    
    /**
     * 执行续保短信定时任务
     * @return
     */
    @RequestMapping(value = "/fms/ord/OrderRenewal/startJob", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData startJob(HttpServletRequest request)
            throws SchedulerException, JobException, ClassNotFoundException,
            FieldRequiredException, UnsupportedEncodingException {

        JobKey jobKey = JobKey.jobKey("订单续保短信通知", "DEFAULT");
        quartzScheduler.triggerJob(jobKey);
        return new ResponseData();
    }
    
    @InitBinder   
    public void initBinder(WebDataBinder binder) {   
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");   
        dateFormat.setLenient(true);   
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));   
    }

    }