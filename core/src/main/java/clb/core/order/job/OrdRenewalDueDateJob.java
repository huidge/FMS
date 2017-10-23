package clb.core.order.job;
/**
 * 订单续保到期日计算定时任务
 **/

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.job.AbstractJob;
import com.hand.hap.system.dto.DTOStatus;

import clb.core.common.utils.DateUtil;
import clb.core.order.dto.OrdOrder;
import clb.core.order.dto.OrdOrderRenewal;
import clb.core.order.mapper.OrdAfterMapper;
import clb.core.order.mapper.OrdOrderMapper;
import clb.core.order.service.IOrdAfterService;
import clb.core.order.service.IOrdOrderRenewalService;
import clb.core.order.service.IOrdOrderService;

public class OrdRenewalDueDateJob extends AbstractJob {

	public static Log log = LogFactory.getLog(OrdRenewalDueDateJob.class);

	@Autowired
    private OrdOrderMapper orderMapper;
    
    @Autowired
    private IOrdOrderService orderService;

	@Override
	public void safeExecute(JobExecutionContext jobExecutionContext) throws Exception {

		JobDetail detail = jobExecutionContext.getJobDetail();
		JobKey key = detail.getKey();
		log.info("---" + key + " executing at " + new Date());

		OrdOrder dto = new OrdOrder();
        List<OrdOrder> orders = orderMapper.querySublineItemName(dto);
        List<OrdOrder> resData = new ArrayList<>();
        StringBuffer stringBuffer = new StringBuffer();
        for(OrdOrder order:orders){
        	String payMethod = order.getPayMethod();
            if("FJ".equals(payMethod)){
            	//当前时间
            	Date now = new Date();
            	//续保到期日
            	Date renewalDueDate = order.getRenewalDueDate();
            	//首期保费日
            	Date firstPayDate = order.getFirstPayDate();
            	//当前期数
            	Long payPeriods = order.getPayPeriods();
            	//年期
            	String sublineItemName = order.getSublineItemName();
            	if(sublineItemName == null)sublineItemName = "0";
            	Long sublineItem = 0L;
            	try{
            		sublineItem = Long.valueOf(sublineItemName); 
            	}catch(Exception e){
            		sublineItem = 0L;
            		stringBuffer.append(String.format("订单%s年期不是数字类型",order.getOrderNumber()));
            		log.debug(String.format("订单%s年期不是数字类型",order.getOrderNumber()));
            	}
            	Date compareDate = null;
            	//如果续保到期日为空,让首期保费日加一年，再比较
            	if(renewalDueDate == null){
            		if(firstPayDate != null){
            			compareDate = DateUtil.addYear(firstPayDate);
            		}
            	}else{
            		compareDate = renewalDueDate;
            	}
            	if(compareDate != null){
            		if(DateUtil.isSameYearMonthDay(compareDate,now)){
            			if(sublineItem != 0){
            				//若当前期数为空，设置当前期数为1
            				if(payPeriods == null){
            					//续期保费日加一年
                    			order.setRenewalDueDate(DateUtil.addYear(compareDate));
                    			//当前期数加一
                    			order.setPayPeriods(1L);
                    			order.set__status(DTOStatus.UPDATE);
                    			resData.add(order);
            				}
            				else if(payPeriods < sublineItem){
            					//续期保费日加一年
                    			order.setRenewalDueDate(DateUtil.addYear(compareDate));
                    			//当前期数加一
                    			order.setPayPeriods(payPeriods+1);
                    			order.set__status(DTOStatus.UPDATE);
                    			resData.add(order);
            				}
            			}
            		}
            	}else{
            		stringBuffer.append(String.format("订单%s续保到期日为空,首期保费日为空,设置数据失败!",order.getOrderNumber()));
            		log.debug(String.format("订单%s续保到期日为空,首期保费日为空,设置数据失败!",order.getOrderNumber()));
            	}
            }
            
        } 
        if(stringBuffer.length() <= 225){
        	setExecutionSummary(stringBuffer.toString());
        }
        IRequest iRequest = RequestHelper.getCurrentRequest(true);
        orderService.batchUpdate(iRequest,resData);
	}
}
