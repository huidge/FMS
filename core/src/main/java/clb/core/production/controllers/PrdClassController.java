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

import clb.core.production.dto.PrdClass;
import clb.core.production.service.IPrdClassService;

/**
 * Created by wanjun.feng on 17/4/12.
 */
@Controller
@RequestMapping("/production/class")
public class PrdClassController extends BaseController {
    
    @Autowired
    private IPrdClassService prdClassService;
    
    /**
     * 查询产品分类
     * @param request
     * @return
     */
    @RequestMapping(value = "/query")
    @ResponseBody
    public ResponseData query(PrdClass prdClass, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        List<PrdClass> prdClassList = new ArrayList<>();
        prdClassList = prdClassService.query(iRequest, prdClass, page, pagesize);
        return new ResponseData(prdClassList);
    }
    
    /**
     * 保存产品分类
     * @param request
     * @return
     * @throws TokenException 
     */
    @RequestMapping(value = "/submit", method = {RequestMethod.POST})
    public ResponseData submit(@RequestBody List<PrdClass> prdClassList, BindingResult result, HttpServletRequest request) throws TokenException {
        checkToken(request, prdClassList);
        getValidator().validate(prdClassList, result);
        if (result.hasErrors()) {
          ResponseData rd = new ResponseData(false);
          rd.setMessage(getErrorMessage(result, request));
          return rd;
        }
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(prdClassService.batchUpdate(iRequest, prdClassList));
    }
    
    @RequestMapping(value = "/remove")
    public ResponseData delete(@RequestBody List<PrdClass> prdClassList, HttpServletRequest request) throws TokenException{
        checkToken(request, prdClassList);
        prdClassService.batchDelete(prdClassList);
        return new ResponseData();
    }
}