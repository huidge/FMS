package clb.core.order.service.impl;

import clb.core.order.dto.OrdChooseItem;
import clb.core.order.dto.OrdFile;
import clb.core.order.mapper.OrdChooseItemMapper;
import clb.core.order.mapper.OrdFileMapper;
import clb.core.order.service.IOrdChooseItemService;
import clb.core.order.service.IOrdFileService;
import clb.core.system.service.IClbUserService;
import com.github.pagehelper.PageHelper;
import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.attachment.service.ISysFileService;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrdChooseItemServiceImpl implements IOrdChooseItemService {

    @Autowired
    private OrdChooseItemMapper ordChooseItemMapper;

    @Autowired
    private IClbUserService userService;

    /**
     * 查询可用产品
     * @param ordChooseItem
     * @return
     */
    public List<OrdChooseItem> queryOrdItem(IRequest request, OrdChooseItem ordChooseItem,
                                            int page, int pagesize) {
        PageHelper.startPage(page, pagesize);

        List<OrdChooseItem> ordItemList = ordChooseItemMapper.queryOrdItem(ordChooseItem);
        if (userService.isDaiBan(request)) {
            for (OrdChooseItem item : ordItemList) {
                if (!"".equals(item.getTheFirstYear()) && item.getTheFirstYear() != null) {
                    item.setTheFirstYear("XXX");
                }
            }
        }
        return  ordItemList;
    }

    /**
     * 查询可用产品
     * @param ordChooseItem
     * @return
     */
    public List<OrdChooseItem> queryOrdItem(IRequest request, OrdChooseItem ordChooseItem) {

        List<OrdChooseItem> ordItemList = ordChooseItemMapper.queryOrdItem(ordChooseItem);
        if (userService.isDaiBan(request)) {
            for (OrdChooseItem item : ordItemList) {
                item.setTheFirstYear("XXX");
            }
        }
        return ordItemList;
    }

    /**
     * 在渠道供应商商品关系上有限制
     * @param request
     * @param ordChooseItem
     * @return
     */
    public List<OrdChooseItem> queryOrdItemLimit(IRequest request, OrdChooseItem ordChooseItem) {
        List<OrdChooseItem> ordItemList = ordChooseItemMapper.queryOrdItemLimit(ordChooseItem);
        if (userService.isDaiBan(request)) {
            for (OrdChooseItem item : ordItemList) {
                item.setTheFirstYear("XXX");
            }
        }
        return ordItemList;
    }
}