package clb.core.order.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.order.dto.SysOrderFunctionCfg;

public interface ISysOrderFunctionCfgService extends IBaseService<SysOrderFunctionCfg>, ProxySelf<ISysOrderFunctionCfgService>{

	List<SysOrderFunctionCfg> selectFunctionInfo(IRequest requestContext,SysOrderFunctionCfg dto,int page,int pageSize);
	
	List<SysOrderFunctionCfg> queryByHeaderId(Long cfgId);
	
	List<SysOrderFunctionCfg> selectByFunctionId(SysOrderFunctionCfg dto);
}