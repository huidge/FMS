package clb.core.commission.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.commission.dto.CmnOverride;

public interface CmnOverrideMapper extends Mapper<CmnOverride>{

    List<CmnOverride> queryBasic(CmnOverride cmnOverride);

    CmnOverride queryMaxVersion(CmnOverride cmnOverride);
    
    CmnOverride queryYTDate(CmnOverride cmnOverride);
    
    public  List<CmnOverride> queryOverrideBasic(CmnOverride cmnOverride);
    
    public  List<CmnOverride> queryOverride(CmnOverride cmnOverride);
}