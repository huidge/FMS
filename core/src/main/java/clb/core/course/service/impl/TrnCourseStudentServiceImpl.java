package clb.core.course.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.github.pagehelper.PageHelper;
import com.hand.hap.account.service.IUserService;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.course.dto.TrnCourse;
import clb.core.course.dto.TrnCourseStudent;
import clb.core.course.mapper.TrnCourseStudentMapper;
import clb.core.course.service.ITrnCourseService;
import clb.core.course.service.ITrnCourseStudentService;
import clb.core.notification.service.INtnNotificationService;

@Service
@Transactional
public class TrnCourseStudentServiceImpl extends BaseServiceImpl<TrnCourseStudent> implements ITrnCourseStudentService{
	
	@Autowired
    private TrnCourseStudentMapper trnCourseStudentMapper;
	@Autowired
	private ITrnCourseService trnCourseService;
	@Autowired
	private IUserService userService;
	@Autowired
	private INtnNotificationService ntnNotificationService;
	
	@Override
	public List<TrnCourseStudent> selectAllField(IRequest requestContext, TrnCourseStudent trnCourseStudent, int page,
			int pagesize) {
		PageHelper.startPage(page, pagesize);
		List<TrnCourseStudent> list = trnCourseStudentMapper.selectAllField(trnCourseStudent);
		return list;
		//return null;
	}
	
	@Override
	public List<TrnCourseStudent> selectAll(IRequest requestContext, TrnCourseStudent trnCourseStudent) {
		List<TrnCourseStudent> list = trnCourseStudentMapper.selectAllField(trnCourseStudent);
		return list;
		//return null;
	}
	
	@Override
	public List<TrnCourseStudent> checkingPhone(IRequest requestContext, TrnCourseStudent trnCourseStudent, int page,
			int pagesize) {
		PageHelper.startPage(page, pagesize);
		List<TrnCourseStudent> list = trnCourseStudentMapper.checkingPhone(trnCourseStudent);
		/*List<TrnCourseStudent> listNew = new ArrayList<>();
		for (TrnCourseStudent trnCourseStudent2 : list) {
			String phoneCode = trnCourseStudent2.getPhoneCode();
			int k = 1;
			if(listNew.size()>0){
				for (TrnCourseStudent listNewobj : listNew) {
					if(phoneCode.equals(listNewobj.getPhoneCode())){
						k = 2;
						break;
					}
				}
			}
			if(k==1){
				//listNew 没有的情况下
				List<TrnCourseStudent> listTemp = new ArrayList<>();
				for (TrnCourseStudent trnCourseStudent3 : list) {
					if(phoneCode.equals(trnCourseStudent3.getPhoneCode())){
						listTemp.add(trnCourseStudent3);
					}
				}
				listNew.add(listTemp.get(0));
			}
		}*/
		return list;
		//return null;
	}
	
	
	
	@Override
	public ResponseData batchEnroll(IRequest request, List<TrnCourseStudent> dto) {
		/*User user=new User();
		user.setUserId(request.getUserId());
		user=userService.selectByPrimaryKey(request, user);
		//用户的手机号码
		String userPhone=(user==null)?"":user.getPhone();*/
		
		TrnCourse cour=new TrnCourse();
		cour.setCourseId(dto.get(0).getCourseId());
		TrnCourse course=trnCourseService.selectByPrimaryKey(request, cour);
		for(TrnCourseStudent stu:dto){
//			stu.setBelongTo(userPhone.equals(stu.getPhoneNumber())?"公司渠道":"外来渠道");
			//根据手机号码查询是否存在
			TrnCourseStudent stu1=new TrnCourseStudent();
			stu1.setCourseId(stu.getCourseId());
			stu1.setCreatedBy(request.getUserId());
			stu1.setPhoneNumber(stu.getPhoneNumber());
			List<TrnCourseStudent>listStu=trnCourseStudentMapper.selectEnrollByParams(stu1);
			if(!CollectionUtils.isEmpty(listStu)){
				stu1=listStu.get(0);
			}
			stu.setLineId(stu1.getLineId());
			if(stu.getPhoneNumber().equals(request.getAttribute("phone")) && request.getAttribute("channelId")!=null ){
				stu.setChannelId(request.getAttribute("channelId"));
			}
			//是否需要报名费
			//if(course.getstatus==null) course.setstatus
			if(stu.getLineId()==null){
				if(course.getPrice()!=null && course.getPrice().compareTo(BigDecimal.ZERO)==1){
					stu.setPayStatus("UNPAID");
				}else{
					stu.setPayStatus("PAID");
				}
				self().insertSelective(request, stu);
			}else{
				self().updateByPrimaryKeySelective(request, stu);
			}
		}
		ResponseData response=new ResponseData(true);
		response.setCode("0");
		response.setMessage("报名成功！");
		if(course.getPrice()!=null && course.getPrice().compareTo(BigDecimal.ZERO)==1){
			response.setSuccess(false);
			response.setCode("-1");
			response.setMessage("需要缴费！");
		}else{
			//调用通知接口发送通知信息
	        Map<String,Object> noticeMap = new HashMap<String,Object>();
	        noticeMap.put("topic", course.getTopic());
	        noticeMap.put("password", course.getPassword());
			ntnNotificationService.sendNotification(request,request.getUserId(), "KC0001", noticeMap);
		}
		response.setRows(dto);
		return response;
	}
	@Override
	public TrnCourseStudent changeStatusById(IRequest request,String payStatus, Long id) {
		TrnCourseStudent dto=trnCourseStudentMapper.selectByPrimaryKey(id);
		dto.setPayStatus(payStatus);
		dto=updateByPrimaryKeySelective(request, dto);
		return dto;
	}

	@Override
	public List<TrnCourseStudent> selectEnrollByParams(IRequest request, TrnCourseStudent student) {
		return trnCourseStudentMapper.selectEnrollByParams(student);
	}
}