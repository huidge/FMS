package clb.core.order.service.impl;

import clb.core.order.mapper.OrdBeneficiaryMapper;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import clb.core.order.dto.OrdBeneficiary;
import clb.core.order.service.IOrdBeneficiaryService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrdBeneficiaryServiceImpl extends BaseServiceImpl<OrdBeneficiary> implements IOrdBeneficiaryService{

    @Autowired
    private OrdBeneficiaryMapper ordBeneficiaryMapper;

    /**
     * 查询
     * @param request
     * @param ordOrder
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public List<OrdBeneficiary> queryOrdBeneficiary(IRequest request, OrdBeneficiary ordBeneficiary, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return ordBeneficiaryMapper.queryOrdBeneficiary(ordBeneficiary);
    }

    /**
     * 受益人查询(接口)
     * daiqian.shi@hand-china.com
     *
     * @param request
     * @param ordBeneficiary
     * @return
     */
    @Override
    public List<OrdBeneficiary> queryWsOrdBeneficiary(IRequest request, OrdBeneficiary ordBeneficiary) {
        return ordBeneficiaryMapper.queryWsOrdBeneficiary(ordBeneficiary);
    }

	@Override
	public List<OrdBeneficiary> selectAllBeneficiary(Long orderId) {
		return ordBeneficiaryMapper.selectAllBeneficiary(orderId);
	}
}