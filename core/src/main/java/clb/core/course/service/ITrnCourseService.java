package clb.core.course.service;

import java.util.List;
import java.util.Map;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.core.annotation.StdWho;
import com.hand.hap.system.service.IBaseService;

import clb.core.course.dto.TrnCourse;

public interface ITrnCourseService extends IBaseService<TrnCourse>, ProxySelf<ITrnCourseService>{
	
	TrnCourse createCourse(IRequest request, @StdWho TrnCourse obj);
	
	List<TrnCourse> selectId(IRequest requestContext, TrnCourse trnCourse);
	
	List<TrnCourse> selectAllField(IRequest requestContext, TrnCourse trnCourse ,int page, int pagesize);

	List<TrnCourse> queryAppCourseList(IRequest requestContext, TrnCourse trnCourse ,int page, int pagesize);

	List<TrnCourse> queryAppCL(IRequest requestContext, TrnCourse trnCourse ,int page, int pagesize);

	List<TrnCourse> updateStatus(IRequest requestContext, TrnCourse trnCourse);
	
	TrnCourse selectOne(IRequest requestContext, TrnCourse trnCourse, int page, int pagesize);
	
	List<TrnCourse> wsSelectAllField(IRequest requestContext, TrnCourse trnCourse ,int page, int pagesize);

}