package clb.core.commission.job;

import clb.core.commission.mapper.CmnChannelCommissionShowMapper;
import clb.core.commission.mapper.CmnSupplierCommissionShowMapper;
import clb.core.commission.mapper.CmnTradeRouteShowMapper;
import clb.core.commission.service.impl.CmnChannelCommissionDataThread;
import clb.core.commission.service.impl.CmnSupplierCommissionDataThread;
import clb.core.commission.service.impl.CmnTradeRouteDataThread;
import com.hand.hap.job.AbstractJob;

import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @jiaolong.li@hand-china.com
 * @create 2017-08-13 14:03
 **/
public class CmnTransferDataJob extends AbstractJob {

    @Autowired
    private CmnSupplierCommissionShowMapper supplierCommissionShowMapper;

    @Autowired
    private CmnChannelCommissionShowMapper commissionShowMapper;

    @Autowired
    private CmnTradeRouteShowMapper routeShowMapper;

    @Override
    public void safeExecute(JobExecutionContext jobExecutionContext) throws Exception {

        JobDetail detail = jobExecutionContext.getJobDetail();
        JobKey key = detail.getKey();
        TriggerKey triggerKey = jobExecutionContext.getTrigger().getKey();
        
        routeShowMapper.deleteRouteData();
        routeShowMapper.insertShowRouteData();
        
        ExecutorService exe = Executors.newFixedThreadPool(3);
        exe.execute(new CmnTradeRouteDataThread(routeShowMapper));
        exe.execute(new CmnSupplierCommissionDataThread(supplierCommissionShowMapper));
        exe.execute(new CmnChannelCommissionDataThread(commissionShowMapper));
        exe.shutdown();
        while (true) {
            if (exe.isTerminated()) {
                System.out.println("供应商佣金、渠道佣金、交易路线数据转移结束了！");
                break;
            }
        }
    }
}
