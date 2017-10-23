package clb.core.order.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import clb.core.system.service.IClbCodeService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.common.utils.CommonUtil;
import clb.core.common.utils.DateUtil;
import clb.core.notification.service.INtnNotificationService;
import clb.core.order.dto.OrdOrder;
import clb.core.order.dto.OrdStatusHis;
import clb.core.order.dto.OrdTeamVisitor;
import clb.core.order.mapper.OrdOrderMapper;
import clb.core.order.service.IOrdOrderService;
import clb.core.order.service.IOrdStatusHisService;
import clb.core.order.service.IOrdTeamVisitorService;
import clb.core.order.service.IOrdValueAddService;
import clb.core.payment.dto.PaymentOrder;
import clb.core.payment.service.IPaymentOrderService;
import clb.core.production.dto.PrdItems;
import clb.core.production.service.IPrdItemsService;
import clb.core.system.dto.ClbUser;
import clb.core.system.mapper.ClbUserMapper;

@Service
@Transactional
public class OrdValueAddServiceImpl extends BaseServiceImpl<OrdOrder> implements IOrdValueAddService {

	@Autowired
	private IOrdOrderService ordOrderService;

	@Autowired
	private IOrdTeamVisitorService ordTeamVisitorService;

	@Autowired
	private IOrdStatusHisService ordStatusHisService;

	@Autowired
	private IPrdItemsService prdItemsService;

	@Autowired
	private IPaymentOrderService paymentOrderService;

	@Autowired
	private INtnNotificationService ntnNotificationService;

	@Autowired
	private OrdOrderMapper ordOrderMapper;

	@Autowired
	private ClbUserMapper clbUserMapper;

	@Autowired
	private IClbCodeService clbCodeService;

	/**
	 * 订单增值服务列表
	 */
	@Override
	public List<OrdOrder> queryValueAddByCondition(IRequest requestContext, OrdOrder dto, int page, int pageSize) {
		// PageHelper.startPage(page, pageSize);
		// 设置查询的订单种类为增值服务
		dto.setOrderType("VALUEADD");
		List<OrdOrder> list = ordOrderService.queryWsOrder(requestContext, dto, page, pageSize);
		for (OrdOrder ordOrder : list) {
			if ("WAIT_PAY".equals(ordOrder.getStatus())) {
				PaymentOrder paymentOrder = paymentOrderService.queryBySource(requestContext, "ORDER",
						String.valueOf(ordOrder.getOrderId()));
				if (paymentOrder.getPaymentId() != null) {
					// 酒店服务要在30分钟之内付款 其他服务要在2小时内付款
					Date date = new Date(
							paymentOrder.getCreationDate().getTime() + paymentOrder.getPayLimitTime() * 60 * 1000);
					// 倒计时
					Double time = paymentOrder.getCreationDate().getTime() * 0.001 + paymentOrder.getPayLimitTime() * 60
							- new Date().getTime() * 0.001;
					time = Math.floor(time);
					ordOrder.setValueAddOrderPayTimes(new Double(time).longValue());
					// ordOrder.setValueAddOrderPayTime(date);
					if (new Date().getTime() > date.getTime()) {
						ordOrder.setValueAddOrderPayTime(null);
						// 查询的时候 发现超时 修改状态
						updateStatusByOrderId(requestContext, ordOrder);
						ordOrder.setStatus("RESERVE_CANCELLED");
					} else {
						ordOrder.setValueAddOrderPayTime(date);
					}
					/*
					 * //如果超时 修改订单的状态为 取消预约 并添加状态到日志表 if (new Date().getTime() > date.getTime()) {
					 * //修改订单的状态为 取消预约 OrdOrder order = new OrdOrder();
					 * order.setOrderId(ordOrder.getOrderId());
					 * order.setStatus("RESERVE_CANCELLED");
					 * ordOrderService.updateByPrimaryKeySelective(requestContext, order);
					 * 
					 * //添加状态到日志表 OrdStatusHis ordStatusHis = new OrdStatusHis();
					 * ordStatusHis.setOrderId(ordOrder.getOrderId());
					 * ordStatusHis.setStatus("RESERVE_CANCELLED"); ordStatusHis.setStatusDate(new
					 * Date()); ordStatusHis.setDescription("您的预约由于超时未支付，已自动关闭!");
					 * ordStatusHisService.insertSelective(requestContext, ordStatusHis);
					 * 
					 * ordOrder.setValueAddOrderPayTime(null); } else {
					 * ordOrder.setValueAddOrderPayTime(date); }
					 */
				}
			}
		}

		return list;
	}

	/**
	 * L团签旅客信息
	 */
	@Override
	public List<OrdTeamVisitor> queryOrdTeamVisitor(IRequest requestContext, OrdTeamVisitor dto, int page,
			int pageSize) {
		PageHelper.startPage(page, pageSize);
		return ordTeamVisitorService.queryWsOrdTeamVisitor(requestContext, dto);
	}

