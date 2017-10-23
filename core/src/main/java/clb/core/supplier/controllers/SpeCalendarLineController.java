package clb.core.supplier.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.supplier.dto.SpeCalendar;
import clb.core.supplier.dto.SpeCalendarLine;
import clb.core.supplier.exceptions.SpeException;
import clb.core.supplier.service.ISpeCalendarLineService;
import clb.core.supplier.service.ISpeCalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping(value = "/supplier/calendarLine")
public class SpeCalendarLineController extends BaseController{

    @Autowired
    private ISpeCalendarLineService service;


    @RequestMapping(value = "/query")
    @ResponseBody
    public ResponseData query(SpeCalendarLine dto) throws SpeException {
        return new ResponseData(service.getCalendarData(dto));
    }
}