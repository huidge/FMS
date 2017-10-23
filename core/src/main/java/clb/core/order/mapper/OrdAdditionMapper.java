package clb.core.order.mapper;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.order.dto.OrdAddition;

import java.util.List;

public interface OrdAdditionMapper extends Mapper<OrdAddition>{
    /**
     * 附加险查询
     * @param ordAddition
     * @return
     */
    public List<OrdAddition> queryOrdAddition(OrdAddition ordAddition);

    /**
     * 附加险查询
     * @param ordAddition
     * @return
     */
    public List<OrdAddition> queryNotAddition(OrdAddition ordAddition);

    /**
     * 附加险信息查询接口
     * @param ordAddition
     * @return
     */
    public List<OrdAddition> queryWsOrdAddition(OrdAddition ordAddition);
    /**
     * 修改附加险的标识和保存状态  通过订单id和产品id
     * @param addition
     */
	public void updateOrdAdditionByOrderIdAndItemId(OrdAddition addition);
	/**
	 * 修改附加险的状态  通过订单id和产品id
	 * @param addition
	 */
	public void updateStatusByOrderId(OrdAddition addition);
	/**
	 * 查询保存的临时状态
	 * @param ordAddition
	 * @return
	 */
	public String querySaveStatusByOrderIdAndItemId(OrdAddition ordAddition);

}