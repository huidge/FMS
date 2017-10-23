package clb.core.forecast.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.print.attribute.HashAttributeSet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.codahale.metrics.annotation.Timed;
import com.hand.hap.account.dto.UserRole;
import com.hand.hap.account.mapper.UserRoleMapper;
import com.hand.hap.account.service.IRole;
import com.hand.hap.account.service.IUserRoleService;
import com.hand.hap.core.IRequest;
import com.hand.hap.intergration.annotation.HapInbound;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;

import clb.core.common.exceptions.CommonException;
import clb.core.forecast.dto.FetChannelCheck;
import clb.core.forecast.dto.FetQuestion;
import clb.core.forecast.service.IFetChannelCheckService;
import clb.core.forecast.service.IFetQuestionLineService;
import clb.core.forecast.service.IFetQuestionService;
import clb.core.sys.controllers.ClbBaseController;

@Controller
public class WsFetChannelCheckController extends ClbBaseController{

    @Autowired
    private IFetChannelCheckService service;
    
    @Autowired
    private IFetQuestionLineService questionLineService;
    
    @Autowired
    private IFetQuestionService questionService;
    
    @Autowired
    private IUserRoleService userRoleService;
    
    /**
     * 校验用户是否有角色
     */
    @Timed
	@HapInbound(apiName = "ClbChannelCheckValidateUserId")
	@RequestMapping(value = "/api/channelcheck/validateuserid", method = RequestMethod.POST, produces = { "application/json" })
	@ResponseBody
    public ResponseData validateUserId(@RequestBody FetChannelCheck dto,HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
    	ResponseData responseData = new ResponseData(true);
		return responseData;
    }
    
    /**
     * 获取对账期间 
     */
    @Timed
	@HapInbound(apiName = "ClbChannelCheckQueryPeriods")
	@RequestMapping(value = "/api/channelcheck/queryPeriods", method = RequestMethod.POST, produces = { "application/json" })
	@ResponseBody
    public ResponseData query(HttpServletRequest request) {
    	IRequest requestContext = createRequestContext(request);
    	List<FetChannelCheck> data = service.selectAll(requestContext);
        List<String> receiptPeriods = new ArrayList<>();
        for(FetChannelCheck d:data){
        	if(!receiptPeriods.contains(d.getCheckPeriod().trim())){
        		receiptPeriods.add(d.getCheckPeriod().trim());
        	}
        }
        return new ResponseData(receiptPeriods);
    }

	/**
	 * 查询付款类型
	 */
	@Timed
	@HapInbound(apiName = "ClbChannelChecketAll")
	@RequestMapping(value = "/api/channelcheck/getAll", method = RequestMethod.POST, produces = { "application/json" })
	@ResponseBody
	public ResponseData getAll(HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		List<FetChannelCheck> data = service.selectAll(requestContext);
		List<String> receiptPeriods = new ArrayList<>();
		for(FetChannelCheck d:data){
			if(!receiptPeriods.contains(d.getCheckPeriod().trim())){
				receiptPeriods.add(d.getCheckPeriod().trim());
			}
		}
		return new ResponseData(receiptPeriods);
	}

	/**
	 * 汇总查询
	 * @throws CommonException
	 */
	@Timed
	@HapInbound(apiName = "ClbChannelChcekSummaryQuery")
	@RequestMapping(value = "/api/channelcheck/summaryQuery", method = RequestMethod.POST, produces = { "application/json" })
	@ResponseBody
	public ResponseData summaryQuery(@RequestBody FetChannelCheck dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
									 @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize,HttpServletRequest request) throws CommonException {
		IRequest iRequest = createRequestContext(request);
		return new ResponseData(service.summaryQuery(iRequest,dto,page,pageSize));
	}

	/**
	 * 获取对账单角标数量
	 *
	 * @param fetChannelCheck
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbChannelChcekGetCheckCount")
	@RequestMapping(value = "/api/channelcheck/getCheckCount", method = RequestMethod.POST)
	@ResponseBody
	public Integer getCheckCount(@RequestBody FetChannelCheck fetChannelCheck,HttpServletRequest request){
		IRequest requestContext = createRequestContext(request);
		return service.getCheckCount(fetChannelCheck.getParamStatus(),fetChannelCheck.getStatus(),requestContext.getUserId());
	}

    /**
     * 普通查询 -- 查看详情
     */
    @Timed
	@HapInbound(apiName = "ClbChannelCheckQuery")
	@RequestMapping(value = "/api/channelcheck/query", method = RequestMethod.POST, produces = { "application/json" })
	@ResponseBody
    public ResponseData query(@RequestBody FetChannelCheck dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
    	return new ResponseData(service.getData(iRequest,dto,page,pageSize));
    }

