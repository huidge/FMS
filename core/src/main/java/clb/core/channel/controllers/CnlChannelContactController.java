package clb.core.channel.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.channel.dto.CnlChannelContact;
import clb.core.channel.service.ICnlChannelContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author jun.zhao02@hand-china.com
 * @version 1.0
 * @name CnlChannelContactController
 * @description:渠道联系人controller
 * @date 2017/4/25
 */
@Controller
@RequestMapping({"/fms/cnl/channel/contact"})
public class CnlChannelContactController extends BaseController{

    @Autowired
    private ICnlChannelContactService service;

    /**
     * 渠道联系人查询
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "/query")
    @ResponseBody
    public ResponseData query(CnlChannelContact dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryContact(requestContext,dto,page,pageSize));
    }

    /**
     * 渠道联系人提交
     * @param dto
     * @param request
     * @return
     */
    @RequestMapping(value = "/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<CnlChannelContact> dto){
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    /**
     * 渠道联系人删除
     * @param dto
     * @param request
     * @return
     */
    @RequestMapping(value = "/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<CnlChannelContact> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
}