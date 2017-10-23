package clb.core.pln.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.pln.dto.PlnEPlanTemplateDetail;

public interface PlnEPlanTemplateDetailMapper extends Mapper<PlnEPlanTemplateDetail>{
	/**
	 * 根据条件查询所有数据
	 * @param dto
	 * @return
	 */
	List<PlnEPlanTemplateDetail> queryList(PlnEPlanTemplateDetail dto);

}