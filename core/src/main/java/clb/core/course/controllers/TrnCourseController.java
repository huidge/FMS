package clb.core.course.controllers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import clb.core.payment.service.IPaymentOrderService;
import com.codahale.metrics.annotation.Timed;
import com.hand.hap.intergration.annotation.HapInbound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.exception.BaseException;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;

import clb.core.common.utils.DateUtil;
import clb.core.course.dto.TrnCourse;
import clb.core.course.dto.TrnCourseStudent;
import clb.core.course.dto.TrnSupport;
import clb.core.course.mapper.TrnCourseStudentMapper;
import clb.core.course.service.ITrnCourseService;
import clb.core.course.service.ITrnCourseStudentService;
import clb.core.notification.service.INtnNotificationService;
import clb.core.system.dto.ClbUser;
import clb.core.system.mapper.ClbUserMapper;
import clb.core.whtcustom.dto.WhtCrKeywordRule;

    @Controller
    public class TrnCourseController extends BaseController{

    @Autowired
    private ITrnCourseService service;
    @Autowired
    private ITrnCourseStudentService CourseStudentService;
    @Autowired
	private TrnCourseStudentMapper trnCourseStudentMapper;
    @Autowired
	private INtnNotificationService ntnNotificationService;
	@Autowired
	private ClbUserMapper clbUserMapper;


	
    @RequestMapping(value = "/fms/trn/course/query")
    @ResponseBody
    public ResponseData query(TrnCourse dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        List<TrnCourse> selectAllField = service.selectAllField(requestContext, dto, page, pageSize);
        for (TrnCourse trnCourse : selectAllField) {
        	System.out.println(trnCourse);
		}
        return new ResponseData(selectAllField);
    }
    
    @RequestMapping(value = "/fms/trn/course/updateStatus")
    @ResponseBody
    public ResponseData updateStatus(TrnCourse dto, HttpServletRequest request) {
    	IRequest requestContext = createRequestContext(request);
    	List<TrnCourse> selectAllField = service.updateStatus(requestContext, dto);
    	/*for (TrnCourse trnCourse : selectAllField) {
		}*/
    	return new ResponseData(selectAllField);
    	//return new ResponseData(service.select(requestContext,dto,page,pageSize));
    }
    
    @RequestMapping(value = "/fms/trn/course/queryId")
    @ResponseBody
    public ResponseData queryId(TrnCourse dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        List<TrnCourse> selectAllId = service.selectId(requestContext, dto);
        return new ResponseData(selectAllId);
        //return new ResponseData(service.select(requestContext,dto,page,pageSize));
    }
    
    @RequestMapping(value = "/fms/trn/course/updataReleaseStatus")
	@ResponseBody 
	public ResponseData updataReleaseStatus(HttpServletRequest request,@RequestParam("status") String status,@RequestParam("ids[]") List<Long> ids) {
		IRequest requestCtx = createRequestContext(request);
		if (status.equals("NOTSTARTED")) {
			for (Long courseId : ids) {
				TrnCourse dto = new TrnCourse();
				dto.setStatus("NOTSTARTED");
				dto.setCourseId(courseId);
				TrnCourse trnCourse = service.updateByPrimaryKeySelective(requestCtx, dto);
			}
		}
		return new ResponseData();
	}
    
    @RequestMapping(value = "/fms/trn/course/updataRecommend")
	@ResponseBody
	public ResponseData updataRecommend(HttpServletRequest request,@RequestParam("appFlag") String appFlag,@RequestParam("ids[]") List<Long> ids) {
		IRequest requestCtx = createRequestContext(request);
		if (appFlag.equals("Y")) {
			for (Long courseId : ids) {
				TrnCourse dto = new TrnCourse();
				dto.setAppFlag("Y");
				dto.setCourseId(courseId);
				TrnCourse trnCourse = service.updateByPrimaryKeySelective(requestCtx, dto);
			}
		}
		else{
			for (Long courseId : ids) {
				TrnCourse dto = new TrnCourse();
				dto.setAppFlag("N");
				dto.setCourseId(courseId);
				TrnCourse trnCourse = service.updateByPrimaryKeySelective(requestCtx, dto);
			}
		}
		return new ResponseData();
	}
    
    //取消课程
    @RequestMapping(value = "/fms/trn/course/updataStatus")
	@ResponseBody
	public ResponseData updataStatus(HttpServletRequest request,@RequestParam("status") String status,@RequestParam("ids[]") List<Long> ids) {
		IRequest requestCtx = createRequestContext(request);
		if (status.equals("CANCELED")) {
			for (Long courseId : ids) {
				TrnCourse dto = new TrnCourse();
				dto.setStatus("CANCELED");
				dto.setCourseId(courseId);
				TrnCourse trnCourse = service.updateByPrimaryKeySelective(requestCtx, dto);
				//调用通知接口发送通知信息  -KC0002 用户已报名的课程取消时
				trnCourse=service.selectByPrimaryKey(requestCtx, trnCourse);
				TrnCourseStudent st=new TrnCourseStudent();
				st.setCourseId(trnCourse.getCourseId());
				List<TrnCourseStudent> studentList=trnCourseStudentMapper.select(st);
				//调用通知接口发送通知信息
				Map<String,Object> noticeMap = new HashMap<String,Object>();
				noticeMap.put("topic", trnCourse.getTopic());
				noticeMap.put("courseDate", DateUtil.getDateStr(trnCourse.getCourseDate(), "yyyy-MM-dd HH:mm:ss"));
				noticeMap.put("password", trnCourse.getPassword());
				Set<Long>userList=new HashSet<Long>();
				for(TrnCourseStudent trnCourseStudent :studentList){
					if(trnCourseStudent.getCreatedBy()>0){
						userList.add(trnCourseStudent.getCreatedBy());
					}
				}
				for(Long u:userList){
					ntnNotificationService.sendNotification(requestCtx,u, "KC0002", noticeMap);
				}
				
			}
		}else if(status.equals("COMPLETE")) {
			for (Long courseId : ids) {
				TrnCourse dto = new TrnCourse();
				dto.setStatus("COMPLETE");
				dto.setCourseId(courseId);
				TrnCourse trnCourse = service.updateByPrimaryKeySelective(requestCtx, dto);
			}
		}else if(status.equals("NOTSTARTED")) {
			for (Long courseId : ids) {
				TrnCourse dto = new TrnCourse();
				dto.setStatus("NOTSTARTED");
				dto.setCourseId(courseId);
				TrnCourse trnCourse = service.updateByPrimaryKeySelective(requestCtx, dto);
			}
		}else if(status.equals("INPROCESS")) {
			for (Long courseId : ids) {
				TrnCourse dto = new TrnCourse();
				dto.setStatus("INPROCESS");
				dto.setCourseId(courseId);
				TrnCourse trnCourse = service.updateByPrimaryKeySelective(requestCtx, dto);
			}
		}
		return new ResponseData();
	}
    
    @RequestMapping(value = "/fms/trn/course/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<TrnCourse> dto){
        IRequest requestCtx = createRequestContext(request);
        TrnCourse trnCourse = dto.get(0);
        //课程费用为0时，更改报名学员的支付状态
        if(trnCourse.getPrice().compareTo(BigDecimal.ZERO)==0){
        	TrnCourseStudent trnCourseStudent = new TrnCourseStudent();
        	trnCourseStudent.setCourseId(trnCourse.getCourseId());
        	List<TrnCourseStudent> selectAllField = CourseStudentService.selectAll(requestCtx, trnCourseStudent);
        	System.out.println(selectAllField.toString());
    		for (TrnCourseStudent trnCourseStudent2 : selectAllField) {
    			if(!"PAID".equals(trnCourseStudent2.getPayStatus())){
    				trnCourseStudent2.setPayStatus("PAID");
    				CourseStudentService.updateByPrimaryKeySelective(requestCtx, trnCourseStudent2);
    			}
			}
        }
        TrnCourse trnCourse2 = service.updateByPrimaryKey(requestCtx, trnCourse);
        List<TrnCourse> list = new ArrayList<>();
        list.add(trnCourse2);
        /*getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestContext = createRequestContext(request);*/
        return new ResponseData(list);
        //return new ResponseData(service.batchUpdate(requestCtx, dto));
    }
    
    @ResponseBody
    @RequestMapping(value="/fms/trn/course/add")
    public ResponseData add(HttpServletRequest request, @RequestBody TrnCourse obj, BindingResult result) throws BaseException {
    	List<TrnCourse> list = new ArrayList<>();
        getValidator().validate(obj, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestContext = createRequestContext(request);
        TrnCourse createCourse = service.createCourse(requestContext, obj);
        list.add(createCourse);
        return new ResponseData(list);
    }

    @RequestMapping(value = "/fms/trn/course/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<TrnCourse> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }

}