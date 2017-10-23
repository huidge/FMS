package clb.core.order.controllers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;

import clb.core.channel.dto.CnlChannelContract;
import clb.core.channel.service.ICnlChannelContractService;
import clb.core.common.service.ISequenceService;
import clb.core.common.utils.CommonUtil;
import clb.core.common.utils.DateUtil;
import clb.core.order.dto.OrdAddition;
import clb.core.order.dto.OrdAfter;
import clb.core.order.dto.OrdAfterFollow;
import clb.core.order.dto.OrdOrder;
import clb.core.order.dto.OrdOrderRenewal;
import clb.core.order.service.IOrdAfterFollowService;
import clb.core.order.service.IOrdAfterService;
import clb.core.order.service.IOrdOrderService;
import clb.core.system.dto.ClbUser;
import clb.core.system.service.IClbUserService;

@Controller
public class OrdAfterController extends BaseController {

	@Autowired
	private IOrdAfterService service;

	@Autowired
	private ISequenceService sequenceService;

	@Autowired
	private IOrdAfterFollowService ordAfterFollowService;

	@Autowired
	private IOrdOrderService ordOrderService;

	@Autowired
	private IClbUserService clbUserService;

	@Autowired
	private ISysFileService fileService;

	@Autowired
	private ICnlChannelContractService cnlChannelContractService;

	@RequestMapping(value = "/fms/ord/after/query")
	@ResponseBody
	public ResponseData query(OrdAfter dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		List<OrdAfter> afterList = service.queryOrdAfterList(requestContext, dto, page, pageSize);
		return new ResponseData(service.queryHandleUserName(requestContext, afterList));
	}