    /**
     * 导出数据
     * @throws CommonException 
     * @param 
     * 		sqlId:FetChannelCheckMapper.getData
     * 		FetChannelCheck 查询参数
     * @return 
     * 		Excel文件
     * @throws UnsupportedEncodingException 
     */
   	@HapInbound(apiName = "ClbChannelChcekExport")
   	@RequestMapping(value = "/api/channelcheck/export")
   	public void exportData(String checkPeriod,String paymentCompanyType,String receiveCompanyType,String paymentCompanyId,String paymentCompanyName,String receiveCompanyName,String receiveCompanyId,String version,@RequestParam(required=false) String paymentType,@RequestParam(required=false) String orderNumber,@RequestParam(required=false) String comments,HttpServletRequest request,HttpServletResponse response) throws CommonException, UnsupportedEncodingException {
    	IRequest iRequest = createRequestContext(request);
    	Map<String, String> params = new HashMap<>();
    	params.put("sqlId","FetChannelCheckMapper.getData");
    	params.put("userId",iRequest.getUserId().toString());
    	params.put("checkPeriod",checkPeriod);
    	params.put("paymentCompanyType",paymentCompanyType);
    	params.put("receiveCompanyType",receiveCompanyType);
    	params.put("paymentCompanyId",paymentCompanyId);
    	params.put("receiveCompanyId",receiveCompanyId);
    	params.put("version",version);
    	params.put("paymentCompanyName",URLDecoder.decode(paymentCompanyName, "gb2312"));
    	params.put("receiveCompanyName",URLDecoder.decode(receiveCompanyName, "gb2312"));
    	if(StringUtils.isNotEmpty(paymentType)){
    		params.put("paymentType",paymentType);
    	}
    	if(StringUtils.isNotEmpty(orderNumber)){
    		params.put("orderNumber",orderNumber);
    	}
    	if(StringUtils.isNotEmpty(comments)){
    		params.put("comments",comments);
    	}
    	service.exportData(iRequest,"FetChannelCheckMapper.getData", params,request,response);
    }
    
   
    /**
     * 确认
     * @param 
     * 		checkPeriod 对账期间
     * 		channelId   渠道Id
     * 		version     版本
     * @return 
     * 		ResponseData 更新的数据
     */
    @Timed
   	@HapInbound(apiName = "ClbChannelChcekEnsure")
   	@RequestMapping(value = "/api/channelcheck/ensure", method = RequestMethod.POST, produces = { "application/json" })
   	@ResponseBody
    public ResponseData enSure(@RequestBody FetChannelCheck dto,HttpServletRequest request) {
    	IRequest iRequest = createRequestContext(request);
    	return new ResponseData(service.enSure(iRequest,dto));
    }
    
    /**
     * 获取问题反馈数据接口
     * @param 
     * 		checkPeriod 对账期间
     * 		channelId   渠道Id
     * 		version     版本
     * 		userId		用户Id
     * @return 
     * 		ResponseData 更新的数据
     */
    @Timed
   	@HapInbound(apiName = "ClbChannelFeedback")
   	@RequestMapping(value = "/api/channelcheck/feedback/query", method = RequestMethod.POST, produces = { "application/json" })
   	@ResponseBody
    public ResponseData queryByCheckPeriod(@RequestBody FetQuestion dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
    	IRequest iRequest = createRequestContext(request);
    	return new ResponseData(questionLineService.queryByCheckPeriodWs(iRequest,dto,request));
    }
    
    /**
     * 提交问题反馈数据接口
     * @param 
     * 		checkPeriod 对账期间
     * 		channelId   渠道Id
     * 		version     版本
     * 		userId		用户Id
     * @return 
     * 		ResponseData 更新的数据
     */
    @Timed
   	@HapInbound(apiName = "ClbChannelFeedback")
   	@RequestMapping(value = "/api/channelcheck/feedback/submit", method = RequestMethod.POST, produces = { "application/json" })
   	@ResponseBody
   	public ResponseData saveData(HttpServletRequest request,@RequestBody FetQuestion dto) throws CommonException{
        IRequest iRequest = createRequestContext(request);
        questionService.saveData(iRequest,dto);
        List<FetQuestion> resData = new ArrayList<>();
        resData.add(dto);
        return new ResponseData(resData);
    }
    
    /**
     * 修改Irequest对象 
     */
   /* public ResponseData wrapIrequest(HttpServletRequest request,IRequest iRequest,Long userId){
    	Locale locale = RequestContextUtils.getLocale(request);
    	iRequest.setLocale(locale.toString());
    	if(userId != null){
    		iRequest.setUserId(userId);
    		//如果该用户有管理员权限，设置管理员权限
    		UserRole userRole = new UserRole();
    		userRole.setUserId(userId);
    		List<IRole> roles= userRoleService.selectUserRoles(iRequest, userRole);
    		if(CollectionUtils.isEmpty(roles)){
    			ResponseData responseData = new ResponseData(false);
    			responseData.setMessage("该用户无有效角色");
    			return responseData;
    		}
    		for(IRole role:roles){
    			if(role.getRoleId().equals(10001L)){
    				iRequest.setRoleId(10001L);
    				break;
    			}
    		}
    	}
    	return new ResponseData(true);
    }*/
    
}