package clb.core.release.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;

import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.attachment.service.ISysFileService;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;

import clb.core.production.dto.PrdItems;
import clb.core.release.dto.NtcArticle;
import clb.core.release.service.INtcArticleService;

import org.antlr.grammar.v3.ANTLRv3Parser.element_return;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RequestMapping({ "/release/article" })
@Controller
public class NtcArticleController extends BaseController {

	@Autowired
	private INtcArticleService service;
	
	@Autowired
	private ISysFileService sysFileService;

	@RequestMapping(value = "/query")
	@ResponseBody
	public ResponseData query(NtcArticle dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		List<NtcArticle> queryByCondition = service.queryByCondition(requestContext, dto, page, pageSize);
		for (NtcArticle ntcArticle : queryByCondition) {
			if(ntcArticle.getCoverFileId() != null ){
				ntcArticle.setSysFile(sysFileService.selectByPrimaryKey(requestContext,ntcArticle.getCoverFileId()));
			}
		}
		return new ResponseData(queryByCondition);
	}

	
	
	@RequestMapping(value = "/submit")
	@ResponseBody
	public ResponseData update(HttpServletRequest request,@RequestBody List<NtcArticle> dto) {
		NtcArticle article = dto.get(0);
		IRequest requestCtx = createRequestContext(request);
		// 修改的时候 根据id修改
		NtcArticle ntcArticle = service.updateByPrimaryKeySelective(requestCtx, article);
		List<NtcArticle> list = new ArrayList<>();
		list.add(ntcArticle);
		return new ResponseData(list);
	}

	@RequestMapping(value = "/add")
	@ResponseBody
	public ResponseData add(HttpServletRequest request, @RequestBody List<NtcArticle> dto) {
		IRequest requestCtx = createRequestContext(request);
		// 设置默认的发布状态为未发布
		NtcArticle ntcArticle = dto.get(0);
		ntcArticle.setStatus("N");
		NtcArticle ntcArticle2 = service.insertSelective(requestCtx, ntcArticle);
		List<NtcArticle> list = new ArrayList<>();
		list.add(ntcArticle2);
		return new ResponseData(list);
	}
	
	
	/**
	 * 添加时校验重复
	 * @param request
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "/addValidation")
	@ResponseBody
	public ResponseData addValidation(HttpServletRequest request, @RequestBody List<NtcArticle> dto) {
		IRequest requestCtx = createRequestContext(request);
		ResponseData responseData = new ResponseData();
		NtcArticle article = new NtcArticle();
		article.setReleasePosition(dto.get(0).getReleasePosition());
		article.setAuthorName(dto.get(0).getAuthorName());
		article.setTitle(dto.get(0).getTitle());
		List<NtcArticle> list = service.queryByCondition(requestCtx, article, 1, 1);
		if(!CollectionUtils.isEmpty(list)){
			responseData.setRows(dto);
			responseData.setMessage("该记录已存在!");
			responseData.setSuccess(false);
			return responseData;
		}else {
			responseData.setSuccess(true);
			responseData.setRows(dto);
			return responseData;
		}
		
	}
	
	@RequestMapping(value = "/remove")
	@ResponseBody
	public ResponseData delete(HttpServletRequest request, @RequestBody List<NtcArticle> dto) {
		service.batchDelete(dto);
		return new ResponseData();
	}
	

	/**
	 * 发布的方法
	 * 
	 * @param request
	 * @param ids
	 * @return
	 */
	@RequestMapping(value = "/updataReleaseStatus")
	@ResponseBody
	public ResponseData updataReleaseStatus(HttpServletRequest request,@RequestParam("status") String status,@RequestParam("ids[]") List<Long> ids) {
		IRequest requestCtx = createRequestContext(request);
		if (status.equals("release")) {
			for (Long articleId : ids) {
				NtcArticle dto = new NtcArticle();
				dto.setStatus("Y");
				dto.setArticleId(articleId);
				dto.setReleaseDate(new Date());
				service.updateByPrimaryKeySelective(requestCtx, dto);
			}
		}else{
			for (Long articleId : ids) {
				NtcArticle dto = new NtcArticle();
				dto.setArticleId(articleId);
				NtcArticle ntcArticle = service.selectByPrimaryKey(requestCtx, dto);
				ntcArticle.setStatus("N");
				ntcArticle.setReleaseDate(null);
				service.updateByPrimaryKey(requestCtx, ntcArticle);
			}
		}
		return new ResponseData();
	}
	/**
	 * 置顶
	 * @param request
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "/stick")
	@ResponseBody
	public ResponseData stick(HttpServletRequest request,NtcArticle dto) {
		IRequest requestCtx = createRequestContext(request);
		dto.setTop(1L);
		service.updateByPrimaryKeySelective(requestCtx, dto);
		return new ResponseData();
	}
	
	/**
	 * 取消置顶
	 * @param request
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "/unStick")
	@ResponseBody
	public ResponseData unStick(HttpServletRequest request,NtcArticle dto) {
		IRequest requestCtx = createRequestContext(request);
		dto.setTop(0L);
		service.updateByPrimaryKeySelective(requestCtx, dto);
		return new ResponseData();
	}
}