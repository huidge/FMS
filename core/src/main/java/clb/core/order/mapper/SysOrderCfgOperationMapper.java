package clb.core.order.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.order.dto.SysOrderCfgOperation;

public interface SysOrderCfgOperationMapper extends Mapper<SysOrderCfgOperation>{

	/**
	 * 查询操作信息
	 * @param cfgId
	 * @return
	 */
	List<SysOrderCfgOperation> selectOperationByCfgId(Long cfgId);
	
	/**
	 * 
	 * @param cfgId
	 * @return
	 */
	List<SysOrderCfgOperation> selectOperationInfo(Long cfgId);
}