package clb.core.order.mapper;

import java.util.List;

import com.github.pagehelper.PageHelper;
import com.hand.hap.mybatis.common.Mapper;

import clb.core.order.dto.OrdAddition;
import clb.core.order.dto.OrdAfter;
import clb.core.order.dto.OrdOrder;
import clb.core.order.dto.OrdOrderRenewal;

public interface OrdAfterMapper extends Mapper<OrdAfter>{
	/**
	 * 订单售后汇总查询接口
	 * @param dto
	 * @return
	 */
	List<OrdAfter> queryOrdAfterList(OrdAfter dto);

	Long queryOrdAfterListTotal(OrdAfter dto);

	List<OrdAfter> queryWSOrdAfterList(OrdAfter dto);
	
	List<OrdAfter> queryWSOrdAfterTeamList(OrdAfter dto);
	
	/**
	 * 续保列表查询
	 * @param dto
	 * @return
	 */
	List<OrdOrderRenewal> queryOrdRenewalList(OrdOrderRenewal dto);
	
	List<OrdOrderRenewal> queryWSOrdRenewalList(OrdOrderRenewal dto);
	
	List<OrdOrderRenewal> ClbWsOrdRenewalTeamList(OrdOrderRenewal dto);
	/**
	 * 续保详情页面的续保表格
	 * @param dto
	 * @return
	 */
	List<OrdOrderRenewal> queryRenewalByOrderId(OrdOrderRenewal dto);
	/**
	 * 通过订单id或者保单编号查询 订单详情
	 * @param dto
	 * @return
	 */
	List<OrdOrder> queryOrdOrderByOrderId(OrdOrder dto);
	/**
	 * 根据订单id 查询订单下的附加险
	 * @param ordAddition
	 * @return
	 */
	List<OrdAddition> queryOrdAdditionByOrderId(OrdAddition ordAddition);
	/**
	 * 售后跟进=>本期续保产品
	 * @param dto
	 * @return
	 */
	List<OrdOrderRenewal> queryRenewalInfoByOrderId(OrdOrderRenewal dto);
	/**
	 * 续保信息/订单详情  通过订单id查询
	 * @param dto
	 * @return
	 */
	List<OrdOrder> queryOrderByOrdOrderId(OrdOrder dto);

	List<OrdAfter> queryOrderInfoByAfterId(OrdAfter dto);
	
	/**
	 * 接口查询售后单信息
	 * @param dto
	 * @return
	 */
	public List<OrdAfter> queryWsOrdAfter(OrdAfter dto);
	
	/**
	 * 修改续保表中的状态  续保到期日  续保标识 币种
	 * @param dto
	 * @return
	 */
	void updateRenewalInfoByOrderId(OrdOrderRenewal dto);
	
	/**
	 * 修改续保表中当前期数下主险和附加险的缴费金额
	 * @param dto
	 * @return
	 */
	void updateRenewalTotalAmountByOrderId(OrdOrderRenewal dto);
	/**
	 * 查询续保的条数
	 * @param dto
	 * @return
	 */
	Long queryRenewalTotal(OrdOrderRenewal dto);
	/**
	 * 查询售后项目 通过售后id
	 * @param dto
	 * @return
	 */
	String queryAfterProject(OrdAfter dto);

}