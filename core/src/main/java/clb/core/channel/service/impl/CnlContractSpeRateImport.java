package clb.core.channel.service.impl;

import clb.core.channel.dto.CnlChannel;
import clb.core.channel.dto.CnlChannelContract;
import clb.core.channel.dto.CnlContractRate;
import clb.core.channel.dto.CnlContractRateHistory;
import clb.core.channel.dto.CnlLevel;
import clb.core.channel.service.*;
import clb.core.commission.dto.CmnChannelRatio;
import clb.core.commission.service.ICmnChannelRatioService;
import clb.core.common.service.ISequenceService;
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
import clb.core.system.dto.ClbUser;
import clb.core.system.mapper.ClbUserMapper;
import clb.core.system.service.IClbEmployeeService;
import clb.core.system.service.IClbUserService;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.dto.CodeValue;
import com.hand.hap.system.mapper.CodeValueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

/**
 * 供应商类型合约费率级别导入
 * 
 * @author Administrator
 */
@Component
public class CnlContractSpeRateImport extends AbstractImportExecute {

	@Autowired
	private IImportTempService importTempService;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ICnlChannelService channelService;

	@Autowired
	private ICnlChannelContractService contractService;
	
    @Autowired
    private ICnlContractRateHistoryService cnlContractRateHistoryService;

	@Autowired
	private ICnlLevelService levelService;

	@Autowired
	private ICmnChannelRatioService ratioService;

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
		//校验该批次数据是否已经导入过
		if (null != importBatchId) {
			//countImportBatchId = supplierAllotRatioService.selectCountImportBatchIdByAttrIbute(importBatchId);
		}

		if (countImportBatchId > 0) {
			throw new ValidationTableException(Constants.BATCH_REPEAT, null);
		}
		List<CnlChannelContract> channelContracts = validateData(importBatchId, request);

