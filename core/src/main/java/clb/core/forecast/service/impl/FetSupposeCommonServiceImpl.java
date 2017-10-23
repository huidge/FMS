package clb.core.forecast.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import clb.core.channel.dto.CnlChannel;
import clb.core.channel.mapper.CnlChannelMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.xpath.operations.Bool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.channel.dto.CnlChannelContract;
import clb.core.channel.dto.CnlContractRole;
import clb.core.channel.mapper.CnlChannelContractMapper;
import clb.core.channel.mapper.CnlContractRoleMapper;
import clb.core.common.exceptions.CommonException;
import clb.core.common.utils.DateUtil;
import clb.core.common.utils.ExceptionUtil;
import clb.core.common.utils.RefelctUtil;
import clb.core.common.utils.SerializeUtil;
import clb.core.exchangeRate.dto.FetExchangeRate;
import clb.core.exchangeRate.mapper.FetExchangeRateMapper;
import clb.core.forecast.dto.FetChannelCheck;
import clb.core.forecast.dto.FetPayable;
import clb.core.forecast.dto.FetPeriod;
import clb.core.forecast.dto.FetPeriodHead;
import clb.core.forecast.dto.FetQuestion;
import clb.core.forecast.dto.FetReceivable;
import clb.core.forecast.dto.FetSupposeCommon;
import clb.core.forecast.dto.FetSupposeMessage;
import clb.core.forecast.dto.FetSupposeUpdateVersion;
import clb.core.forecast.dto.OrdRouteData;
import clb.core.forecast.mapper.FetChannelCheckMapper;
import clb.core.forecast.mapper.FetPayableMapper;
import clb.core.forecast.mapper.FetPeriodHeadMapper;
import clb.core.forecast.mapper.FetPeriodMapper;
import clb.core.forecast.mapper.FetQuestionMapper;
import clb.core.forecast.mapper.FetReceivableMapper;
import clb.core.forecast.mapper.FetSupposeCommonMapper;
import clb.core.forecast.service.IFetChannelCheckService;
import clb.core.forecast.service.IFetPayableService;
import clb.core.forecast.service.IFetPeriodService;
import clb.core.forecast.service.IFetQuestionService;
import clb.core.forecast.service.IFetReceivableService;
import clb.core.forecast.service.IFetSupposeCommonService;
import clb.core.forecast.utils.SupposeCommonUtil;
import clb.core.notification.service.INtnNotificationService;
import clb.core.order.dto.OrdAddition;
import clb.core.order.dto.OrdCommission;
import clb.core.order.mapper.OrdAdditionMapper;
import clb.core.order.mapper.OrdCommissionMapper;
import clb.core.supplier.dto.SpeCollectionTerms;
import clb.core.supplier.mapper.SpeCollectionTermsMapper;
import clb.core.system.dto.ClbCodeValue;
import clb.core.system.dto.ClbUser;
import clb.core.system.mapper.ClbUserMapper;
import clb.core.system.service.IClbCodeService;
import jodd.util.ReflectUtil;
@Service("FetSupposeCommonServiceImpl")
@Scope("prototype")
@Transactional
public class FetSupposeCommonServiceImpl extends BaseServiceImpl<FetSupposeCommon> implements IFetSupposeCommonService{

	private static Logger log = LoggerFactory.getLogger(FetSupposeCommonServiceImpl.class);

	@Autowired
	private FetSupposeCommonMapper commonMapper;

	@Autowired
	private OrdCommissionMapper ordCommissionMapper;

	//付款条件
	@Autowired
	private SpeCollectionTermsMapper collectionTermsMapper;

	//附加险
	@Autowired
	private OrdAdditionMapper ordAdditionMapper;

	//汇率
	@Autowired
	private FetExchangeRateMapper rateMapper;

	//期间Mapper
	@Autowired
	private FetPeriodHeadMapper periodHeaderMapper;

	//期间Mapper
	@Autowired
	private FetPeriodMapper periodMapper;

	//期间Service
	@Autowired
    private IFetPeriodService periodService;

	//应收数据Service
	@Autowired
	private IFetReceivableService fetReceivableService;

	//应收数据Mapper
	@Autowired
	private FetReceivableMapper fetReceivableMapper;

	//应付数据Service
	@Autowired
	private IFetPayableService fetPayableService;

	//应付数据Mapper
	@Autowired
	private FetPayableMapper fetPayableMapper;

	//应付数据Service
	@Autowired
	private IFetChannelCheckService channelCheckService;

	//应付数据Mapper
	@Autowired
	private FetChannelCheckMapper channelCheckMapper;

	//渠道合约信息Mapper
	@Autowired
	private CnlChannelContractMapper channelContractMapper;

	//渠道合约利益分配信息Mapper
	@Autowired
	private CnlContractRoleMapper contractRoleMapper;

	//用户mapper
	@Autowired
	private ClbUserMapper userMapper;

	@Autowired
	private FetQuestionMapper questionMapper;

	@Autowired
	private IFetQuestionService questionService;

	@Autowired
	private IClbCodeService clbCodeService;

	@Autowired
	private INtnNotificationService ntnNotificationService;

	@Autowired
	private CnlChannelMapper cnlChannelMapper;

	//主险Map
	Map<String,String> productTypeMap = new HashMap<>();
	//基准日期Map
	Map<String,String> baseDateTypeMap = new HashMap<>();
	//条件类型Map
	Map<String,String> termTypeMap = new HashMap<>();

	//应收页面
	private static final String RECEIVE_TYPE = "receive";

	//应付页面
	private static final String PAYMENT_TYPE = "payable";

	//渠道对账页面
	private static final String CHECK_TYPE="channelcheck";


	//错误信息
	private List<FetSupposeMessage> errorMessages;

	//所有渠道合约数据
	private List<CnlChannelContract> allChannelContracts;

	//所有交易路线信息
	private List<OrdCommission> allCommissions;

	//所有付款条件
	private List<SpeCollectionTerms> allCollectionTerms;

	//所有利益分配信息
	List<CnlContractRole> allContractRoles;

	//所有用户信息
	List<ClbUser> allUsers;

	 //所有汇率数据
    private List<FetExchangeRate> allExchangeRateData;


	//渠道合约/利益分配 取值数组
	private static final String[] CHANNEL_CONTRACT_ARRAY = {"channelContractId"};

	//渠道合约二次取值数组
	private static final String[] OTHER_CHANNEL_CONTRACT_ARRAY = {"channelId","partyType","partyId"};

	//交易路线取值数组
	private static final String[] COMMISSION_DATA_ARRAY = {"orderId","itemId"};

	//交易路线取值数组
	private static final String[] COLLECTION_TERMS_ARRAY = {"productDivision","termType","baseDate","productCompanyId","paymentCompanyType","paymentCompanyId"};

	//用户取值数组
	private static final String[] USER_DATA = {"userId"};

	//汇率取值数组
    private static final String[] EXCHANGE_DATA= {"foreignCurrency","baseCurrency"};

	/**
	 * 批量更新应收/应付版本
	 *
	 *  @param iRequest 请求信息
	 *  @param List 应收/应付数据
	 *
	 *  @return 更新后的数据
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<FetSupposeMessage> batchUpdateVersion(IRequest iRequest,List<FetSupposeCommon> supposeCommons) throws CommonException {
		//初始化错误信息
		errorMessages = new ArrayList<>();
		//初始化渠道合约数据
		allChannelContracts = channelContractMapper.selectAll();
		//初始化付款条件
		allCollectionTerms = collectionTermsMapper.selectAll();
		//初始化交易路线信息
		allCommissions = ordCommissionMapper.queryCommissionData(null);
		//初始化利益分配信息
		allContractRoles = contractRoleMapper.selectAll();
		//初始化用户信息
		allUsers = userMapper.selectAll();
		//初始化汇率数据
    	FetExchangeRate exchangeRate = new FetExchangeRate();
		exchangeRate.setCurrentTime(new Date());
		allExchangeRateData = rateMapper.selectRateByCurrentTime(exchangeRate);
		if(CollectionUtils.isEmpty(supposeCommons))return new ArrayList<>();
		//请求类型
		String requestType = supposeCommons.get(0).getRequestType();
		//要更新的数据
		List<FetSupposeCommon> resDatas = new ArrayList<>();
		//要更新的应收数据
		List<FetReceivable> allReceivableDatas = new ArrayList<>();
		//要更新的应付数据
		List<FetPayable> allPayableDatas = new ArrayList<>();
		//要更新的渠道对账数据
		List<FetChannelCheck> allChannelCheckDatas = new ArrayList<>();
		//旧的渠道对账数据
		List<FetChannelCheck> oldChannelCheckDatas = new ArrayList<>();
		//问题列表
		List<FetQuestion> questions = new ArrayList();
		//要返回的错误信息
		List<FetSupposeMessage> resMessages = new ArrayList<>();
		for(FetSupposeCommon supposeCommon:supposeCommons){
			//Set集合
			Set<Long> orderIdSet = new HashSet<>();
			//获取订单Id
			List<Long> orderIds = new ArrayList<Long>();
			//获取参数
			Long periodId = supposeCommon.getPeriodId();
			String periodName = supposeCommon.getPeriodName();
			String paymentCompanyType = supposeCommon.getPaymentCompanyType();
			String receiveCompanyType = supposeCommon.getReceiveCompanyType();
			Long paymentCompanyId = supposeCommon.getPaymentCompanyId();
			Long receiveCompanyId = supposeCommon.getReceiveCompanyId();
			Long version = supposeCommon.getVersion();

			//更新参数
			FetSupposeUpdateVersion updateVersion = new FetSupposeUpdateVersion();
			updateVersion.setPaymentCompanyType(paymentCompanyType);
			updateVersion.setPaymentCompanyId(paymentCompanyId);
			updateVersion.setReceiveCompanyType(receiveCompanyType);
			updateVersion.setReceiveCompanyId(receiveCompanyId);
			/*  --------先查出当前期间和指定收款&付款方下的订单数据--------*/
			//应收页面
			if(RECEIVE_TYPE.equals(supposeCommon.getRequestType())){
				FetReceivable receivable = new FetReceivable();
				receivable.setReceiptPeriod(periodName);
				receivable.setPaymentCompanyType(paymentCompanyType);
				receivable.setPaymentCompanyId(paymentCompanyId);
				receivable.setReceiveCompanyType(receiveCompanyType);
				receivable.setReceiveCompanyId(receiveCompanyId);
				//校验是否为最新版本
				receivable.setVersion(version+1);
				if(CollectionUtils.isNotEmpty(fetReceivableMapper.select(receivable))){
					throw new CommonException("FET",String.format("数据:%s-%s-%s不是最新版本！",periodName,supposeCommon.getReceiveCompanyName(),supposeCommon.getPaymentCompanyName()),null);
				}
				receivable.setVersion(version);
				List<FetReceivable> receiveDatas = fetReceivableMapper.select(receivable);
				for(FetReceivable receiveData:receiveDatas){
					orderIdSet.add(receiveData.getOrderId());
				}
			}
			//应付页面
			else if(PAYMENT_TYPE.equals(supposeCommon.getRequestType())){
				FetPayable payable = new FetPayable();
				payable.setPaymentPeriod(periodName);
				payable.setPaymentCompanyType(paymentCompanyType);
				payable.setPaymentCompanyId(paymentCompanyId);
				payable.setReceiveCompanyType(receiveCompanyType);
				payable.setReceiveCompanyId(receiveCompanyId);
				//校验是否为最新版本
				payable.setVersion(version+1);
				if(CollectionUtils.isNotEmpty(fetPayableMapper.select(payable))){
					throw new CommonException("FET",String.format("数据:%s-%s-%s不是最新版本！",periodName,supposeCommon.getPaymentCompanyName(),supposeCommon.getReceiveCompanyName()),null);
				}
				payable.setVersion(version);
				List<FetPayable> paymentDatas = fetPayableMapper.select(payable);
				for(FetPayable paymentData:paymentDatas){
					orderIdSet.add(paymentData.getOrderId());
				}
			}
			else if(CHECK_TYPE.equals(supposeCommon.getRequestType())){
				FetChannelCheck channelCheck = new FetChannelCheck();
				List<FetChannelCheck> channelCheckDatas = new ArrayList<>();
				channelCheck.setCheckPeriod(periodName);
				channelCheck.setPaymentCompanyType(paymentCompanyType);
				channelCheck.setPaymentCompanyId(paymentCompanyId);
				channelCheck.setReceiveCompanyType(receiveCompanyType);
				channelCheck.setReceiveCompanyId(receiveCompanyId);
				//校验是否为最新版本
				channelCheck.setVersion(version+1);
				if(CollectionUtils.isNotEmpty(channelCheckMapper.select(channelCheck))){
					throw new CommonException("FET",String.format("数据:%s-%s-%s不是最新版本！",periodName,supposeCommon.getPaymentCompanyName(),supposeCommon.getReceiveCompanyName()),null);
				}
				channelCheck.setVersion(version);
				channelCheckDatas = channelCheckMapper.select(channelCheck);
				//旧的渠道对账数据
				oldChannelCheckDatas = (List<FetChannelCheck>) CollectionUtils.union(oldChannelCheckDatas,channelCheckDatas);
				for(FetChannelCheck oldChannelCheckData:oldChannelCheckDatas){
					orderIdSet.add(oldChannelCheckData.getOrderId());
				}
			}
			//获取订单信息
			FetSupposeCommon resData = null;
			orderIds = commonMapper.getOrderIds(supposeCommon);
			//将查询到的Id放入Set集合
			for(Long orderId:orderIds){
				orderIdSet.add(orderId);
			}
			Iterator<Long> it = orderIdSet.iterator();

