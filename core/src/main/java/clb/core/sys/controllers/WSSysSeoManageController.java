package clb.core.sys.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.hand.hap.core.IRequest;
import com.hand.hap.intergration.annotation.HapInbound;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;

import clb.core.sys.dto.SysSeoManage;
import clb.core.sys.service.ISysSeoManageService;
/*****
 * @author FengWanJun
 * @Date 2017-09-06
 */
@Controller
public class WSSysSeoManageController extends BaseController{

    @Autowired
    private ISysSeoManageService sysSeoManageService;
    
    @Timed
    @HapInbound(apiName = "ClbWsSysSeoQuery")
    @RequestMapping(value = "/api/public/sys/seo/query", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData query(SysSeoManage dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        List<SysSeoManage> data = sysSeoManageService.selectAll(requestContext);
        return new ResponseData(data);
    }
}