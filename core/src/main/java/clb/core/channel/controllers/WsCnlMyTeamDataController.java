package clb.core.channel.controllers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import clb.core.channel.service.ICnlChannelService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.codahale.metrics.annotation.Timed;
import com.hand.hap.core.IRequest;
import com.hand.hap.intergration.annotation.HapInbound;
import com.hand.hap.system.dto.ResponseData;

import clb.core.channel.dto.CnlChannel;
import clb.core.channel.dto.CnlChannelContract;
import clb.core.channel.dto.CnlChannelManage;
import clb.core.channel.dto.CnlMyTeamData;
import clb.core.channel.dto.CnlMyTeamRank;
import clb.core.channel.mapper.CnlChannelContractMapper;
import clb.core.channel.mapper.CnlChannelMapper;
import clb.core.channel.mapper.CnlMyTeamCommonMapper;
import clb.core.channel.service.ICnlChannelContractService;
import clb.core.channel.service.ICnlContractRateService;
import clb.core.common.exceptions.CommonException;
import clb.core.common.utils.DateUtil;
import clb.core.common.utils.ImportUtil;
import clb.core.common.utils.RefelctUtil;
import clb.core.common.utils.SortUtils;
import clb.core.exchangeRate.dto.FetExchangeRate;
import clb.core.exchangeRate.mapper.FetExchangeRateMapper;
import clb.core.forecast.service.impl.FetSupposeCommonServiceImpl;
import clb.core.order.dto.OrdOrder;
import clb.core.order.dto.OrdStatusHis;
import clb.core.order.mapper.OrdStatusHisMapper;
import clb.core.order.service.IOrdOrderService;
import clb.core.sys.controllers.ClbBaseController;
import clb.core.system.dto.ClbUser;
import clb.core.system.mapper.ClbUserMapper;

@Controller
public class WsCnlMyTeamDataController extends ClbBaseController{
	
	private static Logger log = LoggerFactory.getLogger(WsCnlMyTeamDataController.class);

	@Autowired
	private ClbUserMapper userMapper;

	@Autowired
	private IOrdOrderService orderService;
	
	@Autowired
	private CnlMyTeamCommonMapper teamCommonMapper;
	
	@Autowired
	private CnlChannelContractMapper channelContractMapper;
	
	@Autowired
	private FetExchangeRateMapper rateMapper;

	@Autowired
    private ICnlChannelContractService contractService;

    @Autowired
    private ICnlContractRateService rateService;
    
    @Autowired
    private OrdStatusHisMapper hisMapper;
    
    @Autowired
    private CnlChannelMapper channelMapper;

    @Autowired
	private ICnlChannelService cnlChannelService;
    
    private List<CnlChannel> allChannels;
    
    //保单跟进状态
    private static final String[] INSURANCE_ORDER_FOLLOWING_STATUS = {"NEW","DATA_APPROVING","NEED_REVIEW","DATA_APPROVED",
    "RESERVING","RESERVE_SUCCESS","ARRIVAL","LEAVE"};
    
    //债券订单跟进状态
    private static final String[] BOND_ORDER_FOLLOWING_STATUS = {"DATA_APPROVING","NEED_REVIEW","DATA_APPROVED",
    	    "RESERVING","RESERVE_SUCCESS"};
    
    //投资移民订单跟进状态
    private static final String[] IMMIGRANT_ORDER_FOLLOWING_STATUS = {"NEGOTIATE","RESERVING"};
    
    //取消状态
    private static final String[] CANCELLED_STATUS = {"RESERVE_CANCELLED","CANCELLED","RESERVE_FAIL"};
    
    //保单签单状态
    private static final String[] INSURANCE_ORDER_SIGN_STATUS = {"SIGNED","APPROVING","PENDING","POLICY_EFFECTIVE",
    "SURRENDERING","SURRENDERED","DECLINED","SUSPENDED","EXPIRED"};
    
    //债券订单签单状态
    private static final String[] BOND_ORDER_SIGN_STATUS = {"WAITING_ISSUE","ISSUE_SUCCESS"};
    
    //投资移民订单签单状态
    private static final String[] IMMIGRANT_ORDER_SIGN_STATUS = {"BUY_SUCCESS"};
    
    //所有汇率数据
    private List<FetExchangeRate> allExchangeRateData;
    
    //汇率取值数组
    private static final String[] EXCHANGE_DATA= {"foreignCurrency","baseCurrency"};

