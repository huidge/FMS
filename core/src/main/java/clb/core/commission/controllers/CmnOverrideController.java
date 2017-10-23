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

import clb.core.commission.dto.CmnOverride;
import clb.core.commission.service.ICmnOverrideService;
import clb.core.common.utils.CommonUtil;
import clb.core.common.utils.DateUtil;

@Controller
public class CmnOverrideController extends BaseController {

    @Autowired
    private ICmnOverrideService service;

    @RequestMapping(value = "/fms/cmn/override/query")
    @ResponseBody
    public ResponseData query(CmnOverride dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/fms/cmn/override/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request, @RequestBody List<CmnOverride> dto) {
        IRequest requestCtx = createRequestContext(request);

        //循环传递进来的dto
        for (CmnOverride cmnOverride : dto) {
            cmnOverride.setEffectiveStartDate(DateUtil.dayStart(cmnOverride.getEffectiveStartDate()));
            cmnOverride.setEffectiveEndDate(DateUtil.dayEnd(cmnOverride.getEffectiveEndDate()));
        }
        return new ResponseData(service.batchUpdateOverride(requestCtx, dto));
    }
    
    @RequestMapping(value = "/fms/cmn/override/submitOverride")
    @ResponseBody
    public ResponseData update(HttpServletRequest request, @RequestBody CmnOverride dto) {
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

    @RequestMapping(value = "/fms/cmn/override/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<CmnOverride> dto) {
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
    @RequestMapping(value = "/fms/cmn/override/queryBasic")
    @ResponseBody
    public ResponseData queryBasic(CmnOverride dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
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
    @RequestMapping(value = "/fms/cmn/override/queryMaxVersion")
    @ResponseBody
    public CmnOverride queryMaxVersion(@RequestBody CmnOverride dto, HttpServletRequest request) {
        return service.queryMaxVersion(dto);
    }

    /******
     * 查询是否有源头数据
     * @param dto
     * @param request
     * @return
     */
    @RequestMapping(value = "/fms/cmn/override/queryYTDate")
    @ResponseBody
    public CmnOverride queryYTDate(@RequestBody CmnOverride dto, HttpServletRequest request) {
    	CmnOverride cmnOverride=service.queryYTDate(dto);
    	return cmnOverride==null?new CmnOverride():cmnOverride;
    }
    /**
     * 增加新版本
     *
     * @param dto        Override佣金dto
     * @param oldVersion 最大版本号
     * @param request    请求
     * @return 旧版本&新版本Override佣金dto list
     */
    @RequestMapping(value = "/fms/cmn/override/addVersion/{oldVersion}")
    @ResponseBody
    public ResponseData addVersion(@RequestBody CmnOverride dto, @PathVariable("oldVersion") Long oldVersion, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.addVersion(requestContext, dto, oldVersion));
    }
    
    @RequestMapping(value = "/fms/cmn/override/queryOverrideBasic")
    @ResponseBody
    public ResponseData queryOverrideBasic(CmnOverride dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize,HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryOverrideBasic(requestContext, dto, page, pageSize));
    }
    
    @RequestMapping(value = "/fms/cmn/override/queryEmptyData")
    @ResponseBody
    public ResponseData queryEmptyData(HttpServletRequest request){
    	return new ResponseData();
    }

}