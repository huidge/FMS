package clb.core.workbench.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.workbench.dto.SysWorkbenchModuleCfg;

public interface SysWorkbenchModuleCfgMapper extends Mapper<SysWorkbenchModuleCfg>{
	
	List<SysWorkbenchModuleCfg> selectFunctionInfo(SysWorkbenchModuleCfg sysWorkbenchModuleCfg);

	List<SysWorkbenchModuleCfg> wsSelectFunctionInfo(SysWorkbenchModuleCfg sysWorkbenchModuleCfg);
}