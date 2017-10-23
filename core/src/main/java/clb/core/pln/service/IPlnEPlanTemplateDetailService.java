package clb.core.pln.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.pln.dto.PlnEPlanTemplateDetail;

public interface IPlnEPlanTemplateDetailService extends IBaseService<PlnEPlanTemplateDetail>, ProxySelf<IPlnEPlanTemplateDetailService>{

	List<PlnEPlanTemplateDetail> selectByCondition(PlnEPlanTemplateDetail plnEPlanTemplateDetail);
	/**
	 * 根据条件查询所有数据
	 * @param requestContext
	 * @param dto
	 * @param page
	 * @param pageSize
	 * @return
	 */
	List<PlnEPlanTemplateDetail> queryList(IRequest requestContext, PlnEPlanTemplateDetail dto, int page, int pageSize);
	/**
	 * 查询符合记录的总条数
	 * @param requestContext
	 * @param dto
	 * @return
	 */
	Long selectCount(IRequest requestContext, PlnEPlanTemplateDetail dto);

}