package clb.core.production.service;

import clb.core.production.dto.PrdItemPaymode;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import java.util.List;

public interface IPrdItemPaymodeService extends IBaseService<PrdItemPaymode>, ProxySelf<IPrdItemPaymodeService>{

   List<PrdItemPaymode> query(IRequest iRequest, PrdItemPaymode dto, int page, int pagesize);

   List<PrdItemPaymode> batchUpdate(IRequest request, List<PrdItemPaymode> dtoList);
   /**
    * 在电子计划书模块  需要根据产品查询产品下的货币
    * @param requestContext
    * @param dto
    * @return
    */
   List<PrdItemPaymode> queryCurrencyByItemId(IRequest requestContext, PrdItemPaymode dto);

   List<PrdItemPaymode> queryAllCurrency(IRequest requestContext, PrdItemPaymode dto);
}