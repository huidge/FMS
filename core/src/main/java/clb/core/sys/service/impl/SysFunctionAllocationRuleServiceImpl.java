package clb.core.sys.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.github.pagehelper.PageHelper;
import com.hand.hap.account.mapper.UserRoleMapper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.channel.dto.CnlChannelContract;
import clb.core.channel.dto.CnlContractRole;
import clb.core.channel.mapper.CnlChannelContactMapper;
import clb.core.channel.mapper.CnlChannelContractMapper;
import clb.core.channel.mapper.CnlContractRoleMapper;
import clb.core.channel.service.ICnlChannelService;
import clb.core.common.service.ICommonService;
import clb.core.common.utils.DateUtil;
import clb.core.customer.dto.CtmCustomer;
import clb.core.customer.service.ICtmCustomerService;
import clb.core.notification.dto.NtnNotificationTemplate;
import clb.core.notification.mapper.NtnNotificationTemplateMapper;
import clb.core.notification.service.INtnNotificationService;
import clb.core.sys.dto.SysFunctionAllocationRule;
import clb.core.sys.dto.SysFunctionHandleNotification;
import clb.core.sys.dto.SysFunctionHandlePerson;
import clb.core.sys.dto.SysTaskTimeCfg;
import clb.core.sys.mapper.SysFunctionAllocationRuleMapper;
import clb.core.sys.mapper.SysFunctionHandleNotificationMapper;
import clb.core.sys.mapper.SysFunctionHandlePersonMapper;
import clb.core.sys.mapper.SysTaskTimeCfgMapper;
import clb.core.sys.service.ISysFunctionAllocationRuleService;
import clb.core.sys.service.ISysSendEmailService;
import clb.core.sys.service.ISysSendSMSService;
import clb.core.system.dto.ClbUser;
import clb.core.system.mapper.ClbUserMapper;

/*****
 * @author tiansheng.ye
 * @Date 2017-05-25
 */
