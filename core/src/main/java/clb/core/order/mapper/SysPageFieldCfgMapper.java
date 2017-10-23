package clb.core.order.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.order.dto.SysPageFieldCfg;

public interface SysPageFieldCfgMapper extends Mapper<SysPageFieldCfg>{

	/**
	 * 订单配置查询
	 * @param sysPageFieldCfg
	 * @return
	 */
	List<SysPageFieldCfg> selectSettingInfo(SysPageFieldCfg sysPageFieldCfg);
	
	/**
	 * 通过角色查询第一级节点
	 * @param roleCode
	 * @return
	 */
	List<SysPageFieldCfg> queryByRoleCode(@Param(value="roleCode")String roleCode);
	
	/**
	 * 查询二级节点
	 * @param parentLineId
	 * @return
	 */
	List<SysPageFieldCfg> queryByParentLineId(@Param(value="parentLineId")Long parentLineId);
	
	/**
	 * 更新状态
	 * @param sysPageFieldCfg
	 * @return
	 */
	int updateFlag(SysPageFieldCfg sysPageFieldCfg);
}