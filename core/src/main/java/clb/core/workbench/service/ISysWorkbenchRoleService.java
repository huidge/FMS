package clb.core.workbench.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.workbench.dto.SysWorkbenchRole;

public interface ISysWorkbenchRoleService extends IBaseService<SysWorkbenchRole>, ProxySelf<ISysWorkbenchRoleService>{
	
	public List<SysWorkbenchRole> selectAllFields(IRequest requestContext, SysWorkbenchRole sysWorkbenchRole, int page, int pageSize);
}