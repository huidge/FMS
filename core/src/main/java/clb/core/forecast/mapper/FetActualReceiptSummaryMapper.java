package clb.core.forecast.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.forecast.dto.FetActualReceiptSummary;

public interface FetActualReceiptSummaryMapper extends Mapper<FetActualReceiptSummary>{

	List<FetActualReceiptSummary> getData(FetActualReceiptSummary dto);
	
	
}