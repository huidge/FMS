package clb.core.system.controllers;

/**
 * Created by jiaolong.li on 2017-05-10.
 */
import clb.core.common.service.ISequenceService;
import clb.core.system.dto.ClbUser;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.system.dto.ClbEmployee;
import clb.core.system.service.IClbEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class ClbEmployeeController extends BaseController {

    @Autowired
    private IClbEmployeeService service;

    @Autowired
    private ISequenceService sequenceService;

    @RequestMapping(value = "/clb/hr/employee/query")
    @ResponseBody
    public ResponseData query(ClbEmployee dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        //return new ResponseData(service.select(requestContext, dto, page, pageSize));
        return new ResponseData(service.selectAllFields(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/clb/hr/employee/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request, @RequestBody List<ClbEmployee> dto) {
        IRequest requestCtx = createRequestContext(request);

        // 员工编号自动编号
        for (ClbEmployee employee : dto) {
            if(StringUtils.isEmpty(employee.getEmployeeCode())) {
                employee.setEmployeeCode(sequenceService.getSequence("EMPLOYEE"));
            }
        }

        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/clb/hr/employee/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<ClbEmployee> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }
}