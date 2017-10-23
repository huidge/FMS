package clb.core.production.service.impl;

import clb.core.production.dto.PrdPremium;
import clb.core.production.mapper.PrdPremiumAdjustMapper;
import com.hand.hap.core.IRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.production.dto.PrdPremiumAdjust;
import clb.core.production.service.IPrdPremiumAdjustService;

import java.util.List;

/*****
 * @author tiansheng.ye
 * @Date 2017/07/21
 */
@Service
@Transactional
public class PrdPremiumAdjustServiceImpl extends BaseServiceImpl<PrdPremiumAdjust> implements IPrdPremiumAdjustService{


    @Autowired
    private PrdPremiumAdjustMapper prdPremiumAdjustMapper;

    @Override
    public List<PrdPremiumAdjust> selectAllFields(IRequest requestContext, PrdPremiumAdjust prdPremiumAdjust, int page,
                                            int pageSize) {
        return prdPremiumAdjustMapper.selectAllFields(prdPremiumAdjust);
    }
}
