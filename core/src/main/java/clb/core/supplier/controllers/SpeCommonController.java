package clb.core.supplier.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.supplier.dto.SpeContract;
import clb.core.supplier.service.ISpeCommonService;
import clb.core.supplier.service.ISpeContractService;
import clb.core.system.dto.ClbCode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping(value = "/spe/Common")
public class SpeCommonController extends BaseController{
    
    @Autowired
    private ISpeCommonService commonService;
    
    @RequestMapping(value = "/getCodeValuesByParentId")
    @ResponseBody
    public ResponseData getCodeValuesByParentId(ClbCode dto,String parentValue,HttpServletRequest request){
        IRequest iRequest = createRequestContext(request);
    	return new ResponseData(commonService.getCodeValuesByParentId(iRequest,dto, parentValue));
    }
}