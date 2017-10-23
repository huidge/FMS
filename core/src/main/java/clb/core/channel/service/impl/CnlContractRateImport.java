package clb.core.channel.service.impl;

import clb.core.fnd.service.impl.AbstractImportExecute;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.CodeValue;
import com.hand.hap.system.mapper.CodeValueMapper;

import clb.core.channel.dto.CnlChannel;
import clb.core.channel.dto.CnlChannelContact;
import clb.core.channel.dto.CnlChannelContract;
import clb.core.channel.dto.CnlContractRate;
import clb.core.channel.dto.CnlContractRateHistory;
import clb.core.channel.dto.CnlContractRole;
import clb.core.channel.dto.CnlLevel;
import clb.core.channel.mapper.CnlContractRateMapper;
import clb.core.channel.service.ICnlChannelContactService;
import clb.core.channel.service.ICnlChannelContractService;
import clb.core.channel.service.ICnlChannelService;
import clb.core.channel.service.ICnlContractRateHistoryService;
import clb.core.channel.service.ICnlContractRateService;
import clb.core.channel.service.ICnlContractRoleService;
import clb.core.commission.dto.CmnChannelRatioDetail;
import clb.core.common.service.ISequenceService;
import clb.core.common.utils.DateUtil;
import clb.core.fnd.dto.ImportTemp;
import clb.core.fnd.service.IImportTempService;
import clb.core.fnd.service.impl.AbstractImportExecute;
import clb.core.fnd.utils.Constants;
import clb.core.fnd.utils.ValidationTableException;
import clb.core.production.dto.PrdItemSubline;
import clb.core.production.dto.PrdItems;
import clb.core.production.service.IPrdItemSublineService;
import clb.core.production.service.IPrdItemsService;
import clb.core.supplier.service.ISpeSupplierService;
import clb.core.system.dto.ClbEmployee;
import clb.core.system.dto.ClbUser;
import clb.core.system.service.IClbEmployeeService;
import clb.core.system.service.IClbUserService;

/**
 * 渠道数据导入
 * 
 * @author Administrator
 */
@Component
public class CnlContractRateImport extends AbstractImportExecute {

	@Autowired
	private IImportTempService importTempService;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private CodeValueMapper codeValueMapper;

	@Autowired
	private ISequenceService sequenceService;

	@Autowired
	private ISpeSupplierService speSupplierService;

	@Autowired
	private ICnlChannelService cnlChannelService;

	@Autowired
	private IClbUserService clbUserService;

	@Autowired
	private IClbEmployeeService clbEmployeeService;

	@Autowired
	private ICnlChannelContractService cnlChannelContractService;

	@Autowired
	private ICnlChannelContactService cnlChannelContactService;

	@Autowired
	private ICnlContractRateService cnlContractRateService;
	
	@Autowired
    private CnlContractRateMapper cnlContractRateMapper;

	@Autowired
	private ICnlContractRoleService cnlContractRoleService;
	
	@Autowired
	private IPrdItemsService prdItemsService;
	
	@Autowired
	private IPrdItemSublineService prdItemSublineService;
    
    @Autowired
    private ICnlContractRateHistoryService cnlContractRateHistoryService;

