package clb.core.channel.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;

import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.exception.TokenException;
import com.hand.hap.system.dto.ResponseData;
import clb.core.channel.dto.CnlPaymentTerm;
import clb.core.channel.service.ICnlPaymentTermService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
/**
 * 付款条件controller
 * Created by wanjun.feng on 2017/4/19.
 */
@Controller
@RequestMapping(value = "/fms/cnl/payment/term")
public class CnlPaymentTermController extends BaseController{

    @Autowired
    private ICnlPaymentTermService cnlPaymentTermService;

    @RequestMapping(value = "/query")
    @ResponseBody
    public ResponseData query(CnlPaymentTerm cnlPaymentTerm, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(cnlPaymentTermService.query(requestContext, cnlPaymentTerm, page, pageSize));
    }

    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    public ResponseData update(@RequestBody List<CnlPaymentTerm> cnlPaymentTermList, BindingResult result, HttpServletRequest request) throws TokenException {
        checkToken(request, cnlPaymentTermList);
        getValidator().validate(cnlPaymentTermList, result);
        if (result.hasErrors()) {
            ResponseData rd = new ResponseData(false);
            rd.setMessage(getErrorMessage(result, request));
            return rd;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(cnlPaymentTermService.paymentTermBatchUpdate(requestCtx, cnlPaymentTermList));
    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public ResponseData delete(@RequestBody List<CnlPaymentTerm> cnlPaymentTermList, HttpServletRequest request) throws TokenException {
        checkToken(request, cnlPaymentTermList);
        cnlPaymentTermService.batchDelete(cnlPaymentTermList);
        return new ResponseData();
    }
}