package clb.core.order.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import clb.core.order.dto.*;
import com.hand.hap.account.dto.User;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.core.annotation.StdWho;
import com.hand.hap.system.service.IBaseService;

import clb.core.commission.dto.CmnChannelCommission;
import clb.core.production.dto.PrdItems;
import clb.core.supplier.dto.SpeSupplierLine;
import clb.core.system.dto.ClbCodeValue;
import clb.core.system.dto.ClbUser;

public interface IOrdOrderService extends IBaseService<OrdOrder>, ProxySelf<IOrdOrderService>{
    List<OrdOrder> queryOrder(IRequest request, OrdOrder ordOrder, int page, int pagesize);

    List<OrdOrder> queryWsOrder(IRequest request, OrdOrder ordOrder, int page, int pageSize);

    List<OrdOrder> queryWsPersonalOrder(IRequest request, OrdOrder ordOrder, int page, int pagesize);

    Long queryPersonalTotal(IRequest request, OrdOrder ordOrder);

    List<OrdOrder> queryWsTeamOrder(IRequest request, OrdOrder ordOrder, int page, int pageSize);

    /**
     * 转介绍订单查询接口
     * @param request
     * @param ordOrder
     * @return
     */
    List<OrdOrder> queryWsReferralOrder(IRequest request, OrdOrder ordOrder, int page, int pageSize);

    List<ClbCodeValue> queryOrderStatus(IRequest request, ClbCodeValue clbCodeValue);

    List<ClbCodeValue> queryOrderAppStatus(IRequest request, ClbCodeValue clbCodeValue);

    List<OrdOrder> orderBatchUpdate(IRequest request, @StdWho List<OrdOrder> ordOrders);

    List<ClbUser> queryUser(IRequest request, ClbUser clbUser);

    List<SysOrderCfgField> queryField(IRequest request, SysOrderCfgField sysOrderCfgField);

    List<SysOrderCfgOperation> queryOpera(IRequest request, SysOrderCfgOperation sysOrderCfgOperation);

    //List<CmnSupplierRelation> querySupplierRelation(IRequest request, CmnSupplierRelation cmnSupplierRelation);

    List<CmnChannelCommission> queryTradeRoute(IRequest request, CmnChannelCommission cmnChannelCommission);

    List<OrdOrder> queryOrdTradeRoute(IRequest request, OrdOrder ordOrder);

    /**
     * 投资移民订单查询
     * daiqian.shi@hand-china.com
     * @param request
     * @param ordOrder
     * @param page
     * @param pageSize
     * @return
     */
    public List<OrdOrder> queryImmigrantOrder(IRequest request, OrdOrder ordOrder,int page,int pageSize);

    List<OrdOrder> queryBondOrder(IRequest request, OrdOrder ordOrder,int page,int pageSize);

    List<ClbUser> queryRole(IRequest request, ClbUser clbUser);

    List<ClbUser> queryUserRole(IRequest request, ClbUser clbUser);

    List<SpeSupplierLine> queryAddress(IRequest request, SpeSupplierLine speSupplierLine);

    List<SpeSupplierLine> queryContactPerson(IRequest request, SpeSupplierLine speSupplierLine);

    List<SpeSupplierLine> queryContactPhone(IRequest request, SpeSupplierLine speSupplierLine);

    List<OrdOrder> orderStatusUpdate(IRequest request, @StdWho List<OrdOrder> ordOrders);

    OrdOrder updateStatus(IRequest request, OrdOrder ordOrder);

    List<CmnChannelCommission> validateTradeRoute(IRequest request, CmnChannelCommission cmnChannelCommission);

    List<PrdItems> queryItemByClass(PrdItems prdItems);

    void exportPDF(HttpServletRequest request,HttpServletResponse response,String orderId,IRequest requestContext) throws Exception;

    void aiaContractPDF(HttpServletRequest request, HttpServletResponse response, String orderId, IRequest requestContext);

    List<PrdItems> queryClassAmount(IRequest request, PrdItems prdItems);
    /**
     * 查询续保的年数
     * @param dto
     * @return
     */
	List<OrdOrder> queryRenewalYear(OrdOrder dto);

    void importData(IRequest request, List<OrdOrder> ordOrders, List<OrdCustomer> ordCustomers,
                    List<OrdBeneficiary> ordBeneficiaries, List<OrdTradeRoute> ordTradeRoutes,
                    List<OrdTradeRouteShow> ordTradeRouteShows, List<OrdAddition> ordAdditions ,
                    List<OrdCommission> ordCommissions);
    
    OrdOrder selectOrdOrder(Long orderId);
    
    /**
     * 返回身份信息
     * @param nationValueCode
     * @param provinceValueCode
     * @param cityValueCode
     * @return
     */
    String queryIdentityInfo3(String nationValueCode, String provinceValueCode, String cityValueCode);  
    
    String queryIdentityInfo2(String provinceValueCode, String cityValueCode); 
    
    String queryIdentityInfo1(String nationValueCode);
    /**
     * 添加状态日志  需求变更
     * @param requestContext
     * @param ordStatusHis
     */
	void addHisStatus2(IRequest requestContext, OrdStatusHis ordStatusHis);

    /**
     * 获取pending订单
     *
     * @param channelId
     * @return
     */
    Integer getPendingNum(Long channelId);
}