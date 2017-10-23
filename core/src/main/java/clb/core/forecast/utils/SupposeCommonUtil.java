package clb.core.forecast.utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import clb.core.forecast.dto.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.hand.hap.system.dto.DTOStatus;

import clb.core.channel.dto.CnlChannel;
import clb.core.channel.mapper.CnlChannelMapper;
import clb.core.common.utils.SpringConfigTool;
import clb.core.common.utils.ImportUtil.ImportMessage;
import clb.core.forecast.threads.QueryThread;
import clb.core.supplier.dto.SpeSupplier;
import clb.core.supplier.mapper.SpeSupplierMapper;

public class SupposeCommonUtil {
	private static Logger log = LoggerFactory.getLogger(SupposeCommonUtil.class);
	
	
	/**
	 * 封装类型列表转应付类型列表
	 */
	public static List<FetChannelCheck> commonListToChannelCheckList(List<FetSupposeCommon> orderDatas){
		List<FetChannelCheck> channelChecks = new ArrayList<>();
		//缓存供应商信息
		HashMap<String,SpeSupplier> supplierMap = new HashMap<>();
		//缓存渠道信息
		HashMap<String,CnlChannel> channelMap = new HashMap<>();
		for(FetSupposeCommon orderData:orderDatas){
			if("CHANNEL".equals(orderData.getReceiveCompanyType()) || "CHANNEL".equals(orderData.getPaymentCompanyType())){
				channelChecks.add(commonToChannelCheck(orderData));
			}
		}
		return channelChecks;
	}
	
	/**
	 * 封装类型转渠道对账类型 
	 */
	public static FetChannelCheck commonToChannelCheck(FetSupposeCommon orderData){
		FetChannelCheck channelCheck = new FetChannelCheck();
		
		BigDecimal amount = orderData.getAmount();
		BigDecimal rate = orderData.getRate();
		BigDecimal exchangeRate = orderData.getExchangeRate();
		String paymentType = orderData.getPaymentType();
		String payCompanyType = orderData.getPaymentCompanyType();
		Long payCompanyId = orderData.getPaymentCompanyId();
		String receiveCompanyType = orderData.getReceiveCompanyType();
		Long receiveCompanyId = orderData.getReceiveCompanyId();
		Long orderId = orderData.getOrderId();
		Long itemId = orderData.getItemId();
		String orderCurrency = orderData.getOrderCurrency();
		Date dueDate = orderData.getDueDate();
		
		//插入渠道对账数据
		channelCheck.setCheckPeriod(orderData.getPeriodName());
		channelCheck.setPaymentType(paymentType);
		channelCheck.setPaymentCompanyType(payCompanyType);
		channelCheck.setPaymentCompanyId(payCompanyId);
		channelCheck.setReceiveCompanyType(receiveCompanyType);
		channelCheck.setReceiveCompanyId(receiveCompanyId);
		channelCheck.setOrderId(orderId);
		channelCheck.setOrderCurrency(orderCurrency);
		channelCheck.setDueDate(dueDate);
		channelCheck.setStatus("DQR");
		channelCheck.setItemId(itemId);
		if(orderData.getVersion() != null && orderData.getVersion() != 0L){
			channelCheck.setVersion(orderData.getVersion());
		}
		else channelCheck.setVersion(1L);
		channelCheck.set__status(DTOStatus.ADD);
		channelCheck.setComments(orderData.getComments());
		
		if(amount != null && rate != null && exchangeRate != null){
			//计算实际付款
			BigDecimal hkdAmount = (amount.multiply(rate)).multiply(exchangeRate);
			//保留四位小数
			hkdAmount.setScale(4,BigDecimal.ROUND_DOWN);
			
			channelCheck.setAmount(amount);
			channelCheck.setRate(rate);
			channelCheck.setExchangeRate(exchangeRate);
			channelCheck.setHkdAmount(hkdAmount);
		}
		return channelCheck;
	}
	