	/**
	 * @Description: 数据导入
	 */
	@Override
	public void execute(Map<String, Object> args) throws ValidationTableException, Exception {
		Long importBatchId = (Long) args.get("importBatchId");
		IRequest request = (IRequest) args.get("request");

		String[] locales = request.getLocale().split("_");
		Locale locale = new Locale(locales[0], locales[1]);

		int countImportBatchId = 0;
		// 校验该批次数据是否已经导入过
		if (null != importBatchId) {
			// countImportBatchId =
			// supplierAllotRatioService.selectCountImportBatchIdByAttrIbute(importBatchId);
		}

		if (countImportBatchId > 0) {
			throw new ValidationTableException(Constants.BATCH_REPEAT, null);
		}
		Set<CnlChannelContract> contractSet = new HashSet<CnlChannelContract>();
		List<CnlContractRate> contractRatelist = validateData(contractSet, importBatchId, request);

		if (importTempService.selectErrorCount(importBatchId) == 0) {// 验证没有错误
			contractRatelist.forEach(contractRate -> {
				List<CnlContractRate> cnlContractRateList = cnlContractRateService.select(request, contractRate, 1,
						10);
				if (null != cnlContractRateList && cnlContractRateList.size() == 1) {
					// 导入渠道合约信息和渠道费率
					cnlContractRateList.get(0).setAttribute1(contractRate.getAttribute1());
					cnlContractRateService.updateByPrimaryKey(request, cnlContractRateList.get(0));

				} else if (null == cnlContractRateList || cnlContractRateList.size() == 0) {
					// 渠道费率
					cnlContractRateService.insertSelective(request, contractRate);
				}
			});
			contractSet.forEach(cnlContract -> {
                CnlContractRateHistory cnlContractRateHistory = new CnlContractRateHistory();
                cnlContractRateHistory.setChannelContractId(cnlContract.getChannelContractId());
                cnlContractRateHistory.setRateLevelName("自定义级别");
                cnlContractRateHistoryService.insertSelective(request, cnlContractRateHistory);
			});
			/*for (CnlContractRate cnlContractRate : contractRatelist) {
				List<CnlContractRate> cnlContractRateList = cnlContractRateService.select(request, cnlContractRate, 1, 10);
				if (null != cnlContractRateList && cnlContractRateList.size() == 1) {
					// 导入渠道合约信息和渠道费率
					cnlContractRateList.get(0).setAttribute1(cnlContractRate.getAttribute1());
					cnlContractRateService.updateByPrimaryKey(request, cnlContractRateList.get(0));

				} else if (null == cnlContractRateList || cnlContractRateList.size() == 0) {
					// 渠道费率
					cnlContractRateService.insertSelective(request, cnlContractRate);
				}
			}*/
		}
		StringBuffer errorMsg = new StringBuffer("");
	}

