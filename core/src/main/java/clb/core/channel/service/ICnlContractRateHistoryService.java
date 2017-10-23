package clb.core.channel.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.channel.dto.CnlContractRateHistory;

public interface ICnlContractRateHistoryService extends IBaseService<CnlContractRateHistory>, ProxySelf<ICnlContractRateHistoryService>{
    /**
     * 费率记录查询
     * @param cnlContractRateHis
     * @return
     */
    public List<CnlContractRateHistory> queryRateHis(IRequest request, CnlContractRateHistory cnlContractRateHistory, int page, int pagesize);
}