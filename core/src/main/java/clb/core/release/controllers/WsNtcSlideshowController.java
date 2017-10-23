package clb.core.release.controllers;

import clb.core.release.dto.NtcSlideshow;
import clb.core.release.service.INtcSlideshowService;
import clb.core.sys.controllers.ClbBaseController;
import com.codahale.metrics.annotation.Timed;
import com.hand.hap.attachment.service.ISysFileService;
import com.hand.hap.core.IRequest;
import com.hand.hap.intergration.annotation.HapInbound;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class WsNtcSlideshowController extends ClbBaseController {

	@Autowired
	private INtcSlideshowService service;
	
	@Autowired
	private ISysFileService sysFileService;


	@Timed
	@HapInbound(apiName = "ClbWsNtcArticleLBT")
	@RequestMapping(value = "/api/article/LBT", method = RequestMethod.POST, produces = {"application/json" })
	@ResponseBody
	public ResponseData getArticleLBT(@RequestBody NtcSlideshow dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
									  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		return new ResponseData(service.queryByCondition(requestContext, dto, page, pageSize));
	}

	@Timed
	@HapInbound(apiName = "ClbWsNtcArticleLBTPublic")
	@RequestMapping(value = "/api/public/article/LBT", method = RequestMethod.POST, produces = {"application/json" })
	@ResponseBody
	public ResponseData getArticleLBTPublic(@RequestBody NtcSlideshow dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
									  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		return new ResponseData(service.queryByCondition(requestContext, dto, page, pageSize));
	}
	
}