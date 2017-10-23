package clb.core.channel.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import clb.core.channel.dto.*;
import clb.core.commission.dto.CmnChannelCommission;
import clb.core.common.utils.AESUtil;
import clb.core.system.dto.ClbUser;
import clb.core.system.service.IClbUserService;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.attachment.service.ISysFileService;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IProfileService;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.channel.controllers.WsCnlMyTeamDataController;
import clb.core.channel.mapper.CnlChannelMapper;
import clb.core.channel.mapper.CnlContractRoleMapper;
import clb.core.channel.mapper.CnlContractRateMapper;
import clb.core.commission.mapper.CmnChannelCommissionMapper;
import clb.core.channel.service.ICnlChannelArchiveService;
import clb.core.channel.service.ICnlChannelContactService;
import clb.core.channel.service.ICnlChannelContractService;
import clb.core.channel.service.ICnlChannelService;
import clb.core.channel.service.ICnlContractRateHistoryService;
import clb.core.channel.service.ICnlContractRateService;
import clb.core.common.exceptions.CommonException;
import clb.core.common.service.ISequenceService;
import clb.core.common.utils.DateUtil;
import clb.core.common.utils.RefelctUtil;
import clb.core.common.utils.SortUtils;
import clb.core.exchangeRate.dto.FetExchangeRate;
import clb.core.exchangeRate.mapper.FetExchangeRateMapper;
import clb.core.fnd.utils.OssHelper;
import clb.core.order.dto.OrdOrder;
import clb.core.order.service.IOrdOrderService;

/**
 * @author jun.zhao@hand-china.com
 * @version 1.0
 * @name CnlChannelContactServiceImpl
 * @description 渠道信息 service 接口实现类
 * @date 2017/4/25
 */
@Service
@Transactional
public class CnlChannelServiceImpl extends BaseServiceImpl<CnlChannel> implements ICnlChannelService{
    @Autowired
    private CnlChannelMapper cnlChannelMapper;
    @Autowired
	private IOrdOrderService orderService;
    @Autowired
    private ICnlChannelContactService cnlChannelContactService;
    @Autowired
    private ICnlChannelArchiveService cnlChannelArchiveService;
    @Autowired
    private ICnlChannelContractService cnlChannelContractService;
    @Autowired
    private ISysFileService sysFileService;
    @Autowired
    private ISequenceService sequenceService;
	@Autowired
	private CnlContractRoleMapper cnlContractRoleMapper;
    @Autowired
    private CnlContractRateMapper cnlContractRateMapper;
    @Autowired
    private ICnlContractRateService cnlContractRateService;
    @Autowired
    private ICnlContractRateHistoryService cnlContractRateHistoryService;
    @Autowired
	private FetExchangeRateMapper rateMapper;
    @Autowired
    private IClbUserService clbUserService;
    @Autowired
	private IProfileService profileService;
    @Autowired
	private CmnChannelCommissionMapper cmnChannelCommissionMapper;
    

    private final Logger log = LoggerFactory.getLogger(OssHelper.class);
    
    //保险订单跟进状态
    private static final String[] INSURANCE_ORDER_FOLLOWING_STATUS = {"NEW","DATA_APPROVING","NEED_REVIEW","DATA_APPROVED",
    	    "RESERVING","RESERVE_SUCCESS","ARRIVAL","LEAVE"};
    	    
    //债券订单跟进状态
    private static final String[] BOND_ORDER_FOLLOWING_STATUS = {"DATA_APPROVING","NEED_REVIEW","DATA_APPROVED",
    	    "RESERVING","RESERVE_SUCCESS"};
    
    //投资移民订单跟进状态
    private static final String[] IMMIGRANT_ORDER_FOLLOWING_STATUS = {"NEGOTIATE","RESERVING"};
    
    //保险订单签单状态
    private static final String[] INSURANCE_ORDER_SIGN_STATUS = {"SIGNED","APPROVING","PENDING","POLICY_EFFECTIVE",
    "SURRENDERING","SURRENDERED","DECLINED","SUSPENDED","EXPIRED"};
    
    //债券订单签单状态
    private static final String[] BOND_ORDER_SIGN_STATUS = {"WAITING_ISSUE","ISSUE_SUCCESS"};
    
