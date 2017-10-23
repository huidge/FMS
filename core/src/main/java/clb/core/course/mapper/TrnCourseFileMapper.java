package clb.core.course.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.course.dto.TrnCourse;
import clb.core.course.dto.TrnCourseFile;

public interface TrnCourseFileMapper extends Mapper<TrnCourseFile>{
	List<TrnCourseFile> selectBycourseId(@Param(value="courseId")Long courseId);
	
	List<TrnCourseFile> selectByParams(TrnCourseFile dto);
	
	List<TrnCourseFile> selectAllField(TrnCourseFile trnCourseFile);
}