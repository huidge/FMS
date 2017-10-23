package clb.core.question.controllers;

import clb.core.channel.dto.CnlChannel;
import clb.core.channel.service.ICnlChannelService;
import clb.core.common.controllers.CommonUploadController;
import clb.core.common.service.ICommonUploadService;
import clb.core.question.dto.QaGuideline;
import clb.core.question.dto.QaGuidelineFile;
import clb.core.question.dto.QaQuestion;
import clb.core.question.dto.QaQuestionConsult;
import clb.core.question.service.IQaGuidelineFileService;
import clb.core.question.service.IQaGuidelineService;
import clb.core.question.service.IQaQuestionConsultService;
import clb.core.question.service.IQaQuestionService;
import clb.core.sys.controllers.ClbBaseController;
import com.codahale.metrics.annotation.Timed;
import com.hand.hap.attachment.controllers.AttachmentController;
import com.hand.hap.core.IRequest;
import com.hand.hap.intergration.annotation.HapInbound;
import com.hand.hap.system.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;


@Controller
public class WsQaQuestionController extends ClbBaseController {

    @Autowired
    private IQaGuidelineService service;

    @Autowired
    private IQaQuestionService questionService;

    @Autowired
    private IQaQuestionConsultService questionConsultService;

    @Autowired
    private ICnlChannelService cnlChannelService;

    @Autowired
    private IQaGuidelineFileService fileService;


    /**
     * 获取 指引列表 数据/不分页
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsQaQueryGuideList")
    @RequestMapping(value = "/api/qa/guideList", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData queryGuideList(@RequestBody QaGuideline dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                       @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.query(requestContext, dto));
    }

    /**
     * 获取 操作指引文件 数据
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsQaQueryGuideFile")
    @RequestMapping(value = "/api/qa/guideFile", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData queryGuideFile(@RequestBody QaGuidelineFile dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                       @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(fileService.queryFileInfo(dto));
    }


    /**
     * 获取 每种 问题类型的问题列表
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsQaQueryQuestion")
    @RequestMapping(value = "/api/qa/question", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData getQuestion(@RequestBody QaQuestion dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                    @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(questionService.query(requestContext, dto));
    }


    /**
     * 问题咨询清单
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsQaConsultQuery")
    @RequestMapping(value = "/api/qa/consult/query", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData getQuestionAsk(@RequestBody QaQuestionConsult dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                       @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(questionConsultService.query(requestContext, dto, page, pageSize));
    }


    /**
     * 获取 团队图 数据，从渠道汇总查询获取
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsQaChannelTree")
    @RequestMapping(value = "/api/channel/tree", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData channelTree(CnlChannel dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                    @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        pageSize = 100;
        return new ResponseData(cnlChannelService.select(requestContext, dto, page, pageSize));
    }
}