package clb.core.common.controllers;

import clb.core.common.service.ICommonUploadService;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.hand.hap.attachment.exception.StoragePathNotExsitException;
import com.hand.hap.attachment.exception.UniqueFileMutiException;
import com.hand.hap.core.IRequest;
import com.hand.hap.intergration.annotation.HapInbound;
import com.hand.hap.system.controllers.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * @name WsUploadController
 * @description 前端上传文件通用api
 * @author xiaoyong.luo@hand-china.com 2017年6月2日19:13:31
 * @version 1.0 
 */
@Controller
public class WsUploadController extends BaseController{

    @Autowired
    private ICommonUploadService uploadService;


	/**
	 * 前端上传文件通用api
	 */
	@Timed
	@HapInbound(apiName = "ClbWsCommonUpload")
	@RequestMapping(value = "/api/commons/attach", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
	@ResponseBody
	public String upload(HttpServletRequest request)
			throws StoragePathNotExsitException, UniqueFileMutiException, JsonProcessingException {

		IRequest requestContext = createRequestContext(request);
		Locale locale = RequestContextUtils.getLocale(request);
		return uploadService.Upload(request,locale,requestContext);
	}
}