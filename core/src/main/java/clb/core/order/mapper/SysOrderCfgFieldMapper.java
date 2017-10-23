package clb.core.order.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.order.dto.SysOrderCfgField;

public interface SysOrderCfgFieldMapper extends Mapper<SysOrderCfgField>{

	/**
	 * 查询字段信息
	 * @param sysOrderCfgField
	 * @return
	 */
	List<SysOrderCfgField> selectFieldByCfgId(Long cfgId);
	
	/**
	 * 
	 * @param cfgId
	 * @return
	 */
	List<SysOrderCfgField> selectFieldInfo(Long cfgId);
}