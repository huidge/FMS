package clb.core.forecast.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.ArrayList;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.github.pagehelper.PageHelper;
import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.attachment.service.ISysFileService;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.common.exceptions.CommonException;
import clb.core.common.utils.ExceptionUtil;
import clb.core.common.utils.ImportUtil;
import clb.core.common.utils.ImportUtil.ImportMessage;
import clb.core.forecast.dto.FetActualReceipt;
import clb.core.forecast.dto.FetActualReceiptSummary;
import clb.core.forecast.dto.FetPeriod;
import clb.core.forecast.dto.FetPeriodHead;
import clb.core.forecast.mapper.FetActualReceiptSummaryMapper;
import clb.core.forecast.mapper.FetPeriodHeadMapper;
import clb.core.forecast.mapper.FetPeriodMapper;
import clb.core.forecast.service.IFetActualReceiptSummaryService;
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

@Service
@Transactional
public class FetActualReceiptSummaryServiceImpl extends BaseServiceImpl<FetActualReceiptSummary> implements IFetActualReceiptSummaryService{

	private static Logger log = LoggerFactory.getLogger(FetActualReceiptSummaryServiceImpl.class);
	
	
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
	private FetActualReceiptSummaryMapper actualReceiptSummaryMapper;
	@Autowired
	private SpeSupplierMapper supplierMapper;
	@Autowired
	private FetPeriodHeadMapper periodHeadMapper;
	@Autowired
	private FetPeriodMapper periodMapper;
	
	@Autowired
	private ISysFileService fileService;
	
	@Override
	public List<FetActualReceiptSummary> getData(IRequest iRequest,FetActualReceiptSummary dto,int page,int pagesize) {
		if(pagesize != 0)PageHelper.startPage(page,pagesize);
		List<FetActualReceiptSummary> dtos = actualReceiptSummaryMapper.getData(dto);
		//设置token
		for(FetActualReceiptSummary d:dtos){
			if(d.getCertificateFileId() != null){
				Long fileId = d.getCertificateFileId();
				SysFile file = fileService.selectByPrimaryKey(iRequest,fileId);
				d.setFileToken(file.get_token());
			}
		}
		return dtos;
	}

