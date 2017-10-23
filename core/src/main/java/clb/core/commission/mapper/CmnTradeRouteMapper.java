package clb.core.commission.mapper;

import clb.core.commission.dto.CmnSupplierCommission;
import com.hand.hap.mybatis.common.Mapper;
import clb.core.commission.dto.CmnTradeRoute;
import org.springframework.stereotype.Service;

import java.util.List;

public interface CmnTradeRouteMapper extends Mapper<CmnTradeRoute>{

    public void deleteRouteData(CmnTradeRoute cmnTradeRoute);

    public void updateParentRouteId(CmnTradeRoute cmnTradeRoute);

    public void updateDealPath(CmnTradeRoute cmnTradeRoute);

    public List<CmnTradeRoute> queryDealPath(CmnTradeRoute cmnTradeRoute);

    public void updateCommissionDealPath(CmnTradeRoute cmnTradeRoute);

    List<CmnTradeRoute> queryCmnTradeRoute(CmnTradeRoute cmnTradeRoute);

    List<CmnTradeRoute> queryCmnTradeRouteByCondition(CmnTradeRoute cmnTradeRoute);
}