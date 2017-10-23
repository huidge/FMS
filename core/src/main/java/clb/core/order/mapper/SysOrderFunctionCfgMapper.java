package clb.core.order.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.order.dto.SysOrderFunctionCfg;

public interface SysOrderFunctionCfgMapper extends Mapper<SysOrderFunctionCfg>{

	/**
	 * 查询记录
	 * @param sysOrderFunctionCfg
	 * @return
	 */
	List<SysOrderFunctionCfg> selectFunctionInfo(SysOrderFunctionCfg sysOrderFunctionCfg);
	
	/**
	 * 通过头id查询
	 * @param cfgId
	 * @return
	 */
	List<SysOrderFunctionCfg> queryByHeaderId(Long cfgId);
}