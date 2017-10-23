package clb.core.order.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.hand.hap.account.dto.User;

import clb.core.order.dto.OrdOrder;

public interface OrdAppointmentScheduleMapper{
	
	/**
	 * 根据角色名获取用户 
	 */
	List<User> getUserByRoleName(String roleName);
	
	/**
	 * 查询订单数据 
	 */
	List<OrdOrder> getOrders(OrdOrder order);
	
	/**
	 * 查询订单数据 
	 */
	List<OrdOrder> getReserveData(OrdOrder order);
	
}