package clb.core.order.mapper;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.order.dto.OrdChangeLine;

import java.util.List;

public interface OrdChangeLineMapper extends Mapper<OrdChangeLine>{
    /**
     * 订单变更查询
     * @param ordChangeLine
     * @return
     */
    public List<OrdChangeLine> queryOrdChangeLineByHeaderId(Long headerId);

}