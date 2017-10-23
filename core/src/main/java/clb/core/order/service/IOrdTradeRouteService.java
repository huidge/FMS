package clb.core.order.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.order.dto.OrdTradeRoute;

public interface IOrdTradeRouteService extends IBaseService<OrdTradeRoute>, ProxySelf<IOrdTradeRouteService>{
	/**
	 * 查询交易路线上的1级销售  by orderId
	 * @param request
	 * @param orderId
	 * @return
	 */
	String queryStairSellByOrderId(IRequest request, Long orderId);

}