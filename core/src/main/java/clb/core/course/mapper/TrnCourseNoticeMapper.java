package clb.core.course.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.course.dto.TrnCourseNotice;

public interface TrnCourseNoticeMapper extends Mapper<TrnCourseNotice>{
	
	List<TrnCourseNotice> selectAllField(TrnCourseNotice TrnCourseNotice);
}