    //投资移民订单签单状态
    private static final String[] IMMIGRANT_ORDER_SIGN_STATUS = {"BUY_SUCCESS"};
    
    //订单类型
    private static final String[] ORDER_TYPES = {"INSURANCE","BOND","IMMIGRANT"};
    
    //汇率取值数组
    private static final String[] EXCHANGE_DATA= {"foreignCurrency","baseCurrency"};
    /**
     * 汇总查询
     * @param request
     * @param cnlChannel
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public List<CnlChannel> queryChannelSummary(IRequest request, CnlChannel cnlChannel, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        List<CnlChannel> cnlChannelList = cnlChannelMapper.queryChannelSummary(cnlChannel);
        return cnlChannelList;
    }

	@Override
	public List<CnlChannel> queryChannelSummaryPriv(IRequest request, CnlChannel cnlChannel, int page, int pagesize) {
		PageHelper.startPage(page, pagesize);
		List<CnlChannel> cnlChannelList = cnlChannelMapper.queryChannelSummaryPriv(cnlChannel);
		return cnlChannelList;
	}

    /**
     * 查询
     * @param request
     * @param cnlChannel
     * @return
     */
    @Override
    public List<CnlChannel> queryChannel(IRequest request, CnlChannel cnlChannel) {
        return cnlChannelMapper.queryChannelSummary(cnlChannel);
    }

