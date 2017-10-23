package clb.core.order.service.impl;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.github.pagehelper.PageHelper;
import com.hand.hap.attachment.service.ISysFileService;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.channel.service.ICnlChannelService;
import clb.core.common.service.ISequenceService;
import clb.core.common.utils.CommonUtil;
import clb.core.common.utils.DateUtil;
import clb.core.notification.service.INtnNotificationService;
import clb.core.order.dto.OrdAddition;
import clb.core.order.dto.OrdAfter;
import clb.core.order.dto.OrdAfterFollow;
import clb.core.order.dto.OrdAfterLog;
import clb.core.order.dto.OrdOrder;
import clb.core.order.dto.OrdOrderRenewal;
import clb.core.order.dto.OrdTemplate;
import clb.core.order.dto.OrdTradeRoute;
import clb.core.order.mapper.OrdAfterMapper;
import clb.core.order.mapper.OrdTradeRouteMapper;
import clb.core.order.service.IOrdAdditionService;
import clb.core.order.service.IOrdAfterFollowService;
import clb.core.order.service.IOrdAfterLogService;
import clb.core.order.service.IOrdAfterService;
import clb.core.order.service.IOrdOrderRenewalService;
import clb.core.order.service.IOrdOrderService;
import clb.core.order.service.IOrdTemplateService;
import clb.core.order.service.IOrdTradeRouteService;
import clb.core.system.dto.ClbUser;
import clb.core.system.service.IClbUserService;

@Service
@Transactional
public class OrdAfterServiceImpl extends BaseServiceImpl<OrdAfter> implements IOrdAfterService {

	@Autowired
	private OrdAfterMapper ordAfterMapper;

	@Autowired
	private ISysFileService fileService;

	@Autowired
	private IOrdOrderService ordOrderService;

	@Autowired
	private ISequenceService sequenceService;

	@Autowired
	private IOrdAfterFollowService ordAfterFollowService;

	@Autowired
	private IOrdOrderService ordOrderservice;

	@Autowired
	private IOrdAfterLogService ordAfterLogService;

	@Autowired
	private IOrdOrderRenewalService ordOrderRenewalService;

	@Autowired
	private IOrdAdditionService ordAdditionService;

	@Autowired
	private IOrdTemplateService ordTemplateService;

	@Autowired
	private INtnNotificationService ntnNotificationService;

	@Autowired
	private IClbUserService clbUserService;

	@Autowired
	private OrdTradeRouteMapper ordTradeRouteMapper;

	@Autowired
	private IOrdTradeRouteService ordTradeRouteService;

	/**
	 * 订单售后汇总查询接口
	 */
	@Override
	public List<OrdAfter> queryWSOrdAfterList(IRequest requestContext, OrdAfter dto, int page, int pageSize) {
		PageHelper.startPage(page, pageSize);
		return ordAfterMapper.queryWSOrdAfterList(dto);
	}

	/**
	 * 订单售后汇总查询接口
	 */
	@Override
	public List<OrdAfter> queryOrdAfterList(IRequest requestContext, OrdAfter dto, int page, int pageSize) {
		PageHelper.startPage(page, pageSize);
		List<OrdAfter> list = ordAfterMapper.queryOrdAfterList(dto);
		if (!CollectionUtils.isEmpty(list)) {
			for (OrdAfter ordAfter : list) {
				// 对接业务行政
				OrdTradeRoute preUser = new OrdTradeRoute();
				preUser.setOrderId(ordAfter.getOrderId());
				List<OrdTradeRoute> preUserList = ordTradeRouteMapper.queryPreUserId(preUser);
				if (!CollectionUtils.isEmpty(preUserList)) {
					ordAfter.setPreName(preUserList.get(0).getPreName());
				}
				// 所属一级
				if (ordAfter.getOrderId() != null) {
					ordAfter.setStairSell(
							ordTradeRouteService.queryStairSellByOrderId(requestContext, ordAfter.getOrderId()));
				}
			}
		}
		return list;
	}

	/**
	 * 订单售后汇总查询接口
	 */
	@Override
	public Long queryOrdAfterListTotal(IRequest requestContext, OrdAfter dto) {
		return ordAfterMapper.queryOrdAfterListTotal(dto);
	}

