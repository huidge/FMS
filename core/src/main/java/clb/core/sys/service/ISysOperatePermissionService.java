package clb.core.sys.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.common.exceptions.CommonException;
import clb.core.supplier.exceptions.SpeException;
import clb.core.sys.dto.SysOperatePermission;

public interface ISysOperatePermissionService extends IBaseService<SysOperatePermission>, ProxySelf<ISysOperatePermissionService>{

	List<SysOperatePermission> operatePermissionBatchUpdate(IRequest iRequest,List<SysOperatePermission> data) throws CommonException;
	
}