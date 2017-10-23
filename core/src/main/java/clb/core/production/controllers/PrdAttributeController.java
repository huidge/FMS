package clb.core.production.controllers;

/**
 * Created by jiaolong.li on 2017-04-13.
 */

import clb.core.commission.dto.CmnSpeCmnCalRec;
import clb.core.system.dto.ClbUser;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.production.dto.PrdAttribute;
import clb.core.production.service.IPrdAttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping({"/production/attribute"})
public class PrdAttributeController extends BaseController {

    @Autowired
    private IPrdAttributeService service;

    @RequestMapping(value = "/query")
    @ResponseBody
    public ResponseData query(PrdAttribute dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        //return new ResponseData(service.select(requestContext,dto,page,pageSize));
        return new ResponseData(service.selectAllFields(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request, @RequestBody List<PrdAttribute> dto) {
        IRequest requestCtx = createRequestContext(request);

        for (PrdAttribute prdAttribute : dto){
            // 清空LOV值操作
            if ("".equals(prdAttribute.getValueSetName()) || prdAttribute.getValueSetName() == null ){
                PrdAttribute tmp = new PrdAttribute();
                tmp.setAttId(prdAttribute.getAttId());
                tmp = service.selectByPrimaryKey(requestCtx, tmp);
                tmp.setValueSetId(null);
                service.updateByPrimaryKey(requestCtx, tmp);
            }
        }

        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<PrdAttribute> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }

    @RequestMapping(value = "/queryDefaultValue")
    @ResponseBody
    public String queryDefaultValue(String codeId, String meaning){
        return service.queryDefaultValue(codeId, meaning);
    }
}