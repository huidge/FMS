package clb.core.order.mapper;

import clb.core.order.dto.OrdOrder;
import clb.core.system.dto.ClbUser;
import com.hand.hap.mybatis.common.Mapper;
import clb.core.order.dto.OrdPending;

import java.util.List;

public interface OrdPendingMapper extends Mapper<OrdPending>{
    /**
     * 订单Pending查询
     * @param ordPending
     * @return
     */
    public List<OrdPending> queryOrdPending(OrdPending ordPending);

    /**
     * 订单Pending查询
     * @param ordPending
     * @return
     */
    public List<OrdPending> queryWsOrdPending(OrdPending ordPending);

    /**
     * 订单Pending个人查询
     * @param ordPending
     * @return
     */
    public List<OrdPending> queryWsPersonalOrdPending(OrdPending ordPending);

    /**
     * 订单Pending个人查询需补资料条数
     * @param ordPending
     * @return
     */
    public Long queryOrdPendingTotle(OrdPending ordPending);

    /**
     * 订单Pending团队查询
     * @param ordPending
     * @return
     */
    public List<OrdPending> queryWsTeamOrdPending(OrdPending ordPending);

    /**
     * 统计订单下的pending数量
     * @param orderId
     * @return
     */
    public Long countPendingByOrderId(Long orderId);

    /**
     * 查询处理人
     * @param ordOrder
     * @return
     */
    public List<ClbUser> queryDealUser(OrdOrder ordOrder);

    /**
     * 订单未关闭的Pending查询
     * @param ordPending
     * @return
     */
    public List<OrdPending> queryNotClosed(OrdPending ordPending);


}