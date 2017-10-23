package clb.core.commission.controllers;
/**
 * Created by wanjun.feng on 2017-09-11.
 */

import clb.core.commission.dto.CmnChannelRatioDetail;
import clb.core.commission.service.ICmnChannelRatioDetailService;
import clb.core.sys.controllers.ClbBaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping(value = "/fms/cmn/channel/ratio/detail")
public class CmnChannelRatioDetailController extends ClbBaseController {

    @Autowired
    private ICmnChannelRatioDetailService cmnChannelRatioDetailService;

    @RequestMapping(value = "/query")
    @ResponseBody
    public ResponseData query(CmnChannelRatioDetail dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(cmnChannelRatioDetailService.selectChannelRatioDetails(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request, @RequestBody List<CmnChannelRatioDetail> dto) {
        IRequest requestCtx = createRequestContext(request);
        return cmnChannelRatioDetailService.batchSubmit(requestCtx, dto);
    }

    @RequestMapping(value = "/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<CmnChannelRatioDetail> dto) {
        cmnChannelRatioDetailService.batchDelete(dto);
        return new ResponseData();
    }
}