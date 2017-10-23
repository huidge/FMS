package clb.core.prc.controllers;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;

import clb.core.prc.dto.PrcItemScore;
import clb.core.prc.service.IPrcItemScoreService;

    @Controller
    public class PrcItemScoreController extends BaseController{

    @Autowired
    private IPrcItemScoreService prcItemScoreService;


    @RequestMapping(value = "/fms/prc/item/score/query")
    @ResponseBody
    public ResponseData query(HttpServletRequest request) {
    	 IRequest requestCtx = createRequestContext(request);
    	String attSetId = request.getParameter("attSetId");
        return new ResponseData(prcItemScoreService.selectItemScoreInfo(Long.valueOf(attSetId),requestCtx));
    }

    @RequestMapping(value = "/fms/prc/item/score/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<Map<String,String>> dto){
        IRequest requestCtx = createRequestContext(request);
        String attSetId = request.getParameter("attSetId");
        for (Map<String, String> map : dto) {
        	prcItemScoreService.updateScoreItem(map,Long.valueOf(attSetId),requestCtx);
		}
        return new ResponseData(dto);
    }

    @RequestMapping(value = "/fms/prc/item/score/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<PrcItemScore> dto){
    	prcItemScoreService.batchDelete(dto);
        return new ResponseData();
    }
    
    @RequestMapping(value = "/fms/prc/item/score/queryColumns")
    @ResponseBody
    public ResponseData queryColumns(HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        String attSetId = request.getParameter("attSetId");
        return new ResponseData(prcItemScoreService.queryColumns(Long.valueOf(attSetId)));
    }
    }