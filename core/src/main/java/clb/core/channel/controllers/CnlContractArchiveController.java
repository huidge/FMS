package clb.core.channel.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.channel.dto.CnlContractArchive;
import clb.core.channel.service.ICnlContractArchiveService;
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
 * @name CnlContractArchiveController
 * @description:渠道合约档案信息controller
 * @date 2017/4/25
 */
@Controller
@RequestMapping({"/fms/cnl/contract/archive"})
public class CnlContractArchiveController extends BaseController{

    @Autowired
    private ICnlContractArchiveService service;

    /**
     * 渠道合约档案信息查询
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "/query")
    @ResponseBody
    public ResponseData query(CnlContractArchive dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryArchive(requestContext,dto,page,pageSize));
    }

    /**
     * 渠道合约档案信息提交
     * @param dto
     * @param request
     * @return
     */
    @RequestMapping(value = "/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<CnlContractArchive> dto){
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    /**
     * 渠道合约档案信息删除
     * @param dto
     * @param request
     * @return
     */
    @RequestMapping(value = "/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<CnlContractArchive> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
}