package clb.core.customer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.customer.dto.CtmCustomer;

import java.util.List;
import java.util.Locale;

public interface ICtmCustomerService extends IBaseService<CtmCustomer>, ProxySelf<ICtmCustomerService>{

    List<CtmCustomer> query(IRequest requestContext, CtmCustomer dto, int page, int pageSize);

    List<CtmCustomer> queryAllCustomer(IRequest requestContext, CtmCustomer dto);

    List<CtmCustomer> createCustomer(IRequest request, Locale locale, CtmCustomer dto) ;

    String checkData(CtmCustomer dto, Locale locale);

    List<CtmCustomer> wsQuery(IRequest requestContext, CtmCustomer dto, int page, int pageSize);

    List<CtmCustomer> selectByIdNumber(IRequest requestCtx, CtmCustomer dto);
}