package clb.core.order.mapper;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.order.dto.OrdOrderRenewal;

import java.util.List;

public interface OrdOrderRenewalMapper extends Mapper<OrdOrderRenewal>{
	/**
	 * 修改续保信息 by orderId
	 * @param dto
	 */
	public void updateRenewalByOrderId(OrdOrderRenewal dto);
	
	/**
     * 根据订单ID查询续保信息
     * jun.zhao02@hand-china.com
     * @param ordOrderRenewal
     * @return
     */
    public List<OrdOrderRenewal> queryRenewalByOrder(OrdOrderRenewal ordOrderRenewal);

    /**
     * 接口查询订单续保信息
     * daiqian.shi@hand-china.com
     * @param ordOrderRenewal
     * @return
     */
    public List<OrdOrderRenewal> queryWsRenewal(OrdOrderRenewal ordOrderRenewal);
    /**
     * 修改续保的标识符
     * @param dto
     */
	public void updateFlagByOrderId(OrdOrderRenewal dto);
	
}