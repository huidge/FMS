package clb.core.question.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.question.dto.QaQuestion;
import clb.core.question.service.IQaQuestionService;
import clb.core.release.dto.NtcSlideshow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * 问题查询的controller层
 */
@Controller
@RequestMapping({ "/question/query" })
public class QaQuestionController extends BaseController {

	@Autowired
	private IQaQuestionService service;

	/**
	 * 问题的查询
	 * 
	 * @param dto
	 * @param page
	 * @param pageSize
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/query")
	@ResponseBody
	public ResponseData query(QaQuestion dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		return new ResponseData(service.select(requestContext, dto, page, pageSize));
	}

	/**
	 * 问题的提交
	 * 
	 * @param request
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "/submit")
	@ResponseBody
	public ResponseData update(HttpServletRequest request, @RequestBody List<QaQuestion> dto) {
		IRequest requestCtx = createRequestContext(request);
		ResponseData responseData = new ResponseData();
		//得到传递过来的参数
		QaQuestion qaQuestion = dto.get(0);
		Long questionId = qaQuestion.getQuestionId();
		
		//后台查询
		QaQuestion question = new QaQuestion();
		question.setQuestionName(qaQuestion.getQuestionName().trim());
		question.setQuestionType(qaQuestion.getQuestionType());

		List<QaQuestion> list = service.select(requestCtx, question, 1, 1);

		if (list.size() == 1) {
			Long questionId2 = list.get(0).getQuestionId();
			if (questionId != questionId2) {
				responseData.setMessage("问题类型和问题名称的组合已经存在!");
				responseData.setSuccess(false);
				responseData.setRows(dto);
				return responseData;
			}
		}
		service.updateByPrimaryKeySelective(requestCtx, qaQuestion);
		return new ResponseData(dto);
	}

	@RequestMapping(value = "/add")
	@ResponseBody
	public ResponseData add(HttpServletRequest request, @RequestBody List<QaQuestion> dto) {
		ResponseData responseData = new ResponseData();
		IRequest requestCtx = createRequestContext(request);
		try {
			List<QaQuestion> list = service.select(requestCtx, dto.get(0), 1, 1);
			if (list.size() == 0) {
				service.insertSelective(requestCtx, dto.get(0));
				return new ResponseData(dto);
			} else {
				responseData.setMessage("问题类型和问题名称的组合已经存在!");
				responseData.setSuccess(false);
				return responseData;
			}
		} catch (Exception e) {
			responseData.setRows(dto);
			responseData.setSuccess(false);
			return responseData;
		}
	}

	/**
	 * 问题的删除
	 * 
	 * @param request
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "/remove")
	@ResponseBody
	public ResponseData delete(HttpServletRequest request, @RequestBody List<QaQuestion> dto) {
		service.batchDelete(dto);
		return new ResponseData();
	}

	/**
	 * 根据主键删除一条记录
	 * 
	 * @param request
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "/deleteById")
	@ResponseBody
	public ResponseData deleteById(HttpServletRequest request, QaQuestion dto) {
		service.deleteByPrimaryKey(dto);
		return new ResponseData();
	}

}