package clb.core.supplier.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.supplier.dto.SpeSupplierSettle;
import clb.core.supplier.service.ISpeSupplierSettleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/supplier/settle")
public class SpeSupplierSettleController extends BaseController{

    @Autowired
    private ISpeSupplierSettleService service;


    @RequestMapping(value = "/query")
    @ResponseBody
    public ResponseData query(SpeSupplierSettle dto) {
        return new ResponseData(service.selectData(dto));
    }

    @RequestMapping(value = "/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<SpeSupplierSettle> dto){
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<SpeSupplierSettle> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
}