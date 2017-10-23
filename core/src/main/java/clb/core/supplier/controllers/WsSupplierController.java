package clb.core.supplier.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.hand.hap.core.IRequest;
import com.hand.hap.intergration.annotation.HapInbound;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;

import clb.core.supplier.dto.SpeCalendarLine;
import clb.core.supplier.dto.SpeSupplier;
import clb.core.supplier.mapper.SpeCalendarLineMapper;
import clb.core.supplier.service.ISpeSupplierService;

@Controller
public class WsSupplierController extends BaseController{

	@Autowired
    private ISpeSupplierService speSupplierService;
	@Autowired
	private SpeCalendarLineMapper speCalendarLineMapper;
	/**
	 * 查询所有供应商信息
	 * @param dto
	 * @param page
	 * @param pageSize
	 * @param request
	 * @return
	 */
	@Timed
    @HapInbound(apiName = "ClbWsSupplier")
    @RequestMapping(value = "/api/supplier/queryAll", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
	public ResponseData validatequery(@RequestBody SpeSupplier dto, HttpServletRequest request) {
	        IRequest iRequest = this.createRequestContext(request);
	    	return new ResponseData(speSupplierService.selectDataWithItems(dto));
	}

	/**
	 * 根据渠道查询所有供应商信息
	 * @param dto
	 * @param page
	 * @param pageSize
	 * @param request
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsSupplierByChannel")
	@RequestMapping(value = "/api/supplier/queryByChannel", method = RequestMethod.POST, produces = {"application/json" })
	@ResponseBody
	public ResponseData queryByChannel(@RequestBody SpeSupplier dto, HttpServletRequest request) {
		IRequest iRequest = this.createRequestContext(request);
		return new ResponseData(speSupplierService.selectDataByChannel(dto));
	}
	
	@Timed
	@HapInbound(apiName = "是否工作日")
	@RequestMapping(value = "/api/supplier/isWeekday", method = RequestMethod.POST, produces = {"application/json" })
	@ResponseBody
	public ResponseData isWeekday(@RequestBody SpeCalendarLine dto, HttpServletRequest request) {
		ResponseData response=new ResponseData(true);
		response.setCode("0");
		IRequest iRequest = this.createRequestContext(request);
		SpeSupplier supplier=new SpeSupplier();
		supplier.setSupplierId(dto.getSupplierId());
		supplier=speSupplierService.selectByPrimaryKey(iRequest, supplier);
		dto.setCalendarId(supplier.getCalendarId());
		if(dto.getCalendarId()==null){
			response.setSuccess(false);
			response.setMessage("查找不到日历！");
			return response;
		}
		if(dto.getTheYear()==null || dto.getTheDay()==null || dto.getTheMonth()==null){
			response.setSuccess(false);
			response.setMessage("日期参数不全！");
			return response;
		}
		List<SpeCalendarLine>list= speCalendarLineMapper.select(dto);
		for(SpeCalendarLine cal:list){
			if(cal.getDayType().equals("XXR")){
				response.setCode("-1");
			}
		}
		return response;
	}
	
}
