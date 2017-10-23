package clb.core.channel.service.impl;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.util.CollectionUtils;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.CodeValue;
import com.hand.hap.system.mapper.CodeValueMapper;

import clb.core.attachment.controllers.FmsAttachmentController;
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
import clb.core.production.dto.PrdItemSubline;
import clb.core.production.dto.PrdItems;
import clb.core.production.service.IPrdItemSublineService;
import clb.core.production.service.IPrdItemsService;
import clb.core.supplier.dto.SpeSupplier;
import clb.core.supplier.service.ISpeSupplierService;
import clb.core.system.dto.ClbUser;
import clb.core.system.mapper.ClbUserMapper;
import clb.core.system.service.IClbUserService;

/**
 * 渠道供应商产品关系数据导入
 * 
 * @author Administrator
 */
@Component
public class CnlProSupRelationImport2 extends AbstractImportExecute {

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
	private ICnlChannelContractService cnlChannelContractService;
	
	@Autowired
	private ICnlChannelContactService cnlChannelContactService;
	
	@Autowired
	private ICnlContractRateService cnlContractRateService;
	
	@Autowired
	private ICnlContractRoleService cnlContractRoleService;
	
	@Autowired
	private IPrdItemsService prdItemsService;
	@Autowired
	private IPrdItemSublineService prdItemSublineService;
	
	@Autowired
	private ClbUserMapper userMapper;
	

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
		List<ClbUser> clbUsrs = validateData(importBatchId, request);

		if (importTempService.selectErrorCount(importBatchId) == 0) {// 验证没有错误
			clbUsrs.forEach(clbUser -> {
				/*List<ClbUser> clbUserList = clbUserService.selectByCondition(request, clbUser, 1, 100);
				if (null != clbUserList && clbUserList.size() == 1) {
					clbUserList.get(0).setAttribute1(clbUser.getAttribute1());
					clbUserService.updateByPrimaryKeySelective(request, clbUserList.get(0));
				
				} else if (CollectionUtils.isEmpty(clbUserList)) {
					clbUserService.insertSelective(request, clbUser);
				}*/
				if (!"".equals(clbUser.getPlanQuota())
                        && clbUser.getPlanQuota() != null) {
					userMapper.updatePlanQuota(clbUser);
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
	public List<ClbUser> validateData(Long importBatchId, IRequest request)
			throws ValidationTableException, Exception {
		String[] locales = request.getLocale().split("_");
		Locale locale = new Locale(locales[0], locales[1]);

		PropertyDescriptor attributeTitle = null;
		PropertyDescriptor attributeValue = null;
		List<ImportTemp> importTemps = new ArrayList<ImportTemp>();// 根据流水号查得的所有临时表数据
		List<ClbUser> clbUsers = new ArrayList<ClbUser>();// 将临时表的数据转换成供应商比例维护数据
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
					ClbUser clbUser = new ClbUser();

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
						//用户名
					else if (objTitle.trim().equals(messageSource.getMessage("用户名", null, locale))) {
						if (null != objValue && !"".equals(objValue.trim())) {
							clbUser.setUserName(objValue.trim());
                        } else {
                            clbUser.setUserName("");
                            errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                    locale)).append(";");
						}
					}
						//计划书额度
					else if (objTitle.trim().equals(messageSource.getMessage("计划书额度", null, locale))) {
						if (null != objValue && !"".equals(objValue.trim())) {
							clbUser.setPlanQuota(Long.valueOf(objValue.trim()));
						} else {
							//cnlProSupRelation.setProductId(null);
							clbUser.setPlanQuota(null);
						}
					}
//						//产品名称验证
//					else if (objTitle.trim().equals(messageSource.getMessage("产品名称", null, locale))) {
//						if (null != objValue && !"".equals(objValue.trim())) {
//							PrdItems prdItems = new PrdItems();
//							prdItems.setItemName(objValue.trim());
//							Long itemId = prdItemsService.selectItemIdByItemName(objValue.trim());
//							
//							if (itemId == null) {
//								cnlProSupRelation.setProductId(itemId);
//								errorMessage.append(messageSource.getMessage("该产品不存在",
//										new Object[] { objTitle.trim() }, locale)).append(";");
//							} else {
//								cnlProSupRelation.setProductId(itemId);
//							}
//						} else {
//							cnlProSupRelation.setProductId(null);
//						}
//					}
						
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

						clbUser.setAttribute1(importBatchId.toString()); // 用Attribute1记录批次号
						
						clbUsers.add(clbUser);
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

		return clbUsers;
	}
}
