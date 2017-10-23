package clb.core.workbench.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.workbench.dto.SysWorkbenchModuleCfg;

public interface ISysWorkbenchModuleCfgService extends IBaseService<SysWorkbenchModuleCfg>, ProxySelf<ISysWorkbenchModuleCfgService>{
	
	List<SysWorkbenchModuleCfg> selectFunctionInfo(IRequest requestContext,SysWorkbenchModuleCfg dto,int page,int pageSize);

	List<SysWorkbenchModuleCfg> wsSelectFunctionInfo(IRequest requestContext, SysWorkbenchModuleCfg dto, int page, int pageSize);
}