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

import clb.core.commission.dto.CmnExtra;
import clb.core.commission.service.ICmnExtraService;
import clb.core.common.utils.CommonUtil;
import clb.core.common.utils.DateUtil;

@Controller
public class CmnExtraController extends BaseController {

    @Autowired
    private ICmnExtraService service;
    
    @RequestMapping(value = "/fms/cmn/extra/query")
    @ResponseBody
    public ResponseData query(CmnExtra dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/fms/cmn/extra/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request, @RequestBody List<CmnExtra> dto) {
        IRequest requestCtx = createRequestContext(request);

        //循环传递进来的dto
        for (CmnExtra cmnExtra : dto) {
            cmnExtra.setEffectiveStartDate(DateUtil.dayStart(cmnExtra.getEffectiveStartDate()));
            cmnExtra.setEffectiveEndDate(DateUtil.dayEnd(cmnExtra.getEffectiveEndDate()));
        }
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }
    
    @RequestMapping(value = "/fms/cmn/extra/submitExtra")
    @ResponseBody
    public ResponseData update(HttpServletRequest request, @RequestBody CmnExtra dto) {
        IRequest requestCtx = createRequestContext(request);
        //循环传递进来的dto
        dto.setEffectiveEndDate(DateUtil.dayEnd(dto.getEffectiveEndDate()));
        ResponseData response= new ResponseData(true);
        try {
			service.insertSelective(requestCtx, dto);
		} catch (Exception e) {
			CommonUtil.printStackTraceToStr(e);
			response.setSuccess(false);
			response.setMessage(e.getMessage());
		}
        return response;
    }
    @RequestMapping(value = "/fms/cmn/extra/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<CmnExtra> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }

    /**
     * Override佣金表设置查询
     *
     * @param dto      Override佣金dto
     * @param page     页码
     * @param pageSize 页数
     * @param request  请求
     * @return Override佣金dto list
     */
    @RequestMapping(value = "/fms/cmn/extra/queryBasic")
    @ResponseBody
    public ResponseData queryBasic(CmnExtra dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                   @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryBasic(requestContext, dto, page, pageSize));
    }

    /**
     * 查询是否有最大版本
     *
     * @param dto     Override佣金dto
     * @param request 请求
     * @return version(不存在返回0)
     */
    @RequestMapping(value = "/fms/cmn/extra/queryMaxVersion")
    @ResponseBody
    public CmnExtra queryMaxVersion(@RequestBody CmnExtra dto, HttpServletRequest request) {
        return service.queryMaxVersion(dto);
    }
    
    /******
     * 查询是否有源头数据
     * @param dto
     * @param request
     * @return
     */
    @RequestMapping(value = "/fms/cmn/extra/queryYTDate")
    @ResponseBody
    public CmnExtra queryYTDate(@RequestBody CmnExtra dto, HttpServletRequest request) {
    	CmnExtra cmnExtra=service.queryYTDate(dto);
    	return cmnExtra==null?new CmnExtra():cmnExtra;
    }

    /**
     * 增加新版本
     *
     * @param dto        Override佣金dto
     * @param oldVersion 最大版本号
     * @param request    请求
     * @return 旧版本&新版本Override佣金dto list
     */
    @RequestMapping(value = "/fms/cmn/extra/addVersion/{oldVersion}")
    @ResponseBody
    public ResponseData addVersion(@RequestBody CmnExtra dto, @PathVariable("oldVersion") Long oldVersion, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.addVersion(requestContext, dto, oldVersion));
    }
    
    @RequestMapping(value = "/fms/cmn/extra/queryExtraBasic")
    @ResponseBody
    public ResponseData queryExtraBasic(CmnExtra dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize,HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryExtraBasic(requestContext, dto, page, pageSize));
    }
}