package clb.core.order.controllers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.codahale.metrics.annotation.Timed;
import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.attachment.service.ISysFileService;
import com.hand.hap.core.IRequest;
import com.hand.hap.intergration.annotation.HapInbound;
import com.hand.hap.system.dto.ResponseData;

import clb.core.common.service.ISequenceService;
import clb.core.common.utils.CommonUtil;
import clb.core.order.dto.OrdAddition;
import clb.core.order.dto.OrdAfter;
import clb.core.order.dto.OrdAfterFollow;
import clb.core.order.dto.OrdAfterLog;
import clb.core.order.dto.OrdOrder;
import clb.core.order.dto.OrdOrderRenewal;
import clb.core.order.dto.OrdTemplate;
import clb.core.order.dto.OrdTemplateLine;
import clb.core.order.service.IOrdAdditionService;
import clb.core.order.service.IOrdAfterFollowService;
import clb.core.order.service.IOrdAfterLogService;
import clb.core.order.service.IOrdAfterService;
import clb.core.order.service.IOrdOrderRenewalService;
import clb.core.order.service.IOrdOrderService;
import clb.core.sys.controllers.ClbBaseController;
import clb.core.system.dto.ClbUser;
import clb.core.system.service.IClbUserService;

@Controller
public class WsOrdAfterController extends ClbBaseController {
	@Autowired
	private IOrdAfterService ordAfterService;

	@Autowired
	private ISysFileService fileService;

	@Autowired
	private ISequenceService sequenceService;

	@Autowired
	private IOrdAfterFollowService ordAfterFollowService;

	@Autowired
	private IOrdOrderService ordOrderservice;

	@Autowired
	private IOrdAfterLogService ordAfterLogService;
	
