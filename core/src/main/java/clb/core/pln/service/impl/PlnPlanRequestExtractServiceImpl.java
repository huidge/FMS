package clb.core.pln.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import clb.core.pln.dto.PlnPlanRequestExtract;
import clb.core.pln.service.IPlnPlanRequestExtractService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PlnPlanRequestExtractServiceImpl extends BaseServiceImpl<PlnPlanRequestExtract> implements IPlnPlanRequestExtractService{

}