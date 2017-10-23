package clb.core.channel.service;

import clb.core.channel.dto.CnlChannelContact;
import clb.core.channel.dto.CnlLevel;
import clb.core.channel.dto.CnlPaymentTerm;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.core.annotation.StdWho;
import com.hand.hap.system.service.IBaseService;
import clb.core.channel.dto.CnlChannelContract;

import java.util.List;

public interface ICnlChannelContractService extends IBaseService<CnlChannelContract>, ProxySelf<ICnlChannelContractService>{
    List<CnlChannelContract> queryContract(IRequest request, CnlChannelContract cnlChannelContract, int page, int pagesize);
    List<CnlChannelContract> queryContractPriv(IRequest request, CnlChannelContract cnlChannelContract, int page, int pagesize);
    List<CnlChannelContract> contractBatchUpdate(IRequest request, @StdWho List<CnlChannelContract> cnlChannelContracts);
    List<CnlLevel> queryLevel(IRequest request, CnlLevel cnlLevel);
    List<CnlPaymentTerm> queryPaymentTerm(IRequest request, CnlPaymentTerm cnlPaymentTerm);
    List<CnlChannelContract> selectByCondition(CnlChannelContract cnlChannelContract);
    
    /**
     * 渠道导入程序
     * @param dto
     * @return
     */
    List<CnlChannelContract> queryChannelContractByPartCodeAndPartyName(CnlChannelContract dto);

    public void updateRatioLevel(CnlChannelContract cnlChannelContract);
}