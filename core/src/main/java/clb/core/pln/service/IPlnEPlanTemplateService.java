package clb.core.pln.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.pln.dto.PlnEPlanTemplate;
import clb.core.production.dto.PrdPremium;

public interface IPlnEPlanTemplateService extends IBaseService<PlnEPlanTemplate>, ProxySelf<IPlnEPlanTemplateService>{
	/**
	 * 查询所有数据
	 * @param requestContext
	 * @param dto
	 * @param page
	 * @param pageSize
	 * @return
	 */
	List<PlnEPlanTemplate> query(IRequest requestContext, PlnEPlanTemplate dto, int page, int pageSize);
	/**
	 * 前端查询电子计划书模板信息(大,中,小信息)
	 * @param requestContext
	 * @param dto
	 * @param page
	 * @param pageSize
	 * @return
	 */
	List<PlnEPlanTemplate> queryWsPlnEPlanTemplate(IRequest requestContext, PrdPremium prdPremium, int page,
			int pageSize);
	/**
	 * 完善录入数据
	 * @param requestContext
	 * @throws Exception
	 */
	void dataPerfect(IRequest requestContext) throws Exception;

}