package clb.core.order.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import clb.core.order.dto.OrdCstWork;
import clb.core.order.service.IOrdCstWorkService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrdCstWorkServiceImpl extends BaseServiceImpl<OrdCstWork> implements IOrdCstWorkService{

}