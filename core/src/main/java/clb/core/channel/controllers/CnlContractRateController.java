package clb.core.channel.controllers;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.channel.dto.CnlContractRate;
import clb.core.channel.service.ICnlContractRateService;
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
 * @name CnlContractRateController
 * @description:渠道合约费率信息controller
 * @date 2017/4/25
 */
@Controller
@RequestMapping({"/fms/cnl/contract/rate"})
public class CnlContractRateController extends BaseController{

    @Autowired
    private ICnlContractRateService service;

    /**
     * 渠道合约费率信息查询
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "/query")
    @ResponseBody
    public ResponseData query(CnlContractRate dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        if (dto.getDefineRateFlag().equals("Y")) {
            return new ResponseData(service.queryRate(requestContext,dto,page,pageSize));
        } else {
            if (dto.getPartyType().equals("CHANNEL") && dto.getRateLevelId() != null) {
                return new ResponseData(service.queryChannelRate(requestContext,dto,page,pageSize));
            }
//            else if (dto.getPartyType().equals("SUPPLIER") && dto.getRateLevelName() != null) {
//                return new ResponseData(service.querySupplierRate(requestContext,dto,page,pageSize));
//            }
        }
        return new ResponseData();
    }

    /**
     * 渠道合约费率信息提交
     * @param dto
     * @param request
     * @return
     */
    @RequestMapping(value = "/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<CnlContractRate> dto){
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    /**
     * 渠道合约费率信息删除
     * @param dto
     * @param request
     * @return
     */
    @RequestMapping(value = "/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<CnlContractRate> dto){
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