package clb.core.system.service.impl;

import java.text.SimpleDateFormat;
import java.util.*;

import clb.core.channel.dto.CnlChannelContract;
import clb.core.channel.dto.CnlChannelContractSimple;
import clb.core.channel.dto.CnlContractRate;
import clb.core.channel.dto.CnlContractRateHistory;
import clb.core.channel.mapper.CnlChannelContractMapper;
import clb.core.channel.mapper.CnlChannelMapper;
import clb.core.channel.mapper.CnlContractRateMapper;
import clb.core.channel.service.ICnlChannelContractService;
import clb.core.channel.service.ICnlContractRateService;
import clb.core.common.utils.DateUtil;
import clb.core.supplier.mapper.SpeSupplierMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.github.pagehelper.PageHelper;
import com.hand.hap.account.dto.Role;
import com.hand.hap.account.dto.User;
import com.hand.hap.account.dto.UserRole;
import com.hand.hap.account.mapper.RoleMapper;
import com.hand.hap.account.mapper.UserMapper;
import com.hand.hap.account.mapper.UserRoleMapper;
import com.hand.hap.account.service.IRole;
import com.hand.hap.account.service.IUserRoleService;
import com.hand.hap.core.IRequest;
import com.hand.hap.security.PasswordManager;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IProfileService;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.channel.dto.CnlChannel;
import clb.core.channel.service.ICnlChannelService;
import clb.core.channel.service.ICnlContractRateHistoryService;
import clb.core.customer.dto.CtmCustomer;
import clb.core.customer.service.ICtmCustomerService;
import clb.core.notification.service.INtnNotificationService;
import clb.core.notification.service.INtnTemplateConcernService;
import clb.core.production.dto.PrdItems;
import clb.core.production.service.IPrdItemsService;
import clb.core.supplier.dto.SpeSupplier;
import clb.core.supplier.service.ISpeSupplierService;
import clb.core.system.dto.ClbEmployee;
import clb.core.system.dto.ClbUser;
import clb.core.system.dto.SysUserCustomFunction;
import clb.core.system.mapper.ClbEmployeeMapper;
import clb.core.system.mapper.ClbUserMapper;
import clb.core.system.service.IClbUserService;
import clb.core.system.service.ISysUserCustomFunctionService;

@Service
@Transactional
public class ClbUserServiceImpl extends BaseServiceImpl<ClbUser> implements IClbUserService {

	@Autowired
	private ClbUserMapper clbUserMapper;
	@Autowired
	private ICnlChannelService cnlChannelService;
	@Autowired
	private CnlChannelMapper cnlChannelMapper;
	@Autowired
	private RoleMapper roleMapper;
	@Autowired
	private ClbEmployeeMapper clbEmployeeMapper;
	@Autowired
	private INtnNotificationService ntnNotificationService;
	@Autowired
	private IProfileService profileService;
	@Autowired
	private UserRoleMapper userRoleMapper;
	@Autowired
	private ISysUserCustomFunctionService functionService;
	@Autowired
	private ICtmCustomerService ctmCustomerService;
	@Autowired
	private ISpeSupplierService speSupplierService;
	@Autowired
	private IUserRoleService userRoleService;
	@Autowired
	private PasswordManager passwordManager;
	@Autowired
	private UserMapper userMapper;
	@Autowired 
    private INtnTemplateConcernService ntnTemplateConcernService;
	@Autowired
	private CnlChannelContractMapper cnlChannelContractMapper;
	@Autowired
	private ICnlChannelContractService cnlChannelContractService;
    @Autowired
    private CnlContractRateMapper cnlContractRateMapper;
    @Autowired
    private ICnlContractRateHistoryService cnlContractRateHistoryService;
	@Autowired
	private ICnlContractRateService cnlContractRateService;
	@Autowired
	private SpeSupplierMapper speSupplierMapper;
	@Autowired
    private IPrdItemsService prdItemsService;
	
	public List<ClbUser> selectAllFields(IRequest requestContext, ClbUser clbUser, int page, int pageSize) {
		PageHelper.startPage(page, pageSize);
		return clbUserMapper.selectAllFields(clbUser);
	}

