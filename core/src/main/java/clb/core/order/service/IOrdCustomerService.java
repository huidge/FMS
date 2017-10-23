package clb.core.order.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.order.dto.OrdCustomer;

import java.util.List;

public interface IOrdCustomerService extends IBaseService<OrdCustomer>, ProxySelf<IOrdCustomerService>{

    List<OrdCustomer> queryOrdCustomer(IRequest request, OrdCustomer ordCustomer, int page, int pagesize);

    /**
     * 接口查询投保人，受保人
     * daiqian.shi@hand-china.com
     * @param request
     * @param ordCustomer
     * @return
     */
    List<OrdCustomer> queryWsOrdCustomer(IRequest request, OrdCustomer ordCustomer);

    /**
     * 批量更新或者新增客户
     * @param request
     * @param ordCustomers
     * @return
     */
    List<OrdCustomer> batchSubmit(IRequest request,List<OrdCustomer> ordCustomers);

}