	/**
	 * 保单信息
	 * @param dto
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/fms/ord/after/queryOrderInfoByAfterId")
	@ResponseBody
	public ResponseData queryOrderInfoByAfterId(OrdAfter dto, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		List<OrdAfter> list = service.queryOrderInfoByAfterId(requestContext, dto);
		return new ResponseData(service.queryHandleUserName(requestContext, list));
	}

	/**
	 * 续保信息
	 * 
	 * @param dto
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/fms/ord/after/queryOrderByOrdOrderId")
	@ResponseBody
	public ResponseData queryOrderByOrdOrderId(OrdOrder dto, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		return new ResponseData(service.queryOrderByOrdOrderId(requestContext, dto));
	}

	/**
	 * 续保信息 在续保成功弹窗页面 回显数据
	 * 
	 * @param dto
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/fms/ord/after/queryOrderInfoByOrderId")
	@ResponseBody
	public ResponseData queryOrderInfoByOrderId(OrdOrder dto, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		List<OrdOrder> list = service.queryOrderByOrdOrderId(requestContext, dto);
		if (list.size() > 0) {
			for (OrdOrder ordOrder : list) {
				//宽限期
				Integer gracePeriod = Integer.parseInt(ordOrder.getGracePeriod().toString());
				Integer administrativePeriod = Integer.parseInt(ordOrder.getAdministrativePeriod().toString());
				
				//月缴
				if ("MP".equals(dto.getPayMethod().trim())) {
					ordOrder.setRenewalDueDate(DateUtil.getDate(ordOrder.getRenewalDueDate(), 0, 1, 0));
				//季缴
				} else if ("QP".equals(dto.getPayMethod().trim())) {
					ordOrder.setRenewalDueDate(DateUtil.getDate(ordOrder.getRenewalDueDate(), 0, 3, 0));
				//半年缴
				} else if ("SAP".equals(dto.getPayMethod().trim())) {
					ordOrder.setRenewalDueDate(DateUtil.getDate(ordOrder.getRenewalDueDate(), 0, 6, 0));
				//年缴
				} else if ("AP".equals(dto.getPayMethod().trim())) {
					ordOrder.setRenewalDueDate(DateUtil.getDate(ordOrder.getRenewalDueDate(), 1, 0, 0));
				}
				//宽限期和行政期的判断标准是以产品公司维护的为准  先有宽限期  再有行政期
				//宽限期   
				if(gracePeriod != null && gracePeriod != 0) {
					ordOrder.setGraceDate(DateUtil.getDate(ordOrder.getRenewalDueDate(), 0, 0, gracePeriod));
					//行政期  
					if(administrativePeriod != null &&  administrativePeriod != 0) {
						ordOrder.setAdministrativeDate(DateUtil.getDate(ordOrder.getGraceDate(), 0, 0, administrativePeriod));
					}else {
						ordOrder.setAdministrativeDate(null);
					}
				}else {
					ordOrder.setGraceDate(null);
					ordOrder.setAdministrativeDate(null);
				}
				//续保成功日期  = 弹窗时间
				ordOrder.setRenewalSuccessDate(new Date());
				ordOrder.setPayPeriods(ordOrder.getPayPeriods() + 1);
			}

		}
		return new ResponseData(list);
	}

	/**
	 * 售后跟进==>续保表格
	 * 
	 * @param dto
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/fms/ord/after/queryRenewalInfoByOrderId")
	@ResponseBody
	public ResponseData queryRenewalInfoByOrderId(OrdOrderRenewal dto, HttpServletRequest request,
			@RequestParam(defaultValue = DEFAULT_PAGE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
		IRequest requestContext = createRequestContext(request);
		return new ResponseData(service.queryRenewalInfoByOrderId(requestContext, dto, page, pageSize));
	}

	/**
	 * 退保表格
	 * 
	 * @param dto
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/fms/ord/after/queryExitGridByOrderId")
	@ResponseBody
	public ResponseData queryExitGridByOrderId(OrdAddition dto, HttpServletRequest request,
			@RequestParam(defaultValue = DEFAULT_PAGE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
		IRequest requestContext = createRequestContext(request);
		return new ResponseData(service.queryOrdAdditionByOrderId(requestContext, dto, page, pageSize));
	}

	/**
	 * 售后跟进记录表格
	 * 
	 * @param dto
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/fms/ord/after/queryFollowGridByAfterId")
	@ResponseBody
	public ResponseData queryFollowGridByAfterId(OrdAfterFollow dto, HttpServletRequest request,
			@RequestParam(defaultValue = DEFAULT_PAGE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
		IRequest requestContext = createRequestContext(request);
		List<OrdAfterFollow> list = ordAfterFollowService.queryByAfterId(requestContext, dto, page, pageSize);
		if (list.size() > 0) {
			for (OrdAfterFollow ordAfterFollow : list) {
				if (ordAfterFollow.getFileId() != null) {
					SysFile sysFile = fileService.selectByPrimaryKey(requestContext, ordAfterFollow.getFileId());
					if (sysFile != null) {
						ordAfterFollow.set_token(sysFile.get_token());
					} else {
						ordAfterFollow.set_token(null);
					}
				}
			}
		}
		return new ResponseData(list);
	}

	/**
	 * 添加售后记录
	 * 
	 * @param request
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "/fms/ord/after/add")
	@ResponseBody
	public ResponseData add(HttpServletRequest request, @RequestBody List<OrdAfter> dto) {
		IRequest requestCtx = createRequestContext(request);
		for (OrdAfter ordAfter : dto) {
			if (ordAfter.getAfterNum() == null || "".equals(ordAfter.getAfterNum())) {
				ordAfter.setAfterNum(sequenceService.getSequence("AFTER"));
			}
			service.insertSelective(requestCtx, ordAfter);
		}
		return new ResponseData(service.batchUpdate(requestCtx, dto));
	}

	/**
	 * 售后跟进页面的提交 -->修改售后跟进记录和保单信息
	 * 
	 * @param request
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "/fms/ord/after/submit")
	@ResponseBody
	public ResponseData submit(HttpServletRequest request, @RequestBody List<OrdAfter> dto) {
		IRequest requestCtx = createRequestContext(request);
		ResponseData responseData = new ResponseData();
		try {
			service.submit(requestCtx, dto);
			responseData.setSuccess(true);
			responseData.setMessage("提交成功");
			responseData.setRows(dto);
		} catch (Exception e) {
			CommonUtil.printStackTraceToStr(e);
			responseData.setSuccess(false);
			responseData.setMessage("提交失败,请检查是否填写跟进记录!");
		}
		return responseData;
	}

	@RequestMapping(value = "/fms/ord/after/exitGridSubmit")
	@ResponseBody
	public ResponseData exitGridSubmit(HttpServletRequest request, @RequestBody List<OrdAddition> dto) {

		return new ResponseData();
	}

	@RequestMapping(value = "/fms/ord/after/remove")
	@ResponseBody
	public ResponseData delete(HttpServletRequest request, @RequestBody List<OrdAfter> dto) {
		service.batchDelete(dto);
		return new ResponseData();
	}

	/**
	 * 查询分配处理人
	 * 
	 * @param request
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "/fms/ord/after/queryHandleUserId")
	@ResponseBody
	public ResponseData queryHandleUserId(HttpServletRequest request, OrdOrder dto) {
		IRequest requestCtx = createRequestContext(request);
		// 用一个集合 返回结果
		List<ClbUser> result = new ArrayList<>();
		
		// 通过订单id 找到对应交易路线上的一级渠道和供应商的合约id
		if (dto.getOrderId() != null && dto.getOrderId() != 0) {
			OrdOrder ordOrder = ordOrderService.selectByPrimaryKey(requestCtx, dto);
			//1. 取当前订单上的渠道
			ClbUser user2 = new ClbUser();
			user2.setUserType("CHANNEL");
			user2.setRelatedPartyId(ordOrder.getChannelId());
			result.addAll(clbUserService.selectAllFields(requestCtx, user2, 1, 1000));
			
			//2. 处理人是渠道合约上维护的售后行政
			// 通过合约id找到售后处理人
			if (ordOrder.getChannelContractId() != null) {
				CnlChannelContract cnlChannelContract = new CnlChannelContract();
				cnlChannelContract.setChannelContractId(ordOrder.getChannelContractId());
				CnlChannelContract key = cnlChannelContractService.selectByPrimaryKey(requestCtx, cnlChannelContract);
				
				ClbUser user = new ClbUser();
				if (key.getPolicyFollow() != null && !"".equals(key.getPolicyFollow())) {
					if (key.getPolicyFollowUserId() != null) {
						user.setUserId(key.getPolicyFollowUserId());
						result.addAll(clbUserService.selectAllFields(requestCtx, user, 1, 1000));
						return new ResponseData(result);
					} else if ("SUPPLIER".equals(key.getPolicyFollow())) { // SUPPLIER供应商
						user.setOwnershipId(key.getPartyId());
						user.setOwnershipType("SUPPLIER");
						// 如果是供应商 就去找员工表中 归属方是该供应商的所有员工
					} else if ("CLB_SUPPLIER".equals(key.getPolicyFollow())) { // CLB_SUPPLIER财联邦
						// user.setOwnershipId(key.getPartyId());
						user.setOwnershipType("CLB_SUPPLIER");
						
					} else if ("CHANNEL".equals(key.getPolicyFollow())) { // CHANNEL渠道
						user.setOwnershipId(key.getChannelId());
						user.setOwnershipType("CHANNEL");
					}
					result.addAll(clbUserService.queryUserByOwnership(requestCtx, user));
				}
			}
		}
		return new ResponseData(result);
	}

	/**
	 * 续保成功之后弹窗修改 续保信息
	 * 售后状态为 成功之后 修改下一年的缴费信息 和 续保表中的下一期的缴费总额
	 * 
	 * @param request
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "/fms/ord/after/updateOrder")
	@ResponseBody
	public ResponseData updateOrder(HttpServletRequest request, @RequestBody List<OrdOrder> dto) {
		IRequest requestCtx = createRequestContext(request);
		service.updateOrder(requestCtx, dto.get(0));
		return new ResponseData(dto);
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(true);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}
}