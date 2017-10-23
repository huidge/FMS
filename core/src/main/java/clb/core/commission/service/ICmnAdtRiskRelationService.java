package clb.core.commission.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.commission.dto.CmnAdtRiskRelation;

public interface ICmnAdtRiskRelationService extends IBaseService<CmnAdtRiskRelation>, ProxySelf<ICmnAdtRiskRelationService>{

	List<CmnAdtRiskRelation> queryAll(IRequest requestContext, CmnAdtRiskRelation dto, int page, int pageSize);
	/**
	 * 查询 产品公司+附加险+主险组合  是否存在  by 产品公司+附加险 主险组合为空
	 * @param requestCtx
	 * @param cmnAdtRiskRelation
	 * @return
	 */
	List<CmnAdtRiskRelation> queryByCondition(IRequest requestCtx, CmnAdtRiskRelation cmnAdtRiskRelation);

}