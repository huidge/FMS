package clb.core.prc.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;

import clb.core.prc.dto.PrcRadarLine;
import clb.core.prc.service.IPrcRadarLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

    @Controller
    public class PrcRadarLineController extends BaseController{

    @Autowired
    private IPrcRadarLineService prcRadarLineService;
    
    @RequestMapping(value = "/fms/prc/radar/line/query")
    @ResponseBody
    public ResponseData query(PrcRadarLine dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        String attSetId = request.getParameter("attSetId");
        return new ResponseData(prcRadarLineService.selectByAttSetID(requestContext,attSetId,page,pageSize));
    }

    @RequestMapping(value = "/fms/prc/radar/line/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<PrcRadarLine> dto){
        IRequest requestCtx = createRequestContext(request);
        String attSetId = request.getParameter("attSetId");
        for (PrcRadarLine prcRadarLine : dto) {
        	prcRadarLine.setAttSetId(Long.valueOf(attSetId));
		}
        return new ResponseData(prcRadarLineService.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/fms/prc/radar/line/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<PrcRadarLine> dto){
    	prcRadarLineService.batchDelete(dto);
        return new ResponseData();
    }
    
    @RequestMapping(value = "/fms/prc/radar/line/queryByLineId")
    @ResponseBody
    public ResponseData queryByHeaderId(HttpServletRequest request) {
    	IRequest iRequest = createRequestContext(request);
		ResponseData responseData = new ResponseData();
	    String lineId = request.getParameter("lineId");
	    List<PrcRadarLine> prcAttributeSetList = new ArrayList<PrcRadarLine>();
	    PrcRadarLine prcRadarLine = new PrcRadarLine();
	    prcRadarLine.setLineId(Long.valueOf(lineId));
	    prcAttributeSetList.add(prcRadarLineService.selectByPrimaryKey(iRequest, prcRadarLine));
	    responseData.setRows(prcAttributeSetList);
		return responseData;
    }
    
    /**
     * 行信息form提交
     * @param request
     * @param dto
     * @return
     */
    @RequestMapping(value = "/fms/prc/radar/line/submitRadarLineForm")
    @ResponseBody
    public ResponseData updateRadarLineForm(HttpServletRequest request,@RequestBody List<PrcRadarLine> dto){
        IRequest requestCtx = createRequestContext(request);
        prcRadarLineService.updateByPrimaryKey(requestCtx, dto.get(0));
        return new ResponseData(dto);
    }
    
    }