package clb.core.production.service;

import clb.core.production.dto.PrdItemSecurityPlan;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import java.util.List;

public interface IPrdItemSecurityPlanService extends IBaseService<PrdItemSecurityPlan>, ProxySelf<IPrdItemSecurityPlanService>{

    List<PrdItemSecurityPlan> query(IRequest iRequest, PrdItemSecurityPlan dto, int page, int pagesize);

    List<PrdItemSecurityPlan> querySecurityLevel(IRequest requestContext, PrdItemSecurityPlan prdItemSecurityPlan);

    List<PrdItemSecurityPlan> querySecurityRegion(IRequest requestContext, PrdItemSecurityPlan prdItemSecurityPlan);

    List<PrdItemSecurityPlan> batchUpdate(IRequest request, List<PrdItemSecurityPlan> dtoList);
}