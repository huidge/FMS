package clb.core.sys.service;

import org.quartz.JobExecutionContext;

import com.hand.hap.core.IRequest;

public interface ISysMimuteJobService {

	/****
	 * 逾期支付
	 * @param request
	 */
	public void payOverdue(IRequest request,JobExecutionContext job);
	/*******
	 * 开课通知
	 */
	public void courseStartNotice(IRequest request,JobExecutionContext job);
	
	/*****
	 * 清空大于6小时的导入内存
	 */
	public void removeImportMomery();
}
