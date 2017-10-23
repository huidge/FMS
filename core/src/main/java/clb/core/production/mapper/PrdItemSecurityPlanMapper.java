package clb.core.production.mapper;

import clb.core.production.dto.PrdItemSecurityPlan;
import com.hand.hap.mybatis.common.Mapper;

import java.util.List;

public interface PrdItemSecurityPlanMapper extends Mapper<PrdItemSecurityPlan>{
    public List<PrdItemSecurityPlan> querySecurityLevel(PrdItemSecurityPlan prdItemSecurityPlan);
    public List<PrdItemSecurityPlan> querySecurityRegion(PrdItemSecurityPlan prdItemSecurityPlan);
}