package clb.core.forecast.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.forecast.dto.FetReceivable;

public interface FetReceivableMapper extends Mapper<FetReceivable>{
	
	List<FetReceivable> getData(FetReceivable dto);
	
	List<FetReceivable> summaryQuery(FetReceivable dto);

	/**
	 * 批量插入方法
	 *
	 * @param list
	 * @return
	 */
	int batchInsert(List<FetReceivable> list);

}