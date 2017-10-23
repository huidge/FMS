package clb.core.forecast.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.forecast.dto.FetPayable;

public interface FetPayableMapper extends Mapper<FetPayable>{
	
	List<FetPayable> getData(FetPayable dto);
	
	List<FetPayable> summaryQuery(FetPayable dto);

	int batchInsert(List<FetPayable> list);

}