		if (importTempService.selectErrorCount(importBatchId) == 0) {   //验证没有错误

			// 更新数据
			for (CnlChannelContract contract : channelContracts) {
			    contractService.updateRatioLevel(contract);
                CnlContractRateHistory cnlContractRateHistory = new CnlContractRateHistory();
                cnlContractRateHistory.setChannelContractId(contract.getChannelContractId());
                cnlContractRateHistory.setRateLevelName(contract.getRateLevelName());
                cnlContractRateHistoryService.insertSelective(request, cnlContractRateHistory);
			}
		}
		StringBuffer errorMsg = new StringBuffer("");
	}

	/**
	 * @return
	 * @throws IntrospectionException
	 * @Description: 数据验证
	 * @author
	 */
	public List<CnlChannelContract> validateData(Long importBatchId, IRequest request) throws ValidationTableException, Exception {
		String[] locales = request.getLocale().split("_");
		Locale locale = new Locale(locales[0], locales[1]);
		IRequest iRequest = RequestHelper.newEmptyRequest();

		PropertyDescriptor attributeTitle = null;
		PropertyDescriptor attributeValue = null;
		List<ImportTemp> importTemps = new ArrayList<ImportTemp>();//根据流水号查得的所有临时表数据
		List<CnlChannelContract> channelContracts = new ArrayList<CnlChannelContract>();

		if (null != importBatchId) {
			//查出所有当前批次的数据
			importTemps = importTempService.selectImportData(importBatchId, 1, Constants.MAX_NUMBER, request);
		}
		List<String> sheets = new ArrayList<String>();//sheet页操作
		ImportTemp importTemp = new ImportTemp();//循环对象
		//对当前批次数据做迭代循环校验
		if (null != importTemps && importTemps.size() > 0) {
			Iterator<ImportTemp> iterator = importTemps.iterator();
			ImportTemp importTempTitle = null;
			StringBuffer errorTitleMessage = new StringBuffer("");//检查标题行是否有错
			//防止标题错误重复添加
			boolean isAppendErrorTitle = false;
			while (iterator.hasNext()) {
				importTemp = iterator.next();
				StringBuffer errorMessage = new StringBuffer("");
				if (!sheets.contains(importTemp.getSheet())) {//标题行
					sheets.add(importTemp.getSheet());
					if (!"".equals(errorTitleMessage.toString())) {//行标题有错误
						importTempTitle.setImportMessage(errorTitleMessage.toString());
						importTempService.updateError(importTempTitle, request);
					}
					errorTitleMessage = new StringBuffer("");
					isAppendErrorTitle = false;
					importTempTitle = importTemp;
					//标题行重复性验证
					Set<String> titleSet = new HashSet<String>();
					for (int i = 1; i <= Constants.MAX_ATTR; i++) {
						//标题字段
						attributeTitle = new PropertyDescriptor(Constants.ATTRIBUTE + (i), importTempTitle.getClass());
						Method methodRead = attributeTitle.getReadMethod();
						String objTitle = (String) methodRead.invoke(importTempTitle);
						if (null != objTitle && !"".equals(objTitle.trim())) {
							if (titleSet.contains(objTitle)) {
								errorTitleMessage.append(messageSource.getMessage(Constants.TITLE_EXCEPTION, new Object[]{objTitle.trim()},
										locale)).append(";");
							} else {
								titleSet.add(objTitle);
							}
						}
					}
				} else if (importTemp.getLineNumber() != 1) {   //第一行为标题行

					CnlChannelContract channelContract = new CnlChannelContract();

					//对所有属性循环
					for (int i = 1; i <= Constants.MAX_ATTR; i++) {
						//标题字段
						attributeTitle = new PropertyDescriptor(Constants.ATTRIBUTE + (i), importTempTitle.getClass());
						Method methodTitle = attributeTitle.getReadMethod();
						String objTitle = (String) methodTitle.invoke(importTempTitle);
						//当前数据
						attributeValue = new PropertyDescriptor(Constants.ATTRIBUTE + (i), importTemp.getClass());
						Method methodValue = attributeValue.getReadMethod();
						String objValue = (String) methodValue.invoke(importTemp);

						// *****************************************************************************
						// 以下为数据校验
						// *****************************************************************************
						// 以下按标题做判断对应标题做对应的验证,若没有对应到标题则在最后添加错误信息
						if (null == objTitle || "".equals(objTitle.trim())) {//标题为空时
							if (null == objValue || "".equals(objValue.trim())) {//行内数据也是空，跳出循环，即不允许出现空的列，否则后面数据当无效处理
								break;
							} else {//标题为空但行内有值，表示数据多余或标题缺失
								if (!isAppendErrorTitle) {
									isAppendErrorTitle = true;
									errorTitleMessage.append(messageSource.getMessage(Constants.ERROR_TITLE, null, locale)).append(";");
								}
							}
						}
						// 渠道名称
						else if (objTitle.trim().equals(messageSource.getMessage("渠道名称", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {

								CnlChannel paraChannel = new CnlChannel();
								paraChannel.setChannelName(objValue.trim());
								List<CnlChannel> channelList = channelService.queryChannel(iRequest, paraChannel);
								if (channelList != null && channelList.size() > 0) {
									channelContract.setChannelId(channelList.get(0).getChannelId());
								} else {
									errorMessage.append(messageSource.getMessage("渠道不存在", new Object[]{
											objTitle.trim()}, locale)).append(";");
									continue;
								}
							} else {
								errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
										locale)).append(";");
							}
						}

						// 合约编号
						else if (objTitle.trim().equals(messageSource.getMessage("合约编号", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {

								CnlChannelContract paraChannelContract = new CnlChannelContract();
								paraChannelContract.setChannelId(channelContract.getChannelId());
								paraChannelContract.setContractCode(objValue.trim());
								List<CnlChannelContract> contractList = new ArrayList<CnlChannelContract>();
								contractList = contractService.selectByCondition(paraChannelContract);
								if (contractList != null && contractList.size() > 0) {
									channelContract.setChannelContractId(contractList.get(0).getChannelContractId());
									channelContract.setPartyType(contractList.get(0).getPartyType());
									channelContract.setPartyId(contractList.get(0).getPartyId());
									channelContract.setChannelTypeCode(contractList.get(0).getChannelTypeCode());
								} else {
									errorMessage.append(messageSource.getMessage("合约编号不存在", new Object[]{
											objTitle.trim()}, locale)).append(";");
									continue;
								}

							} else {
								errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
										locale)).append(";");
							}
						}

						// 级别名称
						else if (objTitle.trim().equals(messageSource.getMessage("级别名称", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {

								if ("CHANNEL".equals(channelContract.getPartyType())) {
									CmnChannelRatio paraRatio = new CmnChannelRatio();
									paraRatio.setRatioName(objValue.trim());
									paraRatio.setChannelId(channelContract.getPartyId());
									List<CmnChannelRatio> ratioList = new ArrayList<CmnChannelRatio>();
									ratioList = ratioService.selectByChannelIdAndRatioName(paraRatio);
									if (ratioList != null && ratioList.size() > 0) {
										channelContract.setRateLevelName(ratioList.get(0).getRatioName());
										channelContract.setRateLevelId(ratioList.get(0).getRatioId());
									} else {
										errorMessage.append(messageSource.getMessage("级别名称不存在", new Object[]{
												objTitle.trim()}, locale)).append(";");
										continue;
									}

								} else if ("SUPPLIER".equals(channelContract.getPartyType())) {
									CnlLevel paraLevel = new CnlLevel();
									paraLevel.setLevelName(objValue.trim());
									paraLevel.setSupplierId(channelContract.getPartyId());
									paraLevel.setChannelClassCode(channelContract.getChannelTypeCode());
									List<CnlLevel> levelList = new ArrayList<CnlLevel>();
									levelList = levelService.selectByCondition(paraLevel);
									if (levelList != null && levelList.size() > 0) {
										channelContract.setRateLevelName(objValue.trim());
									} else {
										errorMessage.append(messageSource.getMessage("级别名称不存在", new Object[]{
												objTitle.trim()}, locale)).append(";");
										continue;
									}
								}
							} else {
								errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
										locale)).append(";");
							}
						}

						else {//没有对应的标题，添加导入错误信息--只有第一行需要添加，后续行不需要重复添加
							if (importTemp.getLineNumber() == 2) {
								errorTitleMessage.append(messageSource.getMessage(Constants.TITLE_EXCEPTION, new Object[]{objTitle.trim()},
										locale)).append(";");
							}
						}
					}

					if ("".equals(errorMessage.toString())) {//通过第二次验证,进行数据转换

						channelContracts.add(channelContract);
					}
				}

				//数据转换之后更新数据状态
				importTemp.setImportMessage(errorMessage.toString());//数据转换之后更新错误状态
				importTempService.updateError(importTemp, request);
			}

			if (!"".equals(errorTitleMessage.toString())) {//当前页行标题有错误
				importTempTitle.setImportMessage(errorTitleMessage.toString());
				importTempService.updateError(importTempTitle, request);
			}
		} else {
			throw new ValidationTableException(Constants.DATA_EMPTY, null);
		}

		return channelContracts;
	}
}
