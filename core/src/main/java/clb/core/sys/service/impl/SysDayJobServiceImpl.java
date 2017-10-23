package clb.core.sys.service.impl;

import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.IProfileService;

import clb.core.notification.mapper.NtnNotificationMapper;
import clb.core.notification.service.INtnNotificationService;
import clb.core.sys.service.IIfInvokeInboundService;
import clb.core.sys.service.IIfInvokeOutboundService;
import clb.core.sys.service.ISysDayJobService;

@Service
@Transactional
public class SysDayJobServiceImpl implements ISysDayJobService {

	@Autowired
	private INtnNotificationService ntnNotificationService;
	@Autowired
	private NtnNotificationMapper ntnNotificationMapper;
	
	@Autowired
	private IProfileService profileService;
	
	@Autowired
	private IIfInvokeInboundService invokeInboundService; 
	
	@Autowired
	private IIfInvokeOutboundService invokeOutboundService; 
	
	/*****
	 * 对于代办列表中状态为“已读”的消息，需要定时清除，时间限制为7天。即在消息状态变为已读后，7天后删除。
	 */
	@Override
	public void deleteNoticeOverdue(IRequest request, JobExecutionContext job) {
		ntnNotificationMapper.deleteOverDueDay();
	}
	/**
	 * 定期清除    sys_if_invoke_inbound、sys_if_invoke_outbound表中的数据
	 */
	@Override
	public void truncateTable(IRequest request, int days) {
		invokeInboundService.truncateTable(request,days);
		invokeOutboundService.truncateTable(request,days);
	}

}
