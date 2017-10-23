package clb.core.course.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.core.annotation.StdWho;
import com.hand.hap.system.service.IBaseService;

import clb.core.course.dto.TrnCourse;
import clb.core.course.dto.TrnCourseFile;
import clb.core.production.dto.PrdItemFile;

public interface ITrnCourseFileService extends IBaseService<TrnCourseFile>, ProxySelf<ITrnCourseFileService>{

	List<TrnCourseFile> selectBycourseId(IRequest requestContext, Long courseId, int page, int pageSize);
	
	public List<TrnCourseFile> selectByParams(IRequest requestContext,TrnCourseFile dto, int page, int pageSize);
	
	public void updateDowloadTimes(IRequest requestContext,TrnCourseFile dto);
	
	List<TrnCourseFile> selectAllField(IRequest requestContext, TrnCourseFile trnCourseFile ,int page, int pagesize);

	/*TrnCourseFile createCourseFile(IRequest requestContext, @StdWho TrnCourseFile obj);*/
	
}