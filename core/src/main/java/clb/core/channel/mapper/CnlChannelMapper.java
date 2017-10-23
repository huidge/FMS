package clb.core.channel.mapper;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.channel.dto.CnlChannel;
import java.util.List;

public interface CnlChannelMapper extends Mapper<CnlChannel>{
    /**
     * 渠道汇总查询
     * @param cnlChannel
     * @return
     */
    public List<CnlChannel> queryChannelSummary(CnlChannel cnlChannel);
    public List<CnlChannel> queryChannelSummaryPriv(CnlChannel cnlChannel);
    public List<CnlChannel> queryWsChannel(CnlChannel cnlChannel);

    /**
     * 渠道地区查询
     * @param cnlChannel
     * @return
     */
    public List<CnlChannel> queryArea(CnlChannel cnlChannel);

    List<CnlChannel> queryChannelSimple(CnlChannel dto);
  
    /**
     * 查询渠道名称   by 订单上一级渠道与所属供应商合约id
     * @param contractId
     * @return
     */
	public String queryChannelNameByContractId(Long contractId);

    List<CnlChannel> queryAllCnlWithFilePath();
    public List<CnlChannel> queryChannelByChannelName(CnlChannel cnlChannel);
}