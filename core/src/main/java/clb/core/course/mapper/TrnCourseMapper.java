package clb.core.course.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.course.dto.TrnCourse;

public interface TrnCourseMapper extends Mapper<TrnCourse>{
	List<TrnCourse> selectAllField(TrnCourse trnCourse);

	List<TrnCourse> selectAllFieldByApp(TrnCourse trnCourse);

	TrnCourse selectOne(TrnCourse trnCourse);
	
	List<TrnCourse> wsSelectAllField(TrnCourse trnCourse);

	List<TrnCourse> wsSelectRecentCourse(TrnCourse trnCourse);

	List<TrnCourse> wsSelectRecentCourseByApp(TrnCourse trnCourse);

	List<TrnCourse> wsSelectEnrollCourse(TrnCourse trnCourse);
	
	List<TrnCourse> select70Minute(TrnCourse trnCourse);
}