package clb.core.production.service.impl;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.github.pagehelper.PageHelper;
import com.hand.hap.attachment.service.ISysFileService;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.mapper.CodeValueMapper;
import com.hand.hap.system.service.ILovService;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.commission.dto.CmnCalc;
import clb.core.commission.mapper.CmnCalcMapper;
import clb.core.common.dto.ImportResponse;
import clb.core.common.service.ICommonService;
import clb.core.common.utils.CommonUtil;
import clb.core.prc.dto.PrcItemScore;
import clb.core.prc.dto.PrcRadarLine;
import clb.core.prc.dto.PrcRadarLineDetail;
import clb.core.prc.mapper.PrcItemScoreMapper;
import clb.core.prc.mapper.PrcRadarLineDetailMapper;
import clb.core.prc.mapper.PrcRadarLineMapper;
import clb.core.production.dto.PrdAttribute;
import clb.core.production.dto.PrdAttributeSet;
import clb.core.production.dto.PrdAttributeSetLine;
import clb.core.production.dto.PrdCashValue;
import clb.core.production.dto.PrdDiscount;
import clb.core.production.dto.PrdImageText;
import clb.core.production.dto.PrdItemLabelLines;
import clb.core.production.dto.PrdItemLabels;
import clb.core.production.dto.PrdItemPaymode;
import clb.core.production.dto.PrdItemSecurityPlan;
import clb.core.production.dto.PrdItemSelfpay;
import clb.core.production.dto.PrdItemSubline;
import clb.core.production.dto.PrdItems;
import clb.core.production.dto.PrdRadarChart;
import clb.core.production.dto.PrdRadarChartItem;
import clb.core.production.dto.PrdRadarChartLine;
import clb.core.production.mapper.PrdAttributeMapper;
import clb.core.production.mapper.PrdAttributeSetLineMapper;
import clb.core.production.mapper.PrdAttributeSetMapper;
import clb.core.production.mapper.PrdDiscountMapper;
import clb.core.production.mapper.PrdImageTextMapper;
import clb.core.production.mapper.PrdItemLabelLinesMapper;
import clb.core.production.mapper.PrdItemLabelsMapper;
import clb.core.production.mapper.PrdItemPaymodeMapper;
import clb.core.production.mapper.PrdItemSecurityPlanMapper;
import clb.core.production.mapper.PrdItemSelfpayMapper;
import clb.core.production.mapper.PrdItemSublineMapper;
import clb.core.production.mapper.PrdItemsMapper;
import clb.core.production.service.IPrdCashValueService;
import clb.core.production.service.IPrdItemLabelLinesService;
import clb.core.production.service.IPrdItemLabelsService;
import clb.core.production.service.IPrdItemsService;
import clb.core.supplier.dto.SpeSupplier;
import clb.core.supplier.mapper.SpeSupplierMapper;
import clb.core.system.dto.ClbCodeValue;
import clb.core.system.service.IClbCodeService;
import net.sf.json.JSONObject;

/**
 * Created by Rex.Hua on 17/4/12.
 */
@Service
@Transactional
@SuppressWarnings("rawtypes")
public class PrdItemsServiceImpl extends BaseServiceImpl<PrdItems> implements IPrdItemsService {
	private static String WARN = "警告";
	private static String ERROR = "错误";

	private static Logger logger = LoggerFactory.getLogger(PrdItemsServiceImpl.class);
	@Autowired
	private PrdItemsMapper prdItemsMapper;
	@Autowired
	private PrdItemLabelsMapper prdItemsLabelsMapper;
	@Autowired
	private PrdItemLabelLinesMapper prdItemLabelLinesMapper;
	@Autowired
	private IPrdItemLabelsService prdItemLabelsService;
	@Autowired
	private PrdAttributeSetLineMapper prdAttributeSetLineMapper;
	@Autowired
	private PrdItemSublineMapper prdItemSublineMapper;
	@Autowired
	private PrdItemSecurityPlanMapper prdItemSecurityPlanMapper;
	@Autowired
	private PrdItemSelfpayMapper prdItemSelfpayMapper;
	@Autowired
	private PrdItemPaymodeMapper prdItemPaymodeMapper;
	@Autowired
	private PrdImageTextMapper prdImageTextMapper;
	@Autowired
	private CmnCalcMapper cmnCalcMapper;
	@Autowired
	private IPrdCashValueService prdCashValueService;
	@Autowired
	private PrcRadarLineMapper prcRadarLineMapper;
	@Autowired
	private PrcRadarLineDetailMapper prcRadarLineDetailMapper;
	@Autowired
	private PrcItemScoreMapper prcItemScoreMapper;
	@Autowired
	private ISysFileService fileService;
	@Autowired
	private IPrdItemLabelLinesService prdItemLabelLinesService;
	@Autowired
	private ICommonService commonService;
	@Autowired
	private PrdDiscountMapper prdDiscountMapper;
	@Autowired
	private PrdAttributeSetMapper prdAttributeSetMapper;
	@Autowired
	private CodeValueMapper codeValueMapper;
	@Autowired
	private IClbCodeService clbCodeService;
	@Autowired
	private ILovService lovService;
	@Autowired
	private SpeSupplierMapper speSupplierMapper;
	@Autowired
	private PrdAttributeMapper prdAttributeMapper;

	@Override
	public List<PrdItems> query(PrdItems prdItems) {
		return prdItemsMapper.select(prdItems);
	}

	@Override
	public List<PrdItems> batchUpdate(IRequest request, List<PrdItems> prdItemsList) {
		for (PrdItems prdItems : prdItemsList) {
			if (prdItems.getItemId() == null || prdItems.getItemId().equals("")) {
				// 获取编产品码规则
				String bigClass = prdItems.getBigClass();
				Integer maxItemsCodeNumber = prdItemsMapper.getMaxItemsCode(bigClass) + 1;
				String maxItemsCode = String.format("%05d", maxItemsCodeNumber);
				maxItemsCode = bigClass + maxItemsCode;
				prdItems.setItemCode(maxItemsCode);

				PrdAttributeSetLine prdAttributeSetLine = new PrdAttributeSetLine();
				List<PrdAttributeSetLine> prdAttributeSetLineList = new ArrayList<PrdAttributeSetLine>();

				prdAttributeSetLine.setAttSetId(prdItems.getAttSetId());
				prdAttributeSetLineList = prdAttributeSetLineMapper.selectAllFields(prdAttributeSetLine);
				for (PrdAttributeSetLine dto : prdAttributeSetLineList) {
					if (dto.getStatusCode().equals("Y")) {
						if (StringUtils
								.isBlank(CommonUtil.getMethodValue(prdItems, dto.getFieldCode().toLowerCase()))) {
							Object newPrdItems = CommonUtil.classSetMethod(prdItems, dto.getFieldCode(),
									dto.getDefaultValue());
						}
					}
				}
				prdItems.setPrdAttributeSetLine(prdAttributeSetLineList);

				// 插入
				prdItemsMapper.insertSelective(prdItems);
			} else {
				self().updateByPrimaryKey(request, prdItems);
				////////////////////////////////////////////////////////////////////////////////
				// 更新标签
				List<PrdItemLabelLines> resulLinesList = new ArrayList<PrdItemLabelLines>();
				if (prdItems.getPrdItemLabelLinesList() != null) {
					for (PrdItemLabelLines dto : prdItems.getPrdItemLabelLinesList()) {
						// Init
						PrdItemLabels prdItemLabels = new PrdItemLabels();
						PrdItemLabelLines prdItemLabelLines = new PrdItemLabelLines();
						long labelId = 0;
						// 判断是否在标签中存在
						prdItemLabels.setLabelName(dto.getLabelName());
						prdItemLabels.setBigClass(dto.getBigClass());
						if (prdItemLabelsService.selectLabelId(prdItemLabels) == null
								|| prdItemLabelsService.selectLabelId(prdItemLabels).equals("")) {
							labelId = 0;
						} else {
							labelId = Long.valueOf(prdItemLabelsService.selectLabelId(prdItemLabels));
						}
						if (labelId == 0) {
							// 不存在,则同时插入标签表与产品标签行表
							prdItemLabels.setBigClass(dto.getBigClass());
							prdItemLabels.setLabelName(dto.getLabelName());
							prdItemsLabelsMapper.insertSelective(prdItemLabels);
							labelId = prdItemLabels.getLabelId();
							prdItemLabelLines.setLabelId(labelId);
							prdItemLabelLines.setItemId(prdItems.getItemId());
							// 判断是否存在于产品标签行表
							if (prdItemLabelLinesMapper.getCountByItemLabel(prdItemLabelLines) == 0) {
								prdItemLabelLinesMapper.insertSelective(prdItemLabelLines);
							}
						} else {
							// 存在,则只插入产品标签行表
							prdItemLabelLines.setLabelId(labelId);
							prdItemLabelLines.setItemId(prdItems.getItemId());
							// 判断是否存在于产品标签行表
							// 判断是否存在于产品标签行表
							if (prdItemLabelLinesMapper.getCountByItemLabel(prdItemLabelLines) == 0) {
								prdItemLabelLinesMapper.insertSelective(prdItemLabelLines);
							}
						}
					}
				}
			}
		}
		return prdItemsList;
	}

