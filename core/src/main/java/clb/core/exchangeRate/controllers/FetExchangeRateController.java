package clb.core.exchangeRate.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.exchangeRate.dto.FetExchangeRate;
import clb.core.exchangeRate.service.IFetExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RequestMapping(value = "/exchangeRate/exchangeRate")
@Controller
public class FetExchangeRateController extends BaseController {

	@Autowired
	private IFetExchangeRateService service;

	@RequestMapping(value = "/query")
	@ResponseBody
	public ResponseData query(FetExchangeRate dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		/*dto.setRate(Long.valueOf(dto.getRate()));*/
		return new ResponseData(service.select(requestContext, dto, page, pageSize));
	}
	/**
	 * @param request
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "/submit")
	@ResponseBody
	public ResponseData update(HttpServletRequest request, @RequestBody List<FetExchangeRate> dto) {
		IRequest requestCtx = createRequestContext(request);
		//创建一个list集合  作为框架的返回值
		ResponseData responseData = new ResponseData();
		
		for (FetExchangeRate fetExchangeRate : dto) {
			
			
			
			if("update".equals(fetExchangeRate.get__status())){
				FetExchangeRate rate = new FetExchangeRate();
				rate.setForeignCurrency(fetExchangeRate.getForeignCurrency());
				rate.setBaseCurrency(fetExchangeRate.getBaseCurrency());
				List<FetExchangeRate> list = service.queryByForeignCurrencyAndBaseCurrency(rate);
				if(list.size() > 0){
					for (FetExchangeRate fetExchangeRate2 : list) {
						if(compareTimeRange(fetExchangeRate2,fetExchangeRate) && fetExchangeRate2.getRateId() != fetExchangeRate.getRateId()){
							responseData.setMessage("有效期内的汇率重复了");
				            responseData.setSuccess(false);
							
							return responseData;
						}
					}
				}
				
			}else{
				FetExchangeRate rate = new FetExchangeRate();
				rate.setForeignCurrency(fetExchangeRate.getForeignCurrency());
				rate.setBaseCurrency(fetExchangeRate.getBaseCurrency());
				List<FetExchangeRate> list = service.queryByForeignCurrencyAndBaseCurrency(rate);
				
				if (list.size() > 0) {
					for (FetExchangeRate fetExchangeRate2 : list) {
						
						if(compareTimeRange(fetExchangeRate2,fetExchangeRate)){
							responseData.setMessage("有效期内的汇率重复了");
				            responseData.setSuccess(false);
							
							return responseData;
						}
					}
				}
				
			}
			
		}
		return new ResponseData(service.batchUpdate(requestCtx, dto));
	}

	@RequestMapping(value = "/remove")
	@ResponseBody
	public ResponseData delete(HttpServletRequest request, @RequestBody List<FetExchangeRate> dto) {
		service.batchDelete(dto);
		return new ResponseData();
	}
	/**
	 * 比较 2个汇率对象的有效时间  判断有效时间范围是否重叠   如果重叠了 返回true 否则返回false
	 * @param old
	 * @param current
	 * @return
	 */
	public boolean compareTimeRange(FetExchangeRate old,FetExchangeRate current){
		
		boolean falg = false;
		Date a1 = old.getEffectiveDateFrom();
		Date a2 = old.getEffectiveDateTo();
		
		Date b1 = current.getEffectiveDateFrom();
		Date b2 = current.getEffectiveDateTo();
		
		if (a1.getTime() <= b2.getTime() & a2.getTime() >= b1.getTime()) {
			falg = true;
		}
		
		return falg;
	}
}