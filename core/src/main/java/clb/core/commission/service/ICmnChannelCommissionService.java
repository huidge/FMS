package clb.core.commission.service;
/**
 * Created by jiaolong.li on 2017-04-24.
 */

import clb.core.channel.dto.CnlChannel;
import clb.core.channel.dto.CnlChannelContract;
import clb.core.channel.dto.CnlContractRate;
import clb.core.channel.dto.CnlLevel;
import clb.core.commission.dto.CmnChannelCommission;
import clb.core.commission.dto.CmnChannelRatioDetail;
import clb.core.commission.dto.CmnQueryCommission;
import clb.core.commission.dto.CmnSupplierCommission;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import java.util.List;

public interface ICmnChannelCommissionService extends IBaseService<CmnChannelCommission>, ProxySelf<ICmnChannelCommissionService> {

    public List<CmnChannelCommission> selectAllFields(IRequest requestContext,
                                                      CmnChannelCommission cmnChannelCommission,
                                                      int page,
                                                      int pageSize);

    public List<CmnChannelCommission> selectBatchChannelCmn(CmnChannelCommission cmnChannelCommission);

    public List<CmnChannelCommission> selectShowAllFields(IRequest requestContext,
                                                          CmnChannelCommission cmnChannelCommission,
                                                          int page,
                                                          int pageSize);

    public List<CmnChannelCommission> queryLineQty();

    public List<CmnChannelCommission> selectAllFields(CmnChannelCommission cmnChannelCommission);

    public List<CmnChannelCommission> selectCommissionById(CmnChannelCommission cmnChannelCommission);

    public void deleteCmnData(CmnChannelCommission cmnChannelCommission);

    public List<CnlChannelContract> queryContractList(CnlChannelContract cnlChannelContract);

    public List<CnlLevel> queryLevelList(CnlLevel cnlLevel);

    public List<CmnSupplierCommission> querySupplierCmnList(CmnSupplierCommission supplierCommission);

    public List<CnlContractRate> queryContractRateList(CnlContractRate contractRate);

    public List<CnlContractRate> queryTopRateList(CnlContractRate contractRate);

    public List<CmnChannelRatioDetail> queryTopRatioDetailList(CmnChannelRatioDetail channelRatioDetail);

    public List<CnlChannel> queryChildChannelList(CnlChannel cnlChannel);

    public List<CnlChannelContract> queryContractSpeList(CnlChannelContract cnlChannelContract);

    public List<CnlChannelContract> queryContractCnlList(CnlChannelContract cnlChannelContract);

    public List<CmnChannelCommission> queryChannelCmnList(CmnChannelCommission cmnChannelCommission);

    public List<CmnChannelCommission> queryBondList(CmnChannelCommission cmnChannelCommission);

    public List<CmnChannelCommission>  chooseCommission(IRequest request, CmnQueryCommission dto);

    public void processSingleChannelCmn(IRequest request, Long channelId, Long jobBatchId);
    /**
     * 查询转介费率
     * @param requestContext
     * @param dto
     * @param page
     * @param pageSize
     * @return
     */
	public List<CmnChannelCommission> queryReferralFee(IRequest requestContext, CmnChannelCommission dto, int page, int pageSize);

}