	@Override
	public List<PrdItems> selectAlive(PrdItems prdItems, int page, int pagesize) {
		PageHelper.startPage(page, pagesize);
		List<PrdItems> prdItemsList = new ArrayList<PrdItems>();
		prdItemsList = prdItemsMapper.selectByParam(prdItems);

		if (prdItems.getAttSetId() == null) {
		} else {
			PrdAttributeSetLine prdAttributeSetLine = new PrdAttributeSetLine();
			List<PrdAttributeSetLine> prdAttributeSetLineList = new ArrayList<PrdAttributeSetLine>();

			prdAttributeSetLine.setAttSetId(prdItems.getAttSetId());
			prdAttributeSetLineList = prdAttributeSetLineMapper.selectAllFields(prdAttributeSetLine);
			prdItemsList.get(0).setPrdAttributeSetLine(prdAttributeSetLineList);
		}
		return prdItemsList;
	}

	/**
	 * 根据产品公司的id 查询该公司下的所有产品
	 */
	@Override
	public List<PrdItems> queryitemBySupplierId(IRequest requestContext, PrdItems dto) {
		return prdItemsMapper.queryitemBySupplierId(dto);
	}

	/**
	 * 产品列表列表查询(不包含详细数据)
	 */
	@Override
	public List<PrdItems> wsSelectHeaderAlive(PrdItems prdItems, int page, int pagesize) {
		PageHelper.startPage(page, pagesize);
		List<PrdItems> prdItemsList = new ArrayList<PrdItems>();
		// 属性字段查询条件拼接
		Field[] fields = prdItems.getClass().getDeclaredFields();
		String attributeCondition = " and 1 = 1";
		for (Field field : fields) {
			// setAccessible( true ) :类中的成员变量为private,故必须进行此操作
			field.setAccessible(true);
			try {
				if (field.get(prdItems) != null && !field.get(prdItems).toString().equals("null")) {
					if (field.getName().contains("ATTRIBUTE") || field.getName().contains("attribute")) {
						attributeCondition = attributeCondition + " and fpi." + field.getName() + " = " + "\""
								+ field.get(prdItems) + "\"";
					}
				}
			} catch (Exception e) {
				CommonUtil.printStackTraceToStr(e);
			}
		}
		prdItems.setAttributeCondition(attributeCondition);
		if (prdItems.getAnnualInterestFrom() != null) {
			prdItems.setAnnualInterestFrom(
					prdItems.getAnnualInterestFrom().substring(0, prdItems.getAnnualInterestFrom().length() - 1));
		}
		if (prdItems.getAnnualInterestTo() != null) {
			prdItems.setAnnualInterestTo(
					prdItems.getAnnualInterestTo().substring(0, prdItems.getAnnualInterestTo().length() - 1));
		}
		if (prdItems.getTransactionDataFrom() != null) {
			prdItems.setTransactionDataFrom(
					prdItems.getTransactionDataFrom().substring(0, prdItems.getTransactionDataFrom().length() - 2));
		}
		if (prdItems.getTransactionDataTo() != null) {
			prdItems.setTransactionDataTo(
					prdItems.getTransactionDataTo().substring(0, prdItems.getTransactionDataTo().length() - 2));
		}
		prdItemsList = prdItemsMapper.wsSelectByParam(prdItems);
		for (PrdItems dto : prdItemsList) {

			// set子产品
			PrdItemSubline prdItemSubline = new PrdItemSubline();
			List<PrdItemSubline> prdItemSublineList = new ArrayList<PrdItemSubline>();
			prdItemSubline.setItemId(dto.getItemId());
			prdItemSublineList = prdItemSublineMapper.select(prdItemSubline);
			dto.setPrdItemSublineList(prdItemSublineList);

			// set币种
			PrdItemPaymode prdItemPaymode = new PrdItemPaymode();
			List<PrdItemPaymode> prdItemPaymodeList = new ArrayList<PrdItemPaymode>();
			prdItemPaymode.setItemId(dto.getItemId());
			prdItemPaymodeList = prdItemPaymodeMapper.select(prdItemPaymode);
			dto.setPrdItemPaymode(prdItemPaymodeList);

			// set保障计划
			PrdItemSecurityPlan prdItemSecurityPlan = new PrdItemSecurityPlan();
			List<PrdItemSecurityPlan> prdItemSecurityPlanList = new ArrayList<PrdItemSecurityPlan>();
			prdItemSecurityPlan.setItemId(dto.getItemId());
			prdItemSecurityPlanList = prdItemSecurityPlanMapper.select(prdItemSecurityPlan);
			dto.setPrdItemSecurityPlan(prdItemSecurityPlanList);

			// set自付选项
			PrdItemSelfpay prdItemSelfPay = new PrdItemSelfpay();
			List<PrdItemSelfpay> prdItemSelfpayList = new ArrayList<PrdItemSelfpay>();
			prdItemSelfPay.setItemId(dto.getItemId());
			prdItemSelfpayList = prdItemSelfpayMapper.select(prdItemSelfPay);
			dto.setPrdItemSelfpayList(prdItemSelfpayList);
 
		}

		return prdItemsList;
	}



	@Override
	public List<PrdItems> wsSelectAlive(PrdItems prdItems, int page, int pagesize) {
		PageHelper.startPage(page, pagesize);
		List<PrdItems> prdItemsList = new ArrayList<PrdItems>();
		// 属性字段查询条件拼接
		Field[] fields = prdItems.getClass().getDeclaredFields();
		String attributeCondition = " and 1 = 1";
		for (Field field : fields) {
			// setAccessible( true ) :类中的成员变量为private,故必须进行此操作
			field.setAccessible(true);
			try {
				if (field.get(prdItems) != null && !field.get(prdItems).toString().equals("null")) {
					if (field.getName().contains("ATTRIBUTE") || field.getName().contains("attribute")) {
						attributeCondition = attributeCondition + " and fpi." + field.getName() + " = " + "\""
								+ field.get(prdItems) + "\"";
					}
				}
			} catch (Exception e) {
				CommonUtil.printStackTraceToStr(e);
			}
		}
		prdItems.setAttributeCondition(attributeCondition);
		if (prdItems.getAnnualInterestFrom() != null) {
			prdItems.setAnnualInterestFrom(
					prdItems.getAnnualInterestFrom().substring(0, prdItems.getAnnualInterestFrom().length() - 1));
		}
		if (prdItems.getAnnualInterestTo() != null) {
			prdItems.setAnnualInterestTo(
					prdItems.getAnnualInterestTo().substring(0, prdItems.getAnnualInterestTo().length() - 1));
		}
		if (prdItems.getTransactionDataFrom() != null) {
			prdItems.setTransactionDataFrom(
					prdItems.getTransactionDataFrom().substring(0, prdItems.getTransactionDataFrom().length() - 2));
		}
		if (prdItems.getTransactionDataTo() != null) {
			prdItems.setTransactionDataTo(
					prdItems.getTransactionDataTo().substring(0, prdItems.getTransactionDataTo().length() - 2));
		}
		prdItemsList = prdItemsMapper.wsSelectByParam(prdItems);
		for (PrdItems dto : prdItemsList) {
			// set产品属性
			if (dto.getAttSetId() == null) {
			} else {
				PrdAttributeSetLine prdAttributeSetLine = new PrdAttributeSetLine();
				List<PrdAttributeSetLine> prdAttributeSetLineList = new ArrayList<PrdAttributeSetLine>();

				prdAttributeSetLine.setAttSetId(dto.getAttSetId());
				prdAttributeSetLineList = prdAttributeSetLineMapper.selectAllFields(prdAttributeSetLine);
				dto.setPrdAttributeSetLine(prdAttributeSetLineList);
			}

			// set子产品
			PrdItemSubline prdItemSubline = new PrdItemSubline();
			List<PrdItemSubline> prdItemSublineList = new ArrayList<PrdItemSubline>();
			prdItemSubline.setItemId(dto.getItemId());
			prdItemSublineList = prdItemSublineMapper.select(prdItemSubline);
			dto.setPrdItemSublineList(prdItemSublineList);

			// set币种
			PrdItemPaymode prdItemPaymode = new PrdItemPaymode();
			List<PrdItemPaymode> prdItemPaymodeList = new ArrayList<PrdItemPaymode>();
			prdItemPaymode.setItemId(dto.getItemId());
			prdItemPaymodeList = prdItemPaymodeMapper.select(prdItemPaymode);
			dto.setPrdItemPaymode(prdItemPaymodeList);

			// set保障计划
			PrdItemSecurityPlan prdItemSecurityPlan = new PrdItemSecurityPlan();
			List<PrdItemSecurityPlan> prdItemSecurityPlanList = new ArrayList<PrdItemSecurityPlan>();
			prdItemSecurityPlan.setItemId(dto.getItemId());
			prdItemSecurityPlanList = prdItemSecurityPlanMapper.select(prdItemSecurityPlan);
			dto.setPrdItemSecurityPlan(prdItemSecurityPlanList);

			// set自付选项
			PrdItemSelfpay prdItemSelfPay = new PrdItemSelfpay();
			List<PrdItemSelfpay> prdItemSelfpayList = new ArrayList<PrdItemSelfpay>();
			prdItemSelfPay.setItemId(dto.getItemId());
			prdItemSelfpayList = prdItemSelfpayMapper.select(prdItemSelfPay);
			dto.setPrdItemSelfpayList(prdItemSelfpayList);

			// set优惠信息
			PrdDiscount prdDiscount = new PrdDiscount();
			List<PrdDiscount> prdDiscountList = new ArrayList<PrdDiscount>();
			prdDiscount.setItemId(dto.getItemId());
			prdDiscount.setStatusCode("Y");
			prdDiscountList = prdDiscountMapper.selectDiscounts(prdDiscount);
			dto.setPrdDiscountList(prdDiscountList);
		}

		return prdItemsList;
	}

