package clb.core.commission.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.commission.dto.CmnTradeRoute;

import java.util.List;

public interface ICmnTradeRouteService extends IBaseService<CmnTradeRoute>, ProxySelf<ICmnTradeRouteService>{

    public void deleteRouteData(CmnTradeRoute cmnTradeRoute);

    public void updateParentRouteId(CmnTradeRoute cmnTradeRoute);

    public void updateDealPath(CmnTradeRoute cmnTradeRoute);

    public void updateCommissionDealPath(CmnTradeRoute cmnTradeRoute);

    List<CmnTradeRoute> queryCmnTradeRoute(IRequest request, CmnTradeRoute cmnTradeRoute);

    public List<CmnTradeRoute> queryDealPath(CmnTradeRoute cmnTradeRoute);

    List<CmnTradeRoute> queryCmnTradeRouteByCondition(CmnTradeRoute cmnTradeRoute);

    public void transferData();
}