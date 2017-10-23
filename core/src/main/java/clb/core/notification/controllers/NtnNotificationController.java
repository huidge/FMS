package clb.core.notification.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.hand.hap.core.IRequest;
import com.hand.hap.intergration.annotation.HapInbound;
import com.hand.hap.system.dto.ResponseData;

import clb.core.notification.dto.NtnNotification;
import clb.core.notification.service.INtnNotificationService;
import clb.core.sys.controllers.ClbBaseController;
import clb.core.sys.service.ISysSendSMSService;
import net.sf.json.JSONObject;

@Controller
public class NtnNotificationController extends ClbBaseController {

	@Autowired
	private INtnNotificationService notificationService;
	
	@Autowired
	private ISysSendSMSService sendSMSService;
	/****
	 * 测试发短信
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/fms/ntn/notification/sendSMS")
	public JSONObject sendSMS(HttpServletRequest request){
		JSONObject json=new JSONObject();
		IRequest requestContext = createRequestContext(request);
		/*//直接发短信
		List<String> numList =new ArrayList<String>();
		numList.add("13160810306");
		json=sendSMSService.sendSMSDirect(requestContext,
				"您购买的（产品）受保人（张三）投保人（李四）续保到期日（2017/06/01），请尽快完成缴费。", numList, null, "YZX");*/
		
		//根据通知模板发短信
		/*Map<String,Object> data = new HashMap<String,Object>();
		data.put("name", "产品A");
		data.put("number", "0.01");
		json=sendSMSService.sendSMSByTemplateCode(requestContext, "aaa", data, "YZX");*/
		
		//根据通知模板发站内信、短信
		Map<String,Object> sysdata = new HashMap<String,Object>();
        sysdata.put("userName", "xiaoxiao");
        notificationService.sendNotification(requestContext,10001L, "REGISTER_SUCCESS", sysdata);
		
		return json;
	}
	/**
	 * 查询通知信息
	 * @param dto
	 * @param page
	 * @param pageSize
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/fms/ntn/notification/query")
	@ResponseBody
	public ResponseData query(NtnNotification dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		return new ResponseData(notificationService.select(requestContext, dto, page, pageSize));
	}

	
	/****
	 * 重推发短信
	 * @param outboundId
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/fms/ntn/notification/resetPull")
	@ResponseBody
	public ResponseData resetPull(@RequestParam("outboundId")String outboundId, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		String[] outboundIds = outboundId.split(",");
		boolean resetFlag=notificationService.resetPullSMSByOutboundIds(requestContext, outboundIds);
		ResponseData responseData=new ResponseData();
		responseData.setSuccess(resetFlag);
		if(!resetFlag){
			responseData.setMessage("选择的数据，状态非失败！");
		}
		return responseData;
	}
	/******
	 * 查询通知信息
	 * @param dto
	 * @param page
	 * @param pageSize
	 * @param request
	 * @return
	 */
	@Timed
    @HapInbound(apiName = "ClbWsNotifyList")
    @RequestMapping(value = "/api/notification/list", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
	public ResponseData queryList(@RequestBody NtnNotification dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		dto.setUserId(requestContext.getUserId());
		return new ResponseData(notificationService.queryList(requestContext, dto, page, pageSize));
	}
	/******
	 * 修改通知信息
	 * @param dto
	 * @param request
	 * @return
	 */
	@Timed
    @HapInbound(apiName = "ClbWsNotifyUpdate")
    @RequestMapping(value = "/api/notification/update", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
	public ResponseData update(@RequestBody List<NtnNotification> dto, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		return notificationService.updateStatus(requestContext, dto);
	}
}