package clb.core.commission.mapper;
/**
 * Created by jiaolong.li on 2017-04-24.
 */

import clb.core.channel.dto.CnlChannel;
import clb.core.channel.dto.CnlChannelContract;
import clb.core.channel.dto.CnlContractRate;
import clb.core.channel.dto.CnlLevel;
import clb.core.commission.dto.CmnChannelCommission;
import clb.core.commission.dto.CmnChannelRatioDetail;
import clb.core.commission.dto.CmnSupplierCommission;
import com.hand.hap.mybatis.common.Mapper;
import org.springframework.stereotype.Service;

import java.util.List;

public interface CmnChannelCommissionMapper extends Mapper<CmnChannelCommission> {

    public List<CmnChannelCommission> selectAllFields(CmnChannelCommission cmnChannelCommission);

    public List<CmnChannelCommission> selectBatchChannelCmn(CmnChannelCommission cmnChannelCommission);

    public List<CmnChannelCommission> selectShowAllFields(CmnChannelCommission cmnChannelCommission);

    public List<CmnChannelCommission> queryLineQty();

    public List<CmnChannelCommission> selectCommissionById(CmnChannelCommission cmnChannelCommission);

    public List<CmnChannelCommission> chooseCommission(CmnChannelCommission cmnChannelCommission);

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
    /**
     * 转介费率查询
     * @param dto
     * @return
     */
	public List<CmnChannelCommission> queryReferralFee(CmnChannelCommission dto);
    /**
     * 查询对应渠道的交易路线
     * @param cmnChannelCommission
     * @return
     */
    public List<CmnChannelCommission> selectChannelRouter(CmnChannelCommission cmnChannelCommission);
}