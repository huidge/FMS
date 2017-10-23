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

import clb.core.pln.dto.PlnEPlanTemplateDetail;
import clb.core.pln.dto.PlnEPlanTemplateSmall;
import clb.core.pln.service.IPlnEPlanTemplateDetailService;

@Controller
public class PlnEPlanTemplateDetailController extends BaseController {

	@Autowired
	private IPlnEPlanTemplateDetailService service;

	@RequestMapping(value = "/fms/pln/e/plan/template/detail/query")
	@ResponseBody
	public ResponseData query(PlnEPlanTemplateDetail dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		ResponseData responseData = new ResponseData();
		List<PlnEPlanTemplateDetail> list = new ArrayList<>();
		if (dto.getSmallId() == null) {
			responseData.setRows(list);
			return responseData;
		}
		list = service.queryList(requestContext, dto, page, pageSize);
		responseData.setTotal(service.selectCount(requestContext,dto));
		responseData.setRows(list);
		return responseData;
	}

	@RequestMapping(value = "/fms/pln/e/plan/template/detail/submit")
	@ResponseBody
	public ResponseData update(HttpServletRequest request, @RequestBody List<PlnEPlanTemplateDetail> dto) {
		IRequest requestCtx = createRequestContext(request);
		return new ResponseData(service.batchUpdate(requestCtx, dto));
	}

	@RequestMapping(value = "/fms/pln/e/plan/template/detail/remove")
	@ResponseBody
	public ResponseData delete(HttpServletRequest request, @RequestBody List<PlnEPlanTemplateDetail> dto) {
		service.batchDelete(dto);
		return new ResponseData();
	}

	@RequestMapping(value = "/fms/pln/e/plan/template/detail/updateByDetailId")
	@ResponseBody
	public ResponseData updateBySmallId(HttpServletRequest request, @RequestBody List<PlnEPlanTemplateDetail> dto) {
		IRequest requestCtx = createRequestContext(request);
		ResponseData responseData = new ResponseData();
		List<PlnEPlanTemplateDetail> list = new ArrayList<>();

		PlnEPlanTemplateDetail plnEPlanTemplateDetail = dto.get(0);
		Long seqNum = plnEPlanTemplateDetail.getSeqNum();
		Long smallId = plnEPlanTemplateDetail.getSmallId();
		Long detailId = plnEPlanTemplateDetail.getDetailId();

		PlnEPlanTemplateDetail detail = new PlnEPlanTemplateDetail();
		detail.setSeqNum(seqNum);
		detail.setSmallId(smallId);

		List<PlnEPlanTemplateDetail> select = service.queryList(requestCtx, detail, 1, 2);
		if (select != null && select.size() == 1) {
			Long detailId2 = select.get(0).getDetailId();
			if (detailId.longValue() != detailId2.longValue()) {
				responseData.setSuccess(false);
				responseData.setMessage("序号重复了!");
				list.add(plnEPlanTemplateDetail);
				responseData.setRows(list);
				return responseData;
			}
		} else if (select != null && select.size() > 1) {
			responseData.setSuccess(false);
			responseData.setMessage("序号重复了!");
			list.add(plnEPlanTemplateDetail);
			responseData.setRows(list);
			return responseData;
		}

		PlnEPlanTemplateDetail plnEPlanTemplateDetail1 = service.updateByPrimaryKey(requestCtx, plnEPlanTemplateDetail);
		list.add(plnEPlanTemplateDetail1);
		responseData.setRows(list);
		return responseData;
	}

	@RequestMapping(value = "/fms/pln/e/plan/template/detail/add")
	@ResponseBody
	public ResponseData add(HttpServletRequest request, @RequestBody List<PlnEPlanTemplateDetail> dto) {
		IRequest requestCtx = createRequestContext(request);
		ResponseData responseData = new ResponseData();
		List<PlnEPlanTemplateDetail> list = new ArrayList<>();

		PlnEPlanTemplateDetail plnEPlanTemplateDetail = dto.get(0);
		Long seqNum = plnEPlanTemplateDetail.getSeqNum();
		Long smallId = plnEPlanTemplateDetail.getSmallId();
		
		PlnEPlanTemplateDetail detail = new PlnEPlanTemplateDetail();
		detail.setSeqNum(seqNum);
		detail.setSmallId(smallId);

		List<PlnEPlanTemplateDetail> select = service.select(requestCtx, detail, 1, 1);
		if (select != null && select.size() > 0) {
			responseData.setSuccess(false);
			responseData.setMessage("序号重复了!");
			list.add(plnEPlanTemplateDetail);
			responseData.setRows(list);
			return responseData;
		}

		PlnEPlanTemplateDetail plnEPlanTemplateDetail1 = service.insertSelective(requestCtx, plnEPlanTemplateDetail);
		list.add(plnEPlanTemplateDetail1);
		responseData.setRows(list);
		return responseData;
	}

	@RequestMapping(value = "/fms/pln/e/plan/template/detail/deleteById")
	@ResponseBody
	public ResponseData deleteById(HttpServletRequest request, PlnEPlanTemplateDetail dto) {
		service.deleteByPrimaryKey(dto);
		return new ResponseData();
	}

	@RequestMapping(value = "/fms/pln/e/plan/template/detail/queryById")
	@ResponseBody
	public ResponseData queryById(PlnEPlanTemplateDetail dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		PlnEPlanTemplateDetail primaryKey = service.selectByPrimaryKey(requestContext, dto);
		List<PlnEPlanTemplateDetail> list = new ArrayList<>();
		list.add(primaryKey);
		return new ResponseData(list);
	}

}