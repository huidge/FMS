package clb.core.commission.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.commission.dto.CmnExtras;

public interface CmnExtrasMapper extends Mapper<CmnExtras>{

    List<CmnExtras> queryExtras(CmnExtras cmnExtras);

    Long queryMaxVersion(CmnExtras cmnExtras);
    
    List<CmnExtras> selectExtras(CmnExtras cmnExtras);
}