	@Override
	public List<FetActualReceiptSummary> actualReceiptSummaryBatchUpdate(IRequest iRequest,List<FetActualReceiptSummary> dtos) {
		//删除旧文件
		for(FetActualReceiptSummary dto:dtos){
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
		mapping = ImportUtil.getMapping(FetActualReceiptSummary.class,request,messageSource);
		return ImportUtil.getData(FetActualReceiptSummary.class,files,mapping,fileType);
	}

	/**
	 * 加载并校验Excel数据 
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<ImportMessage> loadExcel(IRequest iRequest, HttpServletRequest request, MultipartFile files)
			throws CommonException {
		Long start = System.currentTimeMillis();
		String fileName = files.getOriginalFilename();
		int index = fileName.lastIndexOf(".");
		String type = fileName.substring(index+1);
		//封装后的Excel数据
		List<ImportMessage> messages = null;
		//读取出来的Excel数据
		List<FetActualReceiptSummary> lineData = new ArrayList<>();
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
			//查询所有供应商
			List<SpeSupplier> speSuppliers = supplierMapper.selectAll();
			//查询所有订单
			List<OrdOrder> orderDatas = orderMapper.selectAll();
			//查询所有产品
			List<PrdItems> items = itemsMapper.selectAll();
			//期间Map
			Map<String,List<FetPeriod>> periodMap =new HashMap<>();
			FetPeriodHead head = new FetPeriodHead();
			//实收付款方都是供应商
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
				FetActualReceiptSummary l = (FetActualReceiptSummary)message.getData();
				//设置收入类型标志位
				boolean isReceiptType = false;
				//设置币种标志位
				boolean isOrderCurrency = false;
				//设置收款人标志位
				boolean isReceiveSupplier = false;
				//设置付款人标志位
				boolean isPaymentSupplier = false;
				//设置订单标志位
				boolean isOrder = false;
				//设置产品名称标志位
				boolean isItem = false;
				//设置期间标志位
				boolean isPeriod = false;
				//处理收入类型
				String receiptType = l.getReceiptType();
				for(ClbCodeValue value:paymentTypeValues){
					if(value.getMeaning().equals(receiptType)
					|| value.getValue().equals(receiptType)){
						l.setReceiptType(value.getValue());
						isReceiptType = true;
						break;
					}
				}
				if(!isReceiptType){
					stringBuffer.append("收入类型:"+l.getReceiptType()+"不存在！");
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
				//处理收付款人
				String receiveSupplierName = l.getReceiveSupplierName();
				String paymentSupplierName = l.getPaymentSupplierName();
				for(SpeSupplier supplier:speSuppliers){
					if(supplier.getName().equals(receiveSupplierName)){
						l.setReceiveSupplierId(supplier.getSupplierId());
						isReceiveSupplier = true;
					}
					if(supplier.getName().equals(paymentSupplierName)){
						l.setPaymentSupplierId(supplier.getSupplierId());
						isPaymentSupplier = true;
					}
					if(isReceiveSupplier && isPaymentSupplier)break;
				}
				if(!isReceiveSupplier){
					stringBuffer.append("供应商:"+l.getReceiveSupplierName()+"不存在！");
					message.setStatus(ERROR_STATUS);
					message.setErrorMessage(stringBuffer.toString());
					continue;
				}
				if(!isPaymentSupplier){
					stringBuffer.append("供应商:"+l.getPaymentSupplierName()+"不存在！");
					message.setStatus(ERROR_STATUS);
					message.setErrorMessage(stringBuffer.toString());
					continue;
				}

				//处理期间
				String periodName = l.getReceiptPeriod();
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
					stringBuffer.append("付款方："+paymentSupplierName+"的期间:"+l.getReceiptPeriod()+"不存在！");
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
	    		if(l.getReceiptType().equals("QDF") || l.getReceiptType().equals("SKF")){
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
				if(!ImportUtil.validateUnique(FetActualReceiptSummary.class,lineData,l,"receiptPeriod","receiveSupplierId","paymentSupplierId","receiptType","orderId","itemId")){
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
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<ImportMessage> uploadData(IRequest iRequest, List<ImportMessage> messages)
			throws CommonException {
		// TODO Auto-generated method stub
		//插入分类表
		Map<String,List<ImportMessage>> classifiedData = getClassifiedData(messages);
		//获取分类数据
		Set<String> keySet = classifiedData.keySet();
		//汇总数据
		List<FetActualReceiptSummary> summaryData = new ArrayList<>();
		for(Iterator it = keySet.iterator(); it.hasNext();){
			String key = it.next().toString();
			FetActualReceiptSummary fetActualReceiptSummary = new FetActualReceiptSummary();
			String[] param = key.split("&");
			fetActualReceiptSummary.setReceiptPeriod(param[0]);
			fetActualReceiptSummary.setReceiveSupplierId(Long.valueOf(param[1]));
			fetActualReceiptSummary.setPaymentSupplierId(Long.valueOf(param[2]));
			List<FetActualReceiptSummary> summaries = actualReceiptSummaryMapper.getData(fetActualReceiptSummary);
			if(CollectionUtils.isNotEmpty(summaries)){
				fetActualReceiptSummary.setVersion(summaries.get(0).getVersion()+1);
			}
			List<ImportMessage> receives = classifiedData.get(key);
			BigDecimal hkdAmount = new BigDecimal(0);
			for(ImportMessage receive:receives){
				FetActualReceipt dataRecrive = (FetActualReceipt) receive.getData();
				hkdAmount = hkdAmount.add(dataRecrive.getHkdAmount());
			}
			fetActualReceiptSummary.setHkdAmount(hkdAmount);
			fetActualReceiptSummary.set__status(DTOStatus.ADD);
			summaryData.add(fetActualReceiptSummary);
		}
		this.actualReceiptSummaryBatchUpdate(iRequest,summaryData);
		for(FetActualReceiptSummary summary:summaryData){
			Date now = summary.getCreationDate();
			String key = summary.getReceiptPeriod()+"&"+summary.getReceiveSupplierId()+"&"+summary.getPaymentSupplierId();
			List<ImportMessage> receives = classifiedData.get(key);
			if(CollectionUtils.isNotEmpty(receives)){
				for(ImportMessage receive:receives){
					FetActualReceipt dataRecrive = (FetActualReceipt) receive.getData();
					dataRecrive.setReceiptSummaryId(summary.getReceiptSummaryId());
					dataRecrive.setCreatedBy(iRequest.getUserId());
					dataRecrive.setLastUpdatedBy(iRequest.getUserId());
					dataRecrive.setLastUpdateLogin(iRequest.getUserId());
					dataRecrive.setCreationDate(now);
					dataRecrive.setLastUpdateDate(now);
					receive.setData(dataRecrive);
				}
			}
		}
		try {
			UpdateThreadHelper.startThread(sqlSessionFactory,"clb.core.forecast.mapper.FetActualReceiptMapper.updateByPrimaryKeySelective","clb.core.forecast.mapper.FetActualReceiptMapper.insertSelective",messages,10);
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
			FetActualReceiptSummary dto = (FetActualReceiptSummary)d.getData();
			String key = dto.getReceiptPeriod()+"&"+dto.getReceiveSupplierId()+"&"+dto.getPaymentSupplierId();
			classifictions = resData.get(key);
			if(classifictions == null){
				classifictions = new ArrayList<>();
				resData.put(key, classifictions);
			}
			FetActualReceipt receipt = new FetActualReceipt();
			receipt.setReceiptType(dto.getReceiptType());
			receipt.setOrderId(dto.getOrderId());
			receipt.setOrderCurrency(dto.getOrderCurrency());
			receipt.setAmount(dto.getAmount());
			receipt.setRate(dto.getRate());
			receipt.setExchangeRate(dto.getExchangeRate());
			receipt.setHkdAmount(dto.getHkdAmount());
			receipt.setActualDate(dto.getActualDate());
			receipt.setComments(dto.getComments());
			receipt.setItemId(dto.getItemId());
			d.setData(receipt);
			d.setOperateFlag(DTOStatus.ADD);
			classifictions.add(d);
		}
		return resData;
		
	}

}