	/**
	 * 续保详情页面的续保表格
	 */
	@Override
	public List<OrdOrderRenewal> queryRenewalByOrderId(IRequest requestCtx, OrdOrderRenewal dto, int page,
			int pageSize) {
		PageHelper.startPage(page, pageSize);

		List<OrdOrderRenewal> renewalByOrderId = ordAfterMapper.queryRenewalByOrderId(dto);
		List<OrdOrderRenewal> renewalByOrderId1 = new ArrayList<>();

		Long orderId = dto.getOrderId();
		if (orderId != null) {
			OrdAddition ordAddition = new OrdAddition();
			ordAddition.setOrderId(orderId);
			Long payPeriods = null;
			try {
				List<OrdAddition> additions = ordAfterMapper.queryOrdAdditionByOrderId(ordAddition);

				if (renewalByOrderId != null && renewalByOrderId.size() > 0 && additions != null
						&& additions.size() > 0) {
					for (OrdOrderRenewal ordOrderRenewal : renewalByOrderId) {
						for (OrdAddition ordAddition2 : additions) {
							if (ordAddition2.getItemId() == ordOrderRenewal.getItemId()) {
								// 判断是否是主险
								if ("ORDER".equalsIgnoreCase(ordAddition2.getSource())) {
									// 得到主险的缴费期数+1
									payPeriods = ordAddition2.getPayPeriods() + 1;
									// 设置主险的数据到对应的集合对象中
									// 设置预计续保日
									Class<?> clz = ordOrderRenewal.getClass();
									OrdOrderRenewal o = (OrdOrderRenewal) clz.newInstance();
									o = ordOrderRenewal;
									Method m1 = clz.getMethod("setCurrency" + payPeriods, String.class);
									Method m2 = clz.getMethod("setRenewalDate" + payPeriods, java.util.Date.class);

									m1.invoke(o, ordAddition2.getCurrency());
									m2.invoke(o, ordAddition2.getRenewalDueDate());

									renewalByOrderId1.add(o);
								}
							}
						}
					}

					for (OrdOrderRenewal ordOrderRenewal : renewalByOrderId) {
						for (OrdAddition ordAddition2 : additions) {
							if (ordAddition2.getItemId() == ordOrderRenewal.getItemId()) {
								// 判断是否是主险
								if ("ADDITION".equalsIgnoreCase(ordAddition2.getAttribute1())
										&& !"TOTAL".equalsIgnoreCase(ordOrderRenewal.getType())) {

									Class<?> clz = ordOrderRenewal.getClass();
									OrdOrderRenewal o = (OrdOrderRenewal) clz.newInstance();
									o = ordOrderRenewal;
									// 设置预计续保日和币种
									Method m1 = clz.getMethod("setCurrency" + payPeriods, String.class);
									Method m2 = clz.getMethod("setRenewalDate" + payPeriods, java.util.Date.class);

									m1.invoke(o, ordAddition2.getCurrency());
									m2.invoke(o, ordAddition2.getRenewalDueDate());
									renewalByOrderId1.add(o);
								}
							}
						}
					}

					for (OrdOrderRenewal ordOrderRenewal : renewalByOrderId) {
						for (OrdAddition ordAddition2 : additions) {
							if ("TOTAL".equalsIgnoreCase(ordOrderRenewal.getType())) {
								// 设置总结金额
								Class<?> clz = ordOrderRenewal.getClass();
								OrdOrderRenewal o1 = (OrdOrderRenewal) clz.newInstance();
								o1 = ordOrderRenewal;
								Method m3 = clz.getMethod("setTotalAmount" + payPeriods, java.math.BigDecimal.class);
								m3.invoke(o1, ordAddition2.getNextPolicyAmount());
								renewalByOrderId1.add(o1);
								break;
							}
						}
					}
				}
			} catch (Exception e) {
				CommonUtil.printStackTraceToStr(e);
			}
		}
		return renewalByOrderId;
	}

	/**
	 * 续保列表查询
	 */
	@Override
	public List<OrdOrderRenewal> queryWSOrdRenewalList(IRequest requestCtx, OrdOrderRenewal dto, int page,
			int pageSize) {
		PageHelper.startPage(page, pageSize);
		List<OrdOrderRenewal> list = ordAfterMapper.queryWSOrdRenewalList(dto);
		// 给需要展示的字段赋值 转换当前续保单的状态
		if (!CollectionUtils.isEmpty(list)) {
			for (OrdOrderRenewal ordOrderRenewal : list) {
				Long payPeriods = ordOrderRenewal.getPayPeriods() + 1;
				// 续保到期日
				Date renewalDueDate = ordOrderRenewal.getRenewalDueDate();
				int days = (int) ((new Date().getTime() - renewalDueDate.getTime()) / (1000 * 3600 * 24));

				// 宽限期和行政期 发送短信 以订单上面的信息为准
				// 宽限期
				// Long gracePeriod = ordOrderRenewal.getGracePeriod();
				Long gracePeriod = null;
				if (ordOrderRenewal.getGraceDate() != null) {
					gracePeriod = DateUtil.getDateDiff2(ordOrderRenewal.getGraceDate(), renewalDueDate);
				}
				// 行政期
				// Long administrativePeriod = ordOrderRenewal.getAdministrativePeriod();
				Long administrativePeriod = null;
				if (ordOrderRenewal.getAdministrativeDate() != null) {
					administrativePeriod = DateUtil.getDateDiff2(ordOrderRenewal.getAdministrativeDate(),
							renewalDueDate);
				}

				OrdOrderRenewal renewal = new OrdOrderRenewal();
				renewal.setOrderId(ordOrderRenewal.getOrderId());

				// 前端只有待续保 和 待确认失效 2种状态
				if (days < 0) { // 待续保(系统默认为待续保)
					ordOrderRenewal.setRenewalStatus("TORENEWAL");
					renewal.setRenewalStatus("TORENEWAL");

				} else if (gracePeriod != null && gracePeriod != 0 && days >= 0 && days <= gracePeriod) { // 宽限期
					ordOrderRenewal.setRenewalStatus("NACHFRIST");
					renewal.setRenewalStatus("TOFAIL");

				} else if (gracePeriod != null && administrativePeriod != null && days > gracePeriod
						&& days <= administrativePeriod) { // 行政期
					ordOrderRenewal.setRenewalStatus("ADMIN");
					renewal.setRenewalStatus("TOFAIL");

				} else { // 待确认失效 ordOrderRenewal.setRenewalStatus("TOFAIL");
					ordOrderRenewal.setRenewalStatus("TOFAIL");
					renewal.setRenewalStatus("TOFAIL");
				}
				// 修改附加险的状态和当前期数下的续保表格的状态 (缴费期数上面已经加1)
				renewal.setRenewalStatusColumn("RENEWAL_STATUS" + payPeriods);
				renewal.setRenewalDateColumn("RENEWAL_DATE" + payPeriods);
				renewal.setRenewalDate(ordOrderRenewal.getRenewalDueDate());
				ordOrderRenewalService.updateRenewalByOrderId(requestCtx, renewal);
			}
		}
		return list;
	}

