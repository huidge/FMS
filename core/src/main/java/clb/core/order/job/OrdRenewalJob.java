package clb.core.order.job;
/**
 * 续保短信通知定时任务
 **/

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;

import com.hand.hap.core.IRequest;
import com.hand.hap.job.AbstractJob;

import clb.core.common.utils.CommonUtil;
import clb.core.common.utils.DateUtil;
import clb.core.notification.service.INtnNotificationService;
import clb.core.order.dto.OrdAfterLog;
import clb.core.order.dto.OrdOrder;
import clb.core.order.dto.OrdOrderRenewal;
import clb.core.order.mapper.OrdAfterMapper;
import clb.core.order.service.IOrdAfterLogService;
import clb.core.order.service.IOrdAfterService;
import clb.core.order.service.IOrdOrderRenewalService;
import clb.core.order.service.IOrdOrderService;
import clb.core.system.dto.ClbUser;
import clb.core.system.service.IClbUserService;

public class OrdRenewalJob extends AbstractJob {

	public static Log log = LogFactory.getLog(OrdRenewalJob.class);

	@Autowired
	private IOrdAfterService ordAfterService;

	@Autowired
	private OrdAfterMapper ordAfterMapper;

	@Autowired
	private IOrdOrderRenewalService ordOrderRenewalService;

	@Autowired
	private IOrdOrderService ordOrderService;

	@Autowired
	private INtnNotificationService ntnNotificationService;

	@Autowired
	private IClbUserService clbUserService;

	@Autowired
	private IOrdAfterLogService ordAfterLogService;

	@Override
	public void safeExecute(JobExecutionContext jobExecutionContext) throws Exception {

		JobDetail detail = jobExecutionContext.getJobDetail();
		JobKey key = detail.getKey();
		TriggerKey triggerKey = jobExecutionContext.getTrigger().getKey();

		// job传入参数

		List<OrdOrderRenewal> list = ordAfterMapper.queryOrdRenewalList(null);
		// 给需要展示的字段赋值 转换当前续保单的状态
		if (list != null && list.size() > 0) {
			for (OrdOrderRenewal ordOrderRenewal : list) {
				// 续保到期日
				Date renewalDueDate = ordOrderRenewal.getRenewalDueDate();
				int days = (int) ((new Date().getTime() - renewalDueDate.getTime()) / (1000 * 3600 * 24));
				
				//以订单上维护的宽限期和行政期时间为准!
				// 宽限期
				//Long gracePeriod = ordOrderRenewal.getGracePeriod();
				Long gracePeriod = null;
				if(ordOrderRenewal.getGraceDate() != null) {
					gracePeriod = DateUtil.getDateDiff2(ordOrderRenewal.getGraceDate(), renewalDueDate);
				}
				// 行政期
				//Long administrativePeriod = ordOrderRenewal.getAdministrativePeriod();
				Long administrativePeriod = null;
				if(ordOrderRenewal.getAdministrativeDate() != null) {
					administrativePeriod = DateUtil.getDateDiff2(ordOrderRenewal.getAdministrativeDate(), renewalDueDate);
				}

				if (days == -30) {// 待续保(系统默认为待续保)
					ordOrderRenewal.setRenewalStatus("TORENEWAL");
					sendNotice(null, ordOrderRenewal);
				} else if (gracePeriod != null && days == 0) { // 宽限期
						ordOrderRenewal.setRenewalStatus("NACHFRIST");
						sendNotice(null, ordOrderRenewal);
						
				} else if (gracePeriod != null && administrativePeriod != null && days == gracePeriod) { // 行政期
						ordOrderRenewal.setRenewalStatus("ADMIN");
						sendNotice(null, ordOrderRenewal);
						
				} else if (days == (gracePeriod == null?0:(administrativePeriod == null?gracePeriod:administrativePeriod))) { // 待确认失效
					ordOrderRenewal.setRenewalStatus("TOFAIL");
					sendNotice(null, ordOrderRenewal);
				}
			}
		}
	}

	public void sendNotice(IRequest requestCtx, OrdOrderRenewal dto){
		OrdOrder ordOrder = new OrdOrder();
		ordOrder.setOrderId(dto.getOrderId());

		OrdOrder key = ordAfterService.queryOrderByOrdOrderId(requestCtx, ordOrder).get(0);

		ClbUser condition = new ClbUser();
		condition.setRelatedPartyId(key.getChannelId());
		condition.setUserType("CHANNEL");
		List<ClbUser> select = clbUserService.select(requestCtx, condition, 1, 1);
		if (select == null || select.get(0).getUserId() == null) {
			throw new RuntimeException("该渠道没有注册为用户!");
		}
		Long userId = clbUserService.select(requestCtx, condition, 1, 1).get(0).getUserId();

		String templateCode = null;
		Map<String, Object> sysdata = new HashMap<>();
		sysdata.put("item", key.getItem());
		sysdata.put("orderNumber", key.getOrderNumber());
		sysdata.put("applicant", key.getApplicant());
		sysdata.put("insurant", key.getInsurant());
		sysdata.put("renewalDueDate", key.getRenewalDueDate().toLocaleString());
		
		Long gracePeriod = key.getGracePeriod();
		Long administrativePeriod = key.getAdministrativePeriod();
		// 待续保
		if ("TORENEWAL".equals(dto.getRenewalStatus())) {
			templateCode = "XB0001";
			sysdata.put("diffDay", DateUtil.getDateDiff(key.getRenewalDueDate(),new Date()));
			// 宽限期
		} else if ("NACHFRIST".equals(dto.getRenewalStatus())) {
			templateCode = "XB0002";
			sysdata.put("diffDay",gracePeriod - DateUtil.getDateDiff(new Date(),key.getRenewalDueDate()));
			// 行政期
		} else if ("ADMIN".equals(dto.getRenewalStatus())) {
			templateCode = "XB0003";
			sysdata.put("diffDay",gracePeriod + administrativePeriod - DateUtil.getDateDiff(new Date(),key.getRenewalDueDate()));
			// 待确认失效
		} else if ("TOFAIL".equals(dto.getRenewalStatus())) {
			templateCode = "XB0004";
		}
		ntnNotificationService.sendNotification(requestCtx, userId, templateCode, sysdata);

		OrdAfterLog ordAfterLog = new OrdAfterLog();
		ordAfterLog.setGeneralId(dto.getRenewalId());
		ordAfterLog.setIdType("RENEWAL");
		ordAfterLog.setComment("定时任务自动发送!");
		ordAfterLog.setStatus(dto.getRenewalStatus());
		ordAfterLog.setStatusDate(new Date());
		ordAfterLog.setObjectVersionNumber(1L);
		ordAfterLogService.insertSelective(requestCtx, ordAfterLog);
	}
}
