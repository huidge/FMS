package clb.core.order.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import clb.core.order.dto.OrdRenewal;
import clb.core.order.service.IOrdRenewalService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrdRenewalServiceImpl extends BaseServiceImpl<OrdRenewal> implements IOrdRenewalService{

}