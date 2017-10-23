package clb.core.course.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.course.dto.TrnCourseNotice;

public interface ITrnCourseNoticeService extends IBaseService<TrnCourseNotice>, ProxySelf<ITrnCourseNoticeService>{
	
	List<TrnCourseNotice> selectAllField(IRequest requestContext, TrnCourseNotice TrnCourseNotice ,int page, int pagesize);
	
	List<TrnCourseNotice> selectAll(IRequest requestContext, TrnCourseNotice TrnCourseNotice);
}