	/**
	 * 封装类型列表转应付类型列表
	 */
	public static List<FetPayable> commonListToPayableList(List<FetSupposeCommon> orderDatas){
		List<FetPayable> payables = new ArrayList<>();
		//缓存供应商信息
		HashMap<String,SpeSupplier> supplierMap = new HashMap<>();
		//缓存渠道信息
		HashMap<String,CnlChannel> channelMap = new HashMap<>();
		for(FetSupposeCommon orderData:orderDatas){
			/*if(param != null){
				if(StringUtils.isNotEmpty(param.getPaymentCompanyType()) && param.getPaymentCompanyId() != null){
					if(!orderData.getPaymentCompanyType().equals(param.getPaymentCompanyType()) || !orderData.getPaymentCompanyId().equals(param.getPaymentCompanyId())){
						continue;
					}
				}
				if(StringUtils.isNotEmpty(param.getReceiveCompanyType()) && param.getReceiveCompanyId() != null){
					if(!orderData.getReceiveCompanyType().equals(param.getReceiveCompanyType()) || !orderData.getReceiveCompanyId().equals(param.getReceiveCompanyId())){
						continue;
					}
				}
			}*/
			payables.add(commonToPayable(orderData));
			
		}
		return payables;
	}
	
	/**
	 * 封装类型转应付类型 
	 */
	public static FetPayable commonToPayable(FetSupposeCommon orderData){
		FetPayable payable = new FetPayable();
		
		BigDecimal amount = orderData.getAmount();
		BigDecimal rate = orderData.getRate();
		BigDecimal exchangeRate = orderData.getExchangeRate();
		String paymentType = orderData.getPaymentType();
		String payCompanyType = orderData.getPaymentCompanyType();
		Long payCompanyId = orderData.getPaymentCompanyId();
		String receiveCompanyType = orderData.getReceiveCompanyType();
		Long receiveCompanyId = orderData.getReceiveCompanyId();
		Long orderId = orderData.getOrderId();
		Long itemId = orderData.getItemId();
		String orderCurrency = orderData.getOrderCurrency();
		Date dueDate = orderData.getDueDate();
		
		//插入应付数据
		payable.setPaymentPeriod(orderData.getPeriodName());
		payable.setPaymentType(paymentType);
		payable.setPaymentCompanyType(payCompanyType);
		payable.setPaymentCompanyId(payCompanyId);
		payable.setReceiveCompanyType(receiveCompanyType);
		payable.setReceiveCompanyId(receiveCompanyId);
		payable.setOrderId(orderId);
		payable.setOrderCurrency(orderCurrency);
		payable.setDueDate(dueDate);
		payable.setItemId(itemId);
		if(orderData.getVersion() != null && orderData.getVersion() != 0L){
			payable.setVersion(orderData.getVersion());
		}
		else payable.setVersion(1L);
		payable.set__status(DTOStatus.ADD);
		payable.setComments(orderData.getComments());
		
		if(amount != null && rate != null && exchangeRate != null){
			//计算实际付款
			BigDecimal hkdAmount = (amount.multiply(rate)).multiply(exchangeRate);
			//保留四位小数
			hkdAmount.setScale(4,BigDecimal.ROUND_DOWN);
			
			payable.setAmount(amount);
			payable.setRate(rate);
			payable.setExchangeRate(exchangeRate);
			payable.setHkdAmount(hkdAmount);
		}
		return payable;
	}
	
	/**
	 * 封装类型列表转应收类型列表
	 */
	public static List<FetReceivable> commonListToReceivableList(List<FetSupposeCommon> orderDatas){
		List<FetReceivable> receivables = new ArrayList<>();
		//缓存供应商信息
		HashMap<String,SpeSupplier> supplierMap = new HashMap<>();
		//缓存渠道信息
		HashMap<String,CnlChannel> channelMap = new HashMap<>();
		for(FetSupposeCommon orderData:orderDatas){
			/*if(param != null){
				if(StringUtils.isNotEmpty(param.getPaymentCompanyType()) && param.getPaymentCompanyId() != null){
					if(!orderData.getPaymentCompanyType().equals(param.getPaymentCompanyType()) || !orderData.getPaymentCompanyId().equals(param.getPaymentCompanyId())){
						continue;
					}
				}
				if(StringUtils.isNotEmpty(param.getReceiveCompanyType()) && param.getReceiveCompanyId() != null){
					if(!orderData.getReceiveCompanyType().equals(param.getReceiveCompanyType()) || !orderData.getReceiveCompanyId().equals(param.getReceiveCompanyId())){
						continue;
					}
				}
			}*/
			receivables.add(commonToReceiveable(orderData));
		}
		return receivables;
	}
	
