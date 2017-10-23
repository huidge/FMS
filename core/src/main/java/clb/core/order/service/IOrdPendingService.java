package clb.core.order.service;

import clb.core.order.dto.OrdOrder;
import clb.core.system.dto.ClbUser;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.core.annotation.StdWho;
import com.hand.hap.system.service.IBaseService;
import clb.core.order.dto.OrdPending;

import java.util.List;

public interface IOrdPendingService extends IBaseService<OrdPending>, ProxySelf<IOrdPendingService>{
    List<OrdPending> queryOrdPending(IRequest request, OrdPending ordPending, int page, int pagesize);
    List<OrdPending> queryWsOrdPending(IRequest request, OrdPending ordPending);
    List<OrdPending> queryWsPersonalOrdPending(IRequest request, OrdPending ordPending);
    Long queryOrdPendingTotle(IRequest request, OrdPending ordPending);
    List<OrdPending> queryWsTeamOrdPending(IRequest request, OrdPending ordPending);
    List<OrdPending> pendingBatchUpdate(IRequest request, @StdWho List<OrdPending> ordPendings);

    /**
     * 统计订单下的pending数量
     * @param orderId
     * @return
     */
    public Long countPendingByOrderId(Long orderId);

    /**
     * 查询处理人
     * @param ordOrder
     * @return
     */
    public List<ClbUser> queryDealUser(OrdOrder ordOrder);

    List<OrdPending> queryNotClosed(IRequest request, OrdPending ordPending);

}