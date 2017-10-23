package clb.core.pln.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.pln.dto.PlnPlanRequestAdtlRisk;

public interface PlnPlanRequestAdtlRiskMapper extends Mapper<PlnPlanRequestAdtlRisk>{
	
	/**
	 * 查询附加险信息
	 * @param planId
	 * @return
	 */
	List<PlnPlanRequestAdtlRisk> selectAdtlRisk(@Param(value="planId")Long planId);

}