    /**
     * 查询
     * @param request
     * @param cnlChannel
     * @return
     * @throws CommonException 
     */
    @Override
    public List<ReturnData> queryWsChannel(IRequest request, CnlChannel cnlChannel) throws CommonException {
    	//查询我的团队下的渠道
    	List<CnlChannel> channels = cnlChannelMapper.queryWsChannel(cnlChannel);
    	//查询所有汇率数据
    	FetExchangeRate exchangeRate = new FetExchangeRate();
		exchangeRate.setCurrentTime(new Date());
		List<FetExchangeRate> allExchangeRateData = rateMapper.selectRateByCurrentTime(exchangeRate);
    	
    	OrdOrder paramOrder = new OrdOrder();
        //获取所有订单数据
        List<OrdOrder> allOrders = orderService.queryWsTeamOrder(request, paramOrder, 0, 0);
    	/* ----------------跟进中订单数量----------------*/
    	//跟进中保单数量
    	Long insuranceInProcessQty = 0L;
    	//跟进中债券订单数量
    	Long bondInProcessQty = 0L;
    	//跟进中移民投资订单数量
    	Long immigrationInProcessQty = 0L;
    	//跟进中基金订单数量
    	Long fundInProcessQty = 0L;
    	//跟进中所有订单数量
    	Long totalInProcessQty = 0L;
    	//全部订单数量
    	Long allInProcessQty = 0L;
    	/* ----------------跟进中订单数量----------------*/
    	
    	/* ----------------生效订单数量----------------*/
    	//生效保单数量
    	Long insuranceInEffictiveQty = 0L;
    	//生效债券订单数量
    	Long bondInEffictiveQty = 0L;
    	//生效移民投资订单数量
    	Long immigrationInEffictiveQty = 0L;
    	//生效基金订单数量
    	Long fundInEffictiveQty = 0L;
    	//生效所有订单数量
    	Long totalInEffictiveQty = 0L;
    	//全部生效订单
    	Long allInEffictiveQty = 0L;
    	/* ----------------生效订单数量----------------*/
    	
    	/* ----------------签单数量----------------*/
    	Long signQty = 0L;
    	/* ----------------签单数量----------------*/
    	
    	/* ----------------签单总量----------------*/
    	Long allSignQty = 0L;
    	/* ----------------签单总量----------------*/
    	
    	/* ----------------签单金额----------------*/
    	BigDecimal SignAmount = BigDecimal.ZERO;
    	/* ----------------签单金额----------------*/
    	
    	/* ----------------签单总额----------------*/
    	BigDecimal allSignAmount = BigDecimal.ZERO;
    	/* ----------------签单金额----------------*/
    	
    	
    	for(CnlChannel channel:channels){
    		/* ---------------每次遍历都初始化变量--------------- */
        	insuranceInProcessQty = 0L;
        	bondInProcessQty = 0L;
        	immigrationInProcessQty = 0L;
        	fundInProcessQty = 0L;
        	totalInProcessQty = 0L;
        	insuranceInEffictiveQty = 0L;
        	bondInEffictiveQty = 0L;
        	immigrationInEffictiveQty = 0L;
        	fundInEffictiveQty = 0L;
        	totalInEffictiveQty = 0L;
        	signQty = 0L;
        	SignAmount = BigDecimal.ZERO;
    		/* ---------------每次遍历都初始化变量--------------- */
    		//查询订单数
    		List<OrdOrder> orders = getOrderDataByChannelId(allOrders,channel.getChannelId());
    		for(OrdOrder order:orders){
    			//计算缴费总额
				BigDecimal exchange = BigDecimal.ONE;
				//不是指定的订单类型
				if(WsCnlMyTeamDataController.searchString(ORDER_TYPES,order.getOrderType())<0){
					continue;
				}
				if(!"HKD".equals(order.getCurrency())){
					FetExchangeRate param = new FetExchangeRate();
					//外币
					param.setForeignCurrency(order.getCurrency());
					//港币
					param.setBaseCurrency("HKD");
					List<FetExchangeRate> exchangeRates = (List<FetExchangeRate>)RefelctUtil.getListDataByFields(allExchangeRateData,param,EXCHANGE_DATA);
					if(CollectionUtils.isEmpty(exchangeRates)){
			        	throw new CommonException("CNL",String.format("请维护外币：%s和基准货币:HKD的汇率!",order.getCurrency()),null);
			        	
					}
					exchange = new BigDecimal(exchangeRates.get(0).getRate());
				}
    			if("INSURANCE".equals(order.getOrderType())){
    				//跟进中
    				if(WsCnlMyTeamDataController.searchString(INSURANCE_ORDER_FOLLOWING_STATUS,order.getStatus()) >= 0){
    					insuranceInProcessQty += 1;
    					totalInProcessQty +=1;
    				}
    				//生效
    				if("POLICY_EFFECTIVE".equals(order.getStatus())){
    					insuranceInEffictiveQty += 1;
    					totalInEffictiveQty +=1;
    				}
    				//签单数量
    				if(WsCnlMyTeamDataController.searchString(INSURANCE_ORDER_SIGN_STATUS,order.getStatus()) >= 0){
    					signQty+=1;
    					//签单金额
    					SignAmount = SignAmount.add(order.getYearPayAmount().multiply(exchange));
    				}
    			}
    			if("BOND".equals(order.getOrderType())){
    				//跟进中
    				if(WsCnlMyTeamDataController.searchString(BOND_ORDER_FOLLOWING_STATUS,order.getStatus()) >= 0){
    					bondInProcessQty += 1;
    					totalInProcessQty +=1;
    				}
    				//生效
    				if("ISSUE_SUCCESS".equals(order.getStatus())){
    					bondInEffictiveQty += 1;
    					totalInEffictiveQty +=1;
    				}
    				//签单数量
    				if(WsCnlMyTeamDataController.searchString(BOND_ORDER_SIGN_STATUS,order.getStatus()) >= 0){
    					signQty+=1;
    					//签单金额
    					SignAmount = SignAmount.add(order.getPolicyAmount().multiply(exchange));
    				}
    			}
    			if("IMMIGRANT".equals(order.getOrderType())){
    				//跟进中
    				if(WsCnlMyTeamDataController.searchString(IMMIGRANT_ORDER_FOLLOWING_STATUS,order.getStatus()) >= 0){
    					immigrationInProcessQty += 1;
    					totalInProcessQty +=1;
    				}
    				//生效
    				if("BUY_SUCCESS".equals(order.getStatus())){
    					immigrationInEffictiveQty += 1;
    					totalInEffictiveQty +=1;
    				}
    				//签单数量
    				if(WsCnlMyTeamDataController.searchString(IMMIGRANT_ORDER_SIGN_STATUS,order.getStatus()) >= 0){
    					signQty+=1;
    					//签单金额
    					SignAmount = SignAmount.add(order.getPolicyAmount().multiply(exchange));
    				}
    			}
    		}
    		
    		//跟进中订单数量
    		channel.setInsuranceInProcessQty(insuranceInProcessQty);
    		channel.setBondInProcessQty(bondInProcessQty);
    		channel.setImmigrationInProcessQty(immigrationInProcessQty);
    		channel.setFundInProcessQty(fundInProcessQty);
    		channel.setTotalInProcessQty(totalInProcessQty);
    		//全部跟进中订单数量
    		allInProcessQty = allInProcessQty+totalInProcessQty;
    		
    		//生效中订单数量
    		channel.setInsuranceInEffictiveQty(insuranceInEffictiveQty);
    		channel.setBondInEffictiveQty(bondInEffictiveQty);
    		channel.setImmigrationInEffictiveQty(immigrationInEffictiveQty);
    		channel.setFundInEffictiveQty(fundInEffictiveQty);
    		channel.setTotalInEffictiveQty(totalInEffictiveQty);
    		//全部生效中订单数量
    		allInEffictiveQty = allInEffictiveQty+totalInEffictiveQty;
    		
    		//签单数量
    		channel.setSignQty(signQty);
    		//全部签单数量
    		allSignQty = allSignQty+signQty;
    		
    		//签单金额
    		SignAmount = SignAmount.setScale(0,BigDecimal.ROUND_HALF_UP);
    		channel.setSignAmount(SignAmount);
    		//全部签单金额
    		allSignAmount = allSignAmount.add(SignAmount);
    	}
    	if(CollectionUtils.isNotEmpty(channels)){
    		for(int i=0;i<channels.size();i++){
    			channels.get(i).setAllInProcessQty(allInProcessQty);
        		channels.get(i).setAllInEffictiveQty(allInEffictiveQty);
        		channels.get(i).setAllSignQty(allSignQty);
        		channels.get(i).setAllSignAmount(allSignAmount);
    		}
    	}
    	
    	
    	//排序
    	List<SortParam> sortParams = cnlChannel.getOrderBy();
    	int rule = 0;
    	if(CollectionUtils.isNotEmpty(sortParams)){
    		rule = sortParams.get(0).getRule();
    		ArrayList<String> param = new ArrayList<>();
    		for(SortParam sParam:sortParams){
    			param.add(sParam.getName());
    		}
        	Collections.sort(channels,SortUtils.createComparator(rule,param));
    	}
    	List<ReturnData> resDatas = new ArrayList<>();
    	for(CnlChannel channel:channels){
    		ReturnData resData = new ReturnData();
    		try {
				BeanUtils.copyProperties(resData,channel);
			} catch (Exception e) {
				log.error("复制Bean属性失败!");
				resData = new ReturnData();
			}
    		resDatas.add(resData);
    	}
        return resDatas;
    }
    
