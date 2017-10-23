package clb.core.forecast.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.attachment.service.ISysFileService;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import clb.core.channel.dto.CnlChannel;
import clb.core.channel.mapper.CnlChannelMapper;
import clb.core.common.exceptions.CommonException;
import clb.core.common.utils.ExceptionUtil;
import clb.core.common.utils.ImportUtil;
import clb.core.common.utils.ImportUtil.ImportMessage;
import clb.core.forecast.dto.FetActualPayment;
import clb.core.forecast.dto.FetActualPaymentSummary;
import clb.core.forecast.dto.FetPeriod;
import clb.core.forecast.dto.FetPeriodHead;
import clb.core.forecast.mapper.FetActualPaymentSummaryMapper;
import clb.core.forecast.mapper.FetPeriodHeadMapper;
import clb.core.forecast.mapper.FetPeriodMapper;
import clb.core.forecast.service.IFetActualPaymentSummaryService;
import clb.core.forecast.utils.UpdateThreadHelper;
import clb.core.order.dto.OrdOrder;
import clb.core.order.mapper.OrdOrderMapper;
import clb.core.production.dto.PrdItems;
import clb.core.production.mapper.PrdItemsMapper;
import clb.core.supplier.dto.SpeSupplier;
import clb.core.supplier.exceptions.SpeException;
import clb.core.supplier.mapper.SpeSupplierMapper;
import clb.core.system.dto.ClbCodeValue;
import clb.core.system.service.IClbCodeService;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class FetActualPaymentSummaryServiceImpl extends BaseServiceImpl<FetActualPaymentSummary> implements IFetActualPaymentSummaryService{

	private static Logger log = LoggerFactory.getLogger(FetActualPaymentSummaryServiceImpl.class);
	
	
	private static final String SUCCESS_STATUS = "true";
	
	private static final String ERROR_STATUS = "false";
	
	private static boolean isRepeat = false;
	
	@Autowired
    @Qualifier("sqlSessionFactory")
    private SqlSessionFactory sqlSessionFactory;
	@Autowired
	private IClbCodeService clbCodeService;
	@Autowired
	private OrdOrderMapper orderMapper;
	@Autowired
	private PrdItemsMapper itemsMapper;
	@Autowired
	private MessageSource messageSource;
	@Autowired
	private FetActualPaymentSummaryMapper actualPaymentSummaryMapper;
	@Autowired
	private CnlChannelMapper channelMapper;
	@Autowired
	private SpeSupplierMapper supplierMapper;
	@Autowired
	private FetPeriodHeadMapper periodHeadMapper;
	@Autowired
	private FetPeriodMapper periodMapper;
	
	@Autowired
	private ISysFileService fileService;
	
	@Override
	public List<FetActualPaymentSummary> getData(IRequest iRequest, FetActualPaymentSummary dto, int page,
			int pagesize) {
		if(pagesize != 0)PageHelper.startPage(page,pagesize);
		List<FetActualPaymentSummary> dtos = actualPaymentSummaryMapper.getData(dto);
		//设置token
		for(FetActualPaymentSummary d:dtos){
			if(d.getCertificateFileId() != null){
				Long fileId = d.getCertificateFileId();
				SysFile file = fileService.selectByPrimaryKey(iRequest,fileId);
				d.setFileToken(file.get_token());
			}
		}
		return dtos;
	}

	@Override
	public List<FetActualPaymentSummary> actualPaymentSummaryBatchUpdate(IRequest iRequest,
			List<FetActualPaymentSummary> dtos) {
		//删除旧文件
		for(FetActualPaymentSummary dto:dtos){
			if(dto.getOldFileId() != null){
				Long fileId = dto.getOldFileId();
				SysFile file = fileService.selectByPrimaryKey(iRequest,fileId);
				fileService.delete(iRequest,file);
			}
		}
		try{
			self().batchUpdate(iRequest,dtos);
		}catch(Exception e){
			if(ExceptionUtil.getExceptionType(e) == 1){
				isRepeat = true;
				throw new RuntimeException(new CommonException("FET","数据重复，操作失败!",null));
			}else{
				throw new RuntimeException(new CommonException("FET","更新失败,请联系管理员!",null));
			}
		}
		return dtos;
	}
	
	/**
	 * 从Excel表格中读取行信息 
	 * @param File Excel文件
	 * @param String 文件类型
	 * @throws SpeException 
	 */
	public List<ImportMessage> getLineData(File files,String fileType,HttpServletRequest request) throws CommonException{
		Map<String,String> mapping = new HashMap<>();
		mapping = ImportUtil.getMapping(FetActualPaymentSummary.class,request,messageSource);
		return ImportUtil.getData(FetActualPaymentSummary.class,files,mapping,fileType);
	}

	@Override
	public List<ImportMessage> loadExcel(IRequest iRequest, HttpServletRequest request, MultipartFile files)
			throws CommonException {
		Long start = System.currentTimeMillis();
		String fileName = files.getOriginalFilename();
		int index = fileName.lastIndexOf(".");
		String type = fileName.substring(index+1);
		//封装后的Excel数据
		List<ImportMessage> messages = null;
		//读取出来的Excel数据
		List<FetActualPaymentSummary> lineData = new ArrayList<>();
        String filePath=request.getServletContext().getRealPath("/");
        File f = new File(filePath+UUID.randomUUID().toString());
		log.info("文件路径为:"+f.getAbsolutePath());
		try {
        	files.transferTo(f);
        	messages = getLineData(f,type,request);
        	//查询收入类型
			List<ClbCodeValue> paymentTypeValues = clbCodeService.selectCodeValuesByCodeName(iRequest,"FET.PAYMENT_TYPE");
			//查询币种
			List<ClbCodeValue> currencyValues = clbCodeService.selectCodeValuesByCodeName(iRequest,"PUB.CURRENCY");
			//查询收款人类型
			List<ClbCodeValue> receiveCompanyTypes = clbCodeService.selectCodeValuesByCodeName(iRequest,"SPE.PAYMENT_COMPANY_TYPE");
			//查询所有渠道
			List<CnlChannel> cnlChannels = channelMapper.selectAll();
			//查询所有供应商
			List<SpeSupplier> speSuppliers = supplierMapper.selectAll();
			//查询所有订单
			List<OrdOrder> orderDatas = orderMapper.selectAll();
			//查询所有产品
			List<PrdItems> items = itemsMapper.selectAll();
			//查询所有期间
			//期间Map
			Map<String,List<FetPeriod>> periodMap =new HashMap<>();
			FetPeriodHead head = new FetPeriodHead();
			//实付付款方都是供应商
			head.setPartyType("SUPPLIER");
			List<FetPeriodHead> periodHeads = periodHeadMapper.select(head);
			for(FetPeriodHead periodHead:periodHeads){
				FetPeriod period = new FetPeriod();
				period.setHeadId(periodHead.getHeadId());
				List<FetPeriod> periods = periodMapper.select(period);
				if(CollectionUtils.isNotEmpty(periods)){
					String key = periodHead.getPartyType()+"&"+periodHead.getPartyId();
					periodMap.put(key,periods);
				}
			}
			for(ImportMessage message:messages){
				if(ERROR_STATUS.equals(message.getStatus()))continue;
				StringBuffer stringBuffer = new StringBuffer("");
				FetActualPaymentSummary l = (FetActualPaymentSummary)message.getData();
				//设置收入类型标志位
				boolean isPaymentType = false;
				//设置币种标志位
				boolean isOrderCurrency = false;
				//设置收款人类型标志位
				boolean isReceiveCompanyType = false;
				//设置收款人标志位
				boolean isReceiveCompanyName = false;
				//设置付款人标志位
				boolean isPaymentSupplier = false;
				//设置订单标志位
				boolean isOrder = false;
				//设置产品名称标志位
				boolean isItem = false;
				//设置期间标志位
				boolean isPeriod = false;
				//处理收入类型
				String receiptType = l.getPaymentType();
				for(ClbCodeValue value:paymentTypeValues){
					if(value.getMeaning().equals(receiptType)
					|| value.getValue().equals(receiptType)){
						l.setPaymentType(value.getValue());
						isPaymentType = true;
						break;
					}
				}
				if(!isPaymentType){
					stringBuffer.append("付款类型:"+l.getPaymentType()+"不存在！");
					message.setStatus(ERROR_STATUS);
					message.setErrorMessage(stringBuffer.toString());
					continue;
				}
				//处理币种
				String currency = l.getOrderCurrency();
				for(ClbCodeValue value:currencyValues){
					if(value.getMeaning().equals(currency)
					|| value.getValue().equals(currency)){
						l.setOrderCurrency(value.getValue());
						isOrderCurrency = true;
						break;
					}
				}
				if(!isOrderCurrency){
					stringBuffer.append("币种:"+l.getOrderCurrency()+"不存在！");
					message.setStatus(ERROR_STATUS);
					message.setErrorMessage(stringBuffer.toString());
					continue;
				}
				//处理收款人类型
				String receiveCompanyType = l.getReceiveCompanyType();
				for(ClbCodeValue value:receiveCompanyTypes){
					if(value.getMeaning().equals(receiveCompanyType)
					|| value.getValue().equals(receiveCompanyType)){
						l.setReceiveCompanyType(value.getValue());
						isReceiveCompanyType = true;
						break;
					}
				}
				if(!isReceiveCompanyType){
					stringBuffer.append("收款人类型:"+l.getReceiveCompanyType()+"不存在！");
					message.setStatus(ERROR_STATUS);
					message.setErrorMessage(stringBuffer.toString());
					continue;
				}
				//处理收款人
				String receiveCompanyName = l.getReceiveCompanyName();
				//如果是供应商
				if("SUPPLIER".equals(l.getReceiveCompanyType())){
					for(SpeSupplier supplier:speSuppliers){
						if(supplier.getName().equals(receiveCompanyName)){
							l.setReceiveCompanyId(supplier.getSupplierId());
							isReceiveCompanyName = true;
						}
						if(isReceiveCompanyName)break;
					}
					if(!isReceiveCompanyName){
						stringBuffer.append("供应商:"+l.getReceiveCompanyName()+"不存在！");
						message.setStatus(ERROR_STATUS);
						message.setErrorMessage(stringBuffer.toString());
						continue;
					}
				}else{
					for(CnlChannel channel:cnlChannels){
						if(channel.getChannelName().equals(receiveCompanyName)){
							l.setReceiveCompanyId(channel.getChannelId());
							isReceiveCompanyName = true;
						}
						if(isReceiveCompanyName)break;
					}
					if(!isReceiveCompanyName){
						stringBuffer.append("渠道:"+l.getReceiveCompanyName()+"不存在！");
						message.setStatus(ERROR_STATUS);
						message.setErrorMessage(stringBuffer.toString());
						continue;
					}
				}
				//处理付款人
				String paymentSupplierName = l.getPaymentSupplierName();
				for(SpeSupplier supplier:speSuppliers){
					if(supplier.getName().equals(paymentSupplierName)){
						l.setPaymentSupplierId(supplier.getSupplierId());
						isPaymentSupplier = true;
					}
					if(isPaymentSupplier)break;
				}
				
				if(!isPaymentSupplier){
					stringBuffer.append("供应商:"+l.getPaymentSupplierName()+"不存在！");
					message.setStatus(ERROR_STATUS);
					message.setErrorMessage(stringBuffer.toString());
					continue;
				}
				//处理期间
				String periodName = l.getPaymentPeriod();
				String key = "SUPPLIER"+"&"+l.getPaymentSupplierId();
				List<FetPeriod> periods = periodMap.get(key);
				if(CollectionUtils.isNotEmpty(periods)){
					for(FetPeriod period:periods){
						if(period.getPeriodName().equals(periodName)){
							isPeriod = true;
							break;
						}
					}
				}
				if(!isPeriod){
					stringBuffer.append("付款方："+paymentSupplierName+"的期间:"+l.getPaymentPeriod()+"不存在！");
					message.setStatus(ERROR_STATUS);
					message.setErrorMessage(stringBuffer.toString());
					continue;
				}
				//处理订单编号和结算日期
				for(OrdOrder orderData:orderDatas){
					if(orderData.getOrderNumber().equals(l.getOrderNumber())){
						l.setOrderId(orderData.getOrderId());
						isOrder = true;
						if(StringUtils.isEmpty(l.getComments())){
							if(StringUtils.isNotEmpty(orderData.getPolicyNumber())){
								l.setComments(orderData.getPolicyNumber());
							}else{
								l.setComments("");
							}
						}
						break;
					}
				}
				if(!isOrder){
					stringBuffer.append("订单编号:"+l.getOrderNumber()+"不存在！");
					message.setStatus(ERROR_STATUS);
					message.setErrorMessage(stringBuffer.toString());
					continue;
				}
				//处理产品名称
				for(PrdItems item:items){
					if(item.getItemName().equals(l.getItemName())){
						l.setItemId(item.getItemId());
						isItem = true;
						break;
					}
				}
				if(!isItem){
					stringBuffer.append("产品名称:"+l.getItemName()+"不存在！");
					message.setStatus(ERROR_STATUS);
					message.setErrorMessage(stringBuffer.toString());
					continue;
				}
				//订单价格
				BigDecimal amount  = l.getAmount();
				//费率
				BigDecimal rate = l.getRate();
				//汇率
				BigDecimal exchangeRate = l.getExchangeRate();
				if(amount == null || rate == null || exchangeRate == null){
					stringBuffer.append("订单价格，费率，汇率不能为空！");
					message.setStatus(ERROR_STATUS);
					message.setErrorMessage(stringBuffer.toString());
					continue;
				}
				//签单费和刷卡费时，订单金额为负值
	    		if(l.getPaymentType().equals("QDF") || l.getPaymentType().equals("SKF")){
	    			//如果金额大于0，变成负数
	    			if(amount.compareTo(new BigDecimal(0)) > 0){
	    				amount = new BigDecimal(0).subtract(amount);
	    			}
	    		}
				//计算实际付款
				BigDecimal hkdAmount = (amount.multiply(rate)).multiply(exchangeRate);
				//保留四位小数
				l.setHkdAmount(hkdAmount.setScale(4,BigDecimal.ROUND_DOWN));
				
				
				lineData.add(l);
				//判断是否有重复数据
				if(!ImportUtil.validateUnique(FetActualPaymentSummary.class,lineData,l,"paymentPeriod","receiveCompanyType","receiveCompanyId","paymentSupplierId","paymentType","orderId","itemId")){
					stringBuffer.append("该条数据与上面的数据重复;");
					message.setStatus(ERROR_STATUS);
					message.setErrorMessage(stringBuffer.toString());
					continue;
				}
			}
			log.info("加载完成，总共耗时："+(System.currentTimeMillis()-start)/1000+"秒");
			return this.uploadData(iRequest,messages);
			
		} catch (Exception e) {
			if(isRepeat){
				throw new RuntimeException(new CommonException("FET","数据重复，操作失败!",null));
			}
			if(e instanceof CommonException){
				CommonException e1 = (CommonException)e;
				throw e1;
			}else{
				log.error("导入失败",e);
				throw new CommonException("FET","导入失败！",null);
			}
		}finally {
			f.delete();
		}
	}

	@Override
	public List<ImportMessage> uploadData(IRequest iRequest, List<ImportMessage> messages) throws CommonException {
		// TODO Auto-generated method stub
		//插入分类表
		Map<String,List<ImportMessage>> classifiedData = getClassifiedData(messages);
		//获取分类数据
		Set<String> keySet = classifiedData.keySet();
		//汇总数据
		List<FetActualPaymentSummary> summaryData = new ArrayList<>();
		for(Iterator it = keySet.iterator(); it.hasNext();){
			String key = it.next().toString();
			FetActualPaymentSummary fetActualPaymentSummary = new FetActualPaymentSummary();
			String[] param = key.split("&");
			fetActualPaymentSummary.setPaymentPeriod(param[0]);
			fetActualPaymentSummary.setReceiveCompanyType(param[1]);
			fetActualPaymentSummary.setReceiveCompanyId(Long.valueOf(param[2]));
			fetActualPaymentSummary.setPaymentSupplierId(Long.valueOf(param[3]));
			List<FetActualPaymentSummary> summaries = actualPaymentSummaryMapper.getData(fetActualPaymentSummary);
			if(CollectionUtils.isNotEmpty(summaries)){
				fetActualPaymentSummary.setVersion(summaries.get(0).getVersion()+1);
			}
			List<ImportMessage> payments = classifiedData.get(key);
			BigDecimal hkdAmount = new BigDecimal(0);
			for(ImportMessage payment:payments){
				FetActualPayment dataRecrive = (FetActualPayment) payment.getData();
				hkdAmount = hkdAmount.add(dataRecrive.getHkdAmount());
			}
			fetActualPaymentSummary.setHkdAmount(hkdAmount);
			fetActualPaymentSummary.set__status(DTOStatus.ADD);
			summaryData.add(fetActualPaymentSummary);
		}
		this.actualPaymentSummaryBatchUpdate(iRequest,summaryData);
		for(FetActualPaymentSummary summary:summaryData){
			Date now = summary.getCreationDate();
			String key = summary.getPaymentPeriod()+"&"+summary.getReceiveCompanyType()+"&"+summary.getReceiveCompanyId()+"&"+summary.getPaymentSupplierId();
			List<ImportMessage> payments = classifiedData.get(key);
			if(CollectionUtils.isNotEmpty(payments)){
				for(ImportMessage payment:payments){
					FetActualPayment dataPayment = (FetActualPayment) payment.getData();
					dataPayment.setPaymentSummaryId(summary.getPaymentSummaryId());
					dataPayment.setCreatedBy(iRequest.getUserId());
					dataPayment.setLastUpdatedBy(iRequest.getUserId());
					dataPayment.setLastUpdateLogin(iRequest.getUserId());
					dataPayment.setCreationDate(now);
					dataPayment.setLastUpdateDate(now);
					payment.setData(dataPayment);
				}
			}
		}
		try {
			UpdateThreadHelper.startThread(sqlSessionFactory,"clb.core.forecast.mapper.FetActualPaymentMapper.updateByPrimaryKeySelective","clb.core.forecast.mapper.FetActualPaymentMapper.insertSelective",messages,10);
		} catch (Exception e) {
			if(e instanceof CommonException){
				CommonException e1 = (CommonException)e;
				throw e1;
			}else{
				log.error("导入失败",e);
				throw new CommonException("FET","导入失败！",null);
			}
		}
		return messages;
	}
	
	/**
	 * 分类数据 
	 */
	public Map<String,List<ImportMessage>> getClassifiedData(List<ImportMessage> data){
		Map<String,List<ImportMessage>> resData = new HashMap<>();
		List<ImportMessage> classifictions = new ArrayList<>();
		for(ImportMessage d:data){
			if(ERROR_STATUS.equals(d.getStatus()))continue;
			FetActualPaymentSummary dto = (FetActualPaymentSummary)d.getData();
			String key = dto.getPaymentPeriod()+"&"+dto.getReceiveCompanyType()+"&"+dto.getReceiveCompanyId()+"&"+dto.getPaymentSupplierId();
			classifictions = resData.get(key);
			if(classifictions == null){
				classifictions = new ArrayList<>();
				resData.put(key, classifictions);
			}
			FetActualPayment payment = new FetActualPayment();
			payment.setPaymentType(dto.getPaymentType());
			payment.setOrderId(dto.getOrderId());
			payment.setOrderCurrency(dto.getOrderCurrency());
			payment.setAmount(dto.getAmount());
			payment.setRate(dto.getRate());
			payment.setExchangeRate(dto.getExchangeRate());
			payment.setHkdAmount(dto.getHkdAmount());
			payment.setActualDate(dto.getActualDate());
			payment.setComments(dto.getComments());
			payment.setItemId(dto.getItemId());
			d.setData(payment);
			d.setOperateFlag(DTOStatus.ADD);
			classifictions.add(d);
		}
		return resData;
		
	}


}