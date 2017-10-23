package clb.core.sys.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.IProfileService;

import clb.core.common.utils.CommonUtil;
import clb.core.course.dto.TrnCourse;
import clb.core.course.dto.TrnCourseStudent;
import clb.core.course.dto.TrnSupport;
import clb.core.course.mapper.TrnCourseMapper;
import clb.core.course.mapper.TrnCourseStudentMapper;
import clb.core.course.mapper.TrnSupportMapper;
import clb.core.notification.service.INtnNotificationService;
import clb.core.order.dto.OrdOrder;
import clb.core.order.service.IOrdOrderService;
import clb.core.payment.dto.PaymentOrder;
import clb.core.payment.mapper.PaymentOrderMapper;
import clb.core.payment.utils.WXPayUtil;
import clb.core.production.dto.PrdItems;
import clb.core.production.mapper.PrdItemsMapper;
import clb.core.sys.service.ISysMimuteJobService;
import clb.core.system.controllers.ClbImportController;
import clb.core.system.dto.ClbCodeValue;
import clb.core.system.dto.ClbUser;
import clb.core.system.mapper.ClbUserMapper;
import clb.core.system.service.IClbCodeService;

@Service
@Transactional
public class SysMimuteJobServiceImpl implements ISysMimuteJobService {

	@Autowired
	private PaymentOrderMapper paymentOrderMapper;
	@Autowired
	private IProfileService profileService;
	@Autowired
	private IOrdOrderService ordOrderService;
	@Autowired
	private TrnSupportMapper trnSupportMapper;
	@Autowired
	private ClbUserMapper userMapper;
	@Autowired
	private INtnNotificationService ntnNotificationService;
	@Autowired
	private PrdItemsMapper prdItemsMapper;
	@Autowired
	private TrnCourseMapper trnCourseMapper;
	@Autowired
	private TrnCourseStudentMapper trnCourseStudentMapper;
	@Autowired
	private IClbCodeService clbCodeService;
	
	@Override
	public void payOverdue(IRequest request, JobExecutionContext job) {
		Long orderintervalTime = Long.valueOf(profileService.getProfileValue(request, "ORDER_PAY_INTERVAL_TIME"));
		Long supportintervalTime = Long.valueOf(profileService.getProfileValue(request, "SUPPORT_PAY_INTERVAL_TIME"));
		List<PaymentOrder> orderList = paymentOrderMapper.queryPayOverdue(new PaymentOrder());
		// 下次执行的时间
		Date nextFireTime = job.getNextFireTime();
		//执行时间
		Date fireTime = job.getFireTime();
		//间隔时间
		Long intervalTime=nextFireTime.getTime()-fireTime.getTime();
		
		for (PaymentOrder order : orderList) {
			Date createDate = order.getCreationDate();
			if(order.getSourceType().equals("ORDER")){
				if ((createDate.getTime() + (order.getPayLimitTime() - orderintervalTime) * 60000 +intervalTime) < nextFireTime.getTime()) {
					String[] ids = order.getSourceId().split(",");
					for (String id : ids) {
						if (StringUtils.isNotBlank(id)) {
							OrdOrder or = new OrdOrder();
							or.setOrderId(Long.valueOf(id));
							or = ordOrderService.selectByPrimaryKey(request, or);
							
							PrdItems item=prdItemsMapper.selectByPrimaryKey(or.getItemId());
							//调用通知接口发送通知信息
					        Map<String,Object> noticeMap = new HashMap<String,Object>();
					        noticeMap.put("itemName", item.getItemName());
							ClbUser user = new ClbUser();
							user.setUserType("CHANNEL");
							user.setRelatedPartyId(or.getChannelId());
							List<ClbUser> userList = userMapper.selectAllFields(user);
							for(ClbUser u:userList){
								ntnNotificationService.sendNotification(request,u.getUserId(), "ORDER01", noticeMap);
							}
						}
					}
					order.setAttribute1("SEND_NOTICE");
					paymentOrderMapper.updateByPrimaryKeySelective(order);
				}
			}else if(order.getSourceType().equals("SUPPORT")){
				if ((createDate.getTime() + (order.getPayLimitTime() - supportintervalTime) * 60000 +intervalTime) < nextFireTime.getTime()) {
					String[] ids = order.getSourceId().split(",");
					for (String id : ids) {
						if (StringUtils.isNotBlank(id)) {
							TrnSupport support = trnSupportMapper.selectByPrimaryKey(Long.valueOf(id));
							//调用通知接口发送通知信息
					        Map<String,Object> noticeMap = new HashMap<String,Object>();
					        noticeMap.put("name", getSupportTypeName(support.getSupportType())+" "+support.getSupportNum());
							// 执行通知
					        if(support.getCreatedBy()>0){
					        	ntnNotificationService.sendNotification(request,support.getCreatedBy(), "ZY0006", noticeMap);
					        }
						}
					}
					order.setAttribute1("SEND_NOTICE");
					paymentOrderMapper.updateByPrimaryKeySelective(order);
				}
			}
		}
	}


	@Override
	public void courseStartNotice(IRequest request,JobExecutionContext job) {
		//查询 离开课一小时到 70分钟的课程
		List<TrnCourse> list=trnCourseMapper.select70Minute(new TrnCourse());
		for(TrnCourse dto:list){
			TrnCourseStudent st=new TrnCourseStudent();
			st.setCourseId(dto.getCourseId());
			List<TrnCourseStudent> studentList=trnCourseStudentMapper.select(st);
			//调用通知接口发送通知信息
			Map<String,Object> noticeMap = new HashMap<String,Object>();
			noticeMap.put("topic", dto.getTopic());
			Set<Long>userList=new HashSet<Long>();
			for(TrnCourseStudent trnCourseStudent :studentList){
				if(trnCourseStudent.getCreatedBy()>0){
					userList.add(trnCourseStudent.getCreatedBy());
				}
			}
			for(Long u:userList){
				ntnNotificationService.sendNotification(request,u, "KC0004", noticeMap);
			}
			//把发送过通知的课程做一个标识
			dto.setAttribute1("SEND_NOTICE");
			trnCourseMapper.updateByPrimaryKeySelective(dto);
		}
	}
	
	public String getSupportTypeName(String supportTypeValue){
    	ClbCodeValue code = new ClbCodeValue();
		code.setValue(supportTypeValue);
		code.setCode("COURSE.SUPPORT_TYPE");
		List<ClbCodeValue>list= clbCodeService.selectCodeValuesByParam(code);
		if(CollectionUtils.isEmpty(list)){
			return null;
		}else{
			return list.get(0).getMeaning();
		}
    }


	@Override
	public void removeImportMomery() {
		try {
			Map<String, Object> importDate=ClbImportController.importDate;
			for(String key :importDate.keySet()){
				Long nowTime=WXPayUtil.getCurrentTimestampMs();
				//六个小时前的时间
				Long sixHourTime=nowTime-6*60*60*1000;
				if(Long.valueOf(key)<sixHourTime){
					importDate.remove(key);
				}
			}
		} catch (NumberFormatException e) {
			CommonUtil.printStackTraceToStr(e);
		}
	}

}
