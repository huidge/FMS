package clb.core.commission.job;
/**
 * 渠道佣金表定时任务
 *
 * @jiaolong.li@hand-china.com
 * @create 2017-04-25 17:34
 **/

import clb.core.channel.dto.CnlChannel;
import clb.core.channel.dto.CnlChannelContract;
import clb.core.channel.dto.CnlContractRate;
import clb.core.channel.dto.CnlLevel;
import clb.core.channel.service.ICnlContractRateService;
import clb.core.channel.service.ICnlLevelService;
import clb.core.commission.dto.*;
import clb.core.commission.mapper.CmnBasicMapper;
import clb.core.commission.service.*;
import clb.core.commission.service.impl.CmnChannelCommissionThread;
import clb.core.commission.service.impl.CmnTradeRouteThread;
import clb.core.common.service.ISequenceService;
import clb.core.common.utils.ListUtil;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.job.AbstractJob;
import com.hand.hap.system.service.IProfileService;
import com.thoughtworks.xstream.mapper.Mapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opensaml.xml.encryption.P;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.jta.WebSphereUowTransactionManager;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class CmnChannelCommissionJob extends AbstractJob {

    public static Log log = LogFactory.getLog(CmnChannelCommissionJob.class);

    @Autowired
    private ICmnSupplierCommissionService supplierCommissionService;

    @Autowired
    private ICmnChannelCommissionService channelCommissionService;

    @Autowired
    private ICnlLevelService levelService;

    @Autowired
    private IProfileService profileService;

    @Autowired
    private Scheduler quartzScheduler;

    @Override
    public void safeExecute(JobExecutionContext jobExecutionContext) throws Exception {

        JobDetail detail = jobExecutionContext.getJobDetail();
        JobKey key = detail.getKey();
        TriggerKey triggerKey = jobExecutionContext.getTrigger().getKey();
        IRequest requestCtx = RequestHelper.newEmptyRequest();

        // 清空渠道佣金表数据
        channelCommissionService.deleteCmnData(new CmnChannelCommission());

        // 取出所有供应商佣金表数据
        CmnSupplierCommission commission = new CmnSupplierCommission();
        List<CmnSupplierCommission> speCmnList = new ArrayList<CmnSupplierCommission>();
        speCmnList = supplierCommissionService.selectAllFields(commission);

        String profileValue = profileService.getProfileValue(requestCtx, "CHANNEL_COMMISSION_THREAD_QTY");
        int threadQty = 1;
        try {
            threadQty = Integer.parseInt(profileValue);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            threadQty = 1;
        }

        ExecutorService exe = Executors.newFixedThreadPool(threadQty);
        List<List<CmnSupplierCommission>> lists= ListUtil.averageAssign(speCmnList, threadQty);

        for (int i = 0; i < lists.size(); i++) {
            exe.execute(new CmnChannelCommissionThread(lists.get(i), channelCommissionService,
                    supplierCommissionService, levelService));
        }
        exe.shutdown();
        while (true) {
            if (exe.isTerminated()) {
                System.out.println("渠道佣金表多线程计算结束了！");
                break;
            }
        }

        // 执行渠道佣金表生成任务
        JobKey jobKey = JobKey.jobKey("佣金交易路线计算", "DEFAULT");
        quartzScheduler.triggerJob(jobKey);
    }

}