	/**
	 * 续保列表查询
	 */
	@Override
	public List<OrdOrderRenewal> queryOrdRenewalList(IRequest requestCtx, OrdOrderRenewal dto, int page, int pageSize) {
		PageHelper.startPage(page, pageSize);
		List<OrdOrderRenewal> list = ordAfterMapper.queryOrdRenewalList(dto);
		// 给需要展示的字段赋值 转换当前续保单的状态
		if (!CollectionUtils.isEmpty(list)) {
			for (OrdOrderRenewal ordOrderRenewal : list) {

				Long payPeriods = ordOrderRenewal.getPayPeriods() + 1;
				// 续保到期日
				Date renewalDueDate = ordOrderRenewal.getRenewalDueDate();
				int days = (int) ((new Date().getTime() - renewalDueDate.getTime()) / (1000 * 3600 * 24));

				// 宽限期和行政期 发送短信 以订单上面的信息为准
				// 宽限期
				Long gracePeriod = null;
				if (ordOrderRenewal.getGraceDate() != null) {
					gracePeriod = DateUtil.getDateDiff2(ordOrderRenewal.getGraceDate(), renewalDueDate);
				}
				// 行政期
				Long administrativePeriod = null;
				if (ordOrderRenewal.getAdministrativeDate() != null) {
					administrativePeriod = DateUtil.getDateDiff2(ordOrderRenewal.getAdministrativeDate(),
							renewalDueDate);
				}

				OrdOrderRenewal renewal = new OrdOrderRenewal();
				renewal.setOrderId(ordOrderRenewal.getOrderId());

				if (days < 0) { // 待续保(系统默认为待续保)
					ordOrderRenewal.setRenewalStatus("TORENEWAL");
					renewal.setRenewalStatus("TORENEWAL");

				} else if (gracePeriod != null && gracePeriod != 0 && days >= 0 && days <= gracePeriod) { // 宽限期
					ordOrderRenewal.setRenewalStatus("NACHFRIST");
					renewal.setRenewalStatus("NACHFRIST");

				} else if (gracePeriod != null && administrativePeriod != null && days > gracePeriod
						&& days <= administrativePeriod) { // 行政期
					ordOrderRenewal.setRenewalStatus("ADMIN");
					renewal.setRenewalStatus("ADMIN");

				} else { // 待确认失效 ordOrderRenewal.setRenewalStatus("TOFAIL");
					ordOrderRenewal.setRenewalStatus("TOFAIL");
					renewal.setRenewalStatus("TOFAIL");
				}

				// 修改附加险的状态和当前期数下的续保表格的状态
				renewal.setRenewalStatusColumn("RENEWAL_STATUS" + payPeriods);
				renewal.setRenewalDateColumn("RENEWAL_DATE" + payPeriods);
				renewal.setRenewalDate(ordOrderRenewal.getRenewalDueDate());
				ordOrderRenewalService.updateRenewalByOrderId(requestCtx, renewal);

				// 对接业务行政
				OrdTradeRoute preUser = new OrdTradeRoute();
				preUser.setOrderId(ordOrderRenewal.getOrderId());
				List<OrdTradeRoute> preUserList = ordTradeRouteMapper.queryPreUserId(preUser);
				if (!CollectionUtils.isEmpty(preUserList)) {
					ordOrderRenewal.setPreName(preUserList.get(0).getPreName());
				}
				// 所属一级
				if (ordOrderRenewal.getOrderId() != null) {
					ordOrderRenewal.setStairSell(
							ordTradeRouteService.queryStairSellByOrderId(requestCtx, ordOrderRenewal.getOrderId()));
				}
			}
		}
		return list;
	}