@Service
@Transactional
public class SysFunctionAllocationRuleServiceImpl extends BaseServiceImpl<SysFunctionAllocationRule>
		implements ISysFunctionAllocationRuleService {

	@Autowired
	private SysFunctionAllocationRuleMapper sysFunctionAllocationRuleMapper;
	@Autowired
	private SysFunctionHandlePersonMapper sysFunctionHandlePersonMapper;
	@Autowired
	private ICnlChannelService cnlChannelService;
	@Autowired
	private ClbUserMapper userMapper;
	@Autowired
	private UserRoleMapper userRoleMapper;
	@Autowired
	private CnlChannelContactMapper cnlChannelContactMapper;
	@Autowired
	private CnlContractRoleMapper cnlContractRoleMapper;
	@Autowired
	private ICommonService commonService;
	@Autowired
	private SysTaskTimeCfgMapper sysTaskTimeCfgMapper;
	@Autowired
	private INtnNotificationService ntnNotificationService;
	@Autowired
	private SysFunctionHandleNotificationMapper sysFunctionHandleNotificationMapper;
	@Autowired
	private NtnNotificationTemplateMapper ntnNotificationTemplateMapper;
	@Autowired
	private  CnlChannelContractMapper cnlChannelContractMapper;
	@Autowired
	private ISysSendSMSService sysSendSMSService;
	@Autowired
	private ICtmCustomerService ctmCustomerService;
	@Autowired
	private ISysSendEmailService sysSendEmailService;
	
	@Override
	public List<SysFunctionAllocationRule> batchSubmit(IRequest request, List<SysFunctionAllocationRule> dto) {
		for (SysFunctionAllocationRule rule : dto) {
			if (rule.getRuleId() == null) {
				SysFunctionAllocationRule newRule = new SysFunctionAllocationRule();
				newRule.setFunctionCode(rule.getFunctionCode());
				newRule.setColumnCode(rule.getColumnCode());
				newRule.setColumnValue(rule.getColumnValue());
				if (sysFunctionAllocationRuleMapper.selectCount(newRule) > 0) {
					throw new RuntimeException("功能名+字段名+字段值不能重复");
				}
			}
		}
		return this.batchUpdate(request, dto);
	}

	@Override
	public List<SysFunctionAllocationRule> selectByDto(IRequest request, SysFunctionAllocationRule dto, int page,
			int pageSize) {
		PageHelper.startPage(page, pageSize);
		return sysFunctionAllocationRuleMapper.selectByDto(dto);
	}

	@Override
	public Long allocationPerson(IRequest request, String userType,Long createBy,Long allocationUserId,Long supplierId,Long channelId,Long noticeChannelId,Long customerId,Long sourceId, String functionCode, String columnCode, String columnValue,
			Map<String, Object> noticeMap) {
		SysFunctionAllocationRule rule = new SysFunctionAllocationRule();
		rule.setFunctionCode(functionCode);
		rule.setColumnCode(columnCode);
		rule.setColumnValue(columnValue);
		List<SysFunctionAllocationRule> ruleList = sysFunctionAllocationRuleMapper.select(rule);
		if (CollectionUtils.isEmpty(ruleList)) {
			// 若‘功能名+字段名+字段值’在规则中不存在
			//throw new RuntimeException("规则未定义");
			return null;
		}
		rule = ruleList.get(0);
		Long userId =null;
		if(allocationUserId!=null){
			userId=allocationUserId;
		}else{
			userId = getUserIdByParam(request, rule, userType, createBy, supplierId, channelId,customerId, sourceId, functionCode, columnCode, columnValue);
		}
		SysFunctionHandlePerson sysFunctionHandlePerson = new SysFunctionHandlePerson();
		sysFunctionHandlePerson.setFunctionCode(functionCode);
		sysFunctionHandlePerson.setColumnCode(columnCode);
		sysFunctionHandlePerson.setSourceId(sourceId);
		if (userId != null) {
			//userId=-1000L时，是不分配处理，但发通知
			if(userId!=-10000L){
				List<SysFunctionHandlePerson> hpList = sysFunctionHandlePersonMapper.select(sysFunctionHandlePerson);
				sysFunctionHandlePerson.setColumnValue(columnValue);
				sysFunctionHandlePerson.setUserId(userId);
				if (!CollectionUtils.isEmpty(hpList)) {
					sysFunctionHandlePerson.setHandleId(hpList.get(0).getHandleId());
					sysFunctionHandlePersonMapper.updateByPrimaryKeySelective(sysFunctionHandlePerson);
				} else {
					sysFunctionHandlePersonMapper.insertSelective(sysFunctionHandlePerson);
				}
			}

			if (rule.getSendNotice().equals("Y")) {
				// 发送通知
				SysFunctionHandleNotification sysFunctionHandleNotification = new SysFunctionHandleNotification();
				sysFunctionHandleNotification.setRuleId(rule.getRuleId());
				List<SysFunctionHandleNotification> notifyList = sysFunctionHandleNotificationMapper
						.select(sysFunctionHandleNotification);
				for (SysFunctionHandleNotification notice : notifyList) {
					NtnNotificationTemplate ntnNotificationTemplate= ntnNotificationTemplateMapper.selectByPrimaryKey(notice.getTemplateId());
					if (notice.getUserType().equals("CREATOR")) {
						ntnNotificationService.sendNotification(request,createBy, ntnNotificationTemplate.getTemplateCode(), noticeMap);
					}else if (notice.getUserType().equals("PROCESSOR")) {
						ntnNotificationService.sendNotification(request,userId, ntnNotificationTemplate.getTemplateCode(), noticeMap);
					}else{
						ClbUser user = new ClbUser();
						user.setUserType(notice.getUserType());
						if (notice.getUserType().equals("CHANNEL")) {
							user.setRelatedPartyId(noticeChannelId==null?-1000L:noticeChannelId);
						}else if (notice.getUserType().equals("SUPPLIER")) {
							user.setRelatedPartyId(supplierId==null?-1000L:supplierId);
						}else if (notice.getUserType().equals("CUSTOMER")) {
							user.setRelatedPartyId(customerId==null?-1000L:customerId);
						}
						List<ClbUser> userList = userMapper.selectAllFields(user);
						for(ClbUser u:userList){
							ntnNotificationService.sendNotification(request,u.getUserId(), ntnNotificationTemplate.getTemplateCode(), noticeMap);
						}
						//如果是客户，但客户未关联用户，则发用户的手机、email
						if(CollectionUtils.isEmpty(userList) && notice.getUserType().equals("CUSTOMER")){
							CtmCustomer ctmCustomer=new CtmCustomer();
							ctmCustomer.setCustomerId(customerId);
							ctmCustomer=ctmCustomerService.selectByPrimaryKey(request, ctmCustomer);
							//发短信
							sysSendSMSService.sendSMSByTemplate(request, ctmCustomer.getPhone(), ctmCustomer.getPhoneCode(),  ntnNotificationTemplate.getTemplateCode(), noticeMap);
							//发邮件
							sysSendEmailService.sendEmailByTemplateCode(request, ctmCustomer.getEmail(), ntnNotificationTemplate.getTemplateCode(), noticeMap);
						}
						
					}
				}
			}
		}
		return userId;
	}

	@Override
	public Long allocationPerson2(IRequest request, String userType, Long createBy, Long allocationUserId, Long supplierId, Long channelId, Long noticeChannelId, Long customerId, Long sourceId, String functionCode, String columnCode, String columnValue, Map<String, Object> noticeMap, String whetherSendMes) {
		SysFunctionAllocationRule rule = new SysFunctionAllocationRule();
		rule.setFunctionCode(functionCode);
		rule.setColumnCode(columnCode);
		rule.setColumnValue(columnValue);
		List<SysFunctionAllocationRule> ruleList = sysFunctionAllocationRuleMapper.select(rule);
		if (CollectionUtils.isEmpty(ruleList)) {
			// 若‘功能名+字段名+字段值’在规则中不存在
			//throw new RuntimeException("规则未定义");
			return null;
		}
		rule = ruleList.get(0);
		Long userId =null;
		if(allocationUserId!=null){
			userId=allocationUserId;
		}else{
			userId = getUserIdByParam(request, rule, userType, createBy, supplierId, channelId,customerId, sourceId, functionCode, columnCode, columnValue);
		}
		SysFunctionHandlePerson sysFunctionHandlePerson = new SysFunctionHandlePerson();
		sysFunctionHandlePerson.setFunctionCode(functionCode);
		sysFunctionHandlePerson.setColumnCode(columnCode);
		sysFunctionHandlePerson.setSourceId(sourceId);
		if (userId != null) {
			//userId=-1000L时，是不分配处理，但发通知
			if(userId!=-10000L){
				List<SysFunctionHandlePerson> hpList = sysFunctionHandlePersonMapper.select(sysFunctionHandlePerson);
				sysFunctionHandlePerson.setColumnValue(columnValue);
				sysFunctionHandlePerson.setUserId(userId);
				if (!CollectionUtils.isEmpty(hpList)) {
					sysFunctionHandlePerson.setHandleId(hpList.get(0).getHandleId());
					sysFunctionHandlePersonMapper.updateByPrimaryKeySelective(sysFunctionHandlePerson);
				} else {
					sysFunctionHandlePersonMapper.insertSelective(sysFunctionHandlePerson);
				}
			}

			if (rule.getSendNotice().equals("Y")) {
				// 发送通知
				SysFunctionHandleNotification sysFunctionHandleNotification = new SysFunctionHandleNotification();
				sysFunctionHandleNotification.setRuleId(rule.getRuleId());
				List<SysFunctionHandleNotification> notifyList = sysFunctionHandleNotificationMapper
						.select(sysFunctionHandleNotification);
				for (SysFunctionHandleNotification notice : notifyList) {
					NtnNotificationTemplate ntnNotificationTemplate= ntnNotificationTemplateMapper.selectByPrimaryKey(notice.getTemplateId());
					if (notice.getUserType().equals("CREATOR")) {
						ntnNotificationService.sendNotification(request,createBy, ntnNotificationTemplate.getTemplateCode(), noticeMap);
					}else if (notice.getUserType().equals("PROCESSOR")) {
						ntnNotificationService.sendNotification(request,userId, ntnNotificationTemplate.getTemplateCode(), noticeMap);
					}else{
						ClbUser user = new ClbUser();
						user.setUserType(notice.getUserType());
						if (notice.getUserType().equals("CHANNEL")) {
							user.setRelatedPartyId(noticeChannelId==null?-1000L:noticeChannelId);
						}else if (notice.getUserType().equals("SUPPLIER")) {
							user.setRelatedPartyId(supplierId==null?-1000L:supplierId);
						}else if (notice.getUserType().equals("CUSTOMER")) {
							user.setRelatedPartyId(customerId==null?-1000L:customerId);
						}
						List<ClbUser> userList = userMapper.selectAllFields(user);
						for(ClbUser u:userList){
							ntnNotificationService.sendNotification(request,u.getUserId(), ntnNotificationTemplate.getTemplateCode(), noticeMap);
						}
						//如果是客户，但客户未关联用户，则发用户的手机、email
						if(CollectionUtils.isEmpty(userList) && notice.getUserType().equals("CUSTOMER")){
							CtmCustomer ctmCustomer=new CtmCustomer();
							ctmCustomer.setCustomerId(customerId);
							ctmCustomer=ctmCustomerService.selectByPrimaryKey(request, ctmCustomer);
							//发短信
							if (!"N".equals(whetherSendMes)) {
								sysSendSMSService.sendSMSByTemplate(request, ctmCustomer.getPhone(), ctmCustomer.getPhoneCode(),  ntnNotificationTemplate.getTemplateCode(), noticeMap);
							}
							//发邮件
							sysSendEmailService.sendEmailByTemplateCode(request, ctmCustomer.getEmail(), ntnNotificationTemplate.getTemplateCode(), noticeMap);
						}

					}
				}
			}
		}
		return userId;
	}

	/***
	 * 根据信息获取用户ID
	 * 
	 * @param request
	 * @param userType
	 * @param sourceId
	 * @param functionCode
	 * @param columnCode
	 * @param columnValue
	 * @return
	 */
	public Long getUserIdByParam(IRequest request, SysFunctionAllocationRule rule, String userType,Long createBy,Long supplierId,Long channelId,Long customerId,Long sourceId, String functionCode, String columnCode, String columnValue) {

		// 查询处理人表
		SysFunctionHandlePerson handlePerson = new SysFunctionHandlePerson();
		handlePerson.setSourceId(sourceId);
		handlePerson.setFunctionCode(functionCode);
		handlePerson.setColumnCode(columnCode);
		handlePerson.setColumnValue(columnValue);
		List<SysFunctionHandlePerson> personList = sysFunctionHandlePersonMapper.select(handlePerson);
		if (!CollectionUtils.isEmpty(personList)) {
			if (personList.get(0).getColumnValue().equals(columnValue)) {
				// 若‘分配处理人’为空，则此跳过，end（即当该字段变为当前字段值时，不更新对应的当前处理人）
				return null;
			}
		}

		// 角色
		Long roleId = rule.getRoleId();
		//如果不分配，则返回 -10000 ,用于区分不分配用户，但仍然发通知
		if(roleId==null){
			return -10000L;
		}
		if (roleId == -1) {
			// 若‘分配处理人’=创建者，则直接取得表数据的created_by以指定为处理人
			return createBy;
		} else if (roleId == -2) {
			List<ClbUser> userList = queryUserByParam("CHANNEL", channelId);
			if (CollectionUtils.isEmpty(userList)) {
				throw new RuntimeException("查询不到渠道用户,channelId:" + channelId);
			} else {
				return userList.get(0).getUserId();
			}
		} else if (roleId == -3) {
			List<ClbUser> userList = queryUserByParam("SUPPLIER", supplierId);
			if (CollectionUtils.isEmpty(userList)) {
				throw new RuntimeException("查询不到供应商用户,supplierId:" + supplierId);
			} else {
				return userList.get(0).getUserId();
			}
		} else if (roleId == -4) {
			List<ClbUser> userList = queryUserByParam("CUSTOMER", customerId);
			if (CollectionUtils.isEmpty(userList)) {
				throw new RuntimeException("查询不到客户用户,customerId:" + customerId);
			} else {
				return userList.get(0).getUserId();
			}
		} else {

		}

		// 根据前置函数放回 用户数组
		Long[] userIdArray = preFunction(request, userType, supplierId, channelId,customerId, sourceId, rule.getRoleId());
		if (rule.getAllocationApi() == null) {
			// 若‘分配处理人’=角色ID但‘分配处理人API’为空，则根据通用前置函数取得用户列表后，在用户列表中随机指定某用户为当前处理人
			return userIdArray.length > 0 ? userIdArray[new Random().nextInt(userIdArray.length)] : null;
		}
		// 若‘分配处理人’=角色ID且‘分配处理人API’都不为空，则先根据通用前置函数取得用户列表后，再根据分配处理人API指定具体用户为当前处理人
		return allocationByAPI(request, userIdArray, rule, createBy, supplierId, channelId, sourceId);
	}

	public Long[] preFunction(IRequest request, String userType,Long supplierId,Long channelId,Long customerId,Long sourceId, Long roleId) {
		ClbUser user = new ClbUser();
		user.setRoleId(roleId);
		user.setUserType(userType);
		if(StringUtils.isBlank(userType)){
			throw new RuntimeException("前置函数中，用户类型不能为空");
		}
		if ("CHANNEL".equals(userType) ) {
			user.setRelatedPartyId(channelId);
		}else if ("SUPPLIER".equals(userType)) {
			user.setRelatedPartyId(supplierId);
		}else if ("SUPPLIER".equals(userType)) {
			user.setRelatedPartyId(supplierId);
		}else if ("CUSTOMER".equals(userType)) {
			user.setRelatedPartyId(customerId);
		}
		if(("CHANNEL".equals(userType)|| "SUPPLIER".equals(userType)|| "CUSTOMER".equals(userType)) && user.getRelatedPartyId()==null){
			throw new RuntimeException("前置函数中，用户类型为："+userType+",relatedPartyId不能为空");
		}
		/***
		 * 根据用户类型、渠道/供应商ID、角色ID得到用户列表
		 */
		List<ClbUser> userList = userMapper.selectFunctionUser(user);
		// 把用户的Id提取到一个数组中
		Long[] userIdArray = new Long[userList.size()];
		for (int i = 0; i < userList.size(); i++) {
			userIdArray[i] = userList.get(i).getUserId();
		}
		return userIdArray;
	}

	/****
	 * 根据用户类型，以及对应渠道/供应商 去查询用户
	 * @param userType
	 * @param paramId
	 * @return
	 */
	public List<ClbUser> queryUserByParam(String userType, Long paramId) {
		ClbUser user = new ClbUser();
		user.setUserType(userType);
		user.setRelatedPartyId(paramId);
		if(StringUtils.isBlank(userType)){
			throw new RuntimeException("用户类型不能为空");
		}
		if(paramId==null){
			throw new RuntimeException("用户类型为："+userType+",relatedPartyId不能为空");
		}
		return userMapper.select(user);
	}

	/*****
	 * 根据API分配用户
	 * @param request
	 * @param userIdArray
	 * @param rule
	 * @param createBy
	 * @return
	 */
	public Long allocationByAPI(IRequest request, Long[] userIdArray, SysFunctionAllocationRule rule,Long createBy,Long supplierId,Long channelId,Long sourceId) {
		String allocationApi = rule.getAllocationApi();

		if (allocationApi.equals("CREATOR")) {
			return createBy;
		} else if (allocationApi.equals("TASK_SHARE")) {
			// 任务均分API：按当天的用户分配该数据总量均分
			// -----查询当前的，该功能的所有的已分配用户数据
			SysFunctionHandlePerson handlePerson = new SysFunctionHandlePerson();
			handlePerson.setFunctionCode(rule.getFunctionCode());
			handlePerson.setLastUpdateDate(DateUtil.dayStart(new Date()));
			List<SysFunctionHandlePerson> handlePersonList = sysFunctionHandlePersonMapper.select(handlePerson);
			return getUserIdByTaskShare(userIdArray, handlePersonList);
		} else if (allocationApi.equals("BUSINESS_ADM")) {
			// 分配业务行政API：渠道字段所在表上的渠道ID，根据渠道ID到渠道表上取得业务行政ID，将处理人分配给该用户
			// -----根据渠道ID、合约主体=供应商 查询 合约信息
			CnlChannelContract cnlChannelContract = new CnlChannelContract();
			cnlChannelContract.setChannelId(channelId);
			cnlChannelContract.setPartyId(supplierId);
			cnlChannelContract.setPartyType("SUPPLIER");
			List<CnlChannelContract> contactList = cnlChannelContractMapper.select(cnlChannelContract);
			if (CollectionUtils.isEmpty(contactList)) {
				throw new RuntimeException("查询不到合约主体为供应商的合约信息,channelId:"+channelId);
			}
			// ------根据合约、对接行政 找到用户
			CnlContractRole cnlContractRole = new CnlContractRole();
			cnlContractRole.setChannelContractId(contactList.get(0).getChannelContractId());
			cnlContractRole.setRole("ADMINISTRATION");
			List<CnlContractRole> contractRoleList = cnlContractRoleMapper.select(cnlContractRole);
			if (CollectionUtils.isEmpty(contractRoleList)) {
				throw new RuntimeException("查询不到行政人员,channelId:"+channelId+",contactId:"+contactList.get(0).getChannelContractId());
			}
			return contractRoleList.get(0).getRoleUserId(); //渠道ID改为用户ID
		} else if (allocationApi.equals("BUSINESS_PLAN")) {
			// 计划书分配API：根据当天用户分配计划书对应的总工时，将任务分配给工时最少的人。
			// ------查询当天已分配计划书工作量的用户数据
			List<SysTaskTimeCfg> cfgList = sysTaskTimeCfgMapper.queryPlanHandle(new SysTaskTimeCfg());
			for(SysTaskTimeCfg cfg:cfgList){
				//先匹配 itemId+有提取+组合
				SysTaskTimeCfg cfg1=new SysTaskTimeCfg();
				cfg1.setAdditionalRiskFlag(cfg.getAdditionalRiskFlag());
				cfg1.setExtractFlag(cfg.getExtractFlag());
				cfg1.setHighMedicalFlag(cfg.getHighMedicalFlag());
				cfg1.setItemId(cfg.getItemId());
				List<SysTaskTimeCfg> cfgList1 = sysTaskTimeCfgMapper.select(cfg1);
				if(!CollectionUtils.isEmpty(cfgList1)){
					cfg.setTaskTime(cfgList1.get(0).getTaskTime());
					continue;
				}
				//匹配itemId
				cfg1=new SysTaskTimeCfg();
				cfg1.setItemId(cfg.getItemId());
				cfg1.setExtractFlag("N");
				cfg1.setHighMedicalFlag("N");
				cfgList1 = sysTaskTimeCfgMapper.select(cfg1);
				if(!CollectionUtils.isEmpty(cfgList1)){
					cfg.setTaskTime(cfgList1.get(0).getTaskTime());
					continue;
				}
				//匹配大中小类
				cfg1=new SysTaskTimeCfg();
				cfg1.setParams(" and ITEM_ID is null and ADDITIONAL_RISK_FLAG='N' and EXTRACT_FLAG='N' and HIGH_MEDICAL_FLAG='N' and big_class='"+cfg.getBigClass()+"' and mid_class='"+cfg.getMidClass()+"' and min_class='"+cfg.getMinClass()+"'");
				cfgList1 = sysTaskTimeCfgMapper.queryByParams(cfg1);
				if(!CollectionUtils.isEmpty(cfgList1)){
					cfg.setTaskTime(cfgList1.get(0).getTaskTime());
					continue;
				}
				cfg1=new SysTaskTimeCfg();
				cfg1.setParams(" and ITEM_ID is null and ADDITIONAL_RISK_FLAG='N' and EXTRACT_FLAG='N' and HIGH_MEDICAL_FLAG='N'  and big_class='"+cfg.getBigClass()+"' and mid_class='"+cfg.getMidClass()+"' and (min_class is null  or min_class='')");
				cfgList1 = sysTaskTimeCfgMapper.queryByParams(cfg1);
				if(!CollectionUtils.isEmpty(cfgList1)){
					cfg.setTaskTime(cfgList1.get(0).getTaskTime());
					continue;
				}
				cfg1=new SysTaskTimeCfg();
				cfg1.setParams(" and ITEM_ID is null and ADDITIONAL_RISK_FLAG='N' and EXTRACT_FLAG='N' and HIGH_MEDICAL_FLAG='N'  and big_class='"+cfg.getBigClass()+"' and (mid_class is null or mid_class='') and (min_class is null  or min_class='')");
				cfgList1 = sysTaskTimeCfgMapper.queryByParams(cfg1);
				if(!CollectionUtils.isEmpty(cfgList1)){
					cfg.setTaskTime(cfgList1.get(0).getTaskTime());
					continue;
				}
				
			}
			if(userIdArray.length<1){
				throw new RuntimeException("查询不到可分配用户");
			}
			return getUserIdByBusinessPlan(userIdArray, cfgList);
		}
		return null;
	}

	/*****
	 * 均分API
	 * 
	 * @param userIdArray
	 *            符合条件的所有用户
	 * @param handlePersonList
	 *            当前已分配的用户
	 * @return 随机 任务量最少的用户
	 */
	public Long getUserIdByTaskShare(Long[] userIdArray, List<SysFunctionHandlePerson> handlePersonList) {
		List<SysFunctionHandlePerson> personList = new ArrayList<SysFunctionHandlePerson>();
		for (Long userId : userIdArray) {
			SysFunctionHandlePerson hp = new SysFunctionHandlePerson();
			hp.setUserId(userId);
			hp.setOrderTotal(0);
			for (SysFunctionHandlePerson handlePerson : handlePersonList) {
				if (handlePerson.getUserId() == userId) {
					hp.setOrderTotal(hp.getOrderTotal() + 1);
				}
			}
			personList.add(hp);
		}
		// 最小分配数
		if(CollectionUtils.isEmpty(personList)){
			throw new RuntimeException("查询不到可分配用户");
		}
		int minTotal = personList.get(0).getOrderTotal();
		// 把所有的最小分配的userId放到一个list中
		List<Long> userIdList = new ArrayList<Long>();
		for (SysFunctionHandlePerson handlePerson : personList) {
			if (handlePerson.getOrderTotal() < minTotal) {
				minTotal = handlePerson.getOrderTotal();
				userIdList = new ArrayList<Long>();
				userIdList.add(handlePerson.getUserId());
			} else if (handlePerson.getOrderTotal() == minTotal) {
				userIdList.add(handlePerson.getUserId());
			}
		}
		return userIdList.size() > 0 ? userIdList.get(new Random().nextInt(userIdList.size())) : null;
	}

	/*****
	 * 计划书API
	 * 
	 * @param userIdArray
	 *            符合条件的所有用户
	 *            当前已分配的用户
	 * @return 随机 任务量最少的用户
	 */
	public Long getUserIdByBusinessPlan(Long[] userIdArray, List<SysTaskTimeCfg> cfgList) {
		List<SysFunctionHandlePerson> personList = new ArrayList<SysFunctionHandlePerson>();
		for (Long userId : userIdArray) {
			SysFunctionHandlePerson hp = new SysFunctionHandlePerson();
			hp.setUserId(userId);
			hp.setTaskTime(0.0);
			for (SysTaskTimeCfg handlePerson : cfgList) {
				if (handlePerson.getUserId().longValue() == userId.longValue()) {
					hp.setTaskTime(hp.getTaskTime()+handlePerson.getTaskTime());
				}
			}
			personList.add(hp);
		}
		// 最小分配数
		double minTaskTime = personList.get(0).getTaskTime();
		// 把所有的最小分配的userId放到一个list中
		List<Long> userIdList = new ArrayList<Long>();
		for (SysFunctionHandlePerson handlePerson : personList) {
			if (handlePerson.getTaskTime() < minTaskTime) {
				minTaskTime = handlePerson.getTaskTime();
				userIdList = new ArrayList<Long>();
				userIdList.add(handlePerson.getUserId());
			} else if (handlePerson.getTaskTime() == minTaskTime) {
				userIdList.add(handlePerson.getUserId());
			}
		}
		return userIdList.get(new Random().nextInt(userIdList.size()));
	}
}