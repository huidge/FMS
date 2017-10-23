package clb.core.forecast.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.forecast.dto.FetActualPaymentSummary;

public interface FetActualPaymentSummaryMapper extends Mapper<FetActualPaymentSummary>{

	List<FetActualPaymentSummary> getData(FetActualPaymentSummary dto);
	
}