    public class ReturnData{
    	private String userName;
    	
    	private String channelName;
    	
    	private Long planQuota;
    	
    	private Long totalInProcessQty;
    	
    	private Long totalInEffictiveQty;
    	
    	private Long signQty;
    	
    	private BigDecimal signAmount;
    	
    	private Long channelId;
    	
    	private Long userId;
    	
    	private Long allInProcessQty;
    	
    	private Long allInEffictiveQty;
    	
    	private Long allSignQty;
    	
    	private Long allSignAmount;

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getChannelName() {
			return channelName;
		}

		public void setChannelName(String channelName) {
			this.channelName = channelName;
		}

		public Long getPlanQuota() {
			return planQuota;
		}

		public void setPlanQuota(Long planQuota) {
			this.planQuota = planQuota;
		}

		public Long getTotalInProcessQty() {
			return totalInProcessQty;
		}

		public void setTotalInProcessQty(Long totalInProcessQty) {
			this.totalInProcessQty = totalInProcessQty;
		}

		public Long getTotalInEffictiveQty() {
			return totalInEffictiveQty;
		}

		public void setTotalInEffictiveQty(Long totalInEffictiveQty) {
			this.totalInEffictiveQty = totalInEffictiveQty;
		}

		public Long getSignQty() {
			return signQty;
		}

