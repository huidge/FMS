package clb.core.order.mapper;

import clb.core.commission.dto.CmnChannelCommission;
import clb.core.order.dto.OrdTradeRoute;
import clb.core.order.dto.SysOrderCfgField;
import clb.core.order.dto.SysOrderCfgOperation;
import clb.core.production.dto.PrdItems;
import clb.core.supplier.dto.SpeSupplierLine;
import clb.core.supplier.dto.SpeSupplierSettle;
import clb.core.system.dto.ClbCodeValue;
import clb.core.system.dto.ClbUser;
import com.hand.hap.account.dto.User;
import com.hand.hap.core.IRequest;
import com.hand.hap.mybatis.common.Mapper;
import clb.core.order.dto.OrdOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrdOrderMapper extends Mapper<OrdOrder>{
    /**
     * 订单查询
     * @param ordOrder
     * @return
     */
    public List<OrdOrder> queryOrder(OrdOrder ordOrder);

    /**
     * 订单接口查询
     * @param ordOrder
     * @return
     */
    public List<OrdOrder> queryWsOrder(OrdOrder ordOrder);

    /**
     * 订单接口个人查询
     * @param ordOrder
     * @return
     */
    public List<OrdOrder> queryWsPersonalOrder(OrdOrder ordOrder);


    public Long queryPersonalTotal(OrdOrder ordOrder);

    /**
     * 订单接口团队查询
     * @param ordOrder
     * @return
     */
    public List<OrdOrder> queryWsTeamOrder(OrdOrder ordOrder);

    /**
     * 转介绍订单查询接口
     * @param ordOrder
     * @return
     */
    public List<OrdOrder> queryWsReferralOrder(OrdOrder ordOrder);

    /**
     * 用户查询
     * @param clbUser
     * @return
     */
    public List<ClbUser> queryUser(ClbUser clbUser);

    /**
     * 字段查询
     * @param sysOrderCfgField
     * @return
     */
    public List<SysOrderCfgField> queryField(SysOrderCfgField sysOrderCfgField);

    /**
     * 操作查询
     * @param sysOrderCfgOperation
     * @return
     */
    public List<SysOrderCfgOperation> queryOpera(SysOrderCfgOperation sysOrderCfgOperation);

    /**
     * 供应商层次查询
     * @param cmnSupplierRelation
     * @return
     */
    //public List<CmnSupplierRelation> querySupplierRelation(CmnSupplierRelation cmnSupplierRelation);

    /**
     * 交易路线
     * @param cmnChannelCommission
     * @return
     */
    public List<CmnChannelCommission> queryTradeRoute(CmnChannelCommission cmnChannelCommission);

    /**
     * 订单交易路线
     * @param ordOrder
     * @return
     */
    public List<OrdOrder> queryOrdTradeRoute(OrdOrder ordOrder);

    /**
     * 订单渠道关系
     * @param ordTradeRoute
     * @return
     */
    public List<OrdTradeRoute> queryChannelRelation(OrdTradeRoute ordTradeRoute);

    /**
     * 订单状态
     * @param clbCodeValue
     * @return
     */
    public List<ClbCodeValue> queryOrderStatus(ClbCodeValue clbCodeValue);

    /**
     * 订单申请书状态
     * @param clbCodeValue
     * @return
     */
    public List<ClbCodeValue> queryOrderAppStatus(ClbCodeValue clbCodeValue);

    /**
     * 供应商费用查询
     * @param speSupplierSettle
     * @return
     */
    public List<SpeSupplierSettle> querySpeSupplierSettle(SpeSupplierSettle speSupplierSettle);

    /**
     * 投资移民订单查询
     * daiqian.shi@hand-china.com
     * @param ordOrder
     * @return
     */
    public List<OrdOrder> queryImmigrantOrder(OrdOrder ordOrder);

    /**
     * 债券订单查询
     * jun.zhao@hand-china.com
     * @param ordOrder
     * @return
     */
    public List<OrdOrder> queryBondOrder(OrdOrder ordOrder);

    /**
     * 各角色查询
     * @param clbUser
     * @return
     */
    public List<ClbUser> queryRole(ClbUser clbUser);

    /**
     * 查询用户及角色
     * @param clbUser
     * @return
     */
    public List<ClbUser> queryUserRole(ClbUser clbUser);

    /**
     * 地点查询
     * @param speSupplierLine
     * @return
     */
    public List<SpeSupplierLine> queryAddress (SpeSupplierLine speSupplierLine);

    /**
     * 联络人查询
     * @param speSupplierLine
     * @return
     */
    public List<SpeSupplierLine> queryContactPerson (SpeSupplierLine speSupplierLine);

    /**
     * 电话查询
     * @param speSupplierLine
     * @return
     */
    public List<SpeSupplierLine> queryContactPhone (SpeSupplierLine speSupplierLine);

    /**
     * 验证交易路线
     * @param cmnChannelCommission
     * @return
     */
    public List<CmnChannelCommission> validateTradeRoute(CmnChannelCommission cmnChannelCommission);
    /**
     * 查询年期
     * @param order
     * @return
     */
    public List<OrdOrder> querySublineItemName(OrdOrder order);
    
    /**
     * 选择所有的订单
     * @return
     */
    public OrdOrder selectOrdOrder(@Param(value="orderId")Long orderId);

    /**
     * 查询产品大类种类金额
     * @param prdItems
     * @return
     */
    public List<PrdItems> queryClassAmount(PrdItems prdItems);
    /**
     * 查询续保的年数
     * @param dto
     * @return
     */
	public List<OrdOrder> queryRenewalYear(OrdOrder dto);

    /**
     * 获取pending订单
     *
     * @param channelId
     * @return
     */
    List<OrdOrder> getPendingNum(Long channelId);
}