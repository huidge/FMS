package clb.core.supplier.service.impl;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
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

import clb.core.common.service.ISequenceService;
import clb.core.fnd.dto.ImportTemp;
import clb.core.fnd.service.IImportTempService;
import clb.core.fnd.service.impl.AbstractImportExecute;
import clb.core.fnd.utils.Constants;
import clb.core.fnd.utils.ValidationTableException;
import clb.core.supplier.dto.SpeCollectionTerms;
import clb.core.supplier.dto.SpeSupplier;
import clb.core.supplier.service.ISpeCollectionTermsService;
import clb.core.supplier.service.ISpeSupplierService;

/**
 * 付款条件维护的数据导入
 * 
 * @author Administrator
 */
@Component
public class SpeCollectionTermsImport extends AbstractImportExecute {

	@Autowired
	private IImportTempService importTempService;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ISpeCollectionTermsService speCollectionTermsService;

	@Autowired
	private CodeValueMapper codeValueMapper;

	@Autowired
	private ISequenceService sequenceService;

	@Autowired
	private ISpeSupplierService speSupplierService;// 供应商

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
		List<SpeCollectionTerms> speCollectionTermss = validateData(importBatchId, request);

		if (importTempService.selectErrorCount(importBatchId) == 0) {// 验证没有错误
			speCollectionTermss.forEach(speCollectionTerms -> {
				List<SpeCollectionTerms> speCollectionTermsList = speCollectionTermsService.select(request,
						speCollectionTerms, 1, 10);
				;
				if (null != speCollectionTermsList && speCollectionTermsList.size() == 1) {
					speCollectionTermsList.get(0).setAttribute1(speCollectionTerms.getAttribute1());
					speCollectionTermsService.updateByPrimaryKeySelective(request, speCollectionTermsList.get(0));
				} else if (speCollectionTermsList.size() == 0) {
					speCollectionTermsService.insertSelective(request, speCollectionTerms);
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
	public List<SpeCollectionTerms> validateData(Long importBatchId, IRequest request)
			throws ValidationTableException, Exception {
		String[] locales = request.getLocale().split("_");
		Locale locale = new Locale(locales[0], locales[1]);

		PropertyDescriptor attributeTitle = null;
		PropertyDescriptor attributeValue = null;
		List<ImportTemp> importTemps = new ArrayList<ImportTemp>();// 根据流水号查得的所有临时表数据
		List<SpeCollectionTerms> speCollectionTerms = new ArrayList<SpeCollectionTerms>();// 将临时表的数据转换成供应商比例维护数据
		// 将没行数据的供应商id、客户id、联系人id存储，方便匹配是否有重复数据
		//Map<Map<Long, Long>, List<Long>> custContactSupMap = new HashMap<Map<Long, Long>, List<Long>>();

		List<String> uniqueList = new ArrayList<String>(); // 唯一性校验：条件分类+条件类型+产品公司+付款方类型+付款方名称

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
					SpeCollectionTerms speCollectionTermss = new SpeCollectionTerms();

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
						// 条件分类验证
						else if (objTitle.trim().equals(messageSource.getMessage("条件分类", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
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
										speCollectionTermss.setProductDivision(valueCode);
										errorMessage.append(messageSource.getMessage("条件分类在系统值列表中不存在",
												new Object[] { objTitle.trim() }, locale)).append(";");
										continue;
									} else {
										speCollectionTermss.setProductDivision(valueCode);
									}

								}
							} else {
								speCollectionTermss.setProductDivision(null);
								errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR,
										new Object[] { objTitle.trim() }, locale)).append(";");
							}
						}
						// 条件类型验证
						else if (objTitle.trim().equals(messageSource.getMessage("条件类型", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {

								if (null != objValue && !"".equals(objValue.trim())) {

									List<CodeValue> codeList = codeValueMapper
											.selectCodeValuesByCodeName("SPE.TERM_TYPE");
									String valueCode = null;
									for (CodeValue code : codeList) {
										if (code.getMeaning().equals(objValue)) {
											valueCode = code.getValue();
											break;
										}
									}
									if (valueCode == null) {
										speCollectionTermss.setTermType(valueCode);
										errorMessage.append(messageSource.getMessage("条件类型在系统值列表中不存在",
												new Object[] { objTitle.trim() }, locale)).append(";");
										continue;
									} else {
										speCollectionTermss.setTermType(valueCode);
									}

								}

							} else {
								speCollectionTermss.setTermType(null);
								errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR,
										new Object[] { objTitle.trim() }, locale)).append(";");
							}
						}
						// 产品公司  允许为空
						else if (objTitle.trim().equals(messageSource.getMessage("产品公司", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								// 需要通过产品公司名称 查询到 产品公司id
								if (null != objValue && !"".equals(objValue.trim())) {

									List<SpeSupplier> list = speSupplierService.selectAll(request);
									String valueCode = null;
									for (SpeSupplier speSupplier : list) {
										if (speSupplier.getName().equals(objValue)) {
											valueCode = speSupplier.getSupplierId().toString();
											break;
										}
									}

									if (valueCode == null) {
										speCollectionTermss.setProductCompanyId(null);
										/*errorMessage.append(messageSource.getMessage("该产品公司不存在",
												new Object[] { objTitle.trim() }, locale)).append(";");
										continue;*/
									} else {
										speCollectionTermss.setProductCompanyId(Long.parseLong(valueCode));
									}

								}

							} else {
								speCollectionTermss.setTermType(null);
								errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR,
										new Object[] { objTitle.trim() }, locale)).append(";");
							}

						}
						// 付款方类型验证
						else if (objTitle.trim().equals(messageSource.getMessage("付款方类型", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								// 需要查快码 判断 类型是否在快码列表里面
								List<CodeValue> codeList = codeValueMapper
										.selectCodeValuesByCodeName("SPE.PAYMENT_COMPANY_TYPE");
								String valueCode = null;
								for (CodeValue code : codeList) {
									if (code.getMeaning().equals(objValue)) {
										valueCode = code.getValue();
										break;
									}
								}
								if (valueCode == null) {
									speCollectionTermss.setProductDivision(valueCode);
									errorMessage.append(messageSource.getMessage("付款方类型在系统值列表中不存在",
											new Object[] { objTitle.trim() }, locale)).append(";");
									continue;
								} else {
									speCollectionTermss.setPaymentCompanyType(valueCode);
								}

							} else {
								speCollectionTermss.setPaymentCompanyType(null);
								errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR,
										new Object[] { objTitle.trim() }, locale)).append(";");
							}
						}
						// 付款方名称验证
						else if (objTitle.trim().equals(messageSource.getMessage("付款方名称", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								// 需要查快码 判断 类型是否在快码列表里面
								
								List<SpeSupplier> list = speSupplierService.selectAll(request);
								String valueCode = null;
								for (SpeSupplier speSupplier : list) {
									if (speSupplier.getName().equals(objValue)) {
										valueCode = speSupplier.getSupplierId().toString();
										break;
									}
								}

								if (valueCode == null) {
									speCollectionTermss.setPaymentCompanyId(null);
									errorMessage.append(messageSource.getMessage("该付款方不存在",
											new Object[] { objTitle.trim() }, locale)).append(";");
									continue;
								} else {
									speCollectionTermss.setPaymentCompanyId(Long.parseLong(valueCode));
								}
								
							} else {
								speCollectionTermss.setPaymentCompanyType(null);
								errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR,
										new Object[] { objTitle.trim() }, locale)).append(";");
							}
						}
						// 基准日期验证
						else if (objTitle.trim().equals(messageSource.getMessage("基准日期", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								// 需要查快码 判断 类型是否在快码列表里面
								List<CodeValue> codeList = codeValueMapper.selectCodeValuesByCodeName("SPE.BASE_DATE");
								String valueCode = null;
								for (CodeValue code : codeList) {
									if (code.getMeaning().equals(objValue)) {
										valueCode = code.getValue();
										break;
									}
								}
								if (valueCode == null) {
									speCollectionTermss.setProductDivision(valueCode);
									errorMessage.append(messageSource.getMessage("基准日期在系统值列表中不存在",
											new Object[] { objTitle.trim() }, locale)).append(";");
									continue;
								} else {
									speCollectionTermss.setBaseDate(valueCode);
								}

							} else {
								speCollectionTermss.setBaseDate(null);
								errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR,
										new Object[] { objTitle.trim() }, locale)).append(";");
							}
						}
						// 结算天数验证
						else if (objTitle.trim().equals(messageSource.getMessage("结算天数", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								speCollectionTermss.setSettleDays(Long.parseLong(objValue.trim()));

							} else {
								speCollectionTermss.setSettleDays(null);
								errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR,
										new Object[] { objTitle.trim() }, locale)).append(";");
							}
						}
						// 第1结算日验证
						else if (objTitle.trim().equals(messageSource.getMessage("第1结算日", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								// 需要查快码 判断 类型是否在快码列表里面
								List<CodeValue> codeList = codeValueMapper
										.selectCodeValuesByCodeName("SPE.SETTLEDATES");
								String valueCode = null;
								for (CodeValue code : codeList) {
									if (code.getMeaning().equals(objValue)) {
										valueCode = code.getValue();
										break;
									}
								}
								if (valueCode == null) {
									speCollectionTermss.setSettleDate1(valueCode);
									errorMessage.append(messageSource.getMessage("第1结算日在系统值列表中不存在",
											new Object[] { objTitle.trim() }, locale)).append(";");
									continue;
								} else {
									speCollectionTermss.setSettleDate1(valueCode);
								}

							} else {
								speCollectionTermss.setSettleDate1(null);
								errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR,
										new Object[] { objTitle.trim() }, locale)).append(";");
							}
						}
						// 第2结算日验证  允许为空
						else if (objTitle.trim().equals(messageSource.getMessage("第2结算日", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								// 需要查快码 判断 类型是否在快码列表里面
								List<CodeValue> codeList = codeValueMapper
										.selectCodeValuesByCodeName("SPE.SETTLEDATES");
								String valueCode = null;
								for (CodeValue code : codeList) {
									if (code.getMeaning().equals(objValue)) {
										valueCode = code.getValue();
										break;
									}
								}
								if (valueCode == null) {
									speCollectionTermss.setSettleDate2(null);
									/*errorMessage.append(messageSource.getMessage("第2结算日在系统值列表中不存在",
											new Object[] { objTitle.trim() }, locale)).append(";");
									continue;*/
								} else {
									speCollectionTermss.setSettleDate2(valueCode);
								}

							} else {
								speCollectionTermss.setSettleDate2(null);
								errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR,
										new Object[] { objTitle.trim() }, locale)).append(";");
							}
						}
						// 是否有效验证
						else if (objTitle.trim().equals(messageSource.getMessage("是否有效", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								// 需要查快码 判断 类型是否在快码列表里面
								List<CodeValue> codeList = codeValueMapper.selectCodeValuesByCodeName("SYS.YES_NO");
								String valueCode = null;
								for (CodeValue code : codeList) {
									if (code.getMeaning().equals(objValue)) {
										valueCode = code.getValue();
										break;
									}
								}
								if (valueCode == null) {
									speCollectionTermss.setStatusCode(valueCode);
									errorMessage.append(messageSource.getMessage("是否有效在系统值列表中不存在",
											new Object[] { objTitle.trim() }, locale)).append(";");
									continue;
								} else {
									speCollectionTermss.setStatusCode(valueCode);
								}

							} else {
								speCollectionTermss.setStatusCode(null);
								errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR,
										new Object[] { objTitle.trim() }, locale)).append(";");
							}
						} else {// 没有对应的标题，添加导入错误信息--只有第一行需要添加，后续行不需要重复添加
							if (importTemp.getLineNumber() == 2) {
								errorTitleMessage.append(messageSource.getMessage(Constants.TITLE_EXCEPTION,
										new Object[] { objTitle.trim() }, locale)).append(";");
							}
						}
					}

					if ("".equals(errorMessage.toString())) {// 通过第二次验证,进行数据转换

						// 唯一性校验：条件分类+条件类型+产品公司+付款方类型+付款方名称
						  if (uniqueList.contains(speCollectionTermss.getProductDivision()+speCollectionTermss.getTermType()
						  + speCollectionTermss.getProductCompanyId()+speCollectionTermss.getPaymentCompanyType()+speCollectionTermss.getPaymentCompanyId())) {
						  errorMessage.append(messageSource.getMessage(
						  "唯一性校验不通过,组合已存在！", null, locale)).append(";"); 
						  }else {
						  uniqueList.add(speCollectionTermss.getProductDivision()+speCollectionTermss.getTermType()
						  + speCollectionTermss.getProductCompanyId()+speCollectionTermss.getPaymentCompanyType()+speCollectionTermss.getPaymentCompanyId()); }
						 

						speCollectionTermss.setAttribute1(importBatchId.toString()); // 用Attribute1记录批次号
						// 调用生成编号的方法 给每条数据加上编号
						speCollectionTermss.setTermCode(sequenceService.getSequence("COLLECTIONTERM"));
						speCollectionTerms.add(speCollectionTermss);
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

		return speCollectionTerms;
	}
}
