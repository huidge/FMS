package clb.core.commission.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import clb.core.commission.dto.CmnTradeRouteShow;
import clb.core.commission.service.ICmnTradeRouteShowService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CmnTradeRouteShowServiceImpl extends BaseServiceImpl<CmnTradeRouteShow> implements ICmnTradeRouteShowService{

}