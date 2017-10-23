package clb.core.order.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import clb.core.order.dto.OrdCstSkill;
import clb.core.order.service.IOrdCstSkillService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrdCstSkillServiceImpl extends BaseServiceImpl<OrdCstSkill> implements IOrdCstSkillService{

}