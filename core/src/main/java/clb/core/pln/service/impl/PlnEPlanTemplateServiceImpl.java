package clb.core.pln.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import clb.core.common.utils.CommonUtil;
import clb.core.pln.dto.PlnEPlanTemplate;
import clb.core.pln.dto.PlnEPlanTemplateBig;
import clb.core.pln.dto.PlnEPlanTemplateDetail;
import clb.core.pln.dto.PlnEPlanTemplateSmall;
import clb.core.pln.mapper.PlnEPlanTemplateBigMapper;
import clb.core.pln.mapper.PlnEPlanTemplateDetailMapper;
import clb.core.pln.mapper.PlnEPlanTemplateMapper;
import clb.core.pln.mapper.PlnEPlanTemplateSmallMapper;
import clb.core.pln.service.IPlnEPlanTemplateBigService;
import clb.core.pln.service.IPlnEPlanTemplateDetailService;
import clb.core.pln.service.IPlnEPlanTemplateService;
import clb.core.pln.service.IPlnEPlanTemplateSmallService;
import clb.core.production.dto.PrdItemPaymode;
import clb.core.production.dto.PrdItems;
import clb.core.production.dto.PrdPremium;
import clb.core.production.service.IPrdItemPaymodeService;
import clb.core.production.service.IPrdItemsService;
import clb.core.production.service.IPrdPremiumService;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PlnEPlanTemplateServiceImpl extends BaseServiceImpl<PlnEPlanTemplate> implements IPlnEPlanTemplateService {

	@Autowired
	private PlnEPlanTemplateMapper mapper;
	@Autowired
	private PlnEPlanTemplateBigMapper bigMapper;
	@Autowired
	private PlnEPlanTemplateSmallMapper smallMapper;
	@Autowired
	private PlnEPlanTemplateDetailMapper detailMapper;

	@Autowired
	private IPlnEPlanTemplateBigService bigService;
	@Autowired
	private IPlnEPlanTemplateSmallService smallService;
	@Autowired
	private IPlnEPlanTemplateDetailService detailService;

	@Autowired
	private IPrdItemPaymodeService prdItemPaymodeService;

	@Autowired
	private IPrdPremiumService prdPremiumService;

	@Autowired
	private IPrdItemsService prdItemsService;

	/**
	 * 查询所有数据
	 */
	@Override
	public List<PlnEPlanTemplate> query(IRequest requestContext, PlnEPlanTemplate dto, int page, int pageSize) {
		PageHelper.startPage(page, pageSize);
		return mapper.query(dto);
	}

	/**
	 * 前端查询电子计划书模板信息(大,中,小信息)
	 */
	@Override
	public List<PlnEPlanTemplate> queryWsPlnEPlanTemplate(IRequest requestContext, PrdPremium prdPremium, int page,
			int pageSize) {
		PageHelper.startPage(page, pageSize);
		Double amount = null;
		// 保费种类是保费IP
		if ("IP".equals(prdPremium.getAmountType())) {
			// 进行相应计算
			amount = counterAmount(requestContext, prdPremium);
			// 如果是保险金额 IC直接参与计算
		} else {
			amount = prdPremium.getAmount();
		}

		PlnEPlanTemplate dto = new PlnEPlanTemplate();
		dto.setItemId(prdPremium.getItemId());
		dto.setCurrency(prdPremium.getCurrency());
		List<PlnEPlanTemplate> list = mapper.queryWsPlnEPlanTemplate(dto);
		// 查询到符合条件的模板
		if (list != null && list.size() > 0) {
			for (PlnEPlanTemplate plnEPlanTemplate : list) {
				plnEPlanTemplate.setAmount((amount == null ? "-":String.valueOf(amount)));
				
				PlnEPlanTemplateBig big = new PlnEPlanTemplateBig();
				big.setTemplateId(plnEPlanTemplate.getTemplateId());
				List<PlnEPlanTemplateBig> bigList = bigMapper.queryList(big);
				if (bigList != null && bigList.size() > 0) {
					plnEPlanTemplate.setPlnEPlanTemplateBigList(bigList);
					// 遍历查询所有的大标题
					for (PlnEPlanTemplateBig plnEPlanTemplateBig : bigList) {
						PlnEPlanTemplateSmall small = new PlnEPlanTemplateSmall();
						small.setBigId(plnEPlanTemplateBig.getBigId());
						List<PlnEPlanTemplateSmall> smallList = smallMapper.queryList(small);
						if (smallList != null && smallList.size() > 0) {
							for (PlnEPlanTemplateSmall plnEPlanTemplateSmall : smallList) {
								String amountFormula = plnEPlanTemplateSmall.getAmountFormula();
								if (!"".equals(amountFormula) && amountFormula != null) {
									Double yearPremium = getYearPremium(amountFormula);
									if(yearPremium != null) {
										plnEPlanTemplateSmall.setAmountFormula(
												amount == null ? amountFormula : getDoubleFormat(amount * yearPremium));
									}
								}
							}
							plnEPlanTemplateBig.setPlnEPlanTemplateSmallList(smallList);
							// 遍历查询所有的小标题
							for (PlnEPlanTemplateSmall plnEPlanTemplateSmall : smallList) {
								PlnEPlanTemplateDetail detail = new PlnEPlanTemplateDetail();
								detail.setSmallId(plnEPlanTemplateSmall.getSmallId());
								List<PlnEPlanTemplateDetail> detailList = detailMapper.queryList(detail);
								if (detailList != null && detailList.size() > 0) {
									for (PlnEPlanTemplateDetail plnEPlanTemplateDetail : detailList) {
										String amountFormula = plnEPlanTemplateDetail.getAmountFormula();
										if (!"".equals(amountFormula) && amountFormula != null) {
											Double yearPremium = getYearPremium(amountFormula);
											if(yearPremium != null) {
												plnEPlanTemplateDetail.setAmountFormula(amount == null ? amountFormula
														: getDoubleFormat(amount * yearPremium));
											}
										}
									}
									plnEPlanTemplateSmall.setPlnEPlanTemplateDetailList(detailList);
								}
							}
						}
					}
				}
			}
		}
		return list;
	}

	/**
	 * 计算保额
	 * 
	 * @param requestContext
	 * @param prdPremium
	 * @return
	 */
	public Double counterAmount(IRequest requestContext, PrdPremium prdPremium) {
		Double money = null;
		try {
			PrdItems items = new PrdItems();
			items.setItemId(prdPremium.getItemId());
			PrdItems prdItems = prdItemsService.selectByPrimaryKey(requestContext, items);
			// 高端医疗险直接显示文本
			if ("GD".equals(prdItems.getMinClass())) {
				return null;
			} else {
				Double rate = null;
				if ("MP".equals(prdPremium.getPayMethod())) {
					rate = prdItems.getOnemonthRate();
				} else if ("QP".equals(prdPremium.getPayMethod())) {
					rate = prdItems.getQuarterRate();
				} else if ("SAP".equals(prdPremium.getPayMethod())) {
					rate = prdItems.getHalfyearRate();
				} else if ("AP".equals(prdPremium.getPayMethod())) {
					rate = prdItems.getOneyearRate();
				}

				prdPremium.setPremiumColumn("pp.PREMIUM" + prdPremium.getAge());
				Double amount = prdPremiumService.queryAmount(requestContext, prdPremium);
				money = (prdPremium.getAmount() * 1000) / (rate * amount);
			}

		} catch (Exception e) {
			CommonUtil.printStackTraceToStr(e);
			money = null;
		}
		return money;

	}

	/**
	 * 截取字符串 得到计算公式的数值
	 * 
	 * @param str
	 * @return
	 */
	public Double getYearPremium(String str) {
		Double parseDouble = null;
		try {
			parseDouble = Double.parseDouble(str.substring(str.indexOf("*") + 1, str.indexOf("%")).trim());
			return parseDouble / 100;
		} catch (Exception e) {
			CommonUtil.printStackTraceToStr(e);
			return null;
		}
	}

	/**
	 * 支持精确小数点位数 四舍五入
	 */
	public String getDoubleFormat(Double value) {
		NumberFormat nf = NumberFormat.getNumberInstance();
		 // 小数点精确2位数
		nf.setMaximumFractionDigits(2);
		return nf.format(value);
	}

	/**
	 * 完善数据
	 */
	@Override
	public void dataPerfect(IRequest requestContext) throws Exception {
		try {
			// 查询出所有的模板
			List<PlnEPlanTemplate> templateList = selectAll(requestContext);
			if (null != templateList && templateList.size() > 0) {
				for (PlnEPlanTemplate plnEPlanTemplate : templateList) {
					if ("".equals(plnEPlanTemplate.getCurrency()) || plnEPlanTemplate.getCurrency() == null) {
						PrdItemPaymode prdItemPaymode = new PrdItemPaymode();
						prdItemPaymode.setItemId(plnEPlanTemplate.getItemId());
						prdItemPaymode.setEnabledFlag("Y");
						List<PrdItemPaymode> curreyList = prdItemPaymodeService.queryCurrencyByItemId(requestContext,
								prdItemPaymode);
						if (curreyList != null && curreyList.size() > 0) {

							// 去掉存在的
							PlnEPlanTemplate templates = new PlnEPlanTemplate();
							templates.setItemId(plnEPlanTemplate.getItemId());

							Iterator<PrdItemPaymode> iterator = curreyList.iterator();
							while (iterator.hasNext()) {
								templates.setCurrency(iterator.next().getCurrencyCode());
								List<PlnEPlanTemplate> query = mapper.query(templates);
								if (query != null && query.size() > 0) {
									iterator.remove();
								}
							}

							if (curreyList != null && curreyList.size() > 0) {
								String templateName = plnEPlanTemplate.getTemplateName();
								plnEPlanTemplate.setCurrency(curreyList.get(0).getCurrencyCode());
								plnEPlanTemplate
										.setTemplateName(templateName + "-" + curreyList.get(0).getCurrencyCode());
								PlnEPlanTemplate planTemplate = updateByPrimaryKeySelective(requestContext,
										plnEPlanTemplate);

								curreyList.remove(0);

								if (curreyList != null && curreyList.size() > 0) {
									for (PrdItemPaymode prdItemPaymode2 : curreyList) {
										PlnEPlanTemplate temp = selectByPrimaryKey(requestContext, planTemplate);
										temp.setCurrency(prdItemPaymode2.getCurrencyCode());
										temp.setTemplateId(null);
										temp.setCreationDate(null);
										temp.setLastUpdateDate(null);
										temp.setObjectVersionNumber(1L);
										temp.setTemplateName(templateName + "-" + prdItemPaymode2.getCurrencyCode());

										PlnEPlanTemplate template = insertSelective(requestContext, temp);

										PlnEPlanTemplateBig templateBig = new PlnEPlanTemplateBig();
										templateBig.setTemplateId(planTemplate.getTemplateId());

										List<PlnEPlanTemplateBig> bigList = bigService.queryList(requestContext,
												templateBig, 1, 1000);
										if (bigList != null && bigList.size() > 0) {
											for (PlnEPlanTemplateBig plnEPlanTemplateBig : bigList) {

												PlnEPlanTemplateSmall templateSmall = new PlnEPlanTemplateSmall();
												templateSmall.setBigId(plnEPlanTemplateBig.getBigId());

												List<PlnEPlanTemplateSmall> smallList = smallService
														.queryList(requestContext, templateSmall, 1, 1000);

												plnEPlanTemplateBig.setTemplateId(template.getTemplateId());
												plnEPlanTemplateBig.setBigId(null);
												plnEPlanTemplateBig.setCreationDate(null);
												plnEPlanTemplateBig.setLastUpdateDate(null);
												plnEPlanTemplateBig.setObjectVersionNumber(1L);

												PlnEPlanTemplateBig big = bigService.insertSelective(requestContext,
														plnEPlanTemplateBig);

												for (PlnEPlanTemplateSmall plnEPlanTemplateSmall : smallList) {

													PlnEPlanTemplateDetail templateDetail = new PlnEPlanTemplateDetail();
													templateDetail.setSmallId(plnEPlanTemplateSmall.getSmallId());

													List<PlnEPlanTemplateDetail> detailList = detailService
															.queryList(requestContext, templateDetail, 1, 1000);

													plnEPlanTemplateSmall.setBigId(big.getBigId());
													plnEPlanTemplateSmall.setSmallId(null);
													plnEPlanTemplateSmall.setCreationDate(null);
													plnEPlanTemplateSmall.setLastUpdateDate(null);
													plnEPlanTemplateSmall.setObjectVersionNumber(1L);

													PlnEPlanTemplateSmall small = smallService
															.insertSelective(requestContext, plnEPlanTemplateSmall);

													if (detailList != null && detailList.size() > 0) {

														for (PlnEPlanTemplateDetail plnEPlanTemplateDetail : detailList) {
															plnEPlanTemplateDetail.setSmallId(small.getSmallId());
															plnEPlanTemplateDetail.setDetailId(null);
															plnEPlanTemplateDetail.setCreationDate(null);
															plnEPlanTemplateDetail.setLastUpdateDate(null);
															plnEPlanTemplateDetail.setObjectVersionNumber(1L);

															detailService.insertSelective(requestContext,
																	plnEPlanTemplateDetail);
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException();
		}

	}

}