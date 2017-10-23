package clb.core.order.controllers;

import clb.core.order.dto.OrdCommission;
import clb.core.order.dto.OrdOrder;
import clb.core.order.mapper.OrdCommissionMapper;
import clb.core.order.service.IOrdCommissionService;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.order.dto.OrdAddition;
import clb.core.order.service.IOrdAdditionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

    @Controller
    public class OrdAdditionController extends BaseController {

        @Autowired
        private IOrdAdditionService service;

        @Autowired
        private OrdCommissionMapper ordCommissionMapper;

        @Autowired
        private IOrdCommissionService ordCommissionService;

        @RequestMapping(value = "/fms/ord/addition/query")
        @ResponseBody
        public ResponseData query(OrdAddition dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
            IRequest requestContext = createRequestContext(request);
            return new ResponseData(service.queryOrdAddition(requestContext, dto, page, pageSize));
        }

        @RequestMapping(value = "/fms/ord/addition/submit")
        @ResponseBody
        public ResponseData update(HttpServletRequest request, @RequestBody List<OrdAddition> dto) {
            IRequest requestCtx = createRequestContext(request);
            return new ResponseData(service.batchUpdate(requestCtx, dto));
        }

        @RequestMapping(value = "/fms/ord/addition/remove")
        @ResponseBody
        public ResponseData delete(HttpServletRequest request, @RequestBody List<OrdAddition> dto) {

            List<OrdCommission> ordCommissions = new ArrayList<>();
            for (OrdAddition o:dto) {
                OrdCommission ordCommission = new OrdCommission();
                ordCommission.setAdditionId(o.getAdditionId());
                List<OrdCommission> ordCommissionList = ordCommissionMapper.queryOrdCommissionAll(ordCommission);
                ordCommissions.addAll(ordCommissionList);
            }
            service.batchDelete(dto);
            ordCommissionService.batchDelete(ordCommissions);
            return new ResponseData();
        }

        @InitBinder
        public void initBinder(WebDataBinder binder) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setLenient(true);
            binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
        }
    }