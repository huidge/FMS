package clb.core.course.controllers;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;

import clb.core.common.utils.DateUtil;
import clb.core.course.dto.TrnSupport;
import clb.core.course.service.ITrnSupportService;
import clb.core.notification.service.INtnNotificationService;
import clb.core.payment.dto.PaymentOrder;
import clb.core.payment.service.IPaymentOrderService;
import clb.core.sys.controllers.ClbBaseController;
import clb.core.system.dto.ClbCodeValue;
import clb.core.system.dto.ClbUser;
import clb.core.system.mapper.ClbUserMapper;
import clb.core.system.service.IClbCodeService;
import net.sf.json.JSONObject;

    @Controller
    public class TrnSupportController extends ClbBaseController{

    @Autowired
    private ITrnSupportService service;
    @Autowired
	private ClbUserMapper userMapper;
    @Autowired
	private IPaymentOrderService paymentOrderService;
	@Autowired
	private INtnNotificationService ntnNotificationService;
	@Autowired
	private IClbCodeService clbCodeService;
	
    @RequestMapping(value = "/fms/trn/support/query")
    @ResponseBody
    public ResponseData query(TrnSupport dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        List<TrnSupport> selectAllField = service.selectAllField(requestContext, dto, page, pageSize);
        if(!CollectionUtils.isEmpty(selectAllField)){
        	for (TrnSupport trnSupport : selectAllField) {
        		trnSupport.setContactPhoneComb(trnSupport.getPhoneCode()+" "+trnSupport.getContactPhone());
			}
        }
        return new ResponseData(selectAllField);
    }
    
    //修改价格
    @RequestMapping(value = "/fms/trn/support/updataAmout")
	@ResponseBody 
	public ResponseData updataAmout(HttpServletRequest request,@RequestParam("amount") String amount,@RequestParam("ids[]") List<Long> ids) {
		IRequest requestCtx = createRequestContext(request);
		ResponseData responseData = new ResponseData();
		if (amount != null || !"".equals(amount)) {
			for (Long supportId : ids) {
				try {
					TrnSupport dto = new TrnSupport();
					BigDecimal bigDecimal = new BigDecimal(amount);
					dto.setAmount(bigDecimal);
					dto.setSupportId(supportId);
					Date day=new Date();    
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
					dto.setAmountOperationDate(df.parse(df.format(day)));
					System.out.println(df.format(day)); 
					
					TrnSupport trnSupport;
					if("0".equals(amount)){
						dto.setStatus("AMOUNT");
						trnSupport = service.updateByPrimaryKeySelective(requestCtx, dto);
					}else{
						dto.setStatus("PAYMENT");
						trnSupport = service.updateByPrimaryKeySelective(requestCtx, dto);
						//判断该订单是否存在
						PaymentOrder queryBySource = paymentOrderService.queryBySource(requestCtx, "SUPPORT", String.valueOf(supportId));
						//支付
						if(queryBySource.getPaymentId() == null){
							PaymentOrder paymentOrder = new PaymentOrder();
							paymentOrder.setOrderContent("支援管理订单");
							paymentOrder.setOrderSubject("支援管理订单");
							paymentOrder.setSourceId(String.valueOf(supportId));
							paymentOrder.setSourceType("SUPPORT");
							paymentOrder.setAmount((new BigDecimal(amount)).doubleValue());
							paymentOrder.setPaymentType("ALIPAY"); 
							JSONObject createOrder = paymentOrderService.createOrder(requestCtx, paymentOrder, getIpAddr(request));
							System.out.println(createOrder);
						}else{
							queryBySource.setAmount((new BigDecimal(amount)).doubleValue());
							PaymentOrder saveOrUpdateOrder = paymentOrderService.saveOrUpdateOrder(requestCtx, queryBySource);
							System.out.println(saveOrUpdateOrder);
						}
					}

					// 金额不等于0才发送通知
					if(!"0".equals(amount)) {
						//调用通知接口发送通知信息
						trnSupport = service.selectByPrimaryKey(requestCtx, trnSupport);
						Map<String, Object> noticeMap = new HashMap<String, Object>();
						noticeMap.put("name", getSupportTypeName(trnSupport.getSupportType()) + " " + trnSupport.getSupportNum());
						noticeMap.put("nowTime", DateUtil.getDateStr(new Date(), "yyyy-MM-dd HH:mm:ss"));
						// 执行通知
						ClbUser user = new ClbUser();
						user.setUserType("CHANNEL");
						user.setRelatedPartyId(trnSupport.getChannelId());
						List<ClbUser> userList = userMapper.selectAllFields(user);
						for (ClbUser u : userList) {
							ntnNotificationService.sendNotification(requestCtx, u.getUserId(), "ZY0001", noticeMap);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					responseData.setSuccess(false);
				}
			}
		}
		return new ResponseData();
	}
    
    //申请失败
    @RequestMapping(value = "/fms/trn/support/fileContent")
	@ResponseBody 
    public ResponseData fileContent(HttpServletRequest request,@RequestParam("costContent") String costContent,@RequestParam("ids[]") List<Long> ids) {
		IRequest requestCtx = createRequestContext(request);
		if (costContent != null || !"".equals(costContent)) {
			for (Long supportId : ids) {
				TrnSupport dto = new TrnSupport();
				dto.setSupportId(supportId);
				TrnSupport trnSupportSource = service.selectByPrimaryKey(requestCtx, dto);
				dto.setCostContent(costContent);
				dto.setStatus("FAIL");
				TrnSupport trnSupport = service.updateByPrimaryKeySelective(requestCtx, dto);
				//调用通知接口发送通知信息
				trnSupport=service.selectByPrimaryKey(requestCtx, trnSupport);
		        Map<String,Object> noticeMap = new HashMap<String,Object>();
		        noticeMap.put("name",getSupportTypeName(trnSupport.getSupportType()) +" "+trnSupport.getSupportNum());
		        noticeMap.put("reaSon", trnSupport.getCostContent());
		        noticeMap.put("nowTime", DateUtil.getDateStr(new Date(), "yyyy-MM-dd HH:mm:ss"));
		        // 执行通知
				ClbUser user = new ClbUser();
				user.setUserType("CHANNEL");
				user.setRelatedPartyId(trnSupport.getChannelId());
				List<ClbUser> userList = userMapper.selectAllFields(user);
				for(ClbUser u:userList){
					ntnNotificationService.sendNotification(requestCtx,u.getUserId(), trnSupportSource.getStatus().equals("AMOUNT")?"ZY0004":"ZY0002", noticeMap);
				}
			}
		}
		return new ResponseData();
	}
    
  //申请成功
    @RequestMapping(value = "/fms/trn/support/successStatus")
	@ResponseBody 
    public ResponseData successStatus(HttpServletRequest request,@RequestParam("status") String status,@RequestParam("ids[]") List<Long> ids) {
		IRequest requestCtx = createRequestContext(request);
		if (status != null || !"".equals(status)) {
			for (Long supportId : ids) {
				TrnSupport dto = new TrnSupport();
				dto.setSupportId(supportId);
				dto.setStatus("SUCCESS");
				TrnSupport trnSupport = service.updateByPrimaryKeySelective(requestCtx, dto);
				//调用通知接口发送通知信息
				trnSupport=service.selectByPrimaryKey(requestCtx, trnSupport);
		        Map<String,Object> noticeMap = new HashMap<String,Object>();
		        noticeMap.put("name",getSupportTypeName(trnSupport.getSupportType()) +" "+trnSupport.getSupportNum());
		        noticeMap.put("nowTime", DateUtil.getDateStr(new Date(), "yyyy-MM-dd HH:mm:ss"));
		        // 执行通知
				ClbUser user = new ClbUser();
				user.setUserType("CHANNEL");
				user.setRelatedPartyId(trnSupport.getChannelId());
				List<ClbUser> userList = userMapper.selectAllFields(user);
				for(ClbUser u:userList){
					ntnNotificationService.sendNotification(requestCtx,u.getUserId(), "ZY0003", noticeMap);
				}
			}
		}
		return new ResponseData();
	}
    
  //取消申请
    @RequestMapping(value = "/fms/trn/support/cancelSupport")
	@ResponseBody 
    public ResponseData cancelSupport(HttpServletRequest request,@RequestParam("status") String status,@RequestParam("ids[]") List<Long> ids) {
		IRequest requestCtx = createRequestContext(request);
		if (status != null || !"".equals(status)) {
			for (Long supportId : ids) {
				TrnSupport dto = new TrnSupport();
				dto.setSupportId(supportId);
				dto.setStatus("CANCEL");
				TrnSupport trnSupport = service.updateByPrimaryKeySelective(requestCtx, dto);
				//调用通知接口发送通知信息
				trnSupport=service.selectByPrimaryKey(requestCtx, trnSupport);
		        Map<String,Object> noticeMap = new HashMap<String,Object>();
		        noticeMap.put("name",getSupportTypeName(trnSupport.getSupportType()) +" "+trnSupport.getSupportNum());
				// 执行通知
				ClbUser user = new ClbUser();
				user.setUserType("CHANNEL");
				user.setRelatedPartyId(trnSupport.getChannelId());
				List<ClbUser> userList = userMapper.selectAllFields(user);
				for(ClbUser u:userList){
					ntnNotificationService.sendNotification(requestCtx,u.getUserId(), "ZY0005", noticeMap);
				}
			}
		}
		return new ResponseData();
	}
    
    @RequestMapping(value = "/fms/trn/support/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<TrnSupport> dto){
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/fms/trn/support/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<TrnSupport> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
    
    
    public String getSupportTypeName(String supportTypeValue){
    	ClbCodeValue code = new ClbCodeValue();
		code.setValue(supportTypeValue);
		code.setCode("COURSE.SUPPORT_TYPE");
		List<ClbCodeValue>list= clbCodeService.selectCodeValuesByParam(code);
		if(CollectionUtils.isEmpty(list)){
			return null;
		}else{
			return list.get(0).getMeaning();
		}
    }
    
    @InitBinder
	public void initBinder(WebDataBinder binder) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(true);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}
    }