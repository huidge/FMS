package clb.core.customer.mapper;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.customer.dto.CtmCustomer;

import java.util.List;

public interface CtmCustomerMapper extends Mapper<CtmCustomer>{

    List<CtmCustomer> query(CtmCustomer ctmCustomer);

    List<CtmCustomer> wsQuery(CtmCustomer dto);

    List<CtmCustomer> selectByIdNumber(CtmCustomer dto);
}