	/**
	 * 封装类型转应收类型 
	 */
	public static FetReceivable commonToReceiveable(FetSupposeCommon orderData){
		FetReceivable receivable = new FetReceivable();
		
		BigDecimal amount = orderData.getAmount();
		BigDecimal rate = orderData.getRate();
		BigDecimal exchangeRate = orderData.getExchangeRate();
		String paymentType = orderData.getPaymentType();
		String payCompanyType = orderData.getPaymentCompanyType();
		Long payCompanyId = orderData.getPaymentCompanyId();
		String receiveCompanyType = orderData.getReceiveCompanyType();
		Long receiveCompanyId = orderData.getReceiveCompanyId();
		Long orderId = orderData.getOrderId();
		Long itemId = orderData.getItemId();
		String orderCurrency = orderData.getOrderCurrency();
		Date dueDate = orderData.getDueDate();
		
		//插入应付数据
		receivable.setReceiptPeriod(orderData.getPeriodName());
		receivable.setReceiptType(paymentType);
		receivable.setPaymentCompanyType(payCompanyType);
		receivable.setPaymentCompanyId(payCompanyId);
		receivable.setReceiveCompanyType(receiveCompanyType);
		receivable.setReceiveCompanyId(receiveCompanyId);
		receivable.setOrderId(orderId);
		receivable.setOrderCurrency(orderCurrency);
		receivable.setDueDate(dueDate);
		receivable.setItemId(itemId);
		if(orderData.getVersion() != null && orderData.getVersion() != 0L){
			receivable.setVersion(orderData.getVersion());
		} else {
			receivable.setVersion(1L);
		}
		receivable.set__status(DTOStatus.ADD);
		receivable.setComments(orderData.getComments());
		
		if(amount != null && rate != null && exchangeRate != null){
			//计算实际付款
			BigDecimal hkdAmount = (amount.multiply(rate)).multiply(exchangeRate);
			//保留四位小数
			hkdAmount.setScale(4,BigDecimal.ROUND_DOWN);
			
			receivable.setAmount(amount);
			receivable.setRate(rate);
			receivable.setExchangeRate(exchangeRate);
			receivable.setHkdAmount(hkdAmount);
		}
		return receivable;
	}
	
	/**
	 * 封装类型列表转应收类型列表
	 */
	public static List<FetTimeReceivable> commonListToTimeReceivableList(List<FetSupposeCommon> orderDatas){
		List<FetTimeReceivable> receivables = new ArrayList<>();
		for(FetSupposeCommon orderData:orderDatas){
			/*if(param != null){
				if(StringUtils.isNotEmpty(param.getPaymentCompanyType()) && param.getPaymentCompanyId() != null){
					if(!orderData.getPaymentCompanyType().equals(param.getPaymentCompanyType()) || !orderData.getPaymentCompanyId().equals(param.getPaymentCompanyId())){
						continue;
					}
				}
				if(StringUtils.isNotEmpty(param.getReceiveCompanyType()) && param.getReceiveCompanyId() != null){
					if(!orderData.getReceiveCompanyType().equals(param.getReceiveCompanyType()) || !orderData.getReceiveCompanyId().equals(param.getReceiveCompanyId())){
						continue;
					}
				}
			}*/
			receivables.add(commonToTimeReceivable(orderData));
		}
		return receivables;
	}


	/**
	 * 封装类型转应收类型
	 */
	public static FetTimeReceivable commonToTimeReceivable(FetSupposeCommon orderData){
		FetTimeReceivable receivable = new FetTimeReceivable();

		BigDecimal amount = orderData.getAmount();
		BigDecimal rate = orderData.getRate();
		BigDecimal exchangeRate = orderData.getExchangeRate();
		String paymentType = orderData.getPaymentType();
		String payCompanyType = orderData.getPaymentCompanyType();
		Long payCompanyId = orderData.getPaymentCompanyId();
		String receiveCompanyType = orderData.getReceiveCompanyType();
		Long receiveCompanyId = orderData.getReceiveCompanyId();
		Long orderId = orderData.getOrderId();
		Long itemId = orderData.getItemId();
		String orderCurrency = orderData.getOrderCurrency();
		Date dueDate = orderData.getDueDate();

		//插入应付数据
		receivable.setReceiptPeriod(orderData.getPeriodName());
		receivable.setReceiptType(paymentType);
		receivable.setPaymentCompanyType(payCompanyType);
		receivable.setPaymentCompanyId(payCompanyId);
		receivable.setReceiveCompanyType(receiveCompanyType);
		receivable.setReceiveCompanyId(receiveCompanyId);
		receivable.setOrderId(orderId);
		receivable.setOrderCurrency(orderCurrency);
		receivable.setDueDate(dueDate);
		receivable.setItemId(itemId);
		if(orderData.getVersion() != null && orderData.getVersion() != 0L){
			receivable.setVersion(orderData.getVersion());
		}
		else receivable.setVersion(1L);
		receivable.set__status(DTOStatus.ADD);
		receivable.setComments(orderData.getComments());

		if(amount != null && rate != null && exchangeRate != null){
			//计算实际付款
			BigDecimal hkdAmount = (amount.multiply(rate)).multiply(exchangeRate);
			//保留四位小数
			hkdAmount.setScale(4,BigDecimal.ROUND_DOWN);

			receivable.setAmount(amount);
			receivable.setRate(rate);
			receivable.setExchangeRate(exchangeRate);
			receivable.setHkdAmount(hkdAmount);
		}
		return receivable;
	}