	/**
	 * 通过订单id或者保单编号查询 订单详情
	 */
	@Override
	public List<OrdOrder> queryOrdOrderByOrderId(IRequest requestCtx, OrdOrder dto) {
		return ordAfterMapper.queryOrdOrderByOrderId(dto);
	}

	/**
	 * 查询退保表格 by orderId
	 */
	@Override
	public List<OrdAddition> queryOrdAdditionByOrderId(IRequest requestCtx, OrdAddition ordAddition, int page,
			int pageSize) {
		PageHelper.startPage(page, pageSize);
		return ordAfterMapper.queryOrdAdditionByOrderId(ordAddition);
	}

	/**
	 * 售后跟进=>续保表格
	 */
	@Override
	public List<OrdOrderRenewal> queryRenewalInfoByOrderId(IRequest requestCtx, OrdOrderRenewal dto, int page,
			int pageSize) {
		PageHelper.startPage(page, pageSize);
		// 查询下一期的续保信息
		OrdOrder order = new OrdOrder();
		order.setOrderId(dto.getOrderId());
		OrdOrder key = ordOrderService.selectByPrimaryKey(requestCtx, order);
		dto.setRenewalStatusColumn("RENEWAL_STATUS" + (key.getPayPeriods() + 1));
		dto.setCurrencyColumn("CURRENCY" + (key.getPayPeriods() + 1));
		dto.setRenewalDateColumn("RENEWAL_DATE" + (key.getPayPeriods() + 1));
		List<OrdOrderRenewal> list = ordAfterMapper.queryRenewalInfoByOrderId(dto);
		if (!CollectionUtils.isEmpty(list)) {
			for (OrdOrderRenewal ordOrderRenewal : list) {
				ordOrderRenewal.setPayPeriods(ordOrderRenewal.getPayPeriods() + 1);
			}
		}
		return list;
	}

	/**
	 * 续保信息/订单详情 通过订单id查询
	 */
	@Override
	public List<OrdOrder> queryOrderByOrdOrderId(IRequest requestCtx, OrdOrder dto) {
		return ordAfterMapper.queryOrderByOrdOrderId(dto);
	}

	/**
	 * 新建售后 ---->提交
	 */
	@Override
	public List<OrdAfter> ordAfterApplication(IRequest requestContext, OrdAfter dto) throws Exception {

		List<OrdAfter> list = new ArrayList<>();

		// 生成售后编号
		if (dto.getAfterNum() == null || "".equals(dto.getAfterNum())) {
			dto.setAfterNum(sequenceService.getSequence("AFTER"));
		}

		// 设置默认的售后状态 为资料审核中
		dto.setAfterStatus("TOAUDIT");
		dto.setCreationDate(new Date());

		OrdAfter insert = insertSelective(requestContext, dto);
		// 发送短信 需要售后类型
		OrdTemplate ordTemplate = new OrdTemplate();
		ordTemplate.setTemplateId(insert.getTemplateId());
		OrdTemplate template = ordTemplateService.selectByPrimaryKey(requestContext, ordTemplate);
		insert.setAfterType(template.getApplyItem());
		sendNotice(requestContext, insert);

		// 退保信息 售后项目是退保的时候
		if ("EXIT".equals(template.getApplyClassCode())) {
			List<OrdAddition> ordOrderExit = dto.getOrdOrderExit();
			if (ordOrderExit != null && ordOrderExit.size() > 0) {
				for (OrdAddition ordAddition : ordOrderExit) {
					String source = ordAddition.getSource();
					if ("ORDER".equalsIgnoreCase(source)) {
						OrdOrder ord = new OrdOrder();
						ord.setOrderId(ordAddition.getOrderId());
						if ("Y".equalsIgnoreCase(ordAddition.getSurrenderFlag())) {
							// 修改订单显示状态 为退保中
							ord.setSaveStatus("SURRENDERING");
							ord.setSurrenderFlag("Y");
						} else {
							ord.setSaveStatus(ordAddition.getStatus());
							ord.setSurrenderFlag("N");
						}

						ordOrderService.updateByPrimaryKeySelective(requestContext, ord);
					} else {
						if ("Y".equalsIgnoreCase(ordAddition.getSurrenderFlag())) {
							// 修改订单显示状态 为退保中
							ordAddition.setSaveStatus("SURRENDERING");
							ordAddition.setSurrenderFlag("Y");
							// 修改附加险的退保标识
						} else {
							ordAddition.setSaveStatus(ordAddition.getStatus());
							ordAddition.setSurrenderFlag("N");
						}

						ordAdditionService.updateOrdAdditionByOrderIdAndItemId(ordAddition);
					}
				}
			}
		}
		// 保单复效 将保单状态改回 保单生效
		if ("REGAIN".equals(template.getApplyClassCode())) {
			OrdOrder ord = new OrdOrder();
			ord.setOrderId(dto.getOrderId());
			ord.setStatus("POLICY_EFFECTIVE");

			ordOrderService.updateByPrimaryKeySelective(requestContext, ord);
		}
		// 如果售后项目是续保 申请续保的订单不会出现在续保清单里面
		if ("RENEWAL".equals(template.getApplyClassCode())) {
			OrdOrder order = new OrdOrder();
			order.setOrderId(dto.getOrderId());
			order.setRenewalFlag("Y");
			ordOrderservice.updateByPrimaryKeySelective(requestContext, order);
		}
		// 数据写入售后表成功后 将状态写入日志表
		OrdAfterLog ordAfterLog = new OrdAfterLog();
		ordAfterLog.setGeneralId(insert.getAfterId());
		ordAfterLog.setIdType("AFTER");
		ordAfterLog.setStatusDate(new Date());
		ordAfterLog.setStatus("TOAUDIT");// 默认的状态是资料审核中

		ordAfterLogService.insertSelective(requestContext, ordAfterLog);

		// 新增售后跟进记录 抽取成方法
		dto.setAfterId(insert.getAfterId());
		saveAfterFollow(requestContext, dto);
		list.add(insert);
		return list;
	}

