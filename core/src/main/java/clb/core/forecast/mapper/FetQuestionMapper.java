package clb.core.forecast.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.forecast.dto.FetQuestion;

public interface FetQuestionMapper extends Mapper<FetQuestion>{

	public List<FetQuestion> getData(FetQuestion dto);
	
	public String selectMaxQuestionCode();

	
}