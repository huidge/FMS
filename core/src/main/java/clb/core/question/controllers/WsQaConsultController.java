package clb.core.question.controllers;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.hand.hap.core.IRequest;
import com.hand.hap.intergration.annotation.HapInbound;
import com.hand.hap.system.dto.ResponseData;

import clb.core.question.dto.QaConsultLine;
import clb.core.question.dto.QaQuestionConsult;
import clb.core.question.service.IQaConsultLineService;
import clb.core.question.service.IQaQuestionConsultService;
import clb.core.sys.controllers.ClbBaseController;

@Controller
public class WsQaConsultController extends ClbBaseController {

	@Autowired
	private IQaConsultLineService qaConsultLineService;
	
	@Autowired
	private IQaQuestionConsultService qaQuestionConsultService;
	
	/**
	 * 客服答复，聊天记录
	 * @param dto
	 * @param page
	 * @param pageSize
	 * @param request
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsQaConLineQuery")
	@RequestMapping(value = "/api/qa/conLine/query", method = RequestMethod.POST, produces = { "application/json" })
	@ResponseBody
	public ResponseData conLineQuery(@RequestBody QaConsultLine dto,
			@RequestParam(defaultValue = DEFAULT_PAGE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		List<QaConsultLine> query = qaConsultLineService.query(requestContext, dto, page, pageSize);
		return new ResponseData(query);
	}
	
	/**
	 * 更新问题咨询
	 * @param request
	 * @param dto
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsQaConsultSubmit")
	@RequestMapping(value = "/api/qa/consult/submit")
	@ResponseBody
	public ResponseData consultUpdate(HttpServletRequest request,
									  @RequestParam(defaultValue = DEFAULT_PAGE) int page,
									  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize,
									  @RequestBody QaQuestionConsult dto){
	    IRequest requestCtx = createRequestContext(request);
	    if(dto.getConsultId()==null){
			dto.setQuestionDate(new Date());
	      	qaQuestionConsultService.insertSelective(requestCtx, dto);
	    }else{
	    	 qaQuestionConsultService.updateByPrimaryKeySelective(requestCtx, dto);
	    }

		QaQuestionConsult consult = new QaQuestionConsult();
		consult.setUserId(dto.getUserId());
	    return new ResponseData(qaQuestionConsultService.query(requestCtx,consult,page,pageSize));
	}
	
	/**
	 * 通过用户查询订单编号
	 * @param request
	 * @param dto
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsQaGetOrderCode")
	@RequestMapping(value = "/api/qa/getOrderCode")
	@ResponseBody
	public ResponseData getOrderCode(HttpServletRequest request,@RequestBody QaQuestionConsult dto){
		IRequest requestCtx = createRequestContext(request);
	    return new ResponseData(qaQuestionConsultService.selectByUserId(dto));
	}
	
	/**
	 * 客户追问
	 * @param dto
	 * @param page
	 * @param pageSize
	 * @param request
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsQaConLineSubmit")
	@RequestMapping(value = "/api/qa/conLine/Submit", method = RequestMethod.POST, produces = { "application/json" })
	@ResponseBody
	public ResponseData conLineSubmit(@RequestBody QaConsultLine dto,
			@RequestParam(defaultValue = DEFAULT_PAGE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		qaConsultLineService.insertSelective(requestContext, dto);
		QaConsultLine consultLine = new QaConsultLine();
		consultLine.setConsultId(dto.getConsultId());
		return new ResponseData(qaConsultLineService.query(requestContext, consultLine, page, pageSize));
	}
}