	@Override
	public List<OrdAfter> queryOrderInfoByAfterId(IRequest requestContext, OrdAfter dto) {
		List<OrdAfter> list = ordAfterMapper.queryOrderInfoByAfterId(dto);
		// 对接业务行政
		OrdTradeRoute preUser = new OrdTradeRoute();
		preUser.setOrderId(dto.getOrderId());
		List<OrdTradeRoute> preUserList = ordTradeRouteMapper.queryPreUserId(preUser);
		if (!CollectionUtils.isEmpty(preUserList)) {
			dto.setPreName(preUserList.get(0).getPreName());
		}
		return list;
	}

	@Override
	public List<OrdAfter> queryWSOrdAfterTeamList(IRequest requestContext, OrdAfter dto, int page, int pageSize) {
		PageHelper.startPage(page, pageSize);
		return ordAfterMapper.queryWSOrdAfterTeamList(dto);
	}

	/**
	 * 接口查询订单售后信息 daiqian.shi@hand-china.com
	 *
	 * @param request
	 * @param dto
	 * @return
	 */
	@Override
	public List<OrdAfter> queryWsOrdAfter(IRequest request, OrdAfter dto) {
		return ordAfterMapper.queryWsOrdAfter(dto);
	}

	/**
	 * 后端售后跟进提交
	 */
	@Override
	public void submit(IRequest requestCtx, List<OrdAfter> dto) throws Exception {
		OrdAfter ordAfter = dto.get(0);
		// 为日志表添加数据 
		//售后状态不是资料审核成功的时候(资料审核的时候 会添加快递信息 在下面处理)  添加日志
		if (!"AUDITSCUU".equals(ordAfter.getAfterStatus())) {
			OrdAfterLog log = new OrdAfterLog();
			log.setGeneralId(ordAfter.getAfterId());
			log.setIdType("AFTER");
			log.setStatus(ordAfter.getAfterStatus());
			log.setStatusDate(new Date());
			ordAfterLogService.insertSelective(requestCtx, log);

			// 发送通知 只有状态变化的时候才会发送
			sendNotice(requestCtx, ordAfter.getOrdOrderInfo());

		}

		// 修改保单信息
		ordAfterMapper.updateByPrimaryKeySelective(ordAfter.getOrdOrderInfo());
		// 修改续保信息(可以不写) 后期可能某些字段能够修改(不删除)
		// ordOrderService.updateByPrimaryKeySelective(requestCtx,ordAfter.getOrderRenewalInfo());

		// 资料审核成功
		if ("AUDITSCUU".equals(ordAfter.getAfterStatus())) {
			OrdAfterLog ordAfterLog = ordAfter.getOrdAfterLog();
			ordAfterLog.setStatusDate(new Date());
			ordAfterLog.setComment(ordAfterLog.getExpressCompany() + ":" + ordAfterLog.getExpressNum());
			ordAfterLog.setObjectVersionNumber(1L);
			ordAfterLogService.insertSelective(requestCtx, ordAfterLog);
			// 状态是处理成功
		} else if ("SUCCESS".equals(ordAfter.getAfterStatus())) {
			// 退保信息 售后项目是退保
			if ("EXIT".equals(ordAfter.getOrdOrderInfo().getAfterProject())) {
				List<OrdAddition> ordOrderExit = ordAfter.getOrdOrderExit();
				if (ordOrderExit != null && ordOrderExit.size() > 0) {
					for (OrdAddition ordAddition : ordOrderExit) {
						if ("Y".equalsIgnoreCase(ordAddition.getSurrenderFlag())) {
							String source = ordAddition.getSource();
							// 售后状态是处理成功 需要将订单状态和附加险状态改为 已退保
							if ("ORDER".equalsIgnoreCase(source)) {
								OrdOrder ord = new OrdOrder();
								ord.setOrderId(ordAddition.getOrderId());
								ord.setStatus("SURRENDERED");
								ord.setSaveStatus("SURRENDERED");
								ord.setSurrenderFlag("Y");

								ordOrderService.updateByPrimaryKeySelective(requestCtx, ord);
							} else {
								ordAddition.setStatus("SURRENDERED");
								ordAddition.setSaveStatus("SURRENDERED");
								ordAddition.setSurrenderFlag("Y");
								ordAdditionService.updateOrdAdditionByOrderIdAndItemId(ordAddition);
							}

						}
					}
				}
			}
			// 售后项目是续保
			if ("RENEWAL".equals(ordAfter.getOrdOrderInfo().getAfterProject())) {
				updateOrder(requestCtx, ordAfter.getOrderRenewalSuccessInfo());
			}
			// 状态是 处理失败和已取消
		} else if ("FAIL".equals(ordAfter.getAfterStatus()) || "CANCELED".equals(ordAfter.getAfterStatus())) {
			if ("EXIT".equals(ordAfter.getOrdOrderInfo().getAfterProject())) {
				List<OrdAddition> ordOrderExit = ordAfter.getOrdOrderExit();
				if (ordOrderExit != null && ordOrderExit.size() > 0) {
					for (OrdAddition ordAddition : ordOrderExit) {
						if ("Y".equalsIgnoreCase(ordAddition.getSurrenderFlag())) {
							String source = ordAddition.getSource();
							// 售后状态是处理失败 需要将订单状态和附加险状态改为 初始状态
							// 如果是订单表中的数据
							if ("ORDER".equalsIgnoreCase(source)) {
								OrdOrder ord = new OrdOrder();
								ord.setOrderId(ordAddition.getOrderId());
								ord.setSaveStatus(ordAddition.getStatus());
								ord.setSurrenderFlag("N");
								ordOrderService.updateByPrimaryKeySelective(requestCtx, ord);
							} else {
								// 附件险表中的数据
								ordAddition.setSaveStatus(ordAddition.getStatus());
								ordAddition.setSurrenderFlag("N");
								ordAdditionService.updateOrdAdditionByOrderIdAndItemId(ordAddition);
							}

						}
					}
				}
			}
			if ("RENEWAL".equals(ordAfter.getOrdOrderInfo().getAfterProject())) {
				// 将续保标识改为 "N" 不是正在续保
				OrdOrder order = new OrdOrder();
				order.setOrderId(ordAfter.getOrderId());
				order.setRenewalFlag("N");
				ordOrderservice.updateByPrimaryKeySelective(requestCtx, order);
			}
		}

		// 增加售后添加记录
		saveAfterFollow(requestCtx, ordAfter);
	}