	public List<ClbUser> selectAll(IRequest requestContext, ClbUser clbUser) {
		return clbUserMapper.selectAllFields(clbUser);
	}

	public List<ClbUser> selectLecturer(IRequest requestContext, ClbUser clbUser) {
		return clbUserMapper.selectLecturer(clbUser);
	}

	public String validateContract(List<CnlChannelContractSimple> contracts,Long channelId,Long parentChannelId) {

		String flag = "Y";
		for (CnlChannelContractSimple c:contracts) {
			if (c.getChannelId().equals(parentChannelId)) {
				if (c.getPartyId().equals(channelId)) {
					return "N";
				} else {
					flag = validateContract(contracts, channelId, c.getPartyId());
					if ("N".equals(flag)) {
						return flag;
					}
				}
			}
		}
		return flag;
	}

	//创建成员时，如果渠道已经存在，只创建合约
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseData addChannelContract(IRequest requestContext, CnlChannel cnlChannel) {
		ResponseData response = new ResponseData(true);
		if (cnlChannel.getChannelId() != null && cnlChannel.getParentChannelId() != null) {
		    CnlChannelContract cnlChannelContract = new CnlChannelContract();
            cnlChannelContract.setChannelId(cnlChannel.getChannelId());
            cnlChannelContract.setPartyType("CHANNEL");
            cnlChannelContract.setPartyId(cnlChannel.getParentChannelId());
            cnlChannelContract.setProductDivision("BX");

            //验证合约是否为自己
            if (cnlChannel.getChannelId().equals(cnlChannel.getParentChannelId())) {
                response.setSuccess(false);
                response.setMessage("不能添加自己的账号为成员!");
                return response;
            }

            CnlChannelContract contract = new CnlChannelContract();
            contract.setPartyType("CHANNEL");
            contract.setProductDivision("BX");
            List<CnlChannelContractSimple> contracts = cnlChannelContractMapper.queryContractSimple(contract);

            String flag = validateContract(contracts,cnlChannel.getChannelId(),cnlChannel.getParentChannelId());

            if ("N".equals(flag)) {
                response.setSuccess(false);
                response.setMessage("该渠道不能被创建!");
                return response;
            }

            //验证合约是否存在
            if (!CollectionUtils.isEmpty(cnlChannelContractMapper.select(cnlChannelContract))) {
                response.setSuccess(false);
                response.setMessage("该成员已存在!");
                return response;
            }

            CnlChannel cnlChannelParent = cnlChannelMapper.selectByPrimaryKey(cnlChannel.getParentChannelId());
            CnlChannel cnlChannelChild = cnlChannelMapper.selectByPrimaryKey(cnlChannel.getChannelId());
            String contractCode = cnlChannelChild.getChannelCode() + cnlChannelParent.getChannelCode().replace("CH","");

            //创建合约
            cnlChannelContract.setChannelId(cnlChannel.getChannelId());
            cnlChannelContract.setContractCode(contractCode);
            cnlChannelContract.setContractTypeCode("SERVICE");
            cnlChannelContract.setPartyId(cnlChannel.getParentChannelId());
            cnlChannelContract.setPartyType("CHANNEL");
            cnlChannelContract.setProductDivision("BX");
            cnlChannelContract.setSettleFlag("N");
            cnlChannelContract.setContractApproach("GENERAL");
            cnlChannelContract.setDefineRateFlag(cnlChannel.getDefineRateFlag());
			cnlChannelContract.setStartDate(DateUtil.getNow());
            try{
				cnlChannelContract.setEndDate(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2099-12-31 23:59:59"));
			}catch (Exception e){
				response.setSuccess(false);
				response.setMessage("日期转换错误!");
				return response;
			}

		    /*
		     * 1、自定义费率=Y，需要保存费率表的数据
		     * 2、自定义费率=N，费率表的数据是根据渠道等级获取的数据，不需要保存合约费率表，只需要保存合约的等级ID和类型
		     */
		    if (cnlChannel.getDefineRateFlag() == "Y") {
		        if (cnlChannel.getCnlChannelContractRate() != null) {
		            cnlChannelContract = cnlChannelContractService.insertSelective(requestContext, cnlChannelContract);
	                //创建费率
	                for (CnlContractRate cnlContractRate : cnlChannel.getCnlChannelContractRate()) {
	                    if (cnlContractRate.getRate1() != null) {
	                        cnlContractRate.setChannelContractId(cnlChannelContract.getChannelContractId());
	                        try {
	                            CnlContractRate _cnlContractRate = new CnlContractRate();
                                _cnlContractRate.setChannelContractId(cnlContractRate.getChannelContractId());
                                _cnlContractRate.setBigClass(cnlContractRate.getBigClass());
                                _cnlContractRate.setMidClass(cnlContractRate.getMidClass());
                                _cnlContractRate.setMinClass(cnlContractRate.getMinClass());
                                _cnlContractRate.setItemId(cnlContractRate.getItemId());
                                //如果只传了产品id则查出后设置进去
                                if (cnlContractRate.getItemId()!=null&&cnlContractRate.getMinClass()==null||"".equals(cnlContractRate.getMinClass().trim())) {
                                	PrdItems prdItems = new PrdItems();
                    				prdItems.setItemId(cnlContractRate.getItemId());
                    				PrdItems items = prdItemsService.selectByPrimaryKey(requestContext, prdItems);
                    				_cnlContractRate.setBigClass(items.getBigClass());
                    				_cnlContractRate.setMidClass(items.getMidClass());
                    				_cnlContractRate.setMinClass(items.getMinClass());
								}
                                _cnlContractRate.setSublineId(cnlContractRate.getSublineId());
                                List<CnlContractRate> _cnlContractRateList = cnlContractRateMapper.selectRateByNull(_cnlContractRate);
                                if (_cnlContractRateList != null && _cnlContractRateList.size() > 1) {
                                    response.setSuccess(false);
                                    response.setMessage("不能设置相同的自定义级别！");
                                }
	                            cnlContractRateService.insertSelective(requestContext, cnlContractRate);
	                            //费率历史记录
                                CnlContractRateHistory _cnlContractRateHistory = new CnlContractRateHistory();
                                _cnlContractRateHistory.setChannelContractId(cnlContractRate.getChannelContractId());
                                _cnlContractRateHistory.setRateLevelName("自定义级别");
                                cnlContractRateHistoryService.insertSelective(requestContext, _cnlContractRateHistory);
	                        } catch(Exception e) {
	                            response.setSuccess(false);
	                            response.setMessage(e.getMessage());
	                        }
	                    } else {
	                        response.setSuccess(false);
	                        response.setMessage("第一年费率不能为空！");
	                    }
	                }
		        } else {
	                response.setSuccess(false);
	                response.setMessage("请维护至少一条佣金成分信息！");
		        }
		    } else {
		        if (cnlChannel.getRateLevelId() != null) {
	                cnlChannelContract.setRateLevelId(cnlChannel.getRateLevelId());
                    cnlChannelContract.setRateLevelName(cnlChannel.getRateLevelName());
                    cnlChannelContract = cnlChannelContractService.insertSelective(requestContext, cnlChannelContract);
                    //费率历史记录
                    CnlContractRateHistory _cnlContractRateHistory = new CnlContractRateHistory();
                    _cnlContractRateHistory.setChannelContractId(cnlChannelContract.getChannelContractId());
                    _cnlContractRateHistory.setRateLevelName(cnlChannelContract.getRateLevelName());
                    cnlContractRateHistoryService.insertSelective(requestContext, _cnlContractRateHistory);
	            } else {
                    response.setSuccess(false);
                    response.setMessage("请选择渠道等级！");
                }
		    }
		}
		return response;
	}

	@Override
	public ResponseData channelUserRegest(IRequest requestContext, ClbUser user, CnlChannel channel,
			String sendNotifyCode) {

		ResponseData response = new ResponseData(true);

		//微信号openId
		String wechatOpenid= user.getWechatOpenid();
		//微信号公众号openId
		String backgroundAppid=user.getBackgroundAppid();
		//APP用户
		String appUserId = user.getAppUserId();
		
		/*Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m = p.matcher(user.getUserName());
		if (m.find()) {
			response.setSuccess(false);
			response.setMessage("用户名中包含有中文，保存失败");
			return response;
		}*/
		ClbUser user1 = new ClbUser();
		user1.setUserName(user.getUserName());
		List<ClbUser> userList = select(requestContext, user1, 1, 10);
		if (!CollectionUtils.isEmpty(userList)) {
			response.setSuccess(false);
			response.setMessage("用户名已存在");
			return response;
		}

		// 查询是否存在渠道用户---验证手机号
		ClbUser saleuser = new ClbUser();
		saleuser.setUserType("CHANNEL");
		saleuser.setPhone(user.getPhone());
		saleuser.setPhoneCode(user.getPhoneCode());
		if (!CollectionUtils.isEmpty(clbUserMapper.selectAllFields(saleuser))) {
			response.setSuccess(false);
			response.setMessage("该手机号已被注册!");
			return response;
		}

		Role role = new Role();
		role.setRoleCode("O-Salesperson");
		List<Role> roleList = roleMapper.select(role);
		if (CollectionUtils.isEmpty(roleList)) {
			response.setSuccess(false);
			response.setMessage("注册异常!");
			return response;
		}
		Long roleId = roleList.get(0).getRoleId();
		channel.setContactPhone(user.getPhone());
		channel.setPhoneCode(user.getPhoneCode());

		SpeSupplier speSupplier = new SpeSupplier();
		speSupplier.setName("财联邦");
		List<SpeSupplier> speSuppliers = speSupplierMapper.select(speSupplier);
		if (CollectionUtils.isEmpty(speSuppliers)) {
			response.setSuccess(false);
			response.setMessage("请维护供应商：财联邦!");
			return response;
		} else {
			channel.setParentSupplierId(speSuppliers.get(0).getSupplierId());
		}

		try {
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟
			String dstr="2099-12-31";
			Date date= sdf.parse(dstr);
			channel.setContractStartDate(DateUtil.getNow());
			channel.setContractEndDate(date);
		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage("日期转换错误!");
			return response;
		}

		List<CnlChannel> cnlChannels = new ArrayList<CnlChannel>();
		cnlChannels.add(channel);
		cnlChannelService.channelBatchUpdate(requestContext, cnlChannels);
		user.setRelatedPartyId(channel.getChannelId());
		// 计划书额度 取配置文件
		String planQuota = profileService.getProfileValue(requestContext, "PLAN_INITIAL_QUOTA");
		user.setPlanQuota(Long.valueOf(planQuota));

		if (StringUtils.isNotBlank(wechatOpenid)) {
			user.setAttribute1("WX");
		} else if (StringUtils.isNotBlank(appUserId)) {
			user.setAttribute1("APP");
			user.setAttribute2(appUserId);
		}

		insertSelective(requestContext, user);
		// 插入角色表
		UserRole userRole = new UserRole();
		userRole.setRoleId(roleId);
		userRole.setUserId(user.getUserId());
		userRoleMapper.insertSelective(userRole);

		//如果是微信注册来源，则要执行更新微信绑定
      	if(StringUtils.isNotBlank(wechatOpenid)){
      		ntnTemplateConcernService.bind(requestContext,backgroundAppid, wechatOpenid,user.getUserId());
      	}
		
		// 插入用户自定义功能
		SysUserCustomFunction sysUserCustomFunction = new SysUserCustomFunction();
		sysUserCustomFunction.setUserId(user.getUserId());
		functionService.initFuncs(sysUserCustomFunction, requestContext);

		// 调用通知接口发送通知信息
		Map<String, Object> sysdata = new HashMap<String, Object>();
		sysdata.put("userName", user.getUserName());
		ntnNotificationService.sendNotification(requestContext, user.getUserId(), sendNotifyCode, sysdata);
		return response;
	}

	@Override
	public ResponseData customerUserRegest(IRequest requestContext, ClbUser user, CtmCustomer customer,Locale locale) {
		ResponseData response = new ResponseData(true);
		//是否存用户名
		boolean existUserName=StringUtils.isNotBlank(user.getUserName());
		//微信号openId
		String wechatOpenid= user.getWechatOpenid();
		//微信号公众号openId
		String backgroundAppid=user.getBackgroundAppid();
		
		if(!existUserName){
			user.setUserName("w"+user.getPhone());
		}else{
			/*Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
			Matcher m = p.matcher(user.getUserName());
			if (m.find()) {
				response.setSuccess(false);
				response.setMessage("用户名中包含有中文，保存失败");
				return response;
			}*/
			ClbUser user1 = new ClbUser();
			user1.setUserName(user.getUserName());
			List<ClbUser> userList = select(requestContext, user1, 1, 10);
			if (!CollectionUtils.isEmpty(userList)) {
				response.setSuccess(false);
				response.setMessage("用户名已存在");
				return response;
			}
		}
		// 查询是否存在客户用户---验证手机号
		ClbUser saleuser = new ClbUser();
		saleuser.setUserType("CUSTOMER");
		saleuser.setPhone(user.getPhone());
		saleuser.setPhoneCode(user.getPhoneCode());
		if (!CollectionUtils.isEmpty(clbUserMapper.selectAllFields(saleuser))) {
			response.setSuccess(false);
			response.setMessage("该手机号已被注册!");
			return response;
		}

        String isOK = ctmCustomerService.checkData(customer, locale);
		if ("CustomerExistUserNotExist".equals(isOK)) {
			CtmCustomer newCtmCustomer = new CtmCustomer();
			newCtmCustomer.setIdentityNumber(customer.getIdentityNumber());
			List<CtmCustomer> customers = ctmCustomerService.query(requestContext, newCtmCustomer, 1, 10);
			customer.setCustomerId(customers.get(0).getCustomerId());
		}
        if(!"".equals(isOK) && !"CustomerExistUserNotExist".equals(isOK)){
            response.setSuccess(false);
			response.setMessage(isOK);
			return response;
        }
		if ("".equals(isOK) || !"CustomerExistUserNotExist".equals(isOK)) {
			ctmCustomerService.createCustomer(requestContext, locale, customer);
		}

        user.setPasswordEncrypted(passwordManager.encode(user.getPassword()));
		user.setRelatedPartyId(customer.getCustomerId());
		self().insertSelective(requestContext, user);
		
		if(!existUserName){
			user.setUserName("W"+user.getUserId());
			self().updateByPrimaryKeySelective(requestContext, user);
		}
		
		//如果是微信注册来源，则要执行更新微信绑定
      	if(StringUtils.isNotBlank(wechatOpenid)){
      		ntnTemplateConcernService.bind(requestContext,backgroundAppid, wechatOpenid,user.getUserId());
      	}
		
		return response;
	}

	@Override
	public boolean isAdmin(IRequest request) {
		// 是否超级管理员
		boolean isAdmin = false;
		for (Long roleId : request.getAllRoleId()) {
			Role role = roleMapper.selectByPrimaryKey(roleId);
			if (role.getRoleCode().equals("ADMIN")) {
				isAdmin = true;
				break;
			}
		}
		return isAdmin;
	}
	
	@Override
	public boolean hasRoleByRoleCode(IRequest request,String roleCode){
		// 是否存在角色
		boolean hasRole = false;
		for (Long roleId : request.getAllRoleId()) {
			Role role = roleMapper.selectByPrimaryKey(roleId);
			if (role.getRoleCode().equals(roleCode)) {
				hasRole = true;
				break;
			}
		}
		return hasRole;
	}

	@Override
	public boolean isImporter(IRequest request) {
		// 超级导入权限角色
		boolean isImporter = false;
		for (Long roleId : request.getAllRoleId()) {
			Role role = roleMapper.selectByPrimaryKey(roleId);
			if (role.getRoleCode().equals("IMPORT")) {
				isImporter = true;
				break;
			}
		}
		return isImporter;
	}

	@Override
	public boolean isDaiBan(IRequest request) {
		// 待办行政角色
		boolean isDaiBan = false;

		UserRole role = new UserRole();
		role.setUserId(request.getUserId());
		List<IRole> roleList = userRoleService.selectUserRoles(request, role);
		for (IRole iRole : roleList) {
			if (iRole.getRoleCode().equals("Charge administrative")) {
				isDaiBan = true;
				break;
			}
		}
		return isDaiBan;
	}

	@Override
	public boolean isSupplier(IRequest request) {
		// 是否供应商角色
		boolean isSupplier = false;
		for (Long roleId : request.getAllRoleId()) {
			Role role = roleMapper.selectByPrimaryKey(roleId);
			if (role.getRoleCode().equals("SUPPLIER")) {
				isSupplier = true;
				break;
			}
		}
		return isSupplier;
	}

	@Override
	public Long getSupplierId(IRequest request) {
		ClbUser clbUser = clbUserMapper.selectByPrimaryKey(request.getUserId());
		Long supplierId = null;
		if (clbUser.getUserType().equals("SUPPLIER")) {
			supplierId = clbUser.getRelatedPartyId();
		} else if (clbUser.getUserType().equals("OPERATOR")) {
			ClbEmployee clbEmployee = clbEmployeeMapper.selectByPrimaryKey(clbUser.getRelatedPartyId());
			if (clbEmployee.getOwnershipType().equals("SUPPLIER")) {
				supplierId = clbEmployee.getOwnershipId();
			}
		}
		return supplierId;
	}

	@Override
	public List<ClbUser> queryUserByPhone(IRequest request, String phone, String phoneCode) {
		ClbUser user = new ClbUser();
		user.setPhone(phone);
		user.setPhoneCode(phoneCode);
		return clbUserMapper.select(user);
	}

	@Override
	public List<ClbUser> queryUserForNotice(IRequest request, String userType, Long userId) {
		List<ClbUser> userList = new ArrayList<ClbUser>();
		if(StringUtils.isBlank(userType)){
			throw new RuntimeException("用户类型不能为空");
		}
		if (userId == null) {
			// userId为空]
			if ("OPERATOR".equals(userType)) {
				// 平台用户
				List<ClbEmployee> List = clbEmployeeMapper.selectEmployeeByUserType(userType);
				for (ClbEmployee dto : List) {
					ClbUser user = new ClbUser();
					user.setPhone(dto.getMobil());
					user.setPhoneCode(dto.getPhoneCode());
					user.setEmail(dto.getEmail());
					// add
					userList.add(user);
				}
			} else if ("SUPPLIER".equals(userType)) {
				// 供应商
				List<SpeSupplier> List = speSupplierService.selectAll(request);
				for (SpeSupplier dto : List) {
					ClbUser user = new ClbUser();
					user.setPhone(dto.getContactNum());
					user.setPhoneCode(dto.getPhoneCode());
					user.setEmail(dto.getEmail());
					// add
					userList.add(user);
				}
			} else if ("CHANNEL".equals(userType)) {
				// 渠道
				List<CnlChannel> List = cnlChannelService.selectAll(request);
				for (CnlChannel dto : List) {
					ClbUser user = new ClbUser();
					user.setPhone(dto.getContactPhone());
					user.setPhoneCode(dto.getPhoneCode());
					user.setEmail(dto.getEmail());
					// add
					userList.add(user);
				}
			} else if ("CUSTOMER".equals(userType)) {
				// 客户
				List<CtmCustomer> List = ctmCustomerService.selectAll(request);
				for (CtmCustomer dto : List) {
					ClbUser user = new ClbUser();
					user.setPhone(dto.getPhone());
					user.setPhoneCode(dto.getPhoneCode());
					user.setEmail(dto.getEmail());
					// add
					userList.add(user);
				}
			}
		} else {
			// 否则根据用户ID取数据
			ClbUser user = new ClbUser();
			user.setUserId(userId);
			userList = clbUserMapper.selectAllFields(user);
		}
		return userList;
	}

	@Override
	public ResponseData channelUserSubmit(IRequest requestCtx, List<ClbUser> dto) {
		String userName = dto.get(0).getUserName();
		String status = dto.get(0).get__status();
		String validateStr = "^[A-Za-z]+$";

		/*Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m = p.matcher(userName);
		if (m.find()) {
			ResponseData responseData = new ResponseData();
			responseData.setSuccess(false);
			responseData.setMessage("用户名中包含有中文，保存失败");
			return responseData;
		}*/

		String newString = "";
		for (int i = 0; i < userName.length(); i++) {
			if (Character.isUpperCase(userName.charAt(i))) {
				newString += Character.toLowerCase(userName.charAt(i));
			} else {
				newString += userName.charAt(i);
			}
		}
		dto.get(0).setUserName(newString);
		// System.out.println(newString);

		for (ClbUser user : dto) {
			// 没有密码,使用默认密码
			if (StringUtils.isEmpty(user.getPassword()) && StringUtils.isEmpty(user.getPasswordEncrypted())) {
				user.setPasswordEncrypted(this.passwordManager.encode(this.passwordManager.getDefaultPassword()));
			} else if (!StringUtils.isEmpty(user.getPassword())) {
				user.setPasswordEncrypted(this.passwordManager.encode(user.getPassword()));
			}

			// 清空LOV值操作
			if (user.getRelatedPartyId() == null) {
				ClbUser usr = new ClbUser();
				usr.setUserId(user.getUserId());
				usr = selectByPrimaryKey(requestCtx, usr);
				usr.setRelatedPartyId(null);
				self().updateByPrimaryKey(requestCtx, usr);
			}
		}

		List<ClbUser> arrayList = selectAll(requestCtx, new ClbUser());
		for (ClbUser clbUser : dto) {
			if (DTOStatus.ADD.equals(clbUser.get__status())) {
				// System.out.println(clbUser.getUserType());
				// System.out.println(clbUser.getRelatedPartyId());
				for (ClbUser user : arrayList) {
					// System.out.println(user.getUserType());
					// System.out.println(user.getRelatedPartyId());
					if (StringUtils.isNotEmpty(user.getUserType()) && user.getRelatedPartyId() != null) {
						if (user.getUserType().equals(clbUser.getUserType())
								&& user.getRelatedPartyId().equals(clbUser.getRelatedPartyId())) {
							ResponseData responseData = new ResponseData();
							responseData.setMessage("关联方姓名不能重复！");
							responseData.setSuccess(false);
							// wb.close();
							return responseData;
						}
					}
				}
			}
		}
		for (ClbUser cu : dto) {
			if (cu.getUserId() != null) {
				self().updateByPrimaryKeySelective(requestCtx, cu);
			} else {
				self().insertSelective(requestCtx, cu);
				// 调用通知接口发送通知信息
				Map<String, Object> sysdata = new HashMap<String, Object>();
				sysdata.put("userName", cu.getUserName());
				ntnNotificationService.sendNotification(requestCtx, cu.getUserId(), "CJZH", sysdata);
			}
		}
		List<UserRole> userRoles = new ArrayList<>();

		Role role = new Role();
		role.setRoleCode("O-Salesperson");
		List<Role> roles = roleMapper.select(role);

		for (ClbUser clbUser : dto) {
			UserRole userRole = new UserRole();
			userRole.set__status(status);
			userRole.setUserId(clbUser.getUserId());
			userRole.setRoleId(roles.get(0).getRoleId());
			userRoles.add(userRole);
		}
		userRoleService.batchUpdate(requestCtx, userRoles);

		for (ClbUser clbUser : dto) {
			// 插入用户自定义功能
			SysUserCustomFunction sysUserCustomFunction = new SysUserCustomFunction();
			sysUserCustomFunction.setUserId(clbUser.getUserId());
			functionService.initFuncs(sysUserCustomFunction, requestCtx);
		}

		return new ResponseData(dto);
	}

	public void updateUserDetail(IRequest irequest, ClbUser user) {
		ClbUser user1 = new ClbUser();
		user1.setUserId(user.getUserId());
		List<ClbUser> userList = clbUserMapper.selectAllFields(user1);
		if (userList.get(0).getUserType().equals("CHANNEL")) {
			// 如果是渠道，则更新渠道表
			CnlChannel cnlChannel = new CnlChannel();
			cnlChannel.setChannelId(userList.get(0).getRelatedPartyId());
			cnlChannel.setContactPhone(user.getPhone());
			cnlChannel.setPhoneCode(user.getPhoneCode());
			cnlChannelService.updateByPrimaryKeySelective(irequest, cnlChannel);

			user.setUserId(irequest.getUserId());
			self().updateByPrimaryKeySelective(irequest, user);
		}
	}

	@Override
	public ResponseData forgetPassword(IRequest request, ClbUser user) {
		ResponseData response = new ResponseData(true);
		if (StringUtils.isBlank(user.getUserName())) {
			response.setSuccess(false);
			response.setMessage("用户名不能为空!");
			return response;
		}
		if (!user.getPassword().equals(user.getRepPassword())) {
			response.setSuccess(false);
			response.setMessage("密码与确认密码不一致!");
			return response;
		}
		User user1 = userMapper.selectByUserName(user.getUserName());
		if (user1 == null) {
			response.setSuccess(false);
			response.setMessage("用户 不存在!");
			return response;
		}
		ClbUser clbUser = new ClbUser();
		clbUser.setUserId(user1.getUserId());
		List<ClbUser> clbUserList = clbUserMapper.selectAllFields(clbUser);
		if (!clbUserList.get(0).getPhone().equals(user.getPhone())) {
			response.setSuccess(false);
			response.setMessage("该用户名对应的手机号与所填手机号不匹配!");
			return response;
		}

		user1.setPasswordEncrypted(passwordManager.encode(user.getPassword()));
		userMapper.updateByPrimaryKeySelective(user1);
		return response;
	}

	@Override
	public ResponseData changePhone(IRequest request, ClbUser user) {
		ResponseData response = new ResponseData(true);
		if (StringUtils.isBlank(user.getPhone()) || StringUtils.isBlank(user.getPhoneCode())) {
			response.setSuccess(false);
			response.setMessage("用户手机号/国家区号不能为空!");
			return response;
		}
		// 查询是否存在渠道用户---验证手机号
		ClbUser saleuser = new ClbUser();
		saleuser.setUserType("CHANNEL");
		saleuser.setPhone(user.getPhone());
		saleuser.setPhoneCode(user.getPhoneCode());
		if (!CollectionUtils.isEmpty(clbUserMapper.selectAllFields(saleuser))) {
			response.setSuccess(false);
			response.setMessage("该手机号已被注册,请换其他手机号码!");
			return response;
		}

		// 查询旧用户
		saleuser = new ClbUser();
		saleuser.setUserType("CHANNEL");
		saleuser.setUserId(user.getUserId());
		List<ClbUser> userList = clbUserMapper.selectAllFields(saleuser);
		if (CollectionUtils.isEmpty(userList)) {
			response.setSuccess(false);
			response.setMessage("查询不到渠道用户!");
			return response;
		}
		user.setUserId(userList.get(0).getUserId());
		updateUserDetail(request, user);
		return response;
	}
	/**
	 * 渠道导入程序  通过关联方名称和用户类型  查询用户
	 */
	@Override
	public List<ClbUser> queryUserIdByUserTypeAndRelatedPartyName(IRequest request, ClbUser clbUser) {
		return clbUserMapper.queryUserIdByUserTypeAndRelatedPartyName(clbUser);
	}
	/**
	 * 通过关联方id和关联方类型  查询平台用户的角色为售后行政的员工
	 */
	@Override
	public List<ClbUser> queryUserByOwnership(IRequest requestCtx, ClbUser user) {
		return clbUserMapper.queryUserByOwnership(user);
	}

	@Override
	public  ClbUser selectBusinessStaff(ClbUser clbUser) {
		return clbUserMapper.queryUserByUserTypeAndRoleNameAndUserName(clbUser);
	}

	@Override
	public List<ClbUser> queryOneUserByAppId(Long appId) {
		return clbUserMapper.queryOneUserByAppId(appId);
	}

	/*@Override
	public JSONObject wxUnfollow(IRequest request, ClbUser user) {
		JSONObject response = new JSONObject();
        response.put("success", true);
    	List<ClbUser>userList=this.selectAll(request, user);
    	if(CollectionUtils.isEmpty(userList)){
    		response.put("success", false);
		    response.put("message", "查询不到用户信息");
		    return response;
    	}
    	user.setUserId(userList.get(0).getUserId());
    	user.setWechatOpenid("");
    	this.updateByPrimaryKeySelective(request, user);
    	response.put("message", "取消成功");
    	return response;
	}*/
}