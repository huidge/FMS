package clb.core.commission.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;

import clb.core.commission.dto.CmnExtras;
import clb.core.commission.service.ICmnExtrasService;
import clb.core.common.utils.DateUtil;

@Controller
public class CmnExtrasController extends BaseController {

    @Autowired
    private ICmnExtrasService service;


    @RequestMapping(value = "/fms/cmn/extras/query")
    @ResponseBody
    public ResponseData query(CmnExtras dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/fms/cmn/extras/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request, @RequestBody List<CmnExtras> dto) {
        IRequest requestCtx = createRequestContext(request);
        //循环传递进来的dto
        for (CmnExtras cmnExtras : dto) {
            cmnExtras.setEffectiveStartDate(DateUtil.dayStart(cmnExtras.getEffectiveStartDate()));
            cmnExtras.setEffectiveEndDate(DateUtil.dayEnd(cmnExtras.getEffectiveEndDate()));
            if(!service.checkData(requestCtx, cmnExtras)){
            	throw new RuntimeException("新增异常：年龄不能与原有数据重叠！");
            }
        }
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/fms/cmn/extras/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<CmnExtras> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }

    /**
     * Extras佣金表设置查询
     *
     * @param dto      Extras佣金dto
     * @param page     页码
     * @param pageSize 页数
     * @param request  请求
     * @return Extras佣金dto list
     */
    @RequestMapping(value = "/fms/cmn/extras/queryExtras")
    @ResponseBody
    public ResponseData queryExtra(CmnExtras dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                   @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryExtras(requestContext, dto, page, pageSize));
    }

    /**
     * 查询是否有最大版本
     *
     * @param dto     Extras佣金dto
     * @param request 请求
     * @return version(不存在返回0)
     */
    @RequestMapping(value = "/fms/cmn/extras/queryMaxVersion")
    @ResponseBody
    public Long queryMaxVersion(@RequestBody CmnExtras dto, HttpServletRequest request) {
        return service.queryMaxVersion(dto);
    }

    /**
     * 增加新版本
     *
     * @param dto       佣金dto
     * @param oldVersion 最大版本号
     * @param request    请求
     * @return 旧版本&新版本Extras佣金dto list
     */
    @RequestMapping(value = "/fms/cmn/extras/addVersion/{oldVersion}")
    @ResponseBody
    public ResponseData addVersion(@RequestBody CmnExtras dto, @PathVariable("oldVersion") Long oldVersion, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.addVersion(requestContext, dto, oldVersion));
    }
}