	@Override
	public List<OrdOrderRenewal> ClbWsOrdRenewalTeamList(IRequest requestCtx, OrdOrderRenewal dto, int page,
			int pageSize) {
		PageHelper.startPage(page, pageSize);
		return ordAfterMapper.ClbWsOrdRenewalTeamList(dto);
	}

	/**
	 * 前端售后列表跟进页面---->提交 主要是添加售后跟进记录 还要传递退保表格(状态为需复查AUDITAGIN)
	 */
	@Override
	public void ordAfterSubmit(IRequest requestCtx, OrdAfter dto) throws Exception {
		if (dto.getAfterId() != null) {
			// 售后状态是资料需复查的时候 提交的时候 要改变状态为资料待审核 并且此时 续保表格是可以更改的
			if (dto.getAfterStatus() == null || "".equals(dto.getAfterStatus())
					|| !"AUDITAGIN".equals(dto.getAfterStatus())) {
				throw new RuntimeException("当前状态下不能进行提交!");
			}

			// 新增售后跟进记录 抽取成方法
			saveAfterFollow(requestCtx, dto);

			// 退表表格处理
			if ("EXIT".equals(dto.getOrdOrderInfo().getAfterProject())) {
				List<OrdAddition> ordOrderExit = dto.getOrdOrderExit();
				if (ordOrderExit != null && ordOrderExit.size() > 0) {
					for (OrdAddition ordAddition : ordOrderExit) {
						if ("Y".equalsIgnoreCase(ordAddition.getSurrenderFlag())) {
							String source = ordAddition.getSource();
							if ("ORDER".equalsIgnoreCase(source)) {
								OrdOrder ord = new OrdOrder();
								ord.setOrderId(ordAddition.getOrderId());
								// 退保中...
								// ord.setSaveStatus(ord.getStatus());
								// ord.setStatus("SURRENDERING");
								ord.setSaveStatus("SURRENDERING");
								ord.setSurrenderFlag("Y");

								ordOrderservice.updateByPrimaryKeySelective(requestCtx, ord);
							} else {
								ordAddition.setSaveStatus("SURRENDERING");
								// ordAddition.setStatus("SURRENDERING");
								ordAdditionService.updateOrdAdditionByOrderIdAndItemId(ordAddition);
							}

						}
					}
				}
			}
			// 改变售后状态为资料待审核
			dto.setAfterStatus("TOAUDIT");
			OrdAfter after = updateByPrimaryKeySelective(requestCtx, dto);

			// 状态变化 发送通知
			sendNotice(requestCtx, after);
		}
	}

