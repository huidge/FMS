package clb.core.commission.job;

import clb.core.commission.dto.CmnChannelCommission;
import clb.core.commission.dto.CmnSupplierCommission;
import clb.core.commission.dto.CmnTradeRoute;
import clb.core.commission.service.ICmnChannelCommissionService;
import clb.core.commission.service.ICmnSupplierCommissionService;
import clb.core.commission.service.ICmnSupplierRouteService;
import clb.core.commission.service.ICmnTradeRouteService;
import clb.core.commission.service.impl.CmnChannelCommissionServiceImpl;
import clb.core.commission.service.impl.CmnTradeRouteThread;
import clb.core.common.utils.ListUtil;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.job.AbstractJob;
import com.hand.hap.system.service.IProfileService;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @jiaolong.li@hand-china.com
 * @create 2017-08-13 14:03
 **/
public class CmnTradeRouteJob extends AbstractJob {

    @Autowired
    private ICmnTradeRouteService routeService;

    @Autowired
    private ICmnSupplierCommissionService supplierCommissionService;

    @Autowired
    private ICmnChannelCommissionService channelCommissionService;

    @Autowired
    private IProfileService profileService;

    @Autowired
    private ICmnSupplierRouteService speRouteService;
    
    @Autowired
    private Scheduler quartzScheduler;

    @Override
    public void safeExecute(JobExecutionContext jobExecutionContext) throws Exception {

        JobDetail detail = jobExecutionContext.getJobDetail();
        JobKey key = detail.getKey();
        TriggerKey triggerKey = jobExecutionContext.getTrigger().getKey();

        IRequest requestCtx = RequestHelper.newEmptyRequest();

        // 删除数据
        routeService.deleteRouteData(new CmnTradeRoute());

        // 分批取出渠道佣金数据启动线程计算
        List<CmnChannelCommission> qtyCommissionList = channelCommissionService.queryLineQty();
        if (qtyCommissionList != null && qtyCommissionList.size() > 0) {
            CmnChannelCommission qtyCommission = qtyCommissionList.get(0);
            Long channelCmnLineQty = qtyCommission.getTotalLineQty();

            if (channelCmnLineQty != null) {
                String routeProfileValue = profileService.getProfileValue(requestCtx, "ROUTE_THREAD_QTY");
                String routeBatchValue = profileService.getProfileValue(requestCtx, "ROUTE_BATCH_QTY");
                int routeValue = 1;
                int routeBatch = 1;
                try {
                    routeValue = Integer.parseInt(routeProfileValue);
                    routeBatch = Integer.parseInt(routeBatchValue);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    routeValue = 300;
                    routeBatch = 2500;
                }

                int batchQty = (int) Math.ceil((float) channelCmnLineQty / routeBatch);

                // ExecutorService exe = Executors.newCachedThreadPool();
                ExecutorService exe = Executors.newFixedThreadPool(routeValue);
                for (int i = 1; i <= batchQty + 1; i++) {
                    List<CmnChannelCommission> channelCommissionList = new ArrayList<CmnChannelCommission>();
                    CmnChannelCommission paraCmn = new CmnChannelCommission();
                    paraCmn.setStartLineId(qtyCommission.getMinLineId() + (i - 1) * routeBatch);
                    //paraCmn.setBatchCount(new Long(batchQty));
                    paraCmn.setBatchCount(new Long(routeBatch));
                    channelCommissionList = channelCommissionService.selectBatchChannelCmn(paraCmn);
                    /*if (channelCommissionList.size() > 0) {
                        exe.execute(new CmnTradeRouteThread(channelCommissionList,
                                channelCommissionService,
                                supplierCommissionService,
                                routeService));
                    }*/
                    exe.execute(new CmnTradeRouteThread(qtyCommission.getMinLineId() + (i - 1) * routeBatch,
                            new Long(routeBatch),
                            channelCommissionService,
                            supplierCommissionService,
                            routeService));
                }
                exe.shutdown();
                while (true) {
                    if (exe.isTerminated()) {
                        System.out.println("交易路线多线程计算结束了！");
                        break;
                    }
                }

                // 更新记录的ParentRouteId
                routeService.updateParentRouteId(new CmnTradeRoute());
            }
        }

        // 临时表数据转移到show表
        // routeService.transferData();
        JobKey jobKey = JobKey.jobKey("佣金数据转移", "DEFAULT");
        quartzScheduler.triggerJob(jobKey);
    }
}
