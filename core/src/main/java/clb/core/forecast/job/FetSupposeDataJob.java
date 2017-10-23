package clb.core.forecast.job;
/**
 * @name FetSupposeDataJob
 * @description 应收、应付数据导入定时任务
 * @author bo.wu@hand-china.com 2017年6月21日15:53:04
 **/

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.springframework.beans.factory.annotation.Autowired;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.job.AbstractJob;

import clb.core.common.exceptions.CommonException;
import clb.core.common.utils.SpringConfigTool;
import clb.core.forecast.dto.FetPeriod;
import clb.core.forecast.mapper.FetPeriodMapper;
import clb.core.forecast.service.IFetSupposeCommonService;

public class FetSupposeDataJob extends AbstractJob {

    public static Log log = LogFactory.getLog(FetSupposeDataJob.class);

    @Autowired
    private FetPeriodMapper periodMapper;

    @Override
    public void safeExecute(JobExecutionContext jobExecutionContext) throws Exception {

        JobKey key = jobExecutionContext.getJobDetail().getKey();
        log.info("---" + key + " executing at " + new Date());
        //先获取所有打开状态下的未计算过的期间
        FetPeriod period = new FetPeriod();
    	period.setStatusCode("OPEN");
    	period.setUpdateFlag("Y");
    	List<FetPeriod> periodDatas = periodMapper.select(period);
    	IRequest iRequest = RequestHelper.getCurrentRequest(true);
    	IFetSupposeCommonService supposeCommonService = (IFetSupposeCommonService) SpringConfigTool.getBean("FetSupposeCommonServiceImpl");
    	try{
    		supposeCommonService.insertSupposeData(iRequest, periodDatas);
    	}catch(CommonException e){
    		setExecutionSummary(e.getDescriptionKey());
    	}
    }

   
}
