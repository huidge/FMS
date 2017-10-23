package clb.core.prc.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.prc.dto.PrcAttributeSet;
import clb.core.prc.service.IPrcAttributeSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

    @Controller
    public class PrcAttributeSetController extends BaseController{

    @Autowired
    private IPrcAttributeSetService service;


    @RequestMapping(value = "/fms/prc/attribute/set/query")
    @ResponseBody
    public ResponseData query(PrcAttributeSet dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectByCondition(requestContext,dto,page,pageSize));
    }

    @RequestMapping(value = "/fms/prc/attribute/set/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<PrcAttributeSet> dto){
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/fms/prc/attribute/set/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<PrcAttributeSet> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
    
	@RequestMapping(value = "/fms/prc/attribute/set/queryByHeaderId", method = RequestMethod.POST)
	@ResponseBody
	public ResponseData queryByHeaderId(HttpServletRequest request) {
		IRequest iRequest = createRequestContext(request);
		ResponseData responseData = new ResponseData();
	    String attSetId = request.getParameter("attSetId");
	    List<PrcAttributeSet> prcAttributeSetList = new ArrayList<PrcAttributeSet>();
	    PrcAttributeSet prcAttributeSet = new PrcAttributeSet();
	    prcAttributeSet.setAttSetId(Long.valueOf(attSetId));
	    prcAttributeSetList.add(service.selectByPrimaryKey(iRequest, prcAttributeSet));
	    responseData.setRows(prcAttributeSetList);
		return responseData;
	}
	
	@RequestMapping(value = "/fms/prc/attribute/set/querySetName")
    @ResponseBody
    public ResponseData querySetName(HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectSetName());
    }
	
	/**
	 * form提交
	 * @param request
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "/fms/prc/attribute/set/submitOnForm")
    @ResponseBody
    public ResponseData updateOnForm(HttpServletRequest request,@RequestBody List<PrcAttributeSet> dto){
        IRequest requestCtx = createRequestContext(request);
        service.updateByPrimaryKeySelective(requestCtx, dto.get(0));
        return new ResponseData(dto);
    }
    }