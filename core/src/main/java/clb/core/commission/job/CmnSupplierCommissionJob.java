package clb.core.commission.job;

import clb.core.commission.dto.*;
import clb.core.commission.mapper.CmnBasicMapper;
import clb.core.commission.mapper.CmnExtraMapper;
import clb.core.commission.mapper.CmnSupplierCommissionMapper;
import clb.core.commission.service.*;
import clb.core.commission.service.impl.CmnChannelCommissionThread;
import clb.core.commission.service.impl.CmnSupplierCommissionThread;
import clb.core.common.service.ISequenceService;
import clb.core.common.utils.DateUtil;
import clb.core.common.utils.ListUtil;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.job.AbstractJob;
import com.hand.hap.system.service.IProfileService;
import com.thoughtworks.xstream.mapper.Mapper;
import oracle.sql.DATE;
import org.apache.bcel.generic.RETURN;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.quartz.*;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * 供应商佣金表定时任务
 *
 * @jiaolong.li@hand-china.com
 * @create 2017-04-25 17:34
 **/
@Component
public class CmnSupplierCommissionJob extends AbstractJob {

    public static Log log = LogFactory.getLog(CmnSupplierCommissionJob.class);

    @Autowired
    private ICmnBasicService basicService;

    @Autowired
    private ICmnOverrideService overrideService;

    @Autowired
    private ICmnExtrasService extrasService;

    @Autowired
    private ICmnSupplierCommissionService supplierCommissionService;

    @Autowired
    private ICmnSupplierRouteService supplierRouteService;

    @Autowired
    private ISequenceService sequenceService;

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

        // 清理供应商佣金表
        supplierCommissionService.deleteCmnData(new CmnSupplierCommission());

        // 清空数据
        supplierRouteService.deleteRouteData(new CmnSupplierRoute());

        // 查询出所有basic记录
        List<CmnBasic> baiscList = basicService.queryBasic(null);

        String profileValue = profileService.getProfileValue(requestCtx, "SUPPLIER_COMMISSION_THREAD_QTY");
        int threadQty = 1;
        try {
            threadQty = Integer.parseInt(profileValue);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            threadQty = 1;
        }

        ExecutorService exe = Executors.newFixedThreadPool(threadQty);
        List<List<CmnBasic>> lists= ListUtil.averageAssign(baiscList, threadQty);

        for (int i = 0; i < lists.size(); i++) {
            exe.execute(new CmnSupplierCommissionThread(lists.get(i),
                                                     basicService,
                                                     overrideService,
                                                     extrasService,
                                                     supplierCommissionService,
                                                     supplierRouteService,
                                                     sequenceService));
        }
        exe.shutdown();
        while (true) {
            if (exe.isTerminated()) {
                System.out.println("供应商佣金多线程计算结束了！");
                break;
            }
        }

        // 执行渠道佣金表生成任务
        JobKey jobKey = JobKey.jobKey("渠道佣金计算", "DEFAULT");
        quartzScheduler.triggerJob(jobKey);
    }
}