package clb.core.order.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import clb.core.order.dto.OrdCstEducation;
import clb.core.order.service.IOrdCstEducationService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrdCstEducationServiceImpl extends BaseServiceImpl<OrdCstEducation> implements IOrdCstEducationService{

}