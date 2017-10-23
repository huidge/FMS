package clb.core.production.service;

import clb.core.production.dto.PrdItemSelfpay;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import java.util.List;

public interface IPrdItemSelfpayService extends IBaseService<PrdItemSelfpay>, ProxySelf<IPrdItemSelfpayService>{

    List<PrdItemSelfpay> query(IRequest iRequest, PrdItemSelfpay dto, int page, int pagesize);

    List<PrdItemSelfpay> batchUpdate(IRequest request, List<PrdItemSelfpay> dtoList);
    
    /**
     * 产品自付查询
     * @param itemId
     * @return
     */
    List<PrdItemSelfpay> selectByItemId(Long itemId);

    List<PrdItemSelfpay> querySelfpay(IRequest requestContext, PrdItemSelfpay querySelfpay);
    
    Long querySelfpayId(String selfpay, Long itemId);
}