package clb.core.order.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.order.dto.OrdAddition;

import java.util.List;

public interface IOrdAdditionService extends IBaseService<OrdAddition>, ProxySelf<IOrdAdditionService>{
    List<OrdAddition> queryOrdAddition(IRequest request, OrdAddition ordAddition, int page, int pagesize);

    /**
     * 附加险信息查询接口
     * daiqian.shi@hand-china.com
     * @param request
     * @param ordAddition
     * @return
     */
    List<OrdAddition> queryWsOrdAddition(IRequest request, OrdAddition ordAddition);
    /**
     * 修改附加险的标识和保存状态  通过订单id和产品id
     * @param addition
     */
	void updateOrdAdditionByOrderIdAndItemId(OrdAddition addition);
	/**
	 * 修改附加险的状态  通过订单id
	 * @param addition
	 */
	void updateStatusByOrderId(OrdAddition addition);
	/**
	 * 查询保存的临时状态
	 * @param ordAddition
	 * @return
	 */
	String querySaveStatusByOrderIdAndItemId(OrdAddition ordAddition);

}