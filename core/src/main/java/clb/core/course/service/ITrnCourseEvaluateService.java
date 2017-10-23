package clb.core.course.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.course.dto.TrnCourseEvaluate;

public interface ITrnCourseEvaluateService extends IBaseService<TrnCourseEvaluate>, ProxySelf<ITrnCourseEvaluateService>{
	List<TrnCourseEvaluate> selectAllField(IRequest requestContext, TrnCourseEvaluate trnCourseEvaluate ,int page, int pagesize);

}