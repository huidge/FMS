package clb.core.order.mapper;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.order.dto.OrdChangeHeader;

import java.util.List;

public interface OrdChangeHeaderMapper extends Mapper<OrdChangeHeader>{
    /**
     * 订单变更查询
     * @param ordChangeHeader
     * @return
     */
    public List<OrdChangeHeader> queryOrdChangeHis(OrdChangeHeader ordChangeHeader);

}