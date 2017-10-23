package clb.core.commission.controllers;
/**
 * Created by jiaolong.li on 2017-04-24.
 */

import clb.core.commission.dto.CmnChannelCommission;
import clb.core.commission.dto.CmnSupplierCommission;
import clb.core.commission.service.ICmnChannelCommissionService;
import clb.core.commission.service.ICmnSupplierCommissionService;
import clb.core.common.mapper.FiredJobMapper;
import clb.core.common.service.ISequenceService;
import clb.core.common.utils.CommonUtil;
import clb.core.fnd.controllers.ImportTempController;
import clb.core.fnd.utils.Constants;
import clb.core.fnd.utils.ValidationTableException;
import clb.core.sys.controllers.ClbBaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.exception.FieldRequiredException;
import com.hand.hap.job.dto.JobCreateDto;
import com.hand.hap.job.exception.JobException;
import com.hand.hap.job.service.IQuartzService;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Controller
public class CmnChannelCommissionController extends ClbBaseController {

    @Autowired
    private ICmnChannelCommissionService service;

    @Autowired
    private FiredJobMapper firedJobMapper;

    @Autowired
    private Scheduler quartzScheduler;

    @Autowired
    private ISequenceService sequenceService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

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

    @RequestMapping(value = "/fms/cmn/channel/commission/query")
    @ResponseBody
    public ResponseData query(CmnChannelCommission dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);

        if (dto.getRequestFirst()) {
            dto.setOrderBy("supplierId," +
                    "        channelTypeCode," +
                    "        channelLevelId," +
                    "        channelId," +
                    "        itemId," +
                    "        contributionPeriod," +
                    "        currency," +
                    "        payMethod," +
                    "        policyholdersMinAge," +
                    "        policyholdersMaxAge," +
                    "        effectiveStartDate," +
                    "        effectiveEndDate");
            return new ResponseData(service.selectShowAllFields(requestContext, dto, page, pageSize));
        } else {
            return new ResponseData();
        }
    }

    @RequestMapping(value = "/fms/cmn/channel/commission/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request, @RequestBody List<CmnChannelCommission> dto) {
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/fms/cmn/channel/commission/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<CmnChannelCommission> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }

    /**
     * 执行渠道佣金表计划任务
     *
     * @return
     */
    @RequestMapping(value = "/fms/cmn/channel/commission/startJob", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData startJob(HttpServletRequest request, String jobName)
            throws SchedulerException, JobException, ClassNotFoundException,
            FieldRequiredException, UnsupportedEncodingException {

        ResponseData response = new ResponseData(true);
        String speCommissionJobStatus = firedJobMapper.queryJobFireStatus("DEFAULT", "供应商佣金计算");
        String cnlCommissionJobStatus = firedJobMapper.queryJobFireStatus("DEFAULT", "渠道佣金计算");
        String routeJobStatus = firedJobMapper.queryJobFireStatus("DEFAULT", "佣金交易路线计算");

        if ("".equals(jobName)) {
            response.setSuccess(false);
            response.setMessage("任务名称不能为空!");
            return response;
        }

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

            if ("渠道佣金计算".equals(jobName)) {
                JobKey jobKey = JobKey.jobKey("渠道佣金计算", "DEFAULT");
                quartzScheduler.triggerJob(jobKey);
            } else if ("佣金交易路线计算".equals(jobName)) {
                JobKey jobKey = JobKey.jobKey("佣金交易路线计算", "DEFAULT");
                quartzScheduler.triggerJob(jobKey);
            } else if ("佣金数据转移".equals(jobName)) {
                JobKey jobKey = JobKey.jobKey("佣金数据转移", "DEFAULT");
                quartzScheduler.triggerJob(jobKey);
            }

            response.setSuccess(true);
            response.setMessage("JOB已启动，请到计划任务->执行记录功能中查看执行情况!");
            return response;
        }
    }

    /**
     *  计算单个渠道佣金值
     * @return
     */
    @RequestMapping(value = "/fms/cmn/channel/commission/calculateSingleCmn", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData calculateSingleCmn(HttpServletRequest request, Long channelId)
            throws SchedulerException, JobException, ClassNotFoundException,
            FieldRequiredException, UnsupportedEncodingException {
        IRequest requestCtx = createRequestContext(request);
        ResponseData response = new ResponseData(true);
        Locale locale = RequestContextUtils.getLocale(request);

        if (channelId == null) {
            response.setSuccess(false);
            response.setMessage("渠道为不能为空!");
            return response;
        }
        Long jobBatchId = Long.valueOf(sequenceService.getSequence(Constants.IMPORT_SEQUENCE_CODE));

        //另起线程
        CmnChannelCommissionController.OtherThread threa = new CmnChannelCommissionController.OtherThread
                (request, requestCtx, locale, channelId, jobBatchId);
        new Thread(threa).start();

        /*response.setMessage("已计算，请重新查询");*/
        response.setMessage(jobBatchId.toString());
        return response;
    }

    /*
     * 定时监控返回值
     * @param batchId
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/fms/cmn/channel/commission/getSingleResult", method = RequestMethod.POST)
    public JSONObject getSingleResult(@RequestParam Long batchId, HttpServletRequest request) throws Exception {
        JSONObject responseData = getSessionBean("cmnjob_" + batchId);
        if(responseData == null) {
            responseData =new JSONObject();
            responseData.put("success", true);
            responseData.put("code", "-1");
            return responseData;
        } else {
            return responseData;
        }

    }

    class OtherThread implements Runnable{
        private HttpServletRequest request;
        private IRequest requestContext ;
        private Locale locale;
        private Long channelId;
        private Long jobBatchId;

        public OtherThread(HttpServletRequest request,
                           IRequest requestContext,
                           Locale locale,
                           Long channelId,
                           Long jobBatchId) {
            this.request = request;
            this.requestContext = requestContext;
            this.locale = locale;
            this.channelId = channelId;
            this.jobBatchId = jobBatchId;
        }

        @Override
        public void run() {
            JSONObject responseData =new JSONObject();
            responseData.put("success", true);
            responseData.put("code", "0");

            /*try {
                service.processSingleChannelCmn(requestContext, channelId, jobBatchId);
            } catch (Exception e) {
                responseData.put("success", false);
                responseData.put("code", "N");
                String errorMessage = getMessageSource().getMessage( Constants.IMPORT_EXCEPTION, null, locale );
                responseData.put("message", errorMessage+"");
                CommonUtil.printStackTraceToStr(e);
            }*/
            service.processSingleChannelCmn(requestContext, channelId, jobBatchId);
            saveSessionRedis("cmnjob_" + jobBatchId, responseData);
        }

    }



}