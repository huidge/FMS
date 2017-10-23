package clb.core.channel.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.channel.dto.CnlContractRateHistory;
import clb.core.channel.service.ICnlContractRateHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
/**
 * @author wanjun.feng@hand-china.com
 * @date 2017/9/18
 */
@Controller
@RequestMapping({"/fms/cnl/contract/rate/his"})
public class CnlContractRateHistoryController extends BaseController{

    @Autowired
    private ICnlContractRateHistoryService cnlContractRateHistoryServiceImpl;

    /**
     * 渠道合约费率历史记录查询
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "/query")
    @ResponseBody
    public ResponseData query(CnlContractRateHistory dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(cnlContractRateHistoryServiceImpl.queryRateHis(requestContext,dto,page,pageSize));
    }

    /**
     * 渠道合约费率历史记录提交
     * @param dto
     * @param request
     * @return
     */
    @RequestMapping(value = "/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<CnlContractRateHistory> dto){
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(cnlContractRateHistoryServiceImpl.batchUpdate(requestCtx, dto));
    }
}