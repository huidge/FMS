package clb.core.channel.service.impl;

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
import clb.core.channel.dto.CnlContractRole;
import clb.core.channel.dto.CnlLevel;
import clb.core.channel.service.ICnlChannelContactService;
import clb.core.channel.service.ICnlChannelContractService;
import clb.core.channel.service.ICnlChannelService;
import clb.core.channel.service.ICnlContractRateService;
import clb.core.channel.service.ICnlContractRoleService;
import clb.core.common.service.ISequenceService;
import clb.core.common.utils.DateUtil;
import clb.core.fnd.dto.ImportTemp;
import clb.core.fnd.service.IImportTempService;
import clb.core.fnd.service.impl.AbstractImportExecute;
import clb.core.fnd.utils.Constants;
import clb.core.fnd.utils.ValidationTableException;
import clb.core.supplier.dto.SpeSupplier;
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
public class CnlChannelOtherImport extends AbstractImportExecute {

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
	private IClbEmployeeService  clbEmployeeService;

	@Autowired
	private ICnlChannelContractService cnlChannelContractService;
	
	@Autowired
	private ICnlChannelContactService cnlChannelContactService;
	
	@Autowired
	private ICnlContractRateService cnlContractRateService;
	
	@Autowired
	private ICnlContractRoleService cnlContractRoleService;

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
		List<CnlChannelContract> cnlChannelContracts = validateData(importBatchId, request);

		if (importTempService.selectErrorCount(importBatchId) == 0) {// 验证没有错误
			cnlChannelContracts.forEach(contract -> {
				List<CnlChannelContract> contractlist = cnlChannelContractService.queryContract(request, contract, 1, 10);
				if (null != contractlist && contractlist.size() == 1) {
					//导入渠道合约信息和渠道费率
					contractlist.get(0).setAttribute1(contract.getAttribute1());
					cnlChannelContractService.updateByPrimaryKey(request, contractlist.get(0));
				}else if(null == contractlist || contractlist.size() == 0) {
					//渠道合约信息
					CnlChannelContract cnlContract = cnlChannelContractService.insertSelective(request, contract);
					
					if("SUPPLIER".equals(contract.getPartyType())) {
						//渠道费率
						List<CnlContractRate> cnlContractRateList = contract.getCnlContractRate();
						if(cnlContractRateList != null && cnlContractRateList.size() >0) {
							for (CnlContractRate cnlContractRate : cnlContractRateList) {
								cnlContractRate.setChannelContractId(contract.getChannelContractId());;
								cnlContractRateService.insertSelective(request, cnlContractRate);
							}
						}
					}
					//渠道角色
					List<CnlContractRole> cnlContractRoleList = contract.getCnlContractRoles();
					if(cnlContractRoleList != null && cnlContractRoleList.size() >0) {
						for (CnlContractRole cnlContractRole : cnlContractRoleList) {
							if(cnlContractRole.getRole() != null || cnlContractRole.getRoleUserId() != null || cnlContractRole.getBenefit() != null) {
								cnlContractRole.setChannelContractId(contract.getChannelContractId());
								cnlContractRoleService.insertSelective(request, cnlContractRole);
							}
						}
					}
						
				}
			});
		}

