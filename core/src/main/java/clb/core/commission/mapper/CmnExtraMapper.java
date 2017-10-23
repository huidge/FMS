package clb.core.commission.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.commission.dto.CmnExtra;

public interface CmnExtraMapper extends Mapper<CmnExtra>{

    List<CmnExtra> queryBasic(CmnExtra cmnExtra);

    CmnExtra queryMaxVersion(CmnExtra cmnExtra);
    
    CmnExtra queryYTDate(CmnExtra cmnExtra);
    
    public  List<CmnExtra> queryExtraBasic(CmnExtra dto);
    
    public  List<CmnExtra> queryExtra(CmnExtra dto);
}