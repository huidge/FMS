package clb.core.forecast.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.forecast.dto.FetActualPayment;

public interface FetActualPaymentMapper extends Mapper<FetActualPayment>{
	
	List<FetActualPayment> getData(FetActualPayment dto);
	
	List<FetActualPayment> summaryQuery(FetActualPayment dto);

}