	/*public static boolean valiDateStatus(FetSupposeCommon orderData,HashMap<String,SpeSupplier> supplierMap,HashMap<String,CnlChannel> channelMap){
		CnlChannel receiveChannel = new CnlChannel();
		CnlChannel paymentChannel = new CnlChannel();
		SpeSupplier receiveSupplier = new SpeSupplier();
		SpeSupplier paymentSupplier = new SpeSupplier();
		
		String receiveStatus = "";
		String paymentStatus = "";
		//获取SqlSessionFactory
		SqlSessionFactory sqlSessionFactory = (SqlSessionFactory) SpringConfigTool.getBean("sqlSessionFactory");
		//开启SqlSession
		SqlSession sqlSession = sqlSessionFactory.openSession();
		if("CHANNEL".equals(orderData.getReceiveCompanyType())){
			String key = "CHANNEL&"+orderData.getReceiveCompanyId();
			if(channelMap.get(key) != null){
				receiveChannel = channelMap.get(key);
			}else{
				receiveChannel.setChannelId(orderData.getReceiveCompanyId());
				receiveChannel = sqlSession.selectOne("clb.core.channel.mapper.CnlChannelMapper.selectByPrimaryKey",receiveChannel);
				channelMap.put(key,receiveChannel);
			}
			receiveStatus = receiveChannel.getStatusCode();
		}
		if("CHANNEL".equals(orderData.getPaymentCompanyType())){
			String key = "CHANNEL&"+orderData.getPaymentCompanyId();
			if(channelMap.get(key) != null){
				paymentChannel = channelMap.get(key);
			}else{
				paymentChannel.setChannelId(orderData.getPaymentCompanyId());
				paymentChannel = sqlSession.selectOne("clb.core.channel.mapper.CnlChannelMapper.selectByPrimaryKey",paymentChannel);
				channelMap.put(key,paymentChannel);
			}
			paymentStatus = paymentChannel.getStatusCode();
		}
		if("SUPPLIER".equals(orderData.getReceiveCompanyType())){
			String key = "SUPPLIER&"+orderData.getReceiveCompanyId();
			if(supplierMap.get(key) != null){
				receiveSupplier = supplierMap.get(key);
			}else{
				receiveSupplier.setSupplierId(orderData.getReceiveCompanyId());
				receiveSupplier = sqlSession.selectOne("clb.core.supplier.mapper.SpeSupplierMapper.selectByPrimaryKey",receiveSupplier);
				supplierMap.put(key,receiveSupplier);
			}
			receiveStatus = receiveSupplier.getStatusCode();
		}
		if("SUPPLIER".equals(orderData.getPaymentCompanyType())){
			String key = "SUPPLIER&"+orderData.getPaymentCompanyId();
			if(supplierMap.get(key) != null){
				paymentSupplier = supplierMap.get(key);
			}else{
				paymentSupplier.setSupplierId(orderData.getPaymentCompanyId());
				paymentSupplier = sqlSession.selectOne("clb.core.supplier.mapper.SpeSupplierMapper.selectByPrimaryKey",paymentSupplier);
				supplierMap.put(key,paymentSupplier);
			}
			paymentStatus = paymentSupplier.getStatusCode();
		}
		
		if("APPROVED".equals(receiveStatus) && "APPROVED".equals(paymentStatus)){
			return true;
		}
		return false;
	}*/
	
}
