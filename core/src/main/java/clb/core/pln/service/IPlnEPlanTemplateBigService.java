package clb.core.pln.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.pln.dto.PlnEPlanTemplateBig;

public interface IPlnEPlanTemplateBigService extends IBaseService<PlnEPlanTemplateBig>, ProxySelf<IPlnEPlanTemplateBigService>{
	/**
	 * 根据条件查询所有数据
	 * @param requestContext
	 * @param dto
	 * @param page
	 * @param pageSize
	 * @return
	 */
	List<PlnEPlanTemplateBig> queryList(IRequest requestContext, PlnEPlanTemplateBig dto, int page, int pageSize);
	/**
	 * 查询符合记录的总条数
	 * @param requestContext
	 * @param dto
	 * @return
	 */
	Long selectCount(IRequest requestContext, PlnEPlanTemplateBig dto);

}