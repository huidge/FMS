package clb.core.forecast.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.forecast.dto.FetReceiptDiff;

public interface FetReceiptDiffMapper extends Mapper<FetReceiptDiff>{

	List<FetReceiptDiff> getData(FetReceiptDiff dto);
	
	List<FetReceiptDiff> summaryQuery(FetReceiptDiff dto);
	
	List<FetReceiptDiff> getDiffData(FetReceiptDiff dto);
	
	Long getMaxVerion(FetReceiptDiff dto);
	
}