	@Override
	public List<PrdImageText> getImageText(PrdImageText dto, int page, int pagesize) {
		PageHelper.startPage(page, pagesize);
		List<PrdImageText> dtoList = new ArrayList<PrdImageText>();
		dtoList = prdImageTextMapper.select(dto);
		return dtoList;
	}

	/*****
	 * 产品对比
	 */
	@Override
	public ResponseData prdCompare(IRequest requestContext, List<CmnCalc> dtoList) {
		ResponseData responseData = new ResponseData(true);

		List<PrdItems> prdList = new ArrayList<PrdItems>();

		for (CmnCalc dto : dtoList) {
			if (dto.getItemId() == null) {
				responseData.setSuccess(false);
				responseData.setMessage("对比的产品ID不能为空!");
				return responseData;
			}
			if (dto.getAge() == null) {
				responseData.setSuccess(false);
				responseData.setMessage("年龄必输!");
				return responseData;
			}
			// 现金价值表
			PrdCashValue cash = new PrdCashValue();
			cash.setInsuranceCoverage(dto.getCoverage() == null ? null : Double.valueOf(dto.getCoverage()));
			cash.setAge(dto.getAge());
			cash.setGender(dto.getGender());
			cash.setSmokeFlag(dto.getSmokeFlag());
			cash.setCurrency(dto.getCurrency());

			PrdItems prd = new PrdItems();
			prd.setItemId(dto.getItemId());
			List<PrdItems> prdItemsList = prdItemsMapper.selectByParam(prd);
			if (CollectionUtils.isEmpty(prdItemsList)) {
				responseData.setSuccess(false);
				responseData.setMessage("查询不到该产品!" + dto.getItemId());
				return responseData;
			}
			prd = prdItemsList.get(0);

			if (prd.getAttSetId() == null) {
				responseData.setSuccess(false);
				responseData.setMessage("该产品未关联产品属性组");
				return responseData;
			} else {
				PrdAttributeSet prdAttributeSet = prdAttributeSetMapper.selectByPrimaryKey(prd.getAttSetId());
				if (!prdAttributeSet.getStatusCode().equals("Y")) {
					responseData.setSuccess(false);
					responseData.setMessage("产品属性组无效");
					return responseData;
				}

				PrdAttributeSetLine prdAttributeSetLine = new PrdAttributeSetLine();
				List<PrdAttributeSetLine> prdAttributeSetLineList = new ArrayList<PrdAttributeSetLine>();
				// prdAttributeSetLine.setDisplayFlag("Y");
				prdAttributeSetLine.setCompareFlag("Y");
				prdAttributeSetLine.setAttSetId(prd.getAttSetId());
				prdAttributeSetLineList = prdAttributeSetLineMapper.selectAllFields(prdAttributeSetLine);
				prd.setPrdAttributeSetLine(prdAttributeSetLineList);
			}
			dto.setItemId(dto.getItemId());
			dto.setAgePremium("PREMIUM" + dto.getAge());

			// 缴费间隔
			Long payTime = 12L;
			// 每期保费
			Double stagePermium = stagePermium(prd, dto, payTime);

			// 年缴保费=每期应缴保费*12/缴费间隔
			double yearPermium = stagePermium * 12 / payTime;
			// 保留两位小数
			yearPermium = new BigDecimal(yearPermium).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			prd.setYearPermium(yearPermium);

			// 年期
			PrdItemSubline prdItemSubline = new PrdItemSubline();
			prdItemSubline.setItemId(dto.getItemId());
			prdItemSubline.setSublineItemName(dto.getProductionAgeLimit());
			List<PrdItemSubline> lineList = prdItemSublineMapper.select(prdItemSubline);
			// 30年后/40年后/50年后/60年后 退保现金价值总额和身故/重疾保障总额
			if (!CollectionUtils.isEmpty(lineList)) {
				cash.setItemId(dto.getItemId());
				cash.setSublineId(lineList.get(0).getSublineId());
				cash.setInsuranceYear(30L);
				List<PrdCashValue> cashList = prdCashValueService.select(requestContext, cash, 1, 10);
				if (!CollectionUtils.isEmpty(cashList)) {
					prd.setSurrenderCash30(
							cashList.get(0).getSurrenderDeposit() + cashList.get(0).getSurrenderNotBonus());
					prd.setDieCash30(cashList.get(0).getDieDeposit() + cashList.get(0).getDieExtra()
							+ cashList.get(0).getDieNotBonus());
				}
				cash.setInsuranceYear(40L);
				cashList = prdCashValueService.select(requestContext, cash, 1, 10);
				if (!CollectionUtils.isEmpty(cashList)) {
					prd.setSurrenderCash40(
							cashList.get(0).getSurrenderDeposit() + cashList.get(0).getSurrenderNotBonus());
					prd.setDieCash40(cashList.get(0).getDieDeposit() + cashList.get(0).getDieExtra()
							+ cashList.get(0).getDieNotBonus());
				}
				cash.setInsuranceYear(50L);
				cashList = prdCashValueService.select(requestContext, cash, 1, 10);
				if (!CollectionUtils.isEmpty(cashList)) {
					prd.setSurrenderCash50(
							cashList.get(0).getSurrenderDeposit() + cashList.get(0).getSurrenderNotBonus());
					prd.setDieCash50(cashList.get(0).getDieDeposit() + cashList.get(0).getDieExtra()
							+ cashList.get(0).getDieNotBonus());
				}
				cash.setInsuranceYear(60L);
				cashList = prdCashValueService.select(requestContext, cash, 1, 10);
				if (!CollectionUtils.isEmpty(cashList)) {
					prd.setSurrenderCash60(
							cashList.get(0).getSurrenderDeposit() + cashList.get(0).getSurrenderNotBonus());
					prd.setDieCash50(cashList.get(0).getDieDeposit() + cashList.get(0).getDieExtra()
							+ cashList.get(0).getDieNotBonus());
				}
			}

			prdList.add(prd);
		}
		return new ResponseData(prdList);
	}

