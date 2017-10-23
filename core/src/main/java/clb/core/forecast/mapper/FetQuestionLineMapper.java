package clb.core.forecast.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.forecast.dto.FetQuestionLine;

public interface FetQuestionLineMapper extends Mapper<FetQuestionLine>{

	List<FetQuestionLine> selectData(FetQuestionLine dto);
	
}