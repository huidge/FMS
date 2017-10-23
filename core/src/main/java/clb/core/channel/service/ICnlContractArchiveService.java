package clb.core.channel.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.channel.dto.CnlContractArchive;

import java.util.List;

public interface ICnlContractArchiveService extends IBaseService<CnlContractArchive>, ProxySelf<ICnlContractArchiveService>{
    List<CnlContractArchive> queryArchive(IRequest request, CnlContractArchive cnlContractArchive, int page, int pagesize);

}