		public void setSignQty(Long signQty) {
			this.signQty = signQty;
		}

		public BigDecimal getSignAmount() {
			return signAmount;
		}

		public void setSignAmount(BigDecimal signAmount) {
			this.signAmount = signAmount;
		}

		public Long getChannelId() {
			return channelId;
		}

		public void setChannelId(Long channelId) {
			this.channelId = channelId;
		}

		public Long getUserId() {
			return userId;
		}

		public void setUserId(Long userId) {
			this.userId = userId;
		}

		public Long getAllInProcessQty() {
			return allInProcessQty;
		}

		public void setAllInProcessQty(Long allInProcessQty) {
			this.allInProcessQty = allInProcessQty;
		}

		public Long getAllInEffictiveQty() {
			return allInEffictiveQty;
		}

		public void setAllInEffictiveQty(Long allInEffictiveQty) {
			this.allInEffictiveQty = allInEffictiveQty;
		}

		public Long getAllSignQty() {
			return allSignQty;
		}

		public void setAllSignQty(Long allSignQty) {
			this.allSignQty = allSignQty;
		}

		public Long getAllSignAmount() {
			return allSignAmount;
		}

		public void setAllSignAmount(Long allSignAmount) {
			this.allSignAmount = allSignAmount;
		}
    	
    	
    }
    
    private List<OrdOrder> getOrderDataByChannelId(List<OrdOrder> originData,Long channelId){
    	//获取渠道Id
		List<OrdOrder> orders = new ArrayList<>();
    	for(OrdOrder order:originData){
    		if(channelId.equals(order.getTransformChannelId())){
    			orders.add(order);
    		}
    	}
    	return orders;
    }

    /**
     * 地区查询
     * @param request
     * @param cnlChannel
     * @return
     */
    @Override
    public List<CnlChannel> queryArea(IRequest request, CnlChannel cnlChannel) {
        return cnlChannelMapper.queryArea(cnlChannel);
    }

