package clb.core.pln.service;

import java.util.List;

import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.pln.dto.PlnPlanRequestAdtlRisk;

public interface IPlnPlanRequestAdtlRiskService extends IBaseService<PlnPlanRequestAdtlRisk>, ProxySelf<IPlnPlanRequestAdtlRiskService>{

	List<PlnPlanRequestAdtlRisk> selectAdtlRisk(Long planId);
}