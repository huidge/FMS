package clb.core.commission.controllers;
/**
 * Created by wanjun.feng on 2017-09-11.
 */

import clb.core.commission.dto.CmnChannelRatio;
import clb.core.commission.dto.CmnChannelRatioDetail;
import clb.core.commission.service.ICmnChannelRatioDetailService;
import clb.core.commission.service.ICmnChannelRatioService;
import clb.core.production.dto.PrdItems;
import clb.core.production.service.IPrdItemsService;
import clb.core.sys.controllers.ClbBaseController;

import com.codahale.metrics.annotation.Timed;
import com.hand.hap.core.IRequest;
import com.hand.hap.intergration.annotation.HapInbound;
import com.hand.hap.system.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class WsCmnChannelRatioController extends ClbBaseController {

    @Autowired
    private ICmnChannelRatioService cmnChannelRatioService;
    @Autowired
    private ICmnChannelRatioDetailService cmnChannelRatioDetailService;
    @Autowired
    private IPrdItemsService prdItemsService;

    @Timed
    @HapInbound(apiName = "ClbWsCmnChannelRatioQuery")
    @RequestMapping(value = "/api/cmn/channel/ratio/query", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData query(@RequestBody CmnChannelRatio dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(cmnChannelRatioService.wsSelectChannelRatios(requestContext, dto, page, pageSize));
    }

    @Timed
    @HapInbound(apiName = "ClbWsCmnChannelRatioSubmit")
    @RequestMapping(value = "/api/cmn/channel/ratio/submit", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData update(@RequestBody List<CmnChannelRatio> dto, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        return cmnChannelRatioService.batchSubmit(requestCtx, dto);
    }
    
    @Timed
    @HapInbound(apiName = "ClbWsCmnChannelRatioDetailQuery")
    @RequestMapping(value = "/api/cmn/channel/ratio/detail/query", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData queryDetail(@RequestBody CmnChannelRatioDetail dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(cmnChannelRatioDetailService.selectChannelRatioDetails(requestContext, dto, page, pageSize));
    }

    @Timed
    @HapInbound(apiName = "ClbWsCmnChannelRatioDetailSubmit")
    @RequestMapping(value = "/api/cmn/channel/ratio/detail/submit", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData updateDetail(@RequestBody List<CmnChannelRatioDetail> dto, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        for (CmnChannelRatioDetail cmnChannelRatioDetail : dto) {
			if (cmnChannelRatioDetail.getItemId()!=null) {
				PrdItems prdItems = new PrdItems();
				prdItems.setItemId(cmnChannelRatioDetail.getItemId());
				PrdItems items = prdItemsService.selectByPrimaryKey(requestCtx, prdItems);
				cmnChannelRatioDetail.setBigClass(items.getBigClass());
				cmnChannelRatioDetail.setMidClass(items.getMidClass());
				cmnChannelRatioDetail.setMinClass(items.getMinClass());
			}
		}
        return cmnChannelRatioDetailService.batchSubmit(requestCtx, dto);
    }
    
    @Timed
    @HapInbound(apiName = "ClbWsCmnChannelRatioDetailSubmit")
    @RequestMapping(value = "/api/cmn/channel/ratio/detail/remove", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<CmnChannelRatioDetail> dto) {
        cmnChannelRatioDetailService.batchDelete(dto);
        return new ResponseData();
    }
}