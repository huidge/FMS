package clb.core.question.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;

import clb.core.pln.dto.PlnEPlanTemplate;
import clb.core.question.dto.QaGuideline;
import clb.core.question.dto.QaGuidelineFile;
import clb.core.question.dto.QaQuestion;
import clb.core.question.service.IQaGuidelineService;
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
 * 问题操作的controller
 * @author Administrator
 *
 */
@Controller
@RequestMapping({ "/question/guideline" })
public class QaGuidelineController extends BaseController {

	@Autowired
	private IQaGuidelineService service;

	@RequestMapping(value = "/query")
	@ResponseBody
	public ResponseData query(QaGuideline dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		return new ResponseData(service.select(requestContext, dto, page, pageSize));
	}
	
	@RequestMapping(value = "/queryById")
	@ResponseBody
	public ResponseData queryById(QaGuideline dto,HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		ResponseData responseData = new ResponseData();
		//判断ID是否有值
		if(dto.getGuidelineId() == null || dto.getGuidelineId() == 0){
			//没有就不查询
			return responseData;
		}else{
			//有就根据id查询
			List<QaGuideline> list = new ArrayList<>();
			QaGuideline guideline = service.selectByPrimaryKey(requestContext, dto);
			list.add(guideline);
			responseData.setRows(list);
			return responseData;
		}
	}
	

	@RequestMapping(value = "/submit")
	@ResponseBody
	public ResponseData submit(HttpServletRequest request, @RequestBody List<QaGuideline> dto) {
		IRequest requestCtx = createRequestContext(request);
		ResponseData responseData = new ResponseData();
		try {
			service.submit(requestCtx,dto);
			responseData.setMessage("操作成功!");
			responseData.setRows(dto);
			return responseData;
		} catch (Exception e) {
			e.printStackTrace();
			responseData.setMessage("操作成功!");
			responseData.setRows(dto);
			return responseData;
		}
	}
	@RequestMapping(value = "/add")
	@ResponseBody
	public ResponseData add(HttpServletRequest request, @RequestBody List<QaGuideline> dto) {
		IRequest requestCtx = createRequestContext(request);
		ResponseData responseData = new ResponseData();
		try {
			service.add(requestCtx,dto);
			responseData.setMessage("操作成功!");
			responseData.setRows(dto);
			return responseData;
		} catch (Exception e) {
			e.printStackTrace();
			responseData.setMessage("操作成功!");
			responseData.setSuccess(false);
			responseData.setRows(dto);
			return responseData;
		}
	}

	@RequestMapping(value = "/remove")
	@ResponseBody
	public ResponseData delete(HttpServletRequest request, @RequestBody List<QaGuideline> dto) {
		service.batchDelete(dto);
		return new ResponseData();
	}
	
	/**
	 * 根据主键删除一条记录
	 * @param request
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "/deleteById")
	@ResponseBody
	public ResponseData deleteById(HttpServletRequest request, QaGuideline dto) {
		service.deleteByPrimaryKey(dto);
		return new ResponseData();
	}
	
	 /**
     * 根据主键删除
     * @param request
     * @param templateIds
     * @return
     */
    @RequestMapping(value = "/deleteByGuidelineId")
    @ResponseBody
    public ResponseData deleteByTemplateId(HttpServletRequest request,@RequestParam("guidelineIds[]") List<Long> guidelineIds){
    	for (Long long1 : guidelineIds) {
    		QaGuideline record = new QaGuideline();
			record.setGuidelineId(long1);
    		service.deleteByPrimaryKey(record);
		}
    	return new ResponseData();
    }
	
}