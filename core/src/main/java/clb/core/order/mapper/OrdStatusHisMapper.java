package clb.core.order.mapper;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.order.dto.OrdStatusHis;

import java.util.List;

public interface OrdStatusHisMapper extends Mapper<OrdStatusHis>{
    /**
     * 状态跟进查询
     * @param ordStatusHis
     * @return
     */
    public List<OrdStatusHis> queryOrdStatusHis(OrdStatusHis ordStatusHis);

    /**
     * 状态跟进查询
     * @param ordStatusHis
     * @return
     */
    public List<OrdStatusHis> queryOrdStatusHisAll(OrdStatusHis ordStatusHis);

    /**
     * 接口查询订单状态跟进
     * daiqian.shi@hand-china.com
     * @param ordStatusHis
     * @return
     */
    public List<OrdStatusHis> queryWsOrdStatusHis(OrdStatusHis ordStatusHis);

    /**
     * 查询状态跟进订单ID及时间
     * @param ordStatusHis
     * @return
     */
    public List<OrdStatusHis> queryOrdStatusHisSimple(OrdStatusHis ordStatusHis);
}