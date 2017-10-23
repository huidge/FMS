package clb.core.commission.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import clb.core.commission.dto.CmnSupplierCommissionShow;
import clb.core.commission.service.ICmnSupplierCommissionShowService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CmnSupplierCommissionShowServiceImpl extends BaseServiceImpl<CmnSupplierCommissionShow> implements ICmnSupplierCommissionShowService{

}