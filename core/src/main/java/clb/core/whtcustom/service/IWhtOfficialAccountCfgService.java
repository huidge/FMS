package clb.core.whtcustom.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.whtcustom.dto.WhtOfficialAccountCfg;

public interface IWhtOfficialAccountCfgService extends IBaseService<WhtOfficialAccountCfg>, ProxySelf<IWhtOfficialAccountCfgService>{
	
	List<WhtOfficialAccountCfg> selectAllField(IRequest requestContext, WhtOfficialAccountCfg whtOfficialAccountCfg ,int page, int pagesize);
	
	List<WhtOfficialAccountCfg> selectAll(IRequest requestContext, WhtOfficialAccountCfg whtOfficialAccountCfg);
	
	List<WhtOfficialAccountCfg> selectAccountName(IRequest requestContext, WhtOfficialAccountCfg whtOfficialAccountCfg);
}