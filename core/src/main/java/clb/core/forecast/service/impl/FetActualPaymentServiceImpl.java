package clb.core.forecast.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.common.exceptions.CommonException;
import clb.core.common.utils.ExceptionUtil;
import clb.core.forecast.dto.FetActualPayment;
import clb.core.forecast.dto.FetActualPaymentSummary;
import clb.core.forecast.mapper.FetActualPaymentMapper;
import clb.core.forecast.service.IFetActualPaymentService;
import clb.core.forecast.service.IFetActualPaymentSummaryService;  
@Service
@Transactional
public class FetActualPaymentServiceImpl extends BaseServiceImpl<FetActualPayment> implements IFetActualPaymentService{
	private static Logger log = LoggerFactory.getLogger(FetActualPaymentServiceImpl.class);
	
	@Autowired
    @Qualifier("sqlSessionFactory")
    private SqlSessionFactory sqlSessionFactory;
	@Autowired
	private FetActualPaymentMapper actualPaymentMapper;
	@Autowired
	private IFetActualPaymentSummaryService actualPaymentSummaryService;
	@Override
	public List<FetActualPayment> getData(IRequest iRequest,FetActualPayment dto,int page,int pagesize) {
		if(pagesize != 0)PageHelper.startPage(page,pagesize);
		List<FetActualPayment> data = actualPaymentMapper.getData(dto);
		for(FetActualPayment d:data){
    		//签单费或刷卡费时，订单金额为负值
    		if(d.getPaymentType().equals("QDF") || d.getPaymentType().equals("SKF")){
    			//如果金额大于0，变成负数
    			if(d.getAmount().compareTo(new BigDecimal(0)) > 0){
    				d.setAmount(new BigDecimal(0).subtract(d.getAmount()));
    			}
    		}
    	}
		/*  ----计算当前页面----  */
		//计算总共数据
		int total = (int) ((Page<?>) data).getTotal();
		//计算最后一页的页数
		int totalPage = (total%pagesize == 0)?total/pagesize:(total/pagesize)+1;
		//最后一页
		if(page == totalPage){
			//获取所有数据
			List<FetActualPayment> allData = new ArrayList<FetActualPayment>();
			if(totalPage == 1 ){
				//获取所有数据
				allData = data;
			}else{
				allData = actualPaymentMapper.getData(dto);
			}
			FetActualPayment summaryData = new FetActualPayment();
			BigDecimal summaryHkd = new BigDecimal(0);
			for(FetActualPayment d:allData){
				summaryHkd = summaryHkd.add(d.getHkdAmount());
			}
			summaryData.setHkdAmount(summaryHkd);
			data.add(summaryData);
		}
		return data;
	}
	
	@Override
	public List<FetActualPayment> fetActualPaymentBatchUpdate(IRequest iRequest,List<FetActualPayment> data) throws CommonException {
		if(CollectionUtils.isNotEmpty(data)){
			FetActualPayment param = new FetActualPayment();
			FetActualPayment paymentParam = new FetActualPayment();
			//查询汇总数据
			FetActualPaymentSummary paymentSummary = new FetActualPaymentSummary();
			paymentSummary.setPaymentSummaryId(data.get(0).getPaymentSummaryId());
			paymentSummary = actualPaymentSummaryService.selectByPrimaryKey(iRequest,paymentSummary);
			BigDecimal hkdAmount = new BigDecimal(0);
			for(FetActualPayment d:data){
				/* ----校验key值是否重复---- */
				if(d.getPaymentId() == null)d.setPaymentId(0L);
				//设置参数并查询
				//头表Id
				param.setPaymentSummaryId(d.getPaymentSummaryId());
				//付款类型
				param.setPaymentType(d.getPaymentType());
				//订单号
				param.setOrderId(d.getOrderId());
				//产品Id
				param.setItemId(d.getItemId());
				param = actualPaymentMapper.selectOne(param);
				if(d.get__status().equals(DTOStatus.ADD)){
					//如果查到值，说明不唯一，报错
					if(param != null){
						throw new CommonException("FET","创建失败：存在重复值",null);
					}
					try{
						d.setCreatedBy(iRequest.getUserId());
						d.setLastUpdatedBy(iRequest.getUserId());
						d.setLastUpdateLogin(iRequest.getUserId());
						d.setCreationDate(new Date());
						d.setLastUpdateDate(new Date());
						this.insertSelective(iRequest,d);
					}catch(Exception e){
						int type = ExceptionUtil.getExceptionType(e);
						if(type == 1){
							throw new CommonException("FET","创建失败：存在重复值",null);
						}else{
							throw e;
						}
					}
					
				}else if(d.get__status().equals(DTOStatus.UPDATE)){
					if(param != null && !param.get_token().equals(d.get_token())){
						throw new CommonException("FET","更新失败：存在重复值",null);
					}
					d.setLastUpdatedBy(iRequest.getUserId());
					d.setLastUpdateLogin(iRequest.getUserId());
					d.setLastUpdateDate(new Date());
					this.updateByPrimaryKeySelective(iRequest,d);
				}
				param = new FetActualPayment();
			}
			//获取所有数据,用于计算
			paymentParam.setPaymentSummaryId(data.get(0).getPaymentSummaryId());
			List<FetActualPayment> hkdDatas = actualPaymentMapper.select(paymentParam);
			for(FetActualPayment hkdData:hkdDatas){
				hkdAmount = hkdAmount.add(hkdData.getHkdAmount());
			}
			paymentSummary.setHkdAmount(hkdAmount);
			actualPaymentSummaryService.updateByPrimaryKey(iRequest,paymentSummary);
		}
		return data;
	}

	
}