	@Override
	public List<OrdTemplate> queryOrdAfterProject(IRequest requestCtx, OrdTemplate dto) {
		return ordTemplateService.queryApplyClassOnOrdAfter(requestCtx, dto);
	}

	@Override
	public List<OrdTemplate> queryApplyItem(IRequest requestCtx, OrdTemplate dto) {
		return ordTemplateService.queryApplyItem(requestCtx, dto);
	}

	/**
	 * 售后发送通知的公共方法
	 * 
	 * @param requestCtx
	 * @param dto
	 */
	public void sendNotice(IRequest requestCtx, OrdAfter dto) throws Exception {
		// 初始化通知参数
		Long userId = null;
		String templateCode = null;
		Map<String, Object> sysdata = new HashMap<>();

		// 根据订单id 查找订单详情
		OrdOrder ordOrder = new OrdOrder();
		ordOrder.setOrderId(dto.getOrderId());
		OrdOrder key = queryOrderByOrdOrderId(requestCtx, ordOrder).get(0);

		// 查找渠道是否存在
		ClbUser condition = new ClbUser();
		condition.setRelatedPartyId(key.getChannelId());
		condition.setUserType("CHANNEL");
		List<ClbUser> select = clbUserService.select(requestCtx, condition, 1, 1);
		if (select == null || select.get(0).getUserId() == null) {
			throw new RuntimeException("该渠道没有注册为用户!");
		}
		userId = select.get(0).getUserId();

		sysdata.put("item", key.getItem());
		sysdata.put("applicant", key.getApplicant());
		sysdata.put("orderNumber", key.getOrderNumber());
		sysdata.put("insurant", key.getInsurant());
		sysdata.put("afterType", dto.getAfterType());
		sysdata.put("submitDate", dto.getCreationDate().toLocaleString());

		// 资料审核中
		if ("TOAUDIT".equals(dto.getAfterStatus())) {
			templateCode = "SH0001";
			// 资料需复查
		} else if ("AUDITAGIN".equals(dto.getAfterStatus())) {
			templateCode = "SH0002";
			// 资料审核成功
		} else if ("AUDITSCUU".equals(dto.getAfterStatus())) {
			templateCode = "SH0004";
			// 已提交保险公司
		} else if ("SUBMIT".equals(dto.getAfterStatus())) {
			templateCode = "SH0003";
			// 处理成功
		} else if ("SUCCESS".equals(dto.getAfterStatus())) {
			templateCode = "SH0006";
			// 处理失败
		} else if ("FAIL".equals(dto.getAfterStatus())) {
			templateCode = "SH0005";
			// 已取消
		} else if ("CANCELED".equals(dto.getAfterStatus())) {
			templateCode = "SH0007";
		}

		ntnNotificationService.sendNotification(requestCtx, userId, templateCode, sysdata);

	}

	/**
	 * 修改续保表中当前期数下主险和附加险的信息 修改状态 币种 续保到期日
	 */
	@Override
	public void updateRenewalInfoByOrderId(IRequest requestCtx, OrdOrderRenewal dto) {
		ordAfterMapper.updateRenewalInfoByOrderId(dto);
	}

	/**
	 * 修改续保表中当前期数下主险和附加险的缴费金额
	 */
	@Override
	public void updateRenewalTotalAmountByOrderId(IRequest requestCtx, OrdOrderRenewal dto) {
		ordAfterMapper.updateRenewalTotalAmountByOrderId(dto);
	}

	/**
	 * 续保成功之后弹窗修改 续保信息 售后状态为 成功之后 修改下一年的缴费信息 和 续保表中的下一期的缴费总额
	 */
	@Override
	public void updateOrder(IRequest requestCtx, OrdOrder ordOrder) {
		/*
		 * List<OrdOrder> list = ordOrderService.queryRenewalYear(ordOrder); Long
		 * renewalYear = list.get(0).getRenewalYear();
		 */

		// 续保成功之后 修改当前缴费期数下的续保的状态为已续保
		OrdOrderRenewal renewal = new OrdOrderRenewal();
		renewal.setOrderId(ordOrder.getOrderId());
		renewal.setRenewalStatusColumn("RENEWAL_STATUS" + ordOrder.getPayPeriods());
		renewal.setRenewalStatus("RENEWALED");
		// 修改下期续保到期日
		renewal.setRenewalDateColumn("RENEWAL_DATE" + (ordOrder.getPayPeriods() + 1));
		renewal.setRenewalDate(ordOrder.getRenewalDueDate());
		ordOrderRenewalService.updateRenewalByOrderId(requestCtx, renewal);

		// 修改下一年的缴费信息 并将续保标识改为 "N" 不是正在续保
		ordOrder.setRenewalFlag("N");
		ordOrderService.updateByPrimaryKeySelective(requestCtx, ordOrder);

		// 修改下期保费金额
		renewal.setTotalAmountColumn("TOTAL_AMOUNT" + (ordOrder.getPayPeriods() + 1));
		renewal.setTotalAmount(ordOrder.getNextPolicyAmount());
		updateRenewalTotalAmountByOrderId(requestCtx, renewal);

	}

