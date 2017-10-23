package clb.core.course.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import clb.core.course.dto.TrnCourseNotice;
import clb.core.course.dto.TrnCourseStudent;
import clb.core.course.mapper.TrnCourseStudentMapper;
import clb.core.course.service.ITrnCourseNoticeService;
import clb.core.course.service.ITrnCourseService;
import clb.core.course.service.ITrnCourseStudentService;
import clb.core.notification.service.INtnNotificationService;
import clb.core.system.dto.ClbUser;
import clb.core.system.mapper.ClbUserMapper;

    @Controller
    public class TrnCourseNoticeController extends BaseController{

    @Autowired
    private ITrnCourseNoticeService service;
    @Autowired
    private ITrnCourseService trnCourseService;
    @Autowired
    private TrnCourseStudentMapper trnCourseStudentMapper;
    @Autowired
	private INtnNotificationService ntnNotificationService;
	@Autowired
	private ClbUserMapper clbUserMapper;
	
    @RequestMapping(value = "/fms/trn/course/notice/query")
    @ResponseBody
    public ResponseData query(TrnCourseNotice dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectAllField(requestContext,dto,page,pageSize));
    }
    
    @RequestMapping(value = "/fms/trn/course/notice/selectAll")
    @ResponseBody
    public ResponseData selectAll(TrnCourseNotice dto, HttpServletRequest request) {
    	IRequest requestContext = createRequestContext(request);
    	return new ResponseData(service.selectAll(requestContext,dto));
    }

    @RequestMapping(value = "/fms/trn/course/notice/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<TrnCourseNotice> dto){
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }
    
    @ResponseBody
    @RequestMapping(value = "/fms/trn/course/notice/add")
    public ResponseData add(HttpServletRequest request, @RequestBody List<TrnCourseNotice> obj){
        IRequest requestContext = createRequestContext(request);
        TrnCourseNotice TrnCourseNotice = obj.get(0);
        TrnCourseNotice insertSelective = service.insertSelective(requestContext,TrnCourseNotice);
		List<TrnCourseNotice> list = new ArrayList<>();
		list.add(insertSelective);
		//发送课程通知
		if(TrnCourseNotice.getCourseId()!=null){
			TrnCourse dto = new TrnCourse();
			dto.setCourseId(TrnCourseNotice.getCourseId());
			TrnCourse trnCourse = trnCourseService.selectByPrimaryKey(requestContext, dto);
			TrnCourseStudent st=new TrnCourseStudent();
			st.setCourseId(TrnCourseNotice.getCourseId());
			List<TrnCourseStudent> studentList=trnCourseStudentMapper.select(st);
			//调用通知接口发送通知信息
			Map<String,Object> noticeMap = new HashMap<String,Object>();
			noticeMap.put("topic", trnCourse.getTopic());
			noticeMap.put("message", TrnCourseNotice.getNotice());
			Set<Long>userList=new HashSet<Long>();
			for(TrnCourseStudent trnCourseStudent :studentList){
				if(trnCourseStudent.getCreatedBy()>0){
					userList.add(trnCourseStudent.getCreatedBy());
				}
			}
			for(Long u:userList){
				ntnNotificationService.sendNotification(requestContext,u, "KC0003", noticeMap);
			}
		}
        return new ResponseData(list);
    }
    
    @RequestMapping(value = "/fms/trn/course/notice/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<TrnCourseNotice> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
    }