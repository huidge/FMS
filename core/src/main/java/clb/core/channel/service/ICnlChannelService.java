package clb.core.channel.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.core.annotation.StdWho;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;

import clb.core.channel.dto.CnlChannel;
import clb.core.channel.service.impl.CnlChannelServiceImpl.ReturnData;
import clb.core.common.exceptions.CommonException;

public interface ICnlChannelService extends IBaseService<CnlChannel>, ProxySelf<ICnlChannelService>{
    List<CnlChannel> queryChannelSummary(IRequest request, CnlChannel cnlChannel, int page, int pagesize);
    List<CnlChannel> queryChannelSummaryPriv(IRequest request, CnlChannel cnlChannel, int page, int pagesize);
    List<CnlChannel> queryChannel(IRequest request, CnlChannel cnlChannel);
    List<ReturnData> queryWsChannel(IRequest request, CnlChannel cnlChannel) throws CommonException;
    List<CnlChannel> queryArea(IRequest request, CnlChannel cnlChannel);
    //CnlChannel createCnlChannel(CnlChannel cnlChannel);
    //CnlChannel updateCnlChannel(CnlChannel cnlChannel);
    ResponseData channelBatchUpdate(IRequest request, @StdWho List<CnlChannel> cnlChannels);

    List<CnlChannel> queryChannelSimple(IRequest request, CnlChannel dto);
    /**
     * 查询渠道名称   by 订单上一级渠道与所属供应商合约id
     * @param request
     * @param contractId
     * @return
     */
    String queryChannelNameByContractId(IRequest request, Long contractId);

    List<CnlChannel> queryAllCnlWithFilePath();
    List<CnlChannel> queryChannelByChannelName(CnlChannel cnlChannel);
}