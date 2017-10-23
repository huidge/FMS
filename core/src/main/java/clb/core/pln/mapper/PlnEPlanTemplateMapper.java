package clb.core.pln.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.pln.dto.PlnEPlanTemplate;

public interface PlnEPlanTemplateMapper extends Mapper<PlnEPlanTemplate>{

	List<PlnEPlanTemplate> query(PlnEPlanTemplate dto);
	/**
	 * 前端查询电子计划书模板信息(大,中,小信息)
	 * @param dto
	 * @return
	 */
	List<PlnEPlanTemplate> queryWsPlnEPlanTemplate(PlnEPlanTemplate dto);

}