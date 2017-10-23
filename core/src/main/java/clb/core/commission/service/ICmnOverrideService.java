package clb.core.commission.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.commission.dto.CmnOverride;

import java.util.List;

public interface ICmnOverrideService extends IBaseService<CmnOverride>, ProxySelf<ICmnOverrideService>{

    List<CmnOverride> queryBasic(IRequest requestContext, CmnOverride dto, int page,
                                 int pageSize);

    CmnOverride queryMaxVersion(CmnOverride cmnBasic);
    
    CmnOverride queryYTDate(CmnOverride cmnBasic);

    List<CmnOverride> addVersion(IRequest request,CmnOverride dto,Long oldVersion);

    public  List<CmnOverride> batchUpdateOverride(IRequest request ,List<CmnOverride>dto);
    /***
     * 根据basicId 得到 override设置期初数据
     * @param request
     * @param basicIds
     * @return
     */
    public  List<CmnOverride> queryOverrideBasic(IRequest request, CmnOverride dto, int page, int pageSize);
}