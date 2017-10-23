package clb.core.order.mapper;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.forecast.dto.FetSupposeCommon;
import clb.core.order.dto.OrdCommission;
import clb.core.order.dto.OrdOrder;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface OrdCommissionMapper extends Mapper<OrdCommission>{
    /**
     * 订单佣金查询
     * @param ordCommission
     * @return
     */
    public List<OrdCommission> queryOrdCommission(OrdCommission ordCommission);

    public List<OrdCommission> queryOrdCommissionAll(OrdCommission ordCommission);

    /**
     * 接口查询佣金信息
     * @param ordCommission
     * @return
     */
    public List<OrdCommission> queryWsOrdCommission(OrdCommission ordCommission);

    /**
     * 订单佣金主体
     * @param ordCommission
     * @return
     */
    public List<OrdCommission> queryCompany(OrdCommission ordCommission);
    
    
    /**
     * 查询佣金信息和付款方/收款方
     * @param ordCommission
     * @return
     */
    public List<OrdCommission> queryCommissionData(OrdCommission ordCommission);
    
    /**
     * 根据订单Id查询佣金信息和付款方/收款方
     * @param orders
     * @return
     */
    public List<OrdCommission> queryCommissionDataByOrderIds(@Param(value="orders")List<FetSupposeCommon> orders);

    /**
     * 查询手工标记
     * @param ordCommission
     * @return
     */
    public List<OrdCommission> queryManualFlag(OrdCommission ordCommission);

}