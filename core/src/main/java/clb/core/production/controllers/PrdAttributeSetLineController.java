package clb.core.production.controllers;
/**
 * Created by jiaolong.li on 2017-04-18.
 */
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.production.dto.PrdAttributeSetLine;
import clb.core.production.service.IPrdAttributeSetLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping({"/production/attributeSetLine"})
public class PrdAttributeSetLineController extends BaseController {

    @Autowired
    private IPrdAttributeSetLineService service;

    @RequestMapping(value = "/query")
    @ResponseBody
    public ResponseData query(PrdAttributeSetLine dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        //return new ResponseData(service.select(requestContext, dto, page, pageSize));
        return new ResponseData(service.selectAllFields(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request, @RequestBody List<PrdAttributeSetLine> dto) {
        IRequest requestCtx = createRequestContext(request);

        List<PrdAttributeSetLine> processDto = new ArrayList<PrdAttributeSetLine>();
        for (PrdAttributeSetLine line : dto){
            if (line.getCompareRule() == null ){
                line.setCompareRule("");
            }
            processDto.add(line);
        }
        return new ResponseData(service.batchUpdate(requestCtx, processDto));
    }

    @RequestMapping(value = "/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<PrdAttributeSetLine> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }
}