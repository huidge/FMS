package clb.core.channel.controllers;

import clb.core.channel.dto.CnlLevel;
import clb.core.channel.dto.CnlPaymentTerm;
import clb.core.commission.dto.CmnExtra;
import clb.core.common.utils.DateUtil;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.channel.dto.CnlChannelContract;
import clb.core.channel.service.ICnlChannelContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author jun.zhao02@hand-china.com
 * @version 1.0
 * @name CnlChannelContractController
 * @description:渠道合约信息controller
 * @date 2017/4/25
 */
@Controller
@RequestMapping({"/fms/cnl/channel/contract"})
public class CnlChannelContractController extends BaseController{

    @Autowired
    private ICnlChannelContractService service;

    /**
     * 渠道合约信息查询
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "/query")
    @ResponseBody
    public ResponseData query(CnlChannelContract dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryContractPriv(requestContext,dto,page,pageSize));
    }

    /**
     * 渠道等级查询
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "/queryLevel")
    @ResponseBody
    public ResponseData queryLevel(CnlLevel dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                   @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryLevel(requestContext,dto));
    }

    /**
     * 付款条件查询
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "/queryTerm")
    @ResponseBody
    public ResponseData queryPaymentTerm(CnlPaymentTerm dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                   @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryPaymentTerm(requestContext,dto));
    }

    /**
     * 渠道合约信息提交
     * @param dto
     * @param request
     * @return
     */
    @RequestMapping(value = "/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<CnlChannelContract> dto){
        IRequest requestCtx = createRequestContext(request);
        //循环传递进来的dto
        for (CnlChannelContract contract : dto) {
            contract.setStartDate(DateUtil.dayStart(contract.getStartDate()));
            contract.setEndDate(DateUtil.dayEnd(contract.getEndDate()));
        }

        return new ResponseData(service.contractBatchUpdate(requestCtx, dto));
    }

    /**
     * 渠道合约信息删除
     * @param dto
     * @param request
     * @return
     */
    @RequestMapping(value = "/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<CnlChannelContract> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(true);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }
}