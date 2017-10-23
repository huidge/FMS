package clb.core.production.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.exception.TokenException;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;

import clb.core.production.dto.PrdDiscount;
import clb.core.production.service.IPrdDiscountService;
/**
 * Created by wanjun.feng on 17/4/18.
 */
@Controller
@RequestMapping("/production/discount")
public class PrdDiscountController extends BaseController {
    
    @Autowired
    private IPrdDiscountService prdDiscountService;
    
    /**
     * 查询产品优惠信息
     * @param request
     * @return
     */
    @RequestMapping(value = "/query")
    @ResponseBody
    public ResponseData query(PrdDiscount prdDiscount, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        List<PrdDiscount> prdDiscountList = new ArrayList<>();
        prdDiscountList = prdDiscountService.query(iRequest, prdDiscount, page, pagesize);
        return new ResponseData(prdDiscountList);
    }
    
    /**
     * 保存产品优惠信息
     * @param request
     * @return
     * @throws TokenException 
     */
    @RequestMapping(value = "/submit", method = {RequestMethod.POST})
    public ResponseData submit(@RequestBody List<PrdDiscount> prdDiscountList, BindingResult result, HttpServletRequest request) throws TokenException {
        checkToken(request, prdDiscountList);
        getValidator().validate(prdDiscountList, result);
        if (result.hasErrors()) {
          ResponseData rd = new ResponseData(false);
          rd.setMessage(getErrorMessage(result, request));
          return rd;
        }
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(prdDiscountService.batchUpdate(iRequest, prdDiscountList));
    }
    
    @RequestMapping(value = "/remove")
    public ResponseData delete(HttpServletRequest request, @RequestBody List<PrdDiscount> prdDiscountList) throws TokenException{
        checkToken(request, prdDiscountList);
        prdDiscountService.batchDelete(prdDiscountList);
        return new ResponseData();
    }
}