	public ResponseData queryCashValueByAge(IRequest requestContext, List<CmnCalc> dtoList){
		ResponseData responseData = new ResponseData(true);
		List<Map<String, Object>>rows=new ArrayList<Map<String, Object>>();
		for (CmnCalc dto : dtoList) {
			if (dto.getItemId() == null) {
				responseData.setSuccess(false);
				responseData.setMessage("对比的产品ID不能为空!");
				return responseData;
			}
			if (dto.getAge() == null) {
				responseData.setSuccess(false);
				responseData.setMessage("年龄必输!");
				return responseData;
			}
			if (dto.getNextAge() == null) {
				responseData.setSuccess(false);
				responseData.setMessage("预期年龄必输!");
				return responseData;
			}
			
			if((dto.getNextAge()-dto.getAge())<0){
				responseData.setSuccess(false);
				responseData.setMessage("所选年龄必须比当前年龄大!");
				return responseData;
			}
			
			
			// 现金价值表
			PrdCashValue cash = new PrdCashValue();
			cash.setInsuranceCoverage(dto.getCoverage() == null ? null : Double.valueOf(dto.getCoverage()));
			cash.setAge(dto.getAge());
			cash.setGender(dto.getGender());
			cash.setSmokeFlag(dto.getSmokeFlag());
			cash.setCurrency(dto.getCurrency());
			// 年期
			PrdItemSubline prdItemSubline = new PrdItemSubline();
			prdItemSubline.setItemId(dto.getItemId());
			prdItemSubline.setSublineItemName(dto.getProductionAgeLimit());
			List<PrdItemSubline> lineList = prdItemSublineMapper.select(prdItemSubline);
			// 30年后/40年后/50年后/60年后 退保现金价值总额和身故/重疾保障总额
			if (!CollectionUtils.isEmpty(lineList)) {
				cash.setItemId(dto.getItemId());
				cash.setSublineId(lineList.get(0).getSublineId());
				/*cash.setInsuranceYear(dto.getNextAge()-dto.getAge());
				List<PrdCashValue> cashList = prdCashValueService.select(requestContext, cash, 1, 10);
				if (!CollectionUtils.isEmpty(cashList)) {
					map.put("surrenderDeposit",cashList.get(0).getSurrenderDeposit());
					map.put("surrenderCash", cashList.get(0).getSurrenderDeposit() + cashList.get(0).getSurrenderNotBonus());
					map.put("dieDeposit", cashList.get(0).getDieDeposit());
					map.put("dieCash", cashList.get(0).getDieDeposit() + cashList.get(0).getDieExtra()+ cashList.get(0).getDieNotBonus());
				}*/
				List<PrdCashValue> cashList = prdCashValueService.selectAllFields(requestContext, cash);
				if (!CollectionUtils.isEmpty(cashList)) {
					for (PrdCashValue cashValue : cashList) {
						Map<String, Object> map=new HashMap<String, Object>();
						map.put("itemId", dto.getItemId());
						map.put("surrenderDeposit", cashValue.getSurrenderDeposit());
						map.put("surrenderCash", cashValue.getSurrenderDeposit() + cashValue.getSurrenderNotBonus());
						map.put("dieDeposit", cashValue.getDieDeposit());
						map.put("dieCash", cashValue.getDieDeposit() + cashValue.getDieExtra() + cashValue.getDieNotBonus());
						map.put("insuranceYear", cashValue.getInsuranceYear());
						rows.add(map);
					}
				}
			}

		}
		responseData.setRows(rows);;
		return responseData;
	}

