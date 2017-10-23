package clb.core.course.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.course.dto.TrnCourseStudent;

public interface TrnCourseStudentMapper extends Mapper<TrnCourseStudent>{
	List<TrnCourseStudent> selectAllField(TrnCourseStudent trnCourseStudent);
	
	List<TrnCourseStudent> checkingPhone(TrnCourseStudent trnCourseStudent);
	/***
	 * 根据手机号或者渠道ID查询报名情况
	 * @param trnCourseStudent
	 * @return
	 */
	List<TrnCourseStudent> selectEnrollByParams(TrnCourseStudent trnCourseStudent);
}