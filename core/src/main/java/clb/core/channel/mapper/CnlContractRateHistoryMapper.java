package clb.core.channel.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.channel.dto.CnlContractRateHistory;

public interface CnlContractRateHistoryMapper extends Mapper<CnlContractRateHistory>{
    /**
     * 费率记录查询
     * @param cnlContractRateHis
     * @return
     */
    public List<CnlContractRateHistory> queryRateHis(CnlContractRateHistory cnlContractRateHis);
}