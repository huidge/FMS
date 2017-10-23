package clb.core.pln.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.pln.dto.PlnPlanRequestAdtlRisk;
import clb.core.pln.mapper.PlnPlanRequestAdtlRiskMapper;
import clb.core.pln.service.IPlnPlanRequestAdtlRiskService;

@Service
@Transactional
public class PlnPlanRequestAdtlRiskServiceImpl extends BaseServiceImpl<PlnPlanRequestAdtlRisk> implements IPlnPlanRequestAdtlRiskService{
 
	@Autowired
	private PlnPlanRequestAdtlRiskMapper plnPlanRequestAdtlRiskMapper; 
	
	@Override
	public List<PlnPlanRequestAdtlRisk> selectAdtlRisk(Long planId) {
		return plnPlanRequestAdtlRiskMapper.selectAdtlRisk(planId);
	}

}