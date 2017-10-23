package clb.core.commission.mapper;

import clb.core.commission.dto.CmnCalc;
import com.hand.hap.mybatis.common.Mapper;

import java.util.List;

public interface CmnCalcMapper extends Mapper<CmnCalc>{

    Double queryAgePremium(CmnCalc CmnCalc);

}