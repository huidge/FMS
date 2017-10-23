package clb.core.system.mapper;

import clb.core.system.dto.ClbUser;
import com.hand.hap.mybatis.common.Mapper;
import clb.core.system.dto.SysUserCustomFunction;

import java.util.List;

public interface SysUserCustomFunctionMapper extends Mapper<SysUserCustomFunction>{

    public List<SysUserCustomFunction> queryOwnFuncs(SysUserCustomFunction sysUserCustomFunction);

    public List<SysUserCustomFunction> queryAvailableFuncs(SysUserCustomFunction sysUserCustomFunction);

    public void deleteFuncs(SysUserCustomFunction sysUserCustomFunction);
}