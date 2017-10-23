package clb.core.pln.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.pln.dto.PlnEPlanTemplateBig;

public interface PlnEPlanTemplateBigMapper extends Mapper<PlnEPlanTemplateBig>{
	/**
	 * 根据条件查询所有数据
	 * @param dto
	 * @return
	 */
	List<PlnEPlanTemplateBig> queryList(PlnEPlanTemplateBig dto);
	
}