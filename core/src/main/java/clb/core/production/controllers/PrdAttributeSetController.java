package clb.core.production.controllers;

/**
 * Created by jiaolong.li on 2017-04-13.
 */

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.production.dto.PrdAttributeSet;
import clb.core.production.service.IPrdAttributeSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping({"/production/attributeSet"})
public class PrdAttributeSetController extends BaseController {

    @Autowired
    private IPrdAttributeSetService service;

    @RequestMapping(value = "/query")
    @ResponseBody
    public ResponseData query(PrdAttributeSet dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        //return new ResponseData(service.select(requestContext,dto,page,pageSize));
        return new ResponseData(service.selectAllFields(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request, @RequestBody List<PrdAttributeSet> dto) {
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<PrdAttributeSet> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }
}