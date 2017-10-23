package clb.core.order.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import clb.core.order.dto.OrdTradeRouteShow;
import clb.core.order.service.IOrdTradeRouteShowService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrdTradeRouteShowServiceImpl extends BaseServiceImpl<OrdTradeRouteShow> implements IOrdTradeRouteShowService{

}