		StringBuffer errorMsg = new StringBuffer("");
	}

	/**
	 * @return
	 * @throws IntrospectionException
	 * @Description: 数据验证
	 * @author
	 */
	public List<CnlChannelContract> validateData(Long importBatchId, IRequest request)
			throws ValidationTableException, Exception {
		String[] locales = request.getLocale().split("_");
		Locale locale = new Locale(locales[0], locales[1]);

		PropertyDescriptor attributeTitle = null;
		PropertyDescriptor attributeValue = null;
		List<ImportTemp> importTemps = new ArrayList<ImportTemp>();// 根据流水号查得的所有临时表数据
		List<CnlChannelContract> cnlChannelContracts = new ArrayList<>();// 将临时表的数据转换成供应商比例维护数据
		// 将没行数据的供应商id、客户id、联系人id存储，方便匹配是否有重复数据
		Map<Map<Long, Long>, List<Long>> custContactSupMap = new HashMap<Map<Long, Long>, List<Long>>();

		List<String> uniqueList = new ArrayList<String>(); // 联系电话做唯一性校验

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
					/*// 渠道表
					CnlChannel cnlChannel = new CnlChannel();
					// 渠道联系方式
					CnlChannelContact cnlChannelContact1 = new CnlChannelContact();
					CnlChannelContact cnlChannelContact2 = new CnlChannelContact();*/
					// 渠道合约
					CnlChannelContract cnlChannelContract = new CnlChannelContract();
					// 合约费率
					CnlContractRate cnlContractRate = new CnlContractRate();
					// 渠道合约角色
					CnlContractRole cnlContractRole1 = new CnlContractRole();
					CnlContractRole cnlContractRole2 = new CnlContractRole();
					CnlContractRole cnlContractRole3 = new CnlContractRole();

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

						//渠道
						else if (objTitle.trim().equals(messageSource.getMessage("渠道", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								CnlChannel cnlChannel = new CnlChannel();
								cnlChannel.setChannelName(objValue.trim());
								List<CnlChannel> summary = cnlChannelService.queryChannelSummary(request, cnlChannel, 1, 1);
								if(summary != null && summary.size() > 0) {
									cnlChannelContract.setChannelId(summary.get(0).getChannelId());
								}else {
									cnlChannelContract.setChannelId(null);
									errorMessage.append(messageSource.getMessage("渠道在系统数据库中不存在",
											new Object[] { objTitle.trim() }, locale)).append(";");
								}
							} else {
								cnlChannelContract.setChannelId(null);
								errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR,
										new Object[] { objTitle.trim() }, locale)).append(";");
							}
						}
						// 渠道合约表
						//合约编号
						else if (objTitle.trim().equals(messageSource.getMessage("合约编号", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								cnlChannelContract.setContractCode(objValue.trim());
							} else {
								cnlChannelContract.setContractCode(null);
							}
						}
						// 合约类别验证
						else if (objTitle.trim().equals(messageSource.getMessage("合约类别", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								List<CodeValue> codeList = codeValueMapper
										.selectCodeValuesByCodeName("CNL.CONTRACT_TYPE");
								String valueCode = null;
								for (CodeValue code : codeList) {
									if (code.getMeaning().equals(objValue)) {
										valueCode = code.getValue();
										break;
									}
								}
								if (valueCode == null) {
									cnlChannelContract.setContractTypeCode(null);
									errorMessage.append(messageSource.getMessage("合约类别在系统值列表中不存在",
											new Object[] { objTitle.trim() }, locale)).append(";");
									continue;
								} else {
									cnlChannelContract.setContractTypeCode(valueCode.trim());
								}
							} else {
								cnlChannelContract.setContractTypeCode(null);
								/*errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR,
										new Object[] { objTitle.trim() }, locale)).append(";");*/
							}
						}
						// 产品分类验证
						else if (objTitle.trim().equals(messageSource.getMessage("产品分类", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								List<CodeValue> codeList = codeValueMapper
										.selectCodeValuesByCodeName("PRD.PRODUCT_DIVISION");
								String valueCode = null;
								for (CodeValue code : codeList) {
									if (code.getMeaning().equals(objValue)) {
										valueCode = code.getValue();
										break;
									}
								}
								if (valueCode == null) {
									cnlChannelContract.setProductDivision(null);
									errorMessage.append(messageSource.getMessage("产品分类在系统值列表中不存在",
											new Object[] { objTitle.trim() }, locale)).append(";");
									continue;
								} else {
									cnlChannelContract.setProductDivision(valueCode.trim());
								}
							} else {
								cnlChannelContract.setProductDivision(null);
								/*errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR,
										new Object[] { objTitle.trim() }, locale)).append(";");*/
							}
						}
						// 合约主体分类验证
						else if (objTitle.trim().equals(messageSource.getMessage("合约主体分类", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								List<CodeValue> codeList = codeValueMapper.selectCodeValuesByCodeName("CNL.PARTY_TYPE");
								String valueCode = null;
								for (CodeValue code : codeList) {
									if (code.getMeaning().equals(objValue)) {
										valueCode = code.getValue();
										break;
									}
								}
								if (valueCode == null) {
									cnlChannelContract.setPartyType(null);
									errorMessage.append(messageSource.getMessage("合约主体分类在系统值列表中不存在",
											new Object[] { objTitle.trim() }, locale)).append(";");
									continue;
								} else {
									cnlChannelContract.setPartyType(valueCode.trim());
								}
							} else {
								cnlChannelContract.setPartyType(null);
								/*errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR,
										new Object[] { objTitle.trim() }, locale)).append(";");*/
							}
						}
						// 合约主体名称验证-----------查表
						else if (objTitle.trim().equals(messageSource.getMessage("合约主体名称", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								Long partyId = null;
								if("SUPPLIER".equals(cnlChannelContract.getPartyType())) {
									SpeSupplier speSupplier = new SpeSupplier();
									speSupplier.setName(objValue.trim());
									List<SpeSupplier> list = speSupplierService.selectByName(speSupplier);
									
									if (list != null && list.size() == 1) {
										for (SpeSupplier supplier : list) {
											if (objValue.trim().equals(supplier.getName())) {
												partyId = supplier.getSupplierId();
												break;
											}
										}
									}
									
								}else if("CHANNEL".equals(cnlChannelContract.getPartyType())){
									CnlChannel channel = new CnlChannel();
									channel.setChannelName(objValue.trim());
									List<CnlChannel> summary = cnlChannelService.queryChannelSummary(request, channel, 1, 10);
									
									if (summary != null && summary.size() == 1) {
										for (CnlChannel cnlChannel2 : summary) {
											if (objValue.trim().equals(cnlChannel2.getChannelName())) {
												partyId = cnlChannel2.getChannelId();
												break;
											}
										}
									}
								}
								
								if (partyId == null) {
									cnlChannelContract.setPartyId(null);
									errorMessage.append(messageSource.getMessage("合约主体名称在系统表中不存在或存在多个",
											new Object[] { objTitle.trim() }, locale)).append(";");
									continue;
								} else {
									cnlChannelContract.setPartyId(partyId);
								}
							} else {
								cnlChannelContract.setPartyId(null);
								errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR,
										new Object[] { objTitle.trim() }, locale)).append(";");
							}
						}
						// 渠道分类验证
						else if (objTitle.trim().equals(messageSource.getMessage("渠道分类", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								if(!"CHANNEL".equals(cnlChannelContract.getPartyType())) {
									List<CodeValue> codeList = codeValueMapper
											.selectCodeValuesByCodeName("CNL.CHANNEL_CLASS");
									String valueCode = null;
									for (CodeValue code : codeList) {
										if (code.getMeaning().equals(objValue)) {
											valueCode = code.getValue();
											break;
										}
									}
									if (valueCode == null) {
										cnlChannelContract.setChannelTypeCode(null);
										errorMessage.append(messageSource.getMessage("渠道分类在系统值列表中不存在",
												new Object[] { objTitle.trim() }, locale)).append(";");
										continue;
									} else {
										cnlChannelContract.setChannelTypeCode(valueCode.trim());
									}
								}
							} else {
								cnlChannelContract.setChannelTypeCode(null);
							}
						}
						// 合约开始时间验证
						else if (objTitle.trim().equals(messageSource.getMessage("合约开始时间", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								try {
									cnlChannelContract.setStartDate(DateUtil.getDate(objValue, "yyyy-MM-dd"));
								} catch (Exception e) {
									cnlChannelContract.setStartDate(null);
									errorMessage.append(messageSource.getMessage("合约开始时间格式错误",
											new Object[] { objTitle.trim() }, locale)).append(";");
								}
							} else {
								cnlChannelContract.setStartDate(null);
								/*errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR,
										new Object[] { objTitle.trim() }, locale)).append(";");*/
							}
						}
						// 合约结束时间验证
						else if (objTitle.trim().equals(messageSource.getMessage("合约结束时间", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								try {
									cnlChannelContract.setEndDate(DateUtil.getDate(objValue, "yyyy-MM-dd"));
								} catch (Exception e) {
									cnlChannelContract.setEndDate(null);
									errorMessage.append(messageSource.getMessage("合约结束时间格式错误",
											new Object[] { objTitle.trim() }, locale)).append(";");
								}
							} else {
								cnlChannelContract.setEndDate(null);
								/*errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR,
										new Object[] { objTitle.trim() }, locale)).append(";");*/
							}
						}
						// 合约费率
						// 费率级别验证 -----------查表
						else if (objTitle.trim().equals(messageSource.getMessage("费率级别", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								if (("SUPPLIER").equals(cnlChannelContract.getPartyType())) {
									CnlLevel cnlLevel1 = new CnlLevel();
									cnlLevel1.setSupplierId(cnlChannelContract.getPartyId());
									cnlLevel1.setChannelClassCode(cnlChannelContract.getChannelTypeCode());
									List<CnlLevel> levelList = cnlChannelContractService.queryLevel(request,cnlLevel1);
									//String cnlLevelName = null;
									Long cnlLevelId = null;
									if (levelList != null && levelList.size() > 0) {
										for (CnlLevel cnlLevel : levelList) {
											if (cnlLevel.getLevelName().equals(objValue.trim())) {
												//cnlLevelName = cnlLevel.getLevelName();
											    cnlLevelId = cnlLevel.getId();
												break;
											}
										}
									}
									/*cnlLevelName == null*/
									if (cnlLevelId == null) {
										//cnlContractRate.setLevelName(null);
										cnlChannelContract.setRateLevelId(null);
										errorMessage.append(messageSource.getMessage("费率级别在渠道等级表中不存在",
												new Object[] { objTitle.trim() }, locale)).append(";");
										continue;
									} else {
										//cnlContractRate.setLevelName(cnlLevelName);
									    cnlChannelContract.setRateLevelId(cnlLevelId);
									}
								} else {
									//cnlContractRate.setLevelId(null);
									//cnlContractRate.setLevelName(null);
									cnlChannelContract.setRateLevelId(null);
								}
							} else {
								//cnlContractRate.setLevelName(null);
								//cnlContractRate.setLevelId(null);
								cnlChannelContract.setRateLevelId(null);
							}
						}
						/*// 分成比例1验证
						else if (objTitle.trim().equals(messageSource.getMessage("分成比例1", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								cnlContractRate.setRate1(BigDecimal.valueOf(Double.parseDouble(objValue.trim())));
							} else {
								cnlContractRate.setRate1(null);
							}
						}
						// 分成比例2验证
						else if (objTitle.trim().equals(messageSource.getMessage("分成比例2", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								cnlContractRate.setRate2(BigDecimal.valueOf(Double.parseDouble(objValue.trim())));
							} else {
								cnlContractRate.setRate2(null);
							}
						}
						// 分成比例3验证
						else if (objTitle.trim().equals(messageSource.getMessage("分成比例3", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								cnlContractRate.setRate3(BigDecimal.valueOf(Double.parseDouble(objValue.trim())));
							} else {
								cnlContractRate.setRate3(null);
							}
						}
						// 分成比例4验证
						else if (objTitle.trim().equals(messageSource.getMessage("分成比例4", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								cnlContractRate.setRate4(BigDecimal.valueOf(Double.parseDouble(objValue.trim())));
							} else {
								cnlContractRate.setRate4(null);
							}
						}
						// 分成比例5验证
						else if (objTitle.trim().equals(messageSource.getMessage("分成比例5", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								cnlContractRate.setRate5(BigDecimal.valueOf(Double.parseDouble(objValue.trim())));
							} else {
								cnlContractRate.setRate5(null);
							}
						}
						// 分成比例6验证
						else if (objTitle.trim().equals(messageSource.getMessage("分成比例6", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								cnlContractRate.setRate6(BigDecimal.valueOf(Double.parseDouble(objValue.trim())));
							} else {
								cnlContractRate.setRate6(null);
							}
						}
						// 分成比例7验证
						else if (objTitle.trim().equals(messageSource.getMessage("分成比例7", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								cnlContractRate.setRate7(BigDecimal.valueOf(Double.parseDouble(objValue.trim())));
							} else {
								cnlContractRate.setRate7(null);
							}
						}
						// 分成比例8验证
						else if (objTitle.trim().equals(messageSource.getMessage("分成比例8", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								cnlContractRate.setRate8(BigDecimal.valueOf(Double.parseDouble(objValue.trim())));
							} else {
								cnlContractRate.setRate8(null);
							}
						}
						// 分成比例9验证
						else if (objTitle.trim().equals(messageSource.getMessage("分成比例9", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								cnlContractRate.setRate9(BigDecimal.valueOf(Double.parseDouble(objValue.trim())));
							} else {
								cnlContractRate.setRate9(null);
							}
						}
						// 分成比例10验证
						else if (objTitle.trim().equals(messageSource.getMessage("分成比例10", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								cnlContractRate.setRate10(BigDecimal.valueOf(Double.parseDouble(objValue.trim())));
							} else {
								cnlContractRate.setRate10(null);
							}
						}*/
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
						// 是否结算验证
						else if (objTitle.trim().equals(messageSource.getMessage("是否结算", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								List<CodeValue> codeList = codeValueMapper.selectCodeValuesByCodeName("SYS.YES_NO");
								String valueCode = null;
								for (CodeValue code : codeList) {
									if (code.getMeaning().equals(objValue)) {
										valueCode = code.getValue();
										break;
									}
								}
								if (valueCode == null) {
									cnlChannelContract.setSettleFlag(null);
									errorMessage.append(messageSource.getMessage("是否结算在系统值列表中不存在",
											new Object[] { objTitle.trim() }, locale)).append(";");
									continue;
								} else {
									cnlChannelContract.setSettleFlag(valueCode.trim());
								}
							} else {
								cnlChannelContract.setSettleFlag(null);
							}
						}
						// 结算方式验证
						else if (objTitle.trim().equals(messageSource.getMessage("结算方式", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								List<CodeValue> codeList = codeValueMapper
										.selectCodeValuesByCodeName("SPE.SETTLEMENT_METHOD");
								String valueCode = null;
								for (CodeValue code : codeList) {
									if (code.getMeaning().equals(objValue)) {
										valueCode = code.getValue();
										break;
									}
								}
								if (valueCode == null) {
									cnlChannelContract.setSettleTypeCode(null);
									errorMessage.append(messageSource.getMessage("结算方式在系统值列表中不存在",
											new Object[] { objTitle.trim() }, locale)).append(";");
									continue;
								} else {
									cnlChannelContract.setSettleTypeCode(valueCode.trim());
								}
							} else {
								cnlChannelContract.setSettleTypeCode(null);
							}
						}
						// 结算账户验证
						else if (objTitle.trim().equals(messageSource.getMessage("结算账户", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								List<CodeValue> codeList = codeValueMapper
										.selectCodeValuesByCodeName("CNL.SETTLE_ACCOUNT");
								String valueCode = null;
								for (CodeValue code : codeList) {
									if (code.getMeaning().equals(objValue)) {
										valueCode = code.getValue();
										break;
									}
								}
								if (valueCode == null) {
									cnlChannelContract.setSettleAccount(null);
									errorMessage.append(messageSource.getMessage("结算账户在系统值列表中不存在",
											new Object[] { objTitle.trim() }, locale)).append(";");
									continue;
								} else {
									cnlChannelContract.setSettleAccount(valueCode.trim());
								}
							} else {
								cnlChannelContract.setSettleAccount(null);
							}
						}
						// 合同方式验证
						else if (objTitle.trim().equals(messageSource.getMessage("合同方式", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								List<CodeValue> codeList = codeValueMapper
										.selectCodeValuesByCodeName("CNL.CONTRACT_APPROACH");
								String valueCode = null;
								for (CodeValue code : codeList) {
									if (code.getMeaning().equals(objValue)) {
										valueCode = code.getValue();
										break;
									}
								}
								if (valueCode == null) {
									cnlChannelContract.setContractApproach(null);
									errorMessage.append(messageSource.getMessage("合同方式在系统值列表中不存在",
											new Object[] { objTitle.trim() }, locale)).append(";");
									continue;
								} else {
									cnlChannelContract.setContractApproach(valueCode.trim());
								}
							} else {
								cnlChannelContract.setContractApproach(null);
							}
						}
						// 特殊处理验证
						else if (objTitle.trim().equals(messageSource.getMessage("特殊处理", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								cnlChannelContract.setSpecialTreatment(objValue.trim());
							} else {
								cnlChannelContract.setSpecialTreatment(null);
							}
						}
						// 渠道合约角色
						// 利益分配角色1验证
						else if (objTitle.trim().equals(messageSource.getMessage("利益分配角色1", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								List<CodeValue> codeList = codeValueMapper
										.selectCodeValuesByCodeName("CNL.CONTRACT_ROLE");
								String valueCode = null;
								for (CodeValue code : codeList) {
									if (code.getMeaning().equals(objValue)) {
										valueCode = code.getValue();
										break;
									}
								}
								if (valueCode == null) {
									cnlContractRole1.setRole(null);
									errorMessage.append(messageSource.getMessage("利益分配角色1在系统值列表中不存在",
											new Object[] { objTitle.trim() }, locale)).append(";");
									continue;
								} else {
									cnlContractRole1.setRole(valueCode.trim());
								}
							} else {
								cnlContractRole1.setRole(null);
							}
						}
						// 利益分配姓名1验证
						else if (objTitle.trim().equals(messageSource.getMessage("利益分配姓名1", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								Long userId = null;
								ClbUser clbUser = new ClbUser();
								if("ADMINISTRATION".equals(cnlContractRole1.getRole())) {
									clbUser.setUserType("OPERATOR");
									clbUser.setRelatedPartyName(objValue.trim());
									List<ClbUser> list = clbUserService.queryUserIdByUserTypeAndRelatedPartyName(request, clbUser);
									
									if(list != null && list.size() == 1) {
										userId = list.get(0).getUserId();
									}
									
									if (userId == null) {
										cnlContractRole1.setRoleUserId(null);
										errorMessage.append(messageSource.getMessage("利益分配姓名1在用户列表中不存在或存个多个此关联方名称(员工)",
												new Object[] { objTitle.trim() }, locale)).append(";");
										continue;
									} else {
										cnlContractRole1.setRoleUserId(userId);
									}
									
								}else {
									
									clbUser.setUserType("CHANNEL");
									clbUser.setRelatedPartyName(objValue.trim());
									List<ClbUser> list1 = clbUserService.queryUserIdByUserTypeAndRelatedPartyName(request, clbUser);
									if(list1 != null && list1.size() == 1) {
										userId = list1.get(0).getUserId();
									}
									if (userId == null) {
										cnlContractRole1.setRoleUserId(null);
										errorMessage.append(messageSource.getMessage("利益分配姓名1在用户列表中不存在或存个多个此关联方名称(渠道)",
												new Object[] { objTitle.trim() }, locale)).append(";");
										continue;
									} else {
										cnlContractRole1.setRoleUserId(userId);
									}
								}
							} else {
								cnlContractRole1.setRoleUserId(null);
							}
						}
						// 利益1验证
						else if (objTitle.trim().equals(messageSource.getMessage("利益1", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								cnlContractRole1.setBenefit(objValue.trim());
							} else {
								cnlContractRole1.setBenefit(null);
							}
						}
						// 利益分配角色2验证
						else if (objTitle.trim().equals(messageSource.getMessage("利益分配角色2", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								List<CodeValue> codeList = codeValueMapper
										.selectCodeValuesByCodeName("CNL.CONTRACT_ROLE");
								String valueCode = null;
								for (CodeValue code : codeList) {
									if (code.getMeaning().equals(objValue)) {
										valueCode = code.getValue();
										break;
									}
								}
								if (valueCode == null) {
									cnlContractRole2.setRole(null);
									errorMessage.append(messageSource.getMessage("利益分配角色2在系统值列表中不存在",
											new Object[] { objTitle.trim() }, locale)).append(";");
									continue;
								} else {
									cnlContractRole2.setRole(valueCode.trim());
								}
							} else {
								cnlContractRole2.setRole(null);
							}
						}
						// 利益分配姓名2验证
						else if (objTitle.trim().equals(messageSource.getMessage("利益分配姓名2", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								Long userId = null;
								ClbUser clbUser = new ClbUser();
								if("ADMINISTRATION".equals(cnlContractRole2.getRole())) {
									clbUser.setUserType("OPERATOR");
									clbUser.setRelatedPartyName(objValue.trim());
									List<ClbUser> list = clbUserService.queryUserIdByUserTypeAndRelatedPartyName(request, clbUser);
									
									if(list != null && list.size() == 1) {
										userId = list.get(0).getUserId();
									}
									
									if (userId == null) {
										cnlContractRole2.setRoleUserId(null);
										errorMessage.append(messageSource.getMessage("利益分配姓名2在用户列表中不存在或存个多个此关联方名称(员工)",
												new Object[] { objTitle.trim() }, locale)).append(";");
										continue;
									} else {
										cnlContractRole2.setRoleUserId(userId);
									}
									
								}else {
									clbUser.setUserType("CHANNEL");
									clbUser.setRelatedPartyName(objValue.trim());
									List<ClbUser> list1 = clbUserService.queryUserIdByUserTypeAndRelatedPartyName(request, clbUser);
									if(list1 != null && list1.size() == 1) {
										userId = list1.get(0).getUserId();
									}
									if (userId == null) {
										cnlContractRole2.setRoleUserId(null);
										errorMessage.append(messageSource.getMessage("利益分配姓名2在用户列表中不存在或存个多个此关联方名称(渠道)",
												new Object[] { objTitle.trim() }, locale)).append(";");
										continue;
									} else {
										cnlContractRole2.setRoleUserId(userId);
									}
								}
							} else {
								cnlContractRole2.setRoleUserId(null);
							}
						}
						// 利益2验证
						else if (objTitle.trim().equals(messageSource.getMessage("利益2", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								cnlContractRole2.setBenefit(objValue.trim());
							} else {
								cnlContractRole2.setBenefit(null);
							}
						}
						// 利益分配角色3验证
						else if (objTitle.trim().equals(messageSource.getMessage("利益分配角色3", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								List<CodeValue> codeList = codeValueMapper
										.selectCodeValuesByCodeName("CNL.CONTRACT_ROLE");
								String valueCode = null;
								for (CodeValue code : codeList) {
									if (code.getMeaning().equals(objValue)) {
										valueCode = code.getValue();
										break;
									}
								}
								if (valueCode == null) {
									cnlContractRole3.setRole(null);
									errorMessage.append(messageSource.getMessage("利益分配角色3在系统值列表中不存在",
											new Object[] { objTitle.trim() }, locale)).append(";");
									continue;
								} else {
									cnlContractRole3.setRole(valueCode.trim());
								}
							} else {
								cnlContractRole3.setRole(null);
							}
						}
						// 利益分配姓名3验证
						else if (objTitle.trim().equals(messageSource.getMessage("利益分配姓名3", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								Long userId = null;
								ClbUser clbUser = new ClbUser();
								if("ADMINISTRATION".equals(cnlContractRole3.getRole())) {
									clbUser.setUserType("OPERATOR");
									clbUser.setRelatedPartyName(objValue.trim());
									List<ClbUser> list = clbUserService.queryUserIdByUserTypeAndRelatedPartyName(request, clbUser);
									
									if(list != null && list.size() == 1) {
										userId = list.get(0).getUserId();
									}
									
									if (userId == null) {
										cnlContractRole3.setRoleUserId(null);
										errorMessage.append(messageSource.getMessage("利益分配姓名3在用户列表中不存在或存个多个此关联方名称(员工)",
												new Object[] { objTitle.trim() }, locale)).append(";");
										continue;
									} else {
										cnlContractRole3.setRoleUserId(userId);
									}
									
								}else {
									
									clbUser.setUserType("CHANNEL");
									clbUser.setRelatedPartyName(objValue.trim());
									List<ClbUser> list1 = clbUserService.queryUserIdByUserTypeAndRelatedPartyName(request, clbUser);
									if(list1 != null && list1.size() == 1) {
										userId = list1.get(0).getUserId();
									}
									if (userId == null) {
										cnlContractRole3.setRoleUserId(null);
										errorMessage.append(messageSource.getMessage("利益分配姓名3在用户列表中不存在或存个多个此关联方名称(渠道)",
												new Object[] { objTitle.trim() }, locale)).append(";");
										continue;
									} else {
										cnlContractRole3.setRoleUserId(userId);
									}
								}
							} else {
								cnlContractRole3.setRoleUserId(null);
							}
						}
						// 利益3验证
						else if (objTitle.trim().equals(messageSource.getMessage("利益3", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								cnlContractRole3.setBenefit(objValue.trim());
							} else {
								cnlContractRole3.setBenefit(null);
							}
						}
						// 渠道合约信息
						// 保单资料审核验证
						else if (objTitle.trim().equals(messageSource.getMessage("保单资料审核", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								List<CodeValue> codeList = codeValueMapper
										.selectCodeValuesByCodeName("CNL.CONTRACT_SERVICE_PARTY");
								String valueCode = null;
								for (CodeValue code : codeList) {
									if (code.getMeaning().equals(objValue)) {
										valueCode = code.getValue();
										break;
									}
								}
								if (valueCode == null) {
									cnlChannelContract.setBusinessStaff(null);
									errorMessage.append(messageSource.getMessage("保单资料审核在系统值列表中不存在",
											new Object[] { objTitle.trim() }, locale)).append(";");
									continue;
								} else {
									cnlChannelContract.setBusinessStaff(valueCode.trim());
								}
							} else {
								cnlChannelContract.setBusinessStaff(null);
							}
						}
						// 保单资料审核人验证
						else if (objTitle.trim().equals(messageSource.getMessage("保单资料审核人", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								ClbUser clbUser1 = new ClbUser();
								clbUser1.setUserType("OPERATOR");// 平台用户
								List<ClbUser> clbList = clbUserService.selectAllFields(request, clbUser1, 1, 1000);
								Long userId = null;
								if (clbList != null && clbList.size() > 0) {
									for (ClbUser clbUser : clbList) {
										if (clbUser.getUserName().equals(objValue.trim())) {
											userId = clbUser.getUserId();
											break;
										}
									}
								}
								if (userId == null) {
									cnlChannelContract.setBusinessStaffUserId(null);
									errorMessage.append(messageSource.getMessage("保单资料审核人在用户列表中不存在或者不是平台用户",
											new Object[] { objTitle.trim() }, locale)).append(";");
									continue;
								} else {
									cnlChannelContract.setBusinessStaffUserId(userId);
								}
							} else {
								cnlChannelContract.setBusinessStaffUserId(null);
							}
						}
						// 预约供应商验证
						else if (objTitle.trim().equals(messageSource.getMessage("预约供应商", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								List<CodeValue> codeList = codeValueMapper
										.selectCodeValuesByCodeName("CNL.CONTRACT_SERVICE_PARTY");
								String valueCode = null;
								for (CodeValue code : codeList) {
									if (code.getMeaning().equals(objValue)) {
										valueCode = code.getValue();
										break;
									}
								}
								if (valueCode == null) {
									cnlChannelContract.setClbClub(null);
									errorMessage.append(messageSource.getMessage("预约供应商在系统值列表中不存在",
											new Object[] { objTitle.trim() }, locale)).append(";");
									continue;
								} else {
									cnlChannelContract.setClbClub(valueCode.trim());
								}
							} else {
								cnlChannelContract.setClbClub(null);
							}
						}
						// 预约供应商人验证
						else if (objTitle.trim().equals(messageSource.getMessage("预约供应商人", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								ClbUser clbUser1 = new ClbUser();
								clbUser1.setUserType("OPERATOR");// 平台用户
								List<ClbUser> clbList = clbUserService.selectAllFields(request, clbUser1, 1, 1000);
								Long userId = null;
								for (ClbUser clbUser : clbList) {
									if (clbUser.getUserName().equals(objValue.trim())) {
										userId = clbUser.getUserId();
										break;
									}
								}
								if (userId == null) {
									cnlChannelContract.setClbClubUserId(null);
									errorMessage.append(messageSource.getMessage("预约供应商人在用户列表中不存在或者不是平台用户",
											new Object[] { objTitle.trim() }, locale)).append(";");
									continue;
								} else {
									cnlChannelContract.setClbClubUserId(userId);
								}
							} else {
								cnlChannelContract.setClbClubUserId(null);
							}
						}
						// 签单提醒验证
						else if (objTitle.trim().equals(messageSource.getMessage("签单提醒", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								List<CodeValue> codeList = codeValueMapper
										.selectCodeValuesByCodeName("CNL.CONTRACT_SERVICE_PARTY");
								String valueCode = null;
								for (CodeValue code : codeList) {
									if (code.getMeaning().equals(objValue)) {
										valueCode = code.getValue();
										break;
									}
								}
								if (valueCode == null) {
									cnlChannelContract.setReserveSupplier(null);
									errorMessage.append(messageSource.getMessage("签单提醒在系统值列表中不存在",
											new Object[] { objTitle.trim() }, locale)).append(";");
									continue;
								} else {
									cnlChannelContract.setReserveSupplier(valueCode.trim());
								}
							} else {
								cnlChannelContract.setReserveSupplier(null);
							}
						}
						// 签单提醒人验证
						else if (objTitle.trim().equals(messageSource.getMessage("签单提醒人", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								ClbUser clbUser1 = new ClbUser();
								clbUser1.setUserType("OPERATOR");// 平台用户
								List<ClbUser> clbList = clbUserService.selectAllFields(request, clbUser1, 1, 1000);
								Long userId = null;
								for (ClbUser clbUser : clbList) {
									if (clbUser.getUserName().equals(objValue.trim())) {
										userId = clbUser.getUserId();
										break;
									}
								}
								if (userId == null) {
									cnlChannelContract.setReserveSupplierUserId(null);
									errorMessage.append(messageSource.getMessage("签单提醒人在用户列表中不存在或者不是平台用户",
											new Object[] { objTitle.trim() }, locale)).append(";");
									continue;
								} else {
									cnlChannelContract.setReserveSupplierUserId(userId);
								}
							} else {
								cnlChannelContract.setReserveSupplierUserId(null);
							}
						}
						// 誊抄申请书验证
						else if (objTitle.trim().equals(messageSource.getMessage("誊抄申请书", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								List<CodeValue> codeList = codeValueMapper
										.selectCodeValuesByCodeName("CNL.CONTRACT_SERVICE_PARTY");
								String valueCode = null;
								for (CodeValue code : codeList) {
									if (code.getMeaning().equals(objValue)) {
										valueCode = code.getValue();
										break;
									}
								}
								if (valueCode == null) {
									cnlChannelContract.setSignNotice(null);
									errorMessage.append(messageSource.getMessage("誊抄申请书在系统值列表中不存在",
											new Object[] { objTitle.trim() }, locale)).append(";");
									continue;
								} else {
									cnlChannelContract.setSignNotice(valueCode.trim());
								}
							} else {
								cnlChannelContract.setSignNotice(null);
							}
						}
						// 誊抄申请书人验证
						else if (objTitle.trim().equals(messageSource.getMessage("誊抄申请书人", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								ClbUser clbUser1 = new ClbUser();
								clbUser1.setUserType("OPERATOR");// 平台用户
								List<ClbUser> clbList = clbUserService.selectAllFields(request, clbUser1, 1, 1000);
								Long userId = null;
								for (ClbUser clbUser : clbList) {
									if (clbUser.getUserName().equals(objValue.trim())) {
										userId = clbUser.getUserId();
										break;
									}
								}
								if (userId == null) {
									cnlChannelContract.setSignNoticeUserId(null);
									errorMessage.append(messageSource.getMessage("誊抄申请书人在用户列表中不存在或者不是平台用户",
											new Object[] { objTitle.trim() }, locale)).append(";");
									continue;
								} else {
									cnlChannelContract.setSignNoticeUserId(userId);
								}
							} else {
								cnlChannelContract.setSignNoticeUserId(null);
							}
						}
						// 会所接待验证
						else if (objTitle.trim().equals(messageSource.getMessage("会所接待", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								List<CodeValue> codeList = codeValueMapper
										.selectCodeValuesByCodeName("CNL.CONTRACT_SERVICE_PARTY");
								String valueCode = null;
								for (CodeValue code : codeList) {
									if (code.getMeaning().equals(objValue)) {
										valueCode = code.getValue();
										break;
									}
								}
								if (valueCode == null) {
									cnlChannelContract.setFill(null);
									errorMessage.append(messageSource.getMessage("会所接待在系统值列表中不存在",
											new Object[] { objTitle.trim() }, locale)).append(";");
									continue;
								} else {
									cnlChannelContract.setFill(valueCode.trim());
								}
							} else {
								cnlChannelContract.setFill(null);
							}
						}
						// 会所接待人验证
						else if (objTitle.trim().equals(messageSource.getMessage("会所接待人", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								ClbUser clbUser1 = new ClbUser();
								clbUser1.setUserType("OPERATOR");// 平台用户
								List<ClbUser> clbList = clbUserService.selectAllFields(request, clbUser1, 1, 1000);
								Long userId = null;
								for (ClbUser clbUser : clbList) {
									if (clbUser.getUserName().equals(objValue.trim())) {
										userId = clbUser.getUserId();
										break;
									}
								}
								if (userId == null) {
									cnlChannelContract.setFillUserId(null);
									errorMessage.append(messageSource.getMessage("会所接待人在用户列表中不存在或者不是平台用户",
											new Object[] { objTitle.trim() }, locale)).append(";");
									continue;
								} else {
									cnlChannelContract.setFillUserId(userId);
								}
							} else {
								cnlChannelContract.setFillUserId(null);
							}
						}
						// 协助签单验证
						else if (objTitle.trim().equals(messageSource.getMessage("协助签单", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								List<CodeValue> codeList = codeValueMapper
										.selectCodeValuesByCodeName("CNL.CONTRACT_SERVICE_PARTY");
								String valueCode = null;
								for (CodeValue code : codeList) {
									if (code.getMeaning().equals(objValue)) {
										valueCode = code.getValue();
										break;
									}
								}
								if (valueCode == null) {
									cnlChannelContract.setContractSign(null);
									errorMessage.append(messageSource.getMessage("协助签单在系统值列表中不存在",
											new Object[] { objTitle.trim() }, locale)).append(";");
									continue;
								} else {
									cnlChannelContract.setContractSign(valueCode.trim());
								}
							} else {
								cnlChannelContract.setContractSign(null);
							}
						}
						// 协助签单人验证
						else if (objTitle.trim().equals(messageSource.getMessage("协助签单人", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								ClbUser clbUser1 = new ClbUser();
								clbUser1.setUserType("OPERATOR");// 平台用户
								List<ClbUser> clbList = clbUserService.selectAllFields(request, clbUser1, 1, 1000);
								Long userId = null;
								for (ClbUser clbUser : clbList) {
									if (clbUser.getUserName().equals(objValue.trim())) {
										userId = clbUser.getUserId();
										break;
									}
								}
								if (userId == null) {
									cnlChannelContract.setContractSignUserId(null);
									errorMessage.append(messageSource.getMessage("协助签单人在用户列表中不存在或者不是平台用户",
											new Object[] { objTitle.trim() }, locale)).append(";");
									continue;
								} else {
									cnlChannelContract.setContractSignUserId(userId);
								}
							} else {
								cnlChannelContract.setContractSignUserId(null);
							}
						}
						// 保单核对验证
						else if (objTitle.trim().equals(messageSource.getMessage("保单核对", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								List<CodeValue> codeList = codeValueMapper
										.selectCodeValuesByCodeName("CNL.CONTRACT_SERVICE_PARTY");
								String valueCode = null;
								for (CodeValue code : codeList) {
									if (code.getMeaning().equals(objValue)) {
										valueCode = code.getValue();
										break;
									}
								}
								if (valueCode == null) {
									cnlChannelContract.setSiteFollow(null);
									errorMessage.append(messageSource.getMessage("保单核对在系统值列表中不存在",
											new Object[] { objTitle.trim() }, locale)).append(";");
									continue;
								} else {
									cnlChannelContract.setSiteFollow(valueCode.trim());
								}
							} else {
								cnlChannelContract.setSiteFollow(null);
							}
						}
						// 保单核对人验证
						else if (objTitle.trim().equals(messageSource.getMessage("保单核对人", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								ClbUser clbUser1 = new ClbUser();
								clbUser1.setUserType("OPERATOR");// 平台用户
								List<ClbUser> clbList = clbUserService.selectAllFields(request, clbUser1, 1, 1000);
								Long userId = null;
								for (ClbUser clbUser : clbList) {
									if (clbUser.getUserName().equals(objValue.trim())) {
										userId = clbUser.getUserId();
										break;
									}
								}
								if (userId == null) {
									cnlChannelContract.setSiteFollowUserId(null);
									errorMessage.append(messageSource.getMessage("保单核对人在用户列表中不存在或者不是平台用户",
											new Object[] { objTitle.trim() }, locale)).append(";");
									continue;
								} else {
									cnlChannelContract.setSiteFollowUserId(userId);
								}
							} else {
								cnlChannelContract.setSiteFollowUserId(null);
							}
						}
						// 跟进Pending验证
						else if (objTitle.trim().equals(messageSource.getMessage("跟进Pending", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								List<CodeValue> codeList = codeValueMapper
										.selectCodeValuesByCodeName("CNL.CONTRACT_SERVICE_PARTY");
								String valueCode = null;
								for (CodeValue code : codeList) {
									if (code.getMeaning().equals(objValue)) {
										valueCode = code.getValue();
										break;
									}
								}
								if (valueCode == null) {
									cnlChannelContract.setPolicyReview(null);
									errorMessage.append(messageSource.getMessage("跟进Pending在系统值列表中不存在",
											new Object[] { objTitle.trim() }, locale)).append(";");
									continue;
								} else {
									cnlChannelContract.setPolicyReview(valueCode.trim());
								}
							} else {
								cnlChannelContract.setPolicyReview(null);
							}
						}
						// 跟进Pending人验证
						else if (objTitle.trim().equals(messageSource.getMessage("跟进Pending人", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								ClbUser clbUser1 = new ClbUser();
								clbUser1.setUserType("OPERATOR");// 平台用户
								List<ClbUser> clbList = clbUserService.selectAllFields(request, clbUser1, 1, 1000);
								Long userId = null;
								for (ClbUser clbUser : clbList) {
									if (clbUser.getUserName().equals(objValue.trim())) {
										userId = clbUser.getUserId();
										break;
									}
								}
								if (userId == null) {
									cnlChannelContract.setPolicyReviewUserId(null);
									errorMessage.append(messageSource.getMessage("跟进Pending人在用户列表中不存在或者不是平台用户",
											new Object[] { objTitle.trim() }, locale)).append(";");
									continue;
								} else {
									cnlChannelContract.setPolicyReviewUserId(userId);
								}
							} else {
								cnlChannelContract.setPolicyReviewUserId(null);
							}
						}
						// 记录批核信息验证
						else if (objTitle.trim().equals(messageSource.getMessage("记录批核信息", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								List<CodeValue> codeList = codeValueMapper
										.selectCodeValuesByCodeName("CNL.CONTRACT_SERVICE_PARTY");
								String valueCode = null;
								for (CodeValue code : codeList) {
									if (code.getMeaning().equals(objValue)) {
										valueCode = code.getValue();
										break;
									}
								}
								if (valueCode == null) {
									cnlChannelContract.setPolicySend(null);
									errorMessage.append(messageSource.getMessage("记录批核信息在系统值列表中不存在",
											new Object[] { objTitle.trim() }, locale)).append(";");
									continue;
								} else {
									cnlChannelContract.setPolicySend(valueCode.trim());
								}
							} else {
								cnlChannelContract.setPolicySend(null);
							}
						}
						// 记录批核信息人验证
						else if (objTitle.trim().equals(messageSource.getMessage("记录批核信息人", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								ClbUser clbUser1 = new ClbUser();
								clbUser1.setUserType("OPERATOR");// 平台用户
								List<ClbUser> clbList = clbUserService.selectAllFields(request, clbUser1, 1, 1000);
								Long userId = null;
								for (ClbUser clbUser : clbList) {
									if (clbUser.getUserName().equals(objValue.trim())) {
										userId = clbUser.getUserId();
										break;
									}
								}
								if (userId == null) {
									cnlChannelContract.setPolicySendUserId(null);
									errorMessage.append(messageSource.getMessage("记录批核信息人在用户列表中不存在或者不是平台用户",
											new Object[] { objTitle.trim() }, locale)).append(";");
									continue;
								} else {
									cnlChannelContract.setPolicySendUserId(userId);
								}
							} else {
								cnlChannelContract.setPolicySendUserId(null);
							}
						}
						// 售后服务验证
						else if (objTitle.trim().equals(messageSource.getMessage("售后服务", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								List<CodeValue> codeList = codeValueMapper
										.selectCodeValuesByCodeName("CNL.CONTRACT_SERVICE_PARTY");
								String valueCode = null;
								for (CodeValue code : codeList) {
									if (code.getMeaning().equals(objValue)) {
										valueCode = code.getValue();
										break;
									}
								}
								if (valueCode == null) {
									cnlChannelContract.setPolicyFollow(null);
									errorMessage.append(messageSource.getMessage("售后服务在系统值列表中不存在",
											new Object[] { objTitle.trim() }, locale)).append(";");
									continue;
								} else {
									cnlChannelContract.setPolicyFollow(valueCode.trim());
								}
							} else {
								cnlChannelContract.setPolicyFollow(null);
							}
						}
						// 售后服务人验证
						else if (objTitle.trim().equals(messageSource.getMessage("售后服务人", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								ClbUser clbUser1 = new ClbUser();
								clbUser1.setUserType("OPERATOR");// 平台用户
								List<ClbUser> clbList = clbUserService.selectAllFields(request, clbUser1, 1, 1000);
								Long userId = null;
								for (ClbUser clbUser : clbList) {
									if (clbUser.getUserName().equals(objValue.trim())) {
										userId = clbUser.getUserId();
										break;
									}
								}
								if (userId == null) {
									cnlChannelContract.setPolicyFollowUserId(null);
									errorMessage.append(messageSource.getMessage("售后服务人在用户列表中不存在或者不是平台用户",
											new Object[] { objTitle.trim() }, locale)).append(";");
									continue;
								} else {
									cnlChannelContract.setPolicyFollowUserId(userId);
								}
							} else {
								cnlChannelContract.setPolicyFollowUserId(null);
							}
						} else {// 没有对应的标题，添加导入错误信息--只有第一行需要添加，后续行不需要重复添加
							if (importTemp.getLineNumber() == 2) {
								errorTitleMessage.append(messageSource.getMessage(Constants.TITLE_EXCEPTION,
										new Object[] { objTitle.trim() }, locale)).append(";");
							}
						}
					}
					if ("".equals(errorMessage.toString())) {// 通过第二次验证,进行数据转换
						// 校验‘联系电话’唯一性
						/*if (uniqueList.contains(cnlChannel.getPhoneCode() + cnlChannel.getContactPhone())) {
							errorMessage.append(messageSource.getMessage(
									cnlChannel.getPhoneCode() + cnlChannel.getContactPhone() + "这个联系电话已存在！", null,
									locale)).append(";");
						} else {
							uniqueList.add(cnlChannel.getPhoneCode() + cnlChannel.getContactPhone());
						}*/

						cnlChannelContract.setAttribute1(importBatchId.toString()); // 用Attribute1记录批次号
						// 调用生成编号的方法 给每条数据加上编号
						//cnlChannel.setChannelCode(sequenceService.getSequence("CHANNEL"));
						
						//渠道合约角色
						List<CnlContractRole> cnlContractRoles = new ArrayList<>();
						cnlContractRole1.setObjectVersionNumber(1L);
						cnlContractRole2.setObjectVersionNumber(1L);
						cnlContractRole3.setObjectVersionNumber(1L);
						cnlContractRoles.add(cnlContractRole1);
						cnlContractRoles.add(cnlContractRole2);
						cnlContractRoles.add(cnlContractRole3);
						cnlChannelContract.setCnlContractRoles(cnlContractRoles);
						//渠道合约费率
						List<CnlContractRate> cnlContractRates = new ArrayList<>();
						cnlContractRates.add(cnlContractRate);
						cnlChannelContract.setCnlContractRate(cnlContractRates);
						//渠道联系方式
						/*List<CnlChannelContact> cnlChannelContacts = new ArrayList<>();
						cnlChannelContacts.add(cnlChannelContact1);
						cnlChannelContacts.add(cnlChannelContact2);
						cnlChannel.setCnlChannelContact(cnlChannelContacts);*/
						//渠道合约
						cnlChannelContracts.add(cnlChannelContract);
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

		return cnlChannelContracts;
	}
}
