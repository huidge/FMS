package clb.core.order.mapper;

import clb.core.order.dto.OrdChooseItem;
import clb.core.order.dto.OrdFile;
import com.hand.hap.mybatis.common.Mapper;

import java.util.List;

public interface OrdChooseItemMapper {
    /**
     * 查询可用产品
     * @param ordChooseItem
     * @return
     */
    public List<OrdChooseItem> queryOrdItem(OrdChooseItem ordChooseItem);

    /**
     * 查询可用产品(加上供应商渠道产品关系限制)
     * @param ordChooseItem
     * @return
     */
    public List<OrdChooseItem> queryOrdItemLimit(OrdChooseItem ordChooseItem);
}