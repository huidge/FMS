package clb.core.customer.mapper;

import clb.core.customer.dto.CtmCustomer;
import com.hand.hap.mybatis.common.Mapper;
import clb.core.customer.dto.CtmCustomerFamily;

import java.util.List;

public interface CtmCustomerFamilyMapper extends Mapper<CtmCustomerFamily>{

    List<CtmCustomerFamily> selectByCustomerId(CtmCustomer customer);
}