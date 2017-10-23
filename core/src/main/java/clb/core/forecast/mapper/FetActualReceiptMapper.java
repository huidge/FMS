package clb.core.forecast.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.forecast.dto.FetActualReceipt;

public interface FetActualReceiptMapper extends Mapper<FetActualReceipt>{

	List<FetActualReceipt> getData(FetActualReceipt dto);
}