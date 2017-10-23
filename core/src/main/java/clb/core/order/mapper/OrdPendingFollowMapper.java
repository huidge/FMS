package clb.core.order.mapper;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.order.dto.OrdPendingFollow;

import java.util.List;

public interface OrdPendingFollowMapper extends Mapper<OrdPendingFollow>{
    /**
     * 订单Pending跟进查询
     * @param ordPendingFollow
     * @return
     */
    public List<OrdPendingFollow> queryOrdPendingFollow(OrdPendingFollow ordPendingFollow);
}