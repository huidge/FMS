package clb.core.order.mapper;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.order.dto.OrdTradeRoute;

import java.util.List;

public interface OrdTradeRouteMapper extends Mapper<OrdTradeRoute>{
    /**
     * 查询预审行政
     * @param ordTradeRoute
     * @return
     */
    public List<OrdTradeRoute> queryPreUserId(OrdTradeRoute ordTradeRoute);
    
    /**
     * 查询交易路线上的1级销售  by orderId
     * @param orderId
     * @return
     */
	public String queryStairSellByOrderId(Long orderId);
}