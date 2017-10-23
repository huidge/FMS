package clb.core.sys.job;

import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.job.AbstractJob;

import clb.core.sys.service.ISysMimuteJobService;
import clb.core.sys.service.ISysMimuteJobSupportService;

/******
 * 
 * @author tiansheng.ye@hand-china.com
 * @Desc 5分钟级别的定时任务
 *
 */
public class SysMimuteJob  extends AbstractJob{

	@Autowired
	private ISysMimuteJobService sysMimuteJobService;
	@Autowired
	private ISysMimuteJobSupportService ISysMimuteJobSupportService;
	
	@Override
	public void safeExecute(JobExecutionContext arg0) throws Exception {
		IRequest request=RequestHelper.newEmptyRequest();
		//增值服务订单支付逾期通知
		sysMimuteJobService.payOverdue(request, arg0);
		//开课通知
		sysMimuteJobService.courseStartNotice(request, arg0);
		
		ISysMimuteJobSupportService.updateAmountInvalid(request);
		
		/*****
		 * 导入的时候，数据是放到服务器内存上的
		 * 先执行一个定时任务，删除 6个小时前的导入缓存数据
		 * 删除大于6个小时的导入缓存数据
		 */
		sysMimuteJobService.removeImportMomery();
	}

}
