package clb.core.order.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.order.dto.OrdBeneficiary;

import java.util.List;

public interface IOrdBeneficiaryService extends IBaseService<OrdBeneficiary>, ProxySelf<IOrdBeneficiaryService>{

    List<OrdBeneficiary> queryOrdBeneficiary(IRequest request, OrdBeneficiary ordBeneficiary, int page, int pagesize);

    /**
     * 受益人查询(接口)
     * daiqian.shi@hand-china.com
     * @param request
     * @param ordBeneficiary
     * @return
     */
    public List<OrdBeneficiary> queryWsOrdBeneficiary(IRequest request, OrdBeneficiary ordBeneficiary);
    
    /**
     * 通过订单查询受益人
     * @param orderId
     * @return
     */
    public List<OrdBeneficiary> selectAllBeneficiary(Long orderId);

}