	/**
	 * @return
	 * @throws IntrospectionException
	 * @Description: 数据验证
	 * @author
	 */
	public List<CnlContractRate> validateData(Set<CnlChannelContract> contractSet, Long importBatchId, IRequest request)
			throws ValidationTableException, Exception {
		String[] locales = request.getLocale().split("_");
		Locale locale = new Locale(locales[0], locales[1]);

		PropertyDescriptor attributeTitle = null;
		PropertyDescriptor attributeValue = null;
		List<ImportTemp> importTemps = new ArrayList<ImportTemp>();// 根据流水号查得的所有临时表数据
		
		List<CnlContractRate> contractRateList = new ArrayList<CnlContractRate>();// 将临时表的数据转换成供应商比例维护数据
		// 将没行数据的供应商id、客户id、联系人id存储，方便匹配是否有重复数据
		Map<Map<Long, Long>, List<Long>> custContactSupMap = new HashMap<Map<Long, Long>, List<Long>>();
		List<String> uniqueList = new ArrayList<String>(); //唯一性校验

		if (null != importBatchId) {
			// 查出所有当前批次的数据
			importTemps = importTempService.selectImportData(importBatchId, 1, Constants.MAX_NUMBER, request);
		}
		List<String> sheets = new ArrayList<String>();// sheet页操作
		ImportTemp importTemp = new ImportTemp();// 循环对象
		// 对当前批次数据做迭代循环校验
		if (null != importTemps && importTemps.size() > 0) {
			Iterator<ImportTemp> iterator = importTemps.iterator();
			ImportTemp importTempTitle = null;
			StringBuffer errorTitleMessage = new StringBuffer("");// 检查标题行是否有错
			// 防止标题错误重复添加
			boolean isAppendErrorTitle = false;
			while (iterator.hasNext()) {
				importTemp = iterator.next();
				StringBuffer errorMessage = new StringBuffer("");
				if (!sheets.contains(importTemp.getSheet())) {// 标题行
					sheets.add(importTemp.getSheet());
					if (!"".equals(errorTitleMessage.toString())) {// 行标题有错误
						importTempTitle.setImportMessage(errorTitleMessage.toString());
						importTempService.updateError(importTempTitle, request);
					}
					errorTitleMessage = new StringBuffer("");
					isAppendErrorTitle = false;
					importTempTitle = importTemp;
					// 标题行重复性验证
					Set<String> titleSet = new HashSet<String>();
					for (int i = 1; i <= Constants.MAX_ATTR; i++) {
						// 标题字段
						attributeTitle = new PropertyDescriptor(Constants.ATTRIBUTE + (i), importTempTitle.getClass());
						Method methodRead = attributeTitle.getReadMethod();
						String objTitle = (String) methodRead.invoke(importTempTitle);
						if (null != objTitle && !"".equals(objTitle.trim())) {
							if (titleSet.contains(objTitle)) {
								errorTitleMessage.append(messageSource.getMessage(Constants.TITLE_EXCEPTION,
										new Object[] { objTitle.trim() }, locale)).append(";");
							} else {
								titleSet.add(objTitle);
							}
						}
					}
				} else if (importTemp.getLineNumber() != 1) { // 第一行为标题行
					// 合约费率
					 CnlContractRate cnlContractRate = new CnlContractRate();

					// 对所有属性循环
					for (int i = 1; i <= Constants.MAX_ATTR; i++) {
						// 标题字段
						attributeTitle = new PropertyDescriptor(Constants.ATTRIBUTE + (i), importTempTitle.getClass());
						Method methodTitle = attributeTitle.getReadMethod();
						String objTitle = (String) methodTitle.invoke(importTempTitle);
						// 当前数据
						attributeValue = new PropertyDescriptor(Constants.ATTRIBUTE + (i), importTemp.getClass());
						Method methodValue = attributeValue.getReadMethod();
						String objValue = (String) methodValue.invoke(importTemp);

						// 以下为数据校验
						// 以下按标题做判断对应标题做对应的验证,若没有对应到标题则在最后添加错误信息
						if (null == objTitle || "".equals(objTitle.trim())) {// 标题为空时
							if (null == objValue || "".equals(objValue.trim())) {// 行内数据也是空，跳出循环，即不允许出现空的列，否则后面数据当无效处理
								break;
							} else {// 标题为空但行内有值，表示数据多余或标题缺失
								if (!isAppendErrorTitle) {
									isAppendErrorTitle = true;
									errorTitleMessage
											.append(messageSource.getMessage(Constants.ERROR_TITLE, null, locale))
											.append(";");
								}
							}
						}
						// 合约费率
						// 合约编号验证
						else if (objTitle.trim().equals(messageSource.getMessage("合约编号", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								CnlChannelContract dto = new CnlChannelContract();
								dto.setContractCode(objValue.trim());
								List<CnlChannelContract> list = cnlChannelContractService.queryChannelContractByPartCodeAndPartyName(dto);
								if(list != null && list.size() == 1) {
								    if (list.get(0).getDefineRateFlag() != "Y") {
								        errorMessage.append("该合约设置了非自定义费率;");
								    }
								    contractSet.add(list.get(0));
									cnlContractRate.setChannelContractId(list.get(0).getChannelContractId());
								}else {
									cnlContractRate.setChannelContractId(null);
									errorMessage.append(messageSource.getMessage("该合约编号不存在",
											new Object[] { objTitle.trim() }, locale)).append(";");
								}
							} else {
								cnlContractRate.setChannelContractId(null);
								errorMessage.append(messageSource.getMessage("合约编号不能为空",
										new Object[] { objTitle.trim() }, locale)).append(";");
							}
						}
						//产品大类验证
						else if (objTitle.trim().equals(messageSource.getMessage("产品大类", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								// 需要查快码 判断 类型是否在快码列表里面
								List<CodeValue> codeList = codeValueMapper
										.selectCodeValuesByCodeName("PRD.PRODUCT_DIVISION");
								String valueCode = null;
								for (CodeValue code : codeList) {
									if (code.getMeaning().equals(objValue.trim())) {
										valueCode = code.getValue();
										break;
									}
								}
								if (valueCode == null) {
									cnlContractRate.setBigClass(null);
									errorMessage.append(messageSource.getMessage("产品大类在系统值列表中不存在",
											new Object[] { objTitle.trim() }, locale)).append(";");
								} else {
									cnlContractRate.setBigClass(valueCode);
								}
							} else {
								cnlContractRate.setBigClass(null);
							}
						}
						//产品中类验证
						else if (objTitle.trim().equals(messageSource.getMessage("产品中类", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								// 需要查快码 判断 类型是否在快码列表里面
								List<CodeValue> codeList = codeValueMapper
										.selectCodeValuesByCodeName("PRD.PRODUCT_CLASS");
								String valueCode = null;
								for (CodeValue code : codeList) {
									if (code.getMeaning().equals(objValue.trim())) {
										valueCode = code.getValue();
										break;
									}
								}
								if (valueCode == null) {
									cnlContractRate.setMidClass(null);
									errorMessage.append(messageSource.getMessage("产品中类在系统值列表中不存在",
											new Object[] { objTitle.trim() }, locale)).append(";");
								} else {
									cnlContractRate.setMidClass(valueCode);
								}
							} else {
								cnlContractRate.setMidClass(null);
							}
						}
						//产品小类验证
						else if (objTitle.trim().equals(messageSource.getMessage("产品小类", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								// 需要查快码 判断 类型是否在快码列表里面
								List<CodeValue> codeList = codeValueMapper
										.selectCodeValuesByCodeName("PRD.PRODUCT_CATAGORY");
								String valueCode = null;
								for (CodeValue code : codeList) {
									if (code.getMeaning().equals(valueCode)) {
										valueCode = code.getValue();
										break;
									}
								}
								if (valueCode == null) {
									cnlContractRate.setMinClass(null);
									errorMessage.append(messageSource.getMessage("产品小类在系统值列表中不存在",
											new Object[] { objTitle.trim() }, locale)).append(";");
								} else {
									cnlContractRate.setMinClass(valueCode);
								}
							} else {
								cnlContractRate.setMinClass(null);
							}
						}
						//产品名称验证
						else if (objTitle.trim().equals(messageSource.getMessage("产品名称", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								PrdItems prdItems = new PrdItems();
								prdItems.setItemName(objValue.trim());
								Long itemId = prdItemsService.selectItemIdByItemName(objValue.trim());
								
								if (itemId == null) {
									cnlContractRate.setItemId(null);
									errorMessage.append(messageSource.getMessage("该产品不存在",
											new Object[] { objTitle.trim() }, locale)).append(";");
								} else {
									cnlContractRate.setItemId(itemId);
								}
							} else {
								cnlContractRate.setItemId(null);
							}
						}
						//供款期验证
						else if (objTitle.trim().equals(messageSource.getMessage("供款期", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								if(cnlContractRate.getItemId() != null) {
									PrdItemSubline dto = new PrdItemSubline();
									dto.setItemId(cnlContractRate.getItemId());
									dto.setSublineItemName(objValue.trim());
									// 需要查快码 判断 类型是否在快码列表里面
									Long sublineId = prdItemSublineService.selectByCondition(objValue.trim(), cnlContractRate.getItemId());
									if(sublineId != null) {
										cnlContractRate.setSublineId(sublineId);
									}else {
										cnlContractRate.setSublineId(null);
										errorMessage.append(messageSource.getMessage("供款期在子产品表中不存在",
												new Object[] { objTitle.trim() }, locale)).append(";");
									}
								}else {
									cnlContractRate.setSublineId(null);
									errorMessage.append(messageSource.getMessage("产品不存在,所以子产品无法赋值",
											new Object[] { objTitle.trim() }, locale)).append(";");
								}
							} else {
								cnlContractRate.setSublineId(null);
							}
							//校验重复数据
							CnlContractRate _cnlContractRate = new CnlContractRate();
							_cnlContractRate.setChannelContractId(cnlContractRate.getChannelContractId());
							_cnlContractRate.setBigClass(cnlContractRate.getBigClass());
							_cnlContractRate.setMidClass(cnlContractRate.getMidClass());
							_cnlContractRate.setMinClass(cnlContractRate.getMinClass());
							_cnlContractRate.setItemId(cnlContractRate.getItemId());
							_cnlContractRate.setSublineId(cnlContractRate.getSublineId());
                            List<CnlContractRate> _cnlContractRateList = cnlContractRateMapper.selectRateByNull(_cnlContractRate);
                            if (_cnlContractRateList != null && _cnlContractRateList.size() > 0) {
                                errorMessage.append("不能设置相同的自定义级别;");
                            }
						}
						// 第1年验证
						else if (objTitle.trim().equals(messageSource.getMessage("第1年", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								cnlContractRate.setRate1(BigDecimal.valueOf(Double.parseDouble(objValue.trim())));
							} else {
								cnlContractRate.setRate1(null);
							}
						}
						// 第2年验证
						else if (objTitle.trim().equals(messageSource.getMessage("第2年", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								cnlContractRate.setRate2(BigDecimal.valueOf(Double.parseDouble(objValue.trim())));
							} else {
								cnlContractRate.setRate2(null);
							}
						}
						// 第3年验证
						else if (objTitle.trim().equals(messageSource.getMessage("第3年", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								cnlContractRate.setRate3(BigDecimal.valueOf(Double.parseDouble(objValue.trim())));
							} else {
								cnlContractRate.setRate3(null);
							}
						}
						// 第4年
						else if (objTitle.trim().equals(messageSource.getMessage("第4年", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								cnlContractRate.setRate4(BigDecimal.valueOf(Double.parseDouble(objValue.trim())));
							} else {
								cnlContractRate.setRate4(null);
							}
						}
						// 第5年
						else if (objTitle.trim().equals(messageSource.getMessage("第5年", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								cnlContractRate.setRate5(BigDecimal.valueOf(Double.parseDouble(objValue.trim())));
							} else {
								cnlContractRate.setRate5(null);
							}
						}
						// 第6年验证
						else if (objTitle.trim().equals(messageSource.getMessage("第6年", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								cnlContractRate.setRate6(BigDecimal.valueOf(Double.parseDouble(objValue.trim())));
							} else {
								cnlContractRate.setRate6(null);
							}
						}
						// 第7年验证
						else if (objTitle.trim().equals(messageSource.getMessage("第7年", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								cnlContractRate.setRate7(BigDecimal.valueOf(Double.parseDouble(objValue.trim())));
							} else {
								cnlContractRate.setRate7(null);
							}
						}
						// 第8年验证
						else if (objTitle.trim().equals(messageSource.getMessage("第8年", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								cnlContractRate.setRate8(BigDecimal.valueOf(Double.parseDouble(objValue.trim())));
							} else {
								cnlContractRate.setRate8(null);
							}
						}
						// 第9年验证
						else if (objTitle.trim().equals(messageSource.getMessage("第9年", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								cnlContractRate.setRate9(BigDecimal.valueOf(Double.parseDouble(objValue.trim())));
							} else {
								cnlContractRate.setRate9(null);
							}
						}
						// 第10年验证
						else if (objTitle.trim().equals(messageSource.getMessage("第10年", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								cnlContractRate.setRate10(BigDecimal.valueOf(Double.parseDouble(objValue.trim())));
							} else {
								cnlContractRate.setRate10(null);
							}
						}
						// 分成备注验证
						else if (objTitle.trim().equals(messageSource.getMessage("分成备注", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								cnlContractRate.setPerformanceRequire(objValue.trim());
							} else {
								cnlContractRate.setPerformanceRequire(null);
							}
						}
						// 调整记录验证
						else if (objTitle.trim().equals(messageSource.getMessage("调整记录", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								cnlContractRate.setSpecialDesc(objValue.trim());
							} else {
								cnlContractRate.setSpecialDesc(null);
							}
						}
					}
					if ("".equals(errorMessage.toString())) {// 通过第二次验证,进行数据转换
						// 校验‘联系电话’唯一性
						/*
						 * if (uniqueList.contains(cnlChannel.getPhoneCode() +
						 * cnlChannel.getContactPhone())) {
						 * errorMessage.append(messageSource.getMessage( cnlChannel.getPhoneCode() +
						 * cnlChannel.getContactPhone() + "这个联系电话已存在！", null, locale)).append(";"); }
						 * else { uniqueList.add(cnlChannel.getPhoneCode() +
						 * cnlChannel.getContactPhone()); }
						 */

						cnlContractRate.setAttribute1(importBatchId.toString()); // 用Attribute1记录批次号
						// 渠道合约费率
						contractRateList.add(cnlContractRate);
					}
				}

				// 数据转换之后更新数据状态
				importTemp.setImportMessage(errorMessage.toString());// 数据转换之后更新错误状态
				importTempService.updateError(importTemp, request);
			}

			if (!"".equals(errorTitleMessage.toString())) {// 当前页行标题有错误
				importTempTitle.setImportMessage(errorTitleMessage.toString());
				importTempService.updateError(importTempTitle, request);
			}
		} else {
			throw new ValidationTableException(Constants.DATA_EMPTY, null);
		}

		return contractRateList;
	}

}
