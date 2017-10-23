package clb.core.order.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import clb.core.order.dto.OrdTemplateLine;
import clb.core.order.service.IOrdTemplateLineService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrdTemplateLineServiceImpl extends BaseServiceImpl<OrdTemplateLine> implements IOrdTemplateLineService{

}