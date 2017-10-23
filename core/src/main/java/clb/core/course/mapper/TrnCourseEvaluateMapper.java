package clb.core.course.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.course.dto.TrnCourseEvaluate;

public interface TrnCourseEvaluateMapper extends Mapper<TrnCourseEvaluate>{
	List<TrnCourseEvaluate> selectAllField(TrnCourseEvaluate trnCourseEvaluate);
	
	List<TrnCourseEvaluate> selectEvaluateByParams(TrnCourseEvaluate trnCourseEvaluate);
}