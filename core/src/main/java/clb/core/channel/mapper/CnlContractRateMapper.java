package clb.core.channel.mapper;

import clb.core.channel.dto.CnlChannelContract;
import com.hand.hap.mybatis.common.Mapper;
import clb.core.channel.dto.CnlContractRate;

import java.util.List;

public interface CnlContractRateMapper extends Mapper<CnlContractRate>{
    /**
     * 费率查询
     * @param cnlContractRate
     * @return
     */
    public List<CnlContractRate> queryRate(CnlContractRate cnlContractRate);
    
    /**
     * 渠道费率查询
     * @param cnlContractRate
     * @return
     */
    public List<CnlContractRate> queryChannelRate(CnlContractRate cnlContractRate);
    /**
     * 供应商费率查询
     * @param cnlContractRate
     * @return
     */
    public List<CnlContractRate> querySupplierRate(CnlContractRate cnlContractRate);

    /**
     * 查询团队成员对应的对应的佣金分成（上级渠道查下级）
     * @param cnlChannelContract
     * @return
     */
    public List<CnlContractRate> queryTeamCommission(CnlChannelContract cnlChannelContract);/**
     * 用于查询是否存在重复数据
     * @param cnlContractRate
     * @return
     */
    public List<CnlContractRate> selectRateByNull(CnlContractRate cnlContractRate);
}