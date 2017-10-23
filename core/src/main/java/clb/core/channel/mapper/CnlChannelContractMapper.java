package clb.core.channel.mapper;

import clb.core.channel.dto.*;
import com.hand.hap.mybatis.common.Mapper;

import java.util.List;

public interface CnlChannelContractMapper extends Mapper<CnlChannelContract>{
    /**
     * 渠道合约查询
     * @param cnlChannelContract
     * @return
     */
    public List<CnlChannelContract> queryContract(CnlChannelContract cnlChannelContract);
    public List<CnlChannelContract> queryContractPriv(CnlChannelContract cnlChannelContract);

    /**
     * 等级查询
     * @param cnlLevel
     * @return
     */
    public List<CnlLevel> queryLevel(CnlLevel cnlLevel);

    /**
     * 付款条件查询
     * @param cnlPaymentTerm
     * @return
     */
    public List<CnlPaymentTerm> queryPaymentTerm(CnlPaymentTerm cnlPaymentTerm);

    /**
     * 根据条件查询
     * @param cnlChannelContract
     * @return
     */
    public List<CnlChannelContract> selectByCondition(CnlChannelContract cnlChannelContract);

    /**
     * 根据日期查询上一天新增的数据
     * @param dateString
     * @return
     */
    public List<CnlChannelContract> queryByDate(String dateString);
    /**
     * 渠道导入程序
     * @param 
     * @return
     */
    public List<CnlChannelContract> queryChannelContractByPartCodeAndPartyName(CnlChannelContract dto);

    public List<CnlChannelContractSimple> queryContractSimple(CnlChannelContract dto);

    public void updateRatioLevel(CnlChannelContract cnlChannelContract);

}