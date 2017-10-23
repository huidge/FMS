package clb.core.workbench.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.workbench.dto.SysWorkbenchRole;

public interface SysWorkbenchRoleMapper extends Mapper<SysWorkbenchRole>{
	
	public List<SysWorkbenchRole> selectAllFields(SysWorkbenchRole sysWorkbenchRole);
}