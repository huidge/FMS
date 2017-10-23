package clb.core.production.service.impl;

import clb.core.production.dto.PrdItemSecurityPlan;
import clb.core.production.mapper.PrdItemSecurityPlanMapper;
import clb.core.production.service.IPrdItemSecurityPlanService;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PrdItemSecurityPlanServiceImpl extends BaseServiceImpl<PrdItemSecurityPlan> implements IPrdItemSecurityPlanService {

    @Autowired
    private PrdItemSecurityPlanMapper prdItemPaymodeMapper;

    @Override
    public List<PrdItemSecurityPlan> query(IRequest iRequest, PrdItemSecurityPlan dto, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return prdItemPaymodeMapper.select(dto);
    }

    @Override
    public List<PrdItemSecurityPlan> querySecurityLevel(IRequest requestContext, PrdItemSecurityPlan prdItemSecurityPlan) {
        return prdItemPaymodeMapper.querySecurityLevel(prdItemSecurityPlan);
    }

    @Override
    public List<PrdItemSecurityPlan> querySecurityRegion(IRequest requestContext, PrdItemSecurityPlan prdItemSecurityPlan) {
        return prdItemPaymodeMapper.querySecurityRegion(prdItemSecurityPlan);
    }

    @Override
    public List<PrdItemSecurityPlan> batchUpdate(IRequest request, List<PrdItemSecurityPlan> dtoList) {
        for (PrdItemSecurityPlan dto : dtoList) {
            if (dto.getPlanId()==null || dto.getPlanId().equals("")) {
                prdItemPaymodeMapper.insertSelective(dto);
            } else {
                prdItemPaymodeMapper.updateByPrimaryKeySelective(dto);
            }
        }
        return dtoList;
    }
}