	/****
	 * 根据用户类型，以及对应渠道/供应商 去查询用户
	 * 
	 * @param userType
	 * @param paramId
	 * @return
	 */
	public List<ClbUser> queryUserByParam(String userType, Long paramId) {
		ClbUser user = new ClbUser();
		user.setUserType(userType);
		user.setRelatedPartyId(paramId);
		return clbUserMapper.select(user);
	}

	/**
	 * 修改状态
	 */
	@Override
	public void updateStatus(IRequest requestContext, OrdOrder ordOrder, String ip) throws Exception {
		try {
			OrdOrder order = new OrdOrder();

			// 状态变更 要添加到日志表
			OrdStatusHis ordStatusHis = new OrdStatusHis();
			ordStatusHis.setOrderId(ordOrder.getOrderId());
			ordStatusHis.setStatus(ordOrder.getStatus());
			ordStatusHis.setStatusDate(new Date());

			// 修改订单状态(新建对象 防止前端 瞎改状态提交)
			order.setOrderId(ordOrder.getOrderId());
			order.setStatus(ordOrder.getStatus());
			// 待支付
			if ("WAIT_PAY".equals(ordOrder.getStatus())) {
				// 支付金额不能小于0
				if (Double.parseDouble(ordOrder.getPrice()) <= 0) {
					throw new RuntimeException();
				}
				order.setPrice(ordOrder.getPrice());
				if ("ZCFW".equals(ordOrder.getValueaddType())) {
					order.setVehicleType(ordOrder.getVehicleType());
				}
				ordStatusHis.setDescription("请在规定时间内完成支付,超时订单将自动关闭!");
				// 预约成功
			} else if ("RESERVE_SUCCESS".equals(ordOrder.getStatus())) {
				ordStatusHis.setDescription("您的订单已经预约成功,请仔细核对相关信息!");
				// 需复查
			} else if ("NEED_REVIEW".equals(ordOrder.getStatus())) {
				ordStatusHis.setDescription("资料有误,原因为:" + ordOrder.getSublineItemName() + "请更改后再重新提交");
				// 预约资料审核中
			} else if ("DATA_APPROVING".equals(ordOrder.getStatus())) {
				ordStatusHis.setDescription("您的资料已提交，请耐心等待审核!");
				// 取消预约
			} else if ("RESERVE_CANCELLED".equals(ordOrder.getStatus())) {
				ordStatusHis.setDescription("您的预约已成功取消!");
			} else {
				ordStatusHis.setDescription("订单状态不存在,请联系管理员--110!");
			}
			// 添加日志
			ordStatusHisService.insertSelective(requestContext, ordStatusHis);
			// 修改状态
			ordOrderService.updateByPrimaryKeySelective(requestContext, order);

			if ("WAIT_PAY".equals(ordOrder.getStatus()) && Double.parseDouble(ordOrder.getPrice()) != 0) {
				// 判断该订单是否存在
				PaymentOrder payOrder = paymentOrderService.queryBySource(requestContext, "ORDER",
						String.valueOf(ordOrder.getOrderId()));
				if (payOrder.getPaymentId() == null) {
					PaymentOrder paymentOrder = new PaymentOrder();
					paymentOrder.setSourceId(String.valueOf(ordOrder.getOrderId()));
					paymentOrder.setSourceType("ORDER");
					payOrder.setPaymentType("ALIPAY");
					paymentOrder.setOrderContent("增值服务订单");
					paymentOrder.setOrderSubject("增值服务订单");
					paymentOrder.setAmount(Double.parseDouble(ordOrder.getPrice()));

					paymentOrderService.createOrder(requestContext, paymentOrder, ip);
				} else {
					payOrder.setAmount(Double.parseDouble(ordOrder.getPrice()));
					paymentOrderService.saveOrUpdateOrder(requestContext, payOrder);
				}
			}

			// 状态修改发送通知 add by zhaojun 20170731
			OrdOrder ordOrder1 = new OrdOrder();
			ordOrder1.setOrderId(ordOrder.getOrderId());
			List<OrdOrder> oList = ordOrderMapper.queryWsOrder(ordOrder1);
			ordOrder1 = oList.get(0);
			Map sendNoticeMap = new HashMap();
			sendNoticeMap.put("item", ordOrder1.getNoticeItem());
			sendNoticeMap.put("insurant", ordOrder1.getInsurant());
			sendNoticeMap.put("applicant", ordOrder1.getApplicant());
			sendNoticeMap.put("policyNumber", ordOrder1.getPolicyNumber());
			sendNoticeMap.put("orderNumber", ordOrder1.getOrderNumber());
			sendNoticeMap.put("reserveDate",DateUtil.getChineseDateString(ordOrder1.getReserveDate()));
			sendNoticeMap.put("productSupplierName", ordOrder1.getProductSupplierName());
			sendNoticeMap.put("issueDate",DateUtil.getChineseDateString(ordOrder1.getIssueDate()));
			sendNoticeMap.put("dividendPeriod", ordOrder1.getDividendPeriod());

			if (ordOrder1.getReserveDate() != null) {
				sendNoticeMap.put("reserveDateStr", DateUtil.getDateStr(ordOrder1.getReserveDate(), "yyyy-MM-dd HH:mm"));
			}
			if (ordOrder1.getEffectiveDate() != null) {
				sendNoticeMap.put("effectiveDate", DateUtil.getDateStr(ordOrder1.getEffectiveDate(), "yyyy-MM-dd"));
			}
			if (ordOrder1.getRenewalDueDate() != null) {
				sendNoticeMap.put("renewalDueDate", DateUtil.getDateStr(ordOrder1.getRenewalDueDate(), "yyyy-MM-dd"));
				sendNoticeMap.put("renewalDueDay", DateUtil.getDateDiff(ordOrder1.getRenewalDueDate(), new Date()));
			}

			sendNoticeMap.put("policyAmount",ordOrder1.getPolicyAmount());

			String valueAddType=clbCodeService.getCodeMeaningByValue(requestContext, "ORD.VALUE_ADD_TYPE", ordOrder1.getValueaddType());
			sendNoticeMap.put("valueAddType",valueAddType);
			sendNoticeMap.put("valueAddNumber",valueAddType + ordOrder1.getOrderNumber());
			sendNoticeMap.put("curDate",DateUtil.getDateStr(new Date(), "yyyy-MM-dd HH:mm:ss"));

			Long userId = null;
			List<ClbUser> userList = queryUserByParam("CHANNEL", ordOrder1.getChannelId());
			if (CollectionUtils.isEmpty(userList)) {
				throw new RuntimeException("查询不到渠道用户,渠道id:" + ordOrder1.getChannelId());
			} else {
				userId = userList.get(0).getUserId();
			}

			if ("DATA_APPROVING".equals(ordOrder.getStatus())) {
				ntnNotificationService.sendNotification(requestContext, userId, "ZZFW0001", sendNoticeMap);
			} else if ("NEED_REVIEW".equals(ordOrder.getStatus())) {
				ntnNotificationService.sendNotification(requestContext, userId, "ZZFW0002", sendNoticeMap);
			} else if ("RESERVE_CANCELLED".equals(ordOrder.getStatus())) {
				ntnNotificationService.sendNotification(requestContext, userId, "ZZFW0003", sendNoticeMap);
			} else if ("WAIT_PAY".equals(ordOrder.getStatus())) {
				if ("JDYD".equals(ordOrder.getValueaddType())) {
					ntnNotificationService.sendNotification(requestContext, userId, "ZZFW0004", sendNoticeMap);
				} else {
					ntnNotificationService.sendNotification(requestContext, userId, "ZZFW0006", sendNoticeMap);
				}
			} else if ("RESERVE_SUCCESS".equals(ordOrder.getStatus())) {
				ntnNotificationService.sendNotification(requestContext, userId, "ZZFW0005", sendNoticeMap);
			}

		} catch (Exception e) {
			CommonUtil.printStackTraceToStr(e);
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * 查询子产品
	 */
	@Override
	public List<PrdItems> querySublineProductName(IRequest requestContext, PrdItems dto) {
		return prdItemsService.wsSelectAlive(dto, 1, 3000);
	}

	/**
	 * 订单超时支付修改状态 添加日志
	 */
	@Override
	public void updateStatusByOrderId(IRequest requestContext, OrdOrder dto) {
		// 修改订单的状态为 取消预约
		dto.setStatus("RESERVE_CANCELLED");
		ordOrderService.updateByPrimaryKeySelective(requestContext, dto);

		PaymentOrder paymentOrder = paymentOrderService.queryBySource(requestContext, "ORDER",
				String.valueOf(dto.getOrderId()));
		 Date date = new Date(paymentOrder.getCreationDate().getTime() + paymentOrder.getPayLimitTime() * 60 * 1000);
		// 添加状态到日志表
		OrdStatusHis ordStatusHis = new OrdStatusHis();
		ordStatusHis.setOrderId(dto.getOrderId());
		ordStatusHis.setStatus("RESERVE_CANCELLED");
		ordStatusHis.setStatusDate(date);
		ordStatusHis.setDescription("您的预约由于超时未支付，已自动关闭!");
		ordStatusHisService.insertSelective(requestContext, ordStatusHis);

	}

}