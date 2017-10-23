package clb.core.course.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;

import clb.core.course.dto.TrnCourse;
import clb.core.course.dto.TrnSupportTeacher;
import clb.core.course.service.ITrnSupportTeacherService;

    @Controller
    public class TrnSupportTeacherController extends BaseController{

    @Autowired
    private ITrnSupportTeacherService service;


    @RequestMapping(value = "/fms/trn/support/teacher/query")
    @ResponseBody
    public ResponseData query(TrnSupportTeacher dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectAllField(requestContext,dto,page,pageSize));
    }
    
    @RequestMapping(value = "/fms/trn/support/teacher/selectLecturer")
    @ResponseBody
    public ResponseData selectLecturer(TrnSupportTeacher dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
    		@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
    	IRequest requestContext = createRequestContext(request);
    	List<TrnSupportTeacher> selectLecturer = service.selectLecturer(requestContext,dto,page,pageSize);
    	for (TrnSupportTeacher trnSupportTeacher : selectLecturer) {
			System.out.println(trnSupportTeacher);
		}
    	return new ResponseData(selectLecturer);
    }
    
    @RequestMapping(value = "/fms/trn/support/teacher/deleteLecturer")
    @ResponseBody 
    public ResponseData updataReleaseStatus(HttpServletRequest request,@RequestParam("ids[]") List<Long> ids) {
    	IRequest requestCtx = createRequestContext(request);
    		for (Long teacherId : ids) {
    			TrnSupportTeacher dto = new TrnSupportTeacher();
    			dto.setTeacherId(teacherId);
    			dto.setSupportId((long) 0);
    			TrnSupportTeacher trnCourse = service.updateByPrimaryKeySelective(requestCtx, dto);
    			//System.out.println(trnCourse);
    		}
    	return new ResponseData();
    }
    
    //添加支援讲师
    @RequestMapping(value = "/fms/trn/support/teacher/addTeacher")
	@ResponseBody 
	public ResponseData addTeacher(HttpServletRequest request,@RequestParam("supportId") String supportId,@RequestParam("ids[]") List<Long> ids) {
		IRequest requestCtx = createRequestContext(request);
		if (supportId != null || !"".equals(supportId)) {
			for (Long teacherId : ids) {
				TrnSupportTeacher dto = new TrnSupportTeacher();
				dto.setSupportId(Long.parseLong(supportId));
				dto.setTeacherId(teacherId);
				TrnSupportTeacher trnSupport = service.updateByPrimaryKeySelective(requestCtx, dto);
				System.out.println(trnSupport);
			}
		}
		return new ResponseData();
	}
    
    @RequestMapping(value = "/fms/trn/support/teacher/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<TrnSupportTeacher> dto){
        IRequest requestCtx = createRequestContext(request);
        ResponseData responseData = new ResponseData();
        for (TrnSupportTeacher trnSupportTeacher : dto) {
        	if("add".equals(trnSupportTeacher.get__status())){
        		TrnSupportTeacher trnSupportTeacher2 = new TrnSupportTeacher();
        		trnSupportTeacher2.setUserId(trnSupportTeacher.getUserId());
        		List<TrnSupportTeacher> selectLecturer = service.selectLecturer(requestCtx,trnSupportTeacher2,1,10000);
        		for (TrnSupportTeacher trnSupportTeacher3 : selectLecturer) {
					if(trnSupportTeacher3.getSupportId()==trnSupportTeacher.getSupportId()){
						responseData.setSuccess(false);
						responseData.setMessage("讲师不能重复支援同一业务，保存失败");
						return responseData;
					}
				}
        	}
		}
        List<TrnSupportTeacher> batchUpdate = service.batchUpdate(requestCtx, dto);
        return new ResponseData(batchUpdate);
    }

    @RequestMapping(value = "/fms/trn/support/teacher/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<TrnSupportTeacher> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
    }