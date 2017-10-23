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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.common.exceptions.CommonException;
import clb.core.common.utils.ExceptionUtil;
import clb.core.forecast.dto.FetActualReceipt;
import clb.core.forecast.dto.FetActualReceiptSummary;
import clb.core.forecast.mapper.FetActualReceiptMapper;
import clb.core.forecast.service.IFetActualReceiptService;
import clb.core.forecast.service.IFetActualReceiptSummaryService;

@Service
@Transactional
public class FetActualReceiptServiceImpl extends BaseServiceImpl<FetActualReceipt> implements IFetActualReceiptService{

	private static Logger log = LoggerFactory.getLogger(FetActualReceiptServiceImpl.class);
	
	@Autowired
    @Qualifier("sqlSessionFactory")
    private SqlSessionFactory sqlSessionFactory;
	@Autowired
	private FetActualReceiptMapper actualReceiptMapper;
	@Autowired
	private IFetActualReceiptSummaryService actualReceiptSummaryService;
	@Override
	public List<FetActualReceipt> getData(IRequest iRequest,FetActualReceipt dto,int page,int pagesize) {
		if(pagesize != 0)PageHelper.startPage(page,pagesize);
		List<FetActualReceipt> data = actualReceiptMapper.getData(dto);
		for(FetActualReceipt d:data){
    		//签单费或刷卡费时，订单金额为负值
    		if(d.getReceiptType().equals("QDF") || d.getReceiptType().equals("SKF")){
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
			List<FetActualReceipt> allData = new ArrayList<FetActualReceipt>();
			if(totalPage == 1 ){
				//获取所有数据
				allData = data;
			}else{
				allData = actualReceiptMapper.getData(dto);
			}
			FetActualReceipt summaryData = new FetActualReceipt();
			BigDecimal summaryHkd = new BigDecimal(0);
			for(FetActualReceipt d:allData){
				summaryHkd = summaryHkd.add(d.getHkdAmount());
			}
			summaryData.setHkdAmount(summaryHkd);
			data.add(summaryData);
		}
		return data;
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<FetActualReceipt> fetActualReceiptBatchUpdate(IRequest iRequest,List<FetActualReceipt> data) throws CommonException {
		if(CollectionUtils.isNotEmpty(data)){
			FetActualReceipt param = new FetActualReceipt();
			FetActualReceipt receiptParam = new FetActualReceipt();
			//查询汇总数据
			FetActualReceiptSummary fetActualReceiptSummary = new FetActualReceiptSummary();
			fetActualReceiptSummary.setReceiptSummaryId(data.get(0).getReceiptSummaryId());
			fetActualReceiptSummary = actualReceiptSummaryService.selectByPrimaryKey(iRequest, fetActualReceiptSummary);
			BigDecimal hkdAmount = new BigDecimal(0);
			for(FetActualReceipt d:data){
				/* ----校验key值是否重复---- */
				if(d.getReceiptId() == null)d.setReceiptId(0L);
				//设置参数并查询
				//头表Id
				param.setReceiptSummaryId(d.getReceiptSummaryId());
				//收款类型
				param.setReceiptType(d.getReceiptType());
				//订单号
				param.setOrderId(d.getOrderId());
				//产品Id
				param.setItemId(d.getItemId());
				param = actualReceiptMapper.selectOne(param);
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
				param = new FetActualReceipt();
			}
			//获取所有数据,用于计算
			receiptParam.setReceiptSummaryId(data.get(0).getReceiptSummaryId());
			List<FetActualReceipt> hkdDatas = actualReceiptMapper.select(receiptParam);
			for(FetActualReceipt hkdData:hkdDatas){
				hkdAmount = hkdAmount.add(hkdData.getHkdAmount());
			}
			fetActualReceiptSummary.setHkdAmount(hkdAmount);
			actualReceiptSummaryService.updateByPrimaryKey(iRequest, fetActualReceiptSummary);
		}
		return data;
	}

	
	
}