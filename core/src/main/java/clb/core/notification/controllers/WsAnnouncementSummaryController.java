package clb.core.notification.controllers;


import javax.servlet.http.HttpServletRequest;

import clb.core.release.dto.NtcAnnouncement;
import clb.core.release.service.INtcAnnouncementService;
import clb.core.sys.controllers.ClbBaseController;
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
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;

import clb.core.release.dto.NtcArticle;
import clb.core.release.service.INtcArticleService;

@Controller
public class WsAnnouncementSummaryController extends ClbBaseController{

	@Autowired
	private INtcArticleService service;
    @Autowired
    private INtcAnnouncementService AnnouncementService;
	
	@Timed
    @HapInbound(apiName = "ClbWsAnnouncementSummary")
    @RequestMapping(value = "/api/announcement/summary", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData queryList(@RequestBody NtcArticle dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                         @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request){
		IRequest requestContext = createRequestContext(request);
        dto.setUserId(requestContext.getUserId());
        return new ResponseData(service.selectAllArticle(requestContext, dto, page, pageSize));
    }
    /******
     * 修改通知信息
     * @param dto
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsNotifyUpdate")
    @RequestMapping(value = "/api/announcement/update", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData update(@RequestBody NtcAnnouncement dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return AnnouncementService.updateStatus(requestContext, dto);
    }
}
