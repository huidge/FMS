package clb.core.prc.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;

import clb.core.pln.dto.PlnPlanLibrary;
import clb.core.prc.dto.PrcRadarLineDetail;
import clb.core.prc.service.IPrcRadarLineDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

    @Controller
    public class PrcRadarLineDetailController extends BaseController{

    @Autowired
    private IPrcRadarLineDetailService prcRadarLineDetailService;


    @RequestMapping(value = "/fms/prc/radar/line/detail/query")
    @ResponseBody
    public ResponseData query(PrcRadarLineDetail dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        String lineId = request.getParameter("lineId");
        return new ResponseData(prcRadarLineDetailService.selectByLineId(requestContext,lineId,page,pageSize));
    }

    @RequestMapping(value = "/fms/prc/radar/line/detail/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<PrcRadarLineDetail> dto){
        IRequest requestCtx = createRequestContext(request);
        String lineId = request.getParameter("lineId");
        for (PrcRadarLineDetail prcRadarLineDetail : dto) {
        	prcRadarLineDetail.setLineId(Long.valueOf(lineId));
		}
        return new ResponseData(prcRadarLineDetailService.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/fms/prc/radar/line/detail/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<PrcRadarLineDetail> dto){
    	prcRadarLineDetailService.batchDelete(dto);
        return new ResponseData();
    }
    
    @RequestMapping(value = "/fms/prc/radar/line/detail/selectAttSetInfo" )
    @ResponseBody
    public ResponseData selectAttSetInfo(HttpServletRequest request, PrcRadarLineDetail dto){
    	return new ResponseData(prcRadarLineDetailService.selectAttSetInfo(dto));
    }
    }