    /**
     * 渠道头行提交
     * @param request
     * @param cnlChannels
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseData channelBatchUpdate(IRequest request, List<CnlChannel> cnlChannels) {
        ResponseData response;
        for (CnlChannel cnlChannel : cnlChannels) {
            if (cnlChannel.getChannelCode() == null) {
                cnlChannel.setChannelCode(sequenceService.getSequence("CHANNEL"));
            }
        }
        for (CnlChannel cnlChannel : cnlChannels) {

        	if(cnlChannel.getChannelId()==null){
        		insertSelective(request, cnlChannel);

				//团队创建成员&微信我的二维码注册
				if (cnlChannel.getParentChannelId() != null) {
					if ("WEIXIN".equals(cnlChannel.getChannelSource())) {//微信我的二维码注册
						CmnChannelCommission _cmnChannelCommission = new CmnChannelCommission();
						_cmnChannelCommission.setChannelId(cnlChannel.getParentChannelId());
						_cmnChannelCommission.setEffectiveDate(new Date());
						List<CmnChannelCommission> _cmnChannelCommissionList = cmnChannelCommissionMapper.selectChannelRouter(_cmnChannelCommission);
						//确定最短交易路线
						String _cnl = null;
						Long _parentCnlId = cnlChannel.getParentChannelId();
						if (_cmnChannelCommissionList != null) {
							for (CmnChannelCommission _router : _cmnChannelCommissionList) {
								if (_cnl != null) {
									if (_router.getDealPath().split("C").length < _cnl.split("C").length) {
										_cnl = _router.getDealPath();
									}
								} else {
									_cnl = _router.getDealPath();
								}
							}
						}
						/**
						 * 如果用户是一级渠道，新用户的合约将被挂在这个一级渠道，级别为‘自定义’，佣金为0；
						 * 如果用户不是一级渠道，新用户的合约将被挂在推广用户所属一级渠道下面
						 * （如用户向上追溯，有多个合约，则取最短路径），级别为‘自定义’，佣金为0。
						 * 并将该推广用户作为新用户在这个合约下的介绍人（即合约利益分配中，默认其为介绍人）。
						 **/
						if (_cnl != null) {
							String[] _router = _cnl.split("C");
							_parentCnlId = Long.parseLong(_router[_router.length-1].split(".S")[0]);
						}
						CnlChannelContract cnlChannelContract = new CnlChannelContract();
						cnlChannelContract.setChannelId(cnlChannel.getChannelId());
						try {
							cnlChannelContract.setContractCode(AESUtil.decrypt("CLB", cnlChannel.getContactPhone()));
						}  catch (Exception e) {
							throw new RuntimeException("电话号码解密出错!");
						}
						cnlChannelContract.setContractTypeCode("REFERRAL");
						cnlChannelContract.setPartyId(_parentCnlId);
						cnlChannelContract.setPartyType("CHANNEL");
						cnlChannelContract.setProductDivision("BX");
						cnlChannelContract.setSettleFlag("N");
						cnlChannelContract.setContractApproach("GENERAL");
						cnlChannelContract.setStartDate(cnlChannel.getContractStartDate());
						cnlChannelContract.setEndDate(DateUtil.dayEnd(cnlChannel.getContractEndDate()));
						cnlChannelContract.setDefineRateFlag(cnlChannel.getDefineRateFlag());
						//合约
						if (cnlChannel.getDefineRateFlag().equals("Y")) {
							cnlChannelContractService.insertSelective(request, cnlChannelContract);
							//创建费率
							CnlContractRate cnlContractRate = new CnlContractRate();
							cnlContractRate.setChannelContractId(cnlChannelContract.getChannelContractId());
							cnlContractRate.setRate1(new BigDecimal(0.00));
							cnlContractRateService.insertSelective(request, cnlContractRate);
							//费率历史记录
							CnlContractRateHistory _cnlContractRateHistory = new CnlContractRateHistory();
							_cnlContractRateHistory.setChannelContractId(cnlChannelContract.getChannelContractId());
							_cnlContractRateHistory.setRateLevelName("自定义级别");
							cnlContractRateHistoryService.insertSelective(request, _cnlContractRateHistory);
							//利益分配
							if (_parentCnlId != cnlChannel.getParentChannelId()) {
								String defaultBenefit = profileService.getProfileValue(request, "Invite_Register");
								if (StringUtils.isEmpty(defaultBenefit)) {
									throw new RuntimeException("系统配置的邀请注册默认介绍费率不存在,请重新配置正确的默认介绍费率!");
								}
								CnlContractRole _cnlContractRole = new CnlContractRole();
								_cnlContractRole.setChannelContractId(cnlChannelContract.getChannelContractId());
								_cnlContractRole.setRole("INTRODUCER");
								_cnlContractRole.setRoleUserId(cnlChannel.getParentUserId());
								_cnlContractRole.setBenefit(defaultBenefit);
								cnlContractRoleMapper.insertSelective(_cnlContractRole);
							}
						} else {
							throw new RuntimeException("请设置自定义费率级别为‘Y’!");
						}
					} else {//团队创建成员
						CnlChannelContract cnlChannelContract = new CnlChannelContract();
						cnlChannelContract.setChannelId(cnlChannel.getChannelId());
						cnlChannelContract.setContractCode(cnlChannel.getChannelCode() + "01");
						cnlChannelContract.setContractTypeCode("SERVICE");
						cnlChannelContract.setPartyId(cnlChannel.getParentChannelId());
						cnlChannelContract.setPartyType("CHANNEL");
						cnlChannelContract.setProductDivision("BX");
						cnlChannelContract.setSettleFlag("N");
						cnlChannelContract.setContractApproach("GENERAL");
						cnlChannelContract.setStartDate(cnlChannel.getContractStartDate());
						cnlChannelContract.setEndDate(DateUtil.dayEnd(cnlChannel.getContractEndDate()));
						cnlChannelContract.setDefineRateFlag(cnlChannel.getDefineRateFlag());


						if (cnlChannel.getDefineRateFlag().equals("Y")) {
							if (cnlChannel.getCnlChannelContractRate() != null && cnlChannel.getCnlChannelContractRate().size() > 0) {
								cnlChannelContractService.insertSelective(request, cnlChannelContract);
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
											_cnlContractRate.setSublineId(cnlContractRate.getSublineId());
											List<CnlContractRate> _cnlContractRateList = cnlContractRateMapper.selectRateByNull(_cnlContractRate);
											if (_cnlContractRateList != null && _cnlContractRateList.size() > 1) {
												response = new ResponseData(false);
												response.setMessage("不能设置相同的自定义级别！");
												return response;
											}
											cnlContractRateService.insertSelective(request, cnlContractRate);
										} catch(Exception e) {
											response = new ResponseData(false);
											response.setMessage(e.getMessage());
											return response;
										}
									} else {
										response = new ResponseData(false);
										response.setMessage("第一年费率不能为空！");
										return response;
									}
								}
								//费率历史记录
								CnlContractRateHistory _cnlContractRateHistory = new CnlContractRateHistory();
								_cnlContractRateHistory.setChannelContractId(cnlChannelContract.getChannelContractId());
								_cnlContractRateHistory.setRateLevelName("自定义级别");
								cnlContractRateHistoryService.insertSelective(request, _cnlContractRateHistory);
							} else {
								response = new ResponseData(false);
								response.setMessage("请维护至少一条佣金成分信息！");
								return response;
							}
						} else {
							if (cnlChannel.getRateLevelId() != null) {
								cnlChannelContract.setRateLevelId(cnlChannel.getRateLevelId());
								cnlChannelContract.setRateLevelName(cnlChannel.getRateLevelName());
								cnlChannelContractService.insertSelective(request, cnlChannelContract);
								//费率历史记录
								CnlContractRateHistory _cnlContractRateHistory = new CnlContractRateHistory();
								_cnlContractRateHistory.setChannelContractId(cnlChannelContract.getChannelContractId());
								_cnlContractRateHistory.setRateLevelName(cnlChannelContract.getRateLevelName());
								cnlContractRateHistoryService.insertSelective(request, _cnlContractRateHistory);
							} else {
								response = new ResponseData(false);
								response.setMessage("请选择渠道等级！");
								return response;
							}
						}
					}
				}
        		//在线注册：PC端注册/APP端注册/微信端注册
        		else if (("ONLINE".equals(cnlChannel.getChannelSource()) ||
							"APP".equals(cnlChannel.getChannelSource()) ||
							"WEIXIN".equals(cnlChannel.getChannelSource()))
						&& cnlChannel.getParentSupplierId() != null) {

					CnlChannelContract cnlChannelContract = new CnlChannelContract();
					cnlChannelContract.setChannelId(cnlChannel.getChannelId());
					cnlChannelContract.setContractCode(cnlChannel.getChannelCode() + "01");
					cnlChannelContract.setContractTypeCode("SERVICE");
					cnlChannelContract.setProductDivision("BX");
					cnlChannelContract.setPartyId(cnlChannel.getParentSupplierId());
					cnlChannelContract.setPartyType("SUPPLIER");
					cnlChannelContract.setChannelTypeCode("CG");
					cnlChannelContract.setSettleFlag("Y");
					cnlChannelContract.setContractApproach("GENERAL");
					cnlChannelContract.setStartDate(DateUtil.dayStart(cnlChannel.getContractStartDate()));
					cnlChannelContract.setEndDate(DateUtil.dayEnd(cnlChannel.getContractEndDate()));

					//签单节点
					//从配置文件读取保单资料审核人
					ClbUser clbUser = new ClbUser();
					clbUser.setUserType("OPERATOR");
					clbUser.setRoleName("分管业务行政");
					//获得资料审核人的姓名
					String name = profileService.getProfileValue(request, "BUSINESS_STAFF_USER_NAME");
					clbUser.setUserName(name);
					//查出看是否有该用户
					ClbUser user = clbUserService.selectBusinessStaff(clbUser);
					if (null == user) {
						throw new RuntimeException("系统配置的保单资料审核人不存在,请重新配置正确的资料审核人!");
					}
					cnlChannelContract.setBusinessStaff("CLB_SUPPLIER");
					cnlChannelContract.setBusinessStaffUserId(user.getUserId());
					cnlChannelContract.setClbClub("CLB_SUPPLIER");
					cnlChannelContract.setReserveSupplier("CLB_SUPPLIER");
					cnlChannelContract.setSignNotice("CLB_SUPPLIER");
					cnlChannelContract.setFill("CLB_SUPPLIER");
					cnlChannelContract.setContractSign("CLB_SUPPLIER");
					cnlChannelContract.setSiteFollow("CLB_SUPPLIER");
					cnlChannelContract.setPolicyReview("CLB_SUPPLIER");
					cnlChannelContract.setPolicySend("CLB_SUPPLIER");
					cnlChannelContract.setPolicyFollow("CLB_SUPPLIER");
                    cnlChannelContract.setRateLevelName("默认");

					cnlChannelContractService.insertSelective(request, cnlChannelContract);

					/*CnlContractRate cnlContractRate = new CnlContractRate();
					cnlContractRate.setChannelContractId(cnlChannelContract.getChannelContractId());
					cnlContractRate.setLevelName("默认");
					cnlContractRateService.insertSelective(request, cnlContractRate);*/
				}
        	}else{
        		updateByPrimaryKeySelective(request, cnlChannel);
        	}
        }

        for(CnlChannel k:cnlChannels){

            List<CnlChannelContact> cnlChannelContacts = k.getCnlChannelContact();
            if(cnlChannelContacts != null && cnlChannelContacts.size() != 0){
                for(CnlChannelContact cnlChannelContact:cnlChannelContacts){
                    if (cnlChannelContact.getChannelId() == null) {
                        cnlChannelContact.setChannelId(k.getChannelId());
                        cnlChannelContact.set__status("add");
                    } else {
                        cnlChannelContact.set__status("update");
                    }
                }
                cnlChannelContactService.batchUpdate(request, cnlChannelContacts);
            }

            List<CnlChannelArchive> cnlChannelArchives = k.getCnlChannelArchive();
            if(cnlChannelArchives != null && cnlChannelArchives.size() != 0){
                for(CnlChannelArchive cnlChannelArchive:cnlChannelArchives){
                    if(cnlChannelArchive.getOldFileId() != null){
                        Long fileId = cnlChannelArchive.getOldFileId();
                        SysFile file = sysFileService.selectByPrimaryKey(request,fileId);
                        if (file != null) {
                            sysFileService.delete(request,file);
                        }
                    }
                    if (cnlChannelArchive.getChannelId() == null) {
                        cnlChannelArchive.setChannelId(k.getChannelId());
                        cnlChannelArchive.set__status("add");
                    } else {
                        cnlChannelArchive.set__status("update");
                    }
                }
                cnlChannelArchiveService.batchUpdate(request, cnlChannelArchives);
            }

            List<CnlChannelContract> cnlChannelContracts = k.getCnlChannelContract();
            if(cnlChannelContracts != null && cnlChannelContracts.size() != 0){
                for(CnlChannelContract cnlChannelContract:cnlChannelContracts){
                    if (cnlChannelContract.getChannelId() == null) {
                        cnlChannelContract.setChannelId(k.getChannelId());
                        cnlChannelContract.set__status("add");
                    } else {
                        cnlChannelContract.set__status("update");
                    }
                    cnlChannelContract.setEndDate(DateUtil.dayEnd(cnlChannelContract.getEndDate()));
                }
                cnlChannelContractService.batchUpdate(request, cnlChannelContracts);
            }
        }
        return new ResponseData(cnlChannels);
    }

    @Override
    public List<CnlChannel> queryChannelSimple(IRequest request, CnlChannel dto) {
        return cnlChannelMapper.queryChannelSimple(dto);
    }
    /**
     * 查询渠道名称   by 订单上一级渠道与所属供应商合约id
     * @param request
     * @param contractId
     * @return
     */
    @Override
    public String queryChannelNameByContractId(IRequest request, Long contractId) {
    	return cnlChannelMapper.queryChannelNameByContractId(contractId);
    }

	@Override
	public List<CnlChannel> queryAllCnlWithFilePath() {
		return cnlChannelMapper.queryAllCnlWithFilePath();
	}
	@Override
	public List<CnlChannel> queryChannelByChannelName(CnlChannel cnlChannel) {
		return cnlChannelMapper.queryChannelByChannelName(cnlChannel);
	}
}