			while(it.hasNext()){
			    Long orderId = it.next();
				FetSupposeCommon param = new FetSupposeCommon();
				param.setOrderId(orderId);
				resData = commonMapper.getOne(param);
				if(resData == null){
					continue;
				}
				resData.setPeriodId(periodId);
				List<FetSupposeCommon> orderDatas = getOrderDataByParam(resData,updateVersion);
				//筛选符合条件的订单数据
				for(FetSupposeCommon orderData:orderDatas){
					Long newVersion = version+1;
					orderData.setVersion(newVersion);
					resDatas.add(orderData);
				}
			}

			//筛选错误信息
			resMessages = getResMessages(requestType,updateVersion);
		}

		int updates = 0;

		try{
			if(RECEIVE_TYPE.equals(requestType)){ // todo
				allReceivableDatas = SupposeCommonUtil.commonListToReceivableList(resDatas);
				updates = allReceivableDatas.size();
//				fetReceivableService.batchUpdate(iRequest, allReceivableDatas);
				fetReceivableService.batchHandle(allReceivableDatas, iRequest);
			}else if(PAYMENT_TYPE.equals(requestType)){
				allPayableDatas = SupposeCommonUtil.commonListToPayableList(resDatas);
				updates = allPayableDatas.size();
//				fetPayableService.batchUpdate(iRequest, allPayableDatas);
				fetPayableService.batchHandle(allPayableDatas, iRequest);
			}else if(CHECK_TYPE.equals(requestType)){
				allChannelCheckDatas = SupposeCommonUtil.commonListToChannelCheckList(resDatas);
				updates = allChannelCheckDatas.size();
//				channelCheckService.batchUpdate(iRequest, allChannelCheckDatas);
				channelCheckService.batchHandle(allChannelCheckDatas, iRequest);
				this.sendNotice(allChannelCheckDatas, iRequest); // 发送对账单通知
				List<FetQuestion> allQuestions = questionMapper.selectAll();
				for(FetChannelCheck channelCheck:oldChannelCheckDatas){
					channelCheck.set__status(DTOStatus.UPDATE);
					channelCheck.setStatus("YSX");
					FetQuestion question = new FetQuestion();
					question.setCheckPeriod(channelCheck.getCheckPeriod());
					question.setPaymentCompanyType(channelCheck.getPaymentCompanyType());
					question.setPaymentCompanyId(channelCheck.getPaymentCompanyId());
					question.setChannelId(channelCheck.getReceiveCompanyId());
					question.setVersion(channelCheck.getVersion());
					List<FetQuestion> questionDatas = RefelctUtil.getListDataByFields(allQuestions,question,"checkPeriod","paymentCompanyType","paymentCompanyId","channelId","version");
					if(CollectionUtils.isNotEmpty(questionDatas)){
						for(FetQuestion questionData:questionDatas){
							questionData.set__status(DTOStatus.UPDATE);
							questionData.setStatus("YJJ");
							questions.add(questionData);
						}
					}
				}
				channelCheckService.batchUpdate(iRequest,oldChannelCheckDatas);
				questionService.batchUpdate(iRequest, questions);
			}
		}catch(Exception e){
			if(ExceptionUtil.getExceptionType(e) == 1){
				throw new RuntimeException(new CommonException("FET","数据重复，更新失败！",null));
			}else{
				throw new RuntimeException(new CommonException("FET","更新失败!",null));
			}
		}
		iRequest.setAttribute("updates",updates);
		GarbageCollection();
		return resMessages;
	}


	/**
	 *  更新全部应收/应付版本
	 *
	 *  @param iRequest 请求信息
	 *  @param periodId 期间Id
	 *  @param requestType 请求类型,用于区分应收/应付
	 *
	 *  @return 更新后的数据
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<FetSupposeMessage> UpdateAllVersion(IRequest iRequest,FetSupposeUpdateVersion param) throws CommonException {
		String requestType = param.getRequestType();
		//初始化错误信息
		errorMessages = new ArrayList<>();
		//根据期间Id获取所有订单数据
		List<FetSupposeCommon> orderDatas = getAllOrderData(param);
		//要更新的数据
		List<FetSupposeCommon> resDatas = new ArrayList<>();
		//要更新的应收数据
		List<FetReceivable> allReceivableDatas = new ArrayList<>();
		//要更新的应付数据
		List<FetPayable> allPayableDatas = new ArrayList<>();
		//要更新的渠道对账数据
		List<FetChannelCheck> allChannelCheckDatas = new ArrayList<>();
		//旧的渠道对账数据
		List<FetChannelCheck> oldChannelCheckDatas = new ArrayList<>();
		//要返回的错误信息
		List<FetSupposeMessage> resMessages = errorMessages;
		//问题列表
		List<FetQuestion> questions = new ArrayList();
		resDatas = calculateMaxVersions(orderDatas,requestType,param,oldChannelCheckDatas);
		//筛选错误信息
		resMessages = getResMessages(requestType,param);

		int updates = 0;

		try{
			if(RECEIVE_TYPE.equals(requestType)){
				allReceivableDatas = SupposeCommonUtil.commonListToReceivableList(resDatas);
				updates = allReceivableDatas.size();
				fetReceivableService.batchHandle(allReceivableDatas,iRequest);  // 批量插入
			}else if(PAYMENT_TYPE.equals(requestType)){
				allPayableDatas = SupposeCommonUtil.commonListToPayableList(resDatas);
				updates = allPayableDatas.size();
				fetPayableService.batchHandle(allPayableDatas, iRequest); // 批量插入
			}else if(CHECK_TYPE.equals(requestType)){
				allChannelCheckDatas = SupposeCommonUtil.commonListToChannelCheckList(resDatas);
				updates = allChannelCheckDatas.size();
				channelCheckService.batchHandle(allChannelCheckDatas, iRequest); // 批量插入
				this.sendNotice(allChannelCheckDatas, iRequest); // 发送对账单通知
				List<FetQuestion> allQuestions = questionMapper.selectAll();
				for(FetChannelCheck channelCheck:oldChannelCheckDatas){
					channelCheck.set__status(DTOStatus.UPDATE);
					channelCheck.setStatus("YSX");
					FetQuestion question = new FetQuestion();
					question.setCheckPeriod(channelCheck.getCheckPeriod());
					question.setPaymentCompanyType(channelCheck.getPaymentCompanyType());
					question.setPaymentCompanyId(channelCheck.getPaymentCompanyId());
					question.setChannelId(channelCheck.getReceiveCompanyId());
					question.setVersion(channelCheck.getVersion());
					List<FetQuestion> questionDatas = RefelctUtil.getListDataByFields(allQuestions,question,"checkPeriod","paymentCompanyType","paymentCompanyId","channelId","version");
					if(CollectionUtils.isNotEmpty(questionDatas)){
						for(FetQuestion questionData:questionDatas){
							questionData.set__status(DTOStatus.UPDATE);
							questionData.setStatus("YJJ");
							questions.add(questionData);
						}
					}
				}
				channelCheckService.batchUpdate(iRequest,oldChannelCheckDatas);
				questionService.batchUpdate(iRequest, questions);
			}
		} catch(Exception e){
			e.printStackTrace();
			if(ExceptionUtil.getExceptionType(e) == 1){
				throw new RuntimeException(new CommonException("FET","数据重复，更新失败！",null));
			}else{
				throw new RuntimeException(new CommonException("FET","插入数据失败!",null));
			}
		}
		iRequest.setAttribute("updates",updates);
		GarbageCollection();
		return resMessages;
	}

	private List<FetSupposeMessage> getResMessages(String requestType,FetSupposeUpdateVersion param){
		List<FetSupposeMessage> resMessages = new ArrayList<>();
		//筛选符合条件的报错信息
		for(FetSupposeMessage message:errorMessages){
			Boolean isContinue = true;
			for(FetSupposeMessage resMessage:resMessages){
				if(resMessage.getOrderNumber() != null && resMessage.getErrorMessage() != null){
					if(resMessage.getOrderNumber().equals(message.getOrderNumber()) && resMessage.getErrorMessage().equals(message.getErrorMessage())){
						isContinue = false;
					}
				}
			}
			if(!isContinue)continue;
			//不考虑收付款方
			if(!message.isConsiderRP()){
				resMessages.add(message);
			}
			else{
				if(param != null){
					//如果是应付页面，取应付是当前参数的数据
					if(PAYMENT_TYPE.equals(requestType)){
						if(param.getPaymentCompanyType() != null && param.getPaymentCompanyId() != null){
							if(!param.getPaymentCompanyType().equals(message.getPaymentCompanyType())
									|| !param.getPaymentCompanyId().equals(message.getPaymentCompanyId())){
								continue;
							}
						}
					}

					//如果是应收和渠道对账页面，取应收是当前参数的数据
					if(RECEIVE_TYPE.equals(requestType) || CHECK_TYPE.equals(requestType)){
						if(param.getReceiveCompanyType() != null && param.getReceiveCompanyId() != null){
							if(!param.getReceiveCompanyType().equals(message.getReceiveCompanyType())
									|| !param.getReceiveCompanyId().equals(message.getReceiveCompanyId())){
								continue;
							}
						}
					}

					resMessages.add(message);
				}else{
					resMessages.add(message);
				}
			}
		}
		return resMessages;
	}
	
	/**
	 * 计算各种数据的版本
	 * @param 订单数据
	 * @param 请求类型
	 * @param 更新版本的参数
	 * @param 旧的渠道对账数据
	 */
	public List<FetSupposeCommon> calculateMaxVersions(List<FetSupposeCommon> orderDatas,String requestType,FetSupposeUpdateVersion param,List<FetChannelCheck> oldChannelCheckDatas){
		String paymentCompanyType = "";
		Long paymentCompanyId = 0L;
		String receiveCompanyType = "";
		Long receiveCompanyId = 0L;
		Map<String,Long> versionMap = new HashMap<>();
		//要更新的数据
		List<FetSupposeCommon> resDatas = new ArrayList<>();
		if(RECEIVE_TYPE.equals(requestType)){
			//获取所有应收数据
			List<FetReceivable> receivables = fetReceivableMapper.summaryQuery(null);
			//筛选符合条件的订单数据
			for(FetSupposeCommon orderData:orderDatas){
				boolean isNew = true;
				//根据付款方类型&付款方名称&收款方类型&收款方名称查找数据，用于获取版本号
				for(FetReceivable receivable:receivables){
					paymentCompanyType = receivable.getPaymentCompanyType();
					paymentCompanyId = receivable.getPaymentCompanyId();
					receiveCompanyType = receivable.getReceiveCompanyType();
					receiveCompanyId = receivable.getReceiveCompanyId();

					if(paymentCompanyType.equals(orderData.getPaymentCompanyType())
					&& paymentCompanyId.equals(orderData.getPaymentCompanyId())
					&& receiveCompanyType.equals(orderData.getReceiveCompanyType())
					&& receiveCompanyId.equals(orderData.getReceiveCompanyId())){
						//先在版本map中获取数据
						String key = paymentCompanyType+"&"+paymentCompanyId+"&"+receiveCompanyType+"&"+receiveCompanyId;
						Long maxVersion = versionMap.get(key);
						//如果没找到，查询最大版本
						if(maxVersion == null || maxVersion == 0){
							//查询版本信息并缓存
							FetSupposeCommon paramData = new FetSupposeCommon();
							paramData.setPaymentCompanyType(paymentCompanyType);
							paramData.setPaymentCompanyId(paymentCompanyId);
							paramData.setReceiveCompanyType(receiveCompanyType);
							paramData.setReceiveCompanyId(receiveCompanyId);
							maxVersion = getMaxVersion(receivables,paramData,requestType);
							versionMap.put(key, maxVersion);
						}
						//最大版本加一
						maxVersion = maxVersion + 1L;
						orderData.setVersion(maxVersion);
						resDatas.add(orderData);
						//查到数据，说明不是新数据，标志位置为false
						isNew = false;
						break;
					}
				}
				//是新的数据，版本置为1
				if(isNew){
					orderData.setVersion(1L);
					resDatas.add(orderData);
				}
			}
		}else if(PAYMENT_TYPE.equals(requestType)){
			//获取所有应收数据，逻辑同上
			List<FetPayable> fetPayables = fetPayableMapper.summaryQuery(null);
			for(FetSupposeCommon orderData:orderDatas){
				boolean isNew = true;
				for(FetPayable fetPayable:fetPayables){
					paymentCompanyType = fetPayable.getPaymentCompanyType();
					paymentCompanyId = fetPayable.getPaymentCompanyId();
					receiveCompanyType = fetPayable.getReceiveCompanyType();
					receiveCompanyId = fetPayable.getReceiveCompanyId();

					if(paymentCompanyType.equals(orderData.getPaymentCompanyType())
					&& paymentCompanyId.equals(orderData.getPaymentCompanyId())
					&& receiveCompanyType.equals(orderData.getReceiveCompanyType())
					&& receiveCompanyId.equals(orderData.getReceiveCompanyId())){
						String key = paymentCompanyType+"&"+paymentCompanyId+"&"+receiveCompanyType+"&"+receiveCompanyId;
						Long maxVersion = versionMap.get(key);
						if(maxVersion == null || maxVersion == 0){
							//查询版本信息并缓存
							FetSupposeCommon paramData = new FetSupposeCommon();
							paramData.setPaymentCompanyType(paymentCompanyType);
							paramData.setPaymentCompanyId(paymentCompanyId);
							paramData.setReceiveCompanyType(receiveCompanyType);
							paramData.setReceiveCompanyId(receiveCompanyId);
							maxVersion = getMaxVersion(fetPayables,paramData,requestType);
							versionMap.put(key, maxVersion);
						}
						maxVersion = maxVersion+1;
						orderData.setVersion(maxVersion);
						resDatas.add(orderData);
						isNew = false;
						break;
					}

				}
				if(isNew){
					orderData.setVersion(1L);
					resDatas.add(orderData);
				}
			}
		}else if(CHECK_TYPE.equals(requestType)){
			//获取所有对账数据，逻辑同上
			List<FetChannelCheck> channelChecks = channelCheckMapper.summaryQuery(null);
			FetChannelCheck updateParam = new FetChannelCheck();
			if(param != null){
				if(param.getPaymentCompanyType() != null && param.getPaymentCompanyId() != null){
					updateParam.setPaymentCompanyType(param.getPaymentCompanyType());
					updateParam.setPaymentCompanyId(param.getPaymentCompanyId());
				}
				if(param.getReceiveCompanyType() != null && param.getReceiveCompanyId() != null){
					updateParam.setReceiveCompanyType(param.getReceiveCompanyType());
					updateParam.setReceiveCompanyId(param.getReceiveCompanyId());
				}
			}
			//获取所有对账期间
			Set<String> periodSet = new HashSet<>();
			for(FetSupposeCommon orderData:orderDatas){
				periodSet.add(orderData.getPeriodName());
			}
			Iterator<String> periodIt = periodSet.iterator();
			while(periodIt.hasNext()){
				updateParam.setCheckPeriod(periodIt.next());
				oldChannelCheckDatas.addAll(channelCheckMapper.select(updateParam));
			}
			for(FetSupposeCommon orderData:orderDatas){
				boolean isNew = true;
				for(FetChannelCheck channelCheck:channelChecks){
					paymentCompanyType = channelCheck.getPaymentCompanyType();
					paymentCompanyId = channelCheck.getPaymentCompanyId();
					receiveCompanyType = channelCheck.getReceiveCompanyType();
					receiveCompanyId = channelCheck.getReceiveCompanyId();

					if(paymentCompanyType.equals(orderData.getPaymentCompanyType())
					&& paymentCompanyId.equals(orderData.getPaymentCompanyId())
					&& receiveCompanyType.equals(orderData.getReceiveCompanyType())
					&& receiveCompanyId.equals(orderData.getReceiveCompanyId())){
						String key = paymentCompanyType+"&"+paymentCompanyId+"&"+receiveCompanyType+"&"+receiveCompanyId;
						Long maxVersion = versionMap.get(key);
						if(maxVersion == null || maxVersion == 0){
							//查询版本信息并缓存
							FetSupposeCommon paramData = new FetSupposeCommon();
							paramData.setPaymentCompanyType(paymentCompanyType);
							paramData.setPaymentCompanyId(paymentCompanyId);
							paramData.setReceiveCompanyType(receiveCompanyType);
							paramData.setReceiveCompanyId(receiveCompanyId);
							maxVersion = getMaxVersion(channelChecks,paramData,requestType);
							versionMap.put(key, maxVersion);
						}
						maxVersion = maxVersion+1;
						orderData.setVersion(maxVersion);
						resDatas.add(orderData);
						isNew = false;
						break;
					}
				}
				if(isNew){
					orderData.setVersion(1L);
					resDatas.add(orderData);
				}
			}
		}
		return resDatas;
	}

	/**
	 * 根据付款方类型&付款方名称&收款方类型&收款方名称于获取最大版本号
	 * @param originData 应收/应付数据
	 * @param param 封装了付款方类型等参数的对象
	 * @param requestType 请求类型,用于区分应收/应付
	 *
	 * @return 最大版本号
	 */
	public Long getMaxVersion(List originData,FetSupposeCommon param,String requestType){
		if(RECEIVE_TYPE.equals(requestType)){
			List<FetReceivable> receivableData = (List<FetReceivable>)originData;
			List<FetReceivable> resData = new ArrayList<>();
			for(FetReceivable receivable:receivableData){
					if(param.getPaymentCompanyType().equals(receivable.getPaymentCompanyType())
					&& param.getPaymentCompanyId().equals(receivable.getPaymentCompanyId())
					&& param.getReceiveCompanyType().equals(receivable.getReceiveCompanyType())
					&& param.getReceiveCompanyId().equals(receivable.getReceiveCompanyId())){
						resData.add(receivable);
					}
			}
			//根据版本号排序
			Collections.sort(resData,new Comparator<FetReceivable>(){
				@Override
				public int compare(FetReceivable args1, FetReceivable args2) {
					return (int) (args1.getVersion()-args2.getVersion());
				}
			});
			return resData.get(resData.size()-1).getVersion();
		}else if(PAYMENT_TYPE.equals(requestType)){
			List<FetPayable> paymentableData = (List<FetPayable>)originData;
			List<FetPayable> resData = new ArrayList<>();
			for(FetPayable payable:paymentableData){
					if(param.getPaymentCompanyType().equals(payable.getPaymentCompanyType())
					&& param.getPaymentCompanyId().equals(payable.getPaymentCompanyId())
					&& param.getReceiveCompanyType().equals(payable.getReceiveCompanyType())
					&& param.getReceiveCompanyId().equals(payable.getReceiveCompanyId())){
						resData.add(payable);
					}
			}
			Collections.sort(resData,new Comparator<FetPayable>(){
				@Override
				public int compare(FetPayable args1, FetPayable args2) {
					return (int) (args1.getVersion()-args2.getVersion());
				}
			});
			return resData.get(resData.size()-1).getVersion();
		}else if(CHECK_TYPE.equals(requestType)){
			List<FetChannelCheck> channelCheckData = (List<FetChannelCheck>)originData;
			List<FetChannelCheck> resData = new ArrayList<>();
			for(FetChannelCheck channelCheck:channelCheckData){
					if(param.getPaymentCompanyType().equals(channelCheck.getPaymentCompanyType())
					&& param.getPaymentCompanyId().equals(channelCheck.getPaymentCompanyId())
					&& param.getReceiveCompanyType().equals(channelCheck.getReceiveCompanyType())
					&& param.getReceiveCompanyId().equals(channelCheck.getReceiveCompanyId())){
						resData.add(channelCheck);
					}
			}
			Collections.sort(resData,new Comparator<FetChannelCheck>(){
				@Override
				public int compare(FetChannelCheck args1, FetChannelCheck args2) {
					return (int) (args1.getVersion()-args2.getVersion());
				}
			});
			return resData.get(resData.size()-1).getVersion();
		}
		return 0L;

	}

	/**
	 * 根据期间Id获取所有订单相关数据
	 *
	 * @param periodId 期间Id
	 *
	 * @return 订单相关数据
	 */
	@Override
	public List<FetSupposeCommon> getAllOrderData(FetSupposeUpdateVersion param) throws CommonException{
		if(errorMessages == null){
			errorMessages = new ArrayList<>();
		}
		//初始化渠道合约数据
		allChannelContracts = channelContractMapper.selectAll();
		//初始化付款条件
		allCollectionTerms = collectionTermsMapper.selectAll();
		//获取所有订单数据
		List<FetSupposeCommon> paramData = commonMapper.getAll();
		//初始化交易路线信息
		allCommissions = ordCommissionMapper.queryCommissionDataByOrderIds(paramData);
		//初始化利益分配信息
		allContractRoles = contractRoleMapper.selectAll();
		//初始化用户信息
		allUsers = userMapper.selectAll();
		//初始化汇率数据
    	FetExchangeRate exchangeRate = new FetExchangeRate();
		exchangeRate.setCurrentTime(new Date());
		allExchangeRateData = rateMapper.selectRateByCurrentTime(exchangeRate);
		List<FetSupposeCommon> orderDatas = new ArrayList<>();
		//筛选订单数据
		for(FetSupposeCommon data : paramData){
			List<FetSupposeCommon> resData = getDealDataByOrder(data,param);
			orderDatas = (List<FetSupposeCommon>) CollectionUtils.union(orderDatas,resData);
		}
		return orderDatas;
	}

	/**
	 * 根据参数筛选订单数据
	 *
	 * @param orderData 订单相关参数
	 * @return 订单数据
	 * @throws CommonException
	 */
	public List<FetSupposeCommon> getOrderDataByParam(FetSupposeCommon orderData,FetSupposeUpdateVersion param) throws CommonException{
		//返回数据
		List<FetSupposeCommon> resDatas = new ArrayList<>();//获取当前期间
		FetPeriod fetPeriod = new FetPeriod();
		fetPeriod.setPeriodId(orderData.getPeriodId());
		fetPeriod = periodMapper.selectByPrimaryKey(fetPeriod);
		if(fetPeriod == null) throw new CommonException("FET","当前期间不存在！",null);
		param.setPeriodId(fetPeriod.getPeriodId());
		//筛选订单数据
		resDatas = getDealDataByOrder(orderData,param);
		return resDatas;

	}



	/**
	 * 根据订单获取交易信息
	 * @throws CommonException
	 */
	public List<FetSupposeCommon> getDealDataByOrder(FetSupposeCommon orderData,FetSupposeUpdateVersion param) throws CommonException{
		//交易信息
		List<FetSupposeCommon> dealRoutes = new ArrayList<>();
		//保单预计冷静期为空，直接返回空
		if(orderData.getExpectCoolDate() == null && "INSURANCE".equals(orderData.getOrderType())){
			log.info("订单{}预计冷静期为空!",orderData.getOrderNumber());
			FetSupposeMessage errorMessage = new FetSupposeMessage();
			errorMessage.setOrderNumber(orderData.getOrderNumber());
			errorMessage.setErrorMessage("预计冷静期为空!");
			errorMessages.add(errorMessage);
			return new ArrayList<FetSupposeCommon>();
		}
		//投资移民签约时间为空，直接返回空
		if(orderData.getIssueDate() == null && "IMMIGRANT".equals(orderData.getOrderType())){
			log.info("订单{}签约时间为空!",orderData.getOrderNumber());
			FetSupposeMessage errorMessage = new FetSupposeMessage();
			errorMessage.setOrderNumber(orderData.getOrderNumber());
			errorMessage.setErrorMessage("签约时间为空!");
			errorMessages.add(errorMessage);
			return new ArrayList<FetSupposeCommon>();
		}
		//债券发行日期为空，直接返回空
		if(orderData.getIssueDate() == null && "BOND".equals(orderData.getOrderType())){
			log.info("订单{}发行日期为空!",orderData.getOrderNumber());
			FetSupposeMessage errorMessage = new FetSupposeMessage();
			errorMessage.setOrderNumber(orderData.getOrderNumber());
			errorMessage.setErrorMessage("发行日期为空!");
			errorMessages.add(errorMessage);
			return new ArrayList<FetSupposeCommon>();
		}
		//缓存付款条件
		OrdCommission paramCommission = new OrdCommission();
		
		/* ----获取主险信息---- */
		paramCommission.setOrderId(orderData.getOrderId());
		paramCommission.setItemId(orderData.getItemId());
		//获取交易路线信息
		List<OrdCommission> commissionData = new ArrayList<>();
		commissionData = (List<OrdCommission>)RefelctUtil.getListDataByFields(allCommissions,paramCommission,COMMISSION_DATA_ARRAY);
		//通过序列号排序
		Collections.sort(commissionData,new Comparator<OrdCommission>(){
			@Override
			public int compare(OrdCommission args1, OrdCommission args2) {
				return (int) (args1.getSeqNum()-args2.getSeqNum());
			}
		});
		//保单期数不能为空
		if(orderData.getPayPeriods() == null && "INSURANCE".equals(orderData.getOrderType())){
			log.info("订单{}当前期数为空!",orderData.getOrderNumber());
			FetSupposeMessage errorMessage = new FetSupposeMessage();
			errorMessage.setOrderNumber(orderData.getOrderNumber());
			errorMessage.setErrorMessage("当前期数为空!");
			errorMessages.add(errorMessage);
			return new ArrayList<FetSupposeCommon>();
		}

		//债券和投资移民订单只计算首年保费
		if("BOND".equals(orderData.getOrderType()) || "IMMIGRANT".equals(orderData.getOrderType())){
			orderData.setPayPeriods(1L);
		}

		dealRoutes = getSupposeData(orderData,param,commissionData);
		return dealRoutes;
	}


	public List<FetSupposeCommon> getSupposeData(FetSupposeCommon orderData,FetSupposeUpdateVersion param,List<OrdCommission> commissionData) throws CommonException{
		//IRequest request = RequestHelper.getCurrentRequest();
		IRequest request = RequestHelper.newEmptyRequest();
		request.setLocale("zh_CN");
		if(productTypeMap.size() == 0){
			List<ClbCodeValue> productCodeValues = clbCodeService.selectCodeValuesByCodeName(request,"PRD.PRODUCT_DIVISION");
			for(ClbCodeValue clbCodeValue:productCodeValues){
				productTypeMap.put(clbCodeValue.getValue(),clbCodeValue.getMeaning());
			}
		}
		if(baseDateTypeMap.size() == 0){
			List<ClbCodeValue> baseDateCodeValues = clbCodeService.selectCodeValuesByCodeName(request,"SPE.BASE_DATE");
			for(ClbCodeValue clbCodeValue:baseDateCodeValues){
				baseDateTypeMap.put(clbCodeValue.getValue(),clbCodeValue.getMeaning());
			}
		}
		if(termTypeMap.size() == 0){
			List<ClbCodeValue> termTypeCodeValues = clbCodeService.selectCodeValuesByCodeName(request,"SPE.TERM_TYPE");
			for(ClbCodeValue clbCodeValue:termTypeCodeValues){
				termTypeMap.put(clbCodeValue.getValue(),clbCodeValue.getMeaning());
			}
		}
		//交易信息
		List<FetSupposeCommon> dealRoutes = new ArrayList<>();

		//缓存时间
		Map<Integer,Date> dueDates = new HashMap();
		//初始期间数据，用于判断是否传入期间Id
		List<FetPeriod> initPeriods = new ArrayList<>();
		Long periodId = param.getPeriodId();
		if(periodId != null){
			FetPeriod period = new FetPeriod();
			period.setPeriodId(periodId);
			period = periodMapper.selectByPrimaryKey(period);
			initPeriods.add(period);
		}
		
		/* ----获取主险信息---- */

		//获取订单结算数据
		List<OrdRouteData> routeDatas = getRouteDatas(commissionData,orderData,false);

		//筛选订单结算数据
		routeDatas = scrrenOrdRouteData(routeDatas, param);
		//没有数据，直接返回空
		if(CollectionUtils.isEmpty(routeDatas)){
			return new ArrayList<FetSupposeCommon>();
		}
		
		/* -------------------------------------查询付款条件------------------------------------- */
		//获取付款方，根据付款条件，计算应收/应付日期
		SpeCollectionTerms collectionTerm = new SpeCollectionTerms();
		//条件分类-产品大分类
		collectionTerm.setProductDivision(orderData.getBigClass());
		//条件类型，根据年期获取。若是第一年，则是首期保费，否则为续期保费
		Long payPeriod = orderData.getPayPeriods();
		if(payPeriod == null)payPeriod = 1L;
		if(payPeriod.equals(1L)){
			//债券取发行日期
			if("BOND".equals(orderData.getOrderType())){
				collectionTerm.setTermType("ZSNZJF");
				collectionTerm.setBaseDate("FXRQ");
			}
			//投资移民取签约时间
			else if("IMMIGRANT".equals(orderData.getOrderType())){
				collectionTerm.setTermType("DSNZJF");
				collectionTerm.setBaseDate("QYSJ");
			}else{
				collectionTerm.setTermType("SNBF");
				collectionTerm.setBaseDate("LJQJZR");
			}
		}else{
			collectionTerm.setTermType("XQBF");
			collectionTerm.setBaseDate("XBR");
		}
		//产品公司
		collectionTerm.setProductCompanyId(orderData.getItemCompanyId());
		//主险交易路线-获取付款方信息
		for(int i=0;i<routeDatas.size();i++){
			//设置付款方信息
			collectionTerm.setPaymentCompanyType(routeDatas.get(i).getPaymentNode().getCompanyType());
			collectionTerm.setPaymentCompanyId(routeDatas.get(i).getPaymentNode().getCompanyId());
			//查询付款条件，计算应收/应付日期
			SpeCollectionTerms termData = new SpeCollectionTerms();
			List<SpeCollectionTerms> terms = RefelctUtil.getListDataByFields(allCollectionTerms,collectionTerm,COLLECTION_TERMS_ARRAY);
			if(CollectionUtils.isEmpty(terms))termData = null;
			else termData = terms.get(0);
			if(termData != null){
				//根据付款条件计算应收/应付日期
				Date dueDate = getDueDate(orderData,termData);
				if(dueDate != null){
					//缓存日期
					dueDates.put(i,dueDate);
					//复制订单信息
					FetSupposeCommon route = (FetSupposeCommon) SerializeUtil.clone(orderData);

					//设置收款方，付款方信息和应收、应付日期
					route.setPaymentCompanyType(routeDatas.get(i).getPaymentNode().getCompanyType());
					route.setPaymentCompanyId(routeDatas.get(i).getPaymentNode().getCompanyId());
					route.setPaymentCompanyName(routeDatas.get(i).getPaymentNode().getCompanyName());
					route.setReceiveCompanyType(routeDatas.get(i).getReceiveNode().getCompanyType());
					route.setReceiveCompanyId(routeDatas.get(i).getReceiveNode().getCompanyId());
					route.setReceiveCompanyName(routeDatas.get(i).getReceiveNode().getCompanyName());
					route.setDueDate(dueDate);
					route.setPaymentType(routeDatas.get(i).getPaymentType());
					route.setItemId(orderData.getItemId());

					//根据收入类型，设置订单币种，订单金额，汇率信息
					//先设置转介费,介绍费和管理津贴(由于route对象要重复使用，所以每次都要新clone一个)
					FetSupposeCommon newRoute = (FetSupposeCommon) SerializeUtil.clone(route);
					newRoute = getAcountData(orderData.getOrderNumber(),newRoute,routeDatas.get(i).getActualRate(),dueDate,"COMMONFEE");
					if(newRoute != null){
						dealRoutes.add(newRoute);
					}
					//类型不为介绍费时，计算刷卡费和签单费
					if(StringUtils.indexOfIgnoreCase(route.getPaymentType(),"JSF") == -1){
						//若刷卡费不为空
						if(route.getCardFee() != null && route.getCardFee().compareTo(BigDecimal.ZERO)!=0){
							//由于route对象要重复使用，所以每次都要新clone一个
							newRoute = (FetSupposeCommon) SerializeUtil.clone(route);
							newRoute = getAcountData(orderData.getOrderNumber(),newRoute,routeDatas.get(i).getActualRate(),dueDate,"SKF");
							if(newRoute != null){
								dealRoutes.add(newRoute);
							}
						}
						//若签单费不为空
						if(route.getSignFee() != null && route.getSignFee().compareTo(BigDecimal.ZERO)!=0){
							//由于route对象要重复使用，所以每次都要新clone一个
							newRoute = (FetSupposeCommon) SerializeUtil.clone(route);
							newRoute = getAcountData(orderData.getOrderNumber(),newRoute,routeDatas.get(i).getActualRate(),dueDate,"QDF");
							if(newRoute != null){
								dealRoutes.add(newRoute);
							}
						}
					}


				}else{
					log.info("订单{}付款条件 {} 获取应收/应付日期失败!",orderData.getOrderNumber(),termData.getTermCode());
					FetSupposeMessage errorMessage = new FetSupposeMessage();
					errorMessage.setOrderNumber(orderData.getOrderNumber());
					errorMessage.setErrorMessage("获取应收/应付日期失败!");
					errorMessage.setPaymentCompanyType(routeDatas.get(i).getPaymentNode().getCompanyType());
					errorMessage.setPaymentCompanyId(routeDatas.get(i).getPaymentNode().getCompanyId());
					errorMessage.setReceiveCompanyType(routeDatas.get(i).getReceiveNode().getCompanyType());
					errorMessage.setReceiveCompanyId(routeDatas.get(i).getReceiveNode().getCompanyId());
					errorMessage.setConsiderRP(true);
					errorMessages.add(errorMessage);
				}
			}else{
				log.info("订单{}付款条件{}未找到！",orderData.getOrderNumber(),collectionTerm);
				FetSupposeMessage errorMessage = new FetSupposeMessage();
				errorMessage.setOrderNumber(orderData.getOrderNumber());
				errorMessage.setErrorMessage("该订单下付款方:"+routeDatas.get(i).getPaymentNode().getCompanyName()+",条件分类:"+productTypeMap.get(collectionTerm.getProductDivision())+",条件类型:"+termTypeMap.get(collectionTerm.getTermType())+",基准日期："+baseDateTypeMap.get(collectionTerm.getBaseDate())+" 付款条件未找到!");
				errorMessage.setPaymentCompanyType(routeDatas.get(i).getPaymentNode().getCompanyType());
				errorMessage.setPaymentCompanyId(routeDatas.get(i).getPaymentNode().getCompanyId());
				errorMessage.setReceiveCompanyType(routeDatas.get(i).getReceiveNode().getCompanyType());
				errorMessage.setReceiveCompanyId(routeDatas.get(i).getReceiveNode().getCompanyId());
				errorMessage.setConsiderRP(true);
				errorMessages.add(errorMessage);
			}

		}
		/*  ----获取附加险信息---- */
		OrdAddition ordAddition = new OrdAddition();
		ordAddition.setOrderId(orderData.getOrderId());
		List<OrdAddition> ordAdditions = ordAdditionMapper.select(ordAddition);
		//附加险信息不为空
		if(CollectionUtils.isNotEmpty(ordAdditions)){
			for(OrdAddition addition:ordAdditions){
				//获取交易路线信息
				OrdCommission paramCommission = new OrdCommission();
				paramCommission.setOrderId(orderData.getOrderId());
				paramCommission.setItemId(addition.getItemId());
				commissionData = (List<OrdCommission>)RefelctUtil.getListDataByFields(allCommissions,paramCommission,COMMISSION_DATA_ARRAY);
				routeDatas = getRouteDatas(commissionData,orderData,true);

				//筛选订单结算数据
				routeDatas = scrrenOrdRouteData(routeDatas, param);
				if(CollectionUtils.isNotEmpty(routeDatas)){
					//附加险交易路线
					for(int i=0;i<routeDatas.size();i++){
						Date dueDate = dueDates.get(i);
						if(dueDate != null){
							//复制订单信息
							FetSupposeCommon route = (FetSupposeCommon) SerializeUtil.clone(orderData);

							//设置产品
							route.setItemId(addition.getItemId());
							//设置金额
							route.setYearPayAmount(addition.getYearPayAmount());

							//设置收款方，付款方信息和应收、应付日期
							route.setPaymentCompanyType(routeDatas.get(i).getPaymentNode().getCompanyType());
							route.setPaymentCompanyId(routeDatas.get(i).getPaymentNode().getCompanyId());
							route.setReceiveCompanyType(routeDatas.get(i).getReceiveNode().getCompanyType());
							route.setReceiveCompanyId(routeDatas.get(i).getReceiveNode().getCompanyId());
							route.setDueDate(dueDate);
							route.setPaymentType(routeDatas.get(i).getPaymentType());
							route.setItemId(addition.getItemId());
							//根据收入类型，获取数据
							//附加险只有转介费
							route = getAcountData(orderData.getOrderNumber(),route,routeDatas.get(i).getActualRate(),dueDate,"COMMONFEE");
							if(route != null){
								dealRoutes.add(route);
							}

						}
					}
				}
			}
		}
		//返回数据
		List<FetSupposeCommon> resList= new ArrayList<>();
		//根据期间筛选数据
		Long beginTime = 0L;
		Long endTime = 0L;
		Long paramTime = 0L;
		//缓存期间数据
		Map<String,List<FetPeriod>> periodMap = new HashMap<>();
		for(FetSupposeCommon common:dealRoutes){
			beginTime = 0L;
			endTime = 0L;
			try{
				paramTime = DateUtil.getFormatDate(common.getDueDate(), "yyyy-MM-dd").getTime();
			}catch(Exception e){
				throw new CommonException("FET","日期转换错误！",null);
			}
			//期间List不为空，说明Id不为空，直接去当前期间
			if(CollectionUtils.isNotEmpty(initPeriods)){
				FetPeriod period = initPeriods.get(0);
				beginTime  = period.getStartDate().getTime();
				endTime = period.getEndDate().getTime();
				if(beginTime.compareTo(paramTime)<=0 && endTime.compareTo(paramTime)>=0){
					common.setPeriodName(period.getPeriodName());
					resList.add(common);
				}
			}else{
				//期间List
				List<FetPeriod> periods = new ArrayList<>();
				//用付款方类型和Id作为Key
				String key = common.getPaymentCompanyType()+"&"+common.getPaymentCompanyId();
				//先从缓存Map中获取
				if(periodMap.get(key) != null){
					periods = periodMap.get(key);
				}else{
					//根据当前付款方，查询期间数据
					FetPeriodHead periodHead = new FetPeriodHead();
					periodHead.setPartyType(common.getPaymentCompanyType());
					periodHead.setPartyId(common.getPaymentCompanyId());
					periodHead = periodHeaderMapper.selectOne(periodHead);
					if(periodHead == null){
						log.info("订单{}付款方{}期间数据未找到！",orderData.getOrderNumber(),common.getPaymentCompanyName());
						FetSupposeMessage errorMessage = new FetSupposeMessage();
						errorMessage.setOrderNumber(orderData.getOrderNumber());
						String companyTypeName = "供应商";
						if("CHANNEL".equals(common.getPaymentCompanyType()))companyTypeName = "渠道";
						errorMessage.setErrorMessage("该订单下付款方:"+companyTypeName+"-"+common.getPaymentCompanyName()+"期间数据未找到!");
						errorMessage.setPaymentCompanyType(common.getPaymentCompanyType());
						errorMessage.setPaymentCompanyId(common.getPaymentCompanyId());
						errorMessage.setReceiveCompanyType(common.getReceiveCompanyType());
						errorMessage.setReceiveCompanyId(common.getReceiveCompanyId());
						errorMessage.setConsiderRP(true);
						errorMessages.add(errorMessage);
						continue;
					}
					//查询所有打开的期间
					FetPeriod paramPeriod = new FetPeriod();
					paramPeriod.setHeadId(periodHead.getHeadId());
					paramPeriod.setStatusCode("OPEN");
					periods = periodMapper.select(paramPeriod);
					//放入Map中缓存，避免多次查询
					periodMap.put(key, periods);
				}
				for(FetPeriod period:periods){
					beginTime  = period.getStartDate().getTime();
					endTime = period.getEndDate().getTime();
					if(beginTime.compareTo(paramTime)<=0 && endTime.compareTo(paramTime)>=0){
						common.setPeriodName(period.getPeriodName());
						resList.add(common);
					}
				}
			}

		}

		return resList;
	}


	/**
	 * 筛选交易信息 (核心代码，不要动)
	 *
	 * @param originData 原始数据
	 * @param param 更新版本页面传递的参数
	 *
	 * @return 筛选后的数据
	 */
	public List<OrdRouteData> scrrenOrdRouteData(List<OrdRouteData> originData,FetSupposeUpdateVersion param){
		List<OrdRouteData> resData  = new ArrayList<>();
		if(param != null){
			for(OrdRouteData routeData:originData){
				if(StringUtils.isNotEmpty(param.getPaymentCompanyType()) && param.getPaymentCompanyId() != null){
					if(!routeData.getPaymentNode().getCompanyType().equals(param.getPaymentCompanyType()) || !routeData.getPaymentNode().getCompanyId().equals(param.getPaymentCompanyId())){
						continue;
					}
				}
				if(StringUtils.isNotEmpty(param.getReceiveCompanyType()) && param.getReceiveCompanyId() != null){
					if(!routeData.getReceiveNode().getCompanyType().equals(param.getReceiveCompanyType()) || !routeData.getReceiveNode().getCompanyId().equals(param.getReceiveCompanyId())){
						continue;
					}
				}
				resData.add(routeData);
			}
		}else{
			return originData;
		}
		return resData;
	}


	/**
	 * 根据订单信息和付款条件，获取应付/应收日期(核心代码，如果不是业务变更，不要动)
	 *
	 * @param 预计冷静期
	 * @param 开始时间
	 * @param 截止时间
	 * @param 付款条件
	 *
	 * @return 结算后的日期或null
	 */

	public Date getDueDate(FetSupposeCommon orderData,SpeCollectionTerms termData){
		if(termData == null)return null;
		Date baseDate = new Date();
		Date dueDate = new Date();
		//结算天数
		Long settleDays = termData.getSettleDays();
		//一天的毫秒数
		Long dayMinus = (long) (24*60*60*1000);

		//如果是保单
		if("INSURANCE".equals(orderData.getOrderType())){
			//首年保费
			if("SNBF".equals(termData.getTermType())){
				baseDate = orderData.getExpectCoolDate();
			}else if("XQBF".equals(termData.getTermType())){
				baseDate = orderData.getRenewalDueDate();
			}
		}
		//债券取发行日期
		if("BOND".equals(orderData.getOrderType())){
			baseDate = orderData.getIssueDate();
		}
		//投资移民取签约时间
		else if("IMMIGRANT".equals(orderData.getOrderType())){
			baseDate = orderData.getIssueDate();
		}
		if(baseDate == null){
			return null;
		}
		//基准日期的毫秒数
		Long baseDateTime = baseDate.getTime();
		//结算天数毫秒数
		Long settleDaysTime = settleDays*dayMinus;
		Long dueDateTime = baseDateTime+settleDaysTime;
		dueDate = DateUtil.transFormToDate(dueDateTime);
		//计算后的日期位于开始日期和截止日期之间
		int day = DateUtil.getDay(dueDate);
		if(day <= Integer.valueOf(termData.getSettleDate1())){
			dueDate = DateUtil.setDate(DateUtil.getYear(dueDate),DateUtil.getMonth(dueDate),Integer.valueOf(termData.getSettleDate1()));
		}else{
			//结算日期2不为空
			if(termData.getSettleDate2() != null){
				//大于结算日期2,顺延到下个月的结算日期1
				if(day > Integer.valueOf(termData.getSettleDate2())){
					dueDate = DateUtil.setDate(DateUtil.getYear(dueDate),DateUtil.getMonth(dueDate)+1,Integer.valueOf(termData.getSettleDate1()));
				}
				//设置为结算日期2
				else{
					dueDate = DateUtil.setDate(DateUtil.getYear(dueDate),DateUtil.getMonth(dueDate),Integer.valueOf(termData.getSettleDate2()));
				}
			}
			//结算日期2为空,顺延到下个月的结算日期1
			else{
				dueDate = DateUtil.setDate(DateUtil.getYear(dueDate),DateUtil.getMonth(dueDate)+1,Integer.valueOf(termData.getSettleDate1()));
			}
		}
		return dueDate;
	}


	/**
	 * 根据收入类型，设置 订单币种，订单金额，费率，汇率信息(核心代码，如果不是业务变更，不要动)
	 *
	 * @param route 订单数据
	 * @param rate 费率信息
	 * @param type 转介费/刷卡费/签单费
	 *
	 * @return 完整的订单数据
	 *
	 * @throws CommonException
	 *
	 */
	public FetSupposeCommon getAcountData(String orderNumber,FetSupposeCommon route,BigDecimal rate,Date dueDate,String type) throws CommonException{
		route.setComments(route.getPolicyNumber());
		if("COMMONFEE".equals(type)){
			//订单币种
			route.setOrderCurrency(route.getOrderCurrency());
			//债券和移民投资订单,取保额
			if("BOND".equals(route.getOrderType()) || "IMMIGRANT".equals(route.getOrderType())){
				//年度缴费
				route.setAmount(route.getPolicyAmount());
			}
			//保单取年缴费
			else{
				//年度缴费
				route.setAmount(route.getYearPayAmount());
			}
			//设置费率
			route.setRate(rate);
		}
		//刷卡费
		if("SKF".equals(type)){
			route.setPaymentType(type);
			//订单币种
			route.setOrderCurrency(route.getOrderCurrency());
			//刷卡费
			if(route.getCardFee().compareTo(BigDecimal.ZERO) > 0){
				route.setAmount(BigDecimal.ZERO.subtract(route.getCardFee()));
			}else{
				route.setAmount(route.getCardFee());
			}
			//费率为100%
			route.setRate(new BigDecimal(1));
		}
		//签单费
		if("QDF".equals(type)){
			route.setPaymentType(type);
			//订单币种
			route.setOrderCurrency("HKD");
			//刷卡费
			if(route.getSignFee().compareTo(BigDecimal.ZERO) > 0){
				route.setAmount(BigDecimal.ZERO.subtract(route.getSignFee()));
			}else{
				route.setAmount(route.getSignFee());
			}
			//费率为100%
			route.setRate(new BigDecimal(1));
		}

		//设置汇率
		if("HKD".equals(route.getOrderCurrency()))route.setExchangeRate(new BigDecimal(1));
		else{
			FetExchangeRate exchangeRate = new FetExchangeRate();
			//外币
			exchangeRate.setForeignCurrency(route.getOrderCurrency());
			//港币
			exchangeRate.setBaseCurrency("HKD");
			List<FetExchangeRate> exchangeRates = (List<FetExchangeRate>)RefelctUtil.getListDataByFields(allExchangeRateData,exchangeRate,EXCHANGE_DATA);
			if(exchangeRates == null || exchangeRates.size() == 0){
				log.info("请维护外币：{}和基准货币:HKD的汇率!",route.getOrderCurrency());
				FetSupposeMessage errorMessage = new FetSupposeMessage();
				errorMessage.setOrderNumber(orderNumber);
				errorMessage.setErrorMessage(String.format("请维护外币：%s和基准货币:HKD的汇率!",route.getOrderCurrency()));
				errorMessage.setPaymentCompanyType(route.getPaymentCompanyType());
				errorMessage.setPaymentCompanyId(route.getPaymentCompanyId());
				errorMessage.setReceiveCompanyType(route.getReceiveCompanyType());
				errorMessage.setReceiveCompanyId(route.getReceiveCompanyId());
				errorMessage.setConsiderRP(true);
				errorMessages.add(errorMessage);
				return null;
			}
			route.setExchangeRate(new BigDecimal(exchangeRates.get(0).getRate()));
		}
		return route;
	}


	/**
	 * 用于定时任务
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void insertSupposeData(IRequest iRequest, List<FetPeriod> periodDatas) throws CommonException {
		if(errorMessages == null){
			errorMessages = new ArrayList<>();
		}
		
		FetSupposeUpdateVersion param = null;
		for(FetPeriod periodData:periodDatas){
			param = new FetSupposeUpdateVersion();
			param.setPeriodId(periodData.getPeriodId());
			FetPeriodHead periodHead = periodHeaderMapper.selectPeriodHeadByPeriod(periodData);
			param.setPaymentCompanyType(periodHead.getPartyType());
			param.setPaymentCompanyId(periodHead.getPartyId());
    		List<FetSupposeCommon> data =  getAllOrderData(param);
    		periodData.setUpdateFlag("N");
    		periodData.set__status(DTOStatus.UPDATE);
    		try{
    			//应收
    			List<FetSupposeCommon> receiveData = calculateMaxVersions(data,RECEIVE_TYPE,param,null);
    			List<FetReceivable> allReceivableDatas = SupposeCommonUtil.commonListToReceivableList(receiveData);
    			fetReceivableService.batchHandle(allReceivableDatas,iRequest);  // 批量插入
    			
    			//应付
    			List<FetSupposeCommon> paymentData = calculateMaxVersions(data,PAYMENT_TYPE,param,null);
    			List<FetPayable> allPayableDatas = SupposeCommonUtil.commonListToPayableList(paymentData);
    			fetPayableService.batchHandle(allPayableDatas, iRequest); // 批量插入
    			
    			//应付
    			List<FetChannelCheck> oldChannelCheckDatas = new ArrayList<>();
    			List<FetSupposeCommon> checkData = calculateMaxVersions(data,CHECK_TYPE,param,oldChannelCheckDatas);
    			List<FetChannelCheck> allChannelCheckDatas = SupposeCommonUtil.commonListToChannelCheckList(checkData);
    			List<FetQuestion> questions = new ArrayList<>();
    			channelCheckService.batchHandle(allChannelCheckDatas, iRequest); // 批量插入
				this.sendNotice(allChannelCheckDatas, iRequest); // 发送对账单通知
				List<FetQuestion> allQuestions = questionMapper.selectAll();
				for(FetChannelCheck channelCheck:oldChannelCheckDatas){
					channelCheck.set__status(DTOStatus.UPDATE);
					channelCheck.setStatus("YSX");
					FetQuestion question = new FetQuestion();
					question.setCheckPeriod(channelCheck.getCheckPeriod());
					question.setPaymentCompanyType(channelCheck.getPaymentCompanyType());
					question.setPaymentCompanyId(channelCheck.getPaymentCompanyId());
					question.setChannelId(channelCheck.getReceiveCompanyId());
					question.setVersion(channelCheck.getVersion());
					List<FetQuestion> questionDatas = RefelctUtil.getListDataByFields(allQuestions,question,"checkPeriod","paymentCompanyType","paymentCompanyId","channelId","version");
					if(CollectionUtils.isNotEmpty(questionDatas)){
						for(FetQuestion questionData:questionDatas){
							questionData.set__status(DTOStatus.UPDATE);
							questionData.setStatus("YJJ");
							questions.add(questionData);
						}
					}
				}
				channelCheckService.batchUpdate(iRequest,oldChannelCheckDatas);
				questionService.batchUpdate(iRequest, questions);
				
    		} catch(Exception e){
    			e.printStackTrace();
    			if(ExceptionUtil.getExceptionType(e) == 1){
    				throw new RuntimeException(new CommonException("FET","数据重复，更新失败！",null));
    			}else{
    				throw new RuntimeException(new CommonException("FET","插入数据失败!",null));
    			}
    		}
    	}
		periodService.batchUpdate(iRequest, periodDatas);
		GarbageCollection();
	}

	/**
	 * 计算实际费率
	 */
	public BigDecimal calculateActualRate(BigDecimal a,BigDecimal b,int i,int index){
		if(i<=index){
			return a.subtract(b);
		}else{
			return a;
		}
	}

	/**
	 * 根据订单交易路线，获取时间收款方/付款方(核心代码 ,不是业务变更,不要动)
	 * @param orderData 订单数据
	 * @param isAddition 是否附加险
	 */
	public List<OrdRouteData> getRouteDatas(List<OrdCommission> commissions,FetSupposeCommon orderData,boolean isAddition) throws CommonException{
		//佣金信息为空或只有一条数据
		if(CollectionUtils.isEmpty(commissions))return new ArrayList<OrdRouteData>();
		else if(commissions.size() == 1) return new ArrayList<OrdRouteData>();
		//订单当前期数
		Long payPeriods = orderData.getPayPeriods();
		//产品公司Id
		Long itemCompanyId = orderData.getItemCompanyId();
		//产品公司名称
		String itemCompanyName = orderData.getItemCompanyName();
		//订单编号
		String orderNumber = orderData.getOrderNumber();

		List<OrdRouteData> ordRouteDatas = new ArrayList<>();
		//定位供应商位置
		int index = -1;
		for(int i=0;i<commissions.size();i++){
			if("SUPPLIER".equals(commissions.get(i).getCompanyType())){
				index = i;
				break;
			}
		}
		if(index == -1){
			FetSupposeMessage errorMessage = new FetSupposeMessage();
			errorMessage.setOrderNumber(orderNumber);
			log.info("该订单的交易路线数据没有供应商!");
			errorMessage.setErrorMessage("该订单的交易路线数据没有供应商!");
			errorMessages.add(errorMessage);
			return new ArrayList<OrdRouteData>();
		}
		boolean isSuccess = true;
		for(int i=0;i<commissions.size();i++){
			//根据年期计算交易路线的实际费率
			switch (payPeriods.intValue()) {
			case 1:
				if(i == 0)commissions.get(i).setActualRate(commissions.get(i).getTheFirstYear());
				else commissions.get(i).setActualRate(calculateActualRate(commissions.get(i).getTheFirstYear(),commissions.get(i-1).getTheFirstYear(),i,index));
				break;
			case 2:
				if(i == 0)commissions.get(i).setActualRate(commissions.get(i).getTheSecondYear());
				else commissions.get(i).setActualRate(calculateActualRate(commissions.get(i).getTheSecondYear(),commissions.get(i-1).getTheSecondYear(),i,index));
				break;
			case 3:
				if(i == 0)commissions.get(i).setActualRate(commissions.get(i).getTheThirdYear());
				else commissions.get(i).setActualRate(calculateActualRate(commissions.get(i).getTheThirdYear(),commissions.get(i-1).getTheThirdYear(),i,index));
				break;
			case 4:
				if(i == 0)commissions.get(i).setActualRate(commissions.get(i).getTheFourthYear());
				else commissions.get(i).setActualRate(calculateActualRate(commissions.get(i).getTheFourthYear(),commissions.get(i-1).getTheFourthYear(),i,index));
				break;
			case 5:
				if(i == 0)commissions.get(i).setActualRate(commissions.get(i).getTheFifthYear());
				else commissions.get(i).setActualRate(calculateActualRate(commissions.get(i).getTheFifthYear(),commissions.get(i-1).getTheFifthYear(),i,index));
				break;
			case 6:
				if(i == 0)commissions.get(i).setActualRate(commissions.get(i).getTheSixthYear());
				else commissions.get(i).setActualRate(calculateActualRate(commissions.get(i).getTheSixthYear(),commissions.get(i-1).getTheSixthYear(),i,index));
				break;
			case 7:
				if(i == 0)commissions.get(i).setActualRate(commissions.get(i).getTheSeventhYear());
				else commissions.get(i).setActualRate(calculateActualRate(commissions.get(i).getTheSeventhYear(),commissions.get(i-1).getTheSeventhYear(),i,index));
				break;
			case 8:
				if(i == 0)commissions.get(i).setActualRate(commissions.get(i).getTheEightYear());
				else commissions.get(i).setActualRate(calculateActualRate(commissions.get(i).getTheEightYear(),commissions.get(i-1).getTheEightYear(),i,index));
				break;
			case 9:
				if(i == 0)commissions.get(i).setActualRate(commissions.get(i).getTheNinthYear());
				else commissions.get(i).setActualRate(calculateActualRate(commissions.get(i).getTheNinthYear(),commissions.get(i-1).getTheNinthYear(),i,index));
				break;
			case 10:
				if(i == 0)commissions.get(i).setActualRate(commissions.get(i).getTheTenthYear());
				else commissions.get(i).setActualRate(calculateActualRate(commissions.get(i).getTheTenthYear(),commissions.get(i-1).getTheTenthYear(),i,index));
				break;
			default:
				break;
			}
			//查询渠道合约详情
			CnlChannelContract channelContract = new CnlChannelContract();
			channelContract.setChannelContractId(commissions.get(i).getChannelContractId());
			List<CnlChannelContract> channelContractList= (List<CnlChannelContract>) RefelctUtil.getListDataByFields(allChannelContracts,channelContract,CHANNEL_CONTRACT_ARRAY);
			//如果找不到合约，用当前渠道和上级来获取
			if(CollectionUtils.isEmpty(channelContractList)){
				//当前节点是渠道
				if("CHANNEL".equals(commissions.get(i).getCompanyType())){
					channelContract = new CnlChannelContract();
					channelContract.setChannelId(commissions.get(i).getCompanyId());
					channelContract.setPartyType(commissions.get(i+1).getCompanyType());
					channelContract.setPartyId(commissions.get(i+1).getCompanyId());
					channelContractList= (List<CnlChannelContract>) RefelctUtil.getListDataByFields(allChannelContracts,channelContract,OTHER_CHANNEL_CONTRACT_ARRAY);
					if(CollectionUtils.isEmpty(channelContractList)){
						channelContract = null;
					}
					else channelContract = channelContractList.get(0);
				}
				//当前节点是供应商，直接设为null
				else{
					channelContract = null;
				}
			}
			else{
				channelContract = channelContractList.get(0);
			}
			//如果合约信息是空
			if(channelContract == null){
				//如果当前节点是渠道，且不是附加险交易路线，报错
				if("CHANNEL".equals(commissions.get(i).getCompanyType()) && !isAddition){
					isSuccess = false;
					FetSupposeMessage errorMessage = new FetSupposeMessage();
					errorMessage.setOrderNumber(orderNumber);
					String companyTypeName = "供应商";
					if("CHANNEL".equals(commissions.get(i).getCompanyType()))companyTypeName = "渠道";
					log.info(String.format("渠道:%s和%s:%s的合约信息不存在！",commissions.get(i).getCompanyName(),companyTypeName,commissions.get(i+1).getCompanyName()));
					errorMessage.setErrorMessage(String.format("渠道:%s和%s:%s的合约信息不存在！",commissions.get(i).getCompanyName(),companyTypeName,commissions.get(i+1).getCompanyName()));
					errorMessages.add(errorMessage);
				}
				commissions.get(i).setChannelContract(null);
			}
			else commissions.get(i).setChannelContract(channelContract);
		}

		if(!isSuccess)return new ArrayList<OrdRouteData>();

		//在这里算介绍费
		for(int i=0;i<index;i++){ // todo
			CnlContractRole param = new CnlContractRole();
			param.setChannelContractId(commissions.get(i).getChannelContractId());
			List<CnlContractRole> contractRoles = (List<CnlContractRole>)RefelctUtil.getListDataByFields(allContractRoles,param,CHANNEL_CONTRACT_ARRAY);
			for(CnlContractRole contractRole:contractRoles){
				//类型为介绍人、创客、SAM
				String role = contractRole.getRole();
				if("INTRODUCER".equals(role) || "MAKER".equals(role) || "SAM".equals(role)){
					OrdRouteData ordRouteData = new OrdRouteData();
					//设置收款方，由于可能重复使用commissions.get(i),所以每次都要新建一个对象，否则会直接修改commissions.get(i)
					OrdCommission ordCommission = (OrdCommission) SerializeUtil.clone(commissions.get(i));
					ordCommission.setCompanyType("CHANNEL");
					Long channelUserId = contractRole.getRoleUserId();
					ClbUser user = new ClbUser();
					user.setUserId(channelUserId);
					List<ClbUser> users = (List<ClbUser>)RefelctUtil.getListDataByFields(allUsers,user,USER_DATA);
					if(CollectionUtils.isEmpty(users) && !isAddition){
						isSuccess = false;
						String roleC = "";
						if ("INTRODUCER".equals(role)) {
							roleC = "介绍人";
						} else if ("MAKER".equals(role)) {
							roleC = "创客";
						} else if ("SAM".equals(role)) {
							roleC = "SAM";
						}
						log.info(String.format("渠道:%s,合约:%s的%s用户不存在！",ordCommission.getCompanyName(), roleC, ordCommission.getContractCode()));
						FetSupposeMessage errorMessage = new FetSupposeMessage();
						errorMessage.setOrderNumber(orderNumber);
						errorMessage.setErrorMessage(String.format("渠道:%s,合约:%s的%s用户不存在！",ordCommission.getCompanyName(), roleC, ordCommission.getContractCode()));
						errorMessages.add(errorMessage);
					}else{
						user = users.get(0);
						ordCommission.setCompanyId(user.getRelatedPartyId()); //渠道ID改为用户关联ID
						ordRouteData.setReceiveNode(ordCommission);
						//该渠道的上级付款
						ordRouteData.setPaymentNode(commissions.get(i+1));
						BigDecimal actualRate = commissions.get(i+1).getActualRate().multiply(new BigDecimal(contractRole.getBenefit()));
						//保留四位小数
						actualRate.setScale(4,BigDecimal.ROUND_DOWN);
						ordRouteData.setActualRate(actualRate);
						ordRouteData.setPaymentType(payPeriods+"JSF");
						ordRouteDatas.add(ordRouteData);
					}
				}
			}
		}

		if(!isSuccess)return new ArrayList<OrdRouteData>();

		for(int i=0;i<commissions.size();i++){
			CnlChannelContract channelContract = commissions.get(i).getChannelContract();
			OrdRouteData ordRouteData = new OrdRouteData();
			 //合约信息是null，说明是供应商
			 if(channelContract == null){
				ordRouteData.setPaymentType(payPeriods+"ZJF");
				if(i == commissions.size()-1){
					//clone一个对象，作为产品公司
					OrdCommission ordCommission = (OrdCommission) SerializeUtil.clone(commissions.get(i));
					ordCommission.setCompanyId(itemCompanyId);
					ordCommission.setCompanyName(itemCompanyName);
					ordRouteData.setReceiveNode(commissions.get(i));
					ordRouteData.setPaymentNode(ordCommission);
					ordRouteData.setActualRate(commissions.get(i).getActualRate());
				}
				else{
					ordRouteData.setReceiveNode(commissions.get(i));
					ordRouteData.setPaymentNode(commissions.get(i+1));
					ordRouteData.setActualRate(commissions.get(i).getActualRate());
				}
			}else{
				if(i == 0)ordRouteData.setPaymentType(payPeriods+"ZJF");
				//如果当前渠道和下级渠道不可结算，则设置为管理津贴
				else{
					if("N".equals(commissions.get(i-1).getChannelContract().getSettleFlag())){
						ordRouteData.setPaymentType(payPeriods+"GLJT");
					}else{
						ordRouteData.setPaymentType(payPeriods+"ZJF");
					}
				}
				if("Y".equals(channelContract.getSettleFlag())){
					ordRouteData.setReceiveNode(commissions.get(i));
					ordRouteData.setPaymentNode(commissions.get(i+1));
					ordRouteData.setActualRate(commissions.get(i).getActualRate());
					commissions.get(i+1).setActualRate(commissions.get(i).getActualRate().add(commissions.get(i+1).getActualRate()));
				}
				else{
					//找上一级可结算的渠道
					for(int j=i+1;j<index;j++){
						channelContract = commissions.get(j).getChannelContract();
						if("Y".equals(channelContract.getSettleFlag())){
							ordRouteData.setReceiveNode(commissions.get(i));
							ordRouteData.setPaymentNode(commissions.get(j+1));
							ordRouteData.setActualRate(commissions.get(i).getActualRate());
							commissions.get(j+1).setActualRate(commissions.get(i).getActualRate().add(commissions.get(j+1).getActualRate()));
							break;
						}
					}
				}

			}
			ordRouteDatas.add(ordRouteData);
		}
		return ordRouteDatas;
	}


	/**
	 * 递归计算日期
	 *
	 * @param Date 日期
	 * @param int  休息日天数或要计算的天数
	 */
	/*public static Date recursionCalculateDate(Date baseDate,int holidays){
		if(holidays <= 0)return baseDate;
		//一天的毫秒数
		Long dayMinus = (long) (24*60*60*1000);
		//获取基础日期的毫秒数
		Long baseDateNumber = baseDate.getTime();
		//计算基础日期加上天数
		Long finalDateNumber = baseDateNumber+holidays*dayMinus;
		//计算
		holidays = 0;
		for(long i=baseDateNumber+dayMinus;i<=finalDateNumber;i=i+dayMinus){
			//如果日期为周末，天数加1
			if(DateUtil.isholiday(i)){
				holidays++;
			}
		}
		Date result = new Date(finalDateNumber);
		if(holidays>0){
			result = recursionCalculateDate(result,holidays);
		}
		return result;
	}*/


	/**
	 * 批量发送通知
	 * add by 谈晟
	 *
	 * @param list
	 */
	private void sendNotice(List<FetChannelCheck> list,IRequest iRequest) {
		Map<String, String> sendNoticeMap = null;
		Set<String> sendNoticeSet = new HashSet<>();
		/* 获取状态为已审核的渠道 */
		CnlChannel channel = new CnlChannel();
		channel.setStatusCode("APPROVED");
		List<CnlChannel> cnlChannelList  = cnlChannelMapper.select(channel);
		List<Long> channelIdList = new ArrayList<>();
		/* 将渠道id存入新List */
		for (CnlChannel cnlChannel : cnlChannelList) {
			channelIdList.add(cnlChannel.getChannelId());
		}
		for (FetChannelCheck fetChannelCheck : list) {
			if (channelIdList.contains(fetChannelCheck.getReceiveCompanyId())){ // 只有已审核的渠道才发送对账通知
				sendNoticeSet.add(String.valueOf(fetChannelCheck.getReceiveCompanyId()) + "-" + fetChannelCheck.getCheckPeriod()); // 剔除重复数据
			}
		}
		for (String s : sendNoticeSet) {
			String[] array = s.split("-");
			Long receiveCompanyId = Long.valueOf(array[0]);
			String checkPeriod = array[1];
			Long userId = queryUserByChannel("CHANNEL",receiveCompanyId);
			sendNoticeMap = new HashMap<>();
			sendNoticeMap.put("checkPeriod", checkPeriod); // 对账期间
			Date now = DateUtil.getNow();
			sendNoticeMap.put("date1", this.addTime(now,3)); // 确认时间
			sendNoticeMap.put("date2", this.addTime(now,4)); // 结算时间
			sendNoticeMap.put("date3", this.addTime(now,9)); // 推迟确认时间
			sendNoticeMap.put("date4", this.addTime(now,10)); // 再次结算时间
			// 发送对账单通知
			ntnNotificationService.sendNotification(iRequest,userId,"DZD0001",sendNoticeMap);
		}
	}

	/**
	 * 查询渠道用户
	 * add by 谈晟
	 *
	 * @param userType
	 * @param channelId
	 * @return
	 */
	private Long queryUserByChannel(String userType, Long channelId) {
		ClbUser user = new ClbUser();
		user.setUserType(userType);
		user.setRelatedPartyId(channelId);
		List<ClbUser> userList = RefelctUtil.getListDataByFields(allUsers,user,"userType","relatedPartyId");
		if (CollectionUtils.isEmpty(userList)) {
			throw new RuntimeException("查询不到渠道用户,channelId:" + channelId);
		} else {
			return userList.get(0).getUserId();
		}
	}

	/**
	 * 增加时间
	 * add by 谈晟
	 *
	 * @return xx月xx日
	 */
	private String addTime(Date baseDate, int days) {
		Date date = DateUtil.getDate(baseDate, 0, 0, days);
		StringBuffer sb = new StringBuffer(String.valueOf(DateUtil.getMonth(date))).append("月").append(DateUtil.getDay(date)).append("日");
		return sb.toString();
	}

	/**
	 * 垃圾回收
	 */
	private void GarbageCollection() {
		productTypeMap = null;
		//基准日期Map
		baseDateTypeMap = null;
		//条件类型Map
		termTypeMap = null;
		//错误信息
		errorMessages = null;
		//所有渠道合约数据
		allChannelContracts = null;
		//所有交易路线信息
		allCommissions = null;
		//所有付款条件
		allCollectionTerms = null;
		//所有利益分配信息
		allContractRoles = null;
		//所有用户信息
		allUsers = null;
		//汇率数据
		allExchangeRateData = null;
		System.gc();
	}
}