	/**
	 * 查询续保的条数
	 */
	@Override
	public Long queryRenewalTotal(IRequest requestCtx, OrdOrderRenewal dto) {
		return ordAfterMapper.queryRenewalTotal(dto);
	}

	/**
	 * 取消售后
	 */
	@Override
	public void cancel(IRequest requestContext, OrdAfter dto) {
		dto.setAfterStatus("CANCELED");
		// 将标识的值设为0 表示取消或者删除
		dto.setFlag(0L);
		updateByPrimaryKeySelective(requestContext, dto);

		// 修改状态后 将状态写入日志表
		OrdAfterLog ordAfterLog = new OrdAfterLog();
		ordAfterLog.setGeneralId(dto.getAfterId());
		ordAfterLog.setIdType("AFTER");
		ordAfterLog.setStatusDate(new Date());
		ordAfterLog.setStatus("CANCELED");// 默认的状态是取消

		ordAfterLogService.insertSelective(requestContext, ordAfterLog);

		// 如果是续保的话 将续保标识改为"N"
		String afterProject = queryAfterProject(dto);
		OrdAfter after = selectByPrimaryKey(requestContext, dto);
		// 续保
		if ("RENEWAL".equals(afterProject)) {
			OrdOrder order = new OrdOrder();
			order.setOrderId(after.getOrderId());
			order.setRenewalFlag("N");
			ordOrderservice.updateByPrimaryKeySelective(requestContext, order);

			// 退保
		} else if ("EXIT".equals(afterProject)) {
			OrdAddition addition = new OrdAddition();
			addition.setOrderId(after.getOrderId());
			List<OrdAddition> list = queryOrdAdditionByOrderId(requestContext, addition, 1, 1000);
			if (!CollectionUtils.isEmpty(list)) {
				for (OrdAddition ordAddition : list) {
					if ("Y".equalsIgnoreCase(ordAddition.getSurrenderFlag())) {
						String source = ordAddition.getSource();
						// 售后状态是处理失败 需要将订单状态和附加险状态改为 初始状态
						if ("ORDER".equalsIgnoreCase(source)) {
							OrdOrder ord = new OrdOrder();
							ord.setOrderId(ordAddition.getOrderId());
							// 修改回原来的状态
							ord.setSaveStatus(ordAddition.getStatus());
							ord.setSurrenderFlag("N");
							ordOrderService.updateByPrimaryKeySelective(requestContext, ord);
						} else {
							// 修改回原来的状态
							ordAddition.setSaveStatus(ordAddition.getStatus());
							ordAddition.setSurrenderFlag("N");
							ordAdditionService.updateOrdAdditionByOrderIdAndItemId(ordAddition);
						}

					}
				}
			}
		}
	}

	/**
	 * 查询售后项目 通过售后id
	 * 
	 * @param dto
	 * @return
	 */
	public String queryAfterProject(OrdAfter dto) {
		return ordAfterMapper.queryAfterProject(dto);
	}

	/**
	 * 查询分配处理人的姓名
	 * 
	 * @param requestCtx
	 * @param list
	 * @return
	 */
	@Override
	public List<OrdAfter> queryHandleUserName(IRequest requestCtx, List<OrdAfter> list) {
		// 查询分配处理人的姓名
		if (!CollectionUtils.isEmpty(list)) {
			for (OrdAfter ordAfter : list) {
				if (ordAfter.getHandlerUserId() != null) {
					ClbUser user = new ClbUser();
					user.setUserId(ordAfter.getHandlerUserId());
					List<ClbUser> userList = clbUserService.selectAllFields(requestCtx, user, 1, 10);
					ordAfter.setHandlerUserName(userList.get(0).getRelatedPartyName());
				}
			}
		}
		return list;
	}

	/**
	 * 新增售后跟进记录
	 * 
	 * @param requestCtx
	 * @param dto
	 */
	@Override
	public void saveAfterFollow(IRequest requestCtx, OrdAfter dto) {
		// 售后跟进内容不能为空
		if (dto.getContent() == null || "".equals(dto.getContent().trim())) {
			throw new RuntimeException("请填写售后记录!");
		}
		// set对应的值
		OrdAfterFollow ordAfterFollow = new OrdAfterFollow();
		ordAfterFollow.setFollowUserId(requestCtx.getUserId());
		ordAfterFollow.setContent(dto.getContent());
		ordAfterFollow.setAfterId(dto.getAfterId());
		ordAfterFollow.setFileId(dto.getFileId());
		ordAfterFollow.setFollowDate(new Date());
		ordAfterFollow.setObjectVersionNumber(1L);

		ordAfterFollowService.insertSelective(requestCtx, ordAfterFollow);
	}

}