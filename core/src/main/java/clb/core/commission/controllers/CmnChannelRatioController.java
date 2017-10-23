package clb.core.commission.controllers;
/**
 * Created by wanjun.feng on 2017-09-11.
 */

import clb.core.commission.dto.CmnChannelRatio;
import clb.core.commission.service.ICmnChannelRatioService;
import clb.core.sys.controllers.ClbBaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping(value = "/fms/cmn/channel/ratio")
public class CmnChannelRatioController extends ClbBaseController {

    @Autowired
    private ICmnChannelRatioService cmnChannelRatioService;

    @RequestMapping(value = "/query")
    @ResponseBody
    public ResponseData query(CmnChannelRatio dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(cmnChannelRatioService.selectChannelRatios(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request, @RequestBody List<CmnChannelRatio> dto) {
        IRequest requestCtx = createRequestContext(request);
        return cmnChannelRatioService.batchSubmit(requestCtx, dto);
    }

    @RequestMapping(value = "/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<CmnChannelRatio> dto) {
        cmnChannelRatioService.batchDelete(dto);
        return new ResponseData();
    }
}