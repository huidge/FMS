package clb.core.order.service;

import clb.core.order.dto.OrdChooseItem;
import clb.core.order.dto.OrdFile;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import java.util.List;

public interface IOrdChooseItemService extends ProxySelf<IOrdChooseItemService>{

    /**
     * 查询可用产品
     * @param ordChooseItem
     * @return
     */
    public List<OrdChooseItem> queryOrdItem(IRequest request, OrdChooseItem ordChooseItem,
                                            int page, int pagesize);

    /**
     * 查询可用产品
     * @param ordChooseItem
     * @return
     */
    public List<OrdChooseItem> queryOrdItem(IRequest request, OrdChooseItem ordChooseItem);

    /**
     * 在渠道供应商商品关系上有限制
     * @param request
     * @param ordChooseItem
     * @return
     */
    public List<OrdChooseItem> queryOrdItemLimit(IRequest request, OrdChooseItem ordChooseItem);
}