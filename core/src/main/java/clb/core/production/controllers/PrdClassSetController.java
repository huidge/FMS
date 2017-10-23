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

import clb.core.production.dto.PrdClassSet;
import clb.core.production.service.IPrdClassSetService;

/**
 * Created by wanjun.feng on 17/4/12.
 */
@Controller
@RequestMapping("/production/classSet")
public class PrdClassSetController extends BaseController {
    
    @Autowired
    private IPrdClassSetService prdClassSetService;

    /**
     * 查询产品分类集
     * @param request
     * @return
     */
    @RequestMapping(value = "/query")
    @ResponseBody
    public ResponseData query(PrdClassSet prdClassSet, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        List<PrdClassSet> prdClassSetList = new ArrayList<>();
        prdClassSetList = prdClassSetService.query(iRequest, prdClassSet, page, pagesize);
        return new ResponseData(prdClassSetList);
    }
    
    /**
     * 保存产品分类集
     * @param request
     * @return
     * @throws TokenException 
     */
    @RequestMapping(value = "/submit", method = {RequestMethod.POST})
    public ResponseData submit(@RequestBody List<PrdClassSet> prdClassSetList, BindingResult result, HttpServletRequest request) throws TokenException {
        checkToken(request, prdClassSetList);
        getValidator().validate(prdClassSetList, result);
        if (result.hasErrors()) {
          ResponseData rd = new ResponseData(false);
          rd.setMessage(getErrorMessage(result, request));
          return rd;
        }
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(prdClassSetService.batchUpdate(iRequest, prdClassSetList));
    }
    
    @RequestMapping(value = "/remove")
    public ResponseData delete(HttpServletRequest request, @RequestBody List<PrdClassSet> prdClassSetList) throws TokenException{
        checkToken(request, prdClassSetList);
        prdClassSetService.batchDelete(prdClassSetList);
        return new ResponseData();
    }
}