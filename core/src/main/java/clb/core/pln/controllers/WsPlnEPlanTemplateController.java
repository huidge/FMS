package clb.core.pln.controllers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.hand.hap.core.IRequest;
import com.hand.hap.intergration.annotation.HapInbound;
import com.hand.hap.system.dto.ResponseData;

import clb.core.pln.dto.PlnEPlanTemplate;
import clb.core.pln.service.IPlnEPlanTemplateService;
import clb.core.production.dto.PrdPremium;
import clb.core.sys.controllers.ClbBaseController;

@Controller
public class WsPlnEPlanTemplateController extends ClbBaseController{

	@Autowired
	private IPlnEPlanTemplateService plnEPlanTemplateService;
	
	/**
	 * 前端查询符合条件的电子计划书模板
	 * @param dto
	 * @param page
	 * @param pageSize
	 * @param request
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsPlnEPlanTemplate")
	@RequestMapping(value = "/api/plnEPlanTemplate/queryWsPlnEPlanTemplate", method = RequestMethod.POST, produces = {"application/json" })
	@ResponseBody
	public ResponseData querLlist(@RequestBody PrdPremium prdPremium, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
								  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request){
		IRequest requestContext = createRequestContext(request);
		List<PlnEPlanTemplate> PlnEPlanTemplateList = plnEPlanTemplateService.queryWsPlnEPlanTemplate(requestContext,prdPremium,page,pageSize);
		return new ResponseData(PlnEPlanTemplateList);
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormat.setLenient(true);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}
}
