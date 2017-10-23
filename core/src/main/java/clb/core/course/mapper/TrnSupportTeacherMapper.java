package clb.core.course.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.course.dto.TrnSupportTeacher;

public interface TrnSupportTeacherMapper extends Mapper<TrnSupportTeacher>{
	
	List<TrnSupportTeacher> selectAllField(TrnSupportTeacher trnSupportTeacher);
	
	List<TrnSupportTeacher> selectLecturer(TrnSupportTeacher trnSupportTeacher);
}