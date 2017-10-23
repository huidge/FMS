package clb.core.customer.service.impl;

import clb.core.customer.dto.CtmCustomer;
import clb.core.customer.mapper.CtmCustomerFamilyMapper;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import clb.core.customer.dto.CtmCustomerFamily;
import clb.core.customer.service.ICtmCustomerFamilyService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CtmCustomerFamilyServiceImpl extends BaseServiceImpl<CtmCustomerFamily> implements ICtmCustomerFamilyService{

    @Autowired
    CtmCustomerFamilyMapper ctmCustomerFamilyMapper;

    @Override
    public List<CtmCustomerFamily> selectByCustomerId(CtmCustomer customer) {
        return ctmCustomerFamilyMapper.selectByCustomerId(customer);
    }
}