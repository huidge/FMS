package clb.core.customer.service;

import clb.core.customer.dto.CtmCustomer;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.customer.dto.CtmCustomerFamily;

import java.util.List;

public interface ICtmCustomerFamilyService extends IBaseService<CtmCustomerFamily>, ProxySelf<ICtmCustomerFamilyService>{

    List<CtmCustomerFamily> selectByCustomerId(CtmCustomer customer);
}