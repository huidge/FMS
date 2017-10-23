package clb.core.production.controllers;

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
import com.hand.hap.core.exception.BaseException;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;

import clb.core.production.dto.PrdPremiumAdjust;
import clb.core.production.service.IPrdPremiumAdjustService;
/*****
 * @author tiansheng.ye
 * @Date 2017/07/21
 */
@Controller
@RequestMapping(value={"/production/prdPremiumAdjust/"})
public class PrdPremiumAdjustController extends BaseController{
	@Autowired
    private IPrdPremiumAdjustService prdPremiumAdjustService;
	
	@RequestMapping(value="query")
	public ResponseData query(PrdPremiumAdjust dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request){
		IRequest requestContext = createRequestContext(request);
        return new ResponseData(prdPremiumAdjustService.select(requestContext, dto, page, pageSize));
	}
	
	@RequestMapping(value = {"submit"}, method = {RequestMethod.POST})
    @ResponseBody
    public ResponseData submit(HttpServletRequest request, @RequestBody List<PrdPremiumAdjust> dtoList, BindingResult result)  {
        getValidator().validate(dtoList, result);
        if (result.hasErrors()) {
          ResponseData rd = new ResponseData(false);
          rd.setMessage(getErrorMessage(result, request));
          return rd;
        }
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(prdPremiumAdjustService.batchUpdate(iRequest, dtoList));
    }
	
	@RequestMapping(value = "remove", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData deleteRole(@RequestBody List<PrdPremiumAdjust> prdPremiums, BindingResult result, HttpServletRequest request)
            throws BaseException {
		prdPremiumAdjustService.batchDelete(prdPremiums);
        return new ResponseData();
    }
	

}
