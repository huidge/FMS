package clb.core.forecast.job;
/**
 * @name FetTimeReceivableDataJob
 * @description 应收、应付数据导入定时任务
 * @author bo.wu@hand-china.com 2017年6月21日15:53:04
 **/

import clb.core.common.exceptions.CommonException;
import clb.core.common.utils.CommonUtil;
import clb.core.common.utils.SpringConfigTool;
import clb.core.forecast.dto.FetPeriod;
import clb.core.forecast.dto.FetSupposeCommon;
import clb.core.forecast.dto.FetSupposeUpdateVersion;
import clb.core.forecast.dto.FetTimeReceivable;
import clb.core.forecast.mapper.FetPeriodMapper;
import clb.core.forecast.service.IFetSupposeCommonService;
import clb.core.forecast.service.IFetTimeReceivableService;
import clb.core.forecast.utils.SupposeCommonUtil;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.job.AbstractJob;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FetTimeReceivableDataJob extends AbstractJob {

    public static Log log = LogFactory.getLog(FetTimeReceivableDataJob.class);

    @Autowired
    private FetPeriodMapper periodMapper;

    @Autowired
    private IFetTimeReceivableService receivableService;

    @Override
    public void safeExecute(JobExecutionContext jobExecutionContext) throws Exception {

        JobKey key = jobExecutionContext.getJobDetail().getKey();
        log.info("---" + key + " executing at " + new Date());

        IRequest iRequest = RequestHelper.newEmptyRequest();
        FetSupposeUpdateVersion version = new FetSupposeUpdateVersion();
        try{

            IFetSupposeCommonService service = (IFetSupposeCommonService) SpringConfigTool.getBean("FetSupposeCommonServiceImpl");
            List<FetSupposeCommon> data =  service.getAllOrderData(version);

            //应收
            List<FetTimeReceivable> receivables = SupposeCommonUtil.commonListToTimeReceivableList(data);
            receivableService.batchUpdate(iRequest, receivables);
        } catch(Exception e){
            CommonUtil.printStackTraceToStr(e);
        } finally {
            //transactionManager.commit(transactionStatus);
        }
    }

   
}
