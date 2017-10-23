package clb.core.order.mapper;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.order.dto.OrdCustomer;

import java.util.List;

public interface OrdCustomerMapper extends Mapper<OrdCustomer>{
    /**
     * 订单客户查询
     * @param ordCustomer
     * @return
     */
    public List<OrdCustomer> queryOrdCustomer(OrdCustomer ordCustomer);

    /**
     * 接口查询投保人，受保人
     * daiqian.shi@hand-china.com
     * @param ordCustomer
     * @return
     */
    public List<OrdCustomer> queryWsOrdCustomer(OrdCustomer ordCustomer);
}