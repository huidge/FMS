package clb.core.channel.controllers;

import clb.core.channel.dto.*;
import clb.core.channel.service.ICnlPerformanceService;
import clb.core.common.utils.DateUtil;
import clb.core.common.utils.StringUtil;
import clb.core.forecast.dto.FetReceivable;
import clb.core.forecast.service.IFetTimeReceivableService;
import clb.core.sys.controllers.ClbBaseController;
import com.codahale.metrics.annotation.Timed;
import com.hand.hap.core.IRequest;
import com.hand.hap.intergration.annotation.HapInbound;
import com.hand.hap.system.dto.ResponseData;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@Controller
public class WsCnlMyPerformanceController extends ClbBaseController {

    @Autowired
    private ICnlPerformanceService performanceService;

    @Autowired
    private IFetTimeReceivableService receivableService;

    /**
     *  当月业绩
     * @param dto
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsCnlMyPerformance")
    @RequestMapping(value = "/api/ordPerformance/query", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public ResponseData query(@RequestBody CnlPerformanceParas dto,
                              @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize,
                              HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);

        // 调用数据计算接口
        receivableService.insertTimeData(null, dto.getChannelId());

        List<CnlPerformanceResults> resultsList = new ArrayList<CnlPerformanceResults>();
        resultsList = performanceService.queryPerformance(dto, page, pageSize);
        return new ResponseData(resultsList);
    }
}
