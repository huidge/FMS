package clb.core.pln.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.pln.dto.PlnEPlanTemplateSmall;

public interface PlnEPlanTemplateSmallMapper extends Mapper<PlnEPlanTemplateSmall>{
	/**
	 * 根据条件查询所有数据
	 * @param dto
	 * @return
	 */
	List<PlnEPlanTemplateSmall> queryList(PlnEPlanTemplateSmall dto);

}