    /**
     * 团队列表 管理
     * @param dto
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsCnlChannelManage")
    @RequestMapping(value = "/api/channel/channelManage", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData channelManage(@RequestBody CnlChannelManage dto, HttpServletRequest request) {

        ResponseData response=new ResponseData(true);
        IRequest requestContext = createRequestContext(request);

        // 用户状态处理
        ClbUser user = new ClbUser();
        user.setUserId(dto.getUserId());
        user.setStatus(dto.getUserStatusCode());
        userMapper.updateStatus(user);

        // 查询渠道当前时间的合约信息
        CnlChannelContract contract = new CnlChannelContract();
        contract.setPartyId(dto.getPartyId());
        contract.setChannelId(dto.getChannelId());
        contract.setPartyType("CHANNEL");
        contract.setEffectiveDate(new Date());
        List<CnlChannelContract> contractList = contractService.selectByCondition(contract);
        if (contractList != null && contractList.size() > 0) {
            contract = contractList.get(0);
        }

        // 处理佣金分成信息
        rateService.submitWsCommissionProportion(requestContext, contract, dto.getRateList());

        return response;
    }

	/**
	 * 微信-新增预约
	 *
	 * @param cnlMyTeamData 必要参数: {"渠道id" : channelId,
	 *                         			  "日期类型" : dateType: {"week", "month", "year"},
	 *                         			  "订单类型" : orderType : {"INSURANCE", "BOND", "IMMIGRANT"}
	 *                      			  "页码" : page
	 *                      			  "每页数据量" : pageSize(传0可以获取所有数据)}
	 * @param request
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsCnlChannelMyTeamWeChatNewOrder")
	@RequestMapping(value = "/api/channel/wcNewOrder", method = RequestMethod.POST)
	@ResponseBody
	public ResponseData wcNewOrder(@RequestBody CnlMyTeamData cnlMyTeamData, HttpServletRequest request) {
		ResponseData responseData = new ResponseData();
		IRequest requestContext = createRequestContext(request);

		OrdOrder paramOrder = new OrdOrder();
		if (cnlMyTeamData.getChannelId() != null) {
			paramOrder.setChannelId(cnlMyTeamData.getChannelId());
		}
		if (cnlMyTeamData.getDateType() != null) {
			paramOrder.setDateType(cnlMyTeamData.getDateType());
		}
		if (cnlMyTeamData.getOrderType() != null) {
			paramOrder.setOrderType(cnlMyTeamData.getOrderType());
		}
		List<OrdOrder> allOrderDatas = orderService.queryWsTeamOrder(requestContext,paramOrder, cnlMyTeamData.getPage(), cnlMyTeamData.getPageSize());
		try {
			List<OrdOrder> newOrders = getDataByDateType(allOrderDatas,"submitDate", cnlMyTeamData.getDateType());
			//筛选状态不是已取消的订单
			Iterator<OrdOrder> it = newOrders.iterator();
			while(it.hasNext()){
				OrdOrder element = it.next();
				if(element.getStatus() != null){
					if(searchString(CANCELLED_STATUS,element.getStatus())>= 0){
						it.remove();
					}
				}
			}
			responseData.setRows(newOrders);
		} catch (CommonException e) {
			e.printStackTrace();
			responseData.setSuccess(false);
			responseData.setMessage(e.getDescriptionKey());
		}
		return responseData;
	}

	/**
	 * 我的团队-新增预约
	 *
	 * @param dateType {"本周" : week, "本月" : month, "本年" : year}
	 * @param request
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsCnlChannelMyTeamNewOrder")
	@RequestMapping(value = "/api/channel/newOrder", method = RequestMethod.POST)
	@ResponseBody
	public ResponseData getNewOrder(@RequestParam String dateType, HttpServletRequest request) {

		IRequest requestContext = createRequestContext(request);

		OrdOrder paramOrder = new OrdOrder();

		List<OrdOrder> allOrderDatas = orderService.queryWsTeamOrder(requestContext,paramOrder, 0, 0);

		//获取我的保单-团队tab页中的订单
		paramOrder.setOrderType("INSURANCE");
		List<OrdOrder> insuranceOrders = RefelctUtil.getListDataByFields(allOrderDatas,paramOrder,"orderType");

		//获取移民投资-团队tab页中的订单
		paramOrder.setOrderType("BOND");
		List<OrdOrder> bondOrders = RefelctUtil.getListDataByFields(allOrderDatas,paramOrder,"orderType");

		//获取债券-团队tab页中的订单
		paramOrder.setOrderType("IMMIGRANT");
		List<OrdOrder> immigrantOrders = RefelctUtil.getListDataByFields(allOrderDatas,paramOrder,"orderType");

		List<OrdOrder> allOrders = new ArrayList<>();
		allOrders.addAll(insuranceOrders);
		allOrders.addAll(bondOrders);
		allOrders.addAll(immigrantOrders);
		ResponseData responseData = new ResponseData();
		List<OrdOrder> newOrders;
		try {
			newOrders = getDataByDateType(allOrders,"submitDate", dateType);
			//筛选状态不是已取消的订单
			Iterator<OrdOrder> it = newOrders.iterator();
			while(it.hasNext()){
				OrdOrder element = it.next();
				if(element.getStatus() != null){
					if(searchString(CANCELLED_STATUS,element.getStatus())>= 0){
						it.remove();
					}
				}
			}
		} catch (CommonException e) {
			e.printStackTrace();
			responseData.setSuccess(false);
			responseData.setMessage(e.getDescriptionKey());
			return responseData;
		}
		return new ResponseData(newOrders);
	}

	/**
	 * 微信-签单数量
	 *
	 * @param cnlMyTeamData 必要参数: {"渠道id" : channelId,
	 *                         			  "日期类型" : dateType: {"week", "month", "year"},
	 *                         			  "订单类型" : orderType : {"INSURANCE", "BOND", "IMMIGRANT"}
	 *                      			  "页码" : page
	 *                      			  "每页数据量" : pageSize(传0可以获取所有数据)}
	 * @param request
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsCnlChannelMyTeamWeChatSignOrder")
	@RequestMapping(value = "/api/channel/wcSignOrder", method = RequestMethod.POST)
	@ResponseBody
	public ResponseData wcSignOrder (@RequestBody CnlMyTeamData cnlMyTeamData, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		// 签单订单总和
		List<OrdOrder> allSignOrders = new ArrayList<>();

		OrdOrder paramOrder = new OrdOrder();
		if (cnlMyTeamData.getChannelId() != null) {
			paramOrder.setChannelId(cnlMyTeamData.getChannelId());
		}
		if (cnlMyTeamData.getDateType() != null) {
			paramOrder.setDateType(cnlMyTeamData.getDateType());
		}
		if (cnlMyTeamData.getOrderType() != null) {
			paramOrder.setOrderType(cnlMyTeamData.getOrderType());
		}
		List<OrdOrder> allOrderDatas = orderService.queryWsTeamOrder(requestContext,paramOrder, cnlMyTeamData.getPage(), cnlMyTeamData.getPageSize());

		OrdStatusHis statusHisParam = new OrdStatusHis();
		String status = null;
		String orderType = cnlMyTeamData.getOrderType();
		String[] statusArray = {};
		if (orderType != null) {
			if ("INSURANCE".equals(orderType)) {
				status = "SIGNED";
				statusArray = INSURANCE_ORDER_SIGN_STATUS;
			} else if ("BOND".equals(orderType)) {
				status = "WAITING_ISSUE";
				statusArray = BOND_ORDER_SIGN_STATUS;
			} else if ("IMMIGRANT".equals(orderType)) {
				status = "BUY_SUCCESS";
				statusArray = IMMIGRANT_ORDER_SIGN_STATUS;
			}
		}
		statusHisParam.setStatus(status);
		List<OrdStatusHis> orderHisSimples  = hisMapper.queryOrdStatusHisSimple(statusHisParam);

		List<String> rules = new ArrayList<>();
		rules.add("statusDate");

		OrdStatusHis statusHis;
		boolean b = true;
		for (OrdOrder order : allOrderDatas) {
			if ("INSURANCE".equals(orderType)) {
				b = searchString(statusArray, order.getStatus()) >= 0 || "Pending".equals(order.getStatusDesc());
			} else {
				b = searchString(statusArray,order.getStatus())>= 0;
			}
			//筛选状态
			if(order.getStatus() != null){
				//状态在状态数组中，或状态描述为Pedding
				if(b){
					if ("INSURANCE".equals(orderType)) {
						if("Pending".equals(order.getStatusDesc()))order.setStatus("PENDING");
					}

					//查询状态跟进,订单状态是已签单的数据
					statusHis = new OrdStatusHis();
					statusHis.setOrderId(order.getOrderId());
					List<OrdStatusHis> hisData = RefelctUtil.getListDataByFields(orderHisSimples,statusHis,"orderId");
					Date date;
					if(CollectionUtils.isNotEmpty(hisData)){
						//有多条数据，按时间倒序排序后取第一条
						Collections.sort(hisData,SortUtils.createComparator(0,rules));
						date = hisData.get(0).getStatusDate();
					}else{
						//否则，取预约签单时间
						date = order.getReserveDate();
					}
					order.setChannelSignDate(date);
					//满足状态条件，不满足时间条件
					if(contoinsDate(date,cnlMyTeamData.getDateType())){
						allSignOrders.add(order);
					}
				}
			}
		}
		return new ResponseData(allSignOrders);
	}

	/**
	 * 我的团队-签单数量
	 *
	 * @param dateType {"本周" : week, "本月" : month, "本年" : year}
	 * @param request
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsCnlChannelMyTeamSignOrder")
	@RequestMapping(value = "/api/channel/signOrder", method = RequestMethod.POST)
	@ResponseBody
	public ResponseData getSignOrder (@RequestParam String dateType, HttpServletRequest request) {
    	IRequest requestContext = createRequestContext(request);
		//签单保单
		List<OrdOrder> signInsuranceOrders = new ArrayList<>();
		//签单债券
		List<OrdOrder> signBondOrders = new ArrayList<>();
		//签单移民投资
		List<OrdOrder> signImmigrationOrders = new ArrayList<>();
		//三种签单订单总和
		List<OrdOrder> allSignOrders = new ArrayList<>();

		OrdOrder paramOrder = new OrdOrder();

		List<OrdOrder> allOrderDatas = orderService.queryWsTeamOrder(requestContext,paramOrder, 0, 0);


		//获取我的保单-团队tab页中的订单
		paramOrder.setOrderType("INSURANCE");
		List<OrdOrder> insuranceOrders = RefelctUtil.getListDataByFields(allOrderDatas,paramOrder,"orderType");

		//获取移民投资-团队tab页中的订单
		paramOrder.setOrderType("BOND");
		List<OrdOrder> bondOrders = RefelctUtil.getListDataByFields(allOrderDatas,paramOrder,"orderType");

		//获取债券-团队tab页中的订单
		paramOrder.setOrderType("IMMIGRANT");
		List<OrdOrder> immigrantOrders = RefelctUtil.getListDataByFields(allOrderDatas,paramOrder,"orderType");

		OrdStatusHis statusHisParam = new OrdStatusHis();

		statusHisParam.setStatus("SIGNED");
		List<OrdStatusHis> allSignHisData = hisMapper.queryOrdStatusHisSimple(statusHisParam);

		statusHisParam.setStatus("WAITING_ISSUE");
		List<OrdStatusHis> allWaitingIssueHisData = hisMapper.queryOrdStatusHisSimple(statusHisParam);

		statusHisParam.setStatus("BUY_SUCCESS");
		List<OrdStatusHis> allBuySuccessHisData = hisMapper.queryOrdStatusHisSimple(statusHisParam);

		List<String> rules = new ArrayList<>();
		rules.add("statusDate");

		OrdStatusHis statusHis;

		//计算签单保单
		for(OrdOrder order:insuranceOrders){
			//筛选状态
			if(order.getStatus() != null){
				//状态在状态数组中，或状态描述为Pedding
				if(searchString(INSURANCE_ORDER_SIGN_STATUS,order.getStatus())>= 0 || "Pending".equals(order.getStatusDesc())){
					if("Pending".equals(order.getStatusDesc()))order.setStatus("PENDING");

					//查询状态跟进,订单状态是已签单的数据
					statusHis = new OrdStatusHis();
					statusHis.setOrderId(order.getOrderId());
					List<OrdStatusHis> hisData = (List<OrdStatusHis>)RefelctUtil.getListDataByFields(allSignHisData,statusHis,"orderId");
					Date date = new Date();
					if(CollectionUtils.isNotEmpty(hisData)){
						//有多条数据，按时间倒序排序后取第一条
						Collections.sort(hisData,SortUtils.createComparator(0,rules));
						date = hisData.get(0).getStatusDate();
					}else{
						//否则，取预约签单时间
						date = order.getReserveDate();
					}
					order.setChannelSignDate(date);
					//满足状态条件，不满足时间条件
					if(contoinsDate(date,dateType)){
						signInsuranceOrders.add(order);
					}

				}
			}
		}
		allSignOrders.addAll(signInsuranceOrders);


		//计算签单债券
		for(OrdOrder order:bondOrders){
			//筛选状态
			if(order.getStatus() != null){
				//状态在状态数组中
				if(searchString(BOND_ORDER_SIGN_STATUS,order.getStatus())>= 0){
					//查询状态跟进,订单状态是等待发行债券的数据
					statusHis = new OrdStatusHis();
					statusHis.setOrderId(order.getOrderId());
					List<OrdStatusHis> hisData = (List<OrdStatusHis>)RefelctUtil.getListDataByFields(allWaitingIssueHisData,statusHis,"orderId");
					Date date = new Date();
					if(CollectionUtils.isNotEmpty(hisData)){
						//有多条数据，按时间倒序排序后取第一条
						Collections.sort(hisData,SortUtils.createComparator(0,rules));
						date = hisData.get(0).getStatusDate();
					}else{
						//否则，取预约签单时间
						date = order.getReserveDate();
					}
					order.setChannelSignDate(date);
					//满足状态条件，不满足时间条件
					if(contoinsDate(date,dateType)){
						signBondOrders.add(order);
					}

				}
			}
		}
		allSignOrders.addAll(signBondOrders);

		//计算签单移民投资
		for(OrdOrder order:immigrantOrders){
			//筛选状态
			if(order.getStatus() != null){
				//状态在状态数组中
				if(searchString(IMMIGRANT_ORDER_SIGN_STATUS,order.getStatus())>= 0){
					//查询状态跟进,订单状态是购买成功的数据
					statusHis = new OrdStatusHis();
					statusHis.setOrderId(order.getOrderId());
					List<OrdStatusHis> hisData = (List<OrdStatusHis>)RefelctUtil.getListDataByFields(allBuySuccessHisData,statusHis,"orderId");
					Date date = new Date();
					if(CollectionUtils.isNotEmpty(hisData)){
						//有多条数据，按时间倒序排序后取第一条
						Collections.sort(hisData,SortUtils.createComparator(0,rules));
						date = hisData.get(0).getStatusDate();
					}else{
						//否则，取预约签单时间
						date = order.getIssueDate();
					}
					order.setChannelSignDate(date);
					//满足状态条件，不满足时间条件
					if(contoinsDate(date,dateType)){
						signImmigrationOrders.add(order);
					}

				}
			}
		}
		allSignOrders.addAll(signImmigrationOrders);

		return new ResponseData(allSignOrders);
	}

    /**
     * 我的团队 数据
     * @param dto
     * @param request
     * @return
     * @throws CommonException 
     */
    @Timed
    @HapInbound(apiName = "ClbWsCnlChannelMyTeamData")
    @RequestMapping(value = "/api/channel/myTeamData", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData query(@RequestBody CnlMyTeamData dto, HttpServletRequest request){
    	
		//查询所有汇率数据
    	FetExchangeRate exchangeRate = new FetExchangeRate();
		exchangeRate.setCurrentTime(new Date());
		allExchangeRateData = rateMapper.selectRateByCurrentTime(exchangeRate);
		
		//查询所有渠道数据
		allChannels = cnlChannelService.queryAllCnlWithFilePath();
        IRequest requestContext = createRequestContext(request);

        List<CnlMyTeamData> myTeamList = new ArrayList<CnlMyTeamData>();
        CnlMyTeamData myTeamData = new CnlMyTeamData();
        ResponseData responseData = new ResponseData();
        
        ClbUser clbUser = new ClbUser();
        clbUser.setUserId(requestContext.getUserId());
        clbUser = userMapper.selectOne(clbUser);
        
        
        
        //获取成员数据
        List<CnlChannelContract>  members = new ArrayList<>();
        
        CnlChannelContract channelContract = new CnlChannelContract();
    	channelContract.setPartyType(clbUser.getUserType());
    	channelContract.setPartyId(clbUser.getRelatedPartyId());
    	members= teamCommonMapper.queryMember(channelContract);
    	if(CollectionUtils.isNotEmpty(members)){
    		//设置成员数量
    		myTeamData.setMemberQty(Long.valueOf(members.size()));
    	}
    	
    	
    	
    	OrdOrder paramOrder = new OrdOrder();
    	
    	List<OrdOrder> allOrderDatas = orderService.queryWsTeamOrder(requestContext,paramOrder, 0, 0);
    	
        //获取我的保单-团队tab页中的订单
    	paramOrder.setOrderType("INSURANCE");
    	List<OrdOrder> insuranceOrders = RefelctUtil.getListDataByFields(allOrderDatas,paramOrder,"orderType");
        
        //获取移民投资-团队tab页中的订单
    	paramOrder.setOrderType("BOND");
    	List<OrdOrder> bondOrders = RefelctUtil.getListDataByFields(allOrderDatas,paramOrder,"orderType");
        
        //获取债券-团队tab页中的订单
    	paramOrder.setOrderType("IMMIGRANT");
        List<OrdOrder> immigrantOrders = RefelctUtil.getListDataByFields(allOrderDatas,paramOrder,"orderType");
       
        //我的团队-保险
    	List<OrdOrder> insurances = new ArrayList<>();
    	//我的团队-债券
    	List<OrdOrder> bonds = new ArrayList<>();
    	//我的团队-移民投资
    	List<OrdOrder> immigrants = new ArrayList<>();
    	//我的团队-pending
    	List<OrdOrder> pendings = new ArrayList<>();
    	
    	//新增预约订单数量
    	Long newOrderQty = 0L;
    	//签单数量
		Long signData = 0L;
		//签单总额
		BigDecimal totalSignAmount = BigDecimal.ZERO;
		//签单保单
		List<OrdOrder> signInsuranceOrders = new ArrayList<>();
		//签单债券
		List<OrdOrder> signBondOrders = new ArrayList<>();
		//签单移民投资
		List<OrdOrder> signImmigrationOrders = new ArrayList<>();
		//三种签单订单总和
		List<OrdOrder> allSignOrders = new ArrayList<>();
		
		//三种符合签单条件但不符合时间条件的订单总和，用于获取环比数据
		List<OrdOrder> allSignNotTimeOrders = new ArrayList<>();

    
    	//我的团队-保险订单
    	if(CollectionUtils.isNotEmpty(insuranceOrders)){
    		for(OrdOrder order:insuranceOrders){
    			//log.debug("保险订单，状态为："+order.getStatus());
    			//log.debug("保险订单，状态描述为："+order.getStatusDesc());
    			if(order.getStatus() != null){
    				if(searchString(INSURANCE_ORDER_FOLLOWING_STATUS,order.getStatus())>= 0){
    					insurances.add(order);
    				}
    				if("Pending".equals(order.getStatusDesc()) || "PENDING".equals(order.getStatus())){
    					pendings.add(order);
    				}
    			}
    		}
    	}
    	
    	//我的团队-债券订单
    	if(CollectionUtils.isNotEmpty(bondOrders)){
    		for(OrdOrder order:bondOrders){
    			//log.debug("债券订单，状态为："+order.getStatus());
    			//log.debug("债券订单，状态描述为："+order.getStatusDesc());
    			if(order.getStatus() != null){
    				if(searchString(BOND_ORDER_FOLLOWING_STATUS,order.getStatus())>= 0){
    					bonds.add(order);
    				}
//    				if("PENDING".equals(order.getStatus()) || "PENDING_APPROVING".equals(order.getStatus()) || "PENDING_HANDLING".equals(order.getStatus())){
//    					pendings.add(order);
//    				}
    			}
    		}
    	}
    	
    	//我的团队-移民投资订单
    	if(CollectionUtils.isNotEmpty(immigrantOrders)){
    		for(OrdOrder order:immigrantOrders){
    			//log.debug("移民投资订单，状态为："+order.getStatus());
    			//log.debug("移民投资订单，状态描述为："+order.getStatusDesc());
    			if(order.getStatus() != null){
    				if(searchString(IMMIGRANT_ORDER_FOLLOWING_STATUS,order.getStatus())>= 0){
    					immigrants.add(order);
    				}
    			}
    		}
    	}
    	
    	//设置保单数量
    	myTeamData.setInsuranceInProcessQty(Long.valueOf(insurances.size()));
    	//设置债券数量
    	myTeamData.setBondInProcessQty(Long.valueOf(bonds.size()));
    	//设置移民投资数量
    	myTeamData.setImmigrationInProcessQty(Long.valueOf(immigrants.size()));
    	//设置总数
    	myTeamData.setTotalInProcessQty(Long.valueOf(insurances.size()+bonds.size()+immigrants.size()));
    	
    	
    	//设置Peding数量
    	if(CollectionUtils.isNotEmpty(pendings)){
    		myTeamData.setPengdingQty(Long.valueOf(pendings.size()));
    	}
    	
    	/*-------------------签单统计-------------------*/

    	//获取所有订单数据
    	List<OrdOrder> allOrders = new ArrayList<>();
    	allOrders.addAll(insuranceOrders);
    	allOrders.addAll(bondOrders);
    	allOrders.addAll(immigrantOrders);
    	List<OrdOrder> newOrders = new ArrayList<>();
    	//根据周，月，年和提交时间筛选数据
    	try{
    		newOrders = getDataByDateType(allOrders,"submitDate",dto.getDateType());
    		//筛选状态不是已取消的订单
    		Iterator<OrdOrder> it = newOrders.iterator();
    		while(it.hasNext()){
    			OrdOrder element = it.next();
    			if(element.getStatus() != null){
    				if(searchString(CANCELLED_STATUS,element.getStatus())>= 0){
    					it.remove();
    				}
    			}
    		}
    	}catch(CommonException e){
    		responseData.setSuccess(false);
    		responseData.setMessage(e.getDescriptionKey());
    		return responseData;
    	}
    	
    	if(CollectionUtils.isNotEmpty(newOrders)){
    		//新增订单数量
    		newOrderQty = Long.valueOf(newOrders.size());
    	}
		/*****计算签单数量*****/
    	
		//状态跟进
		OrdStatusHis statusHis = new OrdStatusHis();
		
		OrdStatusHis statusHisParam = new OrdStatusHis();
		statusHisParam.setStatus("SIGNED");
		List<OrdStatusHis> allSignHisData = hisMapper.queryOrdStatusHisSimple(statusHisParam);
		
		statusHisParam.setStatus("WAITING_ISSUE");
		List<OrdStatusHis> allWaitingIssueHisData = hisMapper.queryOrdStatusHisSimple(statusHisParam);
		
		statusHisParam.setStatus("BUY_SUCCESS");
		List<OrdStatusHis> allBuySuccessHisData = hisMapper.queryOrdStatusHisSimple(statusHisParam);
		List<String> rules = new ArrayList<>();
		rules.add("statusDate");
		//计算签单保单
		for(OrdOrder order:insuranceOrders){
			//筛选状态
			if(order.getStatus() != null){
				//状态在状态数组中，或状态描述为Pedding
				if(searchString(INSURANCE_ORDER_SIGN_STATUS,order.getStatus())>= 0 || "Pending".equals(order.getStatusDesc())){
					if("Pending".equals(order.getStatusDesc()))order.setStatus("PENDING");
					
					//查询状态跟进,订单状态是已签单的数据
					statusHis = new OrdStatusHis();
					statusHis.setOrderId(order.getOrderId());
					List<OrdStatusHis> hisData = (List<OrdStatusHis>)RefelctUtil.getListDataByFields(allSignHisData,statusHis,"orderId");
					Date date = new Date();
					if(CollectionUtils.isNotEmpty(hisData)){
						//有多条数据，按时间倒序排序后取第一条
						Collections.sort(hisData,SortUtils.createComparator(0,rules));
						date = hisData.get(0).getStatusDate();
					}else{
						//否则，取预约签单时间
						date = order.getReserveDate();
					}
					order.setChannelSignDate(date);
					//满足状态条件，不满足时间条件
					allSignNotTimeOrders.add(order);
					if(contoinsDate(date,dto.getDateType())){
						signInsuranceOrders.add(order);
					}
					
				}
			}
		}
		allSignOrders.addAll(signInsuranceOrders);
		
		//计算签单债券
		for(OrdOrder order:bondOrders){
			//筛选状态
			if(order.getStatus() != null){
				//状态在状态数组中
				if(searchString(BOND_ORDER_SIGN_STATUS,order.getStatus())>= 0){
					//查询状态跟进,订单状态是等待发行债券的数据
					statusHis = new OrdStatusHis();
					statusHis.setOrderId(order.getOrderId());
					List<OrdStatusHis> hisData = (List<OrdStatusHis>)RefelctUtil.getListDataByFields(allWaitingIssueHisData,statusHis,"orderId");
					Date date = new Date();
					if(CollectionUtils.isNotEmpty(hisData)){
						//有多条数据，按时间倒序排序后取第一条
						Collections.sort(hisData,SortUtils.createComparator(0,rules));
						date = hisData.get(0).getStatusDate();
					}else{
						//否则，取预约签单时间
						date = order.getReserveDate();
					}
					order.setChannelSignDate(date);
					//满足状态条件，不满足时间条件
					allSignNotTimeOrders.add(order);
					if(contoinsDate(date,dto.getDateType())){
						signBondOrders.add(order);
					}
					
				}
			}
		}
		allSignOrders.addAll(signBondOrders);
		
		//计算签单移民投资
		for(OrdOrder order:immigrantOrders){
			//筛选状态
			if(order.getStatus() != null){
				//状态在状态数组中
				if(searchString(IMMIGRANT_ORDER_SIGN_STATUS,order.getStatus())>= 0){
					//查询状态跟进,订单状态是购买成功的数据
					statusHis = new OrdStatusHis();
					statusHis.setOrderId(order.getOrderId());
					List<OrdStatusHis> hisData = (List<OrdStatusHis>)RefelctUtil.getListDataByFields(allBuySuccessHisData,statusHis,"orderId");
					Date date = new Date();
					if(CollectionUtils.isNotEmpty(hisData)){
						//有多条数据，按时间倒序排序后取第一条
						Collections.sort(hisData,SortUtils.createComparator(0,rules));
						date = hisData.get(0).getStatusDate();
					}else{
						//否则，取预约签单时间
						date = order.getIssueDate();
					}
					order.setChannelSignDate(date);
					//满足状态条件，不满足时间条件
					allSignNotTimeOrders.add(order);
					if(contoinsDate(date,dto.getDateType())){
						signImmigrationOrders.add(order);
					}
					
				}
			}
		}
		allSignOrders.addAll(signImmigrationOrders);
		
		//计算签单总额
		if(CollectionUtils.isNotEmpty(allSignOrders)){
			//签单数量
			signData = Long.valueOf(allSignOrders.size());
    		for(OrdOrder order:allSignOrders){
    			//计算缴费总额
				BigDecimal exchange = BigDecimal.ONE;
				if(!"HKD".equals(order.getCurrency())){
					FetExchangeRate param = new FetExchangeRate();
					//外币
					param.setForeignCurrency(order.getCurrency());
					//港币
					param.setBaseCurrency("HKD");
					List<FetExchangeRate> exchangeRates = (List<FetExchangeRate>)RefelctUtil.getListDataByFields(allExchangeRateData,param,EXCHANGE_DATA);
					if(CollectionUtils.isEmpty(exchangeRates)){
						responseData.setSuccess(false);
			        	responseData.setMessage(String.format("请维护外币：%s和基准货币:HKD的汇率!",order.getCurrency()));
			        	return responseData;
					}
					exchange = new BigDecimal(exchangeRates.get(0).getRate());
				}
				BigDecimal hkdAmount =  BigDecimal.ZERO;
				if("INSURANCE".equals(order.getOrderType())){
					hkdAmount = order.getYearPayAmount().multiply(exchange);
					order.setSignHkdAmount(hkdAmount);
					totalSignAmount = totalSignAmount.add(hkdAmount);
				}else if("BOND".equals(order.getOrderType()) || "IMMIGRANT".equals(order.getOrderType())){
					hkdAmount = order.getPolicyAmount().multiply(exchange);
					order.setSignHkdAmount(hkdAmount);
					totalSignAmount = totalSignAmount.add(hkdAmount);
				}
    		}
		}
    	
    	//设置新增预约订单数量
		myTeamData.setNewOrderQty(newOrderQty);
		//设置签单订单数量
		myTeamData.setSignQty(signData);
		totalSignAmount = totalSignAmount.setScale(0,BigDecimal.ROUND_HALF_UP);
		//设置签单总额
		myTeamData.setTotalSignAmount(totalSignAmount);
        
        //签单数排行
        List<CnlMyTeamRank> quantityRanks = new ArrayList<CnlMyTeamRank>();
        //签单总额排行
        List<CnlMyTeamRank> amountRanks = new ArrayList<CnlMyTeamRank>();
    	//查询每一个成员对应的签单数量
    	for(CnlChannelContract member:members){
    		OrdOrder order = new OrdOrder();
    		order.setChannelId(member.getChannelId());
    		//根据已签单订单数，获取每个渠道的订单数
    		List<OrdOrder> ordDatas = getOrderDataByChannelId(allSignOrders,member.getChannelId());
    		CnlMyTeamRank quantityRank = null;
    		CnlMyTeamRank amountRank = null; 
    		try{
    			quantityRank = getTeamRank(ordDatas,allSignNotTimeOrders,dto.getDateType(),"quantity",member.getChannelId());
        		amountRank = getTeamRank(ordDatas,allSignNotTimeOrders,dto.getDateType(),"amount",member.getChannelId());
				for (CnlChannel channel : allChannels) {
					if (Objects.equals(channel.getChannelId(), quantityRank.getChannelId())) {
						quantityRank.setPhotoFilePath(channel.getPhotoFilePath());
					}
					if (Objects.equals(channel.getChannelId(), amountRank.getChannelId())) {
						amountRank.setPhotoFilePath(channel.getPhotoFilePath());
					}
				}
			}catch(CommonException e){
    			responseData.setSuccess(false);
        		responseData.setMessage(e.getDescriptionKey());
        		return responseData;
    		}
    		
    		if(quantityRank != null){
    			quantityRanks.add(quantityRank);
    		}
    		if(amountRank != null){
    			amountRanks.add(amountRank);
    		}
    	}
		 
    	//排序规则
    	String orderingRule = dto.getOrderingRule();
    	
    	//排序，默认从高到低
    	Collections.sort(quantityRanks,new SignOrdeComparator());
    	Collections.reverse(quantityRanks);
    	Collections.sort(amountRanks,new SignAmountComparator());
    	Collections.reverse(amountRanks);
        // 排名
        if ("quantity".equals(dto.getRankDataType())) {
        	if("ASC".equals(orderingRule)){
        		Collections.reverse(quantityRanks);
        	}
        }

        if ("amount".equals(dto.getRankDataType())) {
        	if("ASC".equals(orderingRule)){
        		Collections.reverse(amountRanks);
        	}
        }
        
        //返回结果
        List<CnlMyTeamRank> resQuantityRanks = new ArrayList<>();
        List<CnlMyTeamRank> resAmountRanks = new ArrayList<>();
        if(quantityRanks.size() >= 5){
        	for(int i=0;i<5;i++){
        		quantityRanks.get(i).setRankNum(Long.valueOf(i));
        		resQuantityRanks.add(quantityRanks.get(i));
        	}
        }else{
        	for(int i=0;i<quantityRanks.size();i++){
        		quantityRanks.get(i).setRankNum(Long.valueOf(i));
        		resQuantityRanks.add(quantityRanks.get(i));
        	}
        }
    	
    	myTeamData.setQuantityRank(resQuantityRanks);
    	

    	if(amountRanks.size() >= 5){
        	for(int i=0;i<5;i++){
        		amountRanks.get(i).setRankNum(Long.valueOf(i));
        		resAmountRanks.add(amountRanks.get(i));
        	}
        }else{
        	for(int i=0;i<amountRanks.size();i++){
        		amountRanks.get(i).setRankNum(Long.valueOf(i));
        		resAmountRanks.add(amountRanks.get(i));
        	}
        }
    	myTeamData.setAmountRank(resAmountRanks);
        myTeamList.add(myTeamData);
        return new ResponseData(myTeamList);
    }
    
    // 自定义比较器：按签单数量排序  
    static class SignOrdeComparator implements Comparator<CnlMyTeamRank> {  
        public int compare(CnlMyTeamRank p1, CnlMyTeamRank p2) {// 实现接口中的方法  
            return p1.getSignOrderQty().compareTo(p2.getSignOrderQty());  
        }
    }  
  
    // 自定义比较器：按签单金额排序  
    static class SignAmountComparator implements Comparator<CnlMyTeamRank> {  
        public int compare(CnlMyTeamRank p1, CnlMyTeamRank p2) {// 实现接口中的方法  
           return p1.getSignAmount().compareTo(p2.getSignAmount());  
        }  
    }  
    
    /**
     * 根据周，月，年筛选数据 
     * @throws CommonException 
     */
    private List<OrdOrder> getDataByDateType(List<OrdOrder> originData,String fieldName,String type) throws CommonException{
    	List<OrdOrder> res = new ArrayList<>();
    	Method method = ImportUtil.getMethod(OrdOrder.class,fieldName,"get");
    	for(OrdOrder order:originData){
    		Date date = new Date();
    		try {
				date = (Date) method.invoke(order);
				//log.debug("签单时间为："+date);
			} catch (Exception e) {
				throw new CommonException("CNL","获取日期失败!",null);
			}
    		if(date != null){
    			if(contoinsDate(date,type)){
    				res.add(order);
    			}
    		}
    	}
    	return res;
    }
    
    /**
     * 判断时间是否是本周/本月/本年
     */
    private boolean contoinsDate(Date param,String type){
    	Date begin = null;
    	Date end = null;
    	if("week".equals(type)){
    		begin = DateUtil.getCurrentMondayAndSunday().get(0);
    		end = DateUtil.getCurrentMondayAndSunday().get(1);
    	}
    	else if("month".equals(type)){
    		begin = DateUtil.getCurrentMonthBeginAndEnd().get(0);
    		end = DateUtil.getCurrentMonthBeginAndEnd().get(1);
    	}
    	else if("year".equals(type)){
    		begin = DateUtil.getCurrentYearBeginAndEnd().get(0);
    		end = DateUtil.getCurrentYearBeginAndEnd().get(1);
    	}
    	Long beginTime = new Long(begin.getTime());
		Long endTime = new Long(end.getTime());
		Long paramTime = new Long(param.getTime());
		//log.debug("参数时间："+DateUtil.transFormToDateString(paramTime));
		//log.debug("开始时间："+DateUtil.transFormToDateString(beginTime));
		//log.debug("结束时间："+DateUtil.transFormToDateString(endTime));
		if(beginTime.compareTo(paramTime)<=0 && endTime.compareTo(paramTime)>=0){
			return true;
		}
		return false;
    }
    
    /**
     * 获取团队数据
     * @param ordDatas 本月/本周/本年订单数据
     * @param allSignNotTimeOrders 满足签单状态的订单数据，用于计算环比
     * @param dateType 时间类型
     * @param requestType 请求类型
     */
    private CnlMyTeamRank getTeamRank(List<OrdOrder> ordDatas,List<OrdOrder> allSignNotTimeOrders,String dateType,String requestType,Long channelId) throws CommonException{
    	CnlMyTeamRank qtyRank = new CnlMyTeamRank();
    	//渠道Id
		qtyRank.setChannelId(channelId);
		//签单数量
		Long orderNumber = 0L;
		//签单金额
		BigDecimal signAmount = BigDecimal.ZERO;
		//环比
		Double variinePercent = 0D;
		if(CollectionUtils.isNotEmpty(ordDatas)){
			//渠道名称
			qtyRank.setChannelName(ordDatas.get(0).getChannelName());
			for(OrdOrder order:ordDatas){
				signAmount = signAmount.add(order.getSignHkdAmount());
	    	}
			//获取签单数量
			orderNumber = Long.valueOf(ordDatas.size());
			//获取签单金额
			signAmount = signAmount.setScale(0,BigDecimal.ROUND_HALF_UP);
			
			//获取计算环比
			variinePercent = getVariinePercent(allSignNotTimeOrders,dateType,requestType,orderNumber,signAmount,qtyRank);
			//设置环比
			qtyRank.setVariinePercent(variinePercent);
		}else{
			CnlChannel channel = new CnlChannel();
			channel.setChannelId(channelId);
			List<CnlChannel> channels = (List<CnlChannel>)RefelctUtil.getListDataByFields(allChannels,channel,"channelId");
			if(CollectionUtils.isNotEmpty(channels)){
				channel = channels.get(0);//渠道名称
				qtyRank.setChannelName(channel.getChannelName());
			}else{
				qtyRank.setChannelName("");
			}
		}
		//设置签单数量
		qtyRank.setSignOrderQty(orderNumber);
		//设置签单签单金额
		qtyRank.setSignAmount(signAmount);
		
		return qtyRank;
    }
    
    //计算环比
    private Double getVariinePercent(List<OrdOrder> ordDatas,String dateType,String requestType,Long currentOrderNumber,BigDecimal currentSignAmount,CnlMyTeamRank qtyRank) throws CommonException{
    	Double variinePercent = new Double(0);
    	Date begin = null;
    	Date end = null;
    	if("week".equals(dateType)){
    		begin = DateUtil.getLastMondayAndSunday().get(0);
    		end = DateUtil.getLastMondayAndSunday().get(1);
    	}
    	else if("month".equals(dateType)){
    		begin = DateUtil.getLastMonthBeginAndEnd().get(0);
    		end = DateUtil.getLastMonthBeginAndEnd().get(1);
    	}
    	else if("year".equals(dateType)){
    		begin = DateUtil.getLastYearBeginAndEnd().get(0);
    		end = DateUtil.getLastYearBeginAndEnd().get(1);
    	}
    	
    	Long beginTime = new Long(begin.getTime());
		Long endTime = new Long(end.getTime());
    	
		//签单数量
		Long orderNumber = 0L;
		//签单金额
		BigDecimal signAmount = BigDecimal.ZERO;
		ordDatas = getOrderDataByChannelId(ordDatas,qtyRank.getChannelId());
    	for(OrdOrder order:ordDatas){
    		//获取签单日期
    		Date date = order.getChannelSignDate();
			Long paramTime = new Long(date.getTime());
			if(beginTime.compareTo(paramTime)<=0 && endTime.compareTo(paramTime)>=0){
				/*log.debug("订单类型："+order.getOrderType());
				log.debug("订单时间："+paramTime);*/
				orderNumber = orderNumber+1;
				//计算缴费总额
				BigDecimal exchange = BigDecimal.ONE;
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
					signAmount = signAmount.add(order.getYearPayAmount().multiply(exchange));
				}else if("BOND".equals(order.getOrderType()) || "IMMIGRANT".equals(order.getOrderType())){
					signAmount = signAmount.add(order.getPolicyAmount().multiply(exchange));
				}
			}
    	}
    	
    	
    	//计算数量环比
    	if("quantity".equals(requestType)){
    		if(orderNumber == 0){
    			return null;
    		}
    		variinePercent = Double.valueOf(currentOrderNumber-orderNumber)/Double.valueOf(orderNumber);
    	}else if("amount".equals(requestType)){
    		BigDecimal signAmountRate = BigDecimal.ZERO;
    		if(BigDecimal.ZERO.compareTo(signAmount) == 0){
    			return null;
    		}
    		if(!BigDecimal.ZERO.equals(signAmount)){
    			signAmount = signAmount.setScale(0,BigDecimal.ROUND_HALF_UP);
    			signAmountRate = (currentSignAmount.subtract(signAmount)).divide(signAmount,4,BigDecimal.ROUND_DOWN);
    		}
    		variinePercent = signAmountRate.doubleValue();
    	}
		return variinePercent;
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
    
    public static int searchString(String[] array,String param){
    	Arrays.sort(array,new StringCompar());
    	return Arrays.binarySearch(array,param);
    }
    
    public static class StringCompar implements Comparator<String>{

		@Override
		public int compare(String o1, String o2) {
			return o1.compareTo(o2);
		}
    	
    }

}