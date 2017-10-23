package clb.core.order.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.common.utils.CommonUtil;
import clb.core.common.utils.DateUtil;
import clb.core.notification.service.INtnNotificationService;
import clb.core.order.dto.OrdAddition;
import clb.core.order.dto.OrdAfterLog;
import clb.core.order.dto.OrdOrder;
import clb.core.order.dto.OrdOrderRenewal;
import clb.core.order.mapper.OrdOrderRenewalMapper;
import clb.core.order.service.IOrdAdditionService;
import clb.core.order.service.IOrdAfterLogService;
import clb.core.order.service.IOrdAfterService;
import clb.core.order.service.IOrdOrderRenewalService;
import clb.core.order.service.IOrdOrderService;
import clb.core.system.dto.ClbUser;
import clb.core.system.service.IClbUserService;

@Service
@Transactional
public class OrdOrderRenewalServiceImpl extends BaseServiceImpl<OrdOrderRenewal> implements IOrdOrderRenewalService {

	@Autowired
	private OrdOrderRenewalMapper ordOrderRenewalMapper;

	@Autowired
	private IOrdAfterService ordAfterService;

	@Autowired
	private IOrdOrderService ordOrderService;

	@Autowired
	private INtnNotificationService ntnNotificationService;

	@Autowired
	private IClbUserService clbUserService;

	@Autowired
	private IOrdAfterLogService ordAfterLogService;

	@Autowired
	private IOrdAdditionService ordAdditionService;

	/**
	 * 修改续保信息 by orderId
	 */
	@Override
	public void updateRenewalByOrderId(IRequest requestCtx, OrdOrderRenewal dto) {
		ordOrderRenewalMapper.updateRenewalByOrderId(dto);
	}

	/**
	 * 接口查询续保信息 daiqian.shi@hand-china.com
	 *
	 * @param request
	 * @param ordOrderRenewal
	 * @return
	 */
	@Override
	public List<OrdOrderRenewal> queryRenewalByOrder(IRequest request, OrdOrderRenewal ordOrderRenewal, int page,
			int pagesize) {
		PageHelper.startPage(page, pagesize);
		return ordOrderRenewalMapper.queryRenewalByOrder(ordOrderRenewal);
	}

	/**
	 * 接口查询续保信息 daiqian.shi@hand-china.com
	 *
	 * @param request
	 * @param ordOrderRenewal
	 * @return
	 */
	@Override
	public List<OrdOrderRenewal> queryWsRenewal(IRequest request, OrdOrderRenewal ordOrderRenewal) {
		return ordOrderRenewalMapper.queryWsRenewal(ordOrderRenewal);
	}

	/**
	 * 发送短信
	 */
	@Override
	public void sendNotice(IRequest requestCtx, OrdOrderRenewal dto) throws Exception {
		if (dto.getOrderId() != null && dto.getOrderId() != 0) {
			OrdOrder ordOrder = new OrdOrder();
			ordOrder.setOrderId(dto.getOrderId());

			OrdOrder key = ordAfterService.queryOrderByOrdOrderId(requestCtx, ordOrder).get(0);

			ClbUser condition = new ClbUser();
			condition.setRelatedPartyId(key.getChannelId());
			condition.setUserType("CHANNEL");
			List<ClbUser> select = clbUserService.select(requestCtx, condition, 1, 1);
			if (select == null || select.get(0).getUserId() == null) {
				throw new RuntimeException("该渠道没有注册为用户!");
			}
			Long userId = clbUserService.select(requestCtx, condition, 1, 1).get(0).getUserId();

			String templateCode = null;
			Map<String, Object> sysdata = new HashMap<>();
			sysdata.put("item", key.getItem());
			sysdata.put("orderNumber", key.getOrderNumber());
			sysdata.put("applicant", key.getApplicant());
			sysdata.put("insurant", key.getInsurant());
			sysdata.put("renewalDueDate", key.getRenewalDueDate().toLocaleString());

			// Long gracePeriod = key.getGracePeriod();
			// Long administrativePeriod = key.getAdministrativePeriod();

			// 宽限期
			// Long gracePeriod = ordOrderRenewal.getGracePeriod();
			Long gracePeriod = null;
			if (key.getGraceDate() != null) {
				gracePeriod = DateUtil.getDateDiff2(key.getGraceDate(), key.getRenewalDueDate());
			}
			// 行政期
			// Long administrativePeriod = ordOrderRenewal.getAdministrativePeriod();
			Long administrativePeriod = null;
			if (key.getAdministrativeDate() != null) {
				administrativePeriod = DateUtil.getDateDiff2(key.getAdministrativeDate(), key.getRenewalDueDate());
			}

			// 待续保
			if ("TORENEWAL".equals(dto.getRenewalStatus())) {
				templateCode = "XB0001";
				sysdata.put("diffDay", DateUtil.getDateDiff(key.getRenewalDueDate(), new Date()));
				// 宽限期
			} else if ("NACHFRIST".equals(dto.getRenewalStatus())) {
				templateCode = "XB0002";
				sysdata.put("diffDay", gracePeriod - DateUtil.getDateDiff(new Date(), key.getRenewalDueDate()));
				// 行政期
			} else if ("ADMIN".equals(dto.getRenewalStatus())) {
				templateCode = "XB0003";
				sysdata.put("diffDay",
						administrativePeriod - DateUtil.getDateDiff(new Date(), key.getRenewalDueDate()));
				// 待确认失效
			} else if ("TOFAIL".equals(dto.getRenewalStatus())) {
				templateCode = "XB0004";
			}
			ntnNotificationService.sendNotification(requestCtx, userId, templateCode, sysdata);

			ordOrder.setSmsFlag("Y");
			ordOrderService.updateByPrimaryKeySelective(requestCtx, ordOrder);

			OrdAfterLog ordAfterLog = new OrdAfterLog();
			ordAfterLog.setGeneralId(dto.getRenewalId());
			ordAfterLog.setIdType("RENEWAL");
			ordAfterLog.setComment("发送短信");
			ordAfterLog.setStatus(dto.getRenewalStatus());
			ordAfterLog.setStatusDate(new Date());
			ordAfterLog.setObjectVersionNumber(1L);
			ordAfterLogService.insertSelective(requestCtx, ordAfterLog);
		}
	}

	@Override
	public void updateStatusByOrderIds(IRequest requestCtx, List<Long> orderIds) {
		// 将保单(主险)和附加险的状态改为保单失效 EXPIRED 在续保表添加标识字段 标识保单状态为失效
		for (Long orderId : orderIds) {
			// 保单
			OrdOrder order = new OrdOrder();
			order.setOrderId(orderId);
			order.setStatus("EXPIRED");
			ordOrderService.updateByPrimaryKeySelective(requestCtx, order);
			// 附加险
			OrdAddition addition = new OrdAddition();
			addition.setOrderId(orderId);
			addition.setStatus("EXPIRED");
			ordAdditionService.updateStatusByOrderId(addition);

			// 修改续保的取消标识符
			OrdOrderRenewal orderRenewal = new OrdOrderRenewal();
			orderRenewal.setOrderId(orderId);
			orderRenewal.setCancelRenewalFlag(1L);
			updateFlagByOrderId(requestCtx, orderRenewal);

		}
	}

	/**
	 * 修改续保的标识符
	 */
	@Override
	public void updateFlagByOrderId(IRequest requestCtx, OrdOrderRenewal dto) {
		ordOrderRenewalMapper.updateFlagByOrderId(dto);

	}

}