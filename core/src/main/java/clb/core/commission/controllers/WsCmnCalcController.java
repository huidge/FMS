package clb.core.commission.controllers;

import clb.core.commission.dto.CmnCalc;
import clb.core.commission.service.ICmnCalcService;
import clb.core.sys.controllers.ClbBaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.intergration.annotation.HapInbound;
import com.hand.hap.system.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.codahale.metrics.annotation.Timed;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
public class WsCmnCalcController extends ClbBaseController {

    @Autowired
    private ICmnCalcService service;

    @Timed
    @HapInbound(apiName = "ClbWsCmnProductionCalcPremium")
    @RequestMapping(value = "/api/cmn/productionCalcPremium", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData productionCalcPremium(@RequestBody CmnCalc dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request){
        IRequest requestContext = createRequestContext(request);
        return service.productionCalcPremium(requestContext,dto, page, pageSize);
    }
}