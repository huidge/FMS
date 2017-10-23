package clb.core.order.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.order.dto.OrdCommission;

import java.util.List;

public interface IOrdCommissionService extends IBaseService<OrdCommission>, ProxySelf<IOrdCommissionService>{
    List<OrdCommission> queryOrdCommission(IRequest request, OrdCommission ordCommission, int page, int pagesize);

    /**
     * 接口查询佣金信息
     * daiqian.shi@hand-china.com
     * @param request
     * @param ordCommission
     * @return
     */
    List<OrdCommission> queryWsOrdCommission(IRequest request, OrdCommission ordCommission);

    List<OrdCommission> queryCompany(IRequest request, OrdCommission ordCommission);

    List<OrdCommission> queryManualFlag(IRequest request, OrdCommission ordCommission);

}