	@Autowired
	private MessageSource messageSource;
	/**
	 * 订单售后汇总接口
	 * 
	 * @param dto
	 * @param request
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsOrdAfterQueryOrdAfterList")
	@RequestMapping(value = "/api/ordAfter/queryOrdAfterList", method = RequestMethod.POST, produces = {
			"application/json" })
	@ResponseBody
	public ResponseData queryOrdAfterList(@RequestBody OrdAfter dto, HttpServletRequest request,
			@RequestParam(defaultValue = DEFAULT_PAGE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
		IRequest requestContext = createRequestContext(request);
		//微信查询使用
		if(dto.getApplicantCustomerId() != null) {
			requestContext.setUserId(null);
		}
		List<OrdAfter> list = ordAfterService.queryWSOrdAfterList(requestContext, dto, page, pageSize);
		return new ResponseData(ordAfterService.queryHandleUserName(requestContext, list));
	}

	/**
	 * 订单售后汇总接口
	 * @param dto
	 * @param request
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsOrdAfterQueryOrdAfterListTatal")
	@RequestMapping(value = "/api/ordAfter/queryOrdAfterListTotal", method = RequestMethod.POST, produces = {
			"application/json" })
	@ResponseBody
	public Long queryOrdAfterListTotal(@RequestBody OrdAfter dto, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		Long list = ordAfterService.queryOrdAfterListTotal(requestContext, dto);
		return list;
	}

	/**
	 * 订单售后汇总接口(团队)
	 * 
	 * @param dto
	 * @param request
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsOrdAfterQueryOrdAfterTeamList")
	@RequestMapping(value = "/api/ordAfter/queryOrdAfterTeamList", method = RequestMethod.POST, produces = {
			"application/json" })
	@ResponseBody
	public ResponseData queryWSOrdAfterTeamList(@RequestBody OrdAfter dto, HttpServletRequest request,
			@RequestParam(defaultValue = DEFAULT_PAGE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
		IRequest requestContext = createRequestContext(request);
		List<OrdAfter> list = ordAfterService.queryWSOrdAfterTeamList(requestContext, dto, page, pageSize);
		return new ResponseData(ordAfterService.queryHandleUserName(requestContext, list));
	}

	/**
	 * 售后列表--->取消售后
	 * 
	 * @param dto
	 * @param request
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsOrdAfterCancel")
	@RequestMapping(value = "/api/ordAfter/cancel", method = RequestMethod.POST, produces = { "application/json" })
	@ResponseBody
	public ResponseData cancel(@RequestBody OrdAfter dto, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		ResponseData responseData = new ResponseData();

		if ("FAIL".equals(dto.getAfterStatus()) || "SUCCESS".equals(dto.getAfterStatus())) {
			responseData.setSuccess(false);
			responseData.setMessage("该状态下不能取消!");
			return responseData;
		}
		
		ordAfterService.cancel(requestContext,dto);
		return responseData;
	}

	/**
	 * 售后  续保列表日志查询 (afterId)
	 * @param request
	 * @param dto
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsOrdAfterLogQuery")
	@RequestMapping(value = "/api/ordRenewal/queryAfterLog", method = RequestMethod.POST, produces = {
			"application/json" })
	@ResponseBody
	public ResponseData queryAfterLog(HttpServletRequest request, @RequestBody OrdAfterLog dto) {
		IRequest requestCtx = createRequestContext(request);
		ResponseData responseData = new ResponseData();
		// 必须传递售后id和id类型(AFTER和RENEWAL)
		Long generalId = dto.getGeneralId();
		String idType = dto.getIdType();
		if (generalId == null || generalId == 0 || idType == null || "".equals(idType)) {
			List<OrdAfterLog> list = new ArrayList<>();
			responseData.setRows(list);
			return responseData;
		}
		List<OrdAfterLog> list = ordAfterLogService.query(requestCtx, dto);
		responseData.setRows(list);
		return responseData;
	}

	/**
	 * 保单信息查询---->售后跟进页面
	 * 
	 * @param dto
	 * @param request
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsOrdAfterQueryOrderInfoByAfterId")
	@RequestMapping(value = "/api/ordAfter/queryOrderInfoByAfterId", method = RequestMethod.POST, produces = {
			"application/json" })
	@ResponseBody
	public ResponseData queryOrderInfoByAfterId(@RequestBody OrdAfter dto, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		Long afterId = dto.getAfterId();
		List<OrdAfter> list = new ArrayList<>();
		if (afterId == 0 || afterId == null) {
			return new ResponseData(list);
		}
		list = ordAfterService.queryOrderInfoByAfterId(requestContext, dto);
		return new ResponseData(ordAfterService.queryHandleUserName(requestContext, list));
	}

	/**
	 * 保单信息查询---->续保详情页面
	 * 
	 * @param dto
	 * @param request
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsOrdAfterQueryOrdInfoByOrderId")
	@RequestMapping(value = "/api/ordAfter/queryOrdInfoByOrderId", method = RequestMethod.POST, produces = {
			"application/json" })
	@ResponseBody
	public ResponseData queryOrdInfoByOrderId(@RequestBody OrdOrder dto, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		Long orderId = dto.getOrderId();
		List<OrdOrder> list = new ArrayList<>();
		if (orderId == 0 || orderId == null) {
			return new ResponseData(list);
		}
		list = ordAfterService.queryOrderByOrdOrderId(requestContext, dto);
		return new ResponseData(list);
	}

	/**
	 * 续保信息查询---->售后跟进页面
	 * 
	 * @param dto
	 * @param request
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsOrdAfterQueryRenewalInfoByOrderId")
	@RequestMapping(value = "/api/ordAfter/queryRenewalInfoByOrderId", method = RequestMethod.POST, produces = {
			"application/json" })
	@ResponseBody
	public ResponseData queryRenewalInfoByOrderId(@RequestBody OrdOrder dto, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		Long orderId = dto.getOrderId();
		List<OrdOrder> list = new ArrayList<>();
		if (orderId == 0 || orderId == null) {
			return new ResponseData(list);
		}
		list = ordAfterService.queryOrderByOrdOrderId(requestContext, dto);
		return new ResponseData(list);
	}

	/**
	 * 本期续保表格查询---->售后跟进页面
	 * 
	 * @param request
	 * @param dto
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsOrdAfterGridByOrderId")
	@RequestMapping(value = "/api/ordAfter/queryRenewalGridByOrderId", method = RequestMethod.POST, produces = {"application/json" })
	@ResponseBody
	public ResponseData queryRenewalInfoByOrderId(HttpServletRequest request, @RequestBody OrdOrderRenewal dto,
			@RequestParam(defaultValue = DEFAULT_PAGE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
		IRequest requestCtx = createRequestContext(request);
		return new ResponseData(ordAfterService.queryRenewalInfoByOrderId(requestCtx, dto, page, pageSize));
	}

	/**
	 * 售后跟进记录查询--->售后跟进页面
	 * 
	 * @param dto
	 * @param request
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsOrdAfterFollowQuery")
	@RequestMapping(value = "/api/ordAfter/queryAfterFollow", method = RequestMethod.POST, produces = {
			"application/json" })
	@ResponseBody
	public ResponseData queryAfterFollow(@RequestBody OrdAfterFollow dto, HttpServletRequest request,
			@RequestParam(defaultValue = DEFAULT_PAGE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
		IRequest requestCtx = createRequestContext(request);
		Long afterId = dto.getAfterId();
		if (afterId == null) {
			List<OrdAfterFollow> list = new ArrayList<>();
			return new ResponseData(list);
		}
		return new ResponseData(ordAfterFollowService.queryByAfterId(requestCtx,dto,page,pageSize));
	}

	/**
	 * 售后列表跟进页面---->提交  主要是添加售后跟进记录  根据状态(资料审核中--资料需复查)传递退保表格 
	 * @param request
	 * @param dto
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsOrdAfterSubmit")
	@RequestMapping(value = "/api/ordAfter/ordAfterSubmit", method = RequestMethod.POST, produces = {"application/json" })
	@ResponseBody
	public ResponseData ordAfterSubmit(HttpServletRequest request, @RequestBody OrdAfter dto) {
		IRequest requestCtx = createRequestContext(request);
		ResponseData responseData = new ResponseData();
		 try {
			 	ordAfterService.ordAfterSubmit(requestCtx,dto);
				responseData.setSuccess(true);
		        responseData.setMessage("提交成功");
			} catch (Exception e) {
				responseData.setSuccess(false);
		        responseData.setMessage(e.getMessage());
			}
	        return responseData;
	}

	/**
	 * 售后跟进记录删除(根据主键)
	 * 
	 * @param dto
	 * @param request
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsOrdAfterFollowDelete")
	@RequestMapping(value = "/api/ordAfter/deleteAfterFollow", method = RequestMethod.POST, produces = {
			"application/json" })
	@ResponseBody
	public ResponseData deleteAfterFollow(@RequestBody OrdAfterFollow dto, HttpServletRequest request) {
		ordAfterFollowService.deleteByPrimaryKey(dto);
		return new ResponseData();
	}

	/**
	 * 文件下载
	 * 
	 * @param request
	 * @param dto
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsOrdAfterFollowFilePath")
	@RequestMapping(value = "/api/ordAfterFollow/filePath", method = RequestMethod.POST, produces = {
			"application/json" })
	@ResponseBody
	public ResponseData queryFilePath(HttpServletRequest request, @RequestBody OrdAfterFollow dto) {
		IRequest requestCtx = createRequestContext(request);
		List<SysFile> sysFileList = new ArrayList<SysFile>();
		SysFile sysFile = fileService.selectByPrimaryKey(requestCtx, dto.getFileId());
		if (sysFile != null) {
			sysFileList.add(sysFile);
		}
		return new ResponseData(sysFileList);
	}

	/**
	 * 续保清单列表查询(个人)
	 * 
	 * @param request
	 * @param dto
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsOrdRenewalList")
	@RequestMapping(value = "/api/ordRenewal/queryOrdRenewalList", method = RequestMethod.POST, produces = {
			"application/json" })
	@ResponseBody
	public ResponseData queryOrdRenewalList(HttpServletRequest request, @RequestBody OrdOrderRenewal dto,
			@RequestParam(defaultValue = DEFAULT_PAGE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
		IRequest requestCtx = createRequestContext(request);
		//微信查询使用
		if(dto.getApplicantCustomerId() != null) {
			requestCtx.setUserId(null);
		}
		return new ResponseData(ordAfterService.queryWSOrdRenewalList(requestCtx, dto, page, pageSize));
	}
	/**
	 * 续保订单的条数
	 * @param request
	 * @param dto
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsOrdRenewalTotal")
	@RequestMapping(value = "/api/ordRenewal/queryRenewalTotal", method = RequestMethod.POST, produces = {
	"application/json" })
	@ResponseBody
	public Long queryRenewalTotal(HttpServletRequest request, @RequestBody OrdOrderRenewal dto) {
		IRequest requestCtx = createRequestContext(request);
		return ordAfterService.queryRenewalTotal(requestCtx,dto);
	}
	/**
	 * 续保清单列表查询(团队)
	 * @param request
	 * @param dto
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsOrdRenewalTeamList")
	@RequestMapping(value = "/api/ordRenewal/queryOrdRenewalTeamList", method = RequestMethod.POST, produces = {"application/json" })
	@ResponseBody
	public ResponseData queryOrdRenewalTeamList(HttpServletRequest request, @RequestBody OrdOrderRenewal dto,
			@RequestParam(defaultValue = DEFAULT_PAGE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
		IRequest requestCtx = createRequestContext(request);
		return new ResponseData(ordAfterService.ClbWsOrdRenewalTeamList(requestCtx, dto, page, pageSize));
	}

	/**
	 * 续保信息表格查询 ----->续保详情页面
	 *
	 * @param dto
	 * @param request
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsOrdRenewalGridAll")
	@RequestMapping(value = "/api/ordRenewal/queryRenewalGridAllByOrderId", method = RequestMethod.POST, produces = {
			"application/json" })
	@ResponseBody
	public ResponseData queryRenewalGridAllByOrderId(@RequestBody OrdOrderRenewal dto, HttpServletRequest request,
			@RequestParam(defaultValue = DEFAULT_PAGE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
		List<OrdAfter> list = new ArrayList<>();
		if (dto.getOrderId() == null) {
			return new ResponseData(list);
		}

		IRequest requestContext = createRequestContext(request);
		return new ResponseData(ordAfterService.queryRenewalByOrderId(requestContext, dto, page, pageSize));
	}

	/**
	 * Lov(查询订单信息) ---->售后申请页面
	 * 
	 * @param request
	 * @param dto
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsOrdOrderByOrderId")
	@RequestMapping(value = "/api/ordOrder/queryOrdOrderByOrderId", method = RequestMethod.POST, produces = {
			"application/json" })
	@ResponseBody
	public ResponseData queryOrdOrderByOrderId(HttpServletRequest request, @RequestBody OrdOrder dto) {
		IRequest requestCtx = createRequestContext(request);
		List<OrdOrder> list = new ArrayList<>();
		ResponseData responseData = new ResponseData();
		// 必须传递订单id
		Long orderId = dto.getOrderId();
		String policyNumber = dto.getPolicyNumber();
		if (orderId != null || policyNumber != null) {
			list = ordAfterService.queryOrdOrderByOrderId(requestCtx, dto);
			responseData.setRows(list);
			return responseData;
		}
		responseData.setMessage("订单ID不存在!");
		responseData.setSuccess(false);
		return responseData;
	}

	/**
	 * 新建售后页面--->提交
	 * 
	 * @param dto
	 * @param request
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsOrdRenewalApplication")
	@RequestMapping(value = "/api/ordRenewal/ordAfterApplication", method = RequestMethod.POST, produces = {"application/json" })
	@ResponseBody
	public ResponseData ordAfterApplication(@RequestBody OrdAfter dto, HttpServletRequest request) {
		IRequest requestCtx = createRequestContext(request);
		ResponseData responseData = new ResponseData();
		List<OrdAfter> list1 = new ArrayList<>();
		list1.add(dto);
		try {
			if (dto.getOrderId() == null || dto.getOrderId() == 0 || dto.getTemplateId() == null || dto.getTemplateId() == 0) {
				responseData.setMessage("您没有选择订单或没有选择售后类型,请重新选择!");
				responseData.setRows(list1);
				responseData.setSuccess(false);
			}else{
				List<OrdAfter> list = ordAfterService.ordAfterApplication(requestCtx, dto);
				responseData.setRows(list);
			}
		} catch (Exception e) {
			CommonUtil.printStackTraceToStr(e);
			responseData.setSuccess(false);
			responseData.setMessage(e.getMessage());
		}
		return responseData;
	}

	/**
	 * 退保表格
	 * 
	 * @param dto
	 * @param request
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsOrdOrderItems")
	@RequestMapping(value = "/api/ordOrder/queryOrdOrderItems", method = RequestMethod.POST, produces = {
			"application/json" })
	@ResponseBody
	public ResponseData queryOrdOrderItems(@RequestBody OrdOrder dto, HttpServletRequest request,
			@RequestParam(defaultValue = DEFAULT_PAGE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
		IRequest requestCtx = createRequestContext(request);
		List<OrdAddition> list = new ArrayList<>();

		Long orderId = dto.getOrderId();
		if (orderId != null) {
			OrdAddition ordAddition = new OrdAddition();
			ordAddition.setOrderId(orderId);
			list = ordAfterService.queryOrdAdditionByOrderId(requestCtx, ordAddition, page, pageSize - 1);
		}

		return new ResponseData(list);
	}

	/**
	 * 查询订单信息 根据订单id 在申请售后页面重新选择订单售后
	 * 
	 * @param dto
	 * @param request
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsOrdAfterQueryOrdByOrderId")
	@RequestMapping(value = "/api/ordAfter/queryOrdByOrderId", method = RequestMethod.POST, produces = {
			"application/json" })
	@ResponseBody
	public ResponseData queryOrdByOrderId(@RequestBody OrdOrder dto, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		OrdOrder ordOrder = ordOrderservice.selectByPrimaryKey(requestContext, dto);
		List<OrdOrder> list = new ArrayList<>();
		list.add(ordOrder);
		return new ResponseData(list);
	}

	/**
	 * 续保详情页面 续保表格
	 * @param request
	 * @param dto
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsOrdRenewalByOrderId")
	@RequestMapping(value = "/api/ordRenewal/queryRenewalByOrderId", method = RequestMethod.POST, produces = {
			"application/json" })
	@ResponseBody
	public ResponseData queryRenewalByOrderId(HttpServletRequest request, @RequestBody OrdOrderRenewal dto,
			@RequestParam(defaultValue = DEFAULT_PAGE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
		IRequest requestCtx = createRequestContext(request);
		ResponseData responseData = new ResponseData();
		// 必须传递订单id
		Long orderId = dto.getOrderId();
		if (orderId == null) {
			List<OrdOrderRenewal> list = new ArrayList<>();
			responseData.setRows(list);
			return responseData;
		}
		List<OrdOrderRenewal> list = ordAfterService.queryRenewalByOrderId(requestCtx, dto, page, pageSize);
		responseData.setRows(list);
		return responseData;
	}
	
	/**
	 * 查询售后项目
	 * @param request
	 * @param dto
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsOrdAfterProject")
	@RequestMapping(value = "/api/ordRenewal/queryOrdAfterProject", method = RequestMethod.POST, produces = {
	"application/json" })
	@ResponseBody
	public ResponseData queryOrdAfterProject(HttpServletRequest request, @RequestBody OrdTemplate dto) {
		IRequest requestCtx = createRequestContext(request);
		dto.setTemplateTypeCode("AFTER_SALES_SERVICE");
		return new ResponseData(ordAfterService.queryOrdAfterProject(requestCtx,dto));
	}
	
	/**
	 * 查询售后类型
	 * @param request
	 * @param dto
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsOrdAfterTypeAndTemplateId")
	@RequestMapping(value = "/api/ordRenewal/queryOrdAfterTypeAndTemplateId", method = RequestMethod.POST, produces = {
	"application/json" })
	@ResponseBody
	public ResponseData queryOrdAfterTypeAndTemplateId(HttpServletRequest request, @RequestBody OrdTemplate dto) {
		IRequest requestCtx = createRequestContext(request);
		dto.setTemplateTypeCode("AFTER_SALES_SERVICE");
		/*List<OrdTemplate> list = new ArrayList<>();
		if(dto.getAfterProject() != null && !"".equals(dto.getAfterProject())){
			return new ResponseData(ordAfterService.queryApplyItem(requestCtx,dto));
		}else{
			return new ResponseData(list);
		}*/
		return new ResponseData(ordAfterService.queryApplyItem(requestCtx,dto));
	}
	/**
	 * 查询文件路径  by  文件id
	 * @param request
	 * @param dto
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsQueryFilePathByFileId")
	@RequestMapping(value = "/api/ordAfter/queryFilePathByFileId", method = RequestMethod.POST, produces = {
	"application/json" })
	@ResponseBody
	public String queryFilePathByFileId(HttpServletRequest request, @RequestBody SysFile dto) {
		IRequest requestCtx = createRequestContext(request);
		SysFile sysFile = fileService.selectByPrimaryKey(requestCtx, dto.getFileId());
		String filePath = null;
		if(sysFile != null) {
			String message = messageSource.getMessage("fms.pub.oss_web_path", null, RequestContextUtils.getLocale(request));
			String path = sysFile.getFilePath();
			filePath = message + path;
		}
		return filePath;
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(true);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

}
