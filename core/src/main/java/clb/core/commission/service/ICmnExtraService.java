package clb.core.commission.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.commission.dto.CmnExtra;

public interface ICmnExtraService extends IBaseService<CmnExtra>, ProxySelf<ICmnExtraService>{

    List<CmnExtra> queryBasic(IRequest requestContext, CmnExtra dto, int page,
                                 int pageSize);

    CmnExtra queryMaxVersion(CmnExtra cmnExtra);
    
    CmnExtra queryYTDate(CmnExtra cmnExtra);

    List<CmnExtra> addVersion(IRequest request, CmnExtra dto, Long oldVersion);
    
    public List<CmnExtra> queryExtraBasic(IRequest request, CmnExtra dto, int page, int pageSize);
}