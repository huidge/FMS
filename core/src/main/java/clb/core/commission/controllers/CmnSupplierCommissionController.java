package clb.core.commission.controllers;
/**
 * Created by jiaolong.li on 2017-04-24.
 */

import clb.core.common.mapper.FiredJobMapper;
import com.hand.hap.core.exception.FieldRequiredException;
import com.hand.hap.job.exception.JobException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.commission.dto.CmnSupplierCommission;
import clb.core.commission.service.ICmnSupplierCommissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class CmnSupplierCommissionController extends BaseController {

    @Autowired
    private ICmnSupplierCommissionService service;

    @Autowired
    private Scheduler quartzScheduler;

    @Autowired
    private FiredJobMapper firedJobMapper;

    /**
     * 时间参数转换
     * @param binder
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(true);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @RequestMapping(value = "/fms/cmn/supplier/commission/query")
    @ResponseBody
    public ResponseData query(CmnSupplierCommission dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectShowAllFields(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/fms/cmn/supplier/commission/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request, @RequestBody List<CmnSupplierCommission> dto) {
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/fms/cmn/supplier/commission/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<CmnSupplierCommission> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }

    /**
     * 执行供应商佣金表计划任务
     *
     * @return
     */
    @RequestMapping(value = "/fms/cmn/supplier/commission/startJob", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData startJob(HttpServletRequest request)
            throws SchedulerException, JobException, ClassNotFoundException,
            FieldRequiredException, UnsupportedEncodingException {

        ResponseData response = new ResponseData(true);
        String speCommissionJobStatus = firedJobMapper.queryJobFireStatus("DEFAULT", "供应商佣金计算");
        String cnlCommissionJobStatus = firedJobMapper.queryJobFireStatus("DEFAULT", "渠道佣金计算");
        String routeJobStatus = firedJobMapper.queryJobFireStatus("DEFAULT", "佣金交易路线计算");

        if ("EXECUTING".equals(speCommissionJobStatus)){
            response.setSuccess(false);
            response.setMessage("供应商佣金计算JOB正在运行，请稍后再试!");
            return response;
        } else if ("EXECUTING".equals(cnlCommissionJobStatus)) {
            response.setSuccess(false);
            response.setMessage("渠道佣金计算JOB正在运行，请稍后再试!");
            return response;
        } else if ("EXECUTING".equals(routeJobStatus)) {
            response.setSuccess(false);
            response.setMessage("佣金交易路线计算JOB正在运行，请稍后再试!");
            return response;
        } else {

            JobKey jobKey = JobKey.jobKey("供应商佣金计算", "DEFAULT");
            quartzScheduler.triggerJob(jobKey);

            response.setSuccess(true);
            response.setMessage("JOB已启动，请到计划任务->执行记录功能中查看执行情况!");
            return response;
        }

    }
}