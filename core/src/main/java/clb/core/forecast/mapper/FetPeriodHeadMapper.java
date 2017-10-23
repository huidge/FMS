package clb.core.forecast.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.forecast.dto.FetPeriod;
import clb.core.forecast.dto.FetPeriodHead;

public interface FetPeriodHeadMapper extends Mapper<FetPeriodHead>{
	
	public List<FetPeriodHead> selectAllFiled(FetPeriodHead dto);
	
	public FetPeriodHead selectPeriodHeadByPeriod(FetPeriod period);
}