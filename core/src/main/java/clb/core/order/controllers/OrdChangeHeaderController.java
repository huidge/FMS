package clb.core.order.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;

import clb.core.order.dto.OrdChangeHeader;
import clb.core.order.service.IOrdChangeHeaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;

@Controller
public class OrdChangeHeaderController extends BaseController{

    @Autowired
    private IOrdChangeHeaderService ordChangeHeaderService;


    @RequestMapping(value = "/fms/ord/change/query")
    @ResponseBody
    public ResponseData query(OrdChangeHeader dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return ordChangeHeaderService.queryOrdChange(requestContext, dto);
    }

}