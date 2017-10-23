package clb.core.commission.controllers;

import clb.core.channel.dto.CnlLevel;
import clb.core.common.utils.DateUtil;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.commission.dto.CmnBasic;
import clb.core.commission.service.ICmnBasicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CmnBasicController extends BaseController {

    @Autowired
    private ICmnBasicService service;


    @RequestMapping(value = "/fms/cmn/basic/query")
    @ResponseBody
    public ResponseData query(CmnBasic dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext, dto, page, pageSize));
    }


    @RequestMapping(value = "/fms/cmn/basic/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request, @RequestBody List<CmnBasic> dto) {
        IRequest requestCtx = createRequestContext(request);
        //循环传递进来的dto
        for (CmnBasic cmnBasic : dto) {
            cmnBasic.setEffectiveStartDate(DateUtil.dayStart(cmnBasic.getEffectiveStartDate()));
            cmnBasic.setEffectiveEndDate(DateUtil.dayEnd(cmnBasic.getEffectiveEndDate()));
            if(!service.checkData(requestCtx, cmnBasic)){
            	throw new RuntimeException("新增异常：年龄不能与原有数据重叠！");
            }
        }
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/fms/cmn/basic/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<CmnBasic> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }

    /**
     * Basic佣金表设置查询
     *
     * @param dto      Basic佣金dto
     * @param page     页码
     * @param pageSize 页数
     * @param request  请求
     * @return Basic佣金dto list
     */
    @RequestMapping(value = "/fms/cmn/basic/queryBasic")
    @ResponseBody
    public ResponseData queryBasic(CmnBasic dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                   @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryBasic(requestContext, dto, page, pageSize));
    }

    /**
     * 查询是否有最大版本
     *
     * @param dto     Basic佣金dto
     * @param request 请求
     * @return version(不存在返回0)
     */
    @RequestMapping(value = "/fms/cmn/basic/queryMaxVersion")
    @ResponseBody
    public Long queryMaxVersion(@RequestBody CmnBasic dto, HttpServletRequest request) {
        return service.queryMaxVersion(dto);
    }

    /**
     * 增加新版本
     *
     * @param dto        Basic佣金dto
     * @param oldVersion 最大版本号
     * @param request    请求
     * @return 旧版本&新版本Basic佣金dto list
     */
    @RequestMapping(value = "/fms/cmn/basic/addVersion/{oldVersion}")
    @ResponseBody
    public ResponseData addVersion(@RequestBody CmnBasic dto, @PathVariable("oldVersion") Long oldVersion, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.addVersion(requestContext, dto, oldVersion));
    }

}