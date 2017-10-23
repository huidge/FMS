package clb.core.forecast.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.forecast.dto.FetPaymentDiff;
public interface FetPaymentDiffMapper extends Mapper<FetPaymentDiff>{

	List<FetPaymentDiff> getData(FetPaymentDiff dto);
	
	List<FetPaymentDiff> summaryQuery(FetPaymentDiff dto);
	
	List<FetPaymentDiff> getDiffData(FetPaymentDiff dto);
	
	Long getMaxVerion(FetPaymentDiff dto);
}