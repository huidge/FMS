package clb.core.commission.service;

import clb.core.commission.dto.CmnBasic;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.commission.dto.CmnExtras;

import java.util.List;

public interface ICmnExtrasService extends IBaseService<CmnExtras>, ProxySelf<ICmnExtrasService>{

    List<CmnExtras> queryExtras(IRequest requestContext, CmnExtras dto, int page,
                              int pageSize);

    List<CmnExtras> queryExtras(CmnExtras dto);

    Long queryMaxVersion(CmnExtras cmnExtras);

    List<CmnExtras> addVersion(IRequest request, CmnExtras dto, Long oldVersion);
    
    /****
     * 校验数据
     * @param request
     * @param dto
     */
    public boolean checkData(IRequest request,CmnExtras dto);
}