package clb.core.order.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import clb.core.order.dto.OrdTradeRoute;
import clb.core.order.mapper.OrdTradeRouteMapper;
import clb.core.order.service.IOrdTradeRouteService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrdTradeRouteServiceImpl extends BaseServiceImpl<OrdTradeRoute> implements IOrdTradeRouteService{
	
	@Autowired
	private OrdTradeRouteMapper ordTradeRouteMapper;
	
	/**
	 * 查询交易路线上的1级销售  by orderId
	 */
	@Override
	public String queryStairSellByOrderId(IRequest request, Long orderId) {
		return ordTradeRouteMapper.queryStairSellByOrderId(orderId);
	}

}