package clb.core.pln.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.pln.dto.PlnEPlanTemplate;
import clb.core.pln.service.IPlnEPlanTemplateService;
import clb.core.production.dto.PrdItemPaymode;
import clb.core.production.dto.PrdItems;
import clb.core.production.service.IPrdItemPaymodeService;
import clb.core.production.service.IPrdItemsService;
import clb.core.supplier.dto.SpeSupplier;
import clb.core.supplier.service.ISpeSupplierService;
import oracle.net.aso.s;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

@Controller
public class PlnEPlanTemplateController extends BaseController {

	@Autowired
	private IPlnEPlanTemplateService service;

	@Autowired
	private IPrdItemPaymodeService prdItemPaymodeService;

	@Autowired
	private IPrdItemsService prdItemsService;

	@Autowired
	private ISpeSupplierService speSupplierService;

	@RequestMapping(value = "/fms/pln/e/plan/template/query")
	@ResponseBody
	public ResponseData query(PlnEPlanTemplate dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		return new ResponseData(service.query(requestContext, dto, page, pageSize));
	}

	@RequestMapping(value = "/fms/pln/e/plan/template/submit")
	@ResponseBody
	public ResponseData update(HttpServletRequest request, @RequestBody List<PlnEPlanTemplate> dto) {

		IRequest requestCtx = createRequestContext(request);
		ResponseData responseData = new ResponseData();
		List<PlnEPlanTemplate> list = new ArrayList<>();
		// 前台每次传递过来的对象只有一个
		PlnEPlanTemplate ePlanTemplate = dto.get(0);
		String templateName = ePlanTemplate.getTemplateName();
		if (!"".equals(templateName) && templateName != null) {

			PlnEPlanTemplate plnEPlanTemplate = new PlnEPlanTemplate();
			plnEPlanTemplate.setTemplateName(templateName);
			
			list = service.select(requestCtx, plnEPlanTemplate, 1, 2);
			//修改
			if (ePlanTemplate.get__status().equals("update")) {
				long templateId = ePlanTemplate.getTemplateId();
				
				if (list != null && list.size() == 1) {
					PlnEPlanTemplate plnEPlanTemplate2 = list.get(0);
					if ((long) plnEPlanTemplate2.getTemplateId() != templateId) {
						responseData.setSuccess(false);
						responseData.setMessage("模板名称重复了!");
						return responseData;
					}
				}
				//新增
			} else {
				if (list != null && list.size() > 0) {
					responseData.setSuccess(false);
					responseData.setMessage("模板名称重复了!");
					return responseData;
				}
			}

			return new ResponseData(service.batchUpdate(requestCtx, dto));
		}
		responseData.setSuccess(false);
		responseData.setMessage("模板名称为空!");
		return responseData;
	}

	@RequestMapping(value = "/fms/pln/e/plan/template/remove")
	@ResponseBody
	public ResponseData delete(HttpServletRequest request, @RequestBody List<PlnEPlanTemplate> dto) {
		service.batchDelete(dto);
		return new ResponseData();
	}

	/**
	 * 根据主键删除
	 * 
	 * @param request
	 * @param templateIds
	 * @return
	 */
	@RequestMapping(value = "/fms/pln/e/plan/template/deleteByTemplateId")
	@ResponseBody
	public ResponseData deleteByTemplateId(HttpServletRequest request,
			@RequestParam("templateIds[]") List<Long> templateIds) {
		for (Long long1 : templateIds) {
			PlnEPlanTemplate record = new PlnEPlanTemplate();
			record.setTemplateId(long1);
			service.deleteByPrimaryKey(record);
		}
		return new ResponseData();
	}

	/**
	 * 自义定根据产品ID 查询该产品下的可用货币
	 * 
	 * @param request
	 * @param dto
	 * @return
	 */

	@RequestMapping(value = "/fms/pln/e/plan/template/queryCurrencyByItemId")
	@ResponseBody
	public ResponseData queryCurrencyByItemId(HttpServletRequest request, PrdItemPaymode dto) {
		IRequest requestContext = createRequestContext(request);
		List<PrdItemPaymode> list = prdItemPaymodeService.queryCurrencyByItemId(requestContext, dto);
		return new ResponseData(list);
	}

	/**
	 * 根据产品公司的id 查询该公司下的所有产品
	 * 
	 * @param request
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "/fms/pln/e/plan/template/queryitemBySupplierId")
	@ResponseBody
	public ResponseData queryitemBySupplierId(HttpServletRequest request, PrdItems dto) {
		IRequest requestContext = createRequestContext(request);
		List<PrdItems> list = prdItemsService.queryitemBySupplierId(requestContext, dto);
		return new ResponseData(list);
	}

	/**
	 * 查询产品的中分类
	 * 
	 * @param request
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "/fms/pln/e/plan/template/queryitemClass")
	@ResponseBody
	public ResponseData queryitemClass(HttpServletRequest request, PrdItems dto) {
		IRequest requestContext = createRequestContext(request);
		List<PrdItems> list = prdItemsService.queryitemClass(requestContext, dto);
		return new ResponseData(list);
	}

	/**
	 * 数据完善(录入数据)
	 * @param request
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "/fms/pln/e/plan/template/dataPerfect")
	@ResponseBody
	public ResponseData dataPerfect(HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		ResponseData responseData = new ResponseData();
		try {
			service.dataPerfect(requestContext);
		} catch (Exception e) {
			responseData.setSuccess(false);
		}
		
		return responseData;
	}
}