	@Override
	public ResponseData prdRadarChart(IRequest requestContext, List<CmnCalc> dtoList) {
		ResponseData responseData = new ResponseData(true);

		PrdItems prd1 = new PrdItems();
		PrdItems prd2 = new PrdItems();
		PrdItems prd3 = new PrdItems();
		// 把后台传来的参数放到map中
		Map map1 = new HashMap();
		Map map2 = new HashMap();
		Map map3 = new HashMap();
		for (int i = 0; i < dtoList.size(); i++) {
			CmnCalc dto = dtoList.get(i);

			if (dto.getItemId() == null) {
				responseData.setSuccess(false);
				responseData.setMessage("对比的产品不能为空!");
				return responseData;
			}

			PrdItems prd = new PrdItems();
			prd.setItemId(dto.getItemId());
			List<PrdItems> prdItemsList = prdItemsMapper.selectByParam(prd);
			if (CollectionUtils.isEmpty(prdItemsList)) {
				responseData.setSuccess(false);
				responseData.setMessage("查询不到该产品!" + dto.getItemId());
				return responseData;
			}
			if (i == 0) {
				map1.put("itemId", dto.getItemId());
				map1.put("paymentMethod", dto.getPaymentMethod());
				map1.put("gender", dto.getGender());
				map1.put("age", dto.getAge());
				map1.put("agePremium", dto.getAgePremium());
				map1.put("smokeFlag", dto.getSmokeFlag());
				map1.put("currency", dto.getCurrency());
				map1.put("securityLevel", dto.getSecurityLevel());
				map1.put("securityArea", dto.getSecurityArea());
				map1.put("selfpay", dto.getSelfpay());
				map1.put("yearPermium", dto.getYearPermium());
				map1.put("nationalityClass", dto.getLivingCity());
				// 保额
				if (dto.getCoverage() != null) {
					map1.put("insuranceCoverage", Double.valueOf(dto.getCoverage()));
				}
				// 年期中文
				map1.put("productionAgeLimit", dto.getProductionAgeLimit());

				prd1 = prdItemsList.get(0);
				if (prd1.getAttSetId() == null) {
					responseData.setSuccess(false);
					responseData.setMessage("该产品未关联产品属性组");
					return responseData;
				}
				PrdAttributeSet prdAttributeSet = prdAttributeSetMapper.selectByPrimaryKey(prd1.getAttSetId());
				if (!prdAttributeSet.getStatusCode().equals("Y") || prdAttributeSet.getEnable().equals("N")) {
					responseData.setSuccess(false);
					responseData.setMessage("雷达图产品属性组无效/未启用");
					return responseData;
				}
			} else if (i == 1) {
				map2.put("itemId", dto.getItemId());
				map2.put("paymentMethod", dto.getPaymentMethod());
				map2.put("gender", dto.getGender());
				map2.put("age", dto.getAge());
				map2.put("agePremium", dto.getAgePremium());
				map2.put("smokeFlag", dto.getSmokeFlag());
				map2.put("currency", dto.getCurrency());
				map2.put("securityLevel", dto.getSecurityLevel());
				map2.put("securityArea", dto.getSecurityArea());
				map2.put("selfpay", dto.getSelfpay());
				map2.put("yearPermium", dto.getYearPermium());
				map2.put("nationalityClass", dto.getLivingCity());
				// 保额
				if (dto.getCoverage() != null) {
					map2.put("insuranceCoverage", Double.valueOf(dto.getCoverage()));
				}
				// 年期中文
				map2.put("productionAgeLimit", dto.getProductionAgeLimit());

				prd2 = prdItemsList.get(0);
			} else if (i == 2) {
				map3.put("itemId", dto.getItemId());
				map3.put("paymentMethod", dto.getPaymentMethod());
				map3.put("gender", dto.getGender());
				map3.put("age", dto.getAge());
				map3.put("agePremium", dto.getAgePremium());
				map3.put("smokeFlag", dto.getSmokeFlag());
				map3.put("currency", dto.getCurrency());
				map3.put("securityLevel", dto.getSecurityLevel());
				map3.put("securityArea", dto.getSecurityArea());
				map3.put("selfpay", dto.getSelfpay());
				map3.put("yearPermium", dto.getYearPermium());
				map3.put("nationalityClass", dto.getLivingCity());
				// 保额
				if (dto.getCoverage() != null) {
					map3.put("insuranceCoverage", Double.valueOf(dto.getCoverage()));
				}
				// 年期中文
				map3.put("productionAgeLimit", dto.getProductionAgeLimit());

				prd3 = prdItemsList.get(0);
			}
		}

		PrdRadarChart prdRadarChart = new PrdRadarChart();
		List<PrdRadarChartItem> itemList = new ArrayList<PrdRadarChartItem>();
		// 产品属性
		PrdAttributeSetLine prdAttributeSetLine = new PrdAttributeSetLine();
		// prdAttributeSetLine.setDisplayFlag("Y");
		prdAttributeSetLine.setCompareFlag("Y");
		prdAttributeSetLine.setAttSetId(prd1.getAttSetId());
		List<PrdAttributeSetLine> prdAttributeSetLineList = prdAttributeSetLineMapper
				.selectAllFields(prdAttributeSetLine);
		PrdRadarChartItem chartItem = new PrdRadarChartItem();
		chartItem.setAttrName("产品名称");
		chartItem.setName1(prd1.getItemName());
		chartItem.setName2(prd2.getItemName());
		chartItem.setName3(prd3.getItemName());
		itemList.add(chartItem);
		chartItem = new PrdRadarChartItem();
		chartItem.setAttrName("所属公司");
		chartItem.setName1(prd1.getSupplierName());
		chartItem.setName2(prd2.getSupplierName());
		chartItem.setName3(prd3.getSupplierName());
		itemList.add(chartItem);
		// 产品信息
		prdRadarChart.setItemList(itemList);

		// 第一层维度
		PrcRadarLine prcRadarLine = new PrcRadarLine();
		prcRadarLine.setStatus("Y");
		prcRadarLine.setAttSetId(prd1.getAttSetId());
		List<PrcRadarLine> radarLineList = prcRadarLineMapper.select(prcRadarLine);
		if (CollectionUtils.isEmpty(radarLineList)) {
			responseData.setSuccess(false);
			responseData.setMessage("所选产品未维护评分!");
			return responseData;
		}

		// 评分详细
		PrcItemScore prcItemScore = new PrcItemScore();
		prcItemScore.setItemId(prd1.getItemId());
		List<PrcItemScore> scoreList1 = new ArrayList<>();
		if (prd1.getItemId() != null) {
			scoreList1 = prcItemScoreMapper.select(prcItemScore);
		}
		prcItemScore.setItemId(prd2.getItemId());
		List<PrcItemScore> scoreList2 = new ArrayList<>();
		if (prd2.getItemId() != null) {
			scoreList2 = prcItemScoreMapper.select(prcItemScore);
		}
		prcItemScore.setItemId(prd3.getItemId());
		List<PrcItemScore> scoreList3 = new ArrayList<>();
		if (prd3.getItemId() != null) {
			scoreList3 = prcItemScoreMapper.select(prcItemScore);
		}

		// 评分以及属性
		List<PrdRadarChartLine> scoreList = new ArrayList<PrdRadarChartLine>();
		for (PrcRadarLine line : radarLineList) {
			PrdRadarChartLine chartLine = new PrdRadarChartLine();
			chartLine.setAttrName(line.getCompDimName());
			for (PrcItemScore score : scoreList1) {
				if (score.getRadarLineId() == line.getLineId()) {
					chartLine.setDetail1(score.getScore() + "");
					continue;
				}
			}
			for (PrcItemScore score : scoreList2) {
				if (score.getRadarLineId() == line.getLineId()) {
					chartLine.setDetail2(score.getScore() + "");
					continue;
				}
			}
			for (PrcItemScore score : scoreList3) {
				if (score.getRadarLineId() == line.getLineId()) {
					chartLine.setDetail3(score.getScore() + "");
					continue;
				}
			}
			// 第二层维度
			PrcRadarLineDetail prcRadarLineDetail = new PrcRadarLineDetail();
			prcRadarLineDetail.setLineId(line.getLineId());
			prcRadarLineDetail.setAttSetId(prd1.getAttSetId());
			List<PrcRadarLineDetail> lineDetailList = prcRadarLineDetailMapper.wsSelectLineDetail(prcRadarLineDetail);
			List<PrdRadarChartLine> lineList = new ArrayList<PrdRadarChartLine>();
			for (PrcRadarLineDetail lineDetail : lineDetailList) {
				PrdRadarChartLine cl = new PrdRadarChartLine();
				cl.setAttrName(lineDetail.getDetailName());
				cl.setCompareRule(lineDetail.getCompareRule());
				if (lineDetail.getSourceType().equals("GDZ")) {
					// 固定值
					for (PrcItemScore score : scoreList1) {
						if (score.getRadarLineDetailId() == lineDetail.getDetailId()) {
							cl.setDetail1(score.getScore().toString());
							continue;
						}
					}
					for (PrcItemScore score : scoreList2) {
						if (score.getRadarLineDetailId() == lineDetail.getDetailId()) {
							cl.setDetail2(score.getScore().toString());
							continue;
						}
					}
					for (PrcItemScore score : scoreList3) {
						if (score.getRadarLineDetailId() == lineDetail.getDetailId()) {
							cl.setDetail3(score.getScore().toString());
							continue;
						}
					}
				} else if (lineDetail.getSourceType().equals("CPSX")) {
					for (PrdAttributeSetLine setLine : prdAttributeSetLineList) {
						// 选择的属性
						if (setLine.getAttId().equals(lineDetail.getAttId())) {
							// 根据属性code得到值
							String fieldCode = setLine.getFieldCode();
							if (StringUtils.isNoneBlank(fieldCode)) {
								if (prd1.getItemId() != null)
									cl.setDetail1(CommonUtil.getMethodValue(prd1, fieldCode.toLowerCase()));
								if (prd2.getItemId() != null)
									cl.setDetail2(CommonUtil.getMethodValue(prd2, fieldCode.toLowerCase()));
								if (prd3.getItemId() != null)
									cl.setDetail3(CommonUtil.getMethodValue(prd3, fieldCode.toLowerCase()));
							}
						}
					}
				} else if (lineDetail.getSourceType().equals("SQL")) {

					String customSql = lineDetail.getSqlContent();
					if (StringUtils.isNoneBlank(customSql)) {
						if (prd1.getItemId() != null) {
							List<HashMap> result = commonService.selectResultsBySql(customSql, map1);
							if (!CollectionUtils.isEmpty(result)) {
								cl.setDetail1(result.get(0).get("value") + "");
							}
						}
						if (prd2.getItemId() != null) {
							List<HashMap> result = commonService.selectResultsBySql(customSql, map2);
							if (!CollectionUtils.isEmpty(result)) {
								cl.setDetail2(result.get(0).get("value") + "");
							}
						}
						if (prd3.getItemId() != null) {
							List<HashMap> result = commonService.selectResultsBySql(customSql, map3);
							if (!CollectionUtils.isEmpty(result)) {
								cl.setDetail3(result.get(0).get("value") + "");
							}
						}
					}
				}
				lineList.add(cl);
			}
			chartLine.setLineList(lineList);

			scoreList.add(chartLine);
		}
		prdRadarChart.setScoreList(scoreList);

		List<PrdRadarChart> list = new ArrayList<PrdRadarChart>();
		list.add(prdRadarChart);
		return new ResponseData(list);

	}

