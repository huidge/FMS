package clb.core.channel.service.impl;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.hand.hap.account.dto.UserRole;
import com.hand.hap.account.service.IUserRoleService;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.CodeValue;
import com.hand.hap.system.dto.ResponseData;
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
import clb.core.supplier.service.ISpeSupplierService;
import clb.core.system.dto.ClbUser;
import clb.core.system.service.IClbUserService;

/**
 * 渠道数据导入
 * 
 * @author Administrator
 */
@Component
public class CnlChannelUserImport extends AbstractImportExecute {

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
	private IUserRoleService userRoleService;

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
		List<ClbUser> users = validateData(importBatchId, request);

		if (importTempService.selectErrorCount(importBatchId) == 0) {// 验证没有错误
			users.forEach(user -> {
				List<ClbUser> clbUsers = clbUserService.selectLecturer(request, user);
				if (null != clbUsers && clbUsers.size() == 1) {
					clbUsers.get(0).setAttribute1(user.getAttribute1());
					clbUserService.updateByPrimaryKeySelective(request, clbUsers.get(0));
				
				} else if (CollectionUtils.isEmpty(clbUsers)) {
					CnlChannel channel = new CnlChannel();
					channel.setChannelId(user.getRelatedPartyId());
					ResponseData responseData = clbUserService.channelUserRegest(request, user, channel , null);
					if(responseData.getMessage() !=null) {
						throw new RuntimeException(responseData.getMessage());
					}
				}
			});
			
			/*for (ClbUser user : users) {
				List<ClbUser> clbUsers = clbUserService.selectLecturer(request, user);
				if (null != clbUsers && clbUsers.size() == 1) {
					clbUsers.get(0).setAttribute1(user.getAttribute1());
					clbUserService.updateByPrimaryKeySelective(request, clbUsers.get(0));
				
				} else if (null == clbUsers || clbUsers.size() == 0) {
					CnlChannel channel = new CnlChannel();
					channel.setChannelId(user.getRelatedPartyId());
					ResponseData responseData = clbUserService.channelUserRegest(request, user, channel , null);
					if(responseData.getMessage() !=null) {
						throw new RuntimeException(responseData.getMessage());
					}
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
	public List<ClbUser> validateData(Long importBatchId, IRequest request)
			throws ValidationTableException, Exception {
		String[] locales = request.getLocale().split("_");
		Locale locale = new Locale(locales[0], locales[1]);

		PropertyDescriptor attributeTitle = null;
		PropertyDescriptor attributeValue = null;
		List<ImportTemp> importTemps = new ArrayList<ImportTemp>();// 根据流水号查得的所有临时表数据
		List<ClbUser> users = new ArrayList<ClbUser>();// 将临时表的数据转换成供应商比例维护数据
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
					ClbUser user = new ClbUser();
					
					CnlChannel cnlChannel = new CnlChannel();
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
								cnlChannel.setChannelName(objValue.trim());
								List<CnlChannel> summary = cnlChannelService.queryChannelSummary(request, cnlChannel, 1, 1);
								Long channelId = null;
								if(summary != null && summary.size() > 0) {
									for (CnlChannel cnlChannel2 : summary) {
										String channelName = cnlChannel2.getChannelName();
										if(objValue.trim().equals(channelName)) {
											channelId = cnlChannel2.getChannelId();
											user.setRelatedPartyId(channelId);
											break;
										}
									}
									if(channelId == null) {
										user.setRelatedPartyId(null);
										errorMessage.append(messageSource.getMessage("渠道在系统数据库中找不到",
												new Object[] { objTitle.trim() }, locale)).append(";");
									}
								}else {
									user.setRelatedPartyId(null);
									errorMessage.append(messageSource.getMessage("渠道在系统数据库中不存在",
											new Object[] { objTitle.trim() }, locale)).append(";");
								}
							} else {
								user.setRelatedPartyId(null);
								errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR,
										new Object[] { objTitle.trim() }, locale)).append(";");
							}
						}
						//用户名
						else if (objTitle.trim().equals(messageSource.getMessage("用户名", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								user.setUserName(objValue.trim());
								List<ClbUser> list = clbUserService.selectLecturer(request, user);
								if(list != null && list.size() > 0 ) {
									user.setUserName(null);
									errorMessage.append(messageSource.getMessage("用户名已经存在",
											new Object[] { objTitle.trim() }, locale)).append(";");
								}
								
							} else {
								user.setUserName(null);
								errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR,
										new Object[] { objTitle.trim() }, locale)).append(";");
							}
						}
						//地区码
						else if (objTitle.trim().equals(messageSource.getMessage("地区码", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
									List<CodeValue> codeList = codeValueMapper
											.selectCodeValuesByCodeName("PUB.PHONE_CODE");
									String valueCode = null;
									for (CodeValue code : codeList) {
										if (code.getMeaning().equals(objValue)) {
											valueCode = code.getValue();
											break;
										}
									}
									if (valueCode == null) {
										user.setPhoneCode(null);
										errorMessage.append(messageSource.getMessage("地区码在系统值列表中不存在",
												new Object[] { objTitle.trim() }, locale)).append(";");
										continue;
									} else {
										user.setPhoneCode(valueCode.trim());
									}
								
							} else {
								user.setPhoneCode(null);
								errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR,
										new Object[] { objTitle.trim() }, locale)).append(";");
							}
						}
						//手机号
						else if (objTitle.trim().equals(messageSource.getMessage("手机号", null, locale))) {
							if (null != objValue && !"".equals(objValue.trim())) {
								user.setPhone(objValue.trim());
							} else {
								user.setPhone(null);
								errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR,
										new Object[] { objTitle.trim() }, locale)).append(";");
							}
						}
						
						else {// 没有对应的标题，添加导入错误信息--只有第一行需要添加，后续行不需要重复添加
							if (importTemp.getLineNumber() == 2) {
								errorTitleMessage.append(messageSource.getMessage(Constants.TITLE_EXCEPTION,
										new Object[] { objTitle.trim() }, locale)).append(";");
							}
						}
					}
					if ("".equals(errorMessage.toString())) {// 通过第二次验证,进行数据转换
						// 校验‘用户名’唯一性
						ClbUser clbUser = new ClbUser();
						clbUser.setUserName(user.getUserName());
						List<ClbUser> list = clbUserService.queryUserIdByUserTypeAndRelatedPartyName(null,clbUser);
						if(!CollectionUtils.isEmpty(list)) {
							errorMessage.append(messageSource.getMessage(
									user.getUserName() + "这个用户名在用户表中已存在！", null,
									locale)).append(";");
						}		
						
						if (uniqueList.contains(user.getUserName())) {
							errorMessage.append(messageSource.getMessage(
									user.getUserName() + "这个用户名在导入的excel中已存在！", null,
									locale)).append(";");
						} else {
							uniqueList.add(user.getUserName());
						}

						user.setAttribute1(importBatchId.toString()); // 用Attribute1记录批次号
						//设置用户类型为渠道
						user.setUserType("CHANNEL");
						//设置开始时间
						//user.setStartActiveDate(DateUtil.getDate("2017-08-11", "yyyy-MM-dd"));
						//设置状态是有效
						//user.setStatus("ACTV");
						//设置计划书额度为20
						//user.setPlanQuota(20L);
						
						users.add(user);
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

		return users;
	}
}
