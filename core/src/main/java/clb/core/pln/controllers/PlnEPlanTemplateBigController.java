package clb.core.pln.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.annotation.AuditEntry;
import com.hand.hap.system.dto.ResponseData;
import clb.core.pln.dto.PlnEPlanTemplateBig;
import clb.core.pln.dto.PlnEPlanTemplateDetail;
import clb.core.pln.dto.PlnEPlanTemplateSmall;
import clb.core.pln.service.IPlnEPlanTemplateBigService;
import clb.core.pln.service.IPlnEPlanTemplateDetailService;
import clb.core.pln.service.IPlnEPlanTemplateSmallService;

import org.drools.lang.DRLExpressions.neg_operator_key_return;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

@Controller
public class PlnEPlanTemplateBigController extends BaseController {

	@Autowired
	private IPlnEPlanTemplateBigService service;

	@Autowired
	private IPlnEPlanTemplateDetailService plnEPlanTemplateDetailService;

	@Autowired
	private IPlnEPlanTemplateSmallService plnEPlanTemplateSmallService;

	@RequestMapping(value = "/fms/pln/e/plan/template/big/query")
	@ResponseBody
	public ResponseData query(PlnEPlanTemplateBig dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		ResponseData responseData = new ResponseData();
		List<PlnEPlanTemplateBig> list = new ArrayList<>();
		if (dto.getTemplateId() == null) {
			responseData.setRows(list);
			return responseData;
		}
		list = service.queryList(requestContext, dto, page, pageSize);
		responseData.setTotal(service.selectCount(requestContext,dto));
		responseData.setRows(list);
		return responseData;
	}

	@RequestMapping(value = "/fms/pln/e/plan/template/big/submit")
	@ResponseBody
	public ResponseData update(HttpServletRequest request, @RequestBody List<PlnEPlanTemplateBig> dto) {
		IRequest requestCtx = createRequestContext(request);
		return new ResponseData(service.batchUpdate(requestCtx, dto));
	}

	@RequestMapping(value = "/fms/pln/e/plan/template/big/remove")
	@ResponseBody
	public ResponseData delete(HttpServletRequest request, @RequestBody List<PlnEPlanTemplateBig> dto) {
		service.batchDelete(dto);
		return new ResponseData();
	}

	/**
	 * 根据主键删除该条记录 及其关联的记录
	 * 
	 * @param request
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "/fms/pln/e/plan/template/big/deleteById")
	@ResponseBody
	public ResponseData deleteById(HttpServletRequest request, PlnEPlanTemplateBig dto) {
		Long bigId = dto.getBigId();
		PlnEPlanTemplateSmall plnEPlanTemplateSmall = new PlnEPlanTemplateSmall();
		plnEPlanTemplateSmall.setBigId(bigId);

		List<PlnEPlanTemplateSmall> list = new ArrayList<>();
		list.add(plnEPlanTemplateSmall);
		// 通过bigId查询 所有的smallId 好封装的方法 带了分页 不适用
		List<PlnEPlanTemplateSmall> resultList = plnEPlanTemplateSmallService.selectByCondition(plnEPlanTemplateSmall);
		if (resultList != null) {

			for (PlnEPlanTemplateSmall plnEPlanTemplateSmall2 : resultList) {
				Long smallId = plnEPlanTemplateSmall2.getSmallId();

				PlnEPlanTemplateDetail plnEPlanTemplateDetail = new PlnEPlanTemplateDetail();

				plnEPlanTemplateDetail.setSmallId(smallId);

				List<PlnEPlanTemplateDetail> resultList2 = plnEPlanTemplateDetailService
						.selectByCondition(plnEPlanTemplateDetail);

				plnEPlanTemplateDetailService.batchDelete(resultList2);
			}
			plnEPlanTemplateSmallService.batchDelete(resultList);
		}

		service.deleteByPrimaryKey(dto);

		return new ResponseData();
	}

	@RequestMapping(value = "/fms/pln/e/plan/template/big/updateByBigId")
	@ResponseBody
	public ResponseData updateByBigId(HttpServletRequest request, @RequestBody List<PlnEPlanTemplateBig> dto) {
		IRequest requestCtx = createRequestContext(request);
		ResponseData responseData = new ResponseData();
		List<PlnEPlanTemplateBig> list = new ArrayList<>();

		PlnEPlanTemplateBig ePlanTemplatebig = dto.get(0);
		// 修改的时候 判断 有没有该序号
		Long seqNum = ePlanTemplatebig.getSeqNum();
		Long bigId = ePlanTemplatebig.getBigId();
		Long templateId = ePlanTemplatebig.getTemplateId();

		PlnEPlanTemplateBig big = new PlnEPlanTemplateBig();
		big.setSeqNum(seqNum);
		big.setTemplateId(templateId);
		List<PlnEPlanTemplateBig> select = service.queryList(requestCtx, big, 1, 2);
		if (select != null && select.size() == 1) {
			Long bigId2 = select.get(0).getBigId();
			if (bigId.longValue() != bigId2.longValue()) {
				responseData.setSuccess(false);
				responseData.setMessage("序号重复了!");
				list.add(ePlanTemplatebig);
				responseData.setRows(list);
				return responseData;
			}
		} else if (select != null && select.size() > 1) {
			responseData.setSuccess(false);
			responseData.setMessage("序号重复了!");
			list.add(ePlanTemplatebig);
			responseData.setRows(list);
			return responseData;
			
		}

		PlnEPlanTemplateBig plnEPlanTemplateBig = service.updateByPrimaryKey(requestCtx, ePlanTemplatebig);
		list.add(plnEPlanTemplateBig);
		responseData.setRows(list);
		return responseData;
	}

	@RequestMapping(value = "/fms/pln/e/plan/template/big/add")
	@ResponseBody
	public ResponseData add(HttpServletRequest request, @RequestBody List<PlnEPlanTemplateBig> dto) {
		IRequest requestCtx = createRequestContext(request);
		ResponseData responseData = new ResponseData();
		List<PlnEPlanTemplateBig> list = new ArrayList<>();
		// 添加数据之前 先判断 该序号是否存在
		PlnEPlanTemplateBig ePlanTemplateBig = dto.get(0);
		Long seqNum = ePlanTemplateBig.getSeqNum();
		Long templateId = ePlanTemplateBig.getTemplateId();
		
		
		PlnEPlanTemplateBig big = new PlnEPlanTemplateBig();
		big.setSeqNum(seqNum);
		big.setTemplateId(templateId);
		
		
		List<PlnEPlanTemplateBig> select = service.select(requestCtx, big, 1, 1);
		if (select != null && select.size() > 0) {
			responseData.setSuccess(false);
			responseData.setMessage("序号重复了!");
			list.add(ePlanTemplateBig);
			responseData.setRows(list);
			return responseData;
		}

		PlnEPlanTemplateBig plnEPlanTemplateBig = service.insertSelective(requestCtx, ePlanTemplateBig);
		list.add(plnEPlanTemplateBig);
		responseData.setRows(list);
		return responseData;
	}
	@RequestMapping(value = "/fms/pln/e/plan/template/big/queryById")
    @ResponseBody
    public ResponseData queryById(PlnEPlanTemplateBig dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
       PlnEPlanTemplateBig primaryKey = service.selectByPrimaryKey(requestContext, dto);
       List<PlnEPlanTemplateBig> list = new ArrayList<>();
       list.add(primaryKey);
        return new ResponseData(list);
    }
}