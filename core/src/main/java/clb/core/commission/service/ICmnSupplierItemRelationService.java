package clb.core.commission.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.commission.dto.CmnSupplierItemRelation;

public interface ICmnSupplierItemRelationService extends IBaseService<CmnSupplierItemRelation>, ProxySelf<ICmnSupplierItemRelationService>{

	List<CmnSupplierItemRelation> selectByCondition(IRequest requestContext, CmnSupplierItemRelation dto, int page, int pageSize);

}