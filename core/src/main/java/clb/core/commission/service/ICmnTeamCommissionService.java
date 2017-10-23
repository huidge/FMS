package clb.core.commission.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.commission.dto.CmnTeamCommission;

import java.util.List;

public interface ICmnTeamCommissionService extends IBaseService<CmnTeamCommission>, ProxySelf<ICmnTeamCommissionService>{
    List<CmnTeamCommission> queryBasic(IRequest requestContext, CmnTeamCommission dto, int page,
                                       int pageSize);
    Long queryMaxVersion(CmnTeamCommission cmnTeamCommission);

    List<CmnTeamCommission> addVersion(IRequest request,CmnTeamCommission dto,Long oldVersion);
}