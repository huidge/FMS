package clb.core.channel.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.channel.dto.CnlContractRole;
import clb.core.channel.service.ICnlContractRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

    @Controller
    public class CnlContractRoleController extends BaseController{

    @Autowired
    private ICnlContractRoleService service;


    @RequestMapping(value = "/fms/cnl/contract/role/query")
    @ResponseBody
    public ResponseData query(CnlContractRole dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryCnlContractRole(requestContext,dto,page,pageSize));
    }

    @RequestMapping(value = "/fms/cnl/contract/role/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<CnlContractRole> dto){
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/fms/cnl/contract/role/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<CnlContractRole> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
    }