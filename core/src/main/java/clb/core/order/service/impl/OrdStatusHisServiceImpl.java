package clb.core.order.service.impl;

import clb.core.order.mapper.OrdStatusHisMapper;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import clb.core.order.dto.OrdStatusHis;
import clb.core.order.service.IOrdStatusHisService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrdStatusHisServiceImpl extends BaseServiceImpl<OrdStatusHis> implements IOrdStatusHisService{
    @Autowired
    private OrdStatusHisMapper ordStatusHisMapper;

    /**
     * 查询
     * @param request
     * @param ordStatusHis
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public List<OrdStatusHis> queryOrdStatusHis(IRequest request, OrdStatusHis ordStatusHis, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return ordStatusHisMapper.queryOrdStatusHis(ordStatusHis);
    }

    /**
     * 查询
     * @param request
     * @param ordStatusHis
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public List<OrdStatusHis> queryOrdStatusHisAll(IRequest request, OrdStatusHis ordStatusHis, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return ordStatusHisMapper.queryOrdStatusHisAll(ordStatusHis);
    }

    /**
     * 查询
     * @param request
     * @param ordStatusHis
     * @return
     */
    @Override
    public List<OrdStatusHis> queryAllOrdStatusHis(IRequest request, OrdStatusHis ordStatusHis) {
        return ordStatusHisMapper.queryOrdStatusHis(ordStatusHis);
    }

    /**
     * 接口查询订单状态跟进
     *daiqian.shi@hand-china.com
     * @param request
     * @param ordStatusHis
     * @return
     */
    @Override
    public List<OrdStatusHis> queryWsOrdStatusHis(IRequest request, OrdStatusHis ordStatusHis) {
        return ordStatusHisMapper.queryWsOrdStatusHis(ordStatusHis);
    }

}