	public Double stagePermium(PrdItems prd, CmnCalc dto, Long payTime) {
		// 基准年缴保费
		Double permium = cmnCalcMapper.queryAgePremium(dto);
		permium = permium == null ? 0 : permium;
		// 基准保额
		Long standardCoverage = 1000L;
		// 现投保额
		Long inputCoverage = dto.getCoverage() == null ? 1000L : dto.getCoverage();
		// 每期保费
		double permiumRate = 1;
		if (prd.getFullyear() == "Y") {
			payTime = 12L;
		} else if (prd.getOneyear() == "Y") {
			payTime = 12L;
		} else if (prd.getHalfyear() == "Y") {
			payTime = 6L;
		} else if (prd.getQuarter() == "Y") {
			payTime = 4L;
		} else if (prd.getOnemonth() == "Y") {
			payTime = 1L;
		}
		// 每期应缴保费=每期保费系数*基准年缴保费*现投保额/基准保额
		double stagePermium = permiumRate * permium * inputCoverage / standardCoverage;
		// 现金价值表
		logger.info("每期应缴保费:" + stagePermium);
		return new BigDecimal(stagePermium).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	@Override
	public List<PrdItems> queryitemClass(IRequest requestContext, PrdItems dto) {
		return prdItemsMapper.queryitemClass(dto);
	}

	@Override
	public List<PrdItems> selectByChannel(IRequest requestContext, PrdItems dto) {
		return prdItemsMapper.selectByChannel(dto);
	}

	@Override
	public List<ImportResponse> verificationItems(IRequest request, List<List<String>> dataList,
			List<PrdItems> beanList) {
		List<ImportResponse> listResPonse = new ArrayList<ImportResponse>();
		ImportResponse response = new ImportResponse();
		Integer sheetNum = 1;
		Integer lineNum = 2;
		List<String> title = dataList.get(0);
		for (int i = 2; i < dataList.size(); i++) {
			lineNum++;
			int mun = 0;
			PrdItems prdItems = new PrdItems();
			if (StringUtils.isNotBlank(dataList.get(i).get(mun))) {
				prdItems.setItemName(dataList.get(i).get(mun));
				List<PrdItems> items = prdItemsMapper.selectByItemName(prdItems);
				if (!CollectionUtils.isEmpty(items)) {
					response = new ImportResponse(sheetNum, lineNum, ERROR, title.get(mun),
							"产品名称:" + dataList.get(i).get(mun) + ",已存在！");
					listResPonse.add(response);
					continue;
				}
			} else {
				response = new ImportResponse(sheetNum, lineNum, ERROR, title.get(mun), "产品名称不能为空！");
				listResPonse.add(response);
				continue;
			}
			mun++;
			// 大分类
			if (StringUtils.isNotBlank(dataList.get(i).get(1))) {
				Map<String, String> params = new HashMap<String, String>();
				params.put("statusCode", "Y");
				List<Map<String, String>> list = (List<Map<String, String>>) lovService.selectDatas(request,
						"PRD_DIVISION", params, 1, 1000);
				for (Map<String, String> map : list) {
					if (map.get("meaning").equals(dataList.get(i).get(mun))) {
						prdItems.setBigClass(map.get("value"));
					}
				}
				if (prdItems.getBigClass() == null) {
					response = new ImportResponse(sheetNum, lineNum, ERROR, title.get(1),
							"查询不到值：" + dataList.get(mun).get(1));
					listResPonse.add(response);
					continue;
				}

			} else {
				response = new ImportResponse(sheetNum, lineNum, ERROR, title.get(mun), "不能为空！");
				listResPonse.add(response);
				continue;
			}
			mun++;
			// 中分类
			if (StringUtils.isNotBlank(dataList.get(i).get(mun))) {
				Map<String, String> params = new HashMap<String, String>();
				params.put("statusCode", "Y");
				params.put("className1", prdItems.getBigClass());
				List<Map<String, String>> list = (List<Map<String, String>>) lovService.selectDatas(request,
						"PRD_CLASS", params, 1, 1000);
				for (Map<String, String> map : list) {
					if (map.get("meaning").equals(dataList.get(i).get(mun))) {
						prdItems.setMidClass(map.get("value"));
					}
				}
				if (prdItems.getMidClass() == null) {
					response = new ImportResponse(sheetNum, lineNum, ERROR, title.get(mun),
							"查询不到值：" + dataList.get(i).get(mun));
					listResPonse.add(response);
					continue;
				}
			} else {
				prdItems.setMidClass("");
			}
			mun++;
			// 小分类
			if (StringUtils.isNotBlank(dataList.get(i).get(mun))) {
				Map<String, String> params = new HashMap<String, String>();
				params.put("statusCode", "Y");
				params.put("className1", prdItems.getBigClass());
				params.put("className2", prdItems.getMidClass());
				List<Map<String, String>> list = (List<Map<String, String>>) lovService.selectDatas(request,
						"PRD_CATAGORY", params, 1, 1000);
				for (Map<String, String> map : list) {
					if (map.get("meaning").equals(dataList.get(i).get(mun))) {
						prdItems.setMinClass(map.get("value"));
					}
				}
				if (prdItems.getMinClass() == null) {
					response = new ImportResponse(sheetNum, lineNum, ERROR, title.get(mun),
							"查询不到值：" + dataList.get(i).get(mun));
					listResPonse.add(response);
					continue;
				}
			} else {
				prdItems.setMinClass("");
			}
			mun++;
			// 产品属性组
			if (StringUtils.isNotBlank(dataList.get(i).get(mun))) {
				PrdAttributeSet prdAttributeSet = new PrdAttributeSet();
				prdAttributeSet.setStatusCode("Y");
				prdAttributeSet.setSetName(dataList.get(i).get(mun));
				List<PrdAttributeSet> list = prdAttributeSetMapper.select(prdAttributeSet);
				if (CollectionUtils.isEmpty(list)) {
					response = new ImportResponse(sheetNum, lineNum, ERROR, title.get(mun),
							"数据库中不存在:" + dataList.get(i).get(mun));
					listResPonse.add(response);
				} else {
					prdItems.setAttSetId(list.get(0).getAttSetId());
				}
			} else {
				response = new ImportResponse(sheetNum, lineNum, ERROR, title.get(mun), "不能为空！");
				listResPonse.add(response);
				continue;
			}
			mun++;
			// 产品公司
			if (StringUtils.isNotBlank(dataList.get(i).get(mun))) {
				SpeSupplier speSupplier = new SpeSupplier();
				speSupplier.setStatusCode("Y");
				speSupplier.setName(dataList.get(i).get(mun));
				List<SpeSupplier> list = speSupplierMapper.selectByName(speSupplier);
				if (CollectionUtils.isEmpty(list)) {
					response = new ImportResponse(sheetNum, lineNum, ERROR, title.get(mun),
							"数据库中不存在:" + dataList.get(i).get(mun));
					listResPonse.add(response);
				} else {
					prdItems.setSupplierId(list.get(0).getSupplierId());
				}
			} else {
				response = new ImportResponse(sheetNum, lineNum, ERROR, title.get(mun), "不能为空！");
				listResPonse.add(response);
				continue;
			}
			mun++;
			// 状态
			if (StringUtils.isNotBlank(dataList.get(i).get(mun))) {
				if (dataList.get(i).get(mun).equals("有效")) {
					prdItems.setEnabledFlag("Y");
				} else if (dataList.get(i).get(mun).equals("无效")) {
					prdItems.setEnabledFlag("N");
				} else {
					response = new ImportResponse(sheetNum, lineNum, ERROR, title.get(mun), "格式有误！");
					listResPonse.add(response);
					continue;
				}
			} else {
				response = new ImportResponse(sheetNum, lineNum, ERROR, title.get(mun), "不能为空！");
				listResPonse.add(response);
				continue;
			}
			mun++;
			// 整付
			if (StringUtils.isNotBlank(dataList.get(i).get(mun))) {
				if (dataList.get(i).get(mun).equals("是")) {
					prdItems.setFullyear("Y");
				} else if (dataList.get(i).get(7).equals("否")) {
					prdItems.setFullyear("N");
				} else {
					response = new ImportResponse(sheetNum, lineNum, ERROR, title.get(mun), "格式有误！");
					listResPonse.add(response);
				}
			} else {
				response = new ImportResponse(sheetNum, lineNum, ERROR, title.get(mun), "不能为空！");
				listResPonse.add(response);
			}

			mun++;
			// 年缴
			if (StringUtils.isNotBlank(dataList.get(i).get(mun))) {
				if (dataList.get(i).get(mun).equals("是")) {
					prdItems.setOneyear("Y");
				} else if (dataList.get(i).get(mun).equals("否")) {
					prdItems.setOneyear("N");
				} else {
					response = new ImportResponse(sheetNum, lineNum, ERROR, title.get(mun), "格式有误！");
					listResPonse.add(response);
				}
			} else {
				response = new ImportResponse(sheetNum, lineNum, ERROR, title.get(mun), "不能为空！");
				listResPonse.add(response);
			}

			mun++;
			// 半年缴
			if (StringUtils.isNotBlank(dataList.get(i).get(mun))) {
				if (dataList.get(i).get(mun).equals("是")) {
					prdItems.setHalfyear("Y");
				} else if (dataList.get(i).get(mun).equals("否")) {
					prdItems.setHalfyear("N");
				} else {
					response = new ImportResponse(sheetNum, lineNum, ERROR, title.get(mun), "格式有误！");
					listResPonse.add(response);
				}
			} else {
				response = new ImportResponse(sheetNum, lineNum, ERROR, title.get(mun), "不能为空！");
				listResPonse.add(response);
			}

			mun++;
			// 季缴
			if (StringUtils.isNotBlank(dataList.get(i).get(mun))) {
				if (dataList.get(i).get(mun).equals("是")) {
					prdItems.setQuarter("Y");
				} else if (dataList.get(i).get(mun).equals("否")) {
					prdItems.setQuarter("N");
				} else {
					response = new ImportResponse(sheetNum, lineNum, ERROR, title.get(mun), "格式有误！");
					listResPonse.add(response);
				}
			} else {
				response = new ImportResponse(sheetNum, lineNum, ERROR, title.get(mun), "不能为空！");
				listResPonse.add(response);
			}

			mun++;
			// 月缴
			if (StringUtils.isNotBlank(dataList.get(i).get(mun))) {
				if (dataList.get(i).get(mun).equals("是")) {
					prdItems.setOnemonth("Y");
				} else if (dataList.get(i).get(mun).equals("否")) {
					prdItems.setOnemonth("N");
				} else {
					response = new ImportResponse(sheetNum, lineNum, ERROR, title.get(mun), "格式有误！");
					listResPonse.add(response);
				}
			} else {
				response = new ImportResponse(sheetNum, lineNum, ERROR, title.get(mun), "不能为空！");
				listResPonse.add(response);
			}

			mun++;
			// 预缴
			if (StringUtils.isNotBlank(dataList.get(i).get(mun))) {
				if (dataList.get(i).get(mun).equals("是")) {
					prdItems.setPrepayFlag("Y");
				} else if (dataList.get(i).get(mun).equals("否")) {
					prdItems.setPrepayFlag("N");
				} else {
					response = new ImportResponse(sheetNum, lineNum, ERROR, title.get(mun), "格式有误！");
					listResPonse.add(response);
				}
			} else {
				response = new ImportResponse(sheetNum, lineNum, ERROR, title.get(mun), "不能为空！");
				listResPonse.add(response);
			}

			for (int j = 1; j <= 100; j++) {
				mun++;
				if (StringUtils.isNotBlank(dataList.get(i).get(mun))) {
					String value = dataList.get(i).get(mun);
					PrdAttribute prdAttribute = new PrdAttribute();
					prdAttribute.setFieldCode(("attribute" + j).toUpperCase());
					// 判断属性是否是值列表，如果是，则转换
					List<PrdAttribute> list = prdAttributeMapper.select(prdAttribute);
					if (!CollectionUtils.isEmpty(list)) {
						if (list.get(0).getFieldType().equals("VALUESET")) {
							// 值列表
							ClbCodeValue clbCode = new ClbCodeValue();
							clbCode.setCodeId(list.get(0).getValueSetId());
							List<ClbCodeValue> codeList = clbCodeService.selectCodeValues(request, clbCode);
							boolean hasCode = false;
							for (ClbCodeValue code : codeList) {
								if (value.equals(code.getMeaning())) {
									value = code.getValue();
									hasCode = true;
									continue;
								}
							}
							if (!hasCode) {
								response = new ImportResponse(sheetNum, lineNum, ERROR, title.get(mun),
										"快码中:" + dataList.get(i).get(mun) + ",不存在！");
								listResPonse.add(response);
							}
						}
					}
					CommonUtil.classSetMethod(prdItems, "ATTRIBUTE" + j, value);
				}
			}

			beanList.add(prdItems);
		}
		return listResPonse;
	}

	@Override
	public List<ImportResponse> verificationPaymode(IRequest request, List<List<String>> dataList,
			List<PrdItems> itemList, List<PrdItemPaymode> beanList) {
		List<ImportResponse> listResPonse = new ArrayList<ImportResponse>();
		ImportResponse response = new ImportResponse();
		Integer sheetNum = 2;
		Integer lineNum = 2;
		List<String> title = dataList.get(0);
		for (int i = 2; i < dataList.size(); i++) {
			lineNum++;
			PrdItemPaymode prdItemPaymode = new PrdItemPaymode();
			prdItemPaymode.setEnabledFlag("Y");
			if (StringUtils.isNotBlank(dataList.get(i).get(0))) {
				boolean hasExist = false;
				// 先看excel中是否存在
				for (PrdItems item : itemList) {
					if (item.getItemName().equals(dataList.get(i).get(0))) {
						hasExist = true;
					}
				}
				// excel 中不存在，则看数据库中是否存在
				if (!hasExist) {
					PrdItems prdItems = new PrdItems();
					prdItems.setItemName(dataList.get(i).get(0));
					List<PrdItems> items = prdItemsMapper.selectByItemName(prdItems);
					if (!CollectionUtils.isEmpty(items)) {
						hasExist = true;
					}
				}
				if (hasExist) {
					prdItemPaymode.setItemName(dataList.get(i).get(0));
				} else {
					response = new ImportResponse(sheetNum, lineNum, ERROR, title.get(0),
							"产品名称:" + dataList.get(i).get(0) + ",不存在!");
					listResPonse.add(response);
					continue;
				}
			} else {
				response = new ImportResponse(sheetNum, lineNum, ERROR, title.get(0), "产品名称不能为空！");
				listResPonse.add(response);
				continue;
			}

			if (StringUtils.isNotBlank(dataList.get(i).get(1))) {
				String valueCode = clbCodeService.getCodeValueByMeaning(request, "PUB.CURRENCY",
						dataList.get(i).get(1));
				if (StringUtils.isBlank(valueCode)) {
					response = new ImportResponse(sheetNum, lineNum, ERROR, title.get(1),
							"快码中该币种:" + dataList.get(i).get(1) + ",不存在！");
					listResPonse.add(response);
				} else {
					prdItemPaymode.setCurrencyCode(valueCode);
				}
			} else {
				response = new ImportResponse(sheetNum, lineNum, ERROR, title.get(1), "币种不能为空！");
				listResPonse.add(response);
				continue;
			}

			beanList.add(prdItemPaymode);
		}
		return listResPonse;
	}

	@Override
	public List<ImportResponse> verificationSubline(IRequest request, List<List<String>> dataList,
			List<PrdItems> itemList, List<PrdItemSubline> beanList) {
		List<ImportResponse> listResPonse = new ArrayList<ImportResponse>();
		ImportResponse response = new ImportResponse();
		Integer sheetNum = 3;
		Integer lineNum = 2;
		List<String> title = dataList.get(0);
		for (int i = 2; i < dataList.size(); i++) {
			lineNum++;
			PrdItemSubline prdItemSubline = new PrdItemSubline();
			prdItemSubline.setEnabledFlag("Y");
			if (StringUtils.isNotBlank(dataList.get(i).get(0))) {
				boolean hasExist = false;
				// 先看excel中是否存在
				for (PrdItems item : itemList) {
					if (item.getItemName().equals(dataList.get(i).get(0))) {
						hasExist = true;
					}
				}
				// excel 中不存在，则看数据库中是否存在
				if (!hasExist) {
					PrdItems prdItems = new PrdItems();
					prdItems.setItemName(dataList.get(i).get(0));
					List<PrdItems> items = prdItemsMapper.selectByItemName(prdItems);
					if (!CollectionUtils.isEmpty(items)) {
						hasExist = true;
					}
				}
				if (hasExist) {
					prdItemSubline.setItemName(dataList.get(i).get(0));
				} else {
					response = new ImportResponse(sheetNum, lineNum, ERROR, title.get(0),
							"产品名称:" + dataList.get(i).get(0) + ",不存在!");
					listResPonse.add(response);
					continue;
				}
			} else {
				response = new ImportResponse(sheetNum, lineNum, ERROR, title.get(0), "产品名称不能为空！");
				listResPonse.add(response);
				continue;
			}

			if (StringUtils.isNotBlank(dataList.get(i).get(1))) {
				String valueCode = clbCodeService.getCodeValueByMeaning(request, "PRD.SUBLINE_PRODUCT_TYPE",
						dataList.get(i).get(1));
				if (StringUtils.isBlank(valueCode)) {
					response = new ImportResponse(sheetNum, lineNum, ERROR, title.get(1),
							"快码中该子产品类型:" + dataList.get(i).get(1) + ",不存在！");
					listResPonse.add(response);
					continue;
				} else {
					prdItemSubline.setSublineItemType(valueCode);
					if (valueCode.equals("FIXED_VALUE")) {
						// 固定值
						prdItemSubline.setSublineItemName(dataList.get(i).get(2));
						if (StringUtils.isNotBlank(dataList.get(i).get(2))) {
							prdItemSubline.setYearPeriod(dataList.get(i).get(2));
						} else {
							prdItemSubline.setYearPeriod(null);
						}
					} else if (valueCode.equals("LIFE_LONG")) {
						// 终身
						// 单价
						prdItemSubline.setSublineItemName("终身");
						if (StringUtils.isNotBlank(dataList.get(i).get(4))) {
							prdItemSubline.setPrice(dataList.get(i).get(4));
						} else {
							prdItemSubline.setPrice(null);
						}
					} else if (valueCode.equals("FULL_PAY")) {
						// 整付
						// 单价
						prdItemSubline.setSublineItemName("整付");
						if (StringUtils.isNotBlank(dataList.get(i).get(4))) {
							prdItemSubline.setPrice(dataList.get(i).get(4));
						} else {
							prdItemSubline.setPrice(null);
						}
					} else if (valueCode.equals("SPECIFY_AGE")) {
						// 至指定年龄
						// 指定年龄
						prdItemSubline.setSublineItemName("至被保人" + dataList.get(i).get(3) + "岁");
						if (StringUtils.isNotBlank(dataList.get(i).get(3))) {
							prdItemSubline.setAge(Long.valueOf(dataList.get(i).get(3)));
						} else {
							prdItemSubline.setAge(null);
						}
					}
				}
			} else {
				response = new ImportResponse(sheetNum, lineNum, ERROR, title.get(1), "子产品类型不能为空！");
				listResPonse.add(response);
				continue;
			}
			// 是否在线支付
			if (StringUtils.isNotBlank(dataList.get(i).get(5))) {
				if (dataList.get(i).get(5).equals("是")) {
					prdItemSubline.setOnlinePaymentFlag("Y");
				} else if (dataList.get(i).get(5).equals("否")) {
					prdItemSubline.setOnlinePaymentFlag("N");
				} else {
					response = new ImportResponse(sheetNum, lineNum, ERROR, title.get(5), "格式有误！");
					listResPonse.add(response);
					continue;
				}
			} else {
				response = new ImportResponse(sheetNum, lineNum, ERROR, title.get(5), "是否在线支付不能为空！");
				listResPonse.add(response);
				continue;
			}
			beanList.add(prdItemSubline);
		}
		return listResPonse;
	}

	@Override
	public List<ImportResponse> verificationPlan(IRequest request, List<List<String>> dataList, List<PrdItems> itemList,
			List<PrdItemSecurityPlan> beanList) {
		List<ImportResponse> listResPonse = new ArrayList<ImportResponse>();
		ImportResponse response = new ImportResponse();
		Integer sheetNum = 4;
		Integer lineNum = 2;
		List<String> title = dataList.get(0);
		for (int i = 2; i < dataList.size(); i++) {
			lineNum++;
			PrdItemSecurityPlan prdItemSecurityPlan = new PrdItemSecurityPlan();
			prdItemSecurityPlan.setEnabledFlag("Y");
			if (StringUtils.isNotBlank(dataList.get(i).get(0))) {
				boolean hasExist = false;
				// 先看excel中是否存在
				for (PrdItems item : itemList) {
					if (item.getItemName().equals(dataList.get(i).get(0))) {
						hasExist = true;
					}
				}
				// excel 中不存在，则看数据库中是否存在
				if (!hasExist) {
					PrdItems prdItems = new PrdItems();
					prdItems.setItemName(dataList.get(i).get(0));
					List<PrdItems> items = prdItemsMapper.selectByItemName(prdItems);
					if (!CollectionUtils.isEmpty(items)) {
						hasExist = true;
					}
				}
				if (hasExist) {
					prdItemSecurityPlan.setItemName(dataList.get(i).get(0));
				} else {
					response = new ImportResponse(sheetNum, lineNum, ERROR, title.get(0),
							"产品名称:" + dataList.get(i).get(0) + ",不存在!");
					listResPonse.add(response);
					continue;
				}
			} else {
				response = new ImportResponse(sheetNum, lineNum, ERROR, title.get(0), "产品名称不能为空！");
				listResPonse.add(response);
				continue;
			}
			// 级别
			if (StringUtils.isNotBlank(dataList.get(i).get(1))) {
				prdItemSecurityPlan.setSecurityLevel(dataList.get(i).get(1));
			} else {
				response = new ImportResponse(sheetNum, lineNum, ERROR, title.get(1), "不能为空！");
				listResPonse.add(response);
				continue;
			}
			// 地区
			if (StringUtils.isNotBlank(dataList.get(i).get(2))) {
				prdItemSecurityPlan.setSecurityRegion(dataList.get(i).get(2));
			} else {
				response = new ImportResponse(sheetNum, lineNum, ERROR, title.get(2), "不能为空！");
				listResPonse.add(response);
				continue;
			}
			beanList.add(prdItemSecurityPlan);
		}
		return listResPonse;
	}

	@Override
	public List<ImportResponse> verificationSelfpay(IRequest request, List<List<String>> dataList,
			List<PrdItems> itemList, List<PrdItemSelfpay> beanList) {
		List<ImportResponse> listResPonse = new ArrayList<ImportResponse>();
		ImportResponse response = new ImportResponse();
		Integer sheetNum = 5;
		Integer lineNum = 2;
		List<String> title = dataList.get(0);
		for (int i = 2; i < dataList.size(); i++) {
			lineNum++;
			PrdItemSelfpay prdItemSelfpay = new PrdItemSelfpay();
			prdItemSelfpay.setEnabledFlag("Y");
			if (StringUtils.isNotBlank(dataList.get(i).get(0))) {
				boolean hasExist = false;
				// 先看excel中是否存在
				for (PrdItems item : itemList) {
					if (item.getItemName().equals(dataList.get(i).get(0))) {
						hasExist = true;
					}
				}
				// excel 中不存在，则看数据库中是否存在
				if (!hasExist) {
					PrdItems prdItems = new PrdItems();
					prdItems.setItemName(dataList.get(i).get(0));
					List<PrdItems> items = prdItemsMapper.selectByItemName(prdItems);
					if (!CollectionUtils.isEmpty(items)) {
						hasExist = true;
					}
				}
				if (hasExist) {
					prdItemSelfpay.setItemName(dataList.get(i).get(0));
				} else {
					response = new ImportResponse(sheetNum, lineNum, ERROR, title.get(0),
							"产品名称:" + dataList.get(i).get(0) + ",不存在!");
					listResPonse.add(response);
					continue;
				}
			} else {
				response = new ImportResponse(sheetNum, lineNum, ERROR, title.get(0), "产品名称不能为空！");
				listResPonse.add(response);
				continue;
			}
			// 自付选项
			if (StringUtils.isNotBlank(dataList.get(i).get(1))) {
				prdItemSelfpay.setSelfpay(dataList.get(i).get(1));
			} else {
				response = new ImportResponse(sheetNum, lineNum, ERROR, title.get(1), "不能为空！");
				listResPonse.add(response);
				continue;
			}
			beanList.add(prdItemSelfpay);
		}
		return listResPonse;
	}

	@Override
	public void importInitialData(IRequest request, List<PrdItems> prdItemsList, List<PrdItemPaymode> modeList,
			List<PrdItemSubline> SublineList, List<PrdItemSecurityPlan> planList, List<PrdItemSelfpay> selfList) {
		// 产品
		batchUpdate(request, prdItemsList);
		// 币种
		for (PrdItemPaymode mode : modeList) {
			PrdItems prdItems = new PrdItems();
			prdItems.setItemName(mode.getItemName());
			List<PrdItems> items = prdItemsMapper.selectByItemName(prdItems);
			mode.setItemId(items.get(0).getItemId());
			// 是否是否已经存在
			List<PrdItemPaymode> list = prdItemPaymodeMapper.select(mode);
			if (CollectionUtils.isEmpty(list)) {
				prdItemPaymodeMapper.insertSelective(mode);
			} else {
				mode.setPaymodeId(list.get(0).getPaymodeId());
				prdItemPaymodeMapper.updateByPrimaryKeySelective(mode);
			}
		}
		// 子产品
		for (PrdItemSubline subline : SublineList) {
			PrdItems prdItems = new PrdItems();
			prdItems.setItemName(subline.getItemName());
			List<PrdItems> items = prdItemsMapper.selectByItemName(prdItems);
			subline.setItemId(items.get(0).getItemId());
			// 是否是否已经存在
			List<PrdItemSubline> list = prdItemSublineMapper.select(subline);
			if (CollectionUtils.isEmpty(list)) {
				prdItemSublineMapper.insertSelective(subline);
			} else {
				subline.setSublineId(list.get(0).getSublineId());
				prdItemSublineMapper.updateByPrimaryKeySelective(subline);
			}
		}
		// 计划
		for (PrdItemSecurityPlan plan : planList) {
			PrdItems prdItems = new PrdItems();
			prdItems.setItemName(plan.getItemName());
			List<PrdItems> items = prdItemsMapper.selectByItemName(prdItems);
			plan.setItemId(items.get(0).getItemId());
			// 是否是否已经存在
			List<PrdItemSecurityPlan> list = prdItemSecurityPlanMapper.select(plan);
			if (CollectionUtils.isEmpty(list)) {
				prdItemSecurityPlanMapper.insertSelective(plan);
			} else {
				plan.setPlanId(list.get(0).getPlanId());
				prdItemSecurityPlanMapper.updateByPrimaryKeySelective(plan);
			}
		}
		// 自付选项
		for (PrdItemSelfpay self : selfList) {
			PrdItems prdItems = new PrdItems();
			prdItems.setItemName(self.getItemName());
			List<PrdItems> items = prdItemsMapper.selectByItemName(prdItems);
			self.setItemId(items.get(0).getItemId());
			// 是否是否已经存在
			List<PrdItemSelfpay> list = prdItemSelfpayMapper.select(self);
			if (CollectionUtils.isEmpty(list)) {
				prdItemSelfpayMapper.insertSelective(self);
			} else {
				self.setSelfpayId(list.get(0).getSelfpayId());
				prdItemSelfpayMapper.updateByPrimaryKeySelective(self);
			}
		}
	}

	@Override
	public Long selectItemIdByItemName(String itemName) {
		return prdItemsMapper.selectItemIdByItemName(itemName);
	}

	@Override
	public String selectSupplierAgeTypeByItem(Long itemId) {
		return prdItemsMapper.selectSupplierAgeTypeByItem(itemId);
	}
}