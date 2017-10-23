package clb.core.sys.service;

import org.quartz.JobExecutionContext;

import com.hand.hap.core.IRequest;

public interface ISysDayJobService {

	/****
	 * 对于代办列表中状态为“已读”的消息，需要定时清除，时间限制为7天。即在消息状态变为已读后，7天后删除。
	 * @param request
	 */
	public void deleteNoticeOverdue(IRequest request,JobExecutionContext job);
	/**
	 * 定期清空 sys_if_invoke_inbound、sys_if_invoke_outbound表
	 * @param request
	 * @param parseInt
	 */
	public void truncateTable(IRequest request, int parseInt);
}
