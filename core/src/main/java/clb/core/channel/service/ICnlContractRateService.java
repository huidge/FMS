package clb.core.channel.service;

import clb.core.channel.dto.CnlChannelContract;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.channel.dto.CnlContractRate;

import java.util.List;

public interface ICnlContractRateService extends IBaseService<CnlContractRate>, ProxySelf<ICnlContractRateService>{
    List<CnlContractRate> queryRate(IRequest request, CnlContractRate cnlContractRate, int page, int pagesize);
    
    public List<CnlContractRate> queryChannelRate(IRequest request, CnlContractRate cnlContractRate, int page, int pagesize);
    
    public List<CnlContractRate> querySupplierRate(IRequest request, CnlContractRate cnlContractRate, int page, int pagesize);
    
    List<CnlContractRate> queryWsChannelCommission(IRequest request, CnlChannelContract contract);

    public void submitWsCommissionProportion(IRequest request, CnlChannelContract contract, List<CnlContractRate> rateList);

}