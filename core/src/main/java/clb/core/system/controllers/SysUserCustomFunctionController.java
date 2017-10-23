package clb.core.system.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.system.dto.SysUserCustomFunction;
import clb.core.system.service.ISysUserCustomFunctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class SysUserCustomFunctionController extends BaseController {

    @Autowired
    private ISysUserCustomFunctionService service;


    @RequestMapping(value = "/fms/sys/user/custom/function/query")
    @ResponseBody
    public ResponseData query(SysUserCustomFunction dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/fms/sys/user/custom/function/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request, @RequestBody List<SysUserCustomFunction> dto) {
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/fms/sys/user/custom/function/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<SysUserCustomFunction> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }
}