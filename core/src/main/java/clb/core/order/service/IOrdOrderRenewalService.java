package clb.core.order.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.order.dto.OrdOrder;
import clb.core.order.dto.OrdOrderRenewal;

import java.util.List;

public interface IOrdOrderRenewalService extends IBaseService<OrdOrderRenewal>, ProxySelf<IOrdOrderRenewalService>{

    /**
     * 修改续保信息 by orderId
     * @param requestCtx
     * @param dto
     */
    void updateRenewalByOrderId(IRequest requestCtx, OrdOrderRenewal dto);

    List<OrdOrderRenewal> queryRenewalByOrder(IRequest request, OrdOrderRenewal ordOrderRenewal, int page, int pagesize);

    /**
     * 接口查询续保信息
     * daiqian.shi@hand-china.com
     * @param request
     * @param ordOrderRenewal
     * @return
     */
    List<OrdOrderRenewal> queryWsRenewal(IRequest request, OrdOrderRenewal ordOrderRenewal);


	void sendNotice(IRequest requestCtx, OrdOrderRenewal dto) throws Exception;
	/**
	 * 修改订单状态  将待确认失效的订单和该订单下的附加险的状态改为订单失效
	 * @param requestCtx
	 * @param orderIds
	 */
	void updateStatusByOrderIds(IRequest requestCtx, List<Long> orderIds);
	
	/**
	 * 修改续保的标识符
	 * @param requestCtx
	 * @param dto
	 */
	void updateFlagByOrderId(IRequest requestCtx,OrdOrderRenewal dto);
	
}

