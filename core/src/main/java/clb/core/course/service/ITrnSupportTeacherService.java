package clb.core.course.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.course.dto.TrnSupportTeacher;

public interface ITrnSupportTeacherService extends IBaseService<TrnSupportTeacher>, ProxySelf<ITrnSupportTeacherService>{
	
	List<TrnSupportTeacher> selectAllField(IRequest requestContext, TrnSupportTeacher trnSupportTeacher ,int page, int pagesize);
	
	List<TrnSupportTeacher> selectLecturer(IRequest requestContext, TrnSupportTeacher trnSupportTeacher ,int page, int pagesize);
}