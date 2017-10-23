package clb.core.order.service.impl;

import clb.core.order.mapper.OrdCommissionMapper;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import clb.core.order.dto.OrdCommission;
import clb.core.order.service.IOrdCommissionService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrdCommissionServiceImpl extends BaseServiceImpl<OrdCommission> implements IOrdCommissionService{

    @Autowired
    private OrdCommissionMapper ordCommissionMapper;

    /**
     * 查询
     * @param request
     * @param ordCommission
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public List<OrdCommission> queryOrdCommission(IRequest request, OrdCommission ordCommission, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return ordCommissionMapper.queryOrdCommission(ordCommission);
    }

    /**
     * 接口查询佣金信息
     * daiqian.shi@hand-china.com
     *
     * @param request
     * @param ordCommission
     * @return
     */
    @Override
    public List<OrdCommission> queryWsOrdCommission(IRequest request, OrdCommission ordCommission) {
        return ordCommissionMapper.queryWsOrdCommission(ordCommission);
    }

    /**
     * 查询
     * @param request
     * @param ordCommission
     * @return
     */
    @Override
    public List<OrdCommission> queryCompany(IRequest request, OrdCommission ordCommission) {
        return ordCommissionMapper.queryCompany(ordCommission);
    }

    @Override
    public List<OrdCommission> queryManualFlag(IRequest request, OrdCommission ordCommission) {
        return ordCommissionMapper.queryManualFlag(ordCommission);
    }
}