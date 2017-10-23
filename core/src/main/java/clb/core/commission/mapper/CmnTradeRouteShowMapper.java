package clb.core.commission.mapper;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.commission.dto.CmnTradeRouteShow;

import java.util.List;

public interface CmnTradeRouteShowMapper extends Mapper<CmnTradeRouteShow>{

    public void deleteRouteData();

    public void insertShowRouteData();

    public void transferShowRouteData(Long batchId);

    List<CmnTradeRouteShow> queryCmnTradeRoute(CmnTradeRouteShow cmnTradeRouteShow);
}