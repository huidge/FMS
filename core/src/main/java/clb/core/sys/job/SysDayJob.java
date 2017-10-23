package clb.core.sys.job;


import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.job.AbstractJob;
import com.hand.hap.system.service.IProfileService;

import clb.core.sys.service.ISysDayJobService;

/******
 * 
 * @author tiansheng.ye@hand-china.com
 * @Desc 天级别的定时任务
 *
 */
public class SysDayJob  extends AbstractJob{

	@Autowired
	private ISysDayJobService sysDayJobService;
	
	@Autowired
	private IProfileService profileService;
	
	@Override
	public void safeExecute(JobExecutionContext arg0) throws Exception {
		IRequest request=RequestHelper.newEmptyRequest();
		//删除过期站内信
		sysDayJobService.deleteNoticeOverdue(request, arg0);
		
		//定期清除    sys_if_invoke_inbound、sys_if_invoke_outbound表中的数据
		String days = profileService.getProfileValue(request, "TRUNCATE_TABLE_INVOTE");//读取配置文件
		sysDayJobService.truncateTable(request,Integer.parseInt(days));
	}

}
