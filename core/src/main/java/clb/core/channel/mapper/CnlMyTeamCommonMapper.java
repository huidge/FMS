package clb.core.channel.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.channel.dto.CnlChannel;
import clb.core.channel.dto.CnlChannelContract;
import clb.core.channel.dto.CnlContractRate;
import clb.core.forecast.dto.FetReceivable;
import clb.core.order.dto.OrdCommission;
import clb.core.order.dto.OrdOrder;
import clb.core.system.dto.ClbCodeValue;

public interface CnlMyTeamCommonMapper{
    /**
     * 成员查询
     * @param CnlChannelContract
     * @return
     */
    public List<CnlChannelContract> queryMember(CnlChannelContract channelContract);
    
    /**
     * 跟进中查询
     * @param OrdCommission
     * 跟进状态
     * @param followingStatus
     * 订单类型
     * @param orderType
     * @return
     */
    public List<OrdCommission> queryFollow(@Param(value="ordCommission")OrdCommission ordCommission,@Param(value="followingStatus")List<ClbCodeValue> followingStatus,@Param(value="orderType")String orderType);
    
    /**
     * 获取当前渠道和下级渠道订单数据
     * @param OrdCommission
     * 跟进状态
     * @return
     */
    public List<OrdOrder> queryAllOrderData(@Param(value="companyType")String companyType,@Param(value="companyId")Long companyId);
    
    
    /**
     * 获取当前渠道和下级渠道订单数据
     * @param OrdCommission
     * 跟进状态
     * @return
     */
    public List<FetReceivable> queryReceivableData(@Param(value="receivable")FetReceivable receivable,@Param(value="orders")List<OrdOrder> orders);
    
    /**
     * 获取当前渠道和下级渠道订单数据
     * @param OrdCommission
     * 跟进状态
     * @return
     */
    public List<OrdOrder> queryOrderDataByChannelId(OrdOrder order);
}