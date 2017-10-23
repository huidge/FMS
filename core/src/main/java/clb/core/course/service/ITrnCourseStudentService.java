package clb.core.course.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;

import clb.core.course.dto.TrnCourseStudent;

public interface ITrnCourseStudentService extends IBaseService<TrnCourseStudent>, ProxySelf<ITrnCourseStudentService>{
	List<TrnCourseStudent> selectAllField(IRequest requestContext, TrnCourseStudent trnCourseStudent ,int page, int pagesize);
	
	List<TrnCourseStudent> selectAll(IRequest requestContext, TrnCourseStudent trnCourseStudent);
	
	List<TrnCourseStudent> checkingPhone(IRequest requestContext, TrnCourseStudent trnCourseStudent ,int page, int pagesize);
	
	public ResponseData batchEnroll(IRequest request,List<TrnCourseStudent> dto);
	/****
	 * 根据ID 修改状态
	 * @param request
	 * @param id
	 */
	public TrnCourseStudent changeStatusById(IRequest request,String payStatus,Long id);
	
	public List<TrnCourseStudent> selectEnrollByParams(IRequest request,TrnCourseStudent student);

}