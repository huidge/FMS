package clb.core.workbench.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.workbench.dto.SysWorkbenchRole;
import clb.core.workbench.mapper.SysWorkbenchRoleMapper;
import clb.core.workbench.service.ISysWorkbenchRoleService;

@Service
@Transactional
public class SysWorkbenchRoleServiceImpl extends BaseServiceImpl<SysWorkbenchRole> implements ISysWorkbenchRoleService{
	
	 @Autowired
	    private SysWorkbenchRoleMapper sysWorkbenchRoleMapper;
	
	@Override
	public List<SysWorkbenchRole> selectAllFields(IRequest requestContext, SysWorkbenchRole sysWorkbenchRole, int page,
			int pageSize) {
		PageHelper.startPage(page, pageSize);
        return sysWorkbenchRoleMapper.selectAllFields(sysWorkbenchRole);
		//return null;
	}

}