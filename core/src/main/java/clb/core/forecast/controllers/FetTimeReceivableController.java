package clb.core.forecast.controllers;

import clb.core.common.exceptions.CommonException;
import clb.core.forecast.dto.FetReceivable;
import clb.core.forecast.service.IFetReceivableService;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.exception.FieldRequiredException;
import com.hand.hap.job.exception.JobException;
import com.hand.hap.job.service.IQuartzService;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class FetTimeReceivableController extends BaseController{

    @Autowired
    private IFetReceivableService service;

    @Autowired
    private IQuartzService quartzService;

    @Autowired
    private Scheduler quartzScheduler;

    /**
     * 执行任务
     *
     * @return
     */
    @RequestMapping(value = "/fms/fet/timeReceivable/startJob", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData startJob(HttpServletRequest request)
            throws SchedulerException, JobException, ClassNotFoundException,
            FieldRequiredException, UnsupportedEncodingException {

        JobKey jobKey = JobKey.jobKey("应收预测数据导入", "DEFAULT");
        quartzScheduler.triggerJob(jobKey);

        return new ResponseData();
    }
}