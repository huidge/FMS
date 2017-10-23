package clb.core.commission.mapper;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.commission.dto.CmnTeamCommission;

import java.util.List;

public interface CmnTeamCommissionMapper extends Mapper<CmnTeamCommission>{

    List<CmnTeamCommission> queryBasic(CmnTeamCommission cmnTeamCommission);

    Long queryMaxVersion(CmnTeamCommission cmnTeamCommission);
}