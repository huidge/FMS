package clb.core.order.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.order.dto.OrdOrder;
import clb.core.order.dto.OrdTeamVisitor;
import clb.core.production.dto.PrdItemSubline;
import clb.core.production.dto.PrdItems;

public interface IOrdValueAddService extends IBaseService<OrdOrder>, ProxySelf<IOrdValueAddService>{
	/**
	 * 订单增值服务列表
	 * @param requestContext
	 * @param dto
	 * @param page
	 * @param pageSize
	 * @return
	 */
	List<OrdOrder> queryValueAddByCondition(IRequest requestContext, OrdOrder dto, int page, int pageSize);
	/**
	 * L团签旅客信息
	 * @param requestContext
	 * @param dto
	 * @param page
	 * @param pageSize
	 * @return
	 */
	List<OrdTeamVisitor> queryOrdTeamVisitor(IRequest requestContext, OrdTeamVisitor dto, int page, int pageSize);
	/**
	 * 修改状态
	 * @param requestContext
	 * @param ordOrder
	 * @param ip 
	 */
	void updateStatus(IRequest requestContext, OrdOrder ordOrder, String ip)throws Exception;
	/**
	 * 查询子产品
	 * @param requestContext
	 * @param dto
	 * @return
	 */
	List<PrdItems> querySublineProductName(IRequest requestContext, PrdItems dto);
	/**
	 * 订单超时支付修改状态  添加日志
	 * @param requestContext
	 * @param dto
	 */
	void updateStatusByOrderId(IRequest requestContext, OrdOrder dto);

}