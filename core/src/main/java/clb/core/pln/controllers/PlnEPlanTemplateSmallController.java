package clb.core.pln.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;

import clb.core.pln.dto.PlnEPlanTemplateBig;
import clb.core.pln.dto.PlnEPlanTemplateDetail;
import clb.core.pln.dto.PlnEPlanTemplateSmall;
import clb.core.pln.service.IPlnEPlanTemplateDetailService;
import clb.core.pln.service.IPlnEPlanTemplateSmallService;

    @Controller
    public class PlnEPlanTemplateSmallController extends BaseController{

    @Autowired
    private IPlnEPlanTemplateSmallService service;
    
    @Autowired
    private IPlnEPlanTemplateDetailService plnEPlanTemplateDetailService;


    @RequestMapping(value = "/fms/pln/e/plan/template/small/query")
    @ResponseBody
    public ResponseData query(PlnEPlanTemplateSmall dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        ResponseData responseData = new ResponseData();
        List<PlnEPlanTemplateSmall> list = new ArrayList<>();
        if (dto.getBigId() == null) {
        	responseData.setRows(list);
        	return responseData;
		}
        list = service.queryList(requestContext,dto,page,pageSize);
        responseData.setTotal(service.selectCount(requestContext,dto));
        responseData.setRows(list);
        return responseData;
    }

    @RequestMapping(value = "/fms/pln/e/plan/template/small/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<PlnEPlanTemplateSmall> dto){
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }
   
    @RequestMapping(value = "/fms/pln/e/plan/template/small/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<PlnEPlanTemplateSmall> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
    
    @RequestMapping(value = "/fms/pln/e/plan/template/small/updateBySmallId")
    @ResponseBody
    public ResponseData updateBySmallId(HttpServletRequest request,@RequestBody List<PlnEPlanTemplateSmall> dto){
    	IRequest requestCtx = createRequestContext(request);
    	ResponseData responseData = new ResponseData();
    	List<PlnEPlanTemplateSmall> list = new ArrayList<>();
    	
    	PlnEPlanTemplateSmall ePlanTemplateSmall = dto.get(0);
    	//修改的时候  判断 有没有该序号
    	Long seqNum = ePlanTemplateSmall.getSeqNum();
    	Long bigId = ePlanTemplateSmall.getBigId();
    	Long smallId = ePlanTemplateSmall.getSmallId();
    	PlnEPlanTemplateSmall small = new PlnEPlanTemplateSmall();
    	small.setSeqNum(seqNum);
    	small.setBigId(bigId);
    	List<PlnEPlanTemplateSmall> select = service.queryList(requestCtx, small, 1, 2);
    	if (select != null && select.size() == 1) {
			Long smallId2 = select.get(0).getSmallId();
			if (smallId.longValue() != smallId2.longValue()) {
				responseData.setSuccess(false);
	    		responseData.setMessage("序号重复了!");
	    		list.add(ePlanTemplateSmall);
	    		responseData.setRows(list);
	        	return responseData;
			}
		}else if(select != null && select.size() > 1){
			responseData.setSuccess(false);
    		responseData.setMessage("序号重复了!");
    		list.add(ePlanTemplateSmall);
    		responseData.setRows(list);
        	return responseData;
		}
    	
    	PlnEPlanTemplateSmall plnEPlanTemplateSmall = service.updateByPrimaryKey(requestCtx, ePlanTemplateSmall);
		list.add(plnEPlanTemplateSmall);
		responseData.setRows(list);
		return responseData;
    }
    
    @RequestMapping(value = "/fms/pln/e/plan/template/small/add")
    @ResponseBody
    public ResponseData add(HttpServletRequest request,@RequestBody List<PlnEPlanTemplateSmall> dto){
    	IRequest requestCtx = createRequestContext(request);
    	ResponseData responseData = new ResponseData();
    	List<PlnEPlanTemplateSmall> list = new ArrayList<>();
    	//添加数据之前 先判断  该序号是否存在
    	PlnEPlanTemplateSmall ePlanTemplateSmall = dto.get(0);
    	Long bigId = ePlanTemplateSmall.getBigId();
    	Long seqNum = ePlanTemplateSmall.getSeqNum();
    	PlnEPlanTemplateSmall small = new PlnEPlanTemplateSmall();
    	small.setSeqNum(seqNum);
    	small.setBigId(bigId);
    	List<PlnEPlanTemplateSmall> select = service.select(requestCtx, small, 1, 1);
    	if (select != null && select.size() > 0) {
    		responseData.setSuccess(false);
    		responseData.setMessage("序号重复了!");
    		list.add(ePlanTemplateSmall);
    		responseData.setRows(list);
        	return responseData;
		}
    	
    	PlnEPlanTemplateSmall plnEPlanTemplateSmall = service.insertSelective(requestCtx, ePlanTemplateSmall);
		list.add(plnEPlanTemplateSmall);
		responseData.setRows(list);
		return responseData;
    }
    /**
     * 根据主键删除记录  及其关联记录
     * @param request
     * @param dto
     * @return
     */
    @RequestMapping(value = "/fms/pln/e/plan/template/small/deleteById")
    @ResponseBody
    public ResponseData deleteById(HttpServletRequest request,PlnEPlanTemplateSmall dto){
    	Long smallId = dto.getSmallId();
    	PlnEPlanTemplateDetail plnEPlanTemplateDetail = new PlnEPlanTemplateDetail();
    	plnEPlanTemplateDetail.setSmallId(smallId);
    	
    	List<PlnEPlanTemplateDetail> list = plnEPlanTemplateDetailService.selectByCondition(plnEPlanTemplateDetail);
    	
    	if (list != null) {
    		plnEPlanTemplateDetailService.batchDelete(list);
		}
    	
    	service.deleteByPrimaryKey(dto);
    	return new ResponseData();
    }
    @RequestMapping(value = "/fms/pln/e/plan/template/small/queryById")
    @ResponseBody
    public ResponseData queryById(PlnEPlanTemplateSmall dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        PlnEPlanTemplateSmall primaryKey = service.selectByPrimaryKey(requestContext, dto);
       List<PlnEPlanTemplateSmall> list = new ArrayList<>();
       list.add(primaryKey);
        return new ResponseData(list);
    }

    }