package clb.core.system.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.system.dto.SysUserCustomFunction;

import java.util.List;

public interface ISysUserCustomFunctionService extends IBaseService<SysUserCustomFunction>, ProxySelf<ISysUserCustomFunctionService> {

    public List<SysUserCustomFunction> queryOwnFuncs(SysUserCustomFunction sysUserCustomFunction);

    public List<SysUserCustomFunction> queryAvailableFuncs(SysUserCustomFunction sysUserCustomFunction);

    public void deleteFuncs(SysUserCustomFunction sysUserCustomFunction);

    public void initFuncs(SysUserCustomFunction sysUserCustomFunction, IRequest requestContext);
}