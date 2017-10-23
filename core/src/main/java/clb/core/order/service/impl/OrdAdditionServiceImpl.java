package clb.core.order.service.impl;

import clb.core.order.mapper.OrdAdditionMapper;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import clb.core.order.dto.OrdAddition;
import clb.core.order.service.IOrdAdditionService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrdAdditionServiceImpl extends BaseServiceImpl<OrdAddition> implements IOrdAdditionService{

    @Autowired
    private OrdAdditionMapper ordAdditionMapper;

    /**
     * 查询
     * @param request
     * @param ordOrder
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public List<OrdAddition> queryOrdAddition(IRequest request, OrdAddition ordAddition, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return ordAdditionMapper.queryOrdAddition(ordAddition);
    }

    /**
     * 附加险信息查询接口
     * daiqian.shi@hand-china.com
     *
     * @param request
     * @param ordAddition
     * @return
     */
    @Override
    public List<OrdAddition> queryWsOrdAddition(IRequest request, OrdAddition ordAddition) {
        return ordAdditionMapper.queryWsOrdAddition(ordAddition);
    }
    /**
     * 修改附加险的标识和保存状态  通过订单id和产品id
     */
	@Override
	public void updateOrdAdditionByOrderIdAndItemId(OrdAddition addition) {
		ordAdditionMapper.updateOrdAdditionByOrderIdAndItemId(addition);
	}
	/**
	 * 修改附加险的状态  通过订单id和产品id
	 */
	@Override
	public void updateStatusByOrderId(OrdAddition addition) {
		ordAdditionMapper.updateStatusByOrderId(addition);
	}
	/**
	 * 查询保存的临时状态
	 */
	@Override
	public String querySaveStatusByOrderIdAndItemId(OrdAddition ordAddition) {
		return ordAdditionMapper.querySaveStatusByOrderIdAndItemId(ordAddition);
	}

}