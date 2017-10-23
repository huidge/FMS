package clb.core.order.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.order.dto.OrdStatusHis;

import java.util.List;

public interface IOrdStatusHisService extends IBaseService<OrdStatusHis>, ProxySelf<IOrdStatusHisService>{

    List<OrdStatusHis> queryOrdStatusHis(IRequest request, OrdStatusHis ordStatusHis, int page, int pagesize);

    List<OrdStatusHis> queryOrdStatusHisAll(IRequest request, OrdStatusHis ordStatusHis, int page, int pagesize);

    List<OrdStatusHis> queryAllOrdStatusHis(IRequest request, OrdStatusHis ordStatusHis);

    /**
     * 接口查询订单状态跟进
     * daiqian.shi@hand-china.com
     * @param request
     * @param ordStatusHis
     * @return
     */
    public List<OrdStatusHis> queryWsOrdStatusHis(IRequest request,OrdStatusHis ordStatusHis);

}