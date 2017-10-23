package clb.core.release.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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

import clb.core.release.dto.NtcArticle;
import clb.core.release.service.INtcArticleService;
import clb.core.sys.controllers.ClbBaseController;
import org.springframework.web.servlet.support.RequestContextUtils;

@Controller
public class WsNtcArticleController extends ClbBaseController {

	@Autowired
	private INtcArticleService ntcArticleService;
	@Autowired
	private MessageSource messageSource;

	@Timed
    @HapInbound(apiName = "ClbWsNtcArticleList")
    @RequestMapping(value = "/api/article/list", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData query(@RequestBody NtcArticle dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		dto.setStatus("Y");
		return new ResponseData(ntcArticleService.queryByCondition(requestContext, dto, page, pageSize));
	}

	@Timed
	@HapInbound(apiName = "ClbWsNtcArticleListPublic")
	@RequestMapping(value = "/api/public/article/list", method = RequestMethod.POST, produces = {"application/json" })
	@ResponseBody
	public ResponseData queryPublic(@RequestBody NtcArticle dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
							  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		dto.setStatus("Y");
		return new ResponseData(ntcArticleService.queryByCondition(requestContext, dto, page, pageSize));
	}

	@Timed
    @HapInbound(apiName = "ClbWsNtcArticleDetail")
    @RequestMapping(value = "/api/article/detail", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public NtcArticle detail(@RequestBody NtcArticle dto,HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		dto.setStatus("Y");
		NtcArticle ntcArticle = ntcArticleService.queryDetailById(requestContext, dto);
		if (ntcArticle.getSfpFilePath()!=null&&ntcArticle.getSfpFilePath()!=""){
			String  path = messageSource.getMessage("fms.pub.oss_web_path",null, RequestContextUtils.getLocale(request));
			ntcArticle.setSfpFilePath(path+ntcArticle.getSfpFilePath());
		}
		return ntcArticle;
	}

	@Timed
	@HapInbound(apiName = "ClbWsNtcArticleDetailPublic")
	@RequestMapping(value = "/api/public/article/detail", method = RequestMethod.POST, produces = {"application/json" })
	@ResponseBody
	public NtcArticle detailPublic(@RequestBody NtcArticle dto,HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		dto.setStatus("Y");
		NtcArticle ntcArticle = ntcArticleService.queryDetailById(requestContext, dto);
		if (ntcArticle.getSfpFilePath()!=null&&ntcArticle.getSfpFilePath()!=""){
			String  path = messageSource.getMessage("fms.pub.oss_web_path",null, RequestContextUtils.getLocale(request));
			ntcArticle.setSfpFilePath(path+ntcArticle.getSfpFilePath());
		}
		return ntcArticle;
	}
}