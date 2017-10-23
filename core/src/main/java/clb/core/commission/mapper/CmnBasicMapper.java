package clb.core.commission.mapper;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.commission.dto.CmnBasic;

import java.util.List;
import java.util.Map;

public interface CmnBasicMapper extends Mapper<CmnBasic>{

    List<CmnBasic> queryBasic(CmnBasic cmnBasic);

    Long queryMaxVersion(CmnBasic cmnBasic);
    
    List<CmnBasic> selectBasic(CmnBasic cmnBasic);
}