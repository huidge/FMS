package clb.core.channel.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.channel.dto.CnlContractRole;

import java.util.List;

public interface ICnlContractRoleService extends IBaseService<CnlContractRole>, ProxySelf<ICnlContractRoleService>{
    List<CnlContractRole> queryCnlContractRole(IRequest request, CnlContractRole cnlContractRole, int page, int pagesize);

}