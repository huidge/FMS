package clb.core.forecast.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.forecast.dto.FetChannelCheck;
import org.apache.ibatis.annotations.Param;

public interface FetChannelCheckMapper extends Mapper<FetChannelCheck>{

	List<FetChannelCheck> getData(FetChannelCheck dto);
	
	List<FetChannelCheck> summaryQuery(FetChannelCheck dto);

	int batchInsert(List<FetChannelCheck> list);

	int getCheckCount(@Param("paramStatus") String paramStatus,@Param("status") String status,@Param("userId") Long userId);
}