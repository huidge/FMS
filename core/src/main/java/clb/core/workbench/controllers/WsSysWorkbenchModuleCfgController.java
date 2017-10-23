package clb.core.workbench.controllers;

import clb.core.production.dto.PrdItems;
import clb.core.sys.controllers.ClbBaseController;
import clb.core.workbench.dto.SysWorkbenchModuleCfg;
import clb.core.workbench.service.ISysWorkbenchModuleCfgService;
import com.codahale.metrics.annotation.Timed;
import com.hand.hap.core.IRequest;
import com.hand.hap.intergration.annotation.HapInbound;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 工作台模块配置.
 *
 * @author Rex.Hua
 */
@Controller
public class WsSysWorkbenchModuleCfgController extends ClbBaseController {

@Autowired
private ISysWorkbenchModuleCfgService service;

    @Timed
    @HapInbound(apiName = "ClbWsSysWorkbenchCfg")
    @RequestMapping(value = "/api/workbench/module/list", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData query(@RequestBody SysWorkbenchModuleCfg dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.wsSelectFunctionInfo(requestContext,dto,page,pageSize));
    }

}