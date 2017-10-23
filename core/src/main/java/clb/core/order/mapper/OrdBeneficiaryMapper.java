package clb.core.order.mapper;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.order.dto.OrdBeneficiary;

import java.util.List;

public interface OrdBeneficiaryMapper extends Mapper<OrdBeneficiary>{
    /**
     * 受益人查询
     * @param ordBeneficiary
     * @return
     */
    public List<OrdBeneficiary> queryOrdBeneficiary(OrdBeneficiary ordBeneficiary);

    /**
     * 受益人查询(接口)
     * daiqian.shi@hand-china.com
     *
     * @param ordBeneficiary
     * @return
     */
    public List<OrdBeneficiary> queryWsOrdBeneficiary(OrdBeneficiary ordBeneficiary);
    
    /**
     * 查询受益人
     * @param orderId
     * @return
     */
    public List<OrdBeneficiary> selectAllBeneficiary(Long orderId);
}