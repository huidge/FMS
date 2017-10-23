package clb.core.commission.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import clb.core.commission.dto.CmnChannelCommissionShow;
import clb.core.commission.service.ICmnChannelCommissionShowService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CmnChannelCommissionShowServiceImpl extends BaseServiceImpl<CmnChannelCommissionShow> implements ICmnChannelCommissionShowService{

}