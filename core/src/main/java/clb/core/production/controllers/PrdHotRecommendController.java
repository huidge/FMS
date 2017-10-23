package clb.core.production.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.production.dto.PrdHotRecommend;
import clb.core.production.service.IPrdHotRecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

    @Controller
    public class PrdHotRecommendController extends BaseController{

    @Autowired
    private IPrdHotRecommendService service;


    @RequestMapping(value = "/fms/prd/hot/recommend/query")
    @ResponseBody
    public ResponseData query(PrdHotRecommend dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        String productType = request.getParameter("productType");
        return new ResponseData(service.selectInfoByType(requestContext,productType,page,pageSize));
    }

    @RequestMapping(value = "/fms/prd/hot/recommend/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<PrdHotRecommend> dto){
    	String productType = request.getParameter("productType");
    	for (PrdHotRecommend prdHotRecommend : dto) {
    		prdHotRecommend.setProductType(productType);
		}
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/fms/